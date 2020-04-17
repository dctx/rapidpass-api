/*
 * Copyright (c) 2020.  DevConnect Philippines, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and limitations under the License.
 */

package ph.devcon.rapidpass.utilities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
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
    public static Instant parse(String string) throws ParseException {
        return Instant.parse(string);
    }

    /**
     * String machine format for date times are stored as ISO8601 as UTC.
     *
     * If we're storing dates internally as strings, we make sure to use UTC.
     *
     * This is used when transmitting data over APIs, for easy parsing of API consumers.
     */
    public static String machineFormat(Date localDate) {
        return toInstant(localDate).toString();
    }

    /**
     * String machine format for date times are stored as ISO8601 as UTC.
     *
     * If we're storing dates internally as strings, we make sure to use UTC.
     *
     * This is used when transmitting data over APIs, for easy parsing of API consumers.
     */
    public static String machineFormat(Instant instant) {
        return instant.toString();
    }

    /**
     * You can display the readable date of an instant.
     */
    public static String readable(Instant instant, String pattern, ZoneId zone) {
        ZonedDateTime gmt = instant.atZone(zone);
        return gmt.format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * Not specifying the zone defaults to using +8 GMT, which is PHT.
     */
    public static String readable(Instant instant, String pattern) {
        ZoneId defaultGmt = ZoneId.ofOffset("GMT", ZoneOffset.ofHours(8));
        return readable(instant, pattern, defaultGmt);
    }

    /**
     * Displaying dates in the following format:
     *
     * Mar 31, 2020, 08:00:00
     */
    public static String readableDateTime(Instant instant) {
        return readable(instant, "MMM dd yyyy, HH:mm:ss");
    }

    /**
     * Displaying dates in the following format:
     *
     * Mar 31, 2020
     */
    public static String readableDate(Instant instant) {
        return readable(instant, "MMM dd yyyy");
    }

    /**
     * Helper function to transform a {@link Date} into an {@link Instant}.
     */
    public static Instant toInstant(Date localDate) {
        return Instant.ofEpochMilli(localDate.getTime());
    }

    /**
     * Helper function to transform an {@link Instant} into a {@link Date}.
     */
    public static Date toDate(Instant instant) {
        return new Date(instant.toEpochMilli());
    }
}
