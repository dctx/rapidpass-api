package ph.devcon.rapidpass.services.notification;

import java.util.Map.Entry;

import javax.activation.DataSource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import ph.devcon.rapidpass.api.models.RapidPassRequest;

@Service(value = "emailService")
public class EmailNotification implements NotificationService {

    @Autowired
    public JavaMailSender emailSender;

    @Override
    public void send(NotificationMessage message) throws NotificationException {
        MimeMessage msg = emailSender.createMimeMessage();
        
        try {
            MimeMessageHelper helper = new MimeMessageHelper(msg, true);
            helper.setFrom(message.getFrom());
            helper.setTo(message.getTo());
            helper.setSubject(message.getTitle());
            helper.setText(message.getMessage());
            for (Entry<String, DataSource> attachment : message.getAttachments().entrySet()) {
                helper.addAttachment(attachment.getKey(), attachment.getValue());
            }
            emailSender.send(msg);
        } catch (MessagingException e) {
            throw new NotificationException(e);
        } catch (MailException e) {
            throw new NotificationException(e);
        }
    }

}
