package sv.edu.catolica.Reservas.service;

import org.springframework.stereotype.Service;
import sv.edu.catolica.Reservas.model.EstadoMesa;
import sv.edu.catolica.Reservas.model.Mesa;
import sv.edu.catolica.Reservas.repository.EstadoMesaRepository;
import sv.edu.catolica.Reservas.repository.MesaRepository;

import java.util.List;

@Service
public class MesaService {

    private final MesaRepository mesaRepository;
    private final EstadoMesaRepository estadoMesaRepository;

    public MesaService(MesaRepository mesaRepository,
                       EstadoMesaRepository estadoMesaRepository) {
        this.mesaRepository = mesaRepository;
        this.estadoMesaRepository = estadoMesaRepository;
    }

    // ===============================
    // 🔵 LISTAR TODAS
    // ===============================
    public List<Mesa> listarTodas() {
        return mesaRepository.findAll();
    }

    // ===============================
    // 🔵 OBTENER POR ID
    // ===============================
    public Mesa obtenerPorId(Long id) {
        return mesaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mesa no encontrada."));
    }

    // ===============================
       // 🔵 CREAR MESA
    // ===============================
    public Mesa crearMesa(Integer numero, Integer cantidad, String estadoNombre) {

        EstadoMesa estadoMesa = obtenerEstado(estadoNombre);

        Mesa mesa = new Mesa();
        mesa.setNumeroMesa(numero);
        mesa.setCantidad(cantidad);
        mesa.setEstadoMesa(estadoMesa);

        return mesaRepository.save(mesa);
    }

    // ===============================
    // 🔵 ACTUALIZAR MESA
    // ===============================
    public Mesa actualizarMesa(Long id, Integer numero, Integer cantidad, String estadoNombre) {

        Mesa mesa = obtenerPorId(id);

        EstadoMesa estadoMesa = obtenerEstado(estadoNombre);

        mesa.setNumeroMesa(numero);
        mesa.setCantidad(cantidad);
        mesa.setEstadoMesa(estadoMesa);

        return mesaRepository.save(mesa);
    }

    // ===============================
    // 🔵 ELIMINAR MESA
    // ===============================
    public void eliminarMesa(Long id) {
        Mesa mesa = obtenerPorId(id);
        mesaRepository.delete(mesa);
    }

    // ===============================
    // 🔵 AUXILIAR: OBTENER ESTADO
    // ===============================
    private EstadoMesa obtenerEstado(String estado) {
        EstadoMesa encontrado = estadoMesaRepository.findByNombre(estado);
        if (encontrado == null)
            throw new RuntimeException("El estado '" + estado + "' no existe.");
        return encontrado;
    }
}
