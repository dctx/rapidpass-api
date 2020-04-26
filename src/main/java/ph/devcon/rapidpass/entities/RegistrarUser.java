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

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.Date;
import javax.validation.constraints.Min;

/**
 *
 * @author eric
 */
@Entity
@Table(name = "registrar_user")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@NamedQueries(
        @NamedQuery(name = "RegistrarUser.findByUsername", query = "SELECT r FROM RegistrarUser r WHERE r.username = :username")
)
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
    @Size(max = 40)
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
//    @OneToMany(mappedBy = "registrarUserId")
//    private Collection<ScannerDevice> scannerDeviceCollection;

    @Min(value = 0)
    @Column(name = "login_attempts")
    private int loginAttempts = 0;

    @Column(name = "is_account_locked")
    private boolean isAccountLocked = false;

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
