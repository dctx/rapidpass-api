/*
 * Copyright (c) 2020.  DevConnect Philippines, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and limitations under the License.
 */

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
