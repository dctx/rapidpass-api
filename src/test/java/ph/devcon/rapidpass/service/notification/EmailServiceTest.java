package ph.devcon.rapidpass.service.notification;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;

import ph.devcon.rapidpass.service.notification.EmailNotification;

@SpringBootTest
public class EmailServiceTest{

    @Mock
    private JavaMailSender sender;

    @Mock
    private MimeMessage msg;

    @Test
    public void sendMail() {
        EmailNotification email = new EmailNotification();

        ArgumentCaptor<MimeMessage> arg = ArgumentCaptor.forClass(MimeMessage.class);
        Session s = null;
        when(sender.createMimeMessage()).thenReturn(msg);
        doNothing().when(sender).send(arg.capture());
        email.emailSender = sender;

        String to = "to@email.com";
        String from = "from@email.com";
        String title = "test title";
        String message = "test message";
        
        // ==== how to send notification
        String attachmentName = "attachment";
        byte[] sampleAttachment = { 0, 1, 2 };
        NotificationMessage msg = NotificationMessage.New()
            .to(to)
            .from(from)
            .title(title)
            .message(message)
            .addAttachment(attachmentName, "application/pdf", sampleAttachment)
            .create();

            try {
                email.send(msg);
            } catch(NotificationException e) {
                fail("should not receive exception");
            }

            // add more verification here
            
    } 

}
