package ph.devcon.rapidpass.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ph.devcon.rapidpass.entities.AccessPass;
import ph.devcon.rapidpass.entities.Registrant;
import ph.devcon.rapidpass.entities.Registrar;
import ph.devcon.rapidpass.enums.AccessPassStatus;
import ph.devcon.rapidpass.models.RapidPass;
import ph.devcon.rapidpass.models.RapidPassRequest;
import ph.devcon.rapidpass.repositories.AccessPassRepository;
import ph.devcon.rapidpass.repositories.RegistrantRepository;
import ph.devcon.rapidpass.repositories.RegistryRepository;
import ph.devcon.rapidpass.repositories.ScannerDeviceRepository;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Collections;

import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.anyString;
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

    @Mock
    AccessPassNotifierService mockAccessPassNotifierService;

    @Mock
    ScannerDeviceRepository mockScannerDeviceRepository;

    private OffsetDateTime now;

    @BeforeEach
    void setUp() {
        instance = new RegistryService(mockRegistryRepository, mockRegistrantRepository, mockAccessPassRepository,
                mockAccessPassNotifierService, mockScannerDeviceRepository);
        now = OffsetDateTime.now();
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
                .status(AccessPassStatus.PENDING.toString())
                .remarks(TEST_INDIVIDUAL_REQUEST.getRemarks())
                .referenceID(TEST_INDIVIDUAL_REQUEST.getIdentifierNumber())
                .build();

        // mock registry always returns a registry
        final Registrar mockRegistrar = new Registrar();
        mockRegistrar.setId(1);

        when(mockAccessPassRepository.findAllByReferenceIDOrderByValidToDesc(anyString())).thenReturn(Collections.emptyList());

        // no existing user
        when(mockRegistrantRepository.findByReferenceId(anyString())).thenReturn(null);

        // mock save and flush
        when(mockRegistrantRepository.save(ArgumentMatchers.any()))
                .thenReturn(Registrant.builder().registrarId(0)
                        .firstName("Jonas").build());

        when(mockAccessPassRepository.saveAndFlush(ArgumentMatchers.any())).thenReturn(samplePendingAccessPass);

        final RapidPass rapidPass = instance.newRequestPass(TEST_INDIVIDUAL_REQUEST);

        assertThat(rapidPass, is(not(nullValue())));

        // for no existing pass, new registrant, expect the ff:
        // save registrant
        verify(mockRegistrantRepository, times(1))
                .save(ArgumentMatchers.any(Registrant.class));
        // save and flush access pass
        verify(mockAccessPassRepository, times(1))
                .saveAndFlush(ArgumentMatchers.any(AccessPass.class));
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
                .status(AccessPassStatus.PENDING.toString())
                .remarks(TEST_INDIVIDUAL_REQUEST.getRemarks())
                .referenceID(TEST_INDIVIDUAL_REQUEST.getIdentifierNumber())
                .validTo(OffsetDateTime.ofInstant(FIVE_DAYS_FROM_NOW.toInstant(), ZoneId.systemDefault()))
                .build();

        // mock registry always returns a registry
        final Registrar mockRegistrar = new Registrar();
        mockRegistrar.setId(1);
        // repository returns an access pass!
        when(mockAccessPassRepository.findAllByReferenceIDOrderByValidToDesc(anyString())).thenReturn(singletonList(samplePendingAccessPass));


        try {
            this.instance.newRequestPass(TEST_INDIVIDUAL_REQUEST);
            fail("should throw exception");
        } catch (Exception e) {
            e.printStackTrace();
            assertThat(e.getMessage(), containsString("An existing PENDING/APPROVED RapidPass already exists"));
        }
    }

    @Test
    @Disabled
    void generateControlCode() {
        // Currently ignoring this test, because Alistair still hasn't updated the commons to 0.2.0

        String controlCode = RegistryService.ControlCodeGenerator.generate("***REMOVED***", 14);
        assertThat("Generated control code matches", controlCode.equals("06SV72CA"));
    }

    @Test
    void updateAccessPass_APPROVED() throws RegistryService.UpdateAccessPassException {
        final AccessPass approvedAccessPass = AccessPass.builder()
                .id(123456)
                .referenceID("ref-id")
                .status("APPROVED")
                .passType("INDIVIDUAL")
                .build();

        final AccessPass pendingAccessPass = AccessPass.builder()
                .status("PENDING")
                .id(123456)
                .build();


        when(mockAccessPassRepository.findAllByReferenceIDOrderByValidToDesc("ref-id"))
                .thenReturn(singletonList(pendingAccessPass));
        when(mockAccessPassRepository.saveAndFlush(ArgumentMatchers.any(AccessPass.class))).thenReturn(approvedAccessPass);
        final RapidPass approved = instance.updateAccessPass("ref-id", RapidPass.builder().status("APPROVED").build());

        assertThat(approved, is(notNullValue()));
        assertThat(approved.getStatus(), is("APPROVED"));

        verify(mockAccessPassRepository, times(2)).saveAndFlush(ArgumentMatchers.any(AccessPass.class));
    }

    @Test
    void updateAccessPass_DENIED() throws RegistryService.UpdateAccessPassException {
        final AccessPass approvedAccessPass = AccessPass.builder()
                .id(123456)
                .referenceID("ref-id")
                .status("DECLINED")
                .passType("INDIVIDUAL")
                .build();

        final AccessPass pendingAccessPass = AccessPass.builder()
                .status("PENDING")
                .id(123456)
                .build();


        when(mockAccessPassRepository.findAllByReferenceIDOrderByValidToDesc("ref-id"))
                .thenReturn(singletonList(pendingAccessPass));
        when(mockAccessPassRepository.saveAndFlush(ArgumentMatchers.any(AccessPass.class))).thenReturn(approvedAccessPass);
        final RapidPass approved = instance.updateAccessPass("ref-id", RapidPass.builder().status("DECLINED").build());

        assertThat(approved, is(notNullValue()));
        assertThat(approved.getStatus(), is("DECLINED"));

        verify(mockAccessPassRepository).saveAndFlush(ArgumentMatchers.any(AccessPass.class));
    }
}
