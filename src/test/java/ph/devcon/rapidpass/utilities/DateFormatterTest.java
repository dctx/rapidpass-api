package ph.devcon.rapidpass.utilities;

import org.junit.jupiter.api.Test;

import java.util.Date;

public class DateFormatterTest {

    public @Test void goodDate() {
        String formattedDate = DateFormatter.format(new Date());

    }
}
