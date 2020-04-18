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