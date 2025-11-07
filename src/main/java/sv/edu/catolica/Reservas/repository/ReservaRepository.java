package sv.edu.catolica.Reservas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sv.edu.catolica.Reservas.model.Reserva;
import sv.edu.catolica.Reservas.model.Usuario;
import java.util.List;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    List<Reserva> findByUsuario(Usuario usuario);
}
