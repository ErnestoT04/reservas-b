package sv.edu.catolica.Reservas.service;

import org.springframework.stereotype.Service;
import sv.edu.catolica.Reservas.model.Reserva;
import sv.edu.catolica.Reservas.repository.ReservaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class AdminReportService {

    private final ReservaRepository reservaRepository;

    public AdminReportService(ReservaRepository reservaRepository) {
        this.reservaRepository = reservaRepository;
    }

    // 🔵 Reporte: Reservas por día
    public List<Reserva> reservasPorDia(String fechaStr) {
        LocalDate fecha = LocalDate.parse(fechaStr);
        return reservaRepository.reservasPorDia(fecha);
    }

    // 🔵 Reporte: Uso de mesas (por rango de fechas)
    public List<Map<String, Object>> usoDeMesas(String desdeStr, String hastaStr) {
        LocalDateTime desde = LocalDateTime.parse(desdeStr);
        LocalDateTime hasta = LocalDateTime.parse(hastaStr);

        List<Object[]> raw = reservaRepository.usoDeMesas(desde, hasta);

        List<Map<String, Object>> resultado = new ArrayList<>();

        for (Object[] fila : raw) {
            Map<String, Object> map = new HashMap<>();
            map.put("mesaId", fila[0]);
            map.put("totalReservas", fila[1]);
            resultado.add(map);
        }

        return resultado;
    }
}
