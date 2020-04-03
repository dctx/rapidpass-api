package ph.devcon.rapidpass.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import ph.devcon.rapidpass.filters.JwtAuthenticationFilter;

/**
 * The {@link SecurityConfig} configuration class sets up HTTP security using the Spring Security Framework.
 *
 * @author jonasespelita@gmail.com
 */
@Configuration
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    /**
     * Set security.enabled to true to enable secured this configuration. Defaults to false for developer convenience
     */
    @Value("${security.enabled:false}")
    private boolean securityEnabled = false;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable() // just to simplify things
                .addFilterAfter(jwtAuthenticationFilter, AbstractPreAuthenticatedProcessingFilter.class)
                .authorizeRequests()
                .antMatchers("/actuator/prometheus").permitAll() // allow metrics endpoint to be scraped
                // authenticating errything else!
                .anyRequest()
                .authenticated();

        // this is where we would implement authorizing group claims later

    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        // disable security config via settings
        if (!securityEnabled) web.ignoring().anyRequest();
    }
}
