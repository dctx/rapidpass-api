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

package ph.devcon.rapidpass.config;

import com.google.common.collect.ImmutableList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import ph.devcon.rapidpass.filters.ApiKeyAuthenticationFilter;
import ph.devcon.rapidpass.filters.JwtAuthenticationFilter;
import ph.devcon.rapidpass.filters.RbacAuthorizationFilter;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * The {@link SecurityConfig} configuration class sets up HTTP security using the Spring Security Framework.
 * <p>
 * The filters set up are as follows: {@link ApiKeyAuthenticationFilter} -> {@link JwtAuthenticationFilter} -> {@link RbacAuthorizationFilter}
 *
 * @author jonasespelita@gmail.com
 */
@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Slf4j
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final ApiKeyAuthenticationFilter apiKeyAuthenticationFilter;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final RbacAuthorizationFilter rbacAuthorizationFilter;

    @Value("${security.cors.allowedorigins}")
    private String allowedOriginsCsv;

    @Value("${security.cors.allowedorigins}")
    private List<String> allowedOrigins;


    /**
     * Set security.enabled to true to enable secured this configuration. Defaults to false for developer convenience
     */
    @Value("${security.enabled:false}")
    private boolean securityEnabled = false;

    @PostConstruct
    public void postConstruct() {
        log.info("SecurityConfig initialized!");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
//                .csrf().csrfTokenRepository(new CrossDomainCsrfTokenRepository())
//                .ignoringAntMatchers("/registry/auth", "/users/auth")
//                .and()
                .cors()
                .and()
                .addFilterBefore(apiKeyAuthenticationFilter, AbstractPreAuthenticatedProcessingFilter.class)
                .addFilterAfter(jwtAuthenticationFilter, ApiKeyAuthenticationFilter.class)
                .addFilterAfter(rbacAuthorizationFilter, JwtAuthenticationFilter.class);
        // rbac config will take care of authorization. endpoints not in rbac is not authenticated
    }


    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        log.debug("allowed origins {}", allowedOrigins);
        configuration.applyPermitDefaultValues();
        configuration.addAllowedMethod(HttpMethod.OPTIONS);
        configuration.addAllowedMethod(HttpMethod.PUT);
        configuration.setAllowedOrigins(allowedOrigins);
        configuration.setAllowedHeaders(ImmutableList.of(
                "Accept", "Accept-Encoding", "Accept-Language",
                "Connection", "Authorization", "Content-Length",
                "Content-Type", "Connection",
                "Host", "Origin", "RP-API-KEY", "Sec-Fetch-Dest",
                "Sec-Fetch-Mode", "Sec-Fetch-Site", "User-Agent",
                "X-XSRF-TOKEN"
        ));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        // disable security config via settings
        if (!securityEnabled) {
            log.warn("Security is currently disabled! security.enabled: false");
            web.ignoring().anyRequest();
        } else {
            log.warn("Security is currently enabled! security.enabled: true");

        }
    }
}
