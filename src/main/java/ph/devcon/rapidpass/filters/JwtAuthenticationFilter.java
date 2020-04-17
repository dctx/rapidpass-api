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

import com.auth0.jwt.exceptions.SignatureVerificationException;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import ph.devcon.rapidpass.config.JwtSecretsConfig;
import ph.devcon.rapidpass.utilities.JwtGenerator;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Optional;

import static java.util.Collections.singletonList;

/**
 * {@link JwtAuthenticationFilter} verifies if we have valid signed tokens. runs after API Key filter - hence using {@link AbstractPreAuthenticatedProcessingFilter}
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

    private final JwtSecretsConfig jwtSecretsConfig;

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

    /**
     * This Authentication manager grants authorities by reading the JWT "group" claim.
     */
    final public static AuthenticationManager JWT_AUTHENTICATION_MANAGER = authentication -> {
        // preauthenticated by API, already verified by JwtGenerator.validateToken,
        // we are authenticated!
        authentication.setAuthenticated(true);

        // add group as authority
        //noinspection unchecked
        final Map<String, Object> principal = (Map<String, Object>) authentication.getPrincipal();
        final GrantedAuthority group = new SimpleGrantedAuthority((String) principal.get("group"));

        // create auth token with granted auth from group claim
        return new PreAuthenticatedAuthenticationToken(authentication.getPrincipal(),
                authentication.getCredentials(),
                singletonList(group));
    };

    @PostConstruct
    void postConstruct() {
        log.info("JwtAuthenticationFilter initialized!");
        // set up a simple authentication manager
        setAuthenticationManager(JWT_AUTHENTICATION_MANAGER);
        setCheckForPrincipalChanges(true);
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


        String token = getHeaderString(request);
        if (StringUtils.isEmpty(token)) {
            log.debug("Could not authenticate request header. No Token in request header.");
            // return whatever principal is currently authenticated or null if not
            final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            return authentication == null ? null : authentication.getPrincipal();
        }

        // remove Bearer
        token = cleanToken(token);
        log.debug("Got JWT token from request. {}", token);
        try {
            // get the claims
            final Map<String, Object> claims =
                    JwtGenerator.claimsToMap(token);

            // determine group secret
            String group = claims.get("group").toString();
            if (StringUtils.isEmpty(group)) {
                log.warn("Could not authenticate token. No group claim found.");
                return null;
            }

            final Optional<JwtSecretsConfig.JwtGroupSecret> groupSecretOptional = jwtSecretsConfig.getSecrets().stream()
                    .filter(grpSecret -> grpSecret.getGroup().equalsIgnoreCase(group))
                    .findFirst();

            if (!groupSecretOptional.isPresent()) {
                log.warn(String.format("Could not authenticate token. Group %s is not valid.", group));
                return null;
            }

            // validate claims and token if authentic using group secret
            if (!JwtGenerator.validateToken(token, claims, groupSecretOptional.get().getSecret())) {
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
