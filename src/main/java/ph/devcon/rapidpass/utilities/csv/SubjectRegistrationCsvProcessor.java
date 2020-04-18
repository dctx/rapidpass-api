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

    @Override
    protected CsvToBean<RapidPassCSVdata> generateCsvToBeanParser(ColumnPositionMappingStrategy strategy, Class<RapidPassCSVdata> type, Reader fileReader) {
        return (CsvToBean<RapidPassCSVdata>) new CsvToBeanBuilder(fileReader)
                .withMappingStrategy(strategy)
                .withType(type)
                .withSkipLines(1)
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
