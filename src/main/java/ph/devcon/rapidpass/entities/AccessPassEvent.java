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

import lombok.Data;
import ph.devcon.rapidpass.utilities.ControlCodeGenerator;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.Size;

/**
 *
 * @author eric
 */
@Entity
@Table(name = "access_pass_event")
@Data
public class AccessPassEvent implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "access_pass_id")
    private Integer accessPassID;
    @Size(max = 30)
    @Column(name = "reference_id")
    private String referenceId;
    @Size(max = 10)
    @Column(name = "pass_type")
    private String passType;
    @Size(max = 10)
    @Column(name = "apor_type")
    private String aporType;
    @Size(max = 100)
    @Column(name = "name")
    private String name;
    @Size(max = 20)
    @Column(name = "plate_number")
    private String plateNumber;
    @Size(max = 20)
    @Column(name = "status")
    private String status;
    @Column(name = "valid_from")
    private OffsetDateTime validFrom;
    @Column(name = "valid_to")
    private OffsetDateTime validTo;
    @Column(name = "event_timestamp")
    private OffsetDateTime eventTimestamp;

    public AccessPassEvent() {
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
        if (!(object instanceof AccessPassEvent)) {
            return false;
        }
        AccessPassEvent other = (AccessPassEvent) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ph.devcon.rapidpass.entities.AccessPassEvent[ id=" + id + " ]";
    }
    
}
