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

package ph.devcon.rapidpass.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ph.devcon.rapidpass.services.notifications.EmailNotificationService;
import ph.devcon.rapidpass.services.notifications.NotificationException;
import ph.devcon.rapidpass.services.notifications.NotificationMessage;
import ph.devcon.rapidpass.services.notifications.SMSNotificationService;

/**
 * The {@link FuncTesterController} provides backend only endpoints for backend peeps to quickly test services' health.
 */
@RestController
@RequestMapping("/tester")
@RequiredArgsConstructor
@Slf4j
public class FuncTesterController {

    private final EmailNotificationService emailNotificationService;
    private final SMSNotificationService smsNotificationService;

    @Value("${testMobile:}")
    private String testMobile = "";

    @Value("${testEmail:}")
    private String testEmail = "";


    @PostMapping("/sms")
    public HttpEntity<?> testSms() throws NotificationException {
        log.info("testSms {}", testMobile);
        smsNotificationService.send(NotificationMessage.New()
                .message("Hello World from Rapidpass!")
                .to(testMobile)
                .create());
        return ResponseEntity.ok("OK");
    }

    @PostMapping("email")
    public HttpEntity<?> testEmail() throws NotificationException {
        log.info("testEmail {}", testEmail);
        emailNotificationService.send(NotificationMessage.New()
                .to(testEmail)
                .title("RapidPass test Email")
                .message("Hello World From RapidPass")
                .from("do-no-reply@me.com")
                .create());
        return ResponseEntity.ok("OK");
    }

    // todo testers for other services
}
