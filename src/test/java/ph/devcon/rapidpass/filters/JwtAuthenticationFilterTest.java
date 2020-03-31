package ph.devcon.rapidpass.filters;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ph.devcon.rapidpass.utilities.JwtGenerator;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests JwtAuthenticationFilter.
 *
 * @author jonasespelita@gmail.com
 */
@WebMvcTest(controllers = JwtAuthenticationFilterTest.TestSecurityConfig.class)
class JwtAuthenticationFilterTest {
    static final String SECRET = "secret";
    @Autowired
    MockMvc mockMvc;

    @Test
    public void test_WITH_VALID_JWT() throws Exception {
        // generate a jwt
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", "12314");
        claims.put("name", "Kevin Smith");
        claims.put("group", "registrant");

        String token = JwtGenerator.generateToken(claims, SECRET);

        mockMvc.perform(get("/hello")
                .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("hello"));
    }

    @Test
    public void test_NO_JWT() throws Exception {
        mockMvc.perform(get("/hello"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @RestController
    @Configuration
    static class TestSecurityConfig extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            final JwtAuthenticationFilter filter = new JwtAuthenticationFilter();
            filter.setJwtSecret(SECRET);
            filter.postConstruct();

            http.csrf().disable() // just to simplify things
                    .addFilterAfter(filter, AbstractPreAuthenticatedProcessingFilter.class)
                    .authorizeRequests()
                    // authenticating errything!
                    .anyRequest()
                    .authenticated();
        }

        @GetMapping("/hello")
        public String hello() {
            return "hello";
        }
    }


}