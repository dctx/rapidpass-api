package ph.devcon.rapidpass.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.Arrays;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    @Value("security.cors.allowed-origins")
    private ArrayList<String> allowedOrigins;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // For now, the only part that needs CORS is the log in for the approver.
        // Inside, we utilise JWT for authentication.
        CorsRegistration corsRegistration = registry.addMapping("/user/auth");
        String[] origins = Arrays.copyOf(allowedOrigins.toArray(), allowedOrigins.toArray().length, String[].class);
        corsRegistration.allowedOrigins(origins);
    }
}
