/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.devcon.rapidpass.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author eric
 */
@Entity
@Table(name = "databasechangeloglock")
@NamedQueries({
    @NamedQuery(name = "Databasechangeloglock.findAll", query = "SELECT d FROM Databasechangeloglock d"),
    @NamedQuery(name = "Databasechangeloglock.findById", query = "SELECT d FROM Databasechangeloglock d WHERE d.id = :id"),
    @NamedQuery(name = "Databasechangeloglock.findByLocked", query = "SELECT d FROM Databasechangeloglock d WHERE d.locked = :locked"),
    @NamedQuery(name = "Databasechangeloglock.findByLockgranted", query = "SELECT d FROM Databasechangeloglock d WHERE d.lockgranted = :lockgranted"),
    @NamedQuery(name = "Databasechangeloglock.findByLockedby", query = "SELECT d FROM Databasechangeloglock d WHERE d.lockedby = :lockedby")})
public class Databasechangeloglock implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "locked")
    private boolean locked;
    @Column(name = "lockgranted")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lockgranted;
    @Size(max = 255)
    @Column(name = "lockedby")
    private String lockedby;

    public Databasechangeloglock() {
    }

    public Databasechangeloglock(Integer id) {
        this.id = id;
    }

    public Databasechangeloglock(Integer id, boolean locked) {
        this.id = id;
        this.locked = locked;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public boolean getLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public Date getLockgranted() {
        return lockgranted;
    }

    public void setLockgranted(Date lockgranted) {
        this.lockgranted = lockgranted;
    }

    public String getLockedby() {
        return lockedby;
    }

    public void setLockedby(String lockedby) {
        this.lockedby = lockedby;
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
        if (!(object instanceof Databasechangeloglock)) {
            return false;
        }
        Databasechangeloglock other = (Databasechangeloglock) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ph.devcon.rapidpass.model.Databasechangeloglock[ id=" + id + " ]";
    }
    
}
