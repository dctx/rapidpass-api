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

package ph.devcon.rapidpass.filters;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({ApiKeyAuthenticationFilterTest.TestControllerConfig.class})
class ApiKeyAuthenticationFilterTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void doApiFilter_NO_API_KEY() throws Exception {
        mockMvc.perform(get("/test")).andExpect(status().isForbidden());
    }

    @Test
    void doApiFilter_WITH_API_KEY() throws Exception {
        mockMvc.perform(get("/test").header("TEST-API-KEY", "ABC123"))
                .andExpect(status().isOk());
    }

    @RestController
    @EnableWebSecurity
    @RequiredArgsConstructor
    @Import(ApiKeyAuthenticationFilter.class)
    static class TestControllerConfig extends WebSecurityConfigurerAdapter {
        private final ApiKeyAuthenticationFilter apiKeyAuthenticationFilter;

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            apiKeyAuthenticationFilter.setRapidPassApiKey("ABC123");
            apiKeyAuthenticationFilter.setApiKeyHeader("TEST-API-KEY");
            http.addFilterBefore(apiKeyAuthenticationFilter, AbstractPreAuthenticatedProcessingFilter.class)
                    .authorizeRequests()
                    .anyRequest()
                    .authenticated();
        }

        @GetMapping("/test")
        public String test() {
            return "test";
        }
    }


}