/*
 * Copyright (c) 2020.  DevConnect Philippines, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and limitations under the License.
 */

package ph.devcon.rapidpass.services.notifications;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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
class SMSNotificationServiceTest {
    SMSNotificationService smsNotificationService;

    @Mock
    RestTemplate mockRestTemplate;

    @BeforeEach
    void setUp() {
        smsNotificationService = new SMSNotificationService(mockRestTemplate);
        smsNotificationService.setApiKey("TEST_API_KEY");
        smsNotificationService.setUrl("TEST_URL");
    }

    // sample test with mock
    @Test
    void mockSend_SUCCESS() throws NotificationException
    {

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

    /**
     * The SMS Notification service should throw a {@link NotificationException} if the API key is not configured.
     */
    @Test
    void mockSend_throwExceptionWhenApiKeyNotConfigured() {

        Assertions.assertThrows(NotificationException.class, () -> {

            // Will cause exception
            smsNotificationService.setApiKey("");

            // act
            NotificationMessage message = NotificationMessage.New().
                    message("hello world")
                    .to("12345")
                    .from("me")
                    .create();

            smsNotificationService.send(message);
        });
    }

    /**
     * The SMS Notification service should throw a {@link NotificationException} if the URL is not configured.
     */
    @Test
    void mockSend_throwExceptionWhenUrlNotConfigured()  {

        Assertions.assertThrows(NotificationException.class, () -> {

            // Will cause exception
            smsNotificationService.setUrl("");

            // act
            NotificationMessage message = NotificationMessage.New().
                    message("hello world")
                    .to("12345")
                    .from("me")
                    .create();

            smsNotificationService.send(message);
        });
    }

    @Test
    void testFormatNumber() {
        Assertions.assertEquals(smsNotificationService.formatNumber("09171234567"), "+639171234567");
        Assertions.assertEquals(smsNotificationService.formatNumber("0917 1234567"), "+639171234567");
        Assertions.assertEquals(smsNotificationService.formatNumber("00639171234567"), "+639171234567");
        Assertions.assertEquals(smsNotificationService.formatNumber("+639171234567"), "+639171234567");
        Assertions.assertEquals(smsNotificationService.formatNumber("+6512345678"), "+6512345678");
    }

    // sample test with actual rest template
    @Test
    @Disabled
    void actualSend() throws NotificationException {
        // use actual rest template
        smsNotificationService = new SMSNotificationService(new RestTemplate());
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