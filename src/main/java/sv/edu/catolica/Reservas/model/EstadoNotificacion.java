package sv.edu.catolica.Reservas.model;

import jakarta.persistence.*;

@Entity
@Table(name = "estado_notificacion")
public class EstadoNotificacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_estadonoti")
    private Long idEstadoNotificacion;

    @Column(name = "est_nombre", nullable = false, length = 50)
    private String nombre;

    @Column(name = "est_descripcion", length = 150)
    private String descripcion;
}

