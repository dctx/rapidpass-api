/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.devcon.rapidpass.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;

/**
 *
 * @author eric
 */
@Entity
@Table(name = "access_pass")
@NamedQueries({
    @NamedQuery(name = "AccessPass.findAll", query = "SELECT a FROM AccessPass a"),
    @NamedQuery(name = "AccessPass.findById", query = "SELECT a FROM AccessPass a WHERE a.id = :id"),
    @NamedQuery(name = "AccessPass.findByReferenceId", query = "SELECT a FROM AccessPass a WHERE a.referenceId = :referenceId"),
    @NamedQuery(name = "AccessPass.findByPassType", query = "SELECT a FROM AccessPass a WHERE a.passType = :passType"),
    @NamedQuery(name = "AccessPass.findByAccessType", query = "SELECT a FROM AccessPass a WHERE a.accessType = :accessType"),
    @NamedQuery(name = "AccessPass.findByControlCode", query = "SELECT a FROM AccessPass a WHERE a.controlCode = :controlCode"),
    @NamedQuery(name = "AccessPass.findByIdType", query = "SELECT a FROM AccessPass a WHERE a.idType = :idType"),
    @NamedQuery(name = "AccessPass.findByPlateOrId", query = "SELECT a FROM AccessPass a WHERE a.plateOrId = :plateOrId"),
    @NamedQuery(name = "AccessPass.findByName", query = "SELECT a FROM AccessPass a WHERE a.name = :name"),
    @NamedQuery(name = "AccessPass.findByCompany", query = "SELECT a FROM AccessPass a WHERE a.company = :company"),
    @NamedQuery(name = "AccessPass.findByRemarks", query = "SELECT a FROM AccessPass a WHERE a.remarks = :remarks"),
    @NamedQuery(name = "AccessPass.findByScope", query = "SELECT a FROM AccessPass a WHERE a.scope = :scope"),
    @NamedQuery(name = "AccessPass.findByLimitations", query = "SELECT a FROM AccessPass a WHERE a.limitations = :limitations"),
    @NamedQuery(name = "AccessPass.findByOriginName", query = "SELECT a FROM AccessPass a WHERE a.originName = :originName"),
    @NamedQuery(name = "AccessPass.findByOriginAddress", query = "SELECT a FROM AccessPass a WHERE a.originAddress = :originAddress"),
    @NamedQuery(name = "AccessPass.findByOriginProvince", query = "SELECT a FROM AccessPass a WHERE a.originProvince = :originProvince"),
    @NamedQuery(name = "AccessPass.findByOriginCity", query = "SELECT a FROM AccessPass a WHERE a.originCity = :originCity"),
    @NamedQuery(name = "AccessPass.findByDestinationName", query = "SELECT a FROM AccessPass a WHERE a.destinationName = :destinationName"),
    @NamedQuery(name = "AccessPass.findByDestinationAddress", query = "SELECT a FROM AccessPass a WHERE a.destinationAddress = :destinationAddress"),
    @NamedQuery(name = "AccessPass.findByDestinationProvince", query = "SELECT a FROM AccessPass a WHERE a.destinationProvince = :destinationProvince"),
    @NamedQuery(name = "AccessPass.findByDestinationCity", query = "SELECT a FROM AccessPass a WHERE a.destinationCity = :destinationCity"),
    @NamedQuery(name = "AccessPass.findByValidFrom", query = "SELECT a FROM AccessPass a WHERE a.validFrom = :validFrom"),
    @NamedQuery(name = "AccessPass.findByValidTo", query = "SELECT a FROM AccessPass a WHERE a.validTo = :validTo"),
    @NamedQuery(name = "AccessPass.findByIssuedBy", query = "SELECT a FROM AccessPass a WHERE a.issuedBy = :issuedBy"),
    @NamedQuery(name = "AccessPass.findByUpdates", query = "SELECT a FROM AccessPass a WHERE a.updates = :updates"),
    @NamedQuery(name = "AccessPass.findByStatus", query = "SELECT a FROM AccessPass a WHERE a.status = :status"),
    @NamedQuery(name = "AccessPass.findByDateTimeCreated", query = "SELECT a FROM AccessPass a WHERE a.dateTimeCreated = :dateTimeCreated"),
    @NamedQuery(name = "AccessPass.findByDateTimeUpdated", query = "SELECT a FROM AccessPass a WHERE a.dateTimeUpdated = :dateTimeUpdated")})
public class AccessPass implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 30)
    @Column(name = "reference_id")
    private String referenceId;
    @Size(max = 10)
    @Column(name = "pass_type")
    private String passType;
    @Size(max = 10)
    @Column(name = "access_type")
    private String accessType;
    @Column(name = "control_code")
    private Integer controlCode;
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
    @Temporal(TemporalType.TIMESTAMP)
    private Date validFrom;
    @Column(name = "valid_to")
    @Temporal(TemporalType.TIMESTAMP)
    private Date validTo;
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
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateTimeCreated;
    @Column(name = "date_time_updated")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateTimeUpdated;
    @JoinColumn(name = "registrant_id", referencedColumnName = "id")
    @ManyToOne
    private Registrant registrantId;
    @OneToMany(mappedBy = "accessPassId")
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

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public String getPassType() {
        return passType;
    }

    public void setPassType(String passType) {
        this.passType = passType;
    }

    public String getAccessType() {
        return accessType;
    }

    public void setAccessType(String accessType) {
        this.accessType = accessType;
    }

    public Integer getControlCode() {
        return controlCode;
    }

    public void setControlCode(Integer controlCode) {
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

    public Date getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(Date validFrom) {
        this.validFrom = validFrom;
    }

    public Date getValidTo() {
        return validTo;
    }

    public void setValidTo(Date validTo) {
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

    public Date getDateTimeCreated() {
        return dateTimeCreated;
    }

    public void setDateTimeCreated(Date dateTimeCreated) {
        this.dateTimeCreated = dateTimeCreated;
    }

    public Date getDateTimeUpdated() {
        return dateTimeUpdated;
    }

    public void setDateTimeUpdated(Date dateTimeUpdated) {
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
        return "ph.devcon.rapidpass.model.AccessPass[ id=" + id + " ]";
    }
    
}
