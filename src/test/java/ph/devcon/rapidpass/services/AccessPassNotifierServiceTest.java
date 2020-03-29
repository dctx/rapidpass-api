package ph.devcon.rapidpass.services;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.google.zxing.WriterException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ph.devcon.rapidpass.entities.AccessPass;
import ph.devcon.rapidpass.entities.Registrant;
import ph.devcon.rapidpass.repositories.AccessPassRepository;
import ph.devcon.rapidpass.services.notifications.NotificationException;
import ph.devcon.rapidpass.services.notifications.NotificationMessage;
import ph.devcon.rapidpass.services.notifications.NotificationService;
import ph.devcon.rapidpass.services.pdf.PdfGeneratorImpl;
import ph.devcon.rapidpass.services.pdf.PdfGeneratorService;

import javax.activation.DataSource;
import java.io.IOException;
import java.text.ParseException;
import java.time.OffsetDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccessPassNotifierServiceTest {

    public static final String TEST_MAILBOX = "test@my-mail.com";
    public static final String TEST_RP_URL = "the-testing-grounds.com";
    public static final String TEST_ACCESSPASS_REFID = "some-accesspass-ref-id";
    public static final OffsetDateTime NOW = OffsetDateTime.now();

    public static final AccessPass INDIVIDUAL_ACCESSPASS = AccessPass.builder().
            status("APPROVED")
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

    QrGeneratorService qrGenerator = new QrGeneratorServiceImpl(new JsonMapper());

    @Mock
    NotificationService mockEmailService;

    @Mock
    NotificationService mockSmsService;


    PdfGeneratorService pdfGeneratorService = new PdfGeneratorImpl();

    @BeforeEach
    void setUp() {
        instance = new AccessPassNotifierService(
                mockAccessPassRepo,
                qrGenerator,
                pdfGeneratorService,
                mockSmsService,
                mockEmailService);

        instance.setMailFrom(TEST_MAILBOX);
        instance.setRapidPassUrl(TEST_RP_URL);
    }

    @Test
    void pushNotifications_SUCCESS() throws NotificationException, IOException, WriterException {
        // arrange
        when(mockAccessPassRepo.findByReferenceID(eq(TEST_ACCESSPASS_REFID)))
                .thenReturn(INDIVIDUAL_ACCESSPASS);

        instance.pushApprovalNotifs(TEST_ACCESSPASS_REFID);

        // verify email and sms send will be called

        verify(mockSmsService, times(1)).send(any());
        verify(mockEmailService, times(1)).send(any());

    }

    @Test
    void generateAccessPassUrl() {
        final String url = instance.generateAccessPassUrl("a-reference-id");
        assertThat(url, is("the-testing-grounds.com/api/v1/registry/qr-codes/a-reference-id"));
    }

    @Test
    void buildEmailMessage() throws ParseException, IOException, WriterException {
        final String toAddress = "my-email@email.com";
        final String testPassLink = "a-test-url.com";
        final NotificationMessage notificationMessage =
                instance.buildEmailMessage(testPassLink,
                        INDIVIDUAL_ACCESSPASS,
                        toAddress);

        final DataSource attachement = notificationMessage.getAttachments().get("rapidpass-qr.pdf");
        assertThat(attachement.getContentType(), is("application/pdf"));

        assertThat(notificationMessage.getFrom(), is(TEST_MAILBOX));
        assertThat(notificationMessage.getTo(), is(toAddress));
        assertThat(notificationMessage.getMessage(), is("Your RapidPass is available here: " + testPassLink));
        assertThat(notificationMessage.getTitle(), is("RapidPass is APPROVED"));

        // you can visually inspect the PDF file generated, by looking for path in the logs
    }

    @Test
    void buildSmsMessage() {
        final String testPassLink = "a-test-url.com";
        final String testMobile = "09158977011";
        final NotificationMessage smsMessage = instance.buildSmsMessage(testPassLink, testMobile);

        assertThat(smsMessage.getFrom(), is("RAPIDPASS.PH"));
        assertThat(smsMessage.getTo(), is(testMobile));
        assertThat(smsMessage.getMessage(), is("Your RapidPass is available here: " + testPassLink));
    }
}