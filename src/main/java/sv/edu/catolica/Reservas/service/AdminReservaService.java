package sv.edu.catolica.Reservas.service;

import org.springframework.stereotype.Service;
import sv.edu.catolica.Reservas.model.Reserva;
import sv.edu.catolica.Reservas.repository.EstadoReservaRepository;
import sv.edu.catolica.Reservas.repository.ReservaRepository;
import sv.edu.catolica.Reservas.repository.UsuarioRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AdminReservaService {

    private final ReservaRepository reservaRepository;
    private final UsuarioRepository usuarioRepository;
    private final EstadoReservaRepository estadoReservaRepository;

    public AdminReservaService(ReservaRepository reservaRepository,
            UsuarioRepository usuarioRepository,
            EstadoReservaRepository estadoReservaRepository) {
        this.reservaRepository = reservaRepository;
        this.usuarioRepository = usuarioRepository;
        this.estadoReservaRepository = estadoReservaRepository;
    }

    public List<Reserva> filtrarReservas(String fecha, Long usuarioId, Long estadoId) {

    List<Reserva> lista = reservaRepository.filtrarSinFecha(usuarioId, estadoId);

    if (fecha != null && !fecha.isBlank()) {
        LocalDate f = LocalDate.parse(fecha);
        LocalDateTime desde = f.atStartOfDay();
        LocalDateTime hasta = f.atTime(23, 59, 59);

        lista = lista.stream()
                .filter(r -> !r.getFechaHora().isBefore(desde)
                        && !r.getFechaHora().isAfter(hasta))
                .toList();
    }

    return lista;
}

}
