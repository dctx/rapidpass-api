package ph.devcon.rapidpass.services;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.google.zxing.WriterException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import ph.devcon.rapidpass.entities.AccessPass;
import ph.devcon.rapidpass.repositories.AccessPassRepository;
import ph.devcon.rapidpass.services.notifications.NotificationException;
import ph.devcon.rapidpass.services.notifications.NotificationService;
import ph.devcon.rapidpass.services.pdf.PdfGeneratorService;

import java.io.IOException;
import java.time.OffsetDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

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
            .build();

    AccessPassNotifierService instance;

    @Mock
    AccessPassRepository mockAccessPassRepo;

    QrGeneratorService qrGenerator = new QrGeneratorServiceImpl(new JsonMapper());

    @Mock
    NotificationService mockEmailService;

    @Mock
    NotificationService mockSmsService;

    @Mock
    PdfGeneratorService pdfGeneratorService;

    @BeforeEach
    void setUp() {
        instance = new AccessPassNotifierService(mockAccessPassRepo,
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

    }

    @Test
    void generateAccessPassUrl() {
        final String url = instance.generateAccessPassUrl("a-reference-id");
        assertThat(url, is("the-testing-grounds.com/api/v1/registry/qr-codes/a-reference-id"));
    }

    @Test
    void buildEmailMessage() {

    }
}