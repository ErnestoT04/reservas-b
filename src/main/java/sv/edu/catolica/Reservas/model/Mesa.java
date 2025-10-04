package sv.edu.catolica.Reservas.model;

import jakarta.persistence.*;

@Entity
@Table(name = "mesa")
public class Mesa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_mesa")
    private Long idMesa;

    @Column(name = "mesa_numeromesa", nullable = false, unique = true)
    private Integer numeroMesa;

    @Column(name = "mesa_cantida", nullable = false)
    private Integer cantidad;

    @ManyToOne
    @JoinColumn(name = "id_estamesa", nullable = false)
    private EstadoMesa estadoMesa;
}

