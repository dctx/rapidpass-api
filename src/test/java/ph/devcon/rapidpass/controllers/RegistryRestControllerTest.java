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

package ph.devcon.rapidpass.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import ph.devcon.rapidpass.api.models.RapidPassUpdateRequest;
import ph.devcon.rapidpass.entities.AccessPass;
import ph.devcon.rapidpass.entities.ControlCode;
import ph.devcon.rapidpass.enums.AccessPassStatus;
import ph.devcon.rapidpass.models.QueryFilter;
import ph.devcon.rapidpass.models.RapidPass;
import ph.devcon.rapidpass.models.RapidPassPageView;
import ph.devcon.rapidpass.models.RapidPassRequest;
import ph.devcon.rapidpass.services.QrPdfService;
import ph.devcon.rapidpass.services.RegistryService;

import java.io.ByteArrayOutputStream;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ph.devcon.rapidpass.enums.PassType.INDIVIDUAL;
import static ph.devcon.rapidpass.enums.PassType.VEHICLE;

/**
 * Tests for PwaController.
 */
@WebMvcTest(RegistryRestController.class)
@EnableConfigurationProperties
@AutoConfigureMockMvc(addFilters = false) // let's simplify by not running keycloack filters
@Import({ExceptionTranslator.class})
class RegistryRestControllerTest {
    public static final RapidPassRequest TEST_INDIVIDUAL_REQUEST =
            RapidPassRequest.builder()
                    .passType(INDIVIDUAL)
                    .identifierNumber("ILOVEYOUPOHZ")
                    .firstName("Jonas")
                    .lastName("Espelita")
                    .mobileNumber("0915999999")
                    .email("jonas.was.here@gmail.com")
                    .destCity("Somewhere in the PH")
                    .company("DEVCON")
                    .aporType("MO")
                    .originStreet("my street")
                    .lastName("pangit")
                    .firstName("ganda")
                    .destName("your heart")
                    .destStreet("love street")
                    .destProvince("Cold Shoulder")
                    .idType("DE")
                    .originProvince("Province of Love")
                    .originName("Abangers lane")
                    .originCity("Friendly Zone")
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
            .originStreet("my street")
            .lastName("pangit")
            .firstName("ganda")
            .destName("your heart")
            .destStreet("love street")
            .idType("DE")
            .originName("Abangers lane")
            .originCity("Friendly Zone")
            .remarks("This is a test for VEHICLE REQUEST").build();
    @InjectMocks
    private final ObjectMapper JSON_MAPPER = new ObjectMapper();

    private AccessPass TEST_INDIVIDUAL_ACCESS_PASS;
    public RapidPass TEST_INDIVIDUAL_RAPID_PASS;

    private AccessPass TEST_VEHICLE_ACCESS_PASS;
    public RapidPass TEST_VEHICLE_RAPID_PASS;

    private static final String API_KEY_HEADER = "RP-API-KEY";
    private static final String API_KEY_VALUE = "dctx";


    @BeforeEach
    void init() {
        TEST_INDIVIDUAL_ACCESS_PASS = new AccessPass();

        TEST_INDIVIDUAL_ACCESS_PASS.setPassType(TEST_INDIVIDUAL_REQUEST.getPassType().toString());
        TEST_INDIVIDUAL_ACCESS_PASS.setDestinationCity(TEST_INDIVIDUAL_REQUEST.getDestCity());
        TEST_INDIVIDUAL_ACCESS_PASS.setCompany(TEST_INDIVIDUAL_REQUEST.getCompany());
        TEST_INDIVIDUAL_ACCESS_PASS.setAporType(TEST_INDIVIDUAL_REQUEST.getAporType());
        TEST_INDIVIDUAL_ACCESS_PASS.setRemarks(TEST_INDIVIDUAL_REQUEST.getRemarks());
        // Mobile number is the reference ID?
        TEST_INDIVIDUAL_ACCESS_PASS.setReferenceID(TEST_INDIVIDUAL_REQUEST.getMobileNumber());
        TEST_INDIVIDUAL_ACCESS_PASS.setValidTo(OffsetDateTime.now());
        TEST_VEHICLE_ACCESS_PASS = new AccessPass();

        TEST_VEHICLE_ACCESS_PASS.setPassType(TEST_VEHICLE_REQUEST.getPassType().toString());
        TEST_VEHICLE_ACCESS_PASS.setDestinationCity(TEST_VEHICLE_REQUEST.getDestCity());
        TEST_VEHICLE_ACCESS_PASS.setCompany(TEST_VEHICLE_REQUEST.getCompany());
        TEST_VEHICLE_ACCESS_PASS.setAporType(TEST_VEHICLE_REQUEST.getAporType());
        TEST_VEHICLE_ACCESS_PASS.setRemarks(TEST_VEHICLE_REQUEST.getRemarks());
        TEST_VEHICLE_ACCESS_PASS.setIdentifierNumber(TEST_VEHICLE_REQUEST.getIdentifierNumber());
        TEST_VEHICLE_ACCESS_PASS.setIdType(TEST_VEHICLE_REQUEST.getIdType());
        TEST_VEHICLE_ACCESS_PASS.setValidTo(OffsetDateTime.now());

        // Mobile number is the reference ID?
        TEST_VEHICLE_ACCESS_PASS.setReferenceID(TEST_VEHICLE_REQUEST.getMobileNumber());

        TEST_INDIVIDUAL_RAPID_PASS = RapidPass.buildFrom(TEST_INDIVIDUAL_ACCESS_PASS);

        TEST_VEHICLE_RAPID_PASS = RapidPass.buildFrom(TEST_VEHICLE_ACCESS_PASS);
    }

    @Autowired
    MockMvc mockMvc;

    @MockBean
    RegistryService mockRegistryService;

    @MockBean
    QrPdfService mockQrPdfService;

    /**
     * This tests POSTing to `requestPass` with a JSON payload for an INDIVIDUAL.
     *
     * @throws Exception on failed test
     */
    @Test
    void newRequestPass_INDIVIDUAL() throws Exception {

        String requestBody = JSON_MAPPER.writeValueAsString(TEST_INDIVIDUAL_REQUEST);

        final String referenceId = TEST_INDIVIDUAL_ACCESS_PASS.getReferenceID();

        when(mockRegistryService.newRequestPass(any()))
                .thenReturn(RapidPass.buildFrom(TEST_INDIVIDUAL_ACCESS_PASS));

        // TODO: Fix unit tests

        // perform post request with json payload to mock server
        mockMvc.perform(
                post("/registry/access-passes")
                        .header(API_KEY_HEADER, API_KEY_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .with(testUser()).with(csrf()))
                .andExpect(status().is2xxSuccessful())
                // expect returned reference Id
//                .andExpect(jsonPath("$.referenceId", is(referenceId)))
                .andDo(print());

        // verify that the RapidPassRequest model created and matches expected attributes and passed to the pwaService
//        verify(mockRegistryService, only()).newRequestPass(any());
    }

    @Test
    void newRequestPass_INDIVIDUAL_NULL_FIELDS() throws Exception {
        String jsonRequestBody = JSON_MAPPER.writeValueAsString(
                RapidPassRequest
                        .builder().passType(INDIVIDUAL)
                        .build());
        // perform post request with json payload to mock server
        mockMvc.perform(
                post("/registry/access-passes")
                        .header(API_KEY_HEADER, API_KEY_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody)
                        .with(testUser()).with(csrf()))
                .andDo(print())
                .andExpect(status().isBadRequest())
                // spot check some errors
                .andExpect(jsonPath("$.identifierNumber", is("must not be empty")))
                .andExpect(jsonPath("$.lastName", is("must not be empty")))
                .andExpect(jsonPath("$.mobileNumber", is("must not be empty")));
    }

    /**
     * This tests POSTing to `requestPass` with a JSON payload for a VEHICLE.
     *
     * @throws Exception on failed test
     */
    @Test
    void newRequestPass_VEHICLE() throws Exception {

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonRequestBody = objectMapper.writeValueAsString(TEST_VEHICLE_REQUEST);

        // perform post request with json payload to mock server
        mockMvc.perform(
                post("/registry/access-passes")
                        .header(API_KEY_HEADER, API_KEY_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andReturn();
        // FIXME
//                .andExpect(status().isCreated());

        // verify that the RapidPassRequest model is properly created and matches expected attributes and passed to the pwaService
        // FIXME
//        verify(mockRegistryService, only()).newRequestPass(eq(TEST_VEHICLE_REQUEST));
    }

    @Test
    void searchAccessPassByMobileNumber() throws Exception {
        RapidPassPageView pageView = RapidPassPageView.builder()
                .currentPage(1)
                .currentPageRows(1)
                .totalPages(1)
                .totalRows(1)
                .isFirstPage(true)
                .isLastPage(true)
                .hasNext(false)
                .hasPrevious(false)
                .rapidPassList(Collections.singletonList(RapidPass.buildFrom(TEST_INDIVIDUAL_ACCESS_PASS)))
                .build();

        when(mockRegistryService.findRapidPass(any(QueryFilter.class)))
                .thenReturn(pageView);

        mockMvc.perform(
                get("/registry/access-passes")
                        .header(API_KEY_HEADER, API_KEY_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("search", TEST_INDIVIDUAL_REQUEST.getMobileNumber())
                        .with(testApproverUser()).with(csrf())
        )
                .andExpect(status().isOk())
                .andExpect(content().json(JSON_MAPPER.writeValueAsString(pageView)))
                .andDo(print());

        verify(mockRegistryService, only()).findRapidPass(any(QueryFilter.class));
    }

    /**
     * This tests GETting `requestPass` with either mobileNum or plateNum.
     *
     * @throws Exception on failed test
     */
    @Test
    void getPassRequest() throws Exception {
        // mock service to return dummy INDIVIDUAL pass request when individual is request type.
        when(mockRegistryService.findByNonUniqueReferenceId(eq("0915999999")))
                .thenReturn(TEST_INDIVIDUAL_ACCESS_PASS);

        // mock service to return dummy VEHICLE pass request when vehicle is request type.
        when(mockRegistryService.findByNonUniqueReferenceId(eq("ABCD 1234")))
                .thenReturn(TEST_VEHICLE_ACCESS_PASS);

        final String getAccessPathUrlTemplate = "/registry/access-passes/{referenceID}";

        // perform GET requestPass with mobileNum
        mockMvc.perform(
                get(getAccessPathUrlTemplate, "0915999999").header(API_KEY_HEADER, API_KEY_VALUE))
                .andExpect(status().isOk())
                // test json is expected
                .andExpect(jsonPath("$.passType").value("INDIVIDUAL"))
                .andDo(print());

        // perform GET requestPass with plateNum
        mockMvc.perform(
                get(getAccessPathUrlTemplate, "ABCD 1234").header(API_KEY_HEADER, API_KEY_VALUE))
                .andExpect(status().isOk())
                // test json is expected
                .andExpect(jsonPath("$.passType").value("VEHICLE"))
                .andExpect(jsonPath("$.identifierNumber").value("ABCD 1234"))
                .andDo(print());
    }


    @Test
    @WithMockUser("user")
    public void getPassRequest_notExists() throws Exception {

        when(mockRegistryService.findByNonUniqueReferenceId("I DO NOT EXIST"))
                .thenReturn(null);

        // mock service to return null
        mockMvc.perform(
                get("/registry/access-passes/{referenceID}", "I DO NOT EXIST")
                        .header(API_KEY_HEADER, API_KEY_VALUE))
                .andExpect(status().isNotFound()
                );

        verify(mockRegistryService, only()).findByNonUniqueReferenceId(eq(("I DO NOT EXIST")));
    }

    @Test
    public void getPassRequestStatus() throws Exception {
        TEST_INDIVIDUAL_ACCESS_PASS.setStatus(AccessPassStatus.APPROVED.toString());
        when(mockRegistryService.findByNonUniqueReferenceId(eq(TEST_INDIVIDUAL_ACCESS_PASS.getReferenceID())))
                .thenReturn(TEST_INDIVIDUAL_ACCESS_PASS);

        TEST_VEHICLE_ACCESS_PASS.setStatus(AccessPassStatus.APPROVED.toString());
        when(mockRegistryService.findByNonUniqueReferenceId(eq(TEST_VEHICLE_ACCESS_PASS.getReferenceID())))
                .thenReturn(TEST_VEHICLE_ACCESS_PASS);

        final String getAccessPathUrlTemplate = "/registry/access-passes/{referenceID}/status";

        mockMvc.perform(
                get(getAccessPathUrlTemplate, TEST_INDIVIDUAL_ACCESS_PASS.getReferenceID()).header(API_KEY_HEADER, API_KEY_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(AccessPassStatus.APPROVED.toString()))
                .andDo(print());

        mockMvc.perform(
                get(getAccessPathUrlTemplate, TEST_VEHICLE_ACCESS_PASS.getReferenceID()).header(API_KEY_HEADER, API_KEY_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(AccessPassStatus.APPROVED.toString()))
                .andDo(print());
    }

    @Test
    public void getPassRequestInvalidStatus() throws Exception {
        String declinedReason = "Fake";

        TEST_INDIVIDUAL_ACCESS_PASS.setStatus(AccessPassStatus.DECLINED.toString());
        TEST_INDIVIDUAL_ACCESS_PASS.setUpdates(declinedReason);
        when(mockRegistryService.findByNonUniqueReferenceId(eq(TEST_INDIVIDUAL_ACCESS_PASS.getReferenceID())))
                .thenReturn(TEST_INDIVIDUAL_ACCESS_PASS);

        final String getAccessPathUrlTemplate = "/registry/access-passes/{referenceID}/status";

        mockMvc.perform(
                get(getAccessPathUrlTemplate, TEST_INDIVIDUAL_ACCESS_PASS.getReferenceID()).header(API_KEY_HEADER, API_KEY_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(AccessPassStatus.DECLINED.toString()))
                .andExpect(jsonPath("$.reason").value(declinedReason))
                .andDo(print());

        TEST_INDIVIDUAL_ACCESS_PASS.setStatus(AccessPassStatus.SUSPENDED.toString());
        when(mockRegistryService.findByNonUniqueReferenceId(eq(TEST_INDIVIDUAL_ACCESS_PASS.getReferenceID())))
                .thenReturn(TEST_INDIVIDUAL_ACCESS_PASS);

        mockMvc.perform(
                get(getAccessPathUrlTemplate, TEST_INDIVIDUAL_ACCESS_PASS.getReferenceID()).header(API_KEY_HEADER, API_KEY_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(AccessPassStatus.SUSPENDED.toString()))
                .andExpect(jsonPath("$.reason").value(declinedReason))
                .andDo(print());

        TEST_VEHICLE_ACCESS_PASS.setStatus(AccessPassStatus.DECLINED.toString());
        TEST_VEHICLE_ACCESS_PASS.setUpdates(declinedReason);
        when(mockRegistryService.findByNonUniqueReferenceId(eq(TEST_VEHICLE_ACCESS_PASS.getReferenceID())))
                .thenReturn(TEST_VEHICLE_ACCESS_PASS);

        mockMvc.perform(
                get(getAccessPathUrlTemplate, TEST_VEHICLE_ACCESS_PASS.getReferenceID()).header(API_KEY_HEADER, API_KEY_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(AccessPassStatus.DECLINED.toString()))
                .andExpect(jsonPath("$.reason").value(declinedReason))
                .andDo(print());

        TEST_VEHICLE_ACCESS_PASS.setStatus(AccessPassStatus.SUSPENDED.toString());
        when(mockRegistryService.findByNonUniqueReferenceId(eq(TEST_VEHICLE_ACCESS_PASS.getReferenceID())))
                .thenReturn(TEST_VEHICLE_ACCESS_PASS);

        mockMvc.perform(
                get(getAccessPathUrlTemplate, TEST_VEHICLE_ACCESS_PASS.getReferenceID()).header(API_KEY_HEADER, API_KEY_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(AccessPassStatus.SUSPENDED.toString()))
                .andExpect(jsonPath("$.reason").value(declinedReason))
                .andDo(print());
    }

    @Test
    public void getNullPassRequestStatus() throws Exception {
        when(mockRegistryService.findByNonUniqueReferenceId(eq(TEST_INDIVIDUAL_ACCESS_PASS.getReferenceID())))
                .thenReturn(null);
        when(mockRegistryService.findByNonUniqueReferenceId(eq(TEST_VEHICLE_ACCESS_PASS.getReferenceID())))
                .thenReturn(null);

        final String getAccessPathUrlTemplate = "/registry/access-passes/{referenceID}/status";

        mockMvc.perform(
                get(getAccessPathUrlTemplate, TEST_INDIVIDUAL_ACCESS_PASS.getReferenceID()).header(API_KEY_HEADER, API_KEY_VALUE))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(String.format("There is no RapidPass found with reference ID `%s`", TEST_INDIVIDUAL_ACCESS_PASS.getReferenceID())))
                .andDo(print());

        mockMvc.perform(
                get(getAccessPathUrlTemplate, TEST_VEHICLE_ACCESS_PASS.getReferenceID()).header(API_KEY_HEADER, API_KEY_VALUE))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(String.format("There is no RapidPass found with reference ID `%s`", TEST_VEHICLE_ACCESS_PASS.getReferenceID())))
                .andDo(print());
    }

    /**
     * This tests GETting `requestPass` with either mobileNum or plateNum.
     *
     * @throws Exception on failed testRegistryRestControllerTest
     */
    @Test
    @Disabled
    // controld codes are currently not implemented! Also, test below is not correct!!!
    void getControlCodes() throws Exception {

        ControlCode controlCode = ControlCode.builder()
                .controlCode("12345")
                .passType(INDIVIDUAL.toString())
                .referenceId("ABCDE")
                .build();

        ArrayList<ControlCode> controlCodes = new ArrayList<>();
        controlCodes.add(controlCode);

        // mock service to return dummy INDIVIDUAL pass request when individual is request type.
        when(mockRegistryService.getControlCodes())
                .thenReturn(controlCodes);

        final String getAccessPathUrlTemplate = "/registry/control-codes/";

        // perform GET requestPass with mobileNum
        mockMvc.perform(
                get(getAccessPathUrlTemplate).header(API_KEY_HEADER, API_KEY_VALUE))
                .andExpect(status().isOk())
                // test json is expected
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0]").isMap())
                .andExpect(jsonPath("$[0].controlCode").value("12345"))
                .andExpect(jsonPath("$[0].passType").value("INDIVIDUAL"))
                .andExpect(jsonPath("$[0].referenceId").value("ABCDE"))
                .andDo(print());
    }


    public void revokeAccessPass() throws Exception {

        // mock service to return dummy INDIVIDUAL pass request when individual is request type.
        when(mockRegistryService.suspend(eq("0915999999"), any()))
                .thenReturn(TEST_INDIVIDUAL_ACCESS_PASS);

        // mock service to return null
        mockMvc.perform(
                delete("/registry/access-passes/{referenceID}", "0915999999").header(API_KEY_HEADER, API_KEY_VALUE))
                .andExpect(status().isOk());

        // verify that the RapidPassRequest model is properly created and matches expected attributes and passed to the pwaService
        verify(mockRegistryService, only()).suspend(eq("0915999999"), any());
    }

    @Test
    public void grantOrDenyRequest() throws Exception {
        // mock service to return dummy VEHICLE pass request when vehicle is request type.

        TEST_VEHICLE_RAPID_PASS.setStatus(AccessPassStatus.APPROVED.toString());

        RapidPassUpdateRequest approveRequest = new RapidPassUpdateRequest();
        approveRequest.setStatus(RapidPassUpdateRequest.StatusEnum.APPROVED);
        approveRequest.setRemarks(null);

        when(mockRegistryService.updateAccessPass(eq(TEST_VEHICLE_RAPID_PASS.getReferenceId()), eq(approveRequest)))
                .thenReturn(TEST_VEHICLE_RAPID_PASS);

        final String urlPath = "/registry/access-passes/{referenceID}";

        TEST_VEHICLE_RAPID_PASS.setStatus(AccessPassStatus.APPROVED.toString());

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonRequestBody = objectMapper.writeValueAsString(approveRequest);

        // perform GET requestPass with mobileNum
        mockMvc.perform(
                put(urlPath, "0915999999")
                        .header(API_KEY_HEADER, API_KEY_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody)
                        .with(testUser()).with(csrf())
        )
                .andExpect(status().isOk())
                // test json is expected
                .andExpect(jsonPath("$.passType").value(TEST_VEHICLE_RAPID_PASS.getPassType().name()))
                .andExpect(jsonPath("$.identifierNumber").value(TEST_VEHICLE_RAPID_PASS.getIdentifierNumber()))
                .andExpect(jsonPath("$.status").value(TEST_VEHICLE_RAPID_PASS.getStatus()))
                .andDo(print());
    }

    @Test
    public void grantOrDenyRequest_testNothingUpdated() throws Exception {
        // mock service to return dummy VEHICLE pass request when vehicle is request type.

        TEST_VEHICLE_RAPID_PASS.setStatus(AccessPassStatus.APPROVED.toString());

        RapidPassUpdateRequest approveRequest = new RapidPassUpdateRequest();
        approveRequest.setStatus(RapidPassUpdateRequest.StatusEnum.APPROVED);
        approveRequest.setRemarks(null);

        // Registry will not return any data, which will case a thrown exception
        when(mockRegistryService.updateAccessPass(eq(TEST_VEHICLE_RAPID_PASS.getReferenceId()), eq(approveRequest)))
                .thenReturn(null);

        final String urlPath = "/registry/access-passes/{referenceID}";

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonRequestBody = objectMapper.writeValueAsString(approveRequest);

        // perform GET requestPass with mobileNum
        mockMvc.perform(
                put(urlPath, TEST_VEHICLE_RAPID_PASS.getReferenceId())
                        .header(API_KEY_HEADER, API_KEY_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody)
                        .with(testUser()).with(csrf())
        )
                .andExpect(status().isBadRequest())
                // test json is expected
                .andExpect(jsonPath("$.message").value(containsString("there was nothing updated")))
                .andDo(print());
    }

    @Test
    public void grantOrDenyRequest_testThrowUpdateException() throws Exception {
        // mock service to return dummy VEHICLE pass request when vehicle is request type.

        TEST_VEHICLE_RAPID_PASS.setStatus(AccessPassStatus.APPROVED.toString());

        RapidPassUpdateRequest approveRequest = new RapidPassUpdateRequest();
        approveRequest.setStatus(RapidPassUpdateRequest.StatusEnum.APPROVED);
        approveRequest.setRemarks(null);

        // Registry will not return any data, which will case a thrown exception
        when(mockRegistryService.updateAccessPass(eq(TEST_VEHICLE_RAPID_PASS.getReferenceId()), eq(approveRequest)))
                .thenThrow(new RegistryService.UpdateAccessPassException("No AccessPass found"));

        final String urlPath = "/registry/access-passes/{referenceID}";

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonRequestBody = objectMapper.writeValueAsString(approveRequest);

        // perform GET requestPass with mobileNum
        mockMvc.perform(
                put(urlPath, TEST_VEHICLE_RAPID_PASS.getReferenceId())
                        .header(API_KEY_HEADER, API_KEY_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody)
                        .with(testUser()).with(csrf())
        )
                .andExpect(status().isBadRequest())
                // test json is expected
                .andExpect(jsonPath("$.message").value(containsString("No AccessPass found")))
                .andDo(print());
    }

    @Test
    public void grantOrDenyRequest_missingParameters() throws Exception {
        // mock service to return dummy VEHICLE pass request when vehicle is request type.

        TEST_VEHICLE_RAPID_PASS.setStatus(AccessPassStatus.APPROVED.toString());

        RapidPassUpdateRequest approveRequest = new RapidPassUpdateRequest();

        final String urlPath = "/registry/access-passes/{referenceID}";

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonRequestBody = objectMapper.writeValueAsString(approveRequest);

        // perform GET requestPass with mobileNum
        mockMvc.perform(
                put(urlPath, TEST_VEHICLE_RAPID_PASS.getReferenceId())
                        .header(API_KEY_HEADER, API_KEY_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody)
                        .with(testUser()).with(csrf())
        )
                .andExpect(status().isBadRequest())
                // test json is expected
//                .andExpect(jsonPath("$.message").value(containsString("No AccessPass found")))
                .andDo(print());
    }

    @Test
    public void downloadQrCode() throws Exception {
        final byte[] samplePdf = {1, 0, 1, 0, 1, 0};
        final ByteArrayOutputStream value = new ByteArrayOutputStream();
        value.write(samplePdf);
        when(mockQrPdfService.generateQrPdf(eq("1234556"))).thenReturn(value);
        final MockHttpServletResponse response = mockMvc.perform(get("/registry/qr-codes/{referenceId}", "1234556").header(API_KEY_HEADER, API_KEY_VALUE))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        assertThat(response.getContentType(), is(MediaType.APPLICATION_PDF.toString()));
        assertThat(response.getContentAsByteArray(), is(samplePdf));
    }

    protected RequestPostProcessor testUser() {
        return user("user").password("userPass").roles("USER");
    }

    protected RequestPostProcessor testApproverUser() {
        return user("user").password("userPass").roles("USER").authorities(Collections.singleton(new SimpleGrantedAuthority("approver")));
    }


}
