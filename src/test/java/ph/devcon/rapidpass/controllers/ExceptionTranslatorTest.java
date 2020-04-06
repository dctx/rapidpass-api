package ph.devcon.rapidpass.controllers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ph.devcon.rapidpass.config.JwtSecretsConfig;
import ph.devcon.rapidpass.config.SimpleRbacConfig;
import ph.devcon.rapidpass.services.RegistryService;

import java.io.IOException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests for {@link ExceptionTranslator}.
 *
 * @author jonasespelita@gmail.com
 */
@WebMvcTest({ExceptionTranslatorTest.TestController.class})
@Import({ExceptionTranslator.class, JwtSecretsConfig.class, SimpleRbacConfig.class})
@WithMockUser("test")
class ExceptionTranslatorTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    JsonParser mockJsonParser;

    @Test
    void illegalArg() throws Exception {
        mockMvc.perform(get("/illegalArgumentException"))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andExpect(jsonPath("$.message").value("illegal args"));


    }

    @Test
    void invalidFormat() throws Exception {
        mockMvc.perform(get("/invalidFormatException"))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andExpect(jsonPath("$.message").value("invalid format"));
    }

    @Test
    void updateError() throws Exception {
        mockMvc.perform(get("/updateAccessPassException"))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andExpect(jsonPath("$.message").value("update access exception"));
    }

    @Test
    void weirdError() throws Exception {
        mockMvc.perform(get("/weirdError"))
                .andExpect(status().isInternalServerError())
                .andDo(print())
                .andExpect(jsonPath("$.message").value("Something went wrong! Please contact application owners."));
    }

    @RestController
    @RequiredArgsConstructor
    @Configuration
    static class TestController {
        private final JsonParser mockJsonParser;

        @GetMapping("/illegalArgumentException")
        public void illegalArgumentException() throws IllegalArgumentException {
            throw new IllegalArgumentException("illegal args");
        }

        @GetMapping("/invalidFormatException")
        public void invalidFormatException() throws InvalidFormatException {
            throw new InvalidFormatException(mockJsonParser, "invalid format", "test", String.class);

        }

        @GetMapping("/updateAccessPassException")
        public void updateAccessPassException() throws RegistryService.UpdateAccessPassException {
            throw new RegistryService.UpdateAccessPassException("update access exception");
        }

        @GetMapping("/weirdError")
        public void weirdError() throws IOException {
            throw new IOException("something went wrong with IO");
        }
    }
}