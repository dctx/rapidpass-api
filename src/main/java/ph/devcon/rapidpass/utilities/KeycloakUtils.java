package ph.devcon.rapidpass.utilities;

import org.keycloak.KeycloakPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Map;

import static java.util.stream.Collectors.toMap;

/**
 * The {@link KeycloakUtils} utility class contains helper methods for keycloak related operations.
 *
 * @author jonasespelita@gmail.com
 */
public class KeycloakUtils {

    private KeycloakUtils() {
        // noop utility class
    }

    /**
     * @return keycloak attributes for logged in user via access token
     */
    public static Map<String, String> getAttributes() {
        final Object rawPrincipal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(rawPrincipal instanceof KeycloakPrincipal)) {
            throw new IllegalStateException("  no keycloak login found!");
        }

        final KeycloakPrincipal principal = (KeycloakPrincipal) rawPrincipal;
        final Map<String, Object> attributes = principal.getKeycloakSecurityContext().getToken().getOtherClaims();

        return attributes.entrySet().stream()
                .collect(toMap(Map.Entry::getKey,
                        entry -> entry.getValue().toString()));
    }
}
