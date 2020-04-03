package ph.devcon.rapidpass.utilities;


import org.apache.commons.codec.DecoderException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static ph.devcon.rapidpass.utilities.CryptUtils.passwordCompare;
import static ph.devcon.rapidpass.utilities.CryptUtils.passwordHash;

public class CryptUtilsTest {

    private static Logger log = LoggerFactory.getLogger(CryptUtils.class);

    @Test
    void TestHash() {
        final String password = "this is my password";
        try {
            final String hash = passwordHash(password);
            log.info("computed hash: " + hash);
            log.info("computed hash lenth: " + hash.length());
            assertTrue(hash.length() <= 140);
            final boolean success = passwordCompare(hash, password);
            log.info("successful recomputed hash");
            assertTrue(success);
        } catch (NoSuchAlgorithmException e) {
            Assertions.fail(e);
        } catch (InvalidKeySpecException e) {
            Assertions.fail(e);
        } catch (DecoderException e) {
            Assertions.fail(e);
        }
    }

}
