package sv.edu.catolica.Reservas.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "reserva")
public class Reserva {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reserva")
    private Long idReserva;

    @ManyToOne
    @JoinColumn(name = "id_mesa", nullable = false)
    private Mesa mesa;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_estadoreserva", nullable = false)
    private EstadoReserva estadoReserva;

    @Column(name = "res_fechahora", nullable = false)
    private LocalDateTime fechaHora;

    @Column(name = "res_fechacreacion")
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @Column(name = "res_cantidadpersonas", nullable = false)
    private Integer cantidadPersonas;
}
