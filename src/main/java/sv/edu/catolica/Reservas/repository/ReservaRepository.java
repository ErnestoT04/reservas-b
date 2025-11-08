package sv.edu.catolica.Reservas.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import sv.edu.catolica.Reservas.model.Mesa;
import sv.edu.catolica.Reservas.model.Reserva;
import sv.edu.catolica.Reservas.model.Usuario;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    List<Reserva> findByUsuario(Usuario usuario);

    List<Reserva> findByMesa(Mesa mesa);

    boolean existsByMesaAndFechaHoraBeforeAndFechaHoraCierreAfter(
            Mesa mesa,
            LocalDateTime inicio,
            LocalDateTime fin);
}
