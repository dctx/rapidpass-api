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

import java.io.Serializable;
import java.time.OffsetDateTime;
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
    
    private OffsetDateTime lockgranted;
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

    public OffsetDateTime getLockgranted() {
        return lockgranted;
    }

    public void setLockgranted(OffsetDateTime lockgranted) {
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
        return "ph.devcon.rapidpass.entities.Databasechangeloglock[ id=" + id + " ]";
    }
    
}
