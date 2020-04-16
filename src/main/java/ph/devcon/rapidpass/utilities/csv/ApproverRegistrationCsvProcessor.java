package ph.devcon.rapidpass.utilities.csv;

import ph.devcon.rapidpass.models.AgencyUser;
import ph.devcon.rapidpass.models.RapidPassCSVdata;

/**
 * Implementation that maps a CSV row into a {@link RapidPassCSVdata} POJO.
 *
 * Each column is enumerated (from 0 to n) in the {{@link #CSV_COLUMN_MAPPING}} static property.
 *
 * The actual implementation of the parsing is found in {@link GenericCsvProcessor}.
 */
public class ApproverRegistrationCsvProcessor extends GenericCsvProcessor<AgencyUser> {

    private static final String[] CSV_COLUMN_MAPPING = {
            "registrar",
            "username",
            "firstName",
            "lastName",
            "email"
    };

    public ApproverRegistrationCsvProcessor() {
        super(CSV_COLUMN_MAPPING);
    }

}