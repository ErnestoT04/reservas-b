package sv.edu.catolica.Reservas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import sv.edu.catolica.Reservas.model.Mesa;
import sv.edu.catolica.Reservas.model.Reserva;
import sv.edu.catolica.Reservas.model.Usuario;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {
        List<Reserva> findByUsuario(Usuario usuario);

        List<Reserva> findByMesa(Mesa mesa);

        boolean existsByMesaAndFechaHoraBeforeAndFechaHoraCierreAfter(
                        Mesa mesa,
                        LocalDateTime inicio,
                        LocalDateTime fin);

        @Query("""
                            SELECT r FROM Reserva r
                            WHERE (:usuarioId IS NULL OR r.usuario.idUsuario = :usuarioId)
                              AND (:estadoId IS NULL OR r.estadoReserva.idEstadoReserva = :estadoId)
                        """)
        List<Reserva> filtrarSinFecha(Long usuarioId, Long estadoId);

        @Query("""
                        SELECT r
                        FROM Reserva r
                        WHERE DATE(r.fechaHora) = :fecha
                        ORDER BY r.fechaHora ASC
                        """)
        List<Reserva> reservasPorDia(LocalDate fecha);

        @Query("""
                        SELECT r.mesa.idMesa AS mesaId,
                               COUNT(r.idReserva) AS totalReservas
                        FROM Reserva r
                        WHERE r.fechaHora BETWEEN :inicio AND :fin
                        GROUP BY r.mesa.idMesa
                        ORDER BY totalReservas DESC
                        """)
        List<Object[]> usoDeMesas(LocalDateTime inicio, LocalDateTime fin);


        @Query("""
SELECT r
FROM Reserva r
WHERE r.mesa.idMesa = :idMesa
  AND r.estadoReserva.idEstadoReserva IN (1, 2)
  AND r.fechaHora < :fin
  AND r.fechaHoraCierre > :inicio
""")
        List<Reserva> reservasSolapadas(
                Long idMesa,
                LocalDateTime inicio,
                LocalDateTime fin
        );


}
