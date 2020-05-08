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
import org.keycloak.adapters.KeycloakConfigResolver;
import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.keycloak.adapters.springsecurity.KeycloakConfiguration;
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
import org.keycloak.adapters.springsecurity.filter.KeycloakAuthenticationProcessingFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import ph.devcon.rapidpass.filters.RbacAuthorizationFilter;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * The {@link SecurityConfig} configuration class sets up HTTP security using the Spring Security Framework and Keycloak. Utilizes the {@link RbacAuthorizationFilter} class
 * for enforcing authorities.
 *
 * @author jonasespelita@gmail.com
 */
@KeycloakConfiguration
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig extends KeycloakWebSecurityConfigurerAdapter {

    @Value("${security.cors.allowedorigins}")
    private List<String> allowedOrigins;


    @PostConstruct
    void postConstruct() {
        log.info("Set up keycloak!");
    }

    private final RbacAuthorizationFilter rbacAuthorizationFilter;

    /**
     * Registers the KeycloakAuthenticationProvider with the authentication manager.
     */
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(keycloakAuthenticationProvider());
    }

    /**
     * Defines the session authentication strategy.
     */
    @Bean
    @Override
    protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new RegisterSessionAuthenticationStrategy(new SessionRegistryImpl());
    }

    /**
     * Make sure it looks at the configuration provided by the Spring Boot Adapter.
     */
    @Bean
    public KeycloakConfigResolver KeycloakConfigResolver() {
        return new KeycloakSpringBootConfigResolver();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);
        http.addFilterAfter(rbacAuthorizationFilter, KeycloakAuthenticationProcessingFilter.class)
                .authorizeRequests()
                .anyRequest().permitAll() // using RBAC filter to enforce authorities
                .and()
                .csrf()
                .disable()
                .cors();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        log.debug("allowed origins {}", allowedOrigins);
        configuration.applyPermitDefaultValues();
        configuration.setAllowedOrigins(allowedOrigins);
        configuration.addAllowedMethod(HttpMethod.OPTIONS);
        configuration.addAllowedMethod(HttpMethod.PUT);
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
}
