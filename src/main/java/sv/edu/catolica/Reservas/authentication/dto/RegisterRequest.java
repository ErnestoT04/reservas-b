package sv.edu.catolica.Reservas.authentication.dto;

public record RegisterRequest(String nombres, String apellidos, String correo, String contrasena, String rolNombre) {}
