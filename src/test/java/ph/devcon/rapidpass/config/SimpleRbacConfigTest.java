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
                simpleRbacConfig.getRbacRoleMatch("/registry/access-passes/12345", "DELETE");
        assertThat(test.get(0).getRole(), is("approver"));


    }


    @Test
    void getRbacRoleMatch_02() {
        final List<SimpleRbacConfig.RbacRole> test =
                simpleRbacConfig.getRbacRoleMatch("/registry/access-passes", "GET");
        assertThat(test, is(not(empty())));
        assertThat(test.get(0).getRole(), is("approver"));
    }

    @Test
    void getRbacRoleMatch_03() {
        final List<SimpleRbacConfig.RbacRole> test =
                simpleRbacConfig.getRbacRoleMatch("/registry/access-passes/12345", "GET");
        assertThat("GET /registry/access-passes/12345 should be public", test, is(empty()));

    }
}