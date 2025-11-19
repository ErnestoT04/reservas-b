package sv.edu.catolica.Reservas.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import sv.edu.catolica.Reservas.model.*;
import sv.edu.catolica.Reservas.repository.*;

import java.time.LocalDateTime;

@Service
public class EmailNotificationService {

    private final JavaMailSender mailSender;
    private final NotificacionRepository notificacionRepository;
    private final EstadoNotificacionRepository estadoNotificacionRepository;
    private final TipoNotificacionRepository tipoNotificacionRepository;

    public EmailNotificationService(JavaMailSender mailSender,
                                    NotificacionRepository notificacionRepository,
                                    EstadoNotificacionRepository estadoNotificacionRepository,
                                    TipoNotificacionRepository tipoNotificacionRepository) {
        this.mailSender = mailSender;
        this.notificacionRepository = notificacionRepository;
        this.estadoNotificacionRepository = estadoNotificacionRepository;
        this.tipoNotificacionRepository = tipoNotificacionRepository;
    }

    public void enviarNotificacion(Reserva reserva, String mensaje) {

        TipoNotificacion tipo = tipoNotificacionRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("Tipo EMAIL no existe"));

        EstadoNotificacion estadoEnviado = estadoNotificacionRepository.findByNombre("ENVIADO");
        EstadoNotificacion estadoError = estadoNotificacionRepository.findByNombre("ERROR");

        Notificacion n = new Notificacion();
        n.setReserva(reserva);
        n.setTipoNotificacion(tipo);
        n.setMensaje(mensaje);
        n.setFechaEnvio(LocalDateTime.now());

        try {
            SimpleMailMessage mail = new SimpleMailMessage();
        String s = reserva.getUsuario().getCorreo();
            mail.setTo(reserva.getUsuario().getCorreo());
            mail.setSubject("Actualización de reserva");
            mail.setText(mensaje);

            mailSender.send(mail);

            n.setEstadoNotificacion(estadoEnviado);

        } catch (Exception e) {
            n.setEstadoNotificacion(estadoError);
            n.setMensaje("ERROR AL ENVIAR: " + mensaje);
        }

        notificacionRepository.save(n);
    }
}
