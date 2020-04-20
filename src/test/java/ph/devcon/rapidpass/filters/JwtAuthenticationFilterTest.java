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
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ph.devcon.rapidpass.config.JwtSecretsConfig;
import ph.devcon.rapidpass.utilities.JwtGenerator;

import javax.servlet.http.Cookie;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests JwtAuthenticationFilter.
 *
 * @author jonasespelita@gmail.com
 */
@WebMvcTest(controllers = JwtAuthenticationFilterTest.TestSecurityConfig.class)
class JwtAuthenticationFilterTest {
    static final String CHECKPOINT_SECRET = "checkpoint-secret";
    @Autowired
    MockMvc mockMvc;

    @Test
    public void test_WITH_VALID_JWT() throws Exception {
        // generate a jwt
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", "12314");
        claims.put("name", "Kevin Smith");
        claims.put("group", "checkpoint");
        claims.put("xsrfToken", "some-uuid");

        String token = JwtGenerator.generateToken(claims, CHECKPOINT_SECRET);

        mockMvc.perform(get("/hello")
                .cookie(new Cookie("xsrfToken", "some-uuid"))
                .header("xsrfToken", "some-uuid")
                .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("hello"));
    }

    @Test
    public void test_NO_JWT() throws Exception {
        mockMvc.perform(get("/hello"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    public void test_JWT_INVALID_ROLE() throws Exception {
        // generate a jwt with jwt
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", "12314");
        claims.put("name", "Kevin Smith");
        claims.put("group", "checkpoint");

        String token = JwtGenerator.generateToken(claims, CHECKPOINT_SECRET);

        mockMvc.perform(get("/approver")
                .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    public void test_JWT_VALID_ROLE() throws Exception {
        // generate a jwt with jwt
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", "12314");
        claims.put("name", "Kevin Smith");
        claims.put("group", "approver");

        String token = JwtGenerator.generateToken(claims, CHECKPOINT_SECRET);

        mockMvc.perform(get("/approver")
                .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isForbidden());
    }


    @RestController
    @Configuration
    @RequiredArgsConstructor
    @EnableConfigurationProperties
    @Import(JwtSecretsConfig.class)
    @EnableGlobalMethodSecurity(securedEnabled = true)
    static class TestSecurityConfig extends WebSecurityConfigurerAdapter {
        private final JwtSecretsConfig jwtSecretsConfig;

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            final JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtSecretsConfig);
            filter.postConstruct();

            http.csrf().disable() // just to simplify things
                    .addFilterAfter(filter, AbstractPreAuthenticatedProcessingFilter.class)
                    .authorizeRequests()
                    // authenticating errything!
                    .anyRequest()
                    .authenticated();
        }

        @GetMapping("/hello")
        public String hello() {
            return "hello";
        }

        @Secured({"approver"})
        // testing with secured annotation but we will be using RbacAuthorizationFilter to determin
        @GetMapping("/approver")
        public String approverOnly() {
            return "authorized approver!";
        }
    }


}
