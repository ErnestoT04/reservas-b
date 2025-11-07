package sv.edu.catolica.Reservas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sv.edu.catolica.Reservas.model.EstadoReserva;

public interface EstadoReservaRepository extends JpaRepository<EstadoReserva, Long> {
    EstadoReserva findByNombre(String nombre);
}
