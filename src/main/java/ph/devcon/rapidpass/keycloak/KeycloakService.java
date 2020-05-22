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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;

/**
 * The {@link KeycloakService} class services keycloak operations.
 *
 * @author darren sapalo
 * @author jonasespelita@gmail.com
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class KeycloakService {

    private final KeycloakConfig keycloakConfig;
    private final Keycloak keycloakClient;

    private String realm;

    @PostConstruct
     void posConstruct() {
        realm = keycloakConfig.getRealm();
    }

    /**
     * Creates a new user with username in keycloak.
     *
     * @param username username to create
     * @param password password to bind with user
     */
    public void createUser(String username, String password) {
        log.debug("registering user {}", username);
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(password);

        UserRepresentation user = new UserRepresentation();
        user.setUsername(username);
        user.setCredentials(Collections.singletonList(credential));
        user.setRealmRoles(Collections.singletonList("inspector"));
        user.setGroups(Collections.singletonList("INSPECTOR"));
        user.setEnabled(true);

        UsersResource users = keycloakClient
                .realm(realm)
                .users();

        users.create(user);

        log.debug("registered user {}", username);
    }

    /**
     * @param username username to check
     * @return true if username exists, false otherwise
     */
    public boolean userExists(String username) {
        List<UserRepresentation> users = keycloakClient
                .realm(realm)
                .users()
                .search(username);

        return users.size() > 0;
    }

    /**
     * Unregisters a user from keycloak.
     *
     * @param username username to unregister
     */
    public void unregisterUser(String username) {
        log.debug("unregistering user {}", username);

        List<UserRepresentation> users = keycloakClient.realm(realm)
                .users()
                .search(username);

        users.stream()
                .map(UserRepresentation::getId)
                .findFirst()
                .ifPresent(id -> {
                    keycloakClient.realm(realm)
                            .users().delete(id);
                    log.debug("unregistered user {}", username);
                });
    }
}
