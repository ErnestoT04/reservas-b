package sv.edu.catolica.Reservas.service;

import org.springframework.stereotype.Service;
import sv.edu.catolica.Reservas.model.Rol;
import sv.edu.catolica.Reservas.model.Usuario;
import sv.edu.catolica.Reservas.repository.RolRepository;
import sv.edu.catolica.Reservas.repository.UsuarioRepository;

import java.util.List;

@Service
public class UsuarioAdminService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;

    public UsuarioAdminService(UsuarioRepository usuarioRepository,
                               RolRepository rolRepository) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
    }

    // 🔵 Listado general
    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    // 🔵 Obtener usuario
    public Usuario obtenerUsuario(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    // 🔵 Activar usuario
    public Usuario activarUsuario(Long id) {
        Usuario usuario = obtenerUsuario(id);
        usuario.setActivo(true);
        return usuarioRepository.save(usuario);
    }

    // 🔵 Desactivar usuario
    public Usuario desactivarUsuario(Long id) {
        Usuario usuario = obtenerUsuario(id);
        usuario.setActivo(false);
        return usuarioRepository.save(usuario);
    }

    // 🔵 Cambiar rol
    public Usuario cambiarRol(Long id, String nuevoRolNombre) {

        Usuario usuario = obtenerUsuario(id);

        Rol rol = rolRepository.findByRolNombre(nuevoRolNombre)
                .orElseThrow(() -> new RuntimeException("El rol no existe: " + nuevoRolNombre));

        usuario.setRol(rol);

        return usuarioRepository.save(usuario);
    }
}
