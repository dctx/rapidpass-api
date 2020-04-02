package ph.devcon.rapidpass.services;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.google.common.collect.ImmutableList;
import com.google.zxing.WriterException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * TODO: This Qr Pdf Service should only be responsible for PDF generation.
 * TODO: Move Access Pass validation to a separate Validator class and do testing for validation there, not here.
 */
@ExtendWith(MockitoExtension.class)
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

        AccessPass accessPass = AccessPass.builder().
                status("APPROVED")
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

        when(accessPassRepository.findAllByReferenceIDOrderByValidToDesc(anyString()))
            .thenReturn(ImmutableList.of(accessPass));

        final byte[] bytes = instance.generateQrPdf(accessPass.getReferenceID());

        assertThat(bytes.length, is(greaterThan(0)));
    }

    @Test
    void generateQrPdf_VEHICLE() throws IOException, WriterException, ParseException, NullPointerException {

        String controlCode = ControlCode.encode(38);

        AccessPass accessPass = AccessPass.builder().
                status("APPROVED")
                .referenceID("09171234567")
                .passType("VEHICLE")
                .controlCode(controlCode)
                .idType("Plate Number")
                .identifierNumber("ABC 123")
                .name("ABC 123")
                .aporType("AB")
                .company("DevCon.ph")
                .validFrom(NOW)
                .validTo(NOW.plusDays(1))
                .build();

        when(accessPassRepository.findAllByReferenceIDOrderByValidToDesc(eq(accessPass.getReferenceID())))
                .thenReturn(ImmutableList.of(accessPass));

        final byte[] bytes = instance.generateQrPdf(accessPass.getReferenceID());

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


            when(accessPassRepository.findAllByReferenceIDOrderByValidToDesc(eq(accessPass.getReferenceID())))
                    .thenReturn(ImmutableList.of(accessPass));

            final byte[] bytes = instance.generateQrPdf(accessPass.getReferenceID());
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


            when(accessPassRepository.findAllByReferenceIDOrderByValidToDesc(eq(accessPass.getReferenceID())))
                    .thenReturn(ImmutableList.of(accessPass));

            final byte[] bytes = instance.generateQrPdf(accessPass.getReferenceID());
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


            when(accessPassRepository.findAllByReferenceIDOrderByValidToDesc(eq(accessPass.getReferenceID())))
                    .thenReturn(ImmutableList.of(accessPass));

            final byte[] bytes = instance.generateQrPdf(accessPass.getReferenceID());
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


            when(accessPassRepository.findAllByReferenceIDOrderByValidToDesc(eq(accessPass.getReferenceID())))
                    .thenReturn(ImmutableList.of(accessPass));

            final byte[] bytes = instance.generateQrPdf(accessPass.getReferenceID());
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


            when(accessPassRepository.findAllByReferenceIDOrderByValidToDesc(eq(accessPass.getReferenceID())))
                    .thenReturn(ImmutableList.of(accessPass));

            final byte[] bytes = instance.generateQrPdf(accessPass.getReferenceID());
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


            when(accessPassRepository.findAllByReferenceIDOrderByValidToDesc(eq(accessPass.getReferenceID())))
                    .thenReturn(ImmutableList.of(accessPass));

            final byte[] bytes = instance.generateQrPdf(accessPass.getReferenceID());
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


            when(accessPassRepository.findAllByReferenceIDOrderByValidToDesc(eq(accessPass.getReferenceID())))
                    .thenReturn(ImmutableList.of(accessPass));

            final byte[] bytes = instance.generateQrPdf(accessPass.getReferenceID());
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


            when(accessPassRepository.findAllByReferenceIDOrderByValidToDesc(eq(accessPass.getReferenceID())))
                    .thenReturn(ImmutableList.of(accessPass));

            final byte[] bytes = instance.generateQrPdf(accessPass.getReferenceID());
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


            when(accessPassRepository.findAllByReferenceIDOrderByValidToDesc(eq(accessPass.getReferenceID())))
                    .thenReturn(ImmutableList.of(accessPass));

            final byte[] bytes = instance.generateQrPdf(accessPass.getReferenceID());
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


            when(accessPassRepository.findAllByReferenceIDOrderByValidToDesc(eq(accessPass.getReferenceID())))
                    .thenReturn(ImmutableList.of(accessPass));

            final byte[] bytes = instance.generateQrPdf(accessPass.getReferenceID());
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


            when(accessPassRepository.findAllByReferenceIDOrderByValidToDesc(eq(accessPass.getReferenceID())))
                    .thenReturn(ImmutableList.of(accessPass));

            final byte[] bytes = instance.generateQrPdf(accessPass.getReferenceID());
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


            when(accessPassRepository.findAllByReferenceIDOrderByValidToDesc(eq(accessPass.getReferenceID())))
                    .thenReturn(ImmutableList.of(accessPass));

            final byte[] bytes = instance.generateQrPdf(accessPass.getReferenceID());
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


            when(accessPassRepository.findAllByReferenceIDOrderByValidToDesc(eq(accessPass.getReferenceID())))
                    .thenReturn(ImmutableList.of(accessPass));

            final byte[] bytes = instance.generateQrPdf(accessPass.getReferenceID());
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


            when(accessPassRepository.findAllByReferenceIDOrderByValidToDesc(eq(accessPass.getReferenceID())))
                    .thenReturn(ImmutableList.of(accessPass));

            final byte[] bytes = instance.generateQrPdf(accessPass.getReferenceID());
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


            when(accessPassRepository.findAllByReferenceIDOrderByValidToDesc(eq(accessPass.getReferenceID())))
                    .thenReturn(ImmutableList.of(accessPass));

            final byte[] bytes = instance.generateQrPdf(accessPass.getReferenceID());
        });

    }

}