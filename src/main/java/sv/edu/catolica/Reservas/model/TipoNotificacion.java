package sv.edu.catolica.Reservas.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Table(name = "tipo_notificacion")
public class TipoNotificacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tiponotificacion")
    private Long idTipoNotificacion;

    @Column(name = "est_nombre", nullable = false, length = 50)
    private String nombre;

    @Column(name = "est_descripcion", length = 150)
    private String descripcion;
}
