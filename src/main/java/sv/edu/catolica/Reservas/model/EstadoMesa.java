package sv.edu.catolica.Reservas.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "estado_mesa")
public class EstadoMesa {
     @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_estadomesa")
    private Long idEstadoMesa;

    @Column(name = "est_nombre")
    private String nombre;

    @Column(name = "est_descripcion")
    private String descripcion;
}
