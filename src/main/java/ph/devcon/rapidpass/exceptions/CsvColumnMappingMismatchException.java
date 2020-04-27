package ph.devcon.rapidpass.exceptions;

import com.opencsv.exceptions.CsvException;

/**
 * @author czeideavanzado
 */
public class CsvColumnMappingMismatchException extends CsvException {

    public CsvColumnMappingMismatchException(String message) {
        super(message);
    }
}
