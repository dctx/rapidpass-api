package ph.devcon.rapidpass.filters;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class ResponseHeaderSecurityFilter implements Filter {

    @Value("${security.csp.srcs.script:*.rapidpass.ph rapidpass.ph}")
    private String source;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        response.addHeader("Strict-Transport-Security", "max-age=15552000; includeSubDomains; preload");
        response.addHeader("Content-Security-Policy", "default-src 'self'; script-src 'report-sample' 'self' https://www.googletagmanager.com/gtag/js; style-src 'report-sample' 'self' https://fonts.googleapis.com; object-src 'none'; base-uri 'self'; connect-src 'self' " + source + "; font-src 'self' https://fonts.gstatic.com; frame-src 'self'; img-src 'self'; manifest-src 'self'; media-src 'self'; worker-src 'none';");
        response.addHeader("X-Frame-Options", "SAMEORIGIN");
        response.addHeader("X-Content-Type-Options", "nosniff");
        response.addHeader("cache-control", "max-age=0, private, must-revalidate");
        response.addHeader("Referrer-Policy", "no-referrer");
        response.addHeader("Feature-Policy", "none");

        filterChain.doFilter(servletRequest, response);
    }

    @Override
    public void destroy() {

    }
}
