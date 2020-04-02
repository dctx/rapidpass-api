package ph.devcon.rapidpass.controllers;

import lombok.RequiredArgsConstructor;
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
public class FuncTesterController {

    private final EmailNotificationService emailNotificationService;
    private final SMSNotificationService smsNotificationService;

    @Value("${testMobile:}")
    private String testMobile = "";

    @Value("${testEmail:}")
    private String testEmail = "";


    @PostMapping("/sms")
    public HttpEntity<?> testSms() throws NotificationException {
        smsNotificationService.send(NotificationMessage.New()
                .message("Hello World from Rapidpass!")
                .to(testMobile)
                .create());
        return ResponseEntity.ok("OK");
    }

    @PostMapping("email")
    public HttpEntity<?> testEmail() throws NotificationException {
        emailNotificationService.send(NotificationMessage.New()
                .to(testEmail)
                .message("Hello World From RapidPass")
                .from("do-no-reply@.com")
                .create());
        return ResponseEntity.ok("OK");
    }

    // todo testers for other services
}
