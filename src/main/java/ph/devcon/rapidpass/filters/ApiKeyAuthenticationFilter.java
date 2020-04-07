package ph.devcon.rapidpass.filters;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

/**
 * {@link ApiKeyAuthenticationFilter} is a simple filter that checks for the a valid API Key in the request headers.
 *
 * @author jonasespelita@gmail.com
 */
@Component
@Slf4j
@Setter
public class ApiKeyAuthenticationFilter extends AbstractPreAuthenticatedProcessingFilter {

    @Value("${rapidpass.auth.apiKey.key:secret}")
    private String rapidPassApiKey;

    @Value("${rapidpass.auth.apiKey.header:RP-API-KEY}")
    private String apiKeyHeader;

    @PostConstruct
    void postConstructor() {
        // this filter does not do final authentication. The JWT filter will do that.
        setAuthenticationManager(authentication -> {
            authentication.setAuthenticated(true);
            return authentication;
        });
        setCheckForPrincipalChanges(true);
    }

    @Override
    protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
        log.debug("Getting API-KEY from request.");
        final String requestApiKey = request.getHeader(apiKeyHeader);

        // check if header api key matches our api key
        if (!rapidPassApiKey.equals(requestApiKey)) {
            log.warn("API Key is not valid!");
            return null;
        }
        log.warn("API Key is authenticated!");
        return requestApiKey;
    }

    @Override
    protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
        return request.getHeader(apiKeyHeader);
    }
}
