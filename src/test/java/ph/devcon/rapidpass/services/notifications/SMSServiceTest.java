package ph.devcon.rapidpass.services.notifications;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.client.RestTemplate;
import ph.devcon.rapidpass.enums.PassType;
import ph.devcon.rapidpass.services.notifications.templates.SMSNotificationTemplate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest(classes = {SMSNotificationService.class})
public class SMSServiceTest {

    @MockBean
    RestTemplate template;

    @Autowired
    @Qualifier("sms")
    private NotificationService sms;

    // TODO Ignoring test since this sends to actual semaphore. needs refactoring.
    // @Test
    public void testSMS() {
        NotificationMessage msg = NotificationMessage.New().
                to("add phone number here").
            message("This is a test").
            create();
        try {
            if (sms == null) {
                fail("sms is null, failing");
            }
            sms.send(msg);
        } catch (NotificationException e) {
            fail(e);
        }
    }

    @Test
    @Disabled // TODO FIXME
    public void testSmsFormatting_INDIVIDUAL() {

        SMSNotificationTemplate template = SMSNotificationTemplate.builder()
                .name("Darren")
                .controlCode("1234")
                .passType(PassType.INDIVIDUAL)
                .url("https://www.google.com")
                .build();

        String message = template.compose();

        assertThat(message, equalTo("Hi, Darren. Your RapidPass has been approved! Your RapidPass control number is 1234. You can also download your QR code on RapidPass.ph by following this link: https://www.google.com"));
    }


    @Test
    @Disabled // TODO FIXME
    public void testSmsFormatting_failIndividual() {

        SMSNotificationTemplate template = SMSNotificationTemplate.builder()
                .name("Darren")
                .passType(PassType.INDIVIDUAL)
                .build();

        String message = template.compose();

        assertThat(message, equalTo("Hi, Darren. Your entry has been rejected due to incomplete field/s. Please register individually via RapidPass.ph to get your QR code."));
    }

    @Test
    public void testSmsFormatting_VEHICLE() {

        SMSNotificationTemplate template = SMSNotificationTemplate.builder()
                .name("Darren")
                .controlCode("1234")
                .vehiclePlateNumber("ABC 1234")
                .passType(PassType.VEHICLE)
                .url("https://www.google.com")
                .build();

        String message = template.compose();

        assertThat(message, equalTo("Hi, Darren. Your RapidPass has been approved for PLATE NO ABC 1234! Your RapidPass control number is 1234. You can download your QR code on rapidpass.ph by following this link: https://www.google.com"));
    }

    @Test
    public void testSmsFormatting_failVehicle() {

        SMSNotificationTemplate template = SMSNotificationTemplate.builder()
                .name("Darren")
                .vehiclePlateNumber("ABC 1234")
                .passType(PassType.VEHICLE)
                .build();

        String message = template.compose();

        assertThat(message, equalTo("Hi, Darren. Your entry has been rejected due to incomplete field/s. Please register individually via RapidPass.ph to get your QR code."));
    }

    @Test
    public void failSmsFormatting_missingParameterForVehicle() {

        SMSNotificationTemplate template = SMSNotificationTemplate.builder()
                .name("Darren")
                .controlCode("1234")
                .passType(PassType.VEHICLE)
                .url("https://www.google.com")
                .build();

        assertThrows(IllegalArgumentException.class, () -> {
            String message = template.compose();
        });
    }
}
