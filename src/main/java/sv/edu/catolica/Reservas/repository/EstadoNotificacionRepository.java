package sv.edu.catolica.Reservas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sv.edu.catolica.Reservas.model.EstadoNotificacion;

public interface EstadoNotificacionRepository extends JpaRepository<EstadoNotificacion, Long> {
    EstadoNotificacion findByNombre(String nombre);
}
