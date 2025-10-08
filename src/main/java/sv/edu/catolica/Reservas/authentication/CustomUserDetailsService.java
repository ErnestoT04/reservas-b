package sv.edu.catolica.Reservas.authentication;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import sv.edu.catolica.Reservas.model.Usuario;
import sv.edu.catolica.Reservas.repository.UsuarioRepository;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepo;

    public CustomUserDetailsService(UsuarioRepository usuarioRepo) {
        this.usuarioRepo = usuarioRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        Usuario u = usuarioRepo.findByCorreo(correo)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        String rol = u.getRol() != null ? u.getRol().getRolNombre() : "Administrador";
        return new User(u.getCorreo(), u.getContrasena(),
                u.getActivo() != null && u.getActivo(),
                true, true, true,
                List.of(new SimpleGrantedAuthority(rol)));
    }
}
