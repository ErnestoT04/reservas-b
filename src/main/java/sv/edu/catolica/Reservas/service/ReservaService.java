package sv.edu.catolica.Reservas.service;

import org.springframework.stereotype.Service;
import sv.edu.catolica.Reservas.model.*;
import sv.edu.catolica.Reservas.repository.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final MesaRepository mesaRepository;
    private final UsuarioRepository usuarioRepository;
    private final EstadoReservaRepository estadoReservaRepository;

    public ReservaService(ReservaRepository reservaRepository,
                          MesaRepository mesaRepository,
                          UsuarioRepository usuarioRepository,
                          EstadoReservaRepository estadoReservaRepository) {
        this.reservaRepository = reservaRepository;
        this.mesaRepository = mesaRepository;
        this.usuarioRepository = usuarioRepository;
        this.estadoReservaRepository = estadoReservaRepository;
    }

    public List<Mesa> obtenerMesasDisponibles() {
        return mesaRepository.findMesasDisponibles();
    }

    public Reserva crearReserva(Long idUsuario, Long idMesa, LocalDateTime fechaHora, Integer personas) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Mesa mesa = mesaRepository.findById(idMesa)
                .orElseThrow(() -> new RuntimeException("Mesa no encontrada"));
        EstadoReserva estado = estadoReservaRepository.findByNombre("Pendiente");

        Reserva reserva = new Reserva();
        reserva.setUsuario(usuario);
        reserva.setMesa(mesa);
        reserva.setEstadoReserva(estado);
        reserva.setFechaHora(fechaHora);
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
}
