package ph.devcon.rapidpass.services.notifications;

import java.util.Map.Entry;

import javax.activation.DataSource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@Qualifier("email")
public class EmailNotificationService implements NotificationService {

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
        } catch (MessagingException | MailException e) {
            throw new NotificationException(e);
        }
    }

}
