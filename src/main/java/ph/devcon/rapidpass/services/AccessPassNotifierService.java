package ph.devcon.rapidpass.services;

import com.google.zxing.WriterException;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ph.devcon.rapidpass.entities.AccessPass;
import ph.devcon.rapidpass.enums.AccessPassStatus;
import ph.devcon.rapidpass.enums.PassType;
import ph.devcon.rapidpass.repositories.AccessPassRepository;
import ph.devcon.rapidpass.services.notifications.NotificationMessage;
import ph.devcon.rapidpass.services.notifications.NotificationService;
import ph.devcon.rapidpass.services.notifications.templates.EmailNotificationTemplate;
import ph.devcon.rapidpass.services.notifications.templates.SMSNotificationTemplate;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.text.ParseException;

/**
 * The {@link AccessPassNotifierService} service is responsible for sending out sms and emails for approved access passes.
 *
 * @author jonasespelita@gmail.com
 */
@Service
@Setter
@Slf4j
public class AccessPassNotifierService {

    /**
     * Repo for access path queries.
     */
    private final AccessPassRepository accessPassRepository;

    /**
     * Service providing PDF creation for an access pass.
     */
    private final QrPdfService qrPdfService;

    private final NotificationService emailService;

    private final NotificationService smsService;

    // todo move to @ConfigurationProperties
    /**
     * The URL value notifier will use when inserting links to access pass. Defaults to http://rapidpass.ph.
     */
    @Value("${notifier.rapidPassUrl:http://rapidpass.ph}")
    private String rapidPassUrl = "http://rapidpass.ph";

    /**
     * The email address that will appear in the from field of the email sent.
     */
    @Value("${notifier.mailFrom:rapidpass-dctx@devcon.ph}")
    private String mailFrom = "rapidpass-dctx@devcon.ph";

    /**
     * The sender that will appear in the sms messages sent.
     */
    @Value("${notifier.smsFrom:RAPIDPASS.PH}")
    private String smsFrom = "RAPIDPASS.PH";

    /**
     * The configurable endpoint to send for users to download qr codes. Defaults to /api/v1/registry/qr-codes/{referenceId}
     */
    @Value("${notifier.qrCodeEndpoint:/api/v1/registry/qr-codes/}")
    private String qrCodeEndpoint = "/api/v1/registry/qr-codes/";

    public AccessPassNotifierService(AccessPassRepository accessPassRepository,
                                     QrPdfService qrPdfService,
                                     @Qualifier("email") NotificationService emailService,
                                     @Qualifier("sms") NotificationService smsService) {
        this.qrPdfService = qrPdfService;
        this.accessPassRepository = accessPassRepository;
        this.emailService = emailService;
        this.smsService = smsService;
    }

    /**
     * Pushes notifications depending on Access Pass status to sms and email.
     *
     * @param accessPass an access pass
     */
    public void pushApprovalDeniedNotifs(@Valid @NotNull AccessPass accessPass) throws ParseException, IOException, WriterException {
        final String accessPassReferenceId = accessPass.getReferenceID();

        log.debug("pushing approval notifications for {}", accessPassReferenceId);

        // pre checks

        // check pass exists
        final AccessPassStatus accessPassStatus = AccessPassStatus.valueOf(accessPass.getStatus().toUpperCase());

        // generate link for the access pass
        final String email = accessPass.getRegistrantId().getEmail();
        final String mobile = accessPass.getRegistrantId().getMobile();

        final PassType passType = PassType.valueOf(accessPass.getPassType().toUpperCase());

        final NotificationMessage emailMessage;
        final NotificationMessage smsMessage;
        switch (accessPassStatus) {
            case APPROVED:
                String accessPassUrl = generateAccessPassUrl(accessPassReferenceId);
                emailMessage = buildApprovedEmailMessage(
                        passType,
                        accessPassUrl,
                        accessPassReferenceId,
                        email);
                smsMessage = buildApprovedSmsMessage(
                        passType,
                        accessPassUrl,
                        mobile,
                        accessPass.getName(),
                        accessPass.getControlCode(),
                        accessPass.getIdentifierNumber());
                break;
            case DECLINED:
                emailMessage = buildDeclinedEmailMessage(passType, email, "TODO");
                smsMessage = buildDeclinedSmsMessage(
                        passType,
                        mobile,
                        accessPass.getName(),
                        accessPass.getIdentifierNumber());
                break;
            default:
                log.warn("Not sending out notification for " + accessPassStatus);
                return;

        }

        // create NotificationMessages to send out
        // email
        if (!StringUtils.isEmpty(email)) {
            try {
                emailService.send(emailMessage);
            } catch (Exception e) {
                // we want to continue despite any error in emailService
                log.error("Error sending email message to " + email, e);
            }
        }

        // sms
        if (!StringUtils.isEmpty(mobile)) {
            try {
                smsService.send(smsMessage);
            } catch (Exception e) {
                // we want to continue despite any error in smsService
                log.error("Error sending SMS message to " + mobile, e);
            }
        }
        log.debug("pushed approval notifications for {}", accessPassReferenceId);
    }

    /**
     * Generates the URL users can use to link to QR code PDF download.
     *
     * @param accessPassReferenceId referenceId of accessPass
     * @return generated URL
     */
    String generateAccessPassUrl(String accessPassReferenceId) {
        return String.format("%s%s%s",
                rapidPassUrl, qrCodeEndpoint, accessPassReferenceId);
    }

    /**
     * Builds a sms message to send.
     *
     * @param accessPassUrl link to qr code pdf download
     * @param mobile        mobile number to send message to
     * @return built message for sms service
     */
    NotificationMessage buildApprovedSmsMessage(PassType passType,
                                                String accessPassUrl,
                                                String mobile,
                                                String name,
                                                String controlCode,
                                                String vehiclePlateNumber) {
        return NotificationMessage.New()
                .from(smsFrom)
                .to(mobile)
                .message(SMSNotificationTemplate.builder()
                        .passType(passType)
                        .url(accessPassUrl)
                        .name(name)
                        .controlCode(controlCode)
                        .vehiclePlateNumber(vehiclePlateNumber)
                        .build().compose())
                .create();
    }

    NotificationMessage buildDeclinedSmsMessage(PassType passType, String mobile, String name, String vehiclePlateNumber) {
        return NotificationMessage.New()
                .from(smsFrom)
                .message(SMSNotificationTemplate.builder()
                        .name(name)
                        .passType(passType)
                        .vehiclePlateNumber(vehiclePlateNumber)
                        .build().compose())
                .create();
    }

    /**
     * Builds an email message to send.
     *
     * @param accessPassUrl link to qr code pdf download
     * @param email         email to send message to
     * @return built message for email service
     * @throws IOException     on error generating qr code
     * @throws WriterException on error generating qr code
     */
    NotificationMessage buildApprovedEmailMessage(PassType passType,
                                                  String accessPassUrl,
                                                  String accessPassReferenceId,
                                                  String email)
            throws IOException, WriterException, ParseException {

        byte[] generatedQrData = qrPdfService.generateQrPdf(accessPassReferenceId);

        return NotificationMessage.New()
                .from(mailFrom)
                .to(email)
                // todo prolly need to move this to an actual email templatr - thymeleaf? velocity? etc...
                .message(
                        EmailNotificationTemplate.builder()
                                .passType(passType)
                                .url(accessPassUrl)
                                .build()
                                .compose()
                )
                .title("RapidPass is APPROVED")
                // attach QR code PDF
                .addAttachment("rapidpass-qr.pdf", "application/pdf", generatedQrData)
                .create();
    }

    NotificationMessage buildDeclinedEmailMessage(PassType passType, String email, String reason) {
        return NotificationMessage.New()
                .from(mailFrom)
                .to(email)
                .message(EmailNotificationTemplate.builder()
                        .passType(passType)
                        .reason(reason).build().compose())
                .create()
                ;
    }


}
