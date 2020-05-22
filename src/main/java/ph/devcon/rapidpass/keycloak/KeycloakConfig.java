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

package ph.devcon.rapidpass.keycloak;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Keycloak configuration.
 *
 * Utilised the keycloak-api namespace instead, because keycloak is used by the keycloak adapter.
 *
 * See: https://github.com/keycloak/keycloak/blob/master/adapters/oidc/spring-boot-adapter-core/src/main/java/org/keycloak/adapters/springboot/KeycloakSpringBootProperties.java
 *
 * @author jonasespelita@gmail.com
 */
@Configuration
@ConfigurationProperties("keycloak-api")
@Getter
@Setter
@Slf4j
public class KeycloakConfig {

    private static final String MASTER_REALM = "master";
    private static final String ADMIN_CLIENT = "admin-cli";

    @Value("${keycloak.auth-server-url}")
    private String authServerUrl;
    @Value("${keycloak.realm}")
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
                realm,
                apiUsername,
                apiPassword,
                ADMIN_CLIENT);
    }
}
