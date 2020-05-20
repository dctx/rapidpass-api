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