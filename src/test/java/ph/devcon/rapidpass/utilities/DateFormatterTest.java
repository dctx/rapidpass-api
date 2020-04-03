package ph.devcon.rapidpass.utilities;

import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.time.Instant;
import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class DateFormatterTest {

    public @Test void parseAndMachineFormatTheSame() throws ParseException {
        Instant parsedDate = DateFormatter.parse("2020-03-31T01:10:29.823Z");
        assertThat(DateFormatter.machineFormat(parsedDate), equalTo("2020-03-31T01:10:29.823Z"));
    }

    public @Test void displayDateReadable() throws ParseException {
        Instant parsedDate = DateFormatter.parse("2020-03-31T01:10:29.823Z");

        assertThat(DateFormatter.readableDateTime(parsedDate), equalTo("Mar 31 2020, 09:10:29"));

        assertThat(DateFormatter.readableDate(parsedDate), equalTo("Mar 31 2020"));

    }

    public @Test void precisionIsByMilliseconds() throws ParseException {
        Instant parsedDate = DateFormatter.parse("2020-03-31T01:10:29.823Z");

        Date transformedDate = DateFormatter.toDate(parsedDate);

        Instant transformInstantResult = DateFormatter.toInstant(transformedDate);

        assertThat(parsedDate, equalTo(transformInstantResult));
    }

}
