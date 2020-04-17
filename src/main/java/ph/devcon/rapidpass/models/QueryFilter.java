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

package ph.devcon.rapidpass.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ph.devcon.rapidpass.api.models.CommonRapidPassFields;
import ph.devcon.rapidpass.enums.AccessPassStatus;
import ph.devcon.rapidpass.enums.PassType;
import ph.devcon.rapidpass.enums.RecordSource;
import ph.devcon.rapidpass.utilities.StringFormatter;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryFilter {

    public static final Integer DEFAULT_PAGE_SIZE = 25;

    /**
     * Allows a user to filter by pass type.
     *
     * For possible values, see {@link PassType}.
     */
    private String passType;
    /**
     * Allows a user to filter by Apor type.
     *
     * For possible values, trust the db data primarily. Note that {@link CommonRapidPassFields.AporTypeEnum} may
     * potentially be outdated, as it is based on the OpenAPI Spec. Again, trust the db first and foremost.
     */
    private String aporType;

    /**
     * Allows a user to filter access passes based on reference ID.
     */
    private String referenceId;

    /**
     * Allows a user to filter by status.
     *
     * For possible values, see {@link AccessPassStatus}.
     */
    private String status;

    /**
     * Allows a user to filter by plateNumber.
     *
     * Note that plate numbers are normalized using {@link StringFormatter#normalizeAlphanumeric(String)}.
     */
    private String plateNumber;

    /**
     * Allows a user to filter by company name.
     */
    private String company;

    /**
     * Allows a user to filter by access pass name.
     */
    private String name;

    /**
     * Allows a user to filter by source.
     *
     * For possible values, see {@link RecordSource}.
     */
    private RecordSource source;

    /**
     * Searching
     */
    private String search;

    /**
     * Allows a user to paginate the results. This specifies which page will be shown.
     */
    private Integer pageNo = 0;

    /**
     * Allows a user to paginate the results. This specifies how many results per page.
     */
    private Integer maxPageRows = DEFAULT_PAGE_SIZE;
}
