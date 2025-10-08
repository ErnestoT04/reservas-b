package sv.edu.catolica.Reservas.service;

import org.springframework.stereotype.Service;
import sv.edu.catolica.Reservas.model.Usuario;
import sv.edu.catolica.Reservas.repository.UsuarioRepository;

import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario registrar(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    public Optional<Usuario> login(String correo, String contrasena) {
        return usuarioRepository.findByCorreo(correo)
                .filter(u -> u.getContrasena().equals(contrasena));
    }

}
