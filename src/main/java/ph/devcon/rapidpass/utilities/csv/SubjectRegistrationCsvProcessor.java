package ph.devcon.rapidpass.utilities.csv;

import ph.devcon.rapidpass.models.RapidPassCSVdata;

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

}
