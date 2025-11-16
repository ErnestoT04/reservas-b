package sv.edu.catolica.Reservas.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sv.edu.catolica.Reservas.model.Reserva;
import sv.edu.catolica.Reservas.service.AdminReservaService;

import java.util.List;

@RestController
@RequestMapping("/api/admin/reservas")
public class AdminReservaController {

    private final AdminReservaService adminReservaService;

    public AdminReservaController(AdminReservaService adminReservaService) {
        this.adminReservaService = adminReservaService;
    }

    // 🔵 LISTAR TODAS O FILTRADAS
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping
    public List<Reserva> filtrarReservas(
            @RequestParam(required = false) String fecha,
            @RequestParam(required = false) Long usuarioId,
            @RequestParam(required = false) Long estadoId) {

        return adminReservaService.filtrarReservas(fecha, usuarioId, estadoId);
    }
}
