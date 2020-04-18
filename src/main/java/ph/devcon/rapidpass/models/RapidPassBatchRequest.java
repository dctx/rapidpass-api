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

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

/**
 * This the JSON format when performing a batch upload of the user data expected by the backend.
 *
 * TODO: This is incorrect, as the batch data is uploaded not using JSON data (as arrays) but instead uploaded as a CSV.
 *
 * The {@link RapidPassBatchRequest} class models a Rapid Pass Request.
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class RapidPassBatchRequest {
    /**
     * Backend only reference number.
     */
    @NotNull
    @Builder.Default
    @EqualsAndHashCode.Exclude
    private String refNum = UUID.randomUUID().toString();

    @NotNull
    private List<RapidPass> batch;

}
