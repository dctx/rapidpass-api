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
import java.time.OffsetDateTime;
import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
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

    @BeforeEach
    void setUp() {
        instance = new RegistryService(mockRegistryRepository, mockRegistrantRepository, mockAccessPassRepository, qrGeneratorService);
    }

    @Test
    void generateQrPdf_INDIVIDUAL() throws IOException, WriterException {
    
        final OffsetDateTime now = OffsetDateTime.now();
        when(mockAccessPassRepository.findByReferenceID("12345"))
                .thenReturn(AccessPass.builder().
                        status("approved")
                        .passType("individual")
                        .controlCode("123456")
                        .identifierNumber("ABCD 123")
                        .aporType("AB")
                        .validFrom(now)
                        .validTo(now.plusDays(1))
                        .build());
        final byte[] bytes = instance.generateQrPdf("12345");

        assertThat(bytes.length, is(greaterThan(0)));
    }

    @Test
    void generateQrPdf_VEHICLE() throws IOException, WriterException {

        when(mockAccessPassRepository.findByReferenceID("12345"))
                .thenReturn(AccessPass.builder().
                        status("approved")
                        .passType("vehicle")
                        .controlCode("123456")
                        .identifierNumber("ABCD 123")
                        .aporType("AB")
                        .validFrom(OffsetDateTime.now())
                        .validTo(OffsetDateTime.now())
                        .build());
        final byte[] bytes = instance.generateQrPdf("12345");

        assertThat(bytes.length, is(greaterThan(0)));

    }
}
