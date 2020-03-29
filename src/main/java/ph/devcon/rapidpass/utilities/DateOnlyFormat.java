package ph.devcon.rapidpass.utilities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateOnlyFormat {
    static SimpleDateFormat sdf = new SimpleDateFormat();

    public static Date parse(String string) throws ParseException {
        return sdf.parse(string);
    }

    public static String format(Date date) {
        return sdf.format(date);
    }
}
