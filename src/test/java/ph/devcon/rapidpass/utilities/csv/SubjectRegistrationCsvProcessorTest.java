package ph.devcon.rapidpass.utilities.csv;

import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import ph.devcon.rapidpass.exceptions.CsvColumnMappingMismatchException;
import ph.devcon.rapidpass.models.RapidPassCSVdata;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.fail;

public class SubjectRegistrationCsvProcessorTest {

    private static String HEADERS = "passType,aporType,firstName,middleName,lastName,suffix,company,idType,identifierNumber,platenumber,mobileNumber,email,originName,originStreet,originCity,originProvince,destName,destStreet,destCity,destProvince,remarks";

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

    public List<RapidPassCSVdata> mock(String filename) throws IOException, CsvColumnMappingMismatchException, CsvRequiredFieldEmptyException {
        SubjectRegistrationCsvProcessor subjectRegistrationCsvProcessor = new SubjectRegistrationCsvProcessor();

        ClassPathResource instructionsClassPath = new ClassPathResource(filename);

        byte[] byteContent = toByteArray(instructionsClassPath.getInputStream());

        return subjectRegistrationCsvProcessor.process(new MockMultipartFile(filename, byteContent));
    }

    /**
     * The parser should be able to handle parsing a CSV whether it has too few, or too many columns.
     *
     * The excess or missing data will be handled by the {@link ph.devcon.rapidpass.utilities.validators.entities.accesspass.BatchAccessPassRequestValidator}.
     */
    @Test
    void handleIncorrectColumns() throws IOException {
        try {
            List<RapidPassCSVdata> process = mock("data-incorrect-columns.csv");

            assertThat(process.size(), equalTo(5));

            RapidPassCSVdata csvData = process.get(1);

            assertThat(csvData.getFirstName(), equalTo("Jezza"));
        } catch (CsvColumnMappingMismatchException e) {
            System.err.println(e);
            fail("Did not handle incorrect columns.");
        }  catch (Exception e) {
            System.err.println(e);
            fail("Did not handle incorrect columns.");
        }
    }

    /**
     * See https://gitlab.com/dctx/rapidpass/rapidpass-api/-/issues/414
     *
     * @throws IOException
     */
    @Test
    void idTypeAlwaysIndividual() throws IOException {
        SubjectRegistrationCsvProcessor subjectRegistrationCsvProcessor = new SubjectRegistrationCsvProcessor();

        final String filename = "fake-data.csv";

        // Incorrect row, has PERSON instead of INDIVIDUAL
        byte[] byteContent = mockData(
                "PERSON,BA,Jose,M,Rizal,,KKK,COM,0001,,09171234567,jose.rizal@gmail.com,Origin,Origin Street,Origin City,Origin Province,Destination,Dest Street,Dest City, Dest Province,SKELETAL FORCE"
        ).getBytes();

        try {
            List<RapidPassCSVdata> process = subjectRegistrationCsvProcessor.process(new MockMultipartFile(filename, byteContent));

            assertThat(process.size(), equalTo(1));

            RapidPassCSVdata csvData = process.get(0);

            assertThat(csvData.getPassType(), equalTo("INDIVIDUAL"));

        } catch (Exception e) {
            System.err.println(e);
            fail("Unexpected error occurred.");
        }
    }

    /**
     * See https://gitlab.com/dctx/rapidpass/rapidpass-api/-/issues/414
     *
     * @throws IOException
     */
    @Test
    void vehicleRowsNotModified() throws IOException {
        SubjectRegistrationCsvProcessor subjectRegistrationCsvProcessor = new SubjectRegistrationCsvProcessor();

        final String filename = "fake-data.csv";

        // Incorrect row, has PERSON instead of INDIVIDUAL
        byte[] byteContent = mockData(
                "VEHICLE,BA,Jose,M,Rizal,,KKK,COM,0001,,09171234567,jose.rizal@gmail.com,Origin,Origin Street,Origin City,Origin Province,Destination,Dest Street,Dest City, Dest Province,SKELETAL FORCE"
        ).getBytes();

        try {
            List<RapidPassCSVdata> process = subjectRegistrationCsvProcessor.process(new MockMultipartFile(filename, byteContent));

            assertThat(process.size(), equalTo(1));

            RapidPassCSVdata csvData = process.get(0);

            assertThat(csvData.getPassType(), equalTo("VEHICLE"));

        } catch (Exception e) {
            System.err.println(e);
            fail("Unexpected error occurred.");
        }
    }

    private static String mockData(String... data) {

        final StringBuilder fileContentBuilder = new StringBuilder();

        fileContentBuilder.append(HEADERS).append("\r\n");

        for (String datum : data) {
            fileContentBuilder.append(datum).append("\r\n");
        }

        return fileContentBuilder.toString();
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
