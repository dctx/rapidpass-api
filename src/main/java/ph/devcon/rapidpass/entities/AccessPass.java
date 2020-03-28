/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.devcon.rapidpass.entities;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Collection;

/**
 *
 * @author eric
 */
@Entity
@Table(name = "access_pass", schema = "public")
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
    @Column(name = "plate_or_id")
    private String plateOrId;
    @Size(max = 100)
    @Column(name = "name")
    private String name;
    @Size(max = 100)
    @Column(name = "company")
    private String company;
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
    @Column(name = "origin_address")
    private String originAddress;
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
    @Column(name = "destination_address")
    private String destinationAddress;
    @Size(max = 50)
    @Column(name = "destination_province")
    private String destinationProvince;
    @Size(max = 50)
    @Column(name = "destination_city")
    private String destinationCity;
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
    @OneToMany(mappedBy = "accessPassId",fetch = FetchType.LAZY)
    private Collection<AccessPassLog> accessPassLogCollection;

    public AccessPass() {
    }

    public AccessPass(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getReferenceID() {
        return referenceID;
    }

    public void setReferenceID(String referenceId) {
        this.referenceID = referenceId;
    }

    public String getPassType() {
        return passType;
    }

    public void setPassType(String passType) {
        this.passType = passType;
    }

    public String getAporType() {
        return aporType;
    }

    public void setAporType(String aporType) {
        this.aporType = aporType;
    }

    public String getControlCode() {
        return controlCode;
    }

    public void setControlCode(String controlCode) {
        this.controlCode = controlCode;
    }

    public String getIdType() {
        return idType;
    }

    public void setIdType(String idType) {
        this.idType = idType;
    }

    public String getPlateOrId() {
        return plateOrId;
    }

    public void setPlateOrId(String plateOrId) {
        this.plateOrId = plateOrId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Integer getScope() {
        return scope;
    }

    public void setScope(Integer scope) {
        this.scope = scope;
    }

    public String getLimitations() {
        return limitations;
    }

    public void setLimitations(String limitations) {
        this.limitations = limitations;
    }

    public String getOriginName() {
        return originName;
    }

    public void setOriginName(String originName) {
        this.originName = originName;
    }

    public String getOriginAddress() {
        return originAddress;
    }

    public void setOriginAddress(String originAddress) {
        this.originAddress = originAddress;
    }

    public String getOriginProvince() {
        return originProvince;
    }

    public void setOriginProvince(String originProvince) {
        this.originProvince = originProvince;
    }

    public String getOriginCity() {
        return originCity;
    }

    public void setOriginCity(String originCity) {
        this.originCity = originCity;
    }

    public String getDestinationName() {
        return destinationName;
    }

    public void setDestinationName(String destinationName) {
        this.destinationName = destinationName;
    }

    public String getDestinationAddress() {
        return destinationAddress;
    }

    public void setDestinationAddress(String destinationAddress) {
        this.destinationAddress = destinationAddress;
    }

    public String getDestinationProvince() {
        return destinationProvince;
    }

    public void setDestinationProvince(String destinationProvince) {
        this.destinationProvince = destinationProvince;
    }

    public String getDestinationCity() {
        return destinationCity;
    }

    public void setDestinationCity(String destinationCity) {
        this.destinationCity = destinationCity;
    }

    public OffsetDateTime getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(OffsetDateTime validFrom) {
        this.validFrom = validFrom;
    }

    public OffsetDateTime getValidTo() {
        return validTo;
    }

    public void setValidTo(OffsetDateTime validTo) {
        this.validTo = validTo;
    }

    public String getIssuedBy() {
        return issuedBy;
    }

    public void setIssuedBy(String issuedBy) {
        this.issuedBy = issuedBy;
    }

    public String getUpdates() {
        return updates;
    }

    public void setUpdates(String updates) {
        this.updates = updates;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public OffsetDateTime getDateTimeCreated() {
        return dateTimeCreated;
    }

    public void setDateTimeCreated(OffsetDateTime dateTimeCreated) {
        this.dateTimeCreated = dateTimeCreated;
    }

    public OffsetDateTime getDateTimeUpdated() {
        return dateTimeUpdated;
    }
    
    public OffsetDateTime getLastUsedOn()
    {
        return lastUsedOn;
    }
    
    public void setLastUsedOn(OffsetDateTime lastUsedOn)
    {
        this.lastUsedOn = lastUsedOn;
    }
    
    public void setDateTimeUpdated(OffsetDateTime dateTimeUpdated) {
        this.dateTimeUpdated = dateTimeUpdated;
    }

    public Registrant getRegistrantId() {
        return registrantId;
    }

    public void setRegistrantId(Registrant registrantId) {
        this.registrantId = registrantId;
    }

    public Collection<AccessPassLog> getAccessPassLogCollection() {
        return accessPassLogCollection;
    }

    public void setAccessPassLogCollection(Collection<AccessPassLog> accessPassLogCollection) {
        this.accessPassLogCollection = accessPassLogCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AccessPass)) {
            return false;
        }
        AccessPass other = (AccessPass) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ph.devcon.rapidpass.entities.AccessPass[ id=" + id + " ]";
    }
    
}
