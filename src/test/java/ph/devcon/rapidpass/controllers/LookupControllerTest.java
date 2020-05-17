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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import ph.devcon.rapidpass.entities.LookupTable;
import ph.devcon.rapidpass.entities.LookupTablePK;
import ph.devcon.rapidpass.enums.LookupType;
import ph.devcon.rapidpass.services.LookupService;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LookupController.class)
@EnableConfigurationProperties
@AutoConfigureMockMvc(addFilters = false) // let's simplify by not running keycloack filters
public class LookupControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LookupService mockLookupService;

    private MockHttpServletRequestBuilder req;

    @BeforeEach
    void setup() {
        this.req = get("/lookup").header("RP-API-KEY", "dctx");
    }

    @Test
    void testOkLookup() {

        final LookupTable ag = new LookupTable("AG", "Agribusiness & Agricultural Workers");
        ag.setLookupTablePK(new LookupTablePK("APOR", "AG"));
        final LookupTable ba = new LookupTable("BA", "Banks");
        ba.setLookupTablePK(new LookupTablePK("APOR", "BA"));
        when(this.mockLookupService.getByType(LookupType.APOR)).thenReturn(Arrays.asList(ag, ba));

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
