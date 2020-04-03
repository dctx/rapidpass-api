package ph.devcon.rapidpass.services;

import com.google.zxing.WriterException;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ph.devcon.dctx.rapidpass.model.QrCodeData;
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
                String controlCode = accessPass.getControlCode();
                String accessPassUrl = generateAccessPassUrl(controlCode);
                log.debug("pushing approval notifications for {}", controlCode);

                emailMessage = buildApprovedEmailMessage(
                        passType,
                        accessPassUrl,
                        controlCode,
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
                emailMessage = buildDeclinedEmailMessage(passType, email, accessPass.getName(), accessPass.getUpdates());
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
                log.debug("pushed approval notifications for {}", accessPass.getControlCode());
            } catch (Exception e) {
                // we want to continue despite any error in smsService
                log.error("Error sending SMS message to " + mobile, e);
            }
        }
    }

    /**
     * Generates the URL users can use to link to QR code PDF download.
     *
     * @param accessPassReferenceId referenceId of accessPass
     * @return generated URL
     */
    String generateAccessPassUrl(String accessPassReferenceId) {
        return rapidPassUrl + qrCodeEndpoint + accessPassReferenceId;
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
                .to(mobile)
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
     * @param passType      The type of pass, whether for vehicle or individual.
     * @param controlCode   The control code, which is needed for QR code generation, to be added to the PDF.
     * @param accessPassUrl The generated URL that will allow a user to download their QR Code as a PDF.
     * @param email         The recipient of the email.
     *
     * @return A {@link NotificationMessage} that holds necessary data for the email service.
     * @throws IOException see {@link QrGeneratorService#generateQr(QrCodeData)}
     * @throws WriterException see {@link QrGeneratorService#generateQr(QrCodeData)}
     */
    NotificationMessage buildApprovedEmailMessage(PassType passType,
                                                  String accessPassUrl,
                                                  String controlCode,
                                                  String email)
            throws IOException, WriterException, ParseException {

        byte[] generatedQrData = qrPdfService.generateQrPdf(controlCode);

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

    NotificationMessage buildDeclinedEmailMessage(PassType passType, String email, String name, String reason) {
        return NotificationMessage.New()
                .from(mailFrom)
                .to(email)
                .message(EmailNotificationTemplate.builder()
                        .name(name)
                        .passType(passType)
                        .reason(reason).build().compose())
                .create()
                ;
    }


}
