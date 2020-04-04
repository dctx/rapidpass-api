package ph.devcon.rapidpass.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.AntPathMatcher;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

/**
 * The {@link SimpleRbacConfig} configuration class defines a simplified model for rbac security.
 * Currently supports role to endpoint access control configuration with verb bindings.
 *
 * <pre>
 * rbac:
 *   roles:
 *     - role: approver
 *       resources:
 *         - endpoint: /approver/**
 *           verbs:
 *             - ALL
 *     - role: checkpoint
 *       resources:
 *         - endpoint: /checkpoint/**
 *           verbs:
 *             - ALL
 * </pre>
 *
 * @author jonasespelita@gmail.com
 */
@Configuration
@ConfigurationProperties(prefix = "rbac")
@Data
public class SimpleRbacConfig {

    /**
     * Spring utility for matching ANT paths.
     */
    private static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();

    /**
     * List of role configurations.
     */
    private List<RbacRole> roles;

    public List<String> getAllRegisteredEndpoints() {
        return roles.stream()
                .flatMap(role ->
                        role.getResources().stream())
                .map(RbacResource::getEndpoint)
                .collect(toList());
    }

    /**
     * Gets the roles that matches a given URI and verb.
     *
     * @param requestURI request URI
     * @param method     request method
     * @return list of roles that matches the uri and method.
     */
    public List<RbacRole> getRbacRoleMatch(
            String requestURI,
            String method) {
        return roles
                .stream()
                .filter(role -> {
                    // check if request endpoint is covered by rbac
                    final Optional<RbacResource> optResource = role.getResources().stream()
                            .filter(resource -> ANT_PATH_MATCHER.match(resource.getEndpoint(), requestURI))
                            .findFirst();
                    // check if request method is covered by rbac
                    return optResource.map(rbacResource -> rbacResource.getVerbs().stream()
                            .anyMatch(verb -> "ALL".equalsIgnoreCase(verb) ||
                                    verb.equalsIgnoreCase(method)))
                            .orElse(false);
                }).collect(toList());
    }

    /**
     * List of supported {@link RbacResource} verbs. Notice inclusion of ALL as utility catcher for all verbs
     */
    enum RbacResourceVerbs {
        ALL,
        PUT,
        DELETE,
        GET,
        POST
    }

    /**
     * Models an RBAC Role and its authorized resources.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RbacRole {
        private String role;
        private List<RbacResource> resources;
    }

    /**
     * Models and RBAC Resource: an endpoint and authorized verbs
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RbacResource {
        private String endpoint;
        private List<String> verbs;
    }

}
