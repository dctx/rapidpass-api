/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.devcon.rapidpass.entities;

import java.io.Serializable;
import java.time.OffsetDateTime;
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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;

/**
 *
 * @author eric
 */
@Entity
@Table(name = "registrar")
@NamedQueries({
    @NamedQuery(name = "Registrar.findAll", query = "SELECT r FROM Registrar r"),
    @NamedQuery(name = "Registrar.findById", query = "SELECT r FROM Registrar r WHERE r.id = :id"),
    @NamedQuery(name = "Registrar.findByName", query = "SELECT r FROM Registrar r WHERE r.name = :name"),
    @NamedQuery(name = "Registrar.findByShortName", query = "SELECT r FROM Registrar r WHERE r.shortName = :shortName"),
    @NamedQuery(name = "Registrar.findByDescription", query = "SELECT r FROM Registrar r WHERE r.description = :description"),
    @NamedQuery(name = "Registrar.findByShardKey", query = "SELECT r FROM Registrar r WHERE r.shardKey = :shardKey"),
    @NamedQuery(name = "Registrar.findByInstitutionType", query = "SELECT r FROM Registrar r WHERE r.institutionType = :institutionType"),
    @NamedQuery(name = "Registrar.findByInstitutionClassification", query = "SELECT r FROM Registrar r WHERE r.institutionClassification = :institutionClassification"),
    @NamedQuery(name = "Registrar.findByStatus", query = "SELECT r FROM Registrar r WHERE r.status = :status"),
    @NamedQuery(name = "Registrar.findByAddress", query = "SELECT r FROM Registrar r WHERE r.address = :address"),
    @NamedQuery(name = "Registrar.findByCountry", query = "SELECT r FROM Registrar r WHERE r.country = :country"),
    @NamedQuery(name = "Registrar.findByRegion", query = "SELECT r FROM Registrar r WHERE r.region = :region"),
    @NamedQuery(name = "Registrar.findByProvince", query = "SELECT r FROM Registrar r WHERE r.province = :province"),
    @NamedQuery(name = "Registrar.findByCity", query = "SELECT r FROM Registrar r WHERE r.city = :city"),
    @NamedQuery(name = "Registrar.findByZipCode", query = "SELECT r FROM Registrar r WHERE r.zipCode = :zipCode"),
    @NamedQuery(name = "Registrar.findByPhone", query = "SELECT r FROM Registrar r WHERE r.phone = :phone"),
    @NamedQuery(name = "Registrar.findByMobile", query = "SELECT r FROM Registrar r WHERE r.mobile = :mobile"),
    @NamedQuery(name = "Registrar.findByEmail", query = "SELECT r FROM Registrar r WHERE r.email = :email"),
    @NamedQuery(name = "Registrar.findByRepresentative", query = "SELECT r FROM Registrar r WHERE r.representative = :representative"),
    @NamedQuery(name = "Registrar.findByRepDesignation", query = "SELECT r FROM Registrar r WHERE r.repDesignation = :repDesignation"),
    @NamedQuery(name = "Registrar.findByRepPhone", query = "SELECT r FROM Registrar r WHERE r.repPhone = :repPhone"),
    @NamedQuery(name = "Registrar.findByRepMobile", query = "SELECT r FROM Registrar r WHERE r.repMobile = :repMobile"),
    @NamedQuery(name = "Registrar.findByRepEmail", query = "SELECT r FROM Registrar r WHERE r.repEmail = :repEmail"),
    @NamedQuery(name = "Registrar.findByWebsite", query = "SELECT r FROM Registrar r WHERE r.website = :website"),
    @NamedQuery(name = "Registrar.findByReference1", query = "SELECT r FROM Registrar r WHERE r.reference1 = :reference1"),
    @NamedQuery(name = "Registrar.findByReference2", query = "SELECT r FROM Registrar r WHERE r.reference2 = :reference2"),
    @NamedQuery(name = "Registrar.findByUpdates", query = "SELECT r FROM Registrar r WHERE r.updates = :updates"),
    @NamedQuery(name = "Registrar.findByDateTimeCreated", query = "SELECT r FROM Registrar r WHERE r.dateTimeCreated = :dateTimeCreated"),
    @NamedQuery(name = "Registrar.findByDateTimeUpdated", query = "SELECT r FROM Registrar r WHERE r.dateTimeUpdated = :dateTimeUpdated")})
public class Registrar implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "registrar_id_generator")
    // FIXME: temporarily set to 1 to compile. will have to fix.
    @SequenceGenerator(name="registrar_id_generator", sequenceName = "registrar_id_seq", allocationSize=1)
    @Basic(optional = false)
    @Column(name = "id", updatable = false, nullable = false)
    private Integer id;
    @Size(max = 150)
    @Column(name = "name")
    private String name;
    @Size(max = 30)
    @Column(name = "short_name")
    private String shortName;
    @Size(max = 2147483647)
    @Column(name = "description")
    private String description;
    @Size(max = 5)
    @Column(name = "shard_key")
    private String shardKey;
    @Size(max = 15)
    @Column(name = "institution_type")
    private String institutionType;
    @Size(max = 25)
    @Column(name = "institution_classification")
    private String institutionClassification;
    @Size(max = 15)
    @Column(name = "status")
    private String status;
    @Size(max = 2147483647)
    @Column(name = "address")
    private String address;
    @Size(max = 50)
    @Column(name = "country")
    private String country;
    @Size(max = 50)
    @Column(name = "region")
    private String region;
    @Size(max = 50)
    @Column(name = "province")
    private String province;
    @Size(max = 50)
    @Column(name = "city")
    private String city;
    @Size(max = 5)
    @Column(name = "zip_code")
    private String zipCode;
    // @Pattern(regexp="^\\(?(\\d{3})\\)?[- ]?(\\d{3})[- ]?(\\d{4})$", message="Invalid phone/fax format, should be as xxx-xxx-xxxx")//if the field contains phone or fax number consider using this annotation to enforce field validation
    @Size(max = 50)
    @Column(name = "phone")
    private String phone;
    @Size(max = 50)
    @Column(name = "mobile")
    private String mobile;
    // @Pattern(regexp="[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", message="Invalid email")//if the field contains email address consider using this annotation to enforce field validation
    @Size(max = 50)
    @Column(name = "email")
    private String email;
    @Size(max = 100)
    @Column(name = "representative")
    private String representative;
    @Size(max = 50)
    @Column(name = "rep_designation")
    private String repDesignation;
    @Size(max = 50)
    @Column(name = "rep_phone")
    private String repPhone;
    @Size(max = 50)
    @Column(name = "rep_mobile")
    private String repMobile;
    @Size(max = 50)
    @Column(name = "rep_email")
    private String repEmail;
    @Size(max = 100)
    @Column(name = "website")
    private String website;
    @Size(max = 50)
    @Column(name = "reference1")
    private String reference1;
    @Size(max = 50)
    @Column(name = "reference2")
    private String reference2;
    @Size(max = 2147483647)
    @Column(name = "updates")
    private String updates;
    @Column(name = "date_time_created")
    
    private OffsetDateTime dateTimeCreated;
    @Column(name = "date_time_updated")
    
    private OffsetDateTime dateTimeUpdated;
    @OneToMany(mappedBy = "parentRegistrarId")
    private Collection<Registrar> registrarCollection;
    @JoinColumn(name = "parent_registrar_id", referencedColumnName = "id")
    @ManyToOne
    private Registrar parentRegistrarId;
    @OneToMany(mappedBy = "registrarId")
    private Collection<RegistrarUser> registrarUserCollection;
    @OneToMany(mappedBy = "registrarId")
    private Collection<ScannerDevice> scannerDeviceCollection;
    @OneToMany(mappedBy = "registrarId")
    private Collection<Registrant> registrantCollection;

    public Registrar() {
    }

    public Registrar(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getShardKey() {
        return shardKey;
    }

    public void setShardKey(String shardKey) {
        this.shardKey = shardKey;
    }

    public String getInstitutionType() {
        return institutionType;
    }

    public void setInstitutionType(String institutionType) {
        this.institutionType = institutionType;
    }

    public String getInstitutionClassification() {
        return institutionClassification;
    }

    public void setInstitutionClassification(String institutionClassification) {
        this.institutionClassification = institutionClassification;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRepresentative() {
        return representative;
    }

    public void setRepresentative(String representative) {
        this.representative = representative;
    }

    public String getRepDesignation() {
        return repDesignation;
    }

    public void setRepDesignation(String repDesignation) {
        this.repDesignation = repDesignation;
    }

    public String getRepPhone() {
        return repPhone;
    }

    public void setRepPhone(String repPhone) {
        this.repPhone = repPhone;
    }

    public String getRepMobile() {
        return repMobile;
    }

    public void setRepMobile(String repMobile) {
        this.repMobile = repMobile;
    }

    public String getRepEmail() {
        return repEmail;
    }

    public void setRepEmail(String repEmail) {
        this.repEmail = repEmail;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getReference1() {
        return reference1;
    }

    public void setReference1(String reference1) {
        this.reference1 = reference1;
    }

    public String getReference2() {
        return reference2;
    }

    public void setReference2(String reference2) {
        this.reference2 = reference2;
    }

    public String getUpdates() {
        return updates;
    }

    public void setUpdates(String updates) {
        this.updates = updates;
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

    public void setDateTimeUpdated(OffsetDateTime dateTimeUpdated) {
        this.dateTimeUpdated = dateTimeUpdated;
    }

    public Collection<Registrar> getRegistrarCollection() {
        return registrarCollection;
    }

    public void setRegistrarCollection(Collection<Registrar> registrarCollection) {
        this.registrarCollection = registrarCollection;
    }

    public Registrar getParentRegistrarId() {
        return parentRegistrarId;
    }

    public void setParentRegistrarId(Registrar parentRegistrarId) {
        this.parentRegistrarId = parentRegistrarId;
    }

    public Collection<RegistrarUser> getRegistrarUserCollection() {
        return registrarUserCollection;
    }

    public void setRegistrarUserCollection(Collection<RegistrarUser> registrarUserCollection) {
        this.registrarUserCollection = registrarUserCollection;
    }

    public Collection<ScannerDevice> getScannerDeviceCollection() {
        return scannerDeviceCollection;
    }

    public void setScannerDeviceCollection(Collection<ScannerDevice> scannerDeviceCollection) {
        this.scannerDeviceCollection = scannerDeviceCollection;
    }

    public Collection<Registrant> getRegistrantCollection() {
        return registrantCollection;
    }

    public void setRegistrantCollection(Collection<Registrant> registrantCollection) {
        this.registrantCollection = registrantCollection;
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
        if (!(object instanceof Registrar)) {
            return false;
        }
        Registrar other = (Registrar) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ph.devcon.rapidpass.entities.Registrar[ id=" + id + " ]";
    }
    
}
