package ph.devcon.rapidpass.filters;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class ApiKeyFilter implements Filter {  // jje - prefer a security filter but good enough for now

    @Value("${security.enabled:false}")
    private boolean securityEnabled = false;

    @Value("${rapidpass.auth.apiKey.enabled:secret}")
    private boolean rapidPassApiKeyEnabled;

    @Value("${rapidpass.auth.apiKey.key:secret}")
    private String rapidPassApiKey;

    @Value("${rapidpass.auth.apiKey.header:RP-API-KEY}")
    private String apiKeyHeader;

    private static final List<String> exclusions = new ArrayList<>();

    public ApiKeyFilter() {
        exclusions.add("/api/v1/spec");
        exclusions.add("/api/v1/swagger-ui.html");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        if (!securityEnabled) {
            log.debug("Skipping API Code Filter due to security.enabled:false");
            chain.doFilter(req, res);
            return;
        }

        log.debug("Running API Code Filter");

        final String requestURI = req.getRequestURI();
        log.debug("uri: {}", requestURI);
        boolean isExcluded = CollectionUtils.contains(exclusions.iterator(), requestURI);

        if (!isExcluded) {
            log.debug("uri not in exclusion list, checking api code");

            String rapidPassKey = req.getHeader(apiKeyHeader);
            if (this.rapidPassApiKeyEnabled && (null == rapidPassKey || !StringUtils.equals(this.rapidPassApiKey, rapidPassKey))) {
                res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "API Key is missing or invalid!");
                return;
            }
        }

        chain.doFilter(req, res);
    }
}
