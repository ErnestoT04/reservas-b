package sv.edu.catolica.Reservas.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long idUsuario;

    @ManyToOne
    @JoinColumn(name = "id_rol", nullable = false)
    private Rol rol;

    @Column(name = "usu_nombres", nullable = false, length = 100)
    private String nombres;

    @Column(name = "usu_apellidos", nullable = false, length = 100)
    private String apellidos;

    @Column(name = "usu_correo", nullable = false, unique = true, length = 150)
    private String correo;

    @Column(name = "usu_contrasena", nullable = false, length = 255)
    private String contrasena;

    @Column(name = "usu_activo")
    private Boolean activo = true;

    @Column(name = "usu_fechacreacion")
    private LocalDateTime fechaCreacion = LocalDateTime.now();


}
