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
import org.apache.commons.lang3.StringUtils;
import ph.devcon.rapidpass.entities.AccessPass;
import ph.devcon.rapidpass.entities.Registrant;
import ph.devcon.rapidpass.enums.PassType;
import ph.devcon.rapidpass.utilities.StringFormatter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * Data model representing a request to create a new {@link AccessPass}. It contains details about an AccessPass and a {@link Registrant}.
 * <p>
 * API consumers send and receive {@link RapidPass} when interacting with the API for registering a rapid pass (GET, PUT).
 * <p>
 * API consumers send {@link RapidPassRequest} when they send a query for creating a {@link AccessPass} (POST).
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class RapidPassRequest {
    // required fields reference: https://docs.google.com/spreadsheets/d/1YemwomlhoKnDcBDI3OlrA5-yMSOSfA3BHj0N6PijmT4/edit#gid=0
    @NotNull
    private PassType passType;
    @NotEmpty
    private String aporType;
    @NotEmpty
    private String firstName;
    private String middleName;
    @NotEmpty
    private String lastName;
    private String suffix;
    @NotEmpty
    private String company;
    @NotEmpty
    private String idType;
    @NotEmpty
    private String identifierNumber;

    private String plateNumber;
    @NotEmpty
    private String mobileNumber; // todo validate. use validation annotations.
    @NotEmpty
    private String email;  // todo validate. use validation annotations.
    @NotEmpty
    private String originName;
    @NotEmpty
    private String originStreet;
    @NotEmpty
    private String originCity;
    @NotEmpty
    private String originProvince;
    @NotEmpty
    private String destName;
    @NotEmpty
    private String destStreet;
    @NotEmpty
    private String destCity;
    @NotEmpty
    private String destProvince;

    private String remarks;

    private String source;

    public String getName() {
        return String.format("%s %s", getFirstName(), getLastName());
    }

    /**
     * <p>Builds a {@link RapidPassRequest} from a {@link RapidPassCSVdata} row.</p>
     *
     * <p>Note that this method does not throw any {@link NullPointerException} errors if there are null property
     * values. It simply assigns `null` to the value.</p>
     *
     * @param csvData The row of data to be parsed into a {@link RapidPassRequest}.
     * @return A new {@link RapidPassRequest}, or `null` if the csvData is null.
     */
    public static RapidPassRequest buildFrom(RapidPassCSVdata csvData) {

        if (csvData == null)
            return null;

        PassType passType = !StringUtils.isEmpty(csvData.getPassType()) ? PassType.valueOf(csvData.getPassType()) : null;

        String plateNumber = !StringUtils.isEmpty(csvData.getPlateNumber()) ? StringFormatter.normalizeAlphanumeric(csvData.getPlateNumber()) : null;

        String company = StringUtils.trim(csvData.getCompany());
        String destName = StringUtils.trim(csvData.getDestName());
        String destStreet = StringUtils.trim(csvData.getDestStreet());
        String destCity = StringUtils.trim(csvData.getDestCity());
        String destProvince = StringUtils.trim(csvData.getDestProvince());
        String email = StringUtils.trim(csvData.getEmail());
        return RapidPassRequest.builder()
                .passType(passType)
                .aporType(StringUtils.trim(csvData.getAporType()))
                .firstName(StringUtils.trim(csvData.getFirstName()))
                .middleName(StringUtils.trim(csvData.getMiddleName()))
                .lastName(StringUtils.trim(csvData.getLastName()))
                .suffix(StringUtils.trim(csvData.getSuffix()))
                .company(StringUtils.defaultIfBlank(company, "NA"))
                .idType(StringUtils.trim(csvData.getIdType()))
                .identifierNumber(StringUtils.trim(csvData.getIdentifierNumber()))
                .plateNumber(StringUtils.defaultIfBlank(plateNumber, "NA"))
                .mobileNumber(StringUtils.trim(csvData.getMobileNumber()))
                .email(email)
                .originName(StringUtils.trim(csvData.getOriginName()))
                .originStreet(StringUtils.trim(csvData.getOriginStreet()))
                .originCity(StringUtils.trim(csvData.getOriginCity()))
                .originProvince(StringUtils.trim(csvData.getOriginProvince()))
                .destName(StringUtils.defaultIfBlank(destName, "NA"))
                .destStreet(StringUtils.defaultIfBlank(destStreet, "NA"))
                .destCity(destCity)
                .destProvince(StringUtils.defaultIfBlank(destProvince, "NA"))
                .remarks(StringUtils.trim(csvData.getRemarks()))
                .build();
    }

    @Override
    public String toString() {
        return String.format("firstName: %s, middleName: %s, lastName: %s, referenceId: %s, idType: %s, mobileNumber: %s",
                firstName, middleName, lastName, identifierNumber, identifierNumber, mobileNumber);
    }
}
