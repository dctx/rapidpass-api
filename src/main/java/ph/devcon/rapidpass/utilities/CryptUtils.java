package ph.devcon.rapidpass.utilities;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

/**
 * CryptUtils is a collection of functions that are related to hashing, encryption, decryption, etc.
 */
public class CryptUtils {

    private static final int SALT_LENGTH = 6;
    private static final String SECURE_RANDOM_ALGORITHM = "SHA1PRNG";
    private static final String SECRET_KEY_FACTORY_NAME = "PBKDF2WithHmacSHA1";
    private static final int ITERATIONS = 1000;
    private static final int KEY_LENGTH = 512;

    private CryptUtils() {}

    /**
     * passwordHash uses the PBKDF2 Hashing algorithm. The hashed value contains a prefix of random salt used
     *
     * @param value the value to hash
     * @return a PBKDF2 hashed string
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public static final String passwordHash(final String value) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return passwordHash(value, randomSalt());
    }

    /**
     * passwordCompare compares a hashed value and a password. The password will be hashed using the same salt and will
     * be compared to the existing hash.
     *
     * @param hashedValue the hashed value
     * @param value the unhashed value to compare
     * @return
     * @throws DecoderException
     * @throws InvalidKeySpecException
     * @throws NoSuchAlgorithmException
     */
    public static final boolean passwordCompare(final String hashedValue, final String value) throws DecoderException, InvalidKeySpecException, NoSuchAlgorithmException {
        final String saltHex = hashedValue.substring(0, SALT_LENGTH * 2);
        final byte[] salt = Hex.decodeHex(saltHex);
        final String newHashedValue = passwordHash(value, salt);
        return hashedValue.equals(newHashedValue);
    }

    private static final String passwordHash(final String value, final byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        final char[] chars = value.toCharArray();
        final PBEKeySpec spec = new PBEKeySpec(chars, salt, ITERATIONS, KEY_LENGTH);
        final SecretKeyFactory skf = SecretKeyFactory.getInstance(SECRET_KEY_FACTORY_NAME);
        final byte[] hash = skf.generateSecret(spec).getEncoded();
        return Hex.encodeHexString(salt) + Hex.encodeHexString(hash);
    }

    private static final byte[] randomSalt() throws NoSuchAlgorithmException {
        final SecureRandom sr = SecureRandom.getInstance(SECURE_RANDOM_ALGORITHM);
        final byte[] salt = new byte[SALT_LENGTH];
        sr.nextBytes(salt);
        return salt;
    }

}

