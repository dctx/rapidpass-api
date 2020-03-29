package ph.devcon.rapidpass.services;

import com.google.zxing.WriterException;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ph.devcon.rapidpass.entities.AccessPass;
import ph.devcon.rapidpass.repositories.AccessPassRepository;
import ph.devcon.rapidpass.services.notifications.NotificationException;
import ph.devcon.rapidpass.services.notifications.NotificationMessage;
import ph.devcon.rapidpass.services.notifications.NotificationService;
import ph.devcon.rapidpass.services.pdf.PdfGeneratorService;

import java.io.IOException;

/**
 * The {@link AccessPassNotifierService} service is responsible for sending out sms and emails for approved access passes.
 *
 * @author jonasespelita@gmail.com
 */
@Service
@RequiredArgsConstructor
@Setter
@Slf4j
public class AccessPassNotifierService {

    private final AccessPassRepository accessPassRepository;
    private final QrGeneratorService qrGeneratorService;
    private final PdfGeneratorService pdfGeneratorService;

    @Qualifier("email")
    private final NotificationService emailService;

    @Qualifier("sms")
    private final NotificationService smsService;

    // todo move to @ConfigurationProperties
    /**
     * The URL value notifier will use when inserting links to access pass. Defaults to http://rapidpass.ph.
     */
    @Value("${notifier.rapidPassUrl:http://rapidpass.ph}")
    private String rapidPassUrl = "http://rapidpass.ph";
    /**
     * The URL value notifier will use when inserting links to access pass. Defaults to localhost.
     */
    @Value("${notifier.mailFrom:rapidpass-dctx@devcon.ph}")
    private String mailFrom = "rapidpass-dctx@devcon.ph";

    /**
     * The configurable endpoint to send for users to download qr codes. Defaults to /api/v1/registry/qr-codes/{referenceId}
     */
    @Value("${notifier.qrCodeEndpoint:/api/v1/registry/qr-codes/}")
    private String qrCodeEndpoint = "/api/v1/registry/qr-codes/";

    /**
     * Pushes APPROVAL notifications to sms and email. Called on access pass APPROVAL.
     *
     * @param accessPassReferenceId referenceId of an Access Pass
     */
    public void pushApprovalNotifs(String accessPassReferenceId) throws IOException, WriterException, NotificationException {
        log.debug("pusing approval notifications for {}", accessPassReferenceId);
        // get access pass correspoinding to reference ID
        final AccessPass accessPass = accessPassRepository.findByReferenceID(accessPassReferenceId);

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
        if (!StringUtils.isEmpty(email)) emailService.send(
                buildEmailMessage(accessPassUrl, accessPass, email));

        final String mobile = accessPass.getRegistrantId().getMobile();
        // sms
        if (!StringUtils.isEmpty(mobile)) smsService.send(
                buildSmsMessage(accessPassUrl, mobile));

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
                .from("RAPIDPASS.PH")
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
    NotificationMessage buildEmailMessage(String accessPassUrl, AccessPass accessPass, String email)
            throws IOException, WriterException {

        // convert access pass to qr code data

        return NotificationMessage.New()
                .from(mailFrom)
                .to(email)
                // todo prolly need to move this to email templatr - thymeleaf? velocity? etc..
                // simple message for now
                .message("Your RapidPass is available here: " + accessPassUrl)
                .title("RapidPass is APPROVED")
                // attach QR code PDF
                .addAttachment("rapidpass-qr.pdf", "application/pdf",
                        null// todo
                )
                .create();
    }


}
