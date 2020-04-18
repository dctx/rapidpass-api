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

package ph.devcon.rapidpass.entities;

import lombok.*;
import ph.devcon.dctx.rapidpass.model.ControlCode;
import ph.devcon.dctx.rapidpass.model.QrCodeData;
import ph.devcon.rapidpass.enums.AccessPassStatus;
import ph.devcon.rapidpass.enums.PassType;
import ph.devcon.rapidpass.enums.RecordSource;
import ph.devcon.rapidpass.models.QueryFilter;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Collection;

/**
 * Data model representing an access pass, that maps out directly to the table definition in the database.
 *
 * @author eric
 */
@Entity
@Table(name = "access_pass", schema = "public")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccessPass implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 30)
    @Column(name = "reference_id")
    private String referenceID;
    @Size(max = 10)
    @Column(name = "pass_type")
    private String passType;
    @Size(max = 10)
    @Column(name = "apor_type")
    private String aporType;
    /**
     * Note that the control code retrieved from the database is no longer reliable. Instead, generate it
     * <a href="https://gitlab.com/dctx/rapidpass/rapidpass-api/-/issues/327">on the fly</a>.
     *
     * <p>The {@link ph.devcon.rapidpass.services.QrPdfService} has the code necessary to encode and decode
     * control codes based on the access passes' ID. Use its {@link ph.devcon.rapidpass.services.QrPdfService#bindControlCodeForAccessPass(AccessPass)}
     * method to give the access pass its correct control code values.
     * </p>
     * @deprecated
     */
    @Column(name = "control_code")
    private String controlCode;
    @Size(max = 10)
    @Column(name = "id_type")
    private String idType;
    @Size(max = 25)
    @Column(name = "identifier_number")
    private String identifierNumber;
    @Size(max = 100)
    @Column(name = "name")
    private String name;
    @Size(max = 100)
    @Column(name = "company")
    private String company;
    @Size(max = 20)
    @Column(name = "plate_number")
    private String plateNumber;
    @Size(max = 250)
    @Column(name = "remarks")
    private String remarks;
    @Column(name = "scope")
    private Integer scope;
    @Size(max = 200)
    @Column(name = "limitations")
    private String limitations;
    @Size(max = 100)
    @Column(name = "origin_name")
    private String originName;
    @Size(max = 150)
    @Column(name = "origin_street")
    private String originStreet;
    @Size(max = 50)
    @Column(name = "origin_province")
    private String originProvince;
    @Size(max = 50)
    @Column(name = "origin_city")
    private String originCity;
    @Size(max = 100)
    @Column(name = "destination_name")
    private String destinationName;
    @Size(max = 150)
    @Column(name = "destination_street")
    private String destinationStreet;
    @Size(max = 150)
    @Column(name = "destination_city")
    private String destinationCity;
    @Size(max = 50)
    @Column(name = "destination_province")
    private String destinationProvince;
    @Column(name = "valid_from")

    private OffsetDateTime validFrom;
    @Column(name = "valid_to")

    private OffsetDateTime validTo;
    @Size(max = 20)
    @Column(name = "issued_by")
    private String issuedBy;
    @Size(max = 2147483647)
    @Column(name = "updates")
    private String updates;
    @Size(max = 20)
    @Column(name = "status")
    private String status;
    @Size(max = 50)
    @Column(name = "source")
    private String source;
    @Column(name = "date_time_created")

    private OffsetDateTime dateTimeCreated;
    @Column(name = "date_time_updated")

    private OffsetDateTime dateTimeUpdated;

    @JoinColumn(name = "registrant_id", referencedColumnName = "id")
    @ManyToOne
    private Registrant registrantId;
    @OneToMany(mappedBy = "accessPassId", fetch = FetchType.LAZY)
    private Collection<AccessPassLog> accessPassLogCollection;

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    /**
     * Checks if an AccessPass is currently valid. A valid access pass is APPROVED and has not yet expired (sysdate < validTo).
     *
     * @param accessPass Access pass to check
     * @return true if valid
     */
    public static boolean isValid(AccessPass accessPass) {
        return AccessPassStatus.APPROVED.toString().equalsIgnoreCase(accessPass.getStatus())
                && accessPass.getValidTo().isAfter(OffsetDateTime.now());
    }

    @Override
    public String toString() {
        return "ph.devcon.rapidpass.entities.AccessPass[ id=" + id + " ]";
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AccessPass)) {
            return false;
        }
        AccessPass other = (AccessPass) object;
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }

    public static AccessPass fromQueryFilter(QueryFilter q) {

        RecordSource source = q.getSource();

        String sourceName = source != null ? source.name() : null;

        return AccessPass.builder()
                .passType(q.getPassType())
                .aporType(q.getAporType())
                .referenceID(q.getReferenceId())
                .status(q.getStatus())
                .name(q.getName())
                .company(q.getCompany())
                .plateNumber(q.getPlateNumber())
                .source(sourceName)
                .build();
    }

    /**
     * Converts an {@link AccessPass} to {@link QrCodeData}
     *
     * @param accessPass access pass to convert
     * @param controlCode The control code derived from the access pass' id.
     */
    public static QrCodeData toQrCodeData(@NonNull AccessPass accessPass, String controlCode) {

        // Only generates QR code data if the user is approved.
        if (!AccessPassStatus.APPROVED.toString().equals(accessPass.getStatus()))
            throw new IllegalArgumentException("Failed to generate QR data. This access pass is not yet approved.");

        long decodedControlCode = ControlCode.decode(controlCode);

        // convert access pass to qr code data
        return PassType.INDIVIDUAL.toString().equalsIgnoreCase(accessPass.getPassType()) ?
                QrCodeData.individual()
                        .apor(accessPass.getAporType())
                        // long to int -> int = long / 1000
                        .validUntil((int) (accessPass.getValidTo().toEpochSecond()))
                        .validFrom((int) (accessPass.getValidFrom().toEpochSecond()))
                        .controlCode(decodedControlCode)
                        .idOrPlate(accessPass.getIdentifierNumber())
                        .build() :
                QrCodeData.vehicle()
                        .apor(accessPass.getAporType())
                        // long to int -> int = long / 1000
                        .validUntil((int) (accessPass.getValidTo().toEpochSecond()))
                        .validFrom((int) (accessPass.getValidFrom().toEpochSecond()))
                        .controlCode(decodedControlCode)
                        .idOrPlate(accessPass.getPlateNumber())
                        .build();
    }

}



