package ph.devcon.rapidpass.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ph.devcon.rapidpass.model.RapidPassRequest;
import ph.devcon.rapidpass.service.RegistryService;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ph.devcon.rapidpass.model.RapidPassRequest.AccessType.MED;
import static ph.devcon.rapidpass.model.RapidPassRequest.AccessType.O;
import static ph.devcon.rapidpass.model.RapidPassRequest.RequestType.INDIVIDUAL;
import static ph.devcon.rapidpass.model.RapidPassRequest.RequestType.VEHICLE;

/**
 * Tests for PwaController.
 */
@WebMvcTest(RegistryController.class)
class RegistryControllerTest {

    public static final RapidPassRequest TEST_INDIVIDUAL_REQUEST =
            RapidPassRequest.builder()
                    .passType(INDIVIDUAL)
                    .name("Jonas Espelita")
                    .mobileNumber("0915999999")
                    .email("jonas.was.here@gmail.com")
                    .destAddress("Somewhere in the PH")
                    .company("DEVCON")
                    .accessType(O)
                    .remarks("This is a test for INDIVIDUAL REQUEST")
                    .build();

    public static final RapidPassRequest TEST_VEHICLE_REQUEST = RapidPassRequest.builder()
            .passType(VEHICLE)
            .plateOrId("ABCD 1234")
            .mobileNumber("0915999999")
            .email("jonas.was.here@gmail.com")
            .destAddress("Somewhere in the PH")
            .company("DEVCON")
            .accessType(MED)
            .remarks("This is a test for VEHICLE REQUEST").build();
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
                                "  \"name\": \"Jonas Espelita\",\n" +
                                "  \"mobileNumber\": \"0915999999\",\n" +
                                "  \"email\": \"jonas.was.here@gmail.com\",\n" +
                                "  \"destAddress\": \"Somewhere in the PH\",\n" +
                                "  \"company\": \"DEVCON\",\n" +
                                "  \"accessType\": \"O\",\n" +
                                "  \"remarks\": \"This is a test for INDIVIDUAL REQUEST\"\n" +
                                "}"))
                .andExpect(status().isCreated());

        // verify that the RapidPassRequest model is properly created and matches expected attributes and passed to the pwaService
        verify(mockRegistryService, only()).createPassRequest(eq(TEST_INDIVIDUAL_REQUEST));
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
        verify(mockRegistryService, only()).createPassRequest(eq(TEST_VEHICLE_REQUEST));
    }

    /**
     * This tests GETting `requestPass` with either mobileNum or plateNum.
     *
     * @throws Exception on failed test
     */
    @Test
    void getPassRequest() throws Exception {
        // mock service to return dummy INDIVIDUAL pass request when individual is request type.
        when(mockRegistryService.getPassRequest(eq("0915999999")))
                .thenReturn(TEST_INDIVIDUAL_REQUEST);

        // mock service to return dummy VEHICLE pass request when vehicle is request type.
        when(mockRegistryService.getPassRequest(eq("ABCD 1234")))
                .thenReturn(TEST_VEHICLE_REQUEST);

        final String getAccessPathUrlTemplate = "/api/v1/registry/accessPasses/{referenceID}";

        // perform GET requestPass with mobileNum
        mockMvc.perform(
                get(getAccessPathUrlTemplate, "0915999999"))
                .andExpect(status().isOk())
                // test json is expected
                .andExpect(jsonPath("$.passType").value("INDIVIDUAL"))
                .andExpect(jsonPath("$.name").value("Jonas Espelita"))
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