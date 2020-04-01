package ph.devcon.rapidpass.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import ph.devcon.rapidpass.config.JwtSecretsConfig.JwtGroupSecret;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * This tests if JwtSecretsConfig supports @ConfigurationProperties.
 *
 * @author jonasespelita@gmail.com
 */
@SpringBootTest(classes = {JwtSecretsConfig.class})
@EnableConfigurationProperties
class JwtSecretsConfigTest {
    @Autowired
    JwtSecretsConfig jwtSecretsConfig;

    @Test
    void properties() {
        // this loads props from : application-test.yaml
        final List<JwtGroupSecret> secrets = jwtSecretsConfig.getSecrets();
        assertThat(secrets, is(not(emptyIterable())));
        assertThat("jwt secrets are parsed correctly!",
                secrets, containsInAnyOrder(
                        new JwtGroupSecret("checkpoint", "checkpoint-secret"),
                        new JwtGroupSecret("approver", "approver-secret")));
    }
}