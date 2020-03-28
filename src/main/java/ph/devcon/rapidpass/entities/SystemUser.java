/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.devcon.rapidpass.entities;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
@Table(name = "system_user")
@NamedQueries({
    @NamedQuery(name = "SystemUser.findAll", query = "SELECT s FROM SystemUser s"),
    @NamedQuery(name = "SystemUser.findById", query = "SELECT s FROM SystemUser s WHERE s.id = :id"),
    @NamedQuery(name = "SystemUser.findByRole", query = "SELECT s FROM SystemUser s WHERE s.role = :role"),
    @NamedQuery(name = "SystemUser.findByName", query = "SELECT s FROM SystemUser s WHERE s.name = :name"),
    @NamedQuery(name = "SystemUser.findByUsername", query = "SELECT s FROM SystemUser s WHERE s.username = :username"),
    @NamedQuery(name = "SystemUser.findByPassword", query = "SELECT s FROM SystemUser s WHERE s.password = :password"),
    @NamedQuery(name = "SystemUser.findByAccessKey", query = "SELECT s FROM SystemUser s WHERE s.accessKey = :accessKey"),
    @NamedQuery(name = "SystemUser.findByEmail", query = "SELECT s FROM SystemUser s WHERE s.email = :email"),
    @NamedQuery(name = "SystemUser.findByMobile", query = "SELECT s FROM SystemUser s WHERE s.mobile = :mobile"),
    @NamedQuery(name = "SystemUser.findByDescription", query = "SELECT s FROM SystemUser s WHERE s.description = :description"),
    @NamedQuery(name = "SystemUser.findByValidFrom", query = "SELECT s FROM SystemUser s WHERE s.validFrom = :validFrom"),
    @NamedQuery(name = "SystemUser.findByValidTo", query = "SELECT s FROM SystemUser s WHERE s.validTo = :validTo"),
    @NamedQuery(name = "SystemUser.findByUpdates", query = "SELECT s FROM SystemUser s WHERE s.updates = :updates"),
    @NamedQuery(name = "SystemUser.findByPasswordHistory", query = "SELECT s FROM SystemUser s WHERE s.passwordHistory = :passwordHistory"),
    @NamedQuery(name = "SystemUser.findByStatus", query = "SELECT s FROM SystemUser s WHERE s.status = :status"),
    @NamedQuery(name = "SystemUser.findByCreatedBy", query = "SELECT s FROM SystemUser s WHERE s.createdBy = :createdBy"),
    @NamedQuery(name = "SystemUser.findByDateTimeCreated", query = "SELECT s FROM SystemUser s WHERE s.dateTimeCreated = :dateTimeCreated")})
public class SystemUser implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 20)
    @Column(name = "role")
    private String role;
    @Size(max = 100)
    @Column(name = "name")
    private String name;
    @Size(max = 50)
    @Column(name = "username")
    private String username;
    @Size(max = 140)
    @Column(name = "password")
    private String password;
    @Size(max = 140)
    @Column(name = "access_key")
    private String accessKey;
    // @Pattern(regexp="[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", message="Invalid email")//if the field contains email address consider using this annotation to enforce field validation
    @Size(max = 50)
    @Column(name = "email")
    private String email;
    @Size(max = 50)
    @Column(name = "mobile")
    private String mobile;
    @Size(max = 2147483647)
    @Column(name = "description")
    private String description;
    @Column(name = "valid_from")
    @Temporal(TemporalType.TIMESTAMP)
    private Date validFrom;
    @Column(name = "valid_to")
    @Temporal(TemporalType.TIMESTAMP)
    private Date validTo;
    @Size(max = 2147483647)
    @Column(name = "updates")
    private String updates;
    @Size(max = 2147483647)
    @Column(name = "password_history")
    private String passwordHistory;
    @Size(max = 15)
    @Column(name = "status")
    private String status;
    @Size(max = 20)
    @Column(name = "created_by")
    private String createdBy;
    @Column(name = "date_time_created")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateTimeCreated;
    @OneToMany(mappedBy = "userId")
    private Collection<SystemUserActivityLog> systemUserActivityLogCollection;

    public SystemUser() {
    }

    public SystemUser(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getUpdates() {
        return updates;
    }

    public void setUpdates(String updates) {
        this.updates = updates;
    }

    public String getPasswordHistory() {
        return passwordHistory;
    }

    public void setPasswordHistory(String passwordHistory) {
        this.passwordHistory = passwordHistory;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getDateTimeCreated() {
        return dateTimeCreated;
    }

    public void setDateTimeCreated(Date dateTimeCreated) {
        this.dateTimeCreated = dateTimeCreated;
    }

    public Collection<SystemUserActivityLog> getSystemUserActivityLogCollection() {
        return systemUserActivityLogCollection;
    }

    public void setSystemUserActivityLogCollection(Collection<SystemUserActivityLog> systemUserActivityLogCollection) {
        this.systemUserActivityLogCollection = systemUserActivityLogCollection;
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
        if (!(object instanceof SystemUser)) {
            return false;
        }
        SystemUser other = (SystemUser) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ph.devcon.rapidpass.entities.SystemUser[ id=" + id + " ]";
    }
    
}
