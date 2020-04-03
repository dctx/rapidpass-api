package ph.devcon.rapidpass.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import ph.devcon.rapidpass.utilities.JwtGenerator;

import java.util.List;

/**
 * The {@link JwtSecretsConfig} configuration class contains the JWT secrets for the various groups. Values are configured in yaml properties:
 *
 * <pre>
 * jwt:
 *   secrets:
 *     - group: checkpoint
 *       secret: checkpoint-secret
 *     - group: approver
 *       secret: approver-secret
 * </pre>
 *
 * @author jonasespelita@gmail.com
 */
@Configuration
@Data
@ConfigurationProperties(prefix = "jwt")
public class JwtSecretsConfig {

    private static Logger log = LoggerFactory.getLogger(JwtGenerator.class);
    /**
     * List of groups and their secrets.
     */
    private List<JwtGroupSecret> secrets;

    /**
     * Models a group secret.
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class JwtGroupSecret {
        private String group;
        private String secret;
    }

    public String findGroupSecret(final String group) {
        for (final JwtGroupSecret secret : this.secrets) {
            if (secret.group.equals(group)) {
                return secret.secret;
            }
        }
        return null;
    }
}