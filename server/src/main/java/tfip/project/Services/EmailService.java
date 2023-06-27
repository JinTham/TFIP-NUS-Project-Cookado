package tfip.project.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendRegisterEmail(String username, String email) {
        SimpleMailMessage message = new SimpleMailMessage();
        String subject = "Registration completed";
        String body = "Welcome %s, thank you very much for your interest in our app!".formatted(username);
        message.setTo(email);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }
}

