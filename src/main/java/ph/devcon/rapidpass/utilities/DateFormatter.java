package ph.devcon.rapidpass.utilities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Instructions for date encoding given by Alistair.
 *
 * <a href="https://gitlab.com/dctx/rapidpass/rapidpass-api/-/issues/125">See this issue.</a>
 */
public class DateFormatter {
    static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    /**
     * Parses a string into a date object.
     * <a href="https://stackoverflow.com/a/18217193/1323398">See this stack overflow thread</a>.
     *
     * @param string the date in string format e.g. "2001-07-04T12:08:56.235-07:00";
     * @return the date in a {@link Date} object
     * @throws ParseException when the string is not formatted correctly.
     */
    public static Date parse(String string) throws ParseException {
        return dateFormat.parse(string);
    }

    public static String format(Date localDate) {
        return dateFormat.format(localDate);
    }
}
