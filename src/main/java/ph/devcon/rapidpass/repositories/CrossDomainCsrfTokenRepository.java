package ph.devcon.rapidpass.repositories;

import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author jonasespelita@gmail.com
 */
public class CrossDomainCsrfTokenRepository implements CsrfTokenRepository {
    static final String DEFAULT_CSRF_COOKIE_NAME = "XSRF-TOKEN";
    private final CookieCsrfTokenRepository delegate;

    public CrossDomainCsrfTokenRepository() {
        delegate = new CookieCsrfTokenRepository();
        delegate.setCookieHttpOnly(false);
    }

    @Override
    public CsrfToken generateToken(HttpServletRequest request) {
        return delegate.generateToken(request);
    }

    @Override
    public void saveToken(CsrfToken token, HttpServletRequest request, HttpServletResponse response) {
        String tokenValue = token == null ? "" : token.getToken();
        Cookie cookie = new Cookie(DEFAULT_CSRF_COOKIE_NAME, tokenValue);
        cookie.setSecure(request.isSecure());
        cookie.setPath("/");

        if (token == null) {
            cookie.setMaxAge(0);
        } else {
            cookie.setMaxAge(86400); // set to 24 hours
        }

        cookie.setHttpOnly(false); // allow javascript access

        // set domain dynamically. TODO: whitelist server names
        cookie.setDomain("rapidpass-approver-dev.azurewebsites.net");
        response.addCookie(cookie);
    }

    @Override
    public CsrfToken loadToken(HttpServletRequest request) {
        return delegate.loadToken(request);
    }
}
