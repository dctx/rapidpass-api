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
import org.apache.commons.lang3.StringUtils;
import ph.devcon.rapidpass.models.RapidPassCSVdata;
import ph.devcon.rapidpass.utilities.normalization.*;

import java.io.Reader;
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
        CsvToBeanFilter dontHandleRowsWithIncorrectColumnLength = strings -> {
            return strings.length == 21;
        };

        // Don't handle rows with email `juan@xxxx.xxx`.
        CsvToBeanFilter dontHandleRowsWithMissingEmailOrDefaultEmail = strings -> {
            int indexOfEmail = 11;
            boolean isEmailNotInCsv = strings.length < indexOfEmail;
            if (isEmailNotInCsv) return false;

            String email = strings[indexOfEmail];

            String DEFAULT_EMAIL = "juan@xxxx.xxx";

            boolean isDefaultEmail = email.equalsIgnoreCase(DEFAULT_EMAIL);

            return !isDefaultEmail;
        };

        // Don't handle rows with mobile number `09000000000`.
        CsvToBeanFilter dontHandleRowsWithMissingMobileNumberOrDefaultMobileNumber = strings -> {
            int indexOfMobileNumber = 10;
            boolean isMobileNotInCsv = strings.length < indexOfMobileNumber;
            if (isMobileNotInCsv) return false;

            String mobileNumber = strings[indexOfMobileNumber];

            String DEFAULT_MOBILE_NUMBER = "09000000000";

            boolean isDefaultMobile = mobileNumber.equals(DEFAULT_MOBILE_NUMBER);

            return !isDefaultMobile;
        };

        // Don't handle rows with missing names.
        CsvToBeanFilter dontHandleRowsWithEmptyFirstNameOrEmptyLastName = strings -> {
            int indexOfFirstName = 2;
            int indexOfLastName = 4;
            boolean isFirstNameNotInCsv = strings.length < indexOfFirstName;
            if (isFirstNameNotInCsv) return false;


            boolean isLastNameNotInCsv = strings.length < indexOfLastName;
            if (isLastNameNotInCsv) return false;

            if (StringUtils.isBlank(StringUtils.trimToEmpty(strings[indexOfFirstName]))) return false;

            if (StringUtils.isBlank(StringUtils.trimToEmpty(strings[indexOfLastName]))) return false;

            return true;
        };

        // Don't handle rows non-required rows.
        CsvToBeanFilter dontHandleRowsWithoutRequiredColumns = strings -> {

            if (strings.length > 0 && StringUtils.isBlank(strings[0])) return false;
            if (strings.length > 1 && StringUtils.isBlank(strings[1])) return false;
            if (strings.length > 10 && StringUtils.isBlank(strings[10])) return false;

            return true;
        };

        CsvToBeanFilter filterComposer = strings -> {
            if (!dontHandleRowsWithIncorrectColumnLength.allowLine(strings)) return false;
            if (!dontHandleRowsWithMissingEmailOrDefaultEmail.allowLine(strings)) return false;
            if (!dontHandleRowsWithMissingMobileNumberOrDefaultMobileNumber.allowLine(strings)) return false;
            if (!dontHandleRowsWithoutRequiredColumns.allowLine(strings)) return false;
//            if (!dontHandleRowsWithEmptyFirstNameOrEmptyLastName.allowLine(strings)) return false;
            return true;
        };

        return (CsvToBean<RapidPassCSVdata>) new CsvToBeanBuilder(fileReader)
                .withMappingStrategy(strategy)
                .withType(type)
                .withSeparator(',')
                .withQuoteChar('"')
                .withFilter(filterComposer)
                .withIgnoreLeadingWhiteSpace(true)
                .build();
    }


    @Override
    public List<NormalizationRule<RapidPassCSVdata>> getNormalizationRules() {
        return ImmutableList.of(
                new Trim("passType"),
                new Capitalize("passType"),
                new Overwrite<>("passType", "INDIVIDUAL", passType -> !"VEHICLE".equals(passType)),

                new Trim("plateNumber"),
                new Capitalize("plateNumber"),

                new Trim("aporType"),
                new Capitalize("aporType"),

                new SplitInTwoAndGetFirst("email"),

                new Trim("mobileNumber"),
                new SplitInTwoAndGetFirst("mobileNumber"),
                new TransformAlphanumeric("mobileNumber"),

                new Trim("company"),

                new DefaultValue("email", ""),
                new Trim("email"),
                new DefaultValue("remarks", "frontliner"),

                new DefaultValue("idType", "OTH"),
                new Trim("idType"),

                new SplitInTwoAndGetFirst("plateNumber"),
                new TransformAlphanumeric("plateNumber"),

                new DefaultValue("identifierNumber", "OTH"),
                new Trim("identifierNumber"),
                new TransformAlphanumeric("identifierNumber"),

                new NormalizeMobileNumber("mobileNumber"),
                new TransformAlphanumeric("mobileNumber"),

                new Max<>("suffix", 25),
                new Max<>("email", 50),

                new Max<>("aporType", 10),
                new Max<>("idType", 25),

                new Max<>("identifierNumber", 25),
                new Max<>("plateNumber", 20),
                new Max<>("remarks", 250),
                new Max<>("originStreet", 150),
                new Max<>("originProvince", 50),
                new Max<>("originCity", 50),


                new Max<>("destStreet", 150),
                new Max<>("destProvince", 50),
                new Max<>("destCity", 50)

        );
    }
}
