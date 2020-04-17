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

import com.google.common.collect.ImmutableMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ph.devcon.rapidpass.config.SimpleRbacConfig;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {RbacAuthorizationFilterTest.TestSecurityConfig.class})
class RbacAuthorizationFilterTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void getEndpointRbacRoles_NO_RBAC() throws Exception {
        // allow checkpoint role in unsecure
        mockMvc.perform(get("/unsecure"))
                .andExpect(status().isOk());
    }

    @Test
    void getEndpointRbacRoles_INVALID_ROLE() throws Exception {
        // dont allow checkpoint role in approver
        mockMvc.perform(get("/approver"))
                .andExpect(status().isForbidden());
    }


    @Test
    void getEndpointRbacRoles_VALID_ROLE_CHECKPNT() throws Exception {
        // allow checkpoint role in approver
        mockMvc.perform(get("/checkpoint"))
                .andExpect(status().isOk());
    }

    @RestController
    @RequiredArgsConstructor
    @EnableConfigurationProperties // loads rbac properties from src/test/resources/application.yaml
    @Import({RbacAuthorizationFilter.class, SimpleRbacConfig.class})
    @Slf4j
    @EnableWebSecurity
    static class TestSecurityConfig extends WebSecurityConfigurerAdapter {
        private final RbacAuthorizationFilter rbacAuthorizationFilter;

        @Override
        protected void configure(HttpSecurity http) throws Exception {

            // this mock filter adds checkpoint role
            final AbstractPreAuthenticatedProcessingFilter mockAuthenticationFilter = new AbstractPreAuthenticatedProcessingFilter() {
                @Override
                protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
                    return ImmutableMap.of("group", "checkpoint");
                }

                @Override
                protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
                    return "creds";
                }
            };
            mockAuthenticationFilter.setAuthenticationManager(JwtAuthenticationFilter.JWT_AUTHENTICATION_MANAGER);


            // do test configuration
            http.addFilterAfter(rbacAuthorizationFilter, AbstractPreAuthenticatedProcessingFilter.class)
                    .addFilterBefore(mockAuthenticationFilter, RbacAuthorizationFilter.class);
        }

        @GetMapping("/unsecure")
        public String unsecured() {
            return "unsecured!";
        }

        @GetMapping("/approver")
        public String approverOnly() {
            return "authorized approver!";
        }

        @GetMapping("/checkpoint")
        public String checkpointOnly() {
            return "authorized checkpoint!";
        }
    }
}