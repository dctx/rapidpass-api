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

package ph.devcon.rapidpass.utilities.csv;

import com.google.common.collect.ImmutableList;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.CsvToBeanFilter;
import ph.devcon.rapidpass.models.RapidPassCSVdata;
import ph.devcon.rapidpass.utilities.normalization.*;

import java.io.Reader;
import java.util.Arrays;
import java.util.List;

/**
 * Implementation that maps a CSV row into a {@link RapidPassCSVdata} POJO.
 *
 * Each column is enumerated (from 0 to n) in the {{@link #CSV_COLUMN_MAPPING}} static property.
 *
 * The actual implementation of the parsing is found in {@link GenericCsvProcessor}.
 */
public class SubjectRegistrationCsvProcessor extends GenericCsvProcessor<RapidPassCSVdata> {

    private static final String[] CSV_COLUMN_MAPPING = {
            "passType",
            "aporType",
            "firstName",
            "middleName",
            "lastName",
            "suffix",
            "company",
            "idType",
            "identifierNumber",
            "plateNumber",
            "mobileNumber",
            "email",
            "originName",
            "originStreet",
            "originCity",
            "originProvince",
            "destName",
            "destStreet",
            "destCity",
            "destProvince",
            "remarks"
    };

    public SubjectRegistrationCsvProcessor() {
        super(CSV_COLUMN_MAPPING);
    }

    protected CsvToBean<RapidPassCSVdata> generateCsvToBeanParser(ColumnPositionMappingStrategy strategy, Class<RapidPassCSVdata> type, Reader fileReader) {

        // Don't handle rows that have incorrect column length.
        CsvToBeanFilter dontHandleRowsWithIncorrectColumnLength = strings -> strings.length == 21;

        // Don't handle rows with email `juan@xxxx.xxx`.
        CsvToBeanFilter dontHandleRowsWithMissingEmailOrDefaultEmail = strings -> {
            int indexOfEmail = 11;
            if (strings.length < indexOfEmail) return false;

            String email = strings[indexOfEmail];

            String DEFAULT_EMAIL = "juan@xxxx.xxx";

            return email.equalsIgnoreCase(DEFAULT_EMAIL);
        };

        // Don't handle rows with mobile number `09000000000`.
        CsvToBeanFilter dontHandleRowsWithMissingMobileNumberOrDefaultMobileNumber = strings -> {
            int indexOfMobileNumber = 10;
            if (strings.length < indexOfMobileNumber) return false;

            String mobileNumber = strings[indexOfMobileNumber];

            String DEFAULT_MOBILE_NUMBER = "09000000000";

            return mobileNumber.equals(DEFAULT_MOBILE_NUMBER);
        };

        // Don't handle rows with mobile csv row `,,,,,,,,,,,,,,,,,,,,`.
        CsvToBeanFilter dontHandleCompletelyEmptyRows = strings -> {

            List<String> values = Arrays.asList(strings);
            int countEmpty = (int) values.stream().filter(v -> v.trim().isEmpty()).count();
            return countEmpty == values.size();
        };

        return (CsvToBean<RapidPassCSVdata>) new CsvToBeanBuilder(fileReader)
                .withMappingStrategy(strategy)
                .withType(type)
                .withSkipLines(1)
                .withFilter(dontHandleRowsWithIncorrectColumnLength)
                .withFilter(dontHandleRowsWithMissingEmailOrDefaultEmail)
                .withFilter(dontHandleRowsWithMissingMobileNumberOrDefaultMobileNumber)
                .withFilter(dontHandleCompletelyEmptyRows)
                .withIgnoreLeadingWhiteSpace(true)
                .build();
    }


    @Override
    public List<NormalizationRule<RapidPassCSVdata>> getNormalizationRules() {
        return ImmutableList.of(
                new Trim("passType"),
                new Capitalize("passType"),
                new Trim("aporType"),
                new Trim("mobileNumber"),
                new DefaultValue("email", ""),
                new DefaultValue("remarks", "frontliner"),
                new DefaultValue("idType", "OTH"),
                new DefaultValue("identifierNumber", "OTH"),
                new NormalizeMobileNumber("mobileNumber")
        );
    }
}
