package sv.edu.catolica.Reservas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sv.edu.catolica.Reservas.model.Mesa;
import sv.edu.catolica.Reservas.model.Reserva;

import java.time.LocalDateTime;
import java.util.List;

public interface MesaRepository extends JpaRepository<Mesa, Long> {

    @Query("SELECT m FROM Mesa m WHERE m.estadoMesa.nombre = 'Disponible'")
    List<Mesa> findMesasDisponibles();

    List<Mesa> findByCantidadGreaterThanEqual(Integer cantidad);
}
