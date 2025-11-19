package sv.edu.catolica.Reservas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sv.edu.catolica.Reservas.model.TipoNotificacion;

public interface TipoNotificacionRepository extends JpaRepository<TipoNotificacion, Long> {}
