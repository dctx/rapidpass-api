package ph.devcon.rapidpass.filters;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ph.devcon.rapidpass.config.SimpleRbacConfig;
import ph.devcon.rapidpass.config.SimpleRbacConfig.RbacRole;

import javax.annotation.PostConstruct;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * The {@link RbacAuthorizationFilter} class processes an authenticated request and checks granted authorities against
 * rbac properties extracted by {@link SimpleRbacConfig} to do rbac authorization.
 *
 * @author jonasespelita@gmail.com
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RbacAuthorizationFilter extends OncePerRequestFilter {

    private final SimpleRbacConfig simpleRbacConfig;

    /**
     * @param request          request match roles
     * @param simpleRbacConfig rbac config used for roles
     * @return all configured roles that covers the request
     */
    static List<RbacRole> getEndpointRbacRoles(HttpServletRequest request, SimpleRbacConfig simpleRbacConfig) {
        final String requestURI = request.getRequestURI();
        final String method = request.getMethod();
        return simpleRbacConfig.getRbacRoleMatch(requestURI, method);
    }

    @PostConstruct
    void postConstruct() {
        log.info("RbacAuthorizationFilter initialized!");
    }

    /**
     * Does RBAC authorization based on {@link SimpleRbacConfig}.
     *
     * @param request     http request
     * @param response    http respoinse
     * @param filterChain filter chain
     * @throws IOException      on error processing the filter
     * @throws ServletException on error processing the filter
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        log.debug("RbacAuthorizationFilter called.");

        // get all configured roles that covers the request
        final List<RbacRole> authorizedRoles = getEndpointRbacRoles(request, simpleRbacConfig);
        log.debug("roles covering {}: {}", request.getRequestURI(), authorizedRoles);


        if (authorizedRoles.isEmpty()) {
            log.debug("Request is not covered by RBAC. Continuing filter chain.");
            filterChain.doFilter(request, response);
            return;
        }

        // check if current role is part of authorized roles
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            log.warn("Unauthenticated request trying to authorize to {} {}", request.getMethod(), request.getRequestURI());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "No Authorization Found!");
            return;
        }

        final boolean isAuthorized =
                authentication.getAuthorities().stream()
                        .anyMatch(authority -> authorizedRoles.stream()
                                .map(RbacRole::getRole)
                                .anyMatch(authorizedRole ->
                                        // check if authorized role matches authority from jwt filter
                                        authorizedRole.equalsIgnoreCase(authority.getAuthority())));
        if (!isAuthorized) {
            log.warn("Unauthorized access by {} to {} {}", authentication.getPrincipal(), request.getMethod(), request.getRequestURI());
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Your group " + authentication.getAuthorities() + " is not permitted to access this resource.");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
