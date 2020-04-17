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
