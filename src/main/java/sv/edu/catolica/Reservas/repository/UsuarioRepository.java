package sv.edu.catolica.Reservas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sv.edu.catolica.Reservas.model.Usuario;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByCorreo(String correo);
}
