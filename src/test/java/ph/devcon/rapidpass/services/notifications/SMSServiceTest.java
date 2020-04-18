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
    public void testSmsFormatting_INDIVIDUAL() {

        SMSNotificationTemplate template = SMSNotificationTemplate.builder()
                .name("Darren")
                .controlCode("1234")
                .passType(PassType.INDIVIDUAL)
                .url("https://www.google.com")
                .build();

        String message = template.compose();

        assertThat(message, equalTo("Your RapidPass has been approved with control number 1234. Download your QR here: https://www.google.com. DO NOT share your QR."));
    }


    @Test
    public void testSmsFormatting_failIndividual() {

        SMSNotificationTemplate template = SMSNotificationTemplate.builder()
        		.reason("incomplete field/s")
                .passType(PassType.INDIVIDUAL)
                .build();

        String message = template.compose();

        assertThat(message, equalTo("Your RapidPass has been rejected. Please contact your approving agency for further inquiries."));
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

        assertThat(message, equalTo("Your RapidPass has been approved with control code 1234 for PLATE NO ABC 1234! Download your QR here: https://www.google.com. DO NOT share your QR."));
    }

    @Test
    public void testSmsFormatting_failVehicle() {

        SMSNotificationTemplate template = SMSNotificationTemplate.builder()
        		.vehiclePlateNumber("ABC 1234")
        		.passType(PassType.VEHICLE)
                .build();

        String message = template.compose();

        assertThat(message, equalTo("Your RapidPass for the vehicle has been rejected. Please contact your approving agency for further inquiries."));
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
