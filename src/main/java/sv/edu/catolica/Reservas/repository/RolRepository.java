package sv.edu.catolica.Reservas.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import sv.edu.catolica.Reservas.model.Rol;

public interface RolRepository extends JpaRepository<Rol, Long> {
    Optional<Rol> findByRolNombre(String rolNombre);
}