package ph.devcon.rapidpass.service.notification;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import ph.devcon.rapidpass.enums.PassType;
import ph.devcon.rapidpass.services.notifications.EmailNotificationService;
import ph.devcon.rapidpass.services.notifications.NotificationException;
import ph.devcon.rapidpass.services.notifications.NotificationMessage;
import ph.devcon.rapidpass.services.notifications.templates.EmailNotificationTemplate;

import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EmailServiceTest{

    @Mock
    private JavaMailSender sender;

    @Mock
    private MimeMessage msg;

    @Test
    public void sendMail() {
        EmailNotificationService email = new EmailNotificationService();

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


    @Test
    public void testSmsFormatting_INDIVIDUAL() {

        EmailNotificationTemplate template = EmailNotificationTemplate.builder()
                .passType(PassType.INDIVIDUAL)
                .url("https://www.google.com")
                .build();

        String message = template.compose();

        assertThat(message, equalTo("Your entry has been approved. We've sent you a list of instructions on how you can use your QR code along with a printable file that you can use at the checkpoint. You can download your QR code on RapidPass.ph by following this link: https://www.google.com"));
    }


    @Test
    public void testSmsFormatting_failIndividual() {

        EmailNotificationTemplate template = EmailNotificationTemplate.builder()
                .reason("Arbitrary reason here")
                .passType(PassType.INDIVIDUAL)
                .build();

        String message = template.compose();

        assertThat(message, equalTo("Your entry has been rejected due to Arbitrary reason here. Please contact RapidPass-dctx@devcon.ph for further concerns and inquiry."));
    }

    @Test
    public void testSmsFormatting_VEHICLE() {

        EmailNotificationTemplate template = EmailNotificationTemplate.builder()
                .passType(PassType.VEHICLE)
                .url("https://www.google.com")
                .build();

        String message = template.compose();

        assertThat(message, equalTo("Your entry for your vehicle has been approved. We've sent you a list of instructions on how you can use your QR code along with a printable file that you can use at the checkpoint. You can download your QR code on RapidPass.ph by following this link: https://www.google.com"));
    }

    @Test
    public void testSmsFormatting_failVehicle() {

        EmailNotificationTemplate template = EmailNotificationTemplate.builder()
                .reason("Arbitrary reason here")
                .passType(PassType.VEHICLE)
                .build();

        String message = template.compose();

        assertThat(message, equalTo("Your entry for your vehicle has been rejected due to Arbitrary reason here. Please contact RapidPass-dctx@devcon.ph} for further concerns and inquiry."));
    }

    @Test
    public void failSmsFormatting_missingParameterForVehicle() {

        EmailNotificationTemplate template = EmailNotificationTemplate.builder()
                .passType(PassType.INDIVIDUAL)
                .build();

        assertThrows(IllegalArgumentException.class, () -> {
            String message = template.compose();
        });
    }
}
