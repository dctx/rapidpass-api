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
public class ApiKeyFilter implements Filter {

    @Value("${rapidpass.auth.apiKey.enabled}")
    private boolean rapidPassApiKeyEnabled;

    @Value("${rapidpass.auth.apiKey.key}")
    private String rapidPassApiKey;

    private static final List<String> exclusions = new ArrayList<>();

    public ApiKeyFilter() {
        exclusions.add("/api/v1/spec");
        exclusions.add("/api/v1/swagger-ui.html");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        log.info("Running API Code Filter");
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        final String requestURI = req.getRequestURI();
        log.info("uri: {}", requestURI);
        boolean isExcluded = CollectionUtils.contains(this.exclusions.iterator(), requestURI);

        if (!isExcluded) {
            log.info("uri not in exclusion list, checking api code");
            String rapidPassKey = req.getHeader("RP-API-KEY");
            if (this.rapidPassApiKeyEnabled && (null == rapidPassKey || !StringUtils.equals(this.rapidPassApiKey, rapidPassKey))) {
                res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "API Key is missing or invalid!");
                return;
            }
        }

        chain.doFilter(req, res);
    }
}
