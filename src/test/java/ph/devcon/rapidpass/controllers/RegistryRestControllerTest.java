package ph.devcon.rapidpass.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import ph.devcon.rapidpass.config.JwtSecretsConfig;
import ph.devcon.rapidpass.config.SimpleRbacConfig;
import ph.devcon.rapidpass.entities.AccessPass;
import ph.devcon.rapidpass.entities.ControlCode;
import ph.devcon.rapidpass.enums.AccessPassStatus;
import ph.devcon.rapidpass.models.RapidPass;
import ph.devcon.rapidpass.models.RapidPassRequest;
import ph.devcon.rapidpass.models.RapidPassStatus;
import ph.devcon.rapidpass.services.ApproverAuthService;
import ph.devcon.rapidpass.services.QrPdfService;
import ph.devcon.rapidpass.services.RegistryService;

import java.util.ArrayList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ph.devcon.rapidpass.enums.PassType.INDIVIDUAL;
import static ph.devcon.rapidpass.enums.PassType.VEHICLE;

/**
 * Tests for PwaController.
 */
@WebMvcTest(RegistryRestController.class)
@EnableConfigurationProperties
@Import({ExceptionTranslator.class, JwtSecretsConfig.class, SimpleRbacConfig.class})
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

        TEST_VEHICLE_ACCESS_PASS = new AccessPass();

        TEST_VEHICLE_ACCESS_PASS.setPassType(TEST_VEHICLE_REQUEST.getPassType().toString());
        TEST_VEHICLE_ACCESS_PASS.setDestinationCity(TEST_VEHICLE_REQUEST.getDestCity());
        TEST_VEHICLE_ACCESS_PASS.setCompany(TEST_VEHICLE_REQUEST.getCompany());
        TEST_VEHICLE_ACCESS_PASS.setAporType(TEST_VEHICLE_REQUEST.getAporType());
        TEST_VEHICLE_ACCESS_PASS.setRemarks(TEST_VEHICLE_REQUEST.getRemarks());
        TEST_VEHICLE_ACCESS_PASS.setIdentifierNumber(TEST_VEHICLE_REQUEST.getIdentifierNumber());
        TEST_VEHICLE_ACCESS_PASS.setIdType(TEST_VEHICLE_REQUEST.getIdType());

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

    @MockBean
    ApproverAuthService mockApproverAuthService;

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

        // perform post request with json payload to mock server
        mockMvc.perform(
                post("/registry/access-passes")
                        .header(API_KEY_HEADER, API_KEY_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                // expect returned reference Id
                .andExpect(jsonPath("$.referenceId", is(referenceId)))
                .andDo(print());

        // verify that the RapidPassRequest model created and matches expected attributes and passed to the pwaService
        verify(mockRegistryService, only()).newRequestPass(any());
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
                        .content(jsonRequestBody))
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


    /**
     * This tests GETting `requestPass` with either mobileNum or plateNum.
     *
     * @throws Exception on failed test
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
        when(mockRegistryService.revoke(eq("0915999999")))
                .thenReturn(TEST_INDIVIDUAL_RAPID_PASS);

        // mock service to return null
        mockMvc.perform(
                delete("/registry/access-passes/{referenceID}", "0915999999").header(API_KEY_HEADER, API_KEY_VALUE))
                .andExpect(status().isOk());

        // verify that the RapidPassRequest model is properly created and matches expected attributes and passed to the pwaService
        verify(mockRegistryService, only()).revoke(eq("0915999999"));
    }

    @Test
    public void grantOrDenyRequest() throws Exception {
        // mock service to return dummy VEHICLE pass request when vehicle is request type.

        TEST_VEHICLE_RAPID_PASS.setStatus(AccessPassStatus.APPROVED.toString());

        RapidPassStatus approveRequest = RapidPassStatus.builder()
                .status(AccessPassStatus.APPROVED)
                .remarks(null)
                .build();

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
        )
                .andExpect(status().isOk())
                // test json is expected
                .andExpect(jsonPath("$.passType").value(TEST_VEHICLE_RAPID_PASS.getPassType().name()))
                .andExpect(jsonPath("$.controlCode").value(TEST_VEHICLE_RAPID_PASS.getControlCode()))
                .andExpect(jsonPath("$.identifierNumber").value(TEST_VEHICLE_RAPID_PASS.getIdentifierNumber()))
                .andExpect(jsonPath("$.status").value(TEST_VEHICLE_RAPID_PASS.getStatus()))
                .andDo(print());
    }

    @Test
    public void grantOrDenyRequest_testNothingUpdated() throws Exception {
        // mock service to return dummy VEHICLE pass request when vehicle is request type.

        TEST_VEHICLE_RAPID_PASS.setStatus(AccessPassStatus.APPROVED.toString());

        RapidPassStatus approveRequest = RapidPassStatus.builder()
                .status(AccessPassStatus.APPROVED)
                .remarks(null)
                .build();

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

        RapidPassStatus approveRequest = RapidPassStatus.builder()
                .status(AccessPassStatus.APPROVED)
                .remarks(null)
                .build();

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

        RapidPassStatus approveRequest = RapidPassStatus.builder()
                .build();

        final String urlPath = "/registry/access-passes/{referenceID}";

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonRequestBody = objectMapper.writeValueAsString(approveRequest);

        // perform GET requestPass with mobileNum
        mockMvc.perform(
                put(urlPath, TEST_VEHICLE_RAPID_PASS.getReferenceId())
                        .header(API_KEY_HEADER, API_KEY_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody)
        )
                .andExpect(status().isBadRequest())
                // test json is expected
//                .andExpect(jsonPath("$.message").value(containsString("No AccessPass found")))
                .andDo(print());
    }

    @Test
    public void downloadQrCode() throws Exception {
        final byte[] samplePdf = {1, 0, 1, 0, 1, 0};
        when(mockQrPdfService.generateQrPdf(eq("1234556"))).thenReturn(samplePdf);
        final MockHttpServletResponse response = mockMvc.perform(get("/registry/qr-codes/{referenceId}", "1234556").header(API_KEY_HEADER, API_KEY_VALUE))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        assertThat(response.getContentType(), is(MediaType.APPLICATION_PDF.toString()));
        assertThat(response.getContentAsByteArray(), is(samplePdf));
    }


}
