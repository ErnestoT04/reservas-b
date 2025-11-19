package sv.edu.catolica.Reservas.authentication;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import sv.edu.catolica.Reservas.authentication.dto.AuthRequest;
import sv.edu.catolica.Reservas.authentication.dto.AuthResponse;
import sv.edu.catolica.Reservas.authentication.dto.RegisterRequest;
import sv.edu.catolica.Reservas.model.Rol;
import sv.edu.catolica.Reservas.model.Usuario;
import sv.edu.catolica.Reservas.repository.RolRepository;
import sv.edu.catolica.Reservas.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;
    private final UsuarioRepository usuarioRepo;
    private final RolRepository rolRepo;
    private final PasswordEncoder encoder;
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    public AuthController(AuthenticationManager authManager, JwtUtil jwtUtil,
            UsuarioRepository usuarioRepo, RolRepository rolRepo,
            PasswordEncoder encoder) {
        this.authManager = authManager;
        this.jwtUtil = jwtUtil;
        this.usuarioRepo = usuarioRepo;
        this.rolRepo = rolRepo;
        this.encoder = encoder;
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest req) {

        // 1. LOG: Mostrar el correo que intenta iniciar sesión
        log.info("Intento de inicio de sesión para el correo: {}", req.correo());

        authManager.authenticate(new UsernamePasswordAuthenticationToken(req.correo(), req.contrasena()));

        Usuario u = usuarioRepo.findByCorreo(req.correo()).orElseThrow();


        // Obtener el rol
        String rolNombre = u.getRol() != null ? u.getRol().getRolNombre() : "USER";

        // 2. LOG: Mostrar el usuario y el rol encontrado
        log.info("Usuario autenticado ID: {}, Nombre: {} {}, Rol: {}",
                u.getIdUsuario(), u.getNombres(), u.getApellidos(), rolNombre);


        Map<String, Object> claims = new HashMap<>();
        claims.put("rol", u.getRol() != null ? u.getRol().getRolNombre() : "USER");
        claims.put("uid", u.getIdUsuario());

        String token = jwtUtil.generateToken(u.getCorreo(), claims);

        Long rolId = (u.getRol() != null) ? u.getRol().getIdRol() : null;

        return new AuthResponse(
                u.getIdUsuario(),
                u.getNombres(),
                u.getApellidos(),
                rolId,
                token);
    }

    @PostMapping("/register")
    public AuthResponse register(@RequestBody RegisterRequest req) {
        if (usuarioRepo.existsByCorreo(req.correo())) {
            throw new RuntimeException("El correo ya está registrado");
        }

        Rol rol = rolRepo.findById(2L)
                .orElseThrow(() -> new RuntimeException("Rol por defecto no encontrado"));

        Usuario u = new Usuario();
        u.setNombres(req.nombres());
        u.setApellidos(req.apellidos());
        u.setCorreo(req.correo());
        u.setContrasena(encoder.encode(req.contrasena()));
        u.setRol(rol);
        u.setActivo(true);

        usuarioRepo.save(u);

        Map<String, Object> claims = new HashMap<>();
        claims.put("rol", rol.getRolNombre());
        claims.put("uid", u.getIdUsuario());

        String token = jwtUtil.generateToken(u.getCorreo(), claims);

        return new AuthResponse(
                u.getIdUsuario(),
                u.getNombres(),
                u.getApellidos(),
                rol.getIdRol(),
                token);
    }

}