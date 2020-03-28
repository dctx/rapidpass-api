package ph.devcon.rapidpass.filters;

import org.apache.commons.codec.binary.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class AuthenticationFilter implements Filter {

    @Value("${enable.rapid.pass.api.key}")
    private boolean rapidPassApiKeyEnabled;

    @Value("${rapid.pass.api.key}")
    private String rapidPassApiKey;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String rapidPassKey = req.getHeader("RP-API-KEY");
        if (this.rapidPassApiKeyEnabled && (null == rapidPassKey || !StringUtils.equals(this.rapidPassApiKey, rapidPassKey))) {
            res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "RP-API-KEY in request header is missing or invalid!");
            return;
        }

        chain.doFilter(req, res);
    }
}