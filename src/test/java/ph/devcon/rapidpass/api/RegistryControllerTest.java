package ph.devcon.rapidpass.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ph.devcon.rapidpass.controllers.RegistryRestController;
import ph.devcon.rapidpass.entities.AccessPass;
import ph.devcon.rapidpass.models.RapidPass;
import ph.devcon.rapidpass.models.RapidPassRequest;
import ph.devcon.rapidpass.services.RegistryService;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ph.devcon.rapidpass.enums.PassType.*;

/**
 * Tests for PwaController.
 */
@WebMvcTest(RegistryRestController.class)
class RegistryControllerTest {

    public static final RapidPassRequest TEST_INDIVIDUAL_REQUEST =
            RapidPassRequest.builder()
                    .passType(INDIVIDUAL)
                    .firstName("Jonas")
                    .lastName("Espelita")
                    .mobileNumber("0915999999")
                    .email("jonas.was.here@gmail.com")
                    .destCity("Somewhere in the PH")
                    .company("DEVCON")
                    .aporType("MO")
                    .remarks("This is a test for INDIVIDUAL REQUEST")
                    .build();

    public static final RapidPassRequest TEST_VEHICLE_REQUEST = RapidPassRequest.builder()
            .passType(VEHICLE)
            .plateOrId("ABCD 1234")
            .mobileNumber("0915999999")
            .email("jonas.was.here@gmail.com")
            .destCity("Somewhere in the PH")
            .company("DEVCON")
            .aporType("M")
            .remarks("This is a test for VEHICLE REQUEST").build();

    public RapidPass TEST_INDIVIDUAL_PASS;

    public RapidPass TEST_VEHICLE_PASS;

    @BeforeEach
    void init() {
        AccessPass individualAccessPass = new AccessPass();

        individualAccessPass.setPassType(TEST_INDIVIDUAL_REQUEST.getPassType().toString());
        individualAccessPass.setDestinationAddress(TEST_INDIVIDUAL_REQUEST.getDestCity());
        individualAccessPass.setCompany(TEST_INDIVIDUAL_REQUEST.getCompany());
        individualAccessPass.setAporType(TEST_INDIVIDUAL_REQUEST.getAporType());
        individualAccessPass.setRemarks(TEST_INDIVIDUAL_REQUEST.getRemarks());
        // Mobile number is the reference ID?
        individualAccessPass.setReferenceId(TEST_INDIVIDUAL_REQUEST.getMobileNumber());

        AccessPass vehicleAccessPass = new AccessPass();

        vehicleAccessPass.setPassType(TEST_VEHICLE_REQUEST.getPassType().toString());
        vehicleAccessPass.setDestinationAddress(TEST_VEHICLE_REQUEST.getDestCity());
        vehicleAccessPass.setCompany(TEST_VEHICLE_REQUEST.getCompany());
        vehicleAccessPass.setAporType(TEST_VEHICLE_REQUEST.getAporType());
        vehicleAccessPass.setRemarks(TEST_VEHICLE_REQUEST.getRemarks());
        // Mobile number is the reference ID?
        vehicleAccessPass.setReferenceId(TEST_VEHICLE_REQUEST.getMobileNumber());

        TEST_INDIVIDUAL_PASS = RapidPass.buildFrom(individualAccessPass);

        TEST_VEHICLE_PASS = RapidPass.buildFrom(vehicleAccessPass);
    }

    @Autowired
    MockMvc mockMvc;

    @MockBean
    RegistryService mockRegistryService;

    /**
     * This tests POSTing to `requestPass` with a JSON payload for an INDIVIDUAL.
     *
     * @throws Exception on failed test
     */
    @Test
    void newRequestPass_INDIVIDUAL() throws Exception {
        // perform post request with json payload to mock server
        mockMvc.perform(
                post("/api/v1/registry/accessPasses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "  \"passType\": \"INDIVIDUAL\",\n" +
                                "  \"firstName\": \"Jonas\",\n" +
                                "  \"lastName\": \"Espelita\",\n" +
                                "  \"mobileNumber\": \"0915999999\",\n" +
                                "  \"email\": \"jonas.was.here@gmail.com\",\n" +
                                "  \"destAddress\": \"Somewhere in the PH\",\n" +
                                "  \"company\": \"DEVCON\",\n" +
                                "  \"accessType\": \"O\",\n" +
                                "  \"remarks\": \"This is a test for INDIVIDUAL REQUEST\"\n" +
                                "}"))
                .andExpect(status().isCreated());

        // verify that the RapidPassRequest model is properly created and matches expected attributes and passed to the pwaService
        verify(mockRegistryService, only()).newRequestPass(eq(TEST_INDIVIDUAL_REQUEST));
    }

    /**
     * This tests POSTing to `requestPass` with a JSON payload for a VEHICLE.
     *
     * @throws Exception on failed test
     */
    @Test
    void newRequestPass_VEHICLE() throws Exception {
        // perform post request with json payload to mock server
        mockMvc.perform(
                post("/api/v1/registry/accessPasses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "  \"passType\": \"VEHICLE\",\n" +
                                "  \"plateOrId\": \"ABCD 1234\",\n" +
                                "  \"mobileNumber\": \"0915999999\",\n" +
                                "  \"email\": \"jonas.was.here@gmail.com\",\n" +
                                "  \"destAddress\": \"Somewhere in the PH\",\n" +
                                "  \"company\": \"DEVCON\",\n" +
                                "  \"accessType\": \"MED\",\n" +
                                "  \"remarks\": \"This is a test for VEHICLE REQUEST\"\n" +
                                "}"))
                .andExpect(status().isCreated());

        // verify that the RapidPassRequest model is properly created and matches expected attributes and passed to the pwaService
        verify(mockRegistryService, only()).newRequestPass(eq(TEST_VEHICLE_REQUEST));
    }

    /**
     * This tests GETting `requestPass` with either mobileNum or plateNum.
     *
     * @throws Exception on failed test
     */
    @Test
    void getPassRequest() throws Exception {
        // mock service to return dummy INDIVIDUAL pass request when individual is request type.
        when(mockRegistryService.find(eq("0915999999")))
                .thenReturn(TEST_INDIVIDUAL_PASS);

        // mock service to return dummy VEHICLE pass request when vehicle is request type.
        when(mockRegistryService.find(eq("ABCD 1234")))
                .thenReturn(TEST_VEHICLE_PASS);

        final String getAccessPathUrlTemplate = "/api/v1/registry/accessPasses/{referenceID}";

        // perform GET requestPass with mobileNum
        mockMvc.perform(
                get(getAccessPathUrlTemplate, "0915999999"))
                .andExpect(status().isOk())
                // test json is expected
                .andExpect(jsonPath("$.passType").value("INDIVIDUAL"))
                .andExpect(jsonPath("$.firstName").value("Jonas"))
                .andExpect(jsonPath("$.lastName").value("Espelita"))
                .andExpect(jsonPath("$.mobileNumber").value("0915999999"))
                .andExpect(jsonPath("$.remarks").value("This is a test for INDIVIDUAL REQUEST"))
                .andExpect(jsonPath("$.requestStatus").value("PENDING"))
                .andDo(print());

        // perform GET requestPass with plateNum
        mockMvc.perform(
                get(getAccessPathUrlTemplate, "ABCD 1234"))
                .andExpect(status().isOk())
                // test json is expected
                .andExpect(jsonPath("$.passType").value("VEHICLE"))
                .andExpect(jsonPath("$.plateOrId").value("ABCD 1234"))
                .andExpect(jsonPath("$.remarks").value("This is a test for VEHICLE REQUEST"))
                .andExpect(jsonPath("$.requestStatus").value("PENDING"))
                .andDo(print());
    }


    @Test
    public void getPassRequest_NULL() throws Exception {
        // mock service to return null
        mockMvc.perform(
                get("/api/v1/registry/accessPasses/{referenceID}", "I DO NOT EXIST"))
                .andExpect(status().isNotFound());
    }
}