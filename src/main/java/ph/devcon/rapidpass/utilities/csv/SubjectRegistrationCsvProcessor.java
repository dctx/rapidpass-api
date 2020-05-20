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
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
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

        CSVReader csvReader = new CSVReader(fileReader);

        return (CsvToBean<RapidPassCSVdata>) new CsvToBeanBuilder(csvReader)
                .withMappingStrategy(strategy)
                .withType(type)
                .withSeparator(',')
                .withQuoteChar('"')
                .withFilter(line -> {

                    boolean shouldInclude = true;

                    if (line.length > 1 && (line[1].contains("NATURE OF") || line[1].contains("aporType")))
                        shouldInclude = false;

                    if (line.length > 2 && (line[2].contains("FIRSTNAME") || line[2].contains("firstName")))
                        shouldInclude = false;

                    if (line.length > 3 && (line[3].contains("LASTNAME") || line[3].contains("lastName")))
                        shouldInclude = false;

                    return shouldInclude;
                })
                .withIgnoreLeadingWhiteSpace(true)
                .build();
    }


    @Override
    public List<NormalizationRule<RapidPassCSVdata>> getNormalizationRules() {

        ImmutableList<String> multiCityAporTypes = ImmutableList.of("DE", "DP", "AG", "GE", "GL", "GJ", "FF", "MS", "TS", "ME", "PO", "SH");

        return ImmutableList.of(
                new Trim<>("passType"),
                new Capitalize<>("passType"),
                new LocalOverwrite<>("passType", "INDIVIDUAL", passType -> !"VEHICLE".equals(passType)),

                new Trim<>("plateNumber"),
                new Capitalize<>("plateNumber"),

                new Trim<>("aporType"),
                new Capitalize<>("aporType"),

                new Trim<>("firstName"),
                new Capitalize<>("firstName"),
                new Trim<>("middleName"),
                new Capitalize<>("middleName"),
                new Trim<>("lastName"),
                new Capitalize<>("lastName"),

                new SplitInTwoAndGetFirst<>("email"),

                new Trim<>("mobileNumber"),
                new SplitInTwoAndGetFirst<>("mobileNumber"),
                new TransformAlphanumeric<>("mobileNumber"),

                new Trim<>("company"),

                new DefaultValue<>("email", ""),
                new Trim<>("email"),
                new DefaultValue<>("remarks", "frontliner"),

                new DefaultValue<>("idType", "OTH"),
                new Trim<>("idType"),

                new SplitInTwoAndGetFirst<>("plateNumber"),
                new TransformAlphanumeric<>("plateNumber"),

                new DefaultValue<>("identifierNumber", "OTH"),
                new Trim<>("identifierNumber"),
                new TransformAlphanumeric<>("identifierNumber"),

                new NormalizeMobileNumber<>("mobileNumber"),
                new TransformAlphanumeric<>("mobileNumber"),

                new Max<>("suffix", 25),
                new Max<>("email", 254),

                new Max<>("aporType", 10),
                new Max<>("idType", 25),

                new Max<>("identifierNumber", 25),
                new Max<>("plateNumber", 20),
                new Max<>("remarks", 250),
                new Max<>("limitations", 200),

                new Max<>("originName", 100),
                new Max<>("originStreet", 150),
                new Max<>("originProvince", 50),
                new Max<>("originCity", 50),

                new Max<>("destName", 100),
                new Max<>("destStreet", 150),
                new Max<>("destProvince", 50),
                new Max<>("destCity", 50),
                new GlobalOverwrite<>("destCity", "Multi City", rapidPassCsvData -> multiCityAporTypes.contains(rapidPassCsvData.getAporType()))

        );
    }
}
