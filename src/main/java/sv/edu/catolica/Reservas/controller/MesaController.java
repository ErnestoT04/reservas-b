package sv.edu.catolica.Reservas.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sv.edu.catolica.Reservas.model.EstadoMesa;
import sv.edu.catolica.Reservas.model.Mesa;
import sv.edu.catolica.Reservas.service.MesaService;
import sv.edu.catolica.Reservas.service.ReservaService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/mesas")
public class MesaController {

    private final MesaService mesaService;
    private final ReservaService reservaService;

    public MesaController(MesaService mesaService, ReservaService reservaService) {
        this.mesaService = mesaService;
        this.reservaService = reservaService;
    }

    // ===============================
    // 🔵 LISTAR TODAS LAS MESAS
    // ===============================
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping
    public List<Mesa> listarTodas() {
        reservaService.actualizarEstadoMesas();
        return mesaService.listarTodas();
    }

    // ===============================
    // 🔵 OBTENER UNA MESA POR ID
    // ===============================
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping("/{id}")
    public Mesa obtenerPorId(@PathVariable Long id) {
        reservaService.actualizarEstadoMesas();
        return mesaService.obtenerPorId(id);
    }

    // ===============================
    // 🔵 CREAR MESA
    // ===============================
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PostMapping
    public Mesa crearMesa(@RequestBody Map<String, Object> body) {

        Integer numero = (Integer) body.get("numeroMesa");
        Integer cantidad = (Integer) body.get("cantidad");
        String estado = body.get("estado").toString();

        return mesaService.crearMesa(numero, cantidad, estado);
    }

    // ===============================
    // 🔵 ACTUALIZAR MESA
    // ===============================
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PutMapping("/{id}")
    public Mesa actualizarMesa(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body) {

        Integer numero = (Integer) body.get("numeroMesa");
        Integer cantidad = (Integer) body.get("cantidad");
        String estado = body.get("estado").toString();

        return mesaService.actualizarMesa(id, numero, cantidad, estado);
    }

    // ===============================
    // 🔵 ELIMINAR MESA
    // ===============================
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @DeleteMapping("/{id}")
    public String eliminarMesa(@PathVariable Long id) {
        mesaService.eliminarMesa(id);
        return "Mesa eliminada correctamente.";
    }
}
