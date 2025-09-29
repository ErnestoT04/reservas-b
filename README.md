# CHANGELOG

---

## [0.1.0] - 2025-09-28
### Added
- **Usuario**: creación de la entidad `Usuario`.  
- **Rol**: incorporación de la entidad `Rol`.  
- **Mesa**: implementación de la entidad `Mesa`.  
- **EstadoMesa**: implementación de la entidad `EstadoMesa`.  
- **Reserva**: creación de la entidad `Reserva` y `EstadoReserva`.  
- **Notificación**: adición de las entidades `Notificación`, `TipoNotificación` y `EstadoNotificación`.  
- **Repositorio Usuario**: creación del repositorio para la entidad `Usuario`.  
- **Autenticación**: implementación de endpoints de autenticación (registro y login).  
- **Servicio de Usuarios**: lógica para el manejo de registro e inicio de sesión.  

### Changed / Refactor
- **Usuario**: refactorización del modelo mediante el uso de Lombok (setters, constructores).  

### Fixed
- Ajustes en el mapeo de entidades relacionadas (Usuario–Reserva).  

### Chore / Config
- **Proyecto base**: generado con Spring Initializr.  
- **Configuración**: conexión inicial a PostgreSQL establecida.  

---

## [Unreleased]
- Implementación de la lógica de notificaciones.  
- Módulo de gestión avanzada de reservas.  
- Integración con frontend (React).  
