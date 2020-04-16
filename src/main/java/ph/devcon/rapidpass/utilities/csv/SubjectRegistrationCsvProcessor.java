package ph.devcon.rapidpass.utilities.csv;

import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.CsvToBeanFilter;
import ph.devcon.rapidpass.models.RapidPassCSVdata;

import java.io.Reader;
import java.util.Arrays;
import java.util.List;

/**
 * Implementation that maps a CSV row into a {@link RapidPassCSVdata} POJO.
 *
 * Each column is enumerated (from 0 to n) in the {{@link #CSV_COLUMN_MAPPING}} static property.
 *
 * The actual implementation of the parsing is found in {@link GenericCsvProcessor}.
 */
public class SubjectRegistrationCsvProcessor extends GenericCsvProcessor<RapidPassCSVdata> {

    private static final String[] CSV_COLUMN_MAPPING = {
            "passType",
            "aporType",
            "firstName",
            "middleName",
            "lastName",
            "suffix",
            "company",
            "idType",
            "identifierNumber",
            "plateNumber",
            "mobileNumber",
            "email",
            "originName",
            "originStreet",
            "originCity",
            "originProvince",
            "destName",
            "destStreet",
            "destCity",
            "destProvince",
            "remarks"
    };

    public SubjectRegistrationCsvProcessor() {
        super(CSV_COLUMN_MAPPING);
    }

    protected CsvToBean<RapidPassCSVdata> generateCsvToBeanParser(ColumnPositionMappingStrategy strategy, Class<RapidPassCSVdata> type, Reader fileReader) {

        // Don't handle rows that have incorrect column length.
        CsvToBeanFilter dontHandleRowsWithIncorrectColumnLength = strings -> strings.length == 21;

        // Don't handle rows with email `juan@xxxx.xxx`.
        CsvToBeanFilter dontHandleRowsWithMissingEmailOrDefaultEmail = strings -> {
            int indexOfEmail = 11;
            if (strings.length < indexOfEmail) return false;

            String email = strings[indexOfEmail];

            String DEFAULT_EMAIL = "juan@xxxx.xxx";

            return email.equalsIgnoreCase(DEFAULT_EMAIL);
        };

        // Don't handle rows with mobile number `09000000000`.
        CsvToBeanFilter dontHandleRowsWithMissingMobileNumberOrDefaultMobileNumber = strings -> {
            int indexOfMobileNumber = 10;
            if (strings.length < indexOfMobileNumber) return false;

            String mobileNumber = strings[indexOfMobileNumber];

            String DEFAULT_MOBILE_NUMBER = "09000000000";

            return mobileNumber.equals(DEFAULT_MOBILE_NUMBER);
        };

        // Don't handle rows with mobile csv row `,,,,,,,,,,,,,,,,,,,,`.
        CsvToBeanFilter dontHandleCompletelyEmptyRows = strings -> {

            List<String> values = Arrays.asList(strings);
            int countEmpty = (int) values.stream().filter(v -> v.trim().isEmpty()).count();
            return countEmpty == values.size();
        };

        return (CsvToBean<RapidPassCSVdata>) new CsvToBeanBuilder(fileReader)
                .withMappingStrategy(strategy)
                .withType(type)
                .withSkipLines(1)
                .withFilter(dontHandleRowsWithIncorrectColumnLength)
                .withFilter(dontHandleRowsWithMissingEmailOrDefaultEmail)
                .withFilter(dontHandleRowsWithMissingMobileNumberOrDefaultMobileNumber)
                .withFilter(dontHandleCompletelyEmptyRows)
                .withIgnoreLeadingWhiteSpace(true)
                .build();
    }

}
