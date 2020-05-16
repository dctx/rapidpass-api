/*
 * Copyright (c) 2020.  DevConnect Philippines, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and limitations under the License.
 */

package ph.devcon.rapidpass.services;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import ph.devcon.rapidpass.api.models.ControlCodeResponse;
import ph.devcon.rapidpass.api.models.RapidPassUpdateRequest;
import ph.devcon.rapidpass.entities.*;
import ph.devcon.rapidpass.enums.AccessPassStatus;
import ph.devcon.rapidpass.exceptions.CsvColumnMappingMismatchException;
import ph.devcon.rapidpass.kafka.RapidPassEventProducer;
import ph.devcon.rapidpass.kafka.RapidPassRequestProducer;
import ph.devcon.rapidpass.models.QueryFilter;
import ph.devcon.rapidpass.models.RapidPass;
import ph.devcon.rapidpass.models.RapidPassCSVdata;
import ph.devcon.rapidpass.models.RapidPassRequest;
import ph.devcon.rapidpass.repositories.*;
import ph.devcon.rapidpass.services.controlcode.ControlCodeService;
import ph.devcon.rapidpass.utilities.csv.SubjectRegistrationCsvProcessorTest;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.*;

import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;
import static ph.devcon.rapidpass.enums.PassType.INDIVIDUAL;
import static ph.devcon.rapidpass.enums.PassType.VEHICLE;

@ExtendWith(MockitoExtension.class)
class RegistryServiceTest {

    RegistryService instance;

    @Mock ApproverAuthService mockAuthService;

    @Mock RegistrarRepository mockRegistrarRepository;

    @Mock ControlCodeService mockControlCodeService;

    @Mock RegistrarUserRepository mockRegistrarUserRepository;

    @Mock RegistryRepository mockRegistryRepository;

    @Mock RegistrantRepository mockRegistrantRepository;

    @Mock AccessPassRepository mockAccessPassRepository;

    @Mock AccessPassNotifierService mockAccessPassNotifierService;

    @Mock ScannerDeviceRepository mockScannerDeviceRepository;

    @Mock LookupTableService lookupTableService;

    @Mock
    RapidPassEventProducer eventProducer;

    @Mock
    RapidPassRequestProducer requestProducer;

    @Mock
    AccessPassEventRepository accessPassEventRepository;

    @BeforeEach
    void setUp() {

        instance = new RegistryService(
                requestProducer,
                eventProducer,
                accessPassEventRepository,
                mockAuthService,
                lookupTableService,
                mockAccessPassNotifierService,
                mockRegistrarRepository,
                mockRegistryRepository,
                mockControlCodeService,
                mockRegistrantRepository,
                mockAccessPassRepository,
                mockScannerDeviceRepository,
                mockRegistrarUserRepository
        );

        instance.expirationMonth = 5;
        instance.expirationDay = 15;
        instance.expirationYear = 2020;

        instance.isKafaEnabled=false;
//        OffsetDateTime now = OffsetDateTime.now();
    }

    public static final RapidPassRequest TEST_INDIVIDUAL_REQUEST =
            RapidPassRequest.builder()
                    .passType(INDIVIDUAL)
                    .firstName("Jonas")
                    .lastName("Espelita")
                    .idType("COM")
                    .identifierNumber("0915999999")
                    .plateNumber("ABC4321")
                    .mobileNumber("09662016319")
                    .email("jonas.was.here@gmail.com")
                    .originStreet("origin street")
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

        SecurityContext mockSecurityContext = mock(SecurityContext.class);
        Authentication mockAuthentication = mock(Authentication.class);

        SecurityContextHolder.setContext(mockSecurityContext);

        HashMap<String, Object> object = new HashMap<>();
        object.put("sub", "jose.rizal@gmail.com");

        when(mockSecurityContext.getAuthentication()).thenReturn(mockAuthentication);
        when(mockAuthentication.getPrincipal()).thenReturn(object);

        final Registrant sampleRegistrant = Registrant.builder()
                .id(1)
                .registrarId(0)
                .firstName(TEST_INDIVIDUAL_REQUEST.getFirstName())
                .lastName(TEST_INDIVIDUAL_REQUEST.getLastName())
                .referenceId(TEST_INDIVIDUAL_REQUEST.getIdentifierNumber())
                .email(TEST_INDIVIDUAL_REQUEST.getEmail())
                .mobile(TEST_INDIVIDUAL_REQUEST.getMobileNumber())
                .build();

        final AccessPass samplePendingAccessPass = AccessPass.builder()
                .id(1)
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
        when(mockRegistrantRepository.saveAndFlush(ArgumentMatchers.any()))
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
                .saveAndFlush(ArgumentMatchers.any(Registrant.class));
        // save and flush access pass
        verify(mockAccessPassRepository, times(2))
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

        RapidPassUpdateRequest approveRequest = new RapidPassUpdateRequest();
        approveRequest.setStatus(RapidPassUpdateRequest.StatusEnum.APPROVED);
        approveRequest.setRemarks(null);

        final RapidPass approved = instance.updateAccessPass(
                "ref-id",
                approveRequest
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

        RapidPassUpdateRequest approveRequest = new RapidPassUpdateRequest();
        approveRequest.setStatus(RapidPassUpdateRequest.StatusEnum.DECLINED);
        approveRequest.setRemarks("Some reason here");

        final RapidPass approved = instance.updateAccessPass("ref-id", approveRequest);

        assertThat(approved, is(notNullValue()));
        assertThat(approved.getStatus(), is("DECLINED"));

        verify(mockAccessPassRepository, times(2)).saveAndFlush(ArgumentMatchers.any(AccessPass.class));
    }

    @Test
    void ensureThatFindAllByQueryFilterIsExecuted() {

        ImmutableList<AccessPass> collections = ImmutableList.of(
                AccessPass.builder()
                        .referenceID("REF-1")
                        .passType(INDIVIDUAL.toString())
                        .name("AJ")
                        .build()
        );

        QueryFilter queryFilter = QueryFilter.builder()
                .search("AJ")
                .pageNo(1)
                .maxPageRows(5)
                .passType(INDIVIDUAL.toString())
                .build();

//        when(mockAccessPassRepository.findAll(any(), (Pageable) any())).thenReturn(
//                new PageImpl(collections)
//        );
//
//        RapidPassPageView rapidPass = instance.findRapidPass(queryFilter);
//
//        assertThat(rapidPass.getRapidPassList(), hasItem((hasProperty("name", equalTo("AJ")))));
//
//        verify(mockAccessPassRepository, only()).findAll(any(), (Pageable) any());
    }

    @Test
    void bulkUploadShouldOverwriteExtendAnExpiredApprovedPass() {

        when(lookupTableService.getAporTypes()).thenReturn(
                Collections.unmodifiableList(Lists.newArrayList(
                        new LookupTable(new LookupTablePK("APOR", "AG")),
                        new LookupTable(new LookupTablePK("APOR", "BP")),
                        new LookupTable(new LookupTablePK("APOR", "MS"))
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

        List<AccessPass> collections = new ArrayList<>();
        collections.add(
                AccessPass.builder()
                        .id(1)
                        .referenceID("09171234567")
                        .passType(INDIVIDUAL.toString())
                        .name("Darren Sapalo")
                        .status("APPROVED")
                        .aporType("MS")
                        .plateNumber("ABC123")
                        .originName("Origin")
                        .originCity("Origin City")
                        .originProvince("Origin Province")
                        .destinationName("Destination")
                        .destinationCity("Destination City")
                        .destinationProvince("Destination Province")
                        .validFrom(OffsetDateTime.of(2020, 4, 25, 23, 59, 59, 0, ZoneOffset.ofHours(8)))
                        .validTo(OffsetDateTime.of(2020, 4, 25, 23, 59, 59, 0, ZoneOffset.ofHours(8)))
                        .build()
        );

        RapidPassCSVdata csvData = new RapidPassCSVdata();
        csvData.setPassType("INDIVIDUAL");
        csvData.setAporType("MS");
        csvData.setFirstName("Jose");
        csvData.setLastName("Rizal");
        csvData.setCompany("DevCon.PH");
        csvData.setIdType("1234");
        csvData.setIdentifierNumber("1234");
        csvData.setPlateNumber("ABC123");
        csvData.setMobileNumber("09171234567");
        csvData.setEmail("jose.rizal@gmail.com");
        csvData.setOriginName("Origin");
        csvData.setOriginStreet("Origin Street");
        csvData.setOriginCity("Origin City");
        csvData.setOriginProvince("Origin Province");
        csvData.setDestName("Dest");
        csvData.setDestStreet("Dest Street");
        csvData.setDestCity("Dest City");
        csvData.setDestProvince("Dest Province");

        ImmutableList<RapidPassCSVdata> mockCsvData = ImmutableList.of(
                csvData
        );

        when(mockAccessPassRepository.findAllByReferenceIDAndPassTypeAndStatusInOrderByValidToDesc(any(), any(), any()))
                .thenReturn(
                        collections
                );

        try {
            List<String> strings = instance.batchUploadRapidPassRequest(mockCsvData);
            assertThat(strings, hasItem(containsString("Extended the validity of the Access Pass.")));

        } catch (RegistryService.UpdateAccessPassException e) {
            e.printStackTrace();
            fail(e);
        }
    }

    @Test
    void bulkUploadShouldOverwriteExistingPendingData() {

        when(lookupTableService.getAporTypes()).thenReturn(
                Collections.unmodifiableList(Lists.newArrayList(
                        new LookupTable(new LookupTablePK("APOR", "AG")),
                        new LookupTable(new LookupTablePK("APOR", "BP")),
                        new LookupTable(new LookupTablePK("APOR", "MS"))
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

        List<AccessPass> collections = new ArrayList<>();
        collections.add(
                AccessPass.builder()
                        .id(1)
                        .referenceID("09171234567")
                        .passType(INDIVIDUAL.toString())
                        .name("Darren Sapalo")
                        .status("PENDING")
                        .aporType("MS")
                        .plateNumber("ABC123")
                        .originName("Origin")
                        .originCity("Origin City")
                        .originProvince("Origin Province")
                        .destinationName("Destination")
                        .destinationCity("Destination City")
                        .destinationProvince("Destination Province")
                        .validFrom(OffsetDateTime.of(2020, 4, 25, 23, 59, 59, 0, ZoneOffset.ofHours(8)))
                        .validTo(OffsetDateTime.of(2020, 4, 25, 23, 59, 59, 0, ZoneOffset.ofHours(8)))
                        .build()
        );

        RapidPassCSVdata csvData = new RapidPassCSVdata();
        csvData.setPassType("INDIVIDUAL");
        csvData.setAporType("MS");
        csvData.setFirstName("Jose");
        csvData.setLastName("Rizal");
        csvData.setCompany("DevCon.PH");
        csvData.setIdType("1234");
        csvData.setIdentifierNumber("1234");
        csvData.setPlateNumber("ABC123");
        csvData.setMobileNumber("09171234567");
        csvData.setEmail("jose.rizal@gmail.com");
        csvData.setOriginName("Origin");
        csvData.setOriginStreet("Origin Street");
        csvData.setOriginCity("Origin City");
        csvData.setOriginProvince("Origin Province");
        csvData.setDestName("Dest");
        csvData.setDestStreet("Dest Street");
        csvData.setDestCity("Dest City");
        csvData.setDestProvince("Dest Province");

        ImmutableList<RapidPassCSVdata> mockCsvData = ImmutableList.of(
                csvData
        );

        when(mockAccessPassRepository.findAllByReferenceIDAndPassTypeAndStatusInOrderByValidToDesc(any(), any(), any()))
                .thenReturn(
                        collections
                );

        try {
            List<String> strings = instance.batchUploadRapidPassRequest(mockCsvData);
            assertThat(strings, hasItem(containsString("Success.")));

        } catch (RegistryService.UpdateAccessPassException e) {
            e.printStackTrace();
            fail("Thrown unexpected error", e);
        }
    }

    @Test
    void getControlCode_exists() {

        final AccessPass mockAccessPass = AccessPass.builder()
                .id(1)
                .controlCode("ABCDEFG1")
                .passType(TEST_INDIVIDUAL_REQUEST.getPassType().toString())
                .destinationCity(TEST_INDIVIDUAL_REQUEST.getDestCity())
                .company(TEST_INDIVIDUAL_REQUEST.getCompany())
                .aporType(TEST_INDIVIDUAL_REQUEST.getAporType())
                .status(AccessPassStatus.APPROVED.toString())
                .remarks(TEST_INDIVIDUAL_REQUEST.getRemarks())
                .referenceID(TEST_INDIVIDUAL_REQUEST.getIdentifierNumber())
                .build();


        when(mockAccessPassRepository.findAllByReferenceIDOrderByValidToDesc(any()))
                .thenReturn(ImmutableList.of(mockAccessPass));

        when(mockControlCodeService.encode(anyInt())).thenReturn("ABCDEFG1");

        ControlCodeResponse controlCode = instance.getControlCode("09171234567");

        assertThat(controlCode, not(equalTo(null)));
        assertThat(controlCode.getControlCode(), equalTo("ABCDEFG1"));
    }

    @Test
    void getControlCode_doesNotExist() {

        when(mockAccessPassRepository.findAllByReferenceIDOrderByValidToDesc(any()))
                .thenReturn(ImmutableList.of());

        ControlCodeResponse controlCode = instance.getControlCode("09171234567");

        assertThat(controlCode, equalTo(null));
    }


    @Test
    public void test_bulkUploadIncorrectColumns() throws CsvColumnMappingMismatchException, CsvRequiredFieldEmptyException, IOException, RegistryService.UpdateAccessPassException {

        RegistryService testTargetRegistryService = new RegistryService(
                null,
                null,
                null,
                null,
                lookupTableService,
                null,
                null,
                null,
                mockControlCodeService,
                mockRegistrantRepository,
                mockAccessPassRepository,
                mockScannerDeviceRepository,
                mockRegistrarUserRepository);

        // no existing user
        when(mockRegistrantRepository.findByMobile(anyString())).thenReturn(null);

        // mock save and flush
        Registrant mockRegistrant = Registrant.builder().registrarId(0)
                .firstName("Darren").build();

        when(mockRegistrantRepository.saveAndFlush(ArgumentMatchers.any()))
                .thenReturn(mockRegistrant);


        // Doesnt matter what the access pass returned is. As long as it looks like it was saved.
        final AccessPass samplePendingAccessPass = AccessPass.builder()
                .id(1)
                .passType(TEST_INDIVIDUAL_REQUEST.getPassType().toString())
                .destinationCity(TEST_INDIVIDUAL_REQUEST.getDestCity())
                .company(TEST_INDIVIDUAL_REQUEST.getCompany())
                .aporType(TEST_INDIVIDUAL_REQUEST.getAporType())
                .status(AccessPassStatus.PENDING.toString())
                .remarks(TEST_INDIVIDUAL_REQUEST.getRemarks())
                .referenceID(TEST_INDIVIDUAL_REQUEST.getIdentifierNumber())
                .registrantId(mockRegistrant)
                .build();

        when(mockAccessPassRepository.saveAndFlush(ArgumentMatchers.any())).thenReturn(samplePendingAccessPass);

        SubjectRegistrationCsvProcessorTest subjectRegistrationCsvProcessorTest = new SubjectRegistrationCsvProcessorTest();
        List<RapidPassCSVdata> mockData = subjectRegistrationCsvProcessorTest.mock("data-incorrect-columns.csv");

        when(lookupTableService.getAporTypes()).thenReturn(
                Collections.unmodifiableList(Lists.newArrayList(
                        new LookupTable(new LookupTablePK("APOR", "MS")),
                        new LookupTable(new LookupTablePK("APOR", "SO")),
                        new LookupTable(new LookupTablePK("APOR", "CA"))
                ))
        );

        ReflectionTestUtils.setField(testTargetRegistryService, "expirationYear", 2020);
        ReflectionTestUtils.setField(testTargetRegistryService, "expirationMonth", 5);
        ReflectionTestUtils.setField(testTargetRegistryService, "expirationDay", 15);

        List<String> strings = testTargetRegistryService.batchUploadRapidPassRequest(mockData);

        assertThat(strings.size(), equalTo(10));

        // Find five items which are all declined

        // Invalid mobile number, because missing columns accidentally misaligned the mobile number
        // (uses 0PASAYCITY as a mobile number)
        // INDIVIDUAL,MS,Darren,,DevCon PH,COM,1234567,,09174567891,Singalong St.,Pasay City,Metro Manila,,Pasay Road,Makati City,Metro Manila,skeleton workforce,,,,,,,,,,,,,,,,
        assertThat(strings.get(0), containsString("declined"));
        assertThat(strings.get(0), containsString("Invalid mobile input"));

        // INDIVIDUAL,SO,Jezza,,Diaz,,,,,,09176549873,,,,,,,,,,
        assertThat(strings.get(1), containsString("Success"));

        // No row data, correct length
        // ,,,,,,,,,,,,,,,,,,,,
        assertThat(strings.get(2), containsString("declined"));
        assertThat(strings.get(2), containsString("Missing APOR Type"));
        assertThat(strings.get(2), containsString("Missing Mobile Number"));
        assertThat(strings.get(2), containsString("Missing First Name"));
        assertThat(strings.get(2), containsString("Missing Last Name"));

        // Too few columns
        // ,,,,,,,
        assertThat(strings.get(3), containsString("declined"));
        assertThat(strings.get(3), containsString("Missing APOR Type"));
        assertThat(strings.get(3), containsString("Missing Mobile Number"));
        assertThat(strings.get(3), containsString("Missing First Name"));
        assertThat(strings.get(3), containsString("Missing Last Name"));

        // Too many columns
        // ,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
        assertThat(strings.get(4), containsString("declined"));
        assertThat(strings.get(4), containsString("Missing APOR Type"));
        assertThat(strings.get(4), containsString("Missing Mobile Number"));
        assertThat(strings.get(4), containsString("Missing First Name"));
        assertThat(strings.get(4), containsString("Missing Last Name"));

        // Plugs in INDIVIDUAL if pass type is missing
        // ,SO,Jose,,Rizal,,,,,,09171234567,,,,,,,,,,
        assertThat(strings.get(5), containsString("Success"));

        // No apor type
        // INDIVIDUAL,,Apolinario,,Mabini,,,,,,09171234567,,,,,,,,,,
        assertThat(strings.get(6), containsString("declined"));
        assertThat(strings.get(6), containsString("Missing APOR Type"));

        // No first name
        // INDIVIDUAL,SO,,,Rizal,,,,,,09171234567,,,,,,,,,,
        assertThat(strings.get(7), containsString("declined"));
        assertThat(strings.get(7), containsString("Missing First Name"));

        // No last name
        // INDIVIDUAL,SO,Andres,,,,,,,,09171234567,,,,,,,,,,
        assertThat(strings.get(8), containsString("declined"));
        assertThat(strings.get(8), containsString("Missing Last Name"));

        // No mobile number
        // INDIVIDUAL,SO,Andres,,Bonifacio,,,,,,,,,,,,,,,,
        assertThat(strings.get(9), containsString("declined"));
        assertThat(strings.get(9), containsString("Missing Mobile Number"));
    }

}
