package ph.devcon.rapidpass.utilities;

import io.jsonwebtoken.SignatureAlgorithm;
import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.platform.commons.logging.LoggerFactory;
import org.postgresql.shaded.com.ongres.scram.common.bouncycastle.pbkdf2.HMac;
import ph.devcon.dctx.rapidpass.commons.HmacSha256;
import ph.devcon.dctx.rapidpass.commons.Signer;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

public class JwtGeneratorTest {

    private Logger log = Logger.getLogger(String.valueOf(JwtGeneratorTest.class));
    JwtGenerator generator;

    @BeforeEach
    void setUp() {
        generator = new JwtGenerator();
    }

    @Test
    void generateToken() throws Exception {
        String secret = "rapidpass";
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", "12314");
        claims.put("name", "Kevin Smith");
        claims.put("group", "registrant");

        log.info(claims.toString());
        String token = generator.generateToken(claims, secret);
        assertNotNull(token);
    }

    @Test
    void validateToken() throws Exception {
        String secret = "rapidpass";
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", "12341");
        claims.put("name", "Kevin Locke");
        claims.put("group", "registrant");

        String token = generator.generateToken(claims, secret);
        assertTrue(JwtGenerator.validateToken(token, claims, secret));
    }
}
