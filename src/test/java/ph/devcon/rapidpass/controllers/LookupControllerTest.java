package ph.devcon.rapidpass.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import ph.devcon.rapidpass.config.JwtSecretsConfig;
import ph.devcon.rapidpass.entities.LookupTable;
import ph.devcon.rapidpass.enums.LookupType;
import ph.devcon.rapidpass.services.LookupTableService;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LookupController.class)
@EnableConfigurationProperties
@Import(JwtSecretsConfig.class)
public class LookupControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LookupTableService mockLookupService;

    private MockHttpServletRequestBuilder req;

    @BeforeEach
    void setup() {
        this.req = get("/lookup").header("RP-API-KEY", "dctx");
    }

    @Test
    void testOkLookup() {

        when(this.mockLookupService.getByType(LookupType.APOR)).thenReturn(Arrays.asList(
           new LookupTable("AG", "Agribusiness & Agricultural Workers"),
           new LookupTable("BA", "Banks")
        ));

        try {
            mockMvc.perform(req.queryParam("type", "APOR"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.APOR").isArray())
                .andExpect(jsonPath("$.APOR").isNotEmpty());
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    void testInvalidType() {
        try {
            mockMvc.perform(req.queryParam("type", ""))
                    .andExpect(status().isBadRequest());
        } catch (Exception e) {
            fail(e);
        }
    }

}
