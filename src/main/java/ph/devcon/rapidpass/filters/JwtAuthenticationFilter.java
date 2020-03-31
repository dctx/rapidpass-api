package ph.devcon.rapidpass.filters;

import com.auth0.jwt.exceptions.SignatureVerificationException;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import ph.devcon.rapidpass.utilities.JwtGenerator;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * The {@link JwtAuthenticationFilter} verifies if we have valid signed tokens. runs after API Key filter - hence using {@link AbstractPreAuthenticatedProcessingFilter}
 * It does NOT authorize requestor groups as this should be done in a separate filter.
 *
 * @author jonasespelita@gmail.com
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Setter
public class JwtAuthenticationFilter extends AbstractPreAuthenticatedProcessingFilter {
    private static final String AUTH_HEADER_STRING = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer";

    @Value("${rapidpass.jwt.secret}")
    private String jwtSecret;

    /**
     * Retrieves the Authorization string from a request header.
     *
     * @param request http request
     * @return Authorization string from a request header
     */
    private static String getHeaderString(
            final HttpServletRequest request) {
        return request.getHeader(AUTH_HEADER_STRING);
    }

    /**
     * Removes the token prefix from a token string.
     *
     * @param token token string
     * @return clean token string
     */
    private static String cleanToken(
            final String token) {
        return token.replace(TOKEN_PREFIX, "").trim();
    }

    @PostConstruct
    void postConstruct() {
        // set up a simple authentication manager
        setAuthenticationManager(
                authentication -> {
                    // preauthenticated by API, trust it until we verify tokens, authorize with another filter
                    authentication.setAuthenticated(true);
                    return authentication;
                });
    }

    /**
     * Validates a jwt token to not be expired and have group claim.
     *
     * @param request request with Authorization Bearer header
     * @return map of claims as [string, string] as principal.
     * null when failed authentication prompting spring security to fail the auth
     */
    @Override
    protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {

        log.debug("Getting identity from request.");
        String token = getHeaderString(request);
        if (StringUtils.isEmpty(token)) {
            log.debug("Could not authenticate request header. No Token.");
            return null;
        }

        // remove Bearer
        token = cleanToken(token);

        try {
            // get the claims
            final Map<String, Object> claims =
                    JwtGenerator.claimsToMap(token);

            // validate claims and token if authentic
            if (!JwtGenerator.validateToken(token, claims, jwtSecret)) {
                log.warn("Could not authenticate JWT with claims {}", claims);
                return null;
            }
            log.debug("authenticating claims {}", claims);

            return claims;
        } catch (JwtException | SignatureVerificationException e) {
            log.debug("Could not authenticate request header. Invalid JWT found.");
            return null;
        }
    }


    /**
     * Returns the jwt as the pre authenticated credentials.
     *
     * @param request request with Authorization Bearer header
     * @return jwt preauthenticated credential
     */
    @Override
    protected Object getPreAuthenticatedCredentials(
            final HttpServletRequest request) {
        log.debug("Getting token from request.");
        String token = getHeaderString(request);
        if (StringUtils.isEmpty(token)) {
            log.debug("Could not authenticate request header.");
            return null;
        }

        final String cleanToken = cleanToken(token);
        log.debug("  found token {}", cleanToken);
        return cleanToken;
    }
}
