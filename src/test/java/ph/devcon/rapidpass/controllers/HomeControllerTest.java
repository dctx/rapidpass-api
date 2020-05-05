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

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
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
@AutoConfigureMockMvc(addFilters = false) // let's simplify by not running keycloack filters

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