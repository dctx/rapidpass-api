package ph.devcon.rapidpass.services;

import com.google.zxing.WriterException;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ph.devcon.rapidpass.entities.AccessPass;
import ph.devcon.rapidpass.repositories.AccessPassRepository;
import ph.devcon.rapidpass.services.notifications.NotificationMessage;
import ph.devcon.rapidpass.services.notifications.NotificationService;

import javax.validation.constraints.Size;
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
     * Pushes APPROVAL notifications to sms and email. Called on access pass APPROVAL.
     *
     * @param accessPass an access pass
     */
    public void pushApprovalNotifs(AccessPass accessPass) {
        @Size(max = 30) final String accessPassReferenceId = accessPass.getReferenceID();
        log.debug("pushing approval notifications for {}", accessPassReferenceId);

        // pre checks

        // check pass exists
        if (accessPass == null)
            throw new IllegalArgumentException(String.format("No RapidPass Request found for %s", accessPassReferenceId));

        // check pass is APPROVED and NOT EXPIRED
        if (!AccessPass.isValid(accessPass))
            throw new IllegalArgumentException(String.format("The RapidPass %s is not a valid APPROVED pass.", accessPassReferenceId));


        // generate link for the access pass
        String accessPassUrl = generateAccessPassUrl(accessPassReferenceId);

        // create NotificationMessages to send out
        // email
        final String email = accessPass.getRegistrantId().getEmail();
        if (!StringUtils.isEmpty(email)) {
            try {
                emailService.send(
                        buildEmailMessage(accessPassUrl, accessPassReferenceId, email));
            } catch (Exception e) {
                // we want to continue despite any error in emailService
                log.error("Error sending email message to " + email, e);
            }
        }

        final String mobile = accessPass.getRegistrantId().getMobile();
        // sms
        if (!StringUtils.isEmpty(mobile)) {
            try {
                smsService.send(
                        buildSmsMessage(accessPassUrl, mobile));
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
    NotificationMessage buildSmsMessage(String accessPassUrl, String mobile) {
        return NotificationMessage.New()
                .from(smsFrom)
                .to(mobile)
                // todo get SMS format - make configurable
                .message("Your RapidPass is available here: " + accessPassUrl)
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
    NotificationMessage buildEmailMessage(String accessPassUrl,
                                          String accessPassReferenceId,
                                          String email)
            throws IOException, WriterException, ParseException {
        return NotificationMessage.New()
                .from(mailFrom)
                .to(email)
                // todo prolly need to move this to email templatr - thymeleaf? velocity? etc...
                // simple message for now
                .message("Your RapidPass is available here: " + accessPassUrl)
                .title("RapidPass is APPROVED")
                // attach QR code PDF
                .addAttachment("rapidpass-qr.pdf", "application/pdf",
                        qrPdfService.generateQrPdf(accessPassRepository.findByReferenceID(accessPassReferenceId)))
                .create();
    }


}
