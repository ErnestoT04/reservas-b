package sv.edu.catolica.Reservas.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sv.edu.catolica.Reservas.model.Usuario;
import sv.edu.catolica.Reservas.service.UsuarioAdminService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/usuarios")
public class UsuarioAdminController {

    private final UsuarioAdminService usuarioAdminService;

    public UsuarioAdminController(UsuarioAdminService usuarioAdminService) {
        this.usuarioAdminService = usuarioAdminService;
    }

    // 🔵 Listar todos los usuarios
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping
    public List<Usuario> listarUsuarios() {
        return usuarioAdminService.listarUsuarios();
    }

    // 🔵 Obtener un usuario por ID
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping("/{id}")
    public Usuario obtenerUsuario(@PathVariable Long id) {
        return usuarioAdminService.obtenerUsuario(id);
    }

    // 🔵 Activar usuario
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PatchMapping("/{id}/activar")
    public Usuario activar(@PathVariable Long id) {
        return usuarioAdminService.activarUsuario(id);
    }

    // 🔵 Desactivar usuario
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PatchMapping("/{id}/desactivar")
    public Usuario desactivar(@PathVariable Long id) {
        return usuarioAdminService.desactivarUsuario(id);
    }

    // 🔵 Cambiar rol del usuario (ADMINISTRADOR, EMPLEADO, USUARIO)
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PatchMapping("/{id}/rol")
    public Usuario cambiarRol(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String nuevoRol = body.get("rol");
        return usuarioAdminService.cambiarRol(id, nuevoRol);
    }
}
