package sv.edu.catolica.Reservas.service;

import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import sv.edu.catolica.Reservas.model.*;
import sv.edu.catolica.Reservas.repository.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final MesaRepository mesaRepository;
    private final UsuarioRepository usuarioRepository;
    private final EstadoReservaRepository estadoReservaRepository;
    private final EstadoMesaRepository estadoMesaRepository;
    private final EmailNotificationService emailNotificationService;

    public ReservaService(ReservaRepository reservaRepository,
                          MesaRepository mesaRepository,
                          UsuarioRepository usuarioRepository,
                          EstadoReservaRepository estadoReservaRepository,
                          EstadoMesaRepository estadoMesaRepository,
                          EmailNotificationService emailNotificationService) {
        this.reservaRepository = reservaRepository;
        this.mesaRepository = mesaRepository;
        this.usuarioRepository = usuarioRepository;
        this.estadoReservaRepository = estadoReservaRepository;
        this.estadoMesaRepository = estadoMesaRepository;
        this.emailNotificationService = emailNotificationService;
    }

    public List<Mesa> obtenerMesasDisponibles() {
        return mesaRepository.findMesasDisponibles();
    }

    // ===========================================================
    // VALIDACIÓN CENTRALIZADA
    // ===========================================================
    private void validarDatosReserva(LocalDateTime inicio, LocalDateTime fin, Mesa mesa, int personas) {

        if (fin == null || !fin.isAfter(inicio)) {
            throw new RuntimeException("La fecha/hora de cierre debe ser mayor que el inicio.");
        }

        Duration duracion = Duration.between(inicio, fin);
        if (duracion.toMinutes() < 30) {
            throw new RuntimeException("La duración mínima de una reserva es de 30 minutos.");
        }

        LocalTime horaApertura = LocalTime.of(6, 30);
        LocalTime horaCierre = LocalTime.of(21, 0);
        LocalTime horaInicio = inicio.toLocalTime();
        LocalTime horaFin = fin.toLocalTime();

        if (horaInicio.isBefore(horaApertura)) {
            throw new RuntimeException("No se pueden hacer reservaciones antes de las 6:30 a. m.");
        }
        if (horaFin.isAfter(horaCierre)) {
            throw new RuntimeException("No se pueden hacer reservaciones después de las 9:00 p. m.");
        }

        if (personas > mesa.getCantidad()) {
            throw new RuntimeException("La cantidad de personas excede la capacidad de la mesa.");
        }
    }

    // ===========================================================
    // CREAR RESERVA (CORREGIDO)
    // ===========================================================
    public Reserva crearReserva(Long idUsuario, Long idMesa,
                                LocalDateTime fechaHoraInicio, LocalDateTime fechaHoraCierre,
                                Integer personas) {

        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Mesa mesa = mesaRepository.findById(idMesa)
                .orElseThrow(() -> new RuntimeException("Mesa no encontrada"));

        validarDatosReserva(fechaHoraInicio, fechaHoraCierre, mesa, personas);

        EstadoReserva estadoPendiente = estadoReservaRepository.findByNombre("Pendiente");
        if (estadoPendiente == null)
            throw new RuntimeException("Estado 'Pendiente' no existe.");

        List<Reserva> reservasExistentes = reservaRepository.findByMesa(mesa);
        for (Reserva r : reservasExistentes) {
            if (r.getEstadoReserva().getNombre().equalsIgnoreCase("Cancelada")) continue;

            boolean seCruza = fechaHoraInicio.isBefore(r.getFechaHoraCierre())
                    && fechaHoraCierre.isAfter(r.getFechaHora());

            if (seCruza) {
                throw new RuntimeException("La mesa ya está reservada entre " +
                        r.getFechaHora() + " y " + r.getFechaHoraCierre());
            }
        }

        Reserva reserva = new Reserva();
        reserva.setUsuario(usuario);
        reserva.setMesa(mesa);
        reserva.setEstadoReserva(estadoPendiente);
        reserva.setFechaHora(fechaHoraInicio);
        reserva.setFechaHoraCierre(fechaHoraCierre);
        reserva.setFechaCreacion(LocalDateTime.now());
        reserva.setCantidadPersonas(personas);

        // ✔ PRIMERO SE GUARDA LA RESERVA
        Reserva guardada = reservaRepository.save(reserva);

        // ✔ LUEGO SE ENVÍA LA NOTIFICACIÓN
        emailNotificationService.enviarNotificacion(
                guardada,
                "Tu reserva ha sido creada para la mesa " +
                        guardada.getMesa().getNumeroMesa() +
                        " el día " + guardada.getFechaHora() +
                        " hasta " + guardada.getFechaHoraCierre()
        );

        return guardada;
    }

    // ===========================================================
    // OBTENER RESERVAS
    // ===========================================================
    public List<Reserva> obtenerMisReservas(Long idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return reservaRepository.findByUsuario(usuario);
    }

    public Optional<Reserva> obtenerReservaPorId(Long id) {
        return reservaRepository.findById(id);
    }

    // ===========================================================
    // ACTUALIZAR RESERVA (CORREGIDO)
    // ===========================================================
    public Reserva actualizarReserva(Long id, LocalDateTime inicio, LocalDateTime fin, Integer personas) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        Mesa mesa = reserva.getMesa();

        validarDatosReserva(inicio, fin, mesa, personas);

        List<Reserva> reservasExistentes = reservaRepository.findByMesa(mesa);
        for (Reserva r : reservasExistentes) {
            if (r.getIdReserva().equals(id)) continue;
            if (r.getEstadoReserva().getNombre().equalsIgnoreCase("Cancelada")) continue;

            boolean seCruza =
                    inicio.isBefore(r.getFechaHoraCierre()) &&
                            fin.isAfter(r.getFechaHora());

            if (seCruza) {
                throw new RuntimeException("La mesa ya está reservada en ese rango de tiempo.");
            }
        }

        reserva.setFechaHora(inicio);
        reserva.setFechaHoraCierre(fin);
        reserva.setCantidadPersonas(personas);

        Reserva guardada = reservaRepository.save(reserva);

        emailNotificationService.enviarNotificacion(
                guardada,
                "Tu reserva ha sido actualizada. Nueva fecha/hora: " + guardada.getFechaHora()
        );

        return guardada;
    }

    // ===========================================================
    // CANCELAR RESERVA (CORREGIDO)
    // ===========================================================
    public Reserva cancelarReserva(Long id) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        EstadoReserva cancelada = estadoReservaRepository.findByNombre("Cancelada");
        reserva.setEstadoReserva(cancelada);

        Mesa mesa = reserva.getMesa();
        EstadoMesa disponible = estadoMesaRepository.findByNombre("Disponible");
        mesa.setEstadoMesa(disponible);
        mesaRepository.save(mesa);

        Reserva guardada = reservaRepository.save(reserva);

        emailNotificationService.enviarNotificacion(
                guardada,
                "Tu reserva ha sido cancelada."
        );

        return guardada;
    }

    // ===========================================================
    // CONFIRMAR RESERVA (CORREGIDO)
    // ===========================================================
    public Reserva confirmarReserva(Long id) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        EstadoReserva confirmada = estadoReservaRepository.findByNombre("Confirmada");
        if (confirmada == null)
            throw new RuntimeException("Estado 'Confirmada' no existe.");

        reserva.setEstadoReserva(confirmada);

        Mesa mesa = reserva.getMesa();
        EstadoMesa ocupada = estadoMesaRepository.findByNombre("Ocupada");
        mesa.setEstadoMesa(ocupada);
        mesaRepository.save(mesa);

        Reserva guardada = reservaRepository.save(reserva);

        emailNotificationService.enviarNotificacion(
                guardada,
                "Tu reserva ha sido confirmada. ¡Te esperamos!"
        );

        return guardada;
    }

    // ===========================================================
    // ACTUALIZAR ESTADO MESAS
    // ===========================================================
    @Transactional
    public void actualizarEstadoMesas() {
        LocalDateTime ahora = LocalDateTime.now();
        List<Mesa> mesas = mesaRepository.findAll();

        for (Mesa mesa : mesas) {

            if (mesa.getEstadoMesa().getNombre().equalsIgnoreCase("Mantenimiento"))
                continue;

            boolean tieneReservaActiva = reservaRepository.findByMesa(mesa).stream()
                    .anyMatch(r ->
                            r.getFechaHora().isBefore(ahora) &&
                                    r.getFechaHoraCierre().isAfter(ahora) &&
                                    !r.getEstadoReserva().getNombre().equalsIgnoreCase("Cancelada")
                    );

            EstadoMesa nuevoEstado = tieneReservaActiva
                    ? estadoMesaRepository.findByNombre("Ocupada")
                    : estadoMesaRepository.findByNombre("Disponible");

            if (!mesa.getEstadoMesa().getIdEstadoMesa().equals(nuevoEstado.getIdEstadoMesa())) {
                mesa.setEstadoMesa(nuevoEstado);
                mesaRepository.save(mesa);
            }
        }
    }

    public List<Mesa> obtenerMesasDisponiblesEnRango(LocalDateTime inicio, LocalDateTime fin) {

        List<Mesa> todas = mesaRepository.findAll();
        List<Mesa> disponibles = new ArrayList<>();

        for (Mesa mesa : todas) {
            if (mesa.getEstadoMesa().getNombre().equalsIgnoreCase("Mantenimiento"))
                continue;

            List<Reserva> ocupa = reservaRepository.reservasSolapadas(
                    mesa.getIdMesa(), inicio, fin
            );

            if (ocupa.isEmpty()) {
                disponibles.add(mesa);
            }
        }

        return disponibles;
    }
}
