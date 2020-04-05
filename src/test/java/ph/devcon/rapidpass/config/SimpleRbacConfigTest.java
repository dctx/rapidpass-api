package ph.devcon.rapidpass.config;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;

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
        log.info("got {}", simpleRbacConfig);
        assertThat(simpleRbacConfig.getRoles(), is(not(empty())));

    }


}