package ph.devcon.rapidpass.utilities.csv;

import ph.devcon.rapidpass.models.RapidPassCSVdata;

/**
 * Implementation that maps a CSV row into a {@link RapidPassCSVdata} POJO.
 *
 * Each column is enumerated (from 0 to n) in the {{@link #CSV_COLUMN_MAPPING}} static property.
 *
 * The actual implementation of the parsing is found in {@link GenericCsvProcessor}.
 */
public class ApproverRegistrationCsvProcessor extends GenericCsvProcessor<RapidPassCSVdata> {

    private static final String[] CSV_COLUMN_MAPPING = {
            "UNKOWN COLUMNS HERE"
    };

    public ApproverRegistrationCsvProcessor() {
        super(CSV_COLUMN_MAPPING);
    }

}
