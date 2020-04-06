package ph.devcon.rapidpass.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import ph.devcon.rapidpass.config.JwtSecretsConfig;
import ph.devcon.rapidpass.config.SimpleRbacConfig;

import java.time.Instant;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * This tests {@link HomeController#getVersion()}.
 *
 * @author jonasespelita@gmail.com
 */
@WebMvcTest({HomeController.class})
@Import({JwtSecretsConfig.class, SimpleRbacConfig.class})
class HomeControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    BuildProperties mockBuildProperties;


    @Test
    void getVersion() throws Exception {
        when(mockBuildProperties.getVersion()).thenReturn("TEST-1.0");
        final Instant now = Instant.now();
        when(mockBuildProperties.getTime()).thenReturn(now);

        mockMvc.perform(get("/version"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.version").value("TEST-1.0." + now.getEpochSecond()));

    }
}