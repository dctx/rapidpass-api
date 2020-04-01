/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.devcon.rapidpass.entities;

import lombok.*;
import org.springframework.util.StringUtils;
import ph.devcon.dctx.rapidpass.model.ControlCode;
import ph.devcon.dctx.rapidpass.model.QrCodeData;
import ph.devcon.rapidpass.enums.AccessPassStatus;
import ph.devcon.rapidpass.enums.PassType;

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
    @Size(max = 150)
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
    @Column(name = "date_time_created")

    private OffsetDateTime dateTimeCreated;
    @Column(name = "date_time_updated")

    private OffsetDateTime dateTimeUpdated;

    @Column(name = "last_used_on")

    private OffsetDateTime lastUsedOn;

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

    /**
     * Converts an {@link AccessPass} to {@link QrCodeData}
     *
     * @param accessPass access pass to convert
     */
    public static QrCodeData toQrCodeData(@NonNull AccessPass accessPass) {

        if (StringUtils.isEmpty(accessPass.getControlCode()))
            throw new IllegalArgumentException("The control code is invalid. [controlCode=" + accessPass.getControlCode() + "]");

        long decodedControlCode = ControlCode.decode(accessPass.getControlCode());

        // convert access pass to qr code data
        return PassType.INDIVIDUAL.toString().equalsIgnoreCase(accessPass.getPassType()) ?
                QrCodeData.individual()
                        .apor(accessPass.getAporType())
                        // long to int -> int = long / 1000
                        .validUntil((int) (accessPass.getValidTo().toEpochSecond() / 1000))
                        .validFrom((int) (accessPass.getValidFrom().toEpochSecond() / 1000))
                        .controlCode(decodedControlCode)
                        .idOrPlate(accessPass.getIdentifierNumber())
                        .build() :
                QrCodeData.vehicle()
                        .apor(accessPass.getAporType())
                        // long to int -> int = long / 1000
                        .validUntil((int) (accessPass.getValidTo().toEpochSecond() / 1000))
                        .validFrom((int) (accessPass.getValidFrom().toEpochSecond() / 1000))
                        .controlCode(decodedControlCode)
                        .idOrPlate(accessPass.getIdentifierNumber())
                        .build();
    }

}



