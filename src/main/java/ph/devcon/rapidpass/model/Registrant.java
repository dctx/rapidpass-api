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
@Table(name = "registrant")
@NamedQueries({
    @NamedQuery(name = "Registrant.findAll", query = "SELECT r FROM Registrant r"),
    @NamedQuery(name = "Registrant.findById", query = "SELECT r FROM Registrant r WHERE r.id = :id"),
    @NamedQuery(name = "Registrant.findByRegistrantType", query = "SELECT r FROM Registrant r WHERE r.registrantType = :registrantType"),
    @NamedQuery(name = "Registrant.findByRegistrantName", query = "SELECT r FROM Registrant r WHERE r.registrantName = :registrantName"),
    @NamedQuery(name = "Registrant.findByPriority", query = "SELECT r FROM Registrant r WHERE r.priority = :priority"),
    @NamedQuery(name = "Registrant.findByOrganizationName", query = "SELECT r FROM Registrant r WHERE r.organizationName = :organizationName"),
    @NamedQuery(name = "Registrant.findByOrganizationId", query = "SELECT r FROM Registrant r WHERE r.organizationId = :organizationId"),
    @NamedQuery(name = "Registrant.findByOrganizationClass", query = "SELECT r FROM Registrant r WHERE r.organizationClass = :organizationClass"),
    @NamedQuery(name = "Registrant.findByReferenceIdType", query = "SELECT r FROM Registrant r WHERE r.referenceIdType = :referenceIdType"),
    @NamedQuery(name = "Registrant.findByReferenceId", query = "SELECT r FROM Registrant r WHERE r.referenceId = :referenceId"),
    @NamedQuery(name = "Registrant.findByFirstName", query = "SELECT r FROM Registrant r WHERE r.firstName = :firstName"),
    @NamedQuery(name = "Registrant.findByLastName", query = "SELECT r FROM Registrant r WHERE r.lastName = :lastName"),
    @NamedQuery(name = "Registrant.findByBirthDate", query = "SELECT r FROM Registrant r WHERE r.birthDate = :birthDate"),
    @NamedQuery(name = "Registrant.findByAddress", query = "SELECT r FROM Registrant r WHERE r.address = :address"),
    @NamedQuery(name = "Registrant.findByProvince", query = "SELECT r FROM Registrant r WHERE r.province = :province"),
    @NamedQuery(name = "Registrant.findByCity", query = "SELECT r FROM Registrant r WHERE r.city = :city"),
    @NamedQuery(name = "Registrant.findByWorkName", query = "SELECT r FROM Registrant r WHERE r.workName = :workName"),
    @NamedQuery(name = "Registrant.findByWorkAddress", query = "SELECT r FROM Registrant r WHERE r.workAddress = :workAddress"),
    @NamedQuery(name = "Registrant.findByWorkProvince", query = "SELECT r FROM Registrant r WHERE r.workProvince = :workProvince"),
    @NamedQuery(name = "Registrant.findByWorkCity", query = "SELECT r FROM Registrant r WHERE r.workCity = :workCity"),
    @NamedQuery(name = "Registrant.findByMobile", query = "SELECT r FROM Registrant r WHERE r.mobile = :mobile"),
    @NamedQuery(name = "Registrant.findByEmail", query = "SELECT r FROM Registrant r WHERE r.email = :email"),
    @NamedQuery(name = "Registrant.findBySocmed1", query = "SELECT r FROM Registrant r WHERE r.socmed1 = :socmed1"),
    @NamedQuery(name = "Registrant.findBySocmed2", query = "SELECT r FROM Registrant r WHERE r.socmed2 = :socmed2"),
    @NamedQuery(name = "Registrant.findByComment", query = "SELECT r FROM Registrant r WHERE r.comment = :comment"),
    @NamedQuery(name = "Registrant.findByUpdates", query = "SELECT r FROM Registrant r WHERE r.updates = :updates"),
    @NamedQuery(name = "Registrant.findByStatus", query = "SELECT r FROM Registrant r WHERE r.status = :status"),
    @NamedQuery(name = "Registrant.findByDateTimeCreated", query = "SELECT r FROM Registrant r WHERE r.dateTimeCreated = :dateTimeCreated"),
    @NamedQuery(name = "Registrant.findByDateTimeUpdated", query = "SELECT r FROM Registrant r WHERE r.dateTimeUpdated = :dateTimeUpdated")})
public class Registrant implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "registrant_type")
    private Integer registrantType;
    @Size(max = 50)
    @Column(name = "registrant_name")
    private String registrantName;
    @Column(name = "priority")
    private Integer priority;
    @Size(max = 10)
    @Column(name = "organization_name")
    private String organizationName;
    @Size(max = 10)
    @Column(name = "organization_id")
    private String organizationId;
    @Size(max = 10)
    @Column(name = "organization_class")
    private String organizationClass;
    @Size(max = 20)
    @Column(name = "reference_id_type")
    private String referenceIdType;
    @Size(max = 50)
    @Column(name = "reference_id")
    private String referenceId;
    @Size(max = 50)
    @Column(name = "first_name")
    private String firstName;
    @Size(max = 50)
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "birth_date")
    @Temporal(TemporalType.DATE)
    private Date birthDate;
    @Size(max = 150)
    @Column(name = "address")
    private String address;
    @Size(max = 50)
    @Column(name = "province")
    private String province;
    @Size(max = 50)
    @Column(name = "city")
    private String city;
    @Size(max = 100)
    @Column(name = "work_name")
    private String workName;
    @Size(max = 150)
    @Column(name = "work_address")
    private String workAddress;
    @Size(max = 50)
    @Column(name = "work_province")
    private String workProvince;
    @Size(max = 50)
    @Column(name = "work_city")
    private String workCity;
    @Size(max = 50)
    @Column(name = "mobile")
    private String mobile;
    // @Pattern(regexp="[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", message="Invalid email")//if the field contains email address consider using this annotation to enforce field validation
    @Size(max = 50)
    @Column(name = "email")
    private String email;
    @Size(max = 50)
    @Column(name = "socmed1")
    private String socmed1;
    @Size(max = 50)
    @Column(name = "socmed2")
    private String socmed2;
    @Size(max = 2147483647)
    @Column(name = "comment")
    private String comment;
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
    @OneToMany(mappedBy = "registrantId")
    private Collection<AccessPass> accessPassCollection;
    @JoinColumn(name = "registrar_id", referencedColumnName = "id")
    @ManyToOne
    private Registrar registrarId;

    public Registrant() {
    }

    public Registrant(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getRegistrantType() {
        return registrantType;
    }

    public void setRegistrantType(Integer registrantType) {
        this.registrantType = registrantType;
    }

    public String getRegistrantName() {
        return registrantName;
    }

    public void setRegistrantName(String registrantName) {
        this.registrantName = registrantName;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public String getOrganizationClass() {
        return organizationClass;
    }

    public void setOrganizationClass(String organizationClass) {
        this.organizationClass = organizationClass;
    }

    public String getReferenceIdType() {
        return referenceIdType;
    }

    public void setReferenceIdType(String referenceIdType) {
        this.referenceIdType = referenceIdType;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public String getWorkName() {
        return workName;
    }

    public void setWorkName(String workName) {
        this.workName = workName;
    }

    public String getWorkAddress() {
        return workAddress;
    }

    public void setWorkAddress(String workAddress) {
        this.workAddress = workAddress;
    }

    public String getWorkProvince() {
        return workProvince;
    }

    public void setWorkProvince(String workProvince) {
        this.workProvince = workProvince;
    }

    public String getWorkCity() {
        return workCity;
    }

    public void setWorkCity(String workCity) {
        this.workCity = workCity;
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

    public String getSocmed1() {
        return socmed1;
    }

    public void setSocmed1(String socmed1) {
        this.socmed1 = socmed1;
    }

    public String getSocmed2() {
        return socmed2;
    }

    public void setSocmed2(String socmed2) {
        this.socmed2 = socmed2;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
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

    public Collection<AccessPass> getAccessPassCollection() {
        return accessPassCollection;
    }

    public void setAccessPassCollection(Collection<AccessPass> accessPassCollection) {
        this.accessPassCollection = accessPassCollection;
    }

    public Registrar getRegistrarId() {
        return registrarId;
    }

    public void setRegistrarId(Registrar registrarId) {
        this.registrarId = registrarId;
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
        if (!(object instanceof Registrant)) {
            return false;
        }
        Registrant other = (Registrant) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ph.devcon.rapidpass.model.Registrant[ id=" + id + " ]";
    }
    
}
