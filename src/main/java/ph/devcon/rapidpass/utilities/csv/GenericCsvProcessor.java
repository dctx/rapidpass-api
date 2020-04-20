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

import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;
import ph.devcon.rapidpass.utilities.normalization.NormalizationRule;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.ParameterizedType;
import java.util.Collections;
import java.util.List;

/**
 * Generic CSV Parser utility class.
 *
 * @param <E>
 */
@Slf4j
public class GenericCsvProcessor<E> implements CsvProcessor<E> {

    private final String[] columnMapping;

    GenericCsvProcessor(String[] columnMapping){
        this.columnMapping = columnMapping;
    }

    /**
     * You may customize this method by overriding it in subclasses.
     *
     * @param type The resulting POJO type.
     * @return a strategy for mapping columns from the csv to their corresponding properties in the POJO.
     */
    protected ColumnPositionMappingStrategy generateStrategy(Class<E> type) {
        ColumnPositionMappingStrategy strategy = new ColumnPositionMappingStrategy();
        strategy.setType(type);
        strategy.setColumnMapping(this.columnMapping);

        return strategy;
    }

    /**
     * You may customize this method by overriding it in subclasses.
     * @param strategy The mapping strategy from csv column to pojo properties.
     * @param type The resulting POJO type.
     * @param fileReader The file reader reference.
     * @return A csv parser.
     */
    protected CsvToBean<E> generateCsvToBeanParser(ColumnPositionMappingStrategy strategy, Class<E> type, Reader fileReader) {
        return (CsvToBean<E>) new CsvToBeanBuilder(fileReader)
            .withMappingStrategy(strategy)
            .withType(type)
            .withSkipLines(1)
            .withIgnoreLeadingWhiteSpace(true)
            .build();
    }

    @Override
    public List<E> process(MultipartFile csvFile) throws IOException {

        if (csvFile.isEmpty()) {
            throw new IllegalArgumentException("Uploaded CSV file was empty.");
        }

        List<E> result;

        ParameterizedType genericSuperclass = (ParameterizedType) this.getClass().getGenericSuperclass();
        Class<E> type = (Class<E>) genericSuperclass.getActualTypeArguments()[0];

        if (type == null) {
            throw new IllegalStateException("Failed to transform CSV row into a POJO. Failed to parse generic type.");
        }

        try (Reader fileReader = new BufferedReader(new InputStreamReader(csvFile.getInputStream()))) {
            ColumnPositionMappingStrategy strategy = generateStrategy(type);

            CsvToBean<E> csvParser = generateCsvToBeanParser(strategy, type, fileReader);

            result = csvParser.parse();

            result = normalize(result);
        } catch (IOException e) {
            throw new IOException("Failed to read CSV file.", e);
        }

        return result;
    }

    public List<NormalizationRule<E>> getNormalizationRules() {
        return Collections.emptyList();
    }

    /**
     * Default normalization op is no operation
     */
    private List<E> normalize(List<E> csvRows) {

        List<NormalizationRule<E>> normalizationRules = getNormalizationRules();

        csvRows.forEach((E csvRow) ->
                normalizationRules.forEach(n -> {
                    try {
                        n.normalize(csvRow);
                    } catch (Exception e) {
                        log.warn("Failed to perform normalization {}", n.getClass().getName(), e);
                    }
                }));

        return csvRows;
    }
}
