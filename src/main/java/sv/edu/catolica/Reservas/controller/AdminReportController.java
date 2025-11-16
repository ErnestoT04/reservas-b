package sv.edu.catolica.Reservas.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sv.edu.catolica.Reservas.model.Reserva;
import sv.edu.catolica.Reservas.service.AdminReportService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/reportes")
public class AdminReportController {

    private final AdminReportService adminReportService;

    public AdminReportController(AdminReportService adminReportService) {
        this.adminReportService = adminReportService;
    }

    // 🔵 Reporte: Reservas por día
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping("/reservas-dia")
    public List<Reserva> reservasPorDia(@RequestParam String fecha) {
        return adminReportService.reservasPorDia(fecha);
    }

    // 🔵 Reporte: Uso de mesas por rango de fechas
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping("/uso-mesas")
    public List<Map<String, Object>> usoDeMesas(
            @RequestParam String desde,
            @RequestParam String hasta) {
        return adminReportService.usoDeMesas(desde, hasta);
    }
}
