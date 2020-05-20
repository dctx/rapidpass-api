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