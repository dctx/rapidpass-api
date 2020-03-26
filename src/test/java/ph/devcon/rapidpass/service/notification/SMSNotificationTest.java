package ph.devcon.rapidpass.service.notification;

import org.junit.Ignore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SMSNotificationTest {
    SMSNotification smsNotificationService;

    @Mock
    RestTemplate mockRestTemplate;

    @BeforeEach
    void setUp() {
        smsNotificationService = new SMSNotification(mockRestTemplate);
        smsNotificationService.setApiKey("TEST_API_KEY");
        smsNotificationService.setUrl("TEST_URL");
    }

    // sample test with mock
    @Test
    void mockSend_SUCCESS() throws NotificationException {

        // setup mockRestTemplate
        when(mockRestTemplate.postForEntity(eq("TEST_URL"), any(HttpEntity.class), eq(String.class)))
                .thenReturn(ResponseEntity.ok("OK"));

        // act
        smsNotificationService.send(
                NotificationMessage.New().
                        message("hello word")
                        .to("12345")
                        .from("me")
                        .create());

        // assert

        // verify postForEntity called with expected vars
        verify(mockRestTemplate, only()).postForEntity(eq("TEST_URL"), any(HttpEntity.class), eq(String.class));
    }

    // sample test with actual rest template
    @Test
    @Ignore
    void actualSend() throws NotificationException {
        // use actual rest template
        smsNotificationService = new SMSNotification(new RestTemplate());
        smsNotificationService.setUrl("https://api.semaphore.co/api/v4/messages");
        smsNotificationService.setApiKey("Enter Key here");

        // WARNING THIS WILL ACTUALL CALL SERVICE
        smsNotificationService.send(
                NotificationMessage.New().
                        message("hello word")
                        .to("12345")
                        .from("me")
                        .create());
    }
}