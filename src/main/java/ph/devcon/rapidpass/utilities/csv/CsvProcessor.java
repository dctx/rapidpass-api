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

import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import org.springframework.web.multipart.MultipartFile;
import ph.devcon.rapidpass.exceptions.CsvColumnMappingMismatchException;

import java.io.IOException;
import java.util.List;

/**
 * A CsvProcessor handles the parsing of a csv file, returning a list of CsvRowTypes.
 * @param <CsvRowType> a POJO that holds data of a row in the CSV file.
 */
public interface CsvProcessor<CsvRowType> {
    List<CsvRowType> process(MultipartFile csvFile) throws IOException, CsvColumnMappingMismatchException, CsvRequiredFieldEmptyException;
}
