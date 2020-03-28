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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;

/**
 *
 * @author eric
 */
@Entity
@Table(name = "registrar_user")
@NamedQueries({
    @NamedQuery(name = "RegistrarUser.findAll", query = "SELECT r FROM RegistrarUser r"),
    @NamedQuery(name = "RegistrarUser.findById", query = "SELECT r FROM RegistrarUser r WHERE r.id = :id"),
    @NamedQuery(name = "RegistrarUser.findByAccessType", query = "SELECT r FROM RegistrarUser r WHERE r.accessType = :accessType"),
    @NamedQuery(name = "RegistrarUser.findByRole", query = "SELECT r FROM RegistrarUser r WHERE r.role = :role"),
    @NamedQuery(name = "RegistrarUser.findByStatus", query = "SELECT r FROM RegistrarUser r WHERE r.status = :status"),
    @NamedQuery(name = "RegistrarUser.findByReference1", query = "SELECT r FROM RegistrarUser r WHERE r.reference1 = :reference1"),
    @NamedQuery(name = "RegistrarUser.findByReference2", query = "SELECT r FROM RegistrarUser r WHERE r.reference2 = :reference2"),
    @NamedQuery(name = "RegistrarUser.findByFirstName", query = "SELECT r FROM RegistrarUser r WHERE r.firstName = :firstName"),
    @NamedQuery(name = "RegistrarUser.findByLastName", query = "SELECT r FROM RegistrarUser r WHERE r.lastName = :lastName"),
    @NamedQuery(name = "RegistrarUser.findByAddress", query = "SELECT r FROM RegistrarUser r WHERE r.address = :address"),
    @NamedQuery(name = "RegistrarUser.findByProvince", query = "SELECT r FROM RegistrarUser r WHERE r.province = :province"),
    @NamedQuery(name = "RegistrarUser.findByCity", query = "SELECT r FROM RegistrarUser r WHERE r.city = :city"),
    @NamedQuery(name = "RegistrarUser.findByMobile", query = "SELECT r FROM RegistrarUser r WHERE r.mobile = :mobile"),
    @NamedQuery(name = "RegistrarUser.findByEmail", query = "SELECT r FROM RegistrarUser r WHERE r.email = :email"),
    @NamedQuery(name = "RegistrarUser.findBySocmed1", query = "SELECT r FROM RegistrarUser r WHERE r.socmed1 = :socmed1"),
    @NamedQuery(name = "RegistrarUser.findBySocmed2", query = "SELECT r FROM RegistrarUser r WHERE r.socmed2 = :socmed2"),
    @NamedQuery(name = "RegistrarUser.findByUsername", query = "SELECT r FROM RegistrarUser r WHERE r.username = :username"),
    @NamedQuery(name = "RegistrarUser.findByPassword", query = "SELECT r FROM RegistrarUser r WHERE r.password = :password"),
    @NamedQuery(name = "RegistrarUser.findByAccessKey", query = "SELECT r FROM RegistrarUser r WHERE r.accessKey = :accessKey"),
    @NamedQuery(name = "RegistrarUser.findByComment", query = "SELECT r FROM RegistrarUser r WHERE r.comment = :comment"),
    @NamedQuery(name = "RegistrarUser.findByUpdates", query = "SELECT r FROM RegistrarUser r WHERE r.updates = :updates"),
    @NamedQuery(name = "RegistrarUser.findByValidFrom", query = "SELECT r FROM RegistrarUser r WHERE r.validFrom = :validFrom"),
    @NamedQuery(name = "RegistrarUser.findByValidTo", query = "SELECT r FROM RegistrarUser r WHERE r.validTo = :validTo"),
    @NamedQuery(name = "RegistrarUser.findByDateTimeCreated", query = "SELECT r FROM RegistrarUser r WHERE r.dateTimeCreated = :dateTimeCreated"),
    @NamedQuery(name = "RegistrarUser.findByDateTimeUpdated", query = "SELECT r FROM RegistrarUser r WHERE r.dateTimeUpdated = :dateTimeUpdated")})
public class RegistrarUser implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 50)
    @Column(name = "access_type")
    private String accessType;
    @Size(max = 50)
    @Column(name = "role")
    private String role;
    @Size(max = 20)
    @Column(name = "status")
    private String status;
    @Size(max = 50)
    @Column(name = "reference1")
    private String reference1;
    @Size(max = 50)
    @Column(name = "reference2")
    private String reference2;
    @Size(max = 50)
    @Column(name = "first_name")
    private String firstName;
    @Size(max = 50)
    @Column(name = "last_name")
    private String lastName;
    @Size(max = 150)
    @Column(name = "address")
    private String address;
    @Size(max = 50)
    @Column(name = "province")
    private String province;
    @Size(max = 50)
    @Column(name = "city")
    private String city;
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
    @Size(max = 20)
    @Column(name = "username")
    private String username;
    @Size(max = 140)
    @Column(name = "password")
    private String password;
    @Size(max = 140)
    @Column(name = "access_key")
    private String accessKey;
    @Size(max = 2147483647)
    @Column(name = "comment")
    private String comment;
    @Size(max = 2147483647)
    @Column(name = "updates")
    private String updates;
    @Column(name = "valid_from")
    
    private Date validFrom;
    @Column(name = "valid_to")
    
    private Date validTo;
    @Column(name = "date_time_created")
    
    private OffsetDateTime dateTimeCreated;
    @Column(name = "date_time_updated")
    
    private OffsetDateTime dateTimeUpdated;
    @OneToMany(mappedBy = "userId")
    private Collection<RegistrarUserActivityLog> registrarUserActivityLogCollection;
    @JoinColumn(name = "registrar_id", referencedColumnName = "id")
    @ManyToOne
    private Registrar registrarId;
    @OneToMany(mappedBy = "registrarUserId")
    private Collection<ScannerDevice> scannerDeviceCollection;

    public RegistrarUser() {
    }

    public RegistrarUser(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAccessType() {
        return accessType;
    }

    public void setAccessType(String accessType) {
        this.accessType = accessType;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
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

    public Collection<RegistrarUserActivityLog> getRegistrarUserActivityLogCollection() {
        return registrarUserActivityLogCollection;
    }

    public void setRegistrarUserActivityLogCollection(Collection<RegistrarUserActivityLog> registrarUserActivityLogCollection) {
        this.registrarUserActivityLogCollection = registrarUserActivityLogCollection;
    }

    public Registrar getRegistrarId() {
        return registrarId;
    }

    public void setRegistrarId(Registrar registrarId) {
        this.registrarId = registrarId;
    }

    public Collection<ScannerDevice> getScannerDeviceCollection() {
        return scannerDeviceCollection;
    }

    public void setScannerDeviceCollection(Collection<ScannerDevice> scannerDeviceCollection) {
        this.scannerDeviceCollection = scannerDeviceCollection;
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
        if (!(object instanceof RegistrarUser)) {
            return false;
        }
        RegistrarUser other = (RegistrarUser) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ph.devcon.rapidpass.entities.RegistrarUser[ id=" + id + " ]";
    }
    
}
