package ph.devcon.rapidpass.services;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.google.zxing.WriterException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import ph.devcon.dctx.rapidpass.model.ControlCode;
import ph.devcon.rapidpass.entities.AccessPass;
import ph.devcon.rapidpass.repositories.AccessPassRepository;

import java.io.IOException;
import java.text.ParseException;
import java.time.OffsetDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

class QrPdfServiceTest {
    static final OffsetDateTime NOW = OffsetDateTime.now();

    QrPdfService instance;

    @Mock
    AccessPassRepository accessPassRepository;

    @BeforeEach
    void setUp() {
        instance = new QrPdfService(new QrGeneratorServiceImpl(new JsonMapper()), accessPassRepository);

    }

    @Test
    void generateQrPdf_INDIVIDUAL() throws IOException, WriterException, ParseException, NullPointerException {

        String controlCode = ControlCode.encode(38);

        final byte[] bytes = instance.generateQrPdf(AccessPass.builder().
                status("APPROVED")
                .passType("INDIVIDUAL")
                .controlCode(controlCode)
                .idType("Driver's License")
                .identifierNumber("N0124734213")
                .name("Darren Karl A. Sapalo")
                .company("DevCon.ph")
                .aporType("AB")
                .validFrom(NOW)
                .validTo(NOW.plusDays(1))
                .build());

        assertThat(bytes.length, is(greaterThan(0)));
    }

    @Test
    void generateQrPdf_VEHICLE() throws IOException, WriterException, ParseException, NullPointerException {

        String controlCode = ControlCode.encode(38);

        final byte[] bytes = instance.generateQrPdf(AccessPass.builder().
                status("APPROVED")
                .passType("VEHICLE")
                .controlCode(controlCode)
                .idType("Plate Number")
                .identifierNumber("ABC 123")
                .name("ABC 123")
                .aporType("AB")
                .company("DevCon.ph")
                .validFrom(NOW)
                .validTo(NOW.plusDays(1))
                .build());

        assertThat(bytes.length, is(greaterThan(0)));
    }

    @Test
    void generateQrPdf_failMissingStatus() {

        assertThrows(IllegalArgumentException.class, () -> {

            // Causes the error
            String nullStatus = null;

            instance.generateQrPdf(AccessPass.builder().
                    status(nullStatus)
                    .passType("VEHICLE")
                    .controlCode("123456")
                    .idType("Plate Number")
                    .identifierNumber("ABC 123")
                    .name("ABC 123")
                    .aporType("AB")
                    .company("DevCon.ph")
                    .validFrom(NOW)
                    .validTo(NOW.plusDays(1))
                    .build());
        });
    }

    @Test
    void generateQrPdf_failNotYetApproved() {

        assertThrows(IllegalArgumentException.class, () -> {

            // Causes the error
            String emptyStringStatus = "";

            instance.generateQrPdf(AccessPass.builder().
                    status(emptyStringStatus)
                    .passType("VEHICLE")
                    .controlCode("123456")
                    .idType("Plate Number")
                    .identifierNumber("ABC 123")
                    .name("ABC 123")
                    .aporType("AB")
                    .company("DevCon.ph")
                    .validFrom(NOW)
                    .validTo(NOW.plusDays(1))
                    .build());
        });
    }

    @Test
    void generateQrPdf_failNoPassType() {

        assertThrows(IllegalArgumentException.class, () -> {


            instance.generateQrPdf(
                    AccessPass.builder().
                            status("APPROVED")
                            .passType("")
                            .controlCode("123456")
                            .idType("Plate Number")
                            .identifierNumber("ABC 123")
                            .name("ABC 123")
                            .aporType("AB")
                            .company("DevCon.ph")
                            .validFrom(NOW)
                            .validTo(NOW.plusDays(1))
                            .build());
        });


        assertThrows(IllegalArgumentException.class, () -> {

            instance.generateQrPdf(AccessPass.builder().
                    status("APPROVED")
                    .passType(null)
                    .controlCode("123456")
                    .idType("Plate Number")
                    .identifierNumber("ABC 123")
                    .name("ABC 123")
                    .aporType("AB")
                    .company("DevCon.ph")
                    .validFrom(NOW)
                    .validTo(NOW.plusDays(1))
                    .build());
        });
    }

    @Test
    void generateQrPdf_failInvalidPassType() {

        assertThrows(IllegalArgumentException.class, () -> {

            String SOME_INVALID_PASS_TYPE = "INVALID_PASS_TYPE";


            instance.generateQrPdf(AccessPass.builder().
                    status("APPROVED")
                    .passType(SOME_INVALID_PASS_TYPE)
                    .controlCode("123456")
                    .idType("Plate Number")
                    .identifierNumber("ABC 123")
                    .name("ABC 123")
                    .aporType("AB")
                    .company("DevCon.ph")
                    .validFrom(NOW)
                    .validTo(NOW.plusDays(1))
                    .build());
        });
    }


    @Test
    void generateQrPdf_failMissingControlCode() {

        assertThrows(IllegalArgumentException.class, () -> {
            String INVALID_CONTROL_CODE = "";

            instance.generateQrPdf(AccessPass.builder().
                    status("APPROVED")
                    .passType("INDIVIDUAL")
                    .controlCode(INVALID_CONTROL_CODE)
                    .idType("Plate Number")
                    .identifierNumber("ABC 123")
                    .name("ABC 123")
                    .aporType("AB")
                    .company("DevCon.ph")
                    .validFrom(NOW)
                    .validTo(NOW.plusDays(1))
                    .build());
        });

        assertThrows(IllegalArgumentException.class, () -> {

            String INVALID_CONTROL_CODE = null;

            instance.generateQrPdf(AccessPass.builder().
                    status("APPROVED")
                    .passType("INDIVIDUAL")
                    .controlCode(INVALID_CONTROL_CODE)
                    .idType("Plate Number")
                    .identifierNumber("ABC 123")
                    .name("ABC 123")
                    .aporType("AB")
                    .company("DevCon.ph")
                    .validFrom(NOW)
                    .validTo(NOW.plusDays(1))
                    .build());
        });
    }

    @Test
    void generateQrPdf_failMissingName() {

        assertThrows(IllegalArgumentException.class, () -> {

            String INVALID_ARGUMENT = "";


            instance.generateQrPdf(AccessPass.builder().
                    status("APPROVED")
                    .passType("INDIVIDUAL")
                    .controlCode("12345")
                    .idType("Plate Number")
                    .identifierNumber("ABC 123")
                    .name(INVALID_ARGUMENT)
                    .aporType("AB")
                    .company("DevCon.ph")
                    .validFrom(NOW)
                    .validTo(NOW.plusDays(1))
                    .build());
        });

        assertThrows(IllegalArgumentException.class, () -> {

            String INVALID_ARGUMENT = null;

            instance.generateQrPdf(AccessPass.builder().
                    status("APPROVED")
                    .passType("INDIVIDUAL")
                    .controlCode("12345")
                    .idType("Plate Number")
                    .identifierNumber("ABC 123")
                    .name(INVALID_ARGUMENT)
                    .aporType("AB")
                    .company("DevCon.ph")
                    .validFrom(NOW)
                    .validTo(NOW.plusDays(1))
                    .build());
        });

    }

    @Test
    void generateQrPdf_failMissingAporType() {

        assertThrows(IllegalArgumentException.class, () -> {

            String INVALID_ARGUMENT = "";

            instance.generateQrPdf(AccessPass.builder().
                    status("APPROVED")
                    .passType("INDIVIDUAL")
                    .controlCode("12345")
                    .idType("Plate Number")
                    .identifierNumber("ABC 123")
                    .name("Darren was here")
                    .aporType(INVALID_ARGUMENT)
                    .company("DevCon.ph")
                    .validFrom(NOW)
                    .validTo(NOW.plusDays(1))
                    .build());
        });

        assertThrows(IllegalArgumentException.class, () -> {

            String INVALID_ARGUMENT = null;

            instance.generateQrPdf(AccessPass.builder().
                    status("APPROVED")
                    .passType("INDIVIDUAL")
                    .controlCode("12345")
                    .idType("Plate Number")
                    .identifierNumber("ABC 123")
                    .name("Darren was here")
                    .aporType(INVALID_ARGUMENT)
                    .company("DevCon.ph")
                    .validFrom(NOW)
                    .validTo(NOW.plusDays(1))
                    .build());
        });
    }

    @Test
    void generateQrPdf_failMissingCompany() {

        assertThrows(IllegalArgumentException.class, () -> {

            String INVALID_ARGUMENT = "";

            instance.generateQrPdf(AccessPass.builder().
                    status("APPROVED")
                    .passType("INDIVIDUAL")
                    .controlCode("12345")
                    .idType("Plate Number")
                    .identifierNumber("ABC 123")
                    .name("Darren was here")
                    .aporType("AB")
                    .company(INVALID_ARGUMENT)
                    .validFrom(NOW)
                    .validTo(NOW.plusDays(1))
                    .build());
        });

        assertThrows(IllegalArgumentException.class, () -> {

            String INVALID_ARGUMENT = null;


            instance.generateQrPdf(AccessPass.builder().
                    status("APPROVED")
                    .passType("INDIVIDUAL")
                    .controlCode("12345")
                    .idType("Plate Number")
                    .identifierNumber("ABC 123")
                    .name("Darren was here")
                    .aporType("AB")
                    .company(INVALID_ARGUMENT)
                    .validFrom(NOW)
                    .validTo(NOW.plusDays(1))
                    .build());
        });
    }

    @Test
    void generateQrPdf_failMissingDateValues() {

        assertThrows(IllegalArgumentException.class, () -> {

            OffsetDateTime INVALID_ARGUMENT = null;

            instance.generateQrPdf(AccessPass.builder().
                    status("APPROVED")
                    .passType("INDIVIDUAL")
                    .controlCode("12345")
                    .idType("Plate Number")
                    .identifierNumber("ABC 123")
                    .name("Darren was here")
                    .aporType("AB")
                    .company("DevCon.ph")
                    .validFrom(INVALID_ARGUMENT)
                    .validTo(NOW.plusDays(1))
                    .build());
        });

        assertThrows(IllegalArgumentException.class, () -> {

            OffsetDateTime INVALID_ARGUMENT = null;


            instance.generateQrPdf(AccessPass.builder().
                    status("APPROVED")
                    .passType("INDIVIDUAL")
                    .controlCode("12345")
                    .idType("Plate Number")
                    .identifierNumber("ABC 123")
                    .name("Darren was here")
                    .aporType("AB")
                    .company("DevCon.ph")
                    .validFrom(NOW)
                    .validTo(INVALID_ARGUMENT)
                    .build());
        });

    }

}