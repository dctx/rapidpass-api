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

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * This tests {@link SimpleRbacConfig}.
 *
 * @author jonasespelita@gmail.com
 */
@SpringBootTest(classes = {SimpleRbacConfig.class})
@EnableConfigurationProperties
@Slf4j
class SimpleRbacConfigTest {
    @Autowired
    SimpleRbacConfig simpleRbacConfig;

    @Test
    void loadConfigProps() {
        log.info("loaded rbac {}", simpleRbacConfig);
        assertThat(simpleRbacConfig.getRoles(), is(not(empty())));

    }

    @Test
    void getRbacRoleMatch_01() {
        final List<SimpleRbacConfig.RbacRole> test =
                simpleRbacConfig.getRbacRoleMatch("/test/registry/access-passes/12345", "DELETE");
        assertThat(test.get(0).getRole(), is("approver"));


    }


    @Test
    void getRbacRoleMatch_02() {
        final List<SimpleRbacConfig.RbacRole> test =
                simpleRbacConfig.getRbacRoleMatch("/test/registry/access-passes", "GET");
        assertThat(test, is(not(empty())));
        assertThat(test.get(0).getRole(), is("approver"));
    }

    @Test
    void getRbacRoleMatch_03() {
        final List<SimpleRbacConfig.RbacRole> test =
                simpleRbacConfig.getRbacRoleMatch("/test/registry/access-passes/12345", "GET");
        assertThat("GET /registry/access-passes/12345 should be public", test, is(empty()));

    }
}