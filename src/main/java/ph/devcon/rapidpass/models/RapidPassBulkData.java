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
import ph.devcon.rapidpass.entities.AccessPass;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RapidPassBulkData {
    private int currentPage;
    private int currentPageRows;
    private int totalPages;
    private long totalRows;
    private List<?> data;


    public static List<?> getColumnNames() {
        List<Object> columnList = new ArrayList<>();
        columnList.add("controlCode");
        columnList.add("referenceID");
        columnList.add("passType");
        columnList.add("aporType");
        columnList.add("identifierNumber");
        columnList.add("validFrom");
        columnList.add("validTo");
        columnList.add("name");
        columnList.add("plateNumber");
        columnList.add("status");
        return columnList;
    }

    public static List<?> values(AccessPass accessPass) {
        List<Object> valueList = new ArrayList<>();
        valueList.add(accessPass.getControlCode());
        valueList.add(accessPass.getReferenceID());
        valueList.add(accessPass.getPassType());
        valueList.add(accessPass.getAporType());
        valueList.add(accessPass.getIdentifierNumber());
        valueList.add(accessPass.getValidFrom().toEpochSecond());
        valueList.add(accessPass.getValidTo().toEpochSecond());
        valueList.add(accessPass.getName());
        valueList.add(accessPass.getPlateNumber());
        valueList.add(accessPass.getStatus());
        return valueList;
    }
}
