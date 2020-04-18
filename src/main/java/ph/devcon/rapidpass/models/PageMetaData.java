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

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Objects;

/**
 * Page Meta Data
 */
@ApiModel(description = "Page Meta Data")
@Validated
@Data
@Builder
public class PageMetaData {

    private Integer currentPage = null;
    private Integer currentPageRows = null;
    private Integer totalPages = null;
    private Long totalRows = null;
    private List<String> columnNames;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PageMetaData pageMetaData = (PageMetaData) o;
        return Objects.equals(this.currentPage, pageMetaData.currentPage) &&
                Objects.equals(this.currentPageRows, pageMetaData.currentPageRows) &&
                Objects.equals(this.totalPages, pageMetaData.totalPages) &&
                Objects.equals(this.totalRows, pageMetaData.totalRows) &&
                Objects.equals(this.columnNames, pageMetaData.columnNames);
    }

}
