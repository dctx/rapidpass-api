package ph.devcon.rapidpass.service.notification;

import static org.junit.jupiter.api.Assertions.fail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import ph.devcon.rapidpass.services.notifications.NotificationException;
import ph.devcon.rapidpass.services.notifications.NotificationMessage;
import ph.devcon.rapidpass.services.notifications.NotificationService;

@SpringBootTest
public class SMSServiceTest {

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
}