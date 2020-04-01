package ph.devcon.rapidpass.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

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
            if (secret.group == group) {
                return secret.secret;
            }
        }
        return null;
    }
}
