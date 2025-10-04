package sv.edu.catolica.Reservas.authentication.dto;

public record AuthResponse(
    Long usu_id,
    String usu_nombre,
    String usu_apellido,
    Long rol_id,
    String token
) {}