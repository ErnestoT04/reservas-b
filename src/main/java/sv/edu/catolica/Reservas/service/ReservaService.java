package sv.edu.catolica.Reservas.service;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import sv.edu.catolica.Reservas.model.*;
import sv.edu.catolica.Reservas.repository.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final MesaRepository mesaRepository;
    private final UsuarioRepository usuarioRepository;
    private final EstadoReservaRepository estadoReservaRepository;
    private final EstadoMesaRepository estadoMesaRepository;

    public ReservaService(ReservaRepository reservaRepository,
            MesaRepository mesaRepository,
            UsuarioRepository usuarioRepository,
            EstadoReservaRepository estadoReservaRepository,
            EstadoMesaRepository estadoMesaRepository) {
        this.reservaRepository = reservaRepository;
        this.mesaRepository = mesaRepository;
        this.usuarioRepository = usuarioRepository;
        this.estadoReservaRepository = estadoReservaRepository;
        this.estadoMesaRepository = estadoMesaRepository;
    }

    public List<Mesa> obtenerMesasDisponibles() {
        return mesaRepository.findMesasDisponibles();
    }

    public Reserva crearReserva(Long idUsuario,
            Long idMesa,
            LocalDateTime fechaHoraInicio,
            LocalDateTime fechaHoraCierre,
            Integer personas) {

        // Validar que la hora de cierre sea después de la de inicio
        if (fechaHoraCierre == null || !fechaHoraCierre.isAfter(fechaHoraInicio)) {
            throw new RuntimeException("La fecha/hora de cierre debe ser mayor que la fecha/hora de inicio.");
        }

        // Validar duración mínima de 30 minutos
        Duration duracion = Duration.between(fechaHoraInicio, fechaHoraCierre);
        if (duracion.toMinutes() < 30) {
            throw new RuntimeException("La duración mínima de una reserva es de 30 minutos.");
        }

        // Reglas de horario permitido
        LocalTime horaApertura = LocalTime.of(6, 30); // 6:30 a. m.
        LocalTime horaCierre = LocalTime.of(21, 0); // 9:00 p. m.

        LocalTime horaInicio = fechaHoraInicio.toLocalTime();
        LocalTime horaFin = fechaHoraCierre.toLocalTime();

        // No permitir iniciar antes de apertura
        if (horaInicio.isBefore(horaApertura)) {
            throw new RuntimeException("No se pueden hacer reservaciones antes de las 6:30 a. m.");
        }

        // No permitir terminar después del cierre
        if (horaFin.isAfter(horaCierre)) {
            throw new RuntimeException("No se pueden hacer reservaciones después de las 9:00 p. m.");
        }

        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Mesa mesa = mesaRepository.findById(idMesa)
                .orElseThrow(() -> new RuntimeException("Mesa no encontrada"));

        EstadoReserva estado = estadoReservaRepository.findByNombre("Pendiente");
        if (estado == null) {
            throw new RuntimeException("Estado de reserva 'Pendiente' no existe.");
        }

        // Validar traslape con reservas existentes de la misma mesa
        List<Reserva> reservasExistentes = reservaRepository.findByMesa(mesa);

        for (Reserva r : reservasExistentes) {
            String nombreEstado = r.getEstadoReserva().getNombre();
            if (nombreEstado != null && nombreEstado.equalsIgnoreCase("Cancelada")) {
                continue;
            }

            boolean seCruza = fechaHoraInicio.isBefore(r.getFechaHoraCierre())
                    && fechaHoraCierre.isAfter(r.getFechaHora());

            if (seCruza) {
                throw new RuntimeException("La mesa ya está reservada entre " +
                        r.getFechaHora() + " y " + r.getFechaHoraCierre());
            }
        }

        // Guardar la reserva válida
        Reserva reserva = new Reserva();
        reserva.setUsuario(usuario);
        reserva.setMesa(mesa);
        reserva.setEstadoReserva(estado);
        reserva.setFechaHora(fechaHoraInicio);
        reserva.setFechaHoraCierre(fechaHoraCierre);
        reserva.setFechaCreacion(LocalDateTime.now());
        reserva.setCantidadPersonas(personas);

        return reservaRepository.save(reserva);
    }

    public List<Reserva> obtenerMisReservas(Long idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return reservaRepository.findByUsuario(usuario);
    }

    public Optional<Reserva> obtenerReservaPorId(Long id) {
        return reservaRepository.findById(id);
    }

    public Reserva actualizarReserva(Long id, LocalDateTime fechaHora, Integer personas) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));
        reserva.setFechaHora(fechaHora);
        reserva.setCantidadPersonas(personas);
        return reservaRepository.save(reserva);
    }

    public Reserva cancelarReserva(Long id) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));
        EstadoReserva cancelada = estadoReservaRepository.findByNombre("Cancelada");
        reserva.setEstadoReserva(cancelada);
        return reservaRepository.save(reserva);
    }

    @Transactional
    public void actualizarEstadoMesas() {
        LocalDateTime ahora = LocalDateTime.now();
        List<Mesa> mesas = mesaRepository.findAll();

        for (Mesa mesa : mesas) {
            // Saltar mantenimiento
            if (mesa.getEstadoMesa().getNombre().equalsIgnoreCase("Mantenimiento")) {
                continue;
            }

            boolean tieneReservaActiva = reservaRepository.existsByMesaAndFechaHoraBeforeAndFechaHoraCierreAfter(
                    mesa, ahora, ahora);

            EstadoMesa nuevoEstado;

            if (tieneReservaActiva) {
                nuevoEstado = estadoMesaRepository.findByNombre("Ocupada");

            } else {
                nuevoEstado = estadoMesaRepository.findByNombre("Disponible");

            }

            // Solo actualizar si cambió (evita escrituras innecesarias)
            if (!mesa.getEstadoMesa().getIdEstadoMesa().equals(nuevoEstado.getIdEstadoMesa())) {
                mesa.setEstadoMesa(nuevoEstado);
                mesaRepository.save(mesa);
            }
        }
    }

}
