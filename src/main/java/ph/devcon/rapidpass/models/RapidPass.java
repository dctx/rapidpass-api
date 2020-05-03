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

import lombok.Builder;
import lombok.Data;
import ph.devcon.rapidpass.entities.AccessPass;
import ph.devcon.rapidpass.entities.Registrant;
import ph.devcon.rapidpass.enums.PassType;

import java.time.format.DateTimeFormatter;

/**
 * Data model representing an {@link AccessPass}, but is only a subset of the model's properties.
 * <p>
 * API consumers send and receive {@link RapidPass} when interacting with the API for registering a rapid pass (GET, PUT).
 * <p>
 * API consumers send {@link RapidPassRequest} when they send a query for creating a {@link AccessPass} (POST).
 * <p>
 * This is JSON format returned to the user when they request for a GET on the AccessPass Resource.
 */
@Data
@Builder
public class RapidPass {

    private PassType passType;
    /**
     * Authorized Personnel Outside Residence
     */
    private String aporType;
    private String referenceId;
    private String controlCode;
    private String name;
    private String company;
    private String idType;
    private String identifierNumber;
    private String plateNumber;
    private String originName;
    private String originStreet;
    private String originCity;
    private String originProvince;
    private String destName;
    private String destStreet;
    private String destCity;
    private String destProvince;
    private String status;
    private String validFrom;
    private String validUntil;
    private String remarks;
    private String updates;
    private String email;
    private Boolean notified;

    public static RapidPass buildFrom(AccessPass accessPass) {
        // TODO: If you want to return only a subset of properties from {@link AccessPass}, do so here.
        final Registrant registrantId = accessPass.getRegistrantId();
        return RapidPass.builder()
                .referenceId(accessPass.getReferenceID())
                .controlCode(accessPass.getControlCode() == null ? "" : accessPass.getControlCode())
                .passType(PassType.valueOf(accessPass.getPassType()))
                .aporType(accessPass.getAporType())
                .name(accessPass.getName())
                .notified(accessPass.getNotified())
                .company(accessPass.getCompany())
                .idType(accessPass.getIdType())
                .identifierNumber(accessPass.getIdentifierNumber())
                .plateNumber(accessPass.getPlateNumber())
                .status(accessPass.getStatus())
                .validFrom(accessPass.getValidFrom() == null ? "" : DateTimeFormatter.ISO_INSTANT.format(accessPass.getValidFrom()))
                .validUntil(accessPass.getValidTo() == null ? "" : DateTimeFormatter.ISO_INSTANT.format(accessPass.getValidTo()))
                .originName(accessPass.getOriginName())
                .originStreet(accessPass.getOriginStreet())
                .originCity(accessPass.getOriginCity())
                .originProvince(accessPass.getOriginProvince())
                .destName(accessPass.getDestinationName())
                .destStreet(accessPass.getDestinationStreet())
                .destCity(accessPass.getDestinationCity())
                .destProvince(accessPass.getDestinationProvince())
                .remarks(accessPass.getRemarks())
                .updates(accessPass.getUpdates())
                .email(registrantId == null ? "" : registrantId.getEmail())
                .build();
    }
}
