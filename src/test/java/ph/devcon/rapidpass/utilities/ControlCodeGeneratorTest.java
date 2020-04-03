package ph.devcon.rapidpass.utilities;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Base64;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.fail;

public class ControlCodeGeneratorTest {

    /**
     * Generate the control code successfully.
     */
    @Test
    void generateControlCode() {
        // Currently ignoring this test, because Alistair still hasn't updated the commons to 0.2.0

        String key = "1234567890";

        byte[] keyInBase64Bytes = Base64.getEncoder().encode(key.getBytes());

        String keyInBase64 = new String(keyInBase64Bytes);

        String controlCode = ControlCodeGenerator.generate(keyInBase64, 25);

        assertThat(controlCode, equalTo("19X42YXG"));
    }

    /**
     * Keys need to be at least 10 characters.
     */
    @Test
    void throwErrorIfKeyIsTooShort() {
        String key = "1234567";

        byte[] keyInBase64Bytes = Base64.getEncoder().encode(key.getBytes());

        String keyInBase64 = new String(keyInBase64Bytes);

        try {
            String controlCode = ControlCodeGenerator.generate(keyInBase64, 25);
            fail("Expected exception did not throw");
        }catch (IllegalArgumentException e) {
            String message = e.getMessage();

            assertThat(message, containsString("key should be at least 10 characters"));
        }


    }

    /**
     * Generated control codes shouldn't begin with 0000. This might be a red flag for the code generation to
     * have problems.
     */
    @Test
    void generatedKeysDoNotStartWith0000() {
        String key = "someSecretKey!6034:#@;";

        byte[] keyInBase64Bytes = Base64.getEncoder().encode(key.getBytes());

        String keyInBase64 = new String(keyInBase64Bytes);

        for (int i = 0; i < 50; i++) {
            String controlCode = ControlCodeGenerator.generate(keyInBase64, i);
            if (controlCode.startsWith("0000"))
                fail("Control codes shouldn't start with 0000s.");
        }
    }

    /**
     * Tests a small number of integers to be encoded as control codes, to test whether the codes are unique.
     *
     * Not a comprehensive test, but generally we want to see if there are outlier values that cause the generator
     * to repeatedly spit out the same number.
     *
     * At least in the span of N entries, it is unique.
     */
    @Test
    void controlCodeAreUnique() {

        int n = 1500;

        int start = 1000;
        int end = start + n;

        String key = "someSecretKey!6034:#@;";

        byte[] keyInBase64Bytes = Base64.getEncoder().encode(key.getBytes());

        String keyInBase64 = new String(keyInBase64Bytes);

        ArrayList arrayList = new ArrayList<>();


        for (int i = start; i < end; i++) {
            String controlCode = ControlCodeGenerator.generate(keyInBase64, i);
            if (arrayList.contains(controlCode))
                fail("Found a conflict in control codes");
            else
                arrayList.add(controlCode);
        }
    }

}
