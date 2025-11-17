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

    // ===========================================================
    // 🔵 VALIDACIÓN CENTRALIZADA
    // ===========================================================
    private void validarDatosReserva(LocalDateTime inicio, LocalDateTime fin, Mesa mesa, int personas) {
        // Validar que la hora de cierre sea después de la de inicio
        if (fin == null || !fin.isAfter(inicio)) {
            throw new RuntimeException("La fecha/hora de cierre debe ser mayor que la fecha/hora de inicio.");
        }

        // Validar duración mínima de 30 minutos
        Duration duracion = Duration.between(inicio, fin);
        if (duracion.toMinutes() < 30) {
            throw new RuntimeException("La duración mínima de una reserva es de 30 minutos.");
        }

        // Reglas de horario permitido
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

        // Validar capacidad de la mesa
        if (personas > mesa.getCantidad()) {
            throw new RuntimeException("La cantidad de personas (" + personas + 
                ") excede la capacidad de la mesa (" + mesa.getCantidad() + ").");
        }
    }

    // ===========================================================
    // 🔵 CREAR RESERVA
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

        // Validar traslape de horarios
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

        return reservaRepository.save(reserva);
    }

    // ===========================================================
    // 🔵 OBTENER RESERVAS
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
    // 🔵 ACTUALIZAR RESERVA
    // ===========================================================
    public Reserva actualizarReserva(Long id, LocalDateTime inicio, LocalDateTime fin, Integer personas) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        Mesa mesa = reserva.getMesa();

        validarDatosReserva(inicio, fin, mesa, personas);

        // Validar traslape con otras reservas
        List<Reserva> reservasExistentes = reservaRepository.findByMesa(mesa);
        for (Reserva r : reservasExistentes) {
            if (r.getIdReserva().equals(id)) continue; // ignorar la misma reserva
            if (r.getEstadoReserva().getNombre().equalsIgnoreCase("Cancelada")) continue;

            boolean seCruza =
                    inicio.isBefore(r.getFechaHoraCierre()) &&
                            fin.isAfter(r.getFechaHora());

            if (seCruza) {
                throw new RuntimeException(
                        "La mesa ya está reservada entre " +
                                r.getFechaHora() + " y " + r.getFechaHoraCierre()
                );
            }
        }


        reserva.setFechaHora(inicio);
        reserva.setFechaHoraCierre(fin);
        reserva.setCantidadPersonas(personas);

        return reservaRepository.save(reserva);
    }


    // ===========================================================
    // 🔵 CANCELAR RESERVA
    // ===========================================================
    public Reserva cancelarReserva(Long id) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));
        EstadoReserva cancelada = estadoReservaRepository.findByNombre("Cancelada");
        reserva.setEstadoReserva(cancelada);

        // 🔹 Al cancelar, la mesa vuelve a "Disponible"
        Mesa mesa = reserva.getMesa();
        EstadoMesa disponible = estadoMesaRepository.findByNombre("Disponible");
        mesa.setEstadoMesa(disponible);
        mesaRepository.save(mesa);

        return reservaRepository.save(reserva);
    }

    // ===========================================================
    // 🔵 CONFIRMAR RESERVA
    // ===========================================================
    public Reserva confirmarReserva(Long id) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        EstadoReserva confirmada = estadoReservaRepository.findByNombre("Confirmada");
        if (confirmada == null)
            throw new RuntimeException("Estado 'Confirmada' no existe.");

        reserva.setEstadoReserva(confirmada);

        // 🔹 Al confirmar, la mesa se marca como "Ocupada"
        Mesa mesa = reserva.getMesa();
        EstadoMesa ocupada = estadoMesaRepository.findByNombre("Ocupada");
        mesa.setEstadoMesa(ocupada);
        mesaRepository.save(mesa);

        return reservaRepository.save(reserva);
    }

    // ===========================================================
    // 🔵 ACTUALIZAR ESTADO DE MESAS AUTOMÁTICAMENTE
    // ===========================================================
    @Transactional
    public void actualizarEstadoMesas() {
        LocalDateTime ahora = LocalDateTime.now();
        List<Mesa> mesas = mesaRepository.findAll();

        for (Mesa mesa : mesas) {
            if (mesa.getEstadoMesa().getNombre().equalsIgnoreCase("Mantenimiento")) continue;

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

            // ignora mesas en mantenimiento
            if (mesa.getEstadoMesa().getNombre().equalsIgnoreCase("Mantenimiento"))
                continue;

            // verifica solapamientos con tu consulta optimizada
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
