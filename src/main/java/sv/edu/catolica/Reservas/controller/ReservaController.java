package sv.edu.catolica.Reservas.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sv.edu.catolica.Reservas.model.Mesa;
import sv.edu.catolica.Reservas.model.Reserva;
import sv.edu.catolica.Reservas.service.ReservaService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ReservaController {

    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @PreAuthorize("hasAnyRole('USUARIO', 'ADMINISTRADOR', 'EMPLEADO')")
    @GetMapping("/mesas/disponibles")
    public List<Mesa> obtenerMesasDisponibles() {
        reservaService.actualizarEstadoMesas();
        return reservaService.obtenerMesasDisponibles();
    }

    @PreAuthorize("hasAnyRole('USUARIO', 'ADMINISTRADOR', 'EMPLEADO')")
    @PostMapping("/reservas")
    public Reserva crearReserva(@RequestBody Map<String, Object> datos) {
        reservaService.actualizarEstadoMesas();
        Long idUsuario = Long.valueOf(datos.get("idUsuario").toString());
        Long idMesa = Long.valueOf(datos.get("idMesa").toString());
        LocalDateTime fechaHora = LocalDateTime.parse(datos.get("fechaHora").toString());
        LocalDateTime fechaHoraCierre = LocalDateTime.parse(datos.get("fechaHoraCierre").toString());
        Integer personas = Integer.valueOf(datos.get("personas").toString());

        return reservaService.crearReserva(idUsuario, idMesa, fechaHora, fechaHoraCierre, personas);
    }

    @PreAuthorize("hasAnyRole('USUARIO', 'ADMINISTRADOR', 'EMPLEADO')")
    @GetMapping("/reservas/mias/{idUsuario}")
    public List<Reserva> obtenerMisReservas(@PathVariable Long idUsuario) {
        reservaService.actualizarEstadoMesas();
        return reservaService.obtenerMisReservas(idUsuario);
    }

    @PreAuthorize("hasAnyRole('USUARIO', 'ADMINISTRADOR', 'EMPLEADO')")
    @GetMapping("/reservas/{id}")
    public Reserva obtenerReservaPorId(@PathVariable Long id) {
        reservaService.actualizarEstadoMesas();
        return reservaService.obtenerReservaPorId(id)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));
    }

    // ✅ Cualquiera puede editar sus reservas
    @PreAuthorize("hasAnyRole('USUARIO', 'ADMINISTRADOR', 'EMPLEADO')")
    @PutMapping("/reservas/{id}")
    public Reserva actualizarReserva(@PathVariable Long id, @RequestBody Map<String, Object> datos) {
        reservaService.actualizarEstadoMesas();
        LocalDateTime fechaHora = LocalDateTime.parse(datos.get("fechaHora").toString());
        Integer personas = Integer.valueOf(datos.get("personas").toString());
        return reservaService.actualizarReserva(id, fechaHora, personas);
    }

    // ✅ Cancelar reserva (solo usuario o admin)
    @PreAuthorize("hasAnyRole('USUARIO', 'ADMINISTRADOR', 'EMPLEADO')")
    @PatchMapping("/reservas/{id}/cancelar")
    public Reserva cancelarReserva(@PathVariable Long id) {
        reservaService.actualizarEstadoMesas();
        return reservaService.cancelarReserva(id);
    }
}
