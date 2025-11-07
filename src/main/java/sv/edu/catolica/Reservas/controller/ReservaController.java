package sv.edu.catolica.Reservas.controller;

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

    @GetMapping("/mesas/disponibles")
    public List<Mesa> obtenerMesasDisponibles() {
        return reservaService.obtenerMesasDisponibles();
    }

    @PostMapping("/reservas")
    public Reserva crearReserva(@RequestBody Map<String, Object> datos) {
        Long idUsuario = Long.valueOf(datos.get("idUsuario").toString());
        Long idMesa = Long.valueOf(datos.get("idMesa").toString());
        LocalDateTime fechaHora = LocalDateTime.parse(datos.get("fechaHora").toString());
        Integer personas = Integer.valueOf(datos.get("personas").toString());
        return reservaService.crearReserva(idUsuario, idMesa, fechaHora, personas);
    }

    @GetMapping("/reservas/mias/{idUsuario}")
    public List<Reserva> obtenerMisReservas(@PathVariable Long idUsuario) {
        return reservaService.obtenerMisReservas(idUsuario);
    }

    @GetMapping("/reservas/{id}")
    public Reserva obtenerReservaPorId(@PathVariable Long id) {
        return reservaService.obtenerReservaPorId(id)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));
    }

    @PutMapping("/reservas/{id}")
    public Reserva actualizarReserva(@PathVariable Long id, @RequestBody Map<String, Object> datos) {
        LocalDateTime fechaHora = LocalDateTime.parse(datos.get("fechaHora").toString());
        Integer personas = Integer.valueOf(datos.get("personas").toString());
        return reservaService.actualizarReserva(id, fechaHora, personas);
    }

    @PatchMapping("/reservas/{id}/cancelar")
    public Reserva cancelarReserva(@PathVariable Long id) {
        return reservaService.cancelarReserva(id);
    }
}
