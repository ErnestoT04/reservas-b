package sv.edu.catolica.Reservas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sv.edu.catolica.Reservas.model.EstadoMesa;
@Repository
public interface EstadoMesaRepository extends JpaRepository<EstadoMesa, Long> {

    EstadoMesa findByNombre(String nombre); // ✅ usa el nombre real del campo
}

