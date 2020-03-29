package ph.devcon.rapidpass.services;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.google.zxing.WriterException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ph.devcon.rapidpass.entities.AccessPass;
import ph.devcon.rapidpass.repositories.AccessPassRepository;
import ph.devcon.rapidpass.repositories.RegistrantRepository;
import ph.devcon.rapidpass.repositories.RegistryRepository;

import java.io.IOException;
import java.text.ParseException;
import java.time.OffsetDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistryServiceTest {

    RegistryService instance;

    @Mock
    RegistryRepository mockRegistryRepository;

    @Mock
    RegistrantRepository mockRegistrantRepository;

    @Mock
    AccessPassRepository mockAccessPassRepository;

    QrGeneratorService qrGeneratorService = new QrGeneratorServiceImpl(new JsonMapper());
    
    private OffsetDateTime now;

    @BeforeEach
    void setUp() {
        instance = new RegistryService(mockRegistryRepository, mockRegistrantRepository, mockAccessPassRepository, qrGeneratorService);
        now = OffsetDateTime.now();
    }

    @Test
    void generateQrPdf_INDIVIDUAL() throws IOException, WriterException, ParseException, NullPointerException {

        
        when(mockAccessPassRepository.findByReferenceID("12345"))
                .thenReturn(AccessPass.builder().
                        status("APPROVED")
                        .passType("INDIVIDUAL")
                        .controlCode("123456")
                        .idType("Driver's License")
                        .identifierNumber("N0124734213")
                        .name("Darren Karl A. Sapalo")
                        .company("DevCon.ph")
                        .aporType("AB")
                        .validFrom(now)
                        .validTo(now.plusDays(1))
                        .build());
        final byte[] bytes = instance.generateQrPdf("12345");

        assertThat(bytes.length, is(greaterThan(0)));
    }

    @Test
    void generateQrPdf_VEHICLE() throws IOException, WriterException, ParseException, NullPointerException {

        when(mockAccessPassRepository.findByReferenceID("12345"))
                .thenReturn(AccessPass.builder().
                        status("APPROVED")
                        .passType("VEHICLE")
                        .controlCode("123456")
                        .idType("Plate Number")
                        .identifierNumber("ABC 123")
                        .name("ABC 123")
                        .aporType("AB")
                        .company("DevCon.ph")
                        .validFrom(now)
                        .validTo(now.plusDays(1))
                        .build());
        final byte[] bytes = instance.generateQrPdf("12345");

        assertThat(bytes.length, is(greaterThan(0)));
    }

    @Test
    void generateQrPdf_failMissingStatus() {

        assertThrows(IllegalArgumentException.class, () -> {

            // Causes the error
            String nullStatus = null;

            when(mockAccessPassRepository.findByReferenceID("12345"))
                    .thenReturn(AccessPass.builder().
                            status(nullStatus)
                            .passType("VEHICLE")
                            .controlCode("123456")
                            .idType("Plate Number")
                            .identifierNumber("ABC 123")
                            .name("ABC 123")
                            .aporType("AB")
                            .company("DevCon.ph")
                            .validFrom(now)
                            .validTo(now.plusDays(1))
                            .build());

            instance.generateQrPdf("12345");
        });
    }

    @Test
    void generateQrPdf_failNotYetApproved() {

        assertThrows(IllegalArgumentException.class, () -> {

            // Causes the error
            String emptyStringStatus = "";

            when(mockAccessPassRepository.findByReferenceID("12345"))
                    .thenReturn(AccessPass.builder().
                            status(emptyStringStatus)
                            .passType("VEHICLE")
                            .controlCode("123456")
                            .idType("Plate Number")
                            .identifierNumber("ABC 123")
                            .name("ABC 123")
                            .aporType("AB")
                            .company("DevCon.ph")
                            .validFrom(now)
                            .validTo(now.plusDays(1))
                            .build());

            instance.generateQrPdf("12345");
        });
    }

    @Test
    void generateQrPdf_failNoPassType() {

        assertThrows(IllegalArgumentException.class, () -> {

            when(mockAccessPassRepository.findByReferenceID("12345"))
                    .thenReturn(AccessPass.builder().
                            status("APPROVED")
                            .passType("")
                            .controlCode("123456")
                            .idType("Plate Number")
                            .identifierNumber("ABC 123")
                            .name("ABC 123")
                            .aporType("AB")
                            .company("DevCon.ph")
                            .validFrom(now)
                            .validTo(now.plusDays(1))
                            .build());

            instance.generateQrPdf("12345");
        });


        assertThrows(IllegalArgumentException.class, () -> {

            when(mockAccessPassRepository.findByReferenceID("12345"))
                    .thenReturn(AccessPass.builder().
                            status("APPROVED")
                            .passType(null)
                            .controlCode("123456")
                            .idType("Plate Number")
                            .identifierNumber("ABC 123")
                            .name("ABC 123")
                            .aporType("AB")
                            .company("DevCon.ph")
                            .validFrom(now)
                            .validTo(now.plusDays(1))
                            .build());

            instance.generateQrPdf("12345");
        });
    }

    @Test
    void generateQrPdf_failInvalidPassType() {

        assertThrows(IllegalArgumentException.class, () -> {

            String SOME_INVALID_PASS_TYPE = "INVALID_PASS_TYPE";

            when(mockAccessPassRepository.findByReferenceID("12345"))
                    .thenReturn(AccessPass.builder().
                            status("APPROVED")
                            .passType(SOME_INVALID_PASS_TYPE)
                            .controlCode("123456")
                            .idType("Plate Number")
                            .identifierNumber("ABC 123")
                            .name("ABC 123")
                            .aporType("AB")
                            .company("DevCon.ph")
                            .validFrom(now)
                            .validTo(now.plusDays(1))
                            .build());

            instance.generateQrPdf("12345");
        });
    }


    @Test
    void generateQrPdf_failMissingControlCode() {

        assertThrows(IllegalArgumentException.class, () -> {

            String INVALID_CONTROL_CODE = "";

            when(mockAccessPassRepository.findByReferenceID("12345"))
                    .thenReturn(AccessPass.builder().
                            status("APPROVED")
                            .passType("INDIVIDUAL")
                            .controlCode(INVALID_CONTROL_CODE)
                            .idType("Plate Number")
                            .identifierNumber("ABC 123")
                            .name("ABC 123")
                            .aporType("AB")
                            .company("DevCon.ph")
                            .validFrom(now)
                            .validTo(now.plusDays(1))
                            .build());

            instance.generateQrPdf("12345");
        });

        assertThrows(IllegalArgumentException.class, () -> {

            String INVALID_CONTROL_CODE = null;

            when(mockAccessPassRepository.findByReferenceID("12345"))
                    .thenReturn(AccessPass.builder().
                            status("APPROVED")
                            .passType("INDIVIDUAL")
                            .controlCode(INVALID_CONTROL_CODE)
                            .idType("Plate Number")
                            .identifierNumber("ABC 123")
                            .name("ABC 123")
                            .aporType("AB")
                            .company("DevCon.ph")
                            .validFrom(now)
                            .validTo(now.plusDays(1))
                            .build());

            instance.generateQrPdf("12345");
        });
    }

    @Test
    void generateQrPdf_failMissingName() {

        assertThrows(IllegalArgumentException.class, () -> {

            String INVALID_ARGUMENT = "";

            when(mockAccessPassRepository.findByReferenceID("12345"))
                    .thenReturn(AccessPass.builder().
                            status("APPROVED")
                            .passType("INDIVIDUAL")
                            .controlCode("12345")
                            .idType("Plate Number")
                            .identifierNumber("ABC 123")
                            .name(INVALID_ARGUMENT)
                            .aporType("AB")
                            .company("DevCon.ph")
                            .validFrom(now)
                            .validTo(now.plusDays(1))
                            .build());

            instance.generateQrPdf("12345");
        });

        assertThrows(IllegalArgumentException.class, () -> {

            String INVALID_ARGUMENT = null;

            when(mockAccessPassRepository.findByReferenceID("12345"))
                    .thenReturn(AccessPass.builder().
                            status("APPROVED")
                            .passType("INDIVIDUAL")
                            .controlCode("12345")
                            .idType("Plate Number")
                            .identifierNumber("ABC 123")
                            .name(INVALID_ARGUMENT)
                            .aporType("AB")
                            .company("DevCon.ph")
                            .validFrom(now)
                            .validTo(now.plusDays(1))
                            .build());

            instance.generateQrPdf("12345");
        });

    }

    @Test
    void generateQrPdf_failMissingAporType() {

        assertThrows(IllegalArgumentException.class, () -> {

            String INVALID_ARGUMENT = "";

            when(mockAccessPassRepository.findByReferenceID("12345"))
                    .thenReturn(AccessPass.builder().
                            status("APPROVED")
                            .passType("INDIVIDUAL")
                            .controlCode("12345")
                            .idType("Plate Number")
                            .identifierNumber("ABC 123")
                            .name("Darren was here")
                            .aporType(INVALID_ARGUMENT)
                            .company("DevCon.ph")
                            .validFrom(now)
                            .validTo(now.plusDays(1))
                            .build());

            instance.generateQrPdf("12345");
        });

        assertThrows(IllegalArgumentException.class, () -> {

            String INVALID_ARGUMENT = null;

            when(mockAccessPassRepository.findByReferenceID("12345"))
                    .thenReturn(AccessPass.builder().
                            status("APPROVED")
                            .passType("INDIVIDUAL")
                            .controlCode("12345")
                            .idType("Plate Number")
                            .identifierNumber("ABC 123")
                            .name("Darren was here")
                            .aporType(INVALID_ARGUMENT)
                            .company("DevCon.ph")
                            .validFrom(now)
                            .validTo(now.plusDays(1))
                            .build());

            instance.generateQrPdf("12345");
        });
    }

    @Test
    void generateQrPdf_failMissingCompany() {

        assertThrows(IllegalArgumentException.class, () -> {

            String INVALID_ARGUMENT = "";

            when(mockAccessPassRepository.findByReferenceID("12345"))
                    .thenReturn(AccessPass.builder().
                            status("APPROVED")
                            .passType("INDIVIDUAL")
                            .controlCode("12345")
                            .idType("Plate Number")
                            .identifierNumber("ABC 123")
                            .name("Darren was here")
                            .aporType("AB")
                            .company(INVALID_ARGUMENT)
                            .validFrom(now)
                            .validTo(now.plusDays(1))
                            .build());

            instance.generateQrPdf("12345");
        });

        assertThrows(IllegalArgumentException.class, () -> {

            String INVALID_ARGUMENT = null;

            when(mockAccessPassRepository.findByReferenceID("12345"))
                    .thenReturn(AccessPass.builder().
                            status("APPROVED")
                            .passType("INDIVIDUAL")
                            .controlCode("12345")
                            .idType("Plate Number")
                            .identifierNumber("ABC 123")
                            .name("Darren was here")
                            .aporType("AB")
                            .company(INVALID_ARGUMENT)
                            .validFrom(now)
                            .validTo(now.plusDays(1))
                            .build());

            instance.generateQrPdf("12345");
        });
    }

    @Test
    void generateQrPdf_failMissingDateValues() {

        assertThrows(IllegalArgumentException.class, () -> {

            OffsetDateTime INVALID_ARGUMENT = null;

            when(mockAccessPassRepository.findByReferenceID("12345"))
                    .thenReturn(AccessPass.builder().
                            status("APPROVED")
                            .passType("INDIVIDUAL")
                            .controlCode("12345")
                            .idType("Plate Number")
                            .identifierNumber("ABC 123")
                            .name("Darren was here")
                            .aporType("AB")
                            .company("DevCon.ph")
                            .validFrom(INVALID_ARGUMENT)
                            .validTo(now.plusDays(1))
                            .build());

            instance.generateQrPdf("12345");
        });

        assertThrows(IllegalArgumentException.class, () -> {

            OffsetDateTime INVALID_ARGUMENT = null;

            when(mockAccessPassRepository.findByReferenceID("12345"))
                    .thenReturn(AccessPass.builder().
                            status("APPROVED")
                            .passType("INDIVIDUAL")
                            .controlCode("12345")
                            .idType("Plate Number")
                            .identifierNumber("ABC 123")
                            .name("Darren was here")
                            .aporType("AB")
                            .company("DevCon.ph")
                            .validFrom(now)
                            .validTo(INVALID_ARGUMENT)
                            .build());

            instance.generateQrPdf("12345");
        });

    }

}
