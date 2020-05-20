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

package ph.devcon.rapidpass.services.notifications;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@Qualifier("keycloak")
@Slf4j
@RequiredArgsConstructor
@Setter
public class KeycloakService {

    @Value("${keycloak.auth-server-url}")
    private String server;

    @Value("${keycloak.realm}")
    private String realm;

    private String clientID = "rapidpass-api";

    @Value("${keycloak.credentials.secret}")
    private String authToken;

    @Value("${keycloak.credentials.apiUser}")
    private String username;

    @Value("${keycloak.credentials.apiPass}")
    private String password;

    public void registerUser(String imei, String password) {
        Keycloak kc = getKeycloakInstance();

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(password);

        UserRepresentation user = new UserRepresentation();
        user.setUsername(imei);
        user.setCredentials(Collections.singletonList(credential));
        user.setEnabled(true);

        UsersResource users = kc.realm(this.realm).users();
        users.create(user);
    }

    public boolean userExists(String imei) {
        Keycloak kc = getKeycloakInstance();

        List<UserRepresentation> users = kc.realm(this.realm).users().search(imei);

        return users.size() > 0;
    }

    public void unregisterUser(String imei) {
        Keycloak kc = getKeycloakInstance();

        List<UserRepresentation> users = kc.realm(this.realm).users().search(imei);

        users.stream()
            .map(UserRepresentation::getId)
            .findFirst().ifPresent(kc.realm(this.realm).users()::delete);
    }


    public Keycloak getKeycloakInstance() {
        return Keycloak.getInstance(server, realm, username, password, clientID);
    }

}
