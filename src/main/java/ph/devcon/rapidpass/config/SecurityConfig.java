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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
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

    @Value("${security.cors.allowed-origins}")
    private List<String> allowedOrigins;

    @Bean
    public FilterRegistrationBean corsFilter(){
        // For now, the only part that needs CORS is the log in for the approver.
        // Inside, we utilise JWT for authentication.

        CorsConfiguration config = new CorsConfiguration();

//        allowedOrigins.add("localhost");
        config.setAllowedOrigins(allowedOrigins);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/user/auth", config);

        config.setAllowCredentials(true);

        allowedOrigins.forEach(config::addAllowedOrigin);

        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        FilterRegistrationBean bean = new FilterRegistrationBean(new CorsFilter(source));
        bean.setOrder(0);
        return bean;
    }

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
        http.csrf()
                .disable() // just to simplify things
//                .cors().configurationSource(corsConfigurationSource()).and()
                .addFilterBefore(apiKeyAuthenticationFilter, AbstractPreAuthenticatedProcessingFilter.class)
                .addFilterAfter(jwtAuthenticationFilter, ApiKeyAuthenticationFilter.class)
                .addFilterAfter(rbacAuthorizationFilter, JwtAuthenticationFilter.class);
        // rbac config will take care of authorization. endpoints not in rbac is not authenticated
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
