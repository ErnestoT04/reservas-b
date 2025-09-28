package sv.edu.catolica.Reservas.model;

import jakarta.persistence.*;

@Entity
@Table(name = "notificacion")
public class Notificacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_notificacion")
    private Long idNotificacion;

    @ManyToOne
    @JoinColumn(name = "id_reserva", nullable = false)
    private Reserva reserva;

    @ManyToOne
    @JoinColumn(name = "id_estadonoti", nullable = false)
    private EstadoNotificacion estadoNotificacion;

    @ManyToOne
    @JoinColumn(name = "id_tiponotificacion", nullable = false)
    private TipoNotificacion tipoNotificacion;

    @Column(name = "noti_mensaje", nullable = false)
    private String mensaje;

    @Column(name = "noti_fechaenvio")
    private LocalDateTime fechaEnvio = LocalDateTime.now();
}

