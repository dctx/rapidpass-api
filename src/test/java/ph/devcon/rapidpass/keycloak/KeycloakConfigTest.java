package ph.devcon.rapidpass.keycloak;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

/**
 * This tests {@link KeycloakConfig} and {@link KeycloakService} autoconfigurations. Uses application-test.yaml
 *
 * @author jonasespelita@gmail.com
 */
@SpringBootTest(classes = {KeycloakConfig.class, KeycloakService.class})
@EnableConfigurationProperties
class KeycloakConfigTest {

    @Autowired
    KeycloakConfig keycloakConfig;

    @Autowired
    KeycloakService keycloakService;

    @Test
    void testAutoconfiguration() {
        assertThat(keycloakConfig.getAuthServerUrl(), is("http://localhost:8180/auth/"));
        assertThat(keycloakConfig.getRealm(), is("rapidpass"));
        assertThat(keycloakConfig.getApiUsername(), is("admin"));
        assertThat(keycloakConfig.getApiPassword(), is("admin"));

        assertThat(keycloakService, is(notNullValue()));
    }
}