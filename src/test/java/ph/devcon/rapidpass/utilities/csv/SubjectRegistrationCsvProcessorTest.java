package ph.devcon.rapidpass.utilities.csv;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import ph.devcon.rapidpass.exceptions.CsvColumnMappingMismatchException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.fail;

public class SubjectRegistrationCsvProcessorTest {

    /**
     * Currently the file that Jeffrey gave me is able to trigger the error, but the test case that we have
     * 'data-with-newline-in-cell' does not reproduce the fail/error.
     *
     * tl;dr this unit test doesn't reliably reproduce the error. Talk to Darren or Jeffrey and look for the
     * file documented at https://gitlab.com/dctx/rapidpass/rapidpass-api/-/issues/406
     *
     * @throws IOException
     */
    @Test
    void handleNewLine() throws IOException {
        SubjectRegistrationCsvProcessor subjectRegistrationCsvProcessor = new SubjectRegistrationCsvProcessor();

        final String filename = "data-with-newline-in-cell.csv";

        ClassPathResource instructionsClassPath = new ClassPathResource(filename);
        byte[] byteContent = toByteArray(instructionsClassPath.getInputStream());

        try {
            subjectRegistrationCsvProcessor.process(new MockMultipartFile(filename, byteContent));
        } catch (CsvColumnMappingMismatchException e) {
            System.err.println(e);
            fail("Did not handle new line.");
        }  catch (Exception e) {
            System.err.println(e);
            fail("Did not handle new line");
        }
    }

    private static byte[] toByteArray(InputStream in) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        byte[] buffer = new byte[1024];
        int len;

        // read bytes from the input stream and store them in buffer
        while ((len = in.read(buffer)) != -1) {
            // write bytes from the buffer into output stream
            os.write(buffer, 0, len);
        }

        return os.toByteArray();
    }
}
