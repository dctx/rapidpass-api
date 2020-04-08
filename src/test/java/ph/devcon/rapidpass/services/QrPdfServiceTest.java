package ph.devcon.rapidpass.services;

import com.google.zxing.WriterException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ph.devcon.dctx.rapidpass.commons.HmacSha256;
import ph.devcon.dctx.rapidpass.commons.QrCodeSerializer;
import ph.devcon.dctx.rapidpass.commons.Signer;
import ph.devcon.dctx.rapidpass.model.ControlCode;
import ph.devcon.rapidpass.entities.AccessPass;
import ph.devcon.rapidpass.repositories.AccessPassRepository;
import ph.devcon.rapidpass.services.controlcode.ControlCodeService;
import ph.devcon.rapidpass.services.controlcode.ControlCodeServiceImpl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.time.OffsetDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

/**
 * TODO: This Qr Pdf Service should only be responsible for PDF generation.
 * TODO: Move Access Pass validation to a separate Validator class and do testing for validation there, not here.
 */
@ExtendWith(MockitoExtension.class)
@Slf4j
class QrPdfServiceTest {
    static final OffsetDateTime NOW = OffsetDateTime.now();

    QrPdfService instance;

    @Mock private AccessPassRepository accessPassRepository;

    @Mock private ControlCodeService controlCodeService;

    @Mock  private QrGeneratorServiceImpl qrGenService;

    private String encryptionKey = "***REMOVED***";
    private String signingKey = "***REMOVED***";
    ;

    @BeforeEach
    void setUp() {
        final byte[] encryptionKeyBytes = Hex.decode(this.encryptionKey);
        final byte[] signingKeyBytes = Hex.decode(this.signingKey);
        final QrCodeSerializer qrCodeSerializer = new QrCodeSerializer(encryptionKeyBytes);
        final Signer signer = HmacSha256.signer(signingKeyBytes);

        qrGenService = new QrGeneratorServiceImpl(qrCodeSerializer, signer);

        controlCodeService = Mockito.mock(ControlCodeService.class);

        accessPassRepository = Mockito.mock(AccessPassRepository.class);

        instance = new QrPdfService(qrGenService, accessPassRepository, controlCodeService);

    }

    private static void writeBytesForVisualInspection(byte[] bytes) throws IOException {
        assertThat(bytes.length, is(greaterThan(0)));

        final File test = File.createTempFile("test", ".pdf");
        final FileOutputStream fileOutputStream = new FileOutputStream(test);
        IOUtils.write(bytes, fileOutputStream);
        log.debug("wrote pdf at {}", test.getAbsolutePath());
    }

    @Test
    void generateQrPdf_INDIVIDUAL() throws IOException, WriterException, ParseException, NullPointerException {
        instance = new QrPdfService(qrGenService, accessPassRepository, controlCodeService);

        String controlCode = ControlCode.encode(38);

        AccessPass accessPass = AccessPass.builder()
                .id(38)
                .status("APPROVED")
                .referenceID("09171234567")
                .passType("INDIVIDUAL")
                .controlCode(controlCode)
                .idType("Driver's License")
                .identifierNumber("N0124734213")
                .name("Darren Karl A. Sapalo")
                .company("DevCon.ph")
                .aporType("AB")
                .validFrom(NOW)
                .validTo(NOW.plusDays(1))
                .build();

        when(controlCodeService.findAccessPassByControlCode(anyString()))
                .thenReturn(accessPass);

        when(controlCodeService.encode(anyInt())).thenReturn(accessPass.getControlCode());

//        when(this.qrGenService.generateQr(any())).thenReturn(new byte[]{ 0, 1, 0, 1, 0 });

        final byte[] bytes = ((ByteArrayOutputStream) instance.generateQrPdf(accessPass.getControlCode())).toByteArray();


        assertThat(bytes.length, is(greaterThan(0)));

        // save bytes to file for visual inspection
        writeBytesForVisualInspection(bytes);
    }

    @Test
    void generateQrPdf_VEHICLE() throws IOException, WriterException, ParseException, NullPointerException {

        String controlCode = ControlCode.encode(38);

        AccessPass accessPass = AccessPass.builder()
                .id(38)
                .status("APPROVED")
                .referenceID("09171234567")
                .passType("VEHICLE")
                .controlCode(controlCode)
                .idType("Plate Number")
                .identifierNumber("ABC 123")
                .plateNumber("ABC 123")
                .name("ABC 123")
                .aporType("AB")
                .company("DevCon.ph")
                .validFrom(NOW)
                .validTo(NOW.plusDays(1))
                .build();

        when(controlCodeService.findAccessPassByControlCode(any()))
                .thenReturn(accessPass);

        when(controlCodeService.encode(anyInt()))
                .thenReturn(controlCode);

        final byte[] bytes = ((ByteArrayOutputStream) instance.generateQrPdf(accessPass.getControlCode())).toByteArray();
        writeBytesForVisualInspection(bytes);

        assertThat(bytes.length, is(greaterThan(0)));
    }

    @Test
    void generateQrPdf_failMissingStatus() {

        assertThrows(IllegalArgumentException.class, () -> {

            // Causes the error
            String nullStatus = null;

            AccessPass accessPass = AccessPass.builder().
                    status(nullStatus)
                    .referenceID("09171234567")
                    .passType("VEHICLE")
                    .controlCode("123456")
                    .idType("Plate Number")
                    .identifierNumber("ABC 123")
                    .name("ABC 123")
                    .aporType("AB")
                    .company("DevCon.ph")
                    .validFrom(NOW)
                    .validTo(NOW.plusDays(1))
                    .build();

            instance.generateQrPdf(accessPass.getControlCode());
            fail("should throw exception");
        });
    }

    @Test
    void generateQrPdf_failNotYetApproved() {

        assertThrows(IllegalArgumentException.class, () -> {

            // Causes the error
            String emptyStringStatus = "";

            AccessPass accessPass = AccessPass.builder().
                    status(emptyStringStatus)
                    .referenceID("09171234567")
                    .passType("VEHICLE")
                    .controlCode("123456")
                    .idType("Plate Number")
                    .identifierNumber("ABC 123")
                    .name("ABC 123")
                    .aporType("AB")
                    .company("DevCon.ph")
                    .validFrom(NOW)
                    .validTo(NOW.plusDays(1))
                    .build();


            instance.generateQrPdf(accessPass.getControlCode());
            fail("should throw exception");
        });
    }

    @Test
    void generateQrPdf_failNoPassType() {

        assertThrows(IllegalArgumentException.class, () -> {


            AccessPass accessPass = AccessPass.builder().
                    status("APPROVED")
                    .referenceID("09171234567")
                    .passType("")
                    .controlCode("123456")
                    .idType("Plate Number")
                    .identifierNumber("ABC 123")
                    .name("ABC 123")
                    .aporType("AB")
                    .company("DevCon.ph")
                    .validFrom(NOW)
                    .validTo(NOW.plusDays(1))
                    .build();

            instance.generateQrPdf(accessPass.getControlCode());
        });


        assertThrows(IllegalArgumentException.class, () -> {

            AccessPass accessPass = AccessPass.builder().
                    status("APPROVED")
                    .referenceID("09171234567")
                    .passType(null)
                    .controlCode("123456")
                    .idType("Plate Number")
                    .identifierNumber("ABC 123")
                    .name("ABC 123")
                    .aporType("AB")
                    .company("DevCon.ph")
                    .validFrom(NOW)
                    .validTo(NOW.plusDays(1))
                    .build();

            instance.generateQrPdf(accessPass.getControlCode());
            fail("should throw exception");
        });
    }

    @Test
    void generateQrPdf_failInvalidPassType() {

        assertThrows(IllegalArgumentException.class, () -> {

            String SOME_INVALID_PASS_TYPE = "INVALID_PASS_TYPE";


            AccessPass accessPass = AccessPass.builder().
                    status("APPROVED")
                    .referenceID("09171234567")
                    .passType(SOME_INVALID_PASS_TYPE)
                    .controlCode("123456")
                    .idType("Plate Number")
                    .identifierNumber("ABC 123")
                    .name("ABC 123")
                    .aporType("AB")
                    .company("DevCon.ph")
                    .validFrom(NOW)
                    .validTo(NOW.plusDays(1))
                    .build();

            instance.generateQrPdf(accessPass.getControlCode());
            fail("should throw exception");
        });
    }


    @Test
    void generateQrPdf_failMissingControlCode() {

        assertThrows(IllegalArgumentException.class, () -> {
            String INVALID_CONTROL_CODE = "";

            AccessPass accessPass = AccessPass.builder().
                    status("APPROVED")
                    .referenceID("09171234567")
                    .passType("INDIVIDUAL")
                    .controlCode(INVALID_CONTROL_CODE)
                    .idType("Plate Number")
                    .identifierNumber("ABC 123")
                    .name("ABC 123")
                    .aporType("AB")
                    .company("DevCon.ph")
                    .validFrom(NOW)
                    .validTo(NOW.plusDays(1))
                    .build();

            instance.generateQrPdf(accessPass.getControlCode());
            fail("should throw exception");
        });

        assertThrows(IllegalArgumentException.class, () -> {

            String INVALID_CONTROL_CODE = null;

            AccessPass accessPass = AccessPass.builder().
                    status("APPROVED")
                    .referenceID("09171234567")
                    .passType("INDIVIDUAL")
                    .controlCode(INVALID_CONTROL_CODE)
                    .idType("Plate Number")
                    .identifierNumber("ABC 123")
                    .name("ABC 123")
                    .aporType("AB")
                    .company("DevCon.ph")
                    .validFrom(NOW)
                    .validTo(NOW.plusDays(1))
                    .build();

            instance.generateQrPdf(accessPass.getControlCode());
            fail("should throw exception");
        });
    }

    @Test
    void generateQrPdf_failMissingName() {

        assertThrows(IllegalArgumentException.class, () -> {

            String INVALID_ARGUMENT = "";


            AccessPass accessPass = AccessPass.builder().
                    status("APPROVED")
                    .referenceID("09171234567")
                    .passType("INDIVIDUAL")
                    .controlCode("12345")
                    .idType("Plate Number")
                    .identifierNumber("ABC 123")
                    .name(INVALID_ARGUMENT)
                    .aporType("AB")
                    .company("DevCon.ph")
                    .validFrom(NOW)
                    .validTo(NOW.plusDays(1))
                    .build();


            instance.generateQrPdf(accessPass.getControlCode());
            fail("should throw exception");
        });

        assertThrows(IllegalArgumentException.class, () -> {

            String INVALID_ARGUMENT = null;

            AccessPass accessPass = AccessPass.builder().
                    status("APPROVED")
                    .referenceID("09171234567")
                    .passType("INDIVIDUAL")
                    .controlCode("12345")
                    .idType("Plate Number")
                    .identifierNumber("ABC 123")
                    .name(INVALID_ARGUMENT)
                    .aporType("AB")
                    .company("DevCon.ph")
                    .validFrom(NOW)
                    .validTo(NOW.plusDays(1))
                    .build();


            instance.generateQrPdf(accessPass.getControlCode());
            fail("should throw exception");
        });

    }

    @Test
    void generateQrPdf_failMissingAporType() {

        assertThrows(IllegalArgumentException.class, () -> {

            String INVALID_ARGUMENT = "";

            AccessPass accessPass = AccessPass.builder().
                    status("APPROVED")
                    .referenceID("09171234567")
                    .passType("INDIVIDUAL")
                    .controlCode("12345")
                    .idType("Plate Number")
                    .identifierNumber("ABC 123")
                    .name("Darren was here")
                    .aporType(INVALID_ARGUMENT)
                    .company("DevCon.ph")
                    .validFrom(NOW)
                    .validTo(NOW.plusDays(1))
                    .build();

            instance.generateQrPdf(accessPass.getControlCode());
            fail("should throw exception");
        });

        assertThrows(IllegalArgumentException.class, () -> {

            String INVALID_ARGUMENT = null;

            AccessPass accessPass = AccessPass.builder().
                    status("APPROVED")
                    .referenceID("09171234567")
                    .passType("INDIVIDUAL")
                    .controlCode("12345")
                    .idType("Plate Number")
                    .identifierNumber("ABC 123")
                    .name("Darren was here")
                    .aporType(INVALID_ARGUMENT)
                    .company("DevCon.ph")
                    .validFrom(NOW)
                    .validTo(NOW.plusDays(1))
                    .build();
            instance.generateQrPdf(accessPass.getControlCode());
            fail("should throw exception");
        });
    }

    @Test
    void generateQrPdf_failMissingCompany() {

        assertThrows(IllegalArgumentException.class, () -> {

            String INVALID_ARGUMENT = "";

            AccessPass accessPass = AccessPass.builder().
                    status("APPROVED")
                    .referenceID("09171234567")
                    .passType("INDIVIDUAL")
                    .controlCode("12345")
                    .idType("Plate Number")
                    .identifierNumber("ABC 123")
                    .name("Darren was here")
                    .aporType("AB")
                    .company(INVALID_ARGUMENT)
                    .validFrom(NOW)
                    .validTo(NOW.plusDays(1))
                    .build();

            instance.generateQrPdf(accessPass.getControlCode());
            fail("should throw exception");
        });

        assertThrows(IllegalArgumentException.class, () -> {

            String INVALID_ARGUMENT = null;


            AccessPass accessPass = AccessPass.builder().
                    status("APPROVED")
                    .referenceID("09171234567")
                    .passType("INDIVIDUAL")
                    .controlCode("12345")
                    .idType("Plate Number")
                    .identifierNumber("ABC 123")
                    .name("Darren was here")
                    .aporType("AB")
                    .company(INVALID_ARGUMENT)
                    .validFrom(NOW)
                    .validTo(NOW.plusDays(1))
                    .build();

            instance.generateQrPdf(accessPass.getControlCode());
            fail("should throw exception");
        });
    }

    @Test
    void generateQrPdf_failMissingDateValues() {

        assertThrows(IllegalArgumentException.class, () -> {

            OffsetDateTime INVALID_ARGUMENT = null;

            AccessPass accessPass = AccessPass.builder().
                    status("APPROVED")
                    .referenceID("09171234567")
                    .passType("INDIVIDUAL")
                    .controlCode("12345")
                    .idType("Plate Number")
                    .identifierNumber("ABC 123")
                    .name("Darren was here")
                    .aporType("AB")
                    .company("DevCon.ph")
                    .validFrom(INVALID_ARGUMENT)
                    .validTo(NOW.plusDays(1))
                    .build();

            instance.generateQrPdf(accessPass.getControlCode());
            fail("should throw exception");
        });

        assertThrows(IllegalArgumentException.class, () -> {

            OffsetDateTime INVALID_ARGUMENT = null;


            AccessPass accessPass = AccessPass.builder().
                    status("APPROVED")
                    .referenceID("09171234567")
                    .passType("INDIVIDUAL")
                    .controlCode("12345")
                    .idType("Plate Number")
                    .identifierNumber("ABC 123")
                    .name("Darren was here")
                    .aporType("AB")
                    .company("DevCon.ph")
                    .validFrom(NOW)
                    .validTo(INVALID_ARGUMENT)
                    .build();

            instance.generateQrPdf(accessPass.getControlCode());
            fail("should throw exception");
        });

    }

}
