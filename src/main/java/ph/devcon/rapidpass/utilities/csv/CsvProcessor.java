package ph.devcon.rapidpass.utilities.csv;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * A CsvProcessor handles the parsing of a csv file, returning a list of CsvRowTypes.
 * @param <CsvRowType> a POJO that holds data of a row in the CSV file.
 */
public interface CsvProcessor<CsvRowType> {
    List<CsvRowType> process(MultipartFile csvFile) throws IOException;
}
