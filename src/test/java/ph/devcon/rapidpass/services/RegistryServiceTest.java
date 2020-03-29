package ph.devcon.rapidpass.services;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.google.zxing.WriterException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ph.devcon.rapidpass.entities.AccessPass;
import ph.devcon.rapidpass.entities.Registrant;
import ph.devcon.rapidpass.entities.Registrar;
import ph.devcon.rapidpass.enums.RequestStatus;
import ph.devcon.rapidpass.models.RapidPass;
import ph.devcon.rapidpass.models.RapidPassRequest;
import ph.devcon.rapidpass.repositories.AccessPassRepository;
import ph.devcon.rapidpass.repositories.RegistrantRepository;
import ph.devcon.rapidpass.repositories.RegistryRepository;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static ph.devcon.rapidpass.enums.PassType.INDIVIDUAL;
import static ph.devcon.rapidpass.enums.PassType.VEHICLE;

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

    public static final RapidPassRequest TEST_INDIVIDUAL_REQUEST =
            RapidPassRequest.builder()
                    .passType(INDIVIDUAL)
                    .firstName("Jonas")
                    .lastName("Espelita")
                    .identifierNumber("0915999999")
                    .mobileNumber("0915999999")
                    .email("jonas.was.here@gmail.com")
                    .destCity("Somewhere in the PH")
                    .company("DEVCON")
                    .aporType("MO")
                    .remarks("This is a test for INDIVIDUAL REQUEST")
                    .build();

    public static final RapidPassRequest TEST_VEHICLE_REQUEST = RapidPassRequest.builder()
            .passType(VEHICLE)
            .identifierNumber("ABCD 1234")
            .mobileNumber("0915999999")
            .email("jonas.was.here@gmail.com")
            .destCity("Somewhere in the PH")
            .company("DEVCON")
            .aporType("M")
            .remarks("This is a test for VEHICLE REQUEST").build();

    @Test
    void newRequestPass_NEW_PASS_NEW_REGISTRANT() {

        final AccessPass samplePendingAccessPass = AccessPass.builder()
                .passType(TEST_INDIVIDUAL_REQUEST.getPassType().toString())
                .destinationCity(TEST_INDIVIDUAL_REQUEST.getDestCity())
                .company(TEST_INDIVIDUAL_REQUEST.getCompany())
                .aporType(TEST_INDIVIDUAL_REQUEST.getAporType())
                .status(RequestStatus.PENDING.toString())
                .remarks(TEST_INDIVIDUAL_REQUEST.getRemarks())
                .referenceID(TEST_INDIVIDUAL_REQUEST.getIdentifierNumber())
                .build();

        // mock registry always returns a registry
        final Registrar mockRegistrar = new Registrar();
        mockRegistrar.setId(1);
        when(mockRegistryRepository.findById(anyInt())).thenReturn(Optional.of(mockRegistrar));

        when(mockAccessPassRepository.findAllByReferenceIDOrderByValidToDesc(anyString())).thenReturn(Collections.emptyList());

        // no existing user
        when(mockRegistrantRepository.findByReferenceId(anyString())).thenReturn(null);

        // mock save and flush
        when(mockRegistrantRepository.save(ArgumentMatchers.any()))
                .thenReturn(Registrant.builder().registrarId(mockRegistrar)
                        .firstName("Jonas").build());

        when(mockAccessPassRepository.saveAndFlush(ArgumentMatchers.any())).thenReturn(samplePendingAccessPass);

        final RapidPass rapidPass = instance.newRequestPass(TEST_INDIVIDUAL_REQUEST);

        assertThat(rapidPass, is(not(nullValue())));

        // for no existing pass, new registrant, expect the ff:
        // save registrant
        verify(mockRegistrantRepository, times(1))
                .save(any(Registrant.class));
        // save and flush access pass
        verify(mockAccessPassRepository, times(1))
                .saveAndFlush(any(AccessPass.class));
    }

    @Test
    void newRequestPass_EXISTING_PASS() {

        final Calendar FIVE_DAYS_FROM_NOW = Calendar.getInstance();
        FIVE_DAYS_FROM_NOW.add(Calendar.DAY_OF_MONTH, 5);
        final AccessPass samplePendingAccessPass = AccessPass.builder()
                .passType(TEST_INDIVIDUAL_REQUEST.getPassType().toString())
                .destinationCity(TEST_INDIVIDUAL_REQUEST.getDestCity())
                .company(TEST_INDIVIDUAL_REQUEST.getCompany())
                .aporType(TEST_INDIVIDUAL_REQUEST.getAporType())
                .status(RequestStatus.PENDING.toString())
                .remarks(TEST_INDIVIDUAL_REQUEST.getRemarks())
                .referenceID(TEST_INDIVIDUAL_REQUEST.getIdentifierNumber())
                .validTo(OffsetDateTime.ofInstant(FIVE_DAYS_FROM_NOW.toInstant(), ZoneId.systemDefault()))
                .build();

        // mock registry always returns a registry
        final Registrar mockRegistrar = new Registrar();
        mockRegistrar.setId(1);
        // repository returns an access pass!
        when(mockAccessPassRepository.findAllByReferenceIDOrderByValidToDesc(anyString())).thenReturn(Collections.singletonList(samplePendingAccessPass));


        try {
            this.instance.newRequestPass(TEST_INDIVIDUAL_REQUEST);
            fail("should throw exception");
        } catch (Exception e) {
            e.printStackTrace();
            assertThat(e.getMessage(), containsString("An existing PENDING/APPROVED RapidPass already exists"));
        }
    }
}
