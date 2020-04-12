package ph.devcon.rapidpass.utilities;

import com.boivie.skip32.Skip32;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import ph.devcon.dctx.rapidpass.commons.CrockfordBase32;
import ph.devcon.dctx.rapidpass.commons.Damm32;
import ph.devcon.rapidpass.entities.AccessPass;

import javax.persistence.Transient;
import java.util.Base64;

public class ControlCodeGenerator {

    /**
     * Generates a control code.
     *
     * @param qrCodeSecretKeyInBase64 This is a unique pass phrase which can be configured using @value. This string value
     *                       should be a secret key should be encoded in base64 format.
     *
     * @param id            The id of the access pass being generated. This should be unique, so make sure you create the
     *                      {@link AccessPass} first, then retrieve its ID, then use that as a parameter for generating the control
     *                      code of the AccessPass.
     *
     * @throws              IllegalArgumentException if the qrCodeSecretKeyInBase64 is not encoded in base64 correctly.
     * @throws              IllegalArgumentException if the decoded qrCodeSecretKey is less than 10 characters.
     *
     * @return A control code in string format.
     */
    public static String generate(String qrCodeSecretKeyInBase64, int id) {

        // The secret key in base 64 format.
        byte[] qrCodeSecretKey = Base64.getDecoder().decode(qrCodeSecretKeyInBase64);

        if (qrCodeSecretKey.length < 10) {
            /*
             * The qrCodeSecretKey should be at least 10 characters, as this is required by Skip32.
             */
            throw new IllegalArgumentException("Failed to generate QR code because the secret is not configured properly (key should be at least 10 characters, found: " + qrCodeSecretKey.length + " length)");
        }

        // The obfuscated unsigned integer (represented as a long, because Java doesn't have uint)
        long obfuscatedId = ((long) Skip32.encrypt(id, qrCodeSecretKey)) & 0xffffffffL;

        // Generating the control code as a string, with 7 parts of a hash + 1 part check digit
        int checkDigit = Damm32.compute(obfuscatedId);
        return CrockfordBase32.encode(obfuscatedId, 7) + CrockfordBase32.encode(checkDigit);
    }

    public static int decode(String qrCodeSecretKeyInBase64, String controlCode) {

        // The secret key in base 64 format.
        byte[] qrCodeSecretKey = Base64.getDecoder().decode(qrCodeSecretKeyInBase64);

        if (qrCodeSecretKey.length < 10) {
            /*
             * The qrCodeSecretKey should be at least 10 characters, as this is required by Skip32.
             */
            throw new IllegalArgumentException("Failed to generate QR code because the secret is not configured properly (key should be at least 10 characters, found: " + qrCodeSecretKey.length + " length)");
        }

        int length = controlCode.length();
        String base = controlCode.substring(0, length - 1);
        String actual = controlCode.substring(length - 1, length);

        long obfuscatedId = CrockfordBase32.decode(base);

        int decryptedNumber = Skip32.decrypt((int) obfuscatedId, qrCodeSecretKey);

        return decryptedNumber;

    }
}