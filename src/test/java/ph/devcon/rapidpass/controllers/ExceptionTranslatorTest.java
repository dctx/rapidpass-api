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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.http.MockHttpInputMessage;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ph.devcon.rapidpass.enums.PassType;
import ph.devcon.rapidpass.services.RegistryService;

import java.io.IOException;

import static org.hamcrest.Matchers.hasItem;
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
@Import({ExceptionTranslator.class})
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

    /**
     * Enum errors should have an `errors` property of type string array, containing the list of erroneous pass types.
     */
    @Test
    void enumError() throws Exception {
        mockMvc.perform(get("/invalidEnumException"))
                .andExpect(status().isInternalServerError())
                .andDo(print())
                .andExpect(jsonPath("$.message").value("Unexpected values for an enum could not be parsed."))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors").value(hasItem("Invalid value `INDIVEHICLE` for property `passType`. Allowed values are [INDIVIDUAL, VEHICLE].")));
    }

    /**
     * In case it was a parsing error that we're not able to handle, we just throw a 500 error.
     */
    @Test
    void unHandledParsingException() throws Exception {
        mockMvc.perform(get("/unHandledParsingException"))
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

        @GetMapping("/invalidEnumException")
        public void invalidEnumException() throws InvalidFormatException {

            InvalidFormatException invalidFormatException = new InvalidFormatException(mockJsonParser, "invalid format", "INDIVEHICLE", PassType.class);
            invalidFormatException.prependPath(
                    new JsonMappingException.Reference("", "passType")
            );


            throw new HttpMessageNotReadableException("invalid http message",
                    invalidFormatException,
                    new MockHttpInputMessage("test".getBytes()));
        }

        @GetMapping("/unHandledParsingException")
        public void unsupportedInvalidEnumException() throws InvalidFormatException {
            /*
             * In case it encounters failure to parse a piece of data
             * that is not an enum.
             */
            InvalidFormatException invalidFormatException = new InvalidFormatException(mockJsonParser, "invalid format", "INDIVEHICLE", String.class);
            invalidFormatException.prependPath(
                    new JsonMappingException.Reference("", "passType")
            );


            throw new HttpMessageNotReadableException("invalid http message",
                    invalidFormatException,
                    new MockHttpInputMessage("test".getBytes()));
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