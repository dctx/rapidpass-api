package ph.devcon.rapidpass.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import ph.devcon.rapidpass.filters.ApiKeyAuthenticationFilter;
import ph.devcon.rapidpass.filters.JwtAuthenticationFilter;
import ph.devcon.rapidpass.filters.RbacAuthorizationFilter;

import javax.annotation.PostConstruct;

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
@Slf4j
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final ApiKeyAuthenticationFilter apiKeyAuthenticationFilter;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final RbacAuthorizationFilter rbacAuthorizationFilter;

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
        http.csrf().disable() // just to simplify things
                .addFilterBefore(apiKeyAuthenticationFilter, AbstractPreAuthenticatedProcessingFilter.class)
                .addFilterAfter(jwtAuthenticationFilter, ApiKeyAuthenticationFilter.class)
                .addFilterAfter(rbacAuthorizationFilter, JwtAuthenticationFilter.class)
                .authorizeRequests()
                .mvcMatchers("/actuator/**").permitAll() // allow metrics endpoint to be scraped

                // TODO remove once we don't need!!!
                .mvcMatchers("/swagger-ui.html").permitAll() // temp access to swagger
                .mvcMatchers("/swagger-ui.html/**").permitAll() // temp access to swagger
                .mvcMatchers("/webjars/**").permitAll() // temp access to swagger
                .mvcMatchers("/api-docs/**").permitAll() // temp access to swagger
                .mvcMatchers("/swagger-resources/**").permitAll() // temp access to swagger
                .mvcMatchers("/").permitAll() // temp access to swagger

                // authenticating errything else!
                .anyRequest()
                .authenticated();
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
