package ph.devcon.rapidpass.keycloak;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Keycloak configuration
 *
 * @author jonasespelita@gmail.com
 */

@Configuration
@ConfigurationProperties("keycloak")
@Getter
@Setter
@Slf4j
public class KeycloakConfig {

    private static final String MASTER_REALM = "master";
    private static final String ADMIN_CLIENT = "admin-cli";

    private String authServerUrl;
    private String realm;
    private String apiUsername;
    private String apiPassword;

    /**
     * @return a keycloak client configured to connect to configured servers
     */
    @Bean
    public Keycloak keycloakClient() {
        log.info("Connecting to keycloak server: {}", authServerUrl);

        return Keycloak.getInstance(
                authServerUrl,
                MASTER_REALM,
                apiUsername,
                apiPassword,
                ADMIN_CLIENT);
    }
}
