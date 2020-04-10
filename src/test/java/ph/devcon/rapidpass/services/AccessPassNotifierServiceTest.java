package ph.devcon.rapidpass.services;

import com.google.zxing.WriterException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ph.devcon.rapidpass.entities.AccessPass;
import ph.devcon.rapidpass.entities.Registrant;
import ph.devcon.rapidpass.enums.PassType;
import ph.devcon.rapidpass.repositories.AccessPassRepository;
import ph.devcon.rapidpass.services.notifications.NotificationException;
import ph.devcon.rapidpass.services.notifications.NotificationMessage;
import ph.devcon.rapidpass.services.notifications.NotificationService;

import javax.activation.DataSource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.time.OffsetDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccessPassNotifierServiceTest {

    public static final String TEST_MAILBOX = "test@my-mail.com";
    public static final String TEST_RP_URL = "the-testing-grounds.com";
    public static final OffsetDateTime NOW = OffsetDateTime.now();

    public static final AccessPass INDIVIDUAL_ACCESSPASS = AccessPass.builder().
            status("APPROVED")
            .referenceID("a-reference-id")
            .passType("INDIVIDUAL")
            .controlCode("123456")
            .idType("Driver's License")
            .identifierNumber("N0124734213")
            .name("Darren Karl A. Sapalo")
            .company("DevCon.ph")
            .aporType("AB")
            .validFrom(NOW)
            .validTo(NOW.plusDays(1))
            .registrantId(Registrant.builder()
                    .email("my-email@email.com").
                            mobile("0915890883")
                    .build())
            .build();

    AccessPassNotifierService instance;

    @Mock
    AccessPassRepository mockAccessPassRepo;

    @Mock
    RegistryService mockRegistryService;

    @Mock
    QrPdfService mockQrPdfService;

    @Mock
    NotificationService mockEmailService;

    @Mock
    NotificationService mockSmsService;

    @BeforeEach
    void setUp() {
        instance = new AccessPassNotifierService(mockAccessPassRepo, mockQrPdfService, mockEmailService, mockSmsService);

        instance.setMailFrom(TEST_MAILBOX);
        instance.setRapidPassUrl(TEST_RP_URL);
    }

    @Test
    void pushNotifications_SUCCESS() throws NotificationException, ParseException, IOException, WriterException {

        final ByteArrayOutputStream value = new ByteArrayOutputStream();
        value.write(new byte[]{1, 0, 1, 0, 1});
        when(mockQrPdfService.generateQrPdf(anyString())).thenReturn(value);
        // mock send notifs to access pass
        instance.pushApprovalDeniedNotifs(INDIVIDUAL_ACCESSPASS);

        // verify email and sms send will be called

        verify(mockSmsService, times(1)).send(any());
        verify(mockEmailService, times(1)).send(any());

    }

    @Test
    void generateAccessPassUrl() {
        final String url = instance.generateAccessPassUrl("a-reference-id");
        assertThat(url, is("the-testing-grounds.com/qr/a-reference-id"));
    }

    @Test
    void buildEmailMessage() throws ParseException, IOException, WriterException {

        final ByteArrayOutputStream value = new ByteArrayOutputStream();
        value.write(new byte[]{1, 0, 1, 0, 1});
        when(mockQrPdfService.generateQrPdf(eq(INDIVIDUAL_ACCESSPASS.getReferenceID())))
                .thenReturn(value);
        final String toAddress = "my-email@email.com";
        final String testPassLink = "a-test-url.com";
        final NotificationMessage notificationMessage =
                instance.buildApprovedEmailMessage(PassType.INDIVIDUAL, testPassLink,
                        INDIVIDUAL_ACCESSPASS.getReferenceID(),
                        toAddress);

        final DataSource attachement = notificationMessage.getAttachments().get("rapidpass-qr.pdf");
        assertThat(attachement.getContentType(), is("application/pdf"));

        assertThat(notificationMessage.getFrom(), is(TEST_MAILBOX));
        assertThat(notificationMessage.getTo(), is(toAddress));
        assertThat(notificationMessage.getMessage(), is(
                "Your entry has been approved. We've sent you a list of instructions on how you can use your QR code along with a" +
                        " printable file that you can use at the checkpoint. " +
                        "You can download your QR code on RapidPass.ph by following this a-test-url.com. Please DO NOT share your QR code."));
        assertThat(notificationMessage.getTitle(), is("RapidPass is APPROVED"));

    }


    @Test
    void buildDeclinedEmailMessage() {
        final NotificationMessage declinedMessage = instance.buildDeclinedEmailMessage(PassType.INDIVIDUAL, "my-email.com", "Jonas", "blue balls");
        assertThat(declinedMessage.getMessage(), is("Your entry has been rejected due to blue balls. Please contact your approving agency for further inquiries."));
    }

    @Test
    void buildDeclinedSmsMessage() {
        final NotificationMessage declinedSms = instance.buildDeclinedSmsMessage(PassType.INDIVIDUAL, "091579123", "Jonas", "1234567234");
        assertThat(declinedSms.getMessage(), is("Your RapidPass has been rejected. Please contact your approving agency for further inquiries."));
    }

    @Test
    @Disabled
        // FIXME
    void buildApprovedSmsMessage() {
        final String testPassLink = "a-test-url.com";
        final String testMobile = "09158977011";
        final NotificationMessage smsMessage = instance.buildApprovedSmsMessage(PassType.INDIVIDUAL, testPassLink, testMobile, "JONAS", "12345", "ABCD 1234");

        assertThat(smsMessage.getFrom(), is("RAPIDPASS"));
        assertThat(smsMessage.getTo(), is(testMobile));
        assertThat(smsMessage.getMessage(), is("Hi, JONAS. Your RapidPass has been approved! Your RapidPass control number is 12345. " +
                "You can also download your QR code on RapidPass.ph by following this link: a-test-url.com"));
    }
}
