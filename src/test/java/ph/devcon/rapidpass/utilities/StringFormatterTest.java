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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class StringFormatterTest {

    @Test
    void testRemoveSpecialCharacters() {
        String result;

        result = StringFormatter.normalizeAlphanumeric("%sXSS");
        assertThat(result, equalTo("SXSS"));

        result = StringFormatter.normalizeAlphanumeric("= 1; DROP DATABASE;");
        assertThat(result, equalTo("1DROPDATABASE"));

        result = StringFormatter.normalizeAlphanumeric("ABC-234");
        assertThat(result, equalTo("ABC234"));

        result = StringFormatter.normalizeAlphanumeric("ABC:234");
        assertThat(result, equalTo("ABC234"));

        // Conduction stickers
        result = StringFormatter.normalizeAlphanumeric("A1-A001");
        assertThat(result, equalTo("A1A001"));
    }

    @Test
    void testLowercaseTransformedToUppercase() {
        String result;
        result = StringFormatter.normalizeAlphanumeric("mr0802");
        assertThat(result, equalTo("MR0802"));
    }

    @Test
    void testRemovesWhitespaces() {
        String result = StringFormatter.normalizeAlphanumeric("A B C   2  34    ");
        assertThat(result, equalTo("ABC234"));
    }

    @Test
    void testAgainstHomographAttacks() {
        // The first letter is a cyrillic A
        // See https://www.wikiwand.com/en/IDN_homograph_attack
        String result = StringFormatter.normalizeAlphanumeric("аabcd");

        // The formatter must remove non alpha-numeric characters, which include cyrillic letters.
        assertThat(result, equalTo("ABCD"));
    }

}
