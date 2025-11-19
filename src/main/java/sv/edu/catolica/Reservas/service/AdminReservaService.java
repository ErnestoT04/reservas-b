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

        // SI HAY FECHA → usar query correcta de BD
        if (fecha != null && !fecha.isBlank()) {

            LocalDate f = LocalDate.parse(fecha);

            // ESTA consulta sí devuelve reservas del día exacto
            List<Reserva> lista = reservaRepository.reservasPorDia(f);

            // filtros opcionales
            if (usuarioId != null) {
                lista = lista.stream()
                        .filter(r -> r.getUsuario().getIdUsuario().equals(usuarioId))
                        .toList();
            }

            if (estadoId != null) {
                lista = lista.stream()
                        .filter(r -> r.getEstadoReserva().getIdEstadoReserva().equals(estadoId))
                        .toList();
            }

            return lista;
        }

        // SI NO HAY FECHA → usa filtro sin fecha
        return reservaRepository.filtrarSinFecha(usuarioId, estadoId);
    }



}
