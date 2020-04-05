package ph.devcon.rapidpass.services;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ph.devcon.rapidpass.entities.*;
import ph.devcon.rapidpass.enums.AccessPassStatus;
import ph.devcon.rapidpass.models.RapidPass;
import ph.devcon.rapidpass.models.RapidPassRequest;
import ph.devcon.rapidpass.models.RapidPassStatus;
import ph.devcon.rapidpass.repositories.*;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Collections;

import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static ph.devcon.rapidpass.enums.PassType.INDIVIDUAL;
import static ph.devcon.rapidpass.enums.PassType.VEHICLE;

@ExtendWith(MockitoExtension.class)
class RegistryServiceTest {

    RegistryService instance;

    @Mock AuthService mockAuthService;

    @Mock RegistrarRepository mockRegistrarRepository;

    @Mock RegistryRepository mockRegistryRepository;

    @Mock RegistrarUserRepository mockRegistrarUserRepository;

    @Mock RegistrantRepository mockRegistrantRepository;

    @Mock AccessPassRepository mockAccessPassRepository;

    @Mock AccessPassNotifierService mockAccessPassNotifierService;

    @Mock ScannerDeviceRepository mockScannerDeviceRepository;

    @Mock LookupTableService lookupTableService;

    private OffsetDateTime now;

    @BeforeEach
    void setUp() {
        instance = new RegistryService(
                mockAuthService,
                lookupTableService,
                mockAccessPassNotifierService,
                mockRegistryRepository,
                mockRegistrarRepository,
                mockRegistrantRepository,
                mockAccessPassRepository,
                mockScannerDeviceRepository,
                mockRegistrarUserRepository
                );
        now = OffsetDateTime.now();
    }


    public static final RapidPassRequest TEST_INDIVIDUAL_REQUEST =
            RapidPassRequest.builder()
                    .passType(INDIVIDUAL)
                    .firstName("Jonas")
                    .lastName("Espelita")
                    .idType("COM")
                    .identifierNumber("0915999999")
                    .plateNumber("ABC4321")
                    .mobileNumber("0915999999")
                    .email("jonas.was.here@gmail.com")
                    .destCity("Somewhere in the PH")
                    .company("DEVCON")
                    .aporType("AG")
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

        final Registrant sampleRegistrant = Registrant.builder()
                .registrarId(0)
                .firstName(TEST_INDIVIDUAL_REQUEST.getFirstName())
                .lastName(TEST_INDIVIDUAL_REQUEST.getLastName())
                .referenceId(TEST_INDIVIDUAL_REQUEST.getIdentifierNumber())
                .email(TEST_INDIVIDUAL_REQUEST.getEmail())
                .mobile(TEST_INDIVIDUAL_REQUEST.getMobileNumber())
                .build();

        final AccessPass samplePendingAccessPass = AccessPass.builder()
                .passType(TEST_INDIVIDUAL_REQUEST.getPassType().toString())
                .destinationCity(TEST_INDIVIDUAL_REQUEST.getDestCity())
                .company(TEST_INDIVIDUAL_REQUEST.getCompany())
                .aporType(TEST_INDIVIDUAL_REQUEST.getAporType())
                .status(AccessPassStatus.PENDING.toString())
                .remarks(TEST_INDIVIDUAL_REQUEST.getRemarks())
                .referenceID(TEST_INDIVIDUAL_REQUEST.getIdentifierNumber())
                .registrantId(sampleRegistrant)
                .build();

        // mock registry always returns a registry
        final Registrar mockRegistrar = new Registrar();
        mockRegistrar.setId(0);

//        when(mockAccessPassRepository.findAllByReferenceIDOrderByValidToDesc(anyString())).thenReturn(Collections.emptyList());

        // no existing user
        when(mockRegistrantRepository.findByMobile(anyString())).thenReturn(null);

        // mock save and flush
        when(mockRegistrantRepository.save(ArgumentMatchers.any()))
                .thenReturn(Registrant.builder().registrarId(0)
                        .firstName("Jonas").build());

        when(mockAccessPassRepository.saveAndFlush(ArgumentMatchers.any())).thenReturn(samplePendingAccessPass);

        when(lookupTableService.getAporTypes()).thenReturn(
                Collections.unmodifiableList(Lists.newArrayList(
                        new LookupTable(new LookupTablePK("APOR", "AG")),
                        new LookupTable(new LookupTablePK("APOR", "BP")),
                        new LookupTable(new LookupTablePK("APOR", "CA"))
                ))
        );

        when(lookupTableService.getIndividualIdTypes()).thenReturn(
                Collections.unmodifiableList(Lists.newArrayList(
                        new LookupTable(new LookupTablePK("IDTYPE-IND", "LTO")),
                        new LookupTable(new LookupTablePK("IDTYPE-IND", "COM")),
                        new LookupTable(new LookupTablePK("IDTYPE-IND", "NBI"))
                ))
        );

        when(lookupTableService.getVehicleIdTypes()).thenReturn(
                Collections.unmodifiableList(Lists.newArrayList(
                        new LookupTable(new LookupTablePK("IDTYPE-VHC", "PLT")),
                        new LookupTable(new LookupTablePK("IDTYPE-VHC", "CND"))
                ))
        );

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
    void newRequestPass_throwErrorIfAPassAlreadyExists() {

        final Calendar FIVE_DAYS_FROM_NOW = Calendar.getInstance();
        FIVE_DAYS_FROM_NOW.add(Calendar.DAY_OF_MONTH, 5);

        when(lookupTableService.getAporTypes()).thenReturn(
                Collections.unmodifiableList(Lists.newArrayList(
                        new LookupTable(new LookupTablePK("APOR", "AG")),
                        new LookupTable(new LookupTablePK("APOR", "BP")),
                        new LookupTable(new LookupTablePK("APOR", "CA"))
                ))
        );

        when(lookupTableService.getIndividualIdTypes()).thenReturn(
                Collections.unmodifiableList(Lists.newArrayList(
                        new LookupTable(new LookupTablePK("IDTYPE-IND", "LTO")),
                        new LookupTable(new LookupTablePK("IDTYPE-IND", "COM")),
                        new LookupTable(new LookupTablePK("IDTYPE-IND", "NBI"))
                ))
        );

        when(lookupTableService.getVehicleIdTypes()).thenReturn(
                Collections.unmodifiableList(Lists.newArrayList(
                        new LookupTable(new LookupTablePK("IDTYPE-VHC", "PLT")),
                        new LookupTable(new LookupTablePK("IDTYPE-VHC", "CND"))
                ))
        );


        final AccessPass samplePendingAccessPass = AccessPass.builder()
                .passType(TEST_INDIVIDUAL_REQUEST.getPassType().toString())
                .identifierNumber(TEST_INDIVIDUAL_REQUEST.getMobileNumber())
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

        Registrant registrant = Registrant.builder().registrarId(0)
                .firstName("Jonas").build();

        // Attempting to find access passes that match the same reference ID, and a specified time frame
        when(mockAccessPassRepository.findAllByReferenceIDOrderByValidToDesc(anyString()))
                .thenReturn(singletonList(samplePendingAccessPass));

        try {
            this.instance.newRequestPass(TEST_INDIVIDUAL_REQUEST);
            fail("Expected exception did not throw");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), containsString("An existing PENDING/APPROVED RapidPass already exists"));
        }
    }

    /**
     * Test will be removed eventually --
     *
     * Currently relying on front end to do validation.
     *
     * See https://gitlab.com/dctx/rapidpass/rapidpass-api/-/issues/236
     */
    @Test
    void temporarilyAllowInvalidIdTypesForSingleNewAccessPassRequests(){

        final Registrant sampleRegistrant = Registrant.builder()
                .registrarId(0)
                .firstName(TEST_INDIVIDUAL_REQUEST.getFirstName())
                .lastName(TEST_INDIVIDUAL_REQUEST.getLastName())
                .referenceId(TEST_INDIVIDUAL_REQUEST.getIdentifierNumber())
                .email(TEST_INDIVIDUAL_REQUEST.getEmail())
                .mobile(TEST_INDIVIDUAL_REQUEST.getMobileNumber())
                .build();

        final AccessPass samplePendingAccessPass = AccessPass.builder()
                .passType(TEST_INDIVIDUAL_REQUEST.getPassType().toString())
                .destinationCity(TEST_INDIVIDUAL_REQUEST.getDestCity())
                .company(TEST_INDIVIDUAL_REQUEST.getCompany())
                // Allows platenumber (vehicle id type) for an individual access pass  request
                .aporType("PLT")
                .status(AccessPassStatus.PENDING.toString())
                .remarks(TEST_INDIVIDUAL_REQUEST.getRemarks())
                .referenceID(TEST_INDIVIDUAL_REQUEST.getIdentifierNumber())
                .registrantId(sampleRegistrant)
                .build();

        // mock registry always returns a registry
        final Registrar mockRegistrar = new Registrar();
        mockRegistrar.setId(0);

//        when(mockAccessPassRepository.findAllByReferenceIDOrderByValidToDesc(anyString())).thenReturn(Collections.emptyList());

        // no existing user
        when(mockRegistrantRepository.findByMobile(anyString())).thenReturn(null);

        // mock save and flush
        when(mockRegistrantRepository.save(ArgumentMatchers.any()))
                .thenReturn(Registrant.builder().registrarId(0)
                        .firstName("Jonas").build());

        when(mockAccessPassRepository.saveAndFlush(ArgumentMatchers.any())).thenReturn(samplePendingAccessPass);

        when(lookupTableService.getAporTypes()).thenReturn(
                Collections.unmodifiableList(Lists.newArrayList(
                        new LookupTable(new LookupTablePK("APOR", "AG")),
                        new LookupTable(new LookupTablePK("APOR", "BP")),
                        new LookupTable(new LookupTablePK("APOR", "CA"))
                ))
        );

        when(lookupTableService.getIndividualIdTypes()).thenReturn(
                Collections.unmodifiableList(Lists.newArrayList(
                        new LookupTable(new LookupTablePK("IDTYPE-IND", "LTO")),
                        new LookupTable(new LookupTablePK("IDTYPE-IND", "COM")),
                        new LookupTable(new LookupTablePK("IDTYPE-IND", "NBI"))
                ))
        );

        when(lookupTableService.getVehicleIdTypes()).thenReturn(
                Collections.unmodifiableList(Lists.newArrayList(
                        new LookupTable(new LookupTablePK("IDTYPE-VHC", "PLT")),
                        new LookupTable(new LookupTablePK("IDTYPE-VHC", "CND"))
                ))
        );

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
        final RapidPass approved = instance.updateAccessPass(
                "ref-id",
                RapidPassStatus.builder()
                        .status(AccessPassStatus.APPROVED)
                        .remarks(null) // No need for remarks if the user is approved
                        .build()
        );

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
        final RapidPass approved = instance.updateAccessPass("ref-id", RapidPassStatus.builder()
                .status(AccessPassStatus.DECLINED)
                .remarks("Some reason here")
                .build()
        );

        assertThat(approved, is(notNullValue()));
        assertThat(approved.getStatus(), is("DECLINED"));

        verify(mockAccessPassRepository).saveAndFlush(ArgumentMatchers.any(AccessPass.class));
    }
}
