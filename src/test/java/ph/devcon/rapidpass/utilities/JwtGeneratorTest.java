package ph.devcon.rapidpass.utilities;

import com.auth0.jwt.interfaces.DecodedJWT;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import static org.hamcrest.Matchers.is;

public class JwtGeneratorTest {

    private Logger log = Logger.getLogger(String.valueOf(JwtGeneratorTest.class));
    JwtGenerator generator;

    @BeforeEach
    void setUp() {
        generator = new JwtGenerator();
    }

    @Test
    void generateToken() throws Exception {
        String secret = "checkpoint-secret";
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", "12314");
        claims.put("name", "Kevin Smith");
        claims.put("group", "checkpoint");

        log.info(claims.toString());
        String token = JwtGenerator.generateToken(claims, secret);
        log.info(token);
        Assertions.assertNotNull(token);
    }

    @Test
    void validateToken() throws Exception {
        String secret = "rapidpass";
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", "12341");
        claims.put("name", "Kevin Locke");
        claims.put("group", "registrant");

        String token = JwtGenerator.generateToken(claims, secret);
        Assertions.assertTrue(JwtGenerator.validateToken(token, claims, secret));
    }

    @Test
    void getClaims() {
        final Map<String, Object> claims = new HashMap<>();

        claims.put("group", "registrant");
        claims.put("sub", "test");

        final String token = JwtGenerator.generateToken(claims, "not_so_secret_secret");
        System.out.println("token = " + token);
        final DecodedJWT decodedJWT = JwtGenerator.decodedJWT(token);
        final String decodedGroup = decodedJWT.getClaims().get("group").asString();
        MatcherAssert.assertThat(decodedGroup, is("registrant"));
    }
}
