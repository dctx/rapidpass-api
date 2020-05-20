package ph.devcon.rapidpass.keycloak;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.ServerInfoResource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration test for keycloak service. Requires a configured keycloak instance to be setup locally.
 *
 * @author jonasespelita@gmail.com
 */
@Slf4j
class KeycloakServiceIT {
    KeycloakConfig keycloakConfig;
    Keycloak keycloakClient;

    KeycloakService keycloakService;

    @BeforeEach
    void setUp() {
        // try to connect to local keycloak instance
        keycloakConfig = new KeycloakConfig();
        keycloakConfig.setRealm("rapidpass");
        keycloakConfig.setAuthServerUrl("http://localhost:8180/auth/");
        keycloakConfig.setApiUsername("admin");
        keycloakConfig.setApiPassword("admin");

        keycloakClient = keycloakConfig.keycloakClient();

        keycloakService = new KeycloakService(keycloakConfig, keycloakClient);
        keycloakService.posConstruct();
    }

    @Test
    void configuration() {
        final ServerInfoResource serverInfoResource = keycloakClient.serverInfo();
        final String version = serverInfoResource.getInfo().getSystemInfo().getVersion();

        log.debug("running keycloak version {}", version);
    }

    @Test
    void createUser() {
        final String username = "my-unit-test";
        keycloakService.createUser(username, username);
        assertTrue(keycloakService.userExists(username));
        keycloakService.unregisterUser(username);
        assertFalse(keycloakService.userExists(username));
    }
}