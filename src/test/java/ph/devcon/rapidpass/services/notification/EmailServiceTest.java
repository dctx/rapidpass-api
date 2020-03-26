package ph.devcon.rapidpass.services.notification;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.BDDMockito.Then;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.javamail.JavaMailSender;

import ph.devcon.rapidpass.services.notification.EmailNotification;

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
