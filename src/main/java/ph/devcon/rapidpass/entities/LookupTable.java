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

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.Size;

/**
 *
 * @author eric
 */
@Entity
@Table(name = "lookup_table")
@Data
public class LookupTable implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected LookupTablePK lookupTablePK;
    @Size(max = 80)
    @Column(name = "description")
    private String description;

    public LookupTable() {
    }

    public LookupTable(LookupTablePK lookupTablePK) {
        this.lookupTablePK = lookupTablePK;
    }

    public LookupTable(String key, String value) {
        this.lookupTablePK = new LookupTablePK(key, value);
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (lookupTablePK != null ? lookupTablePK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof LookupTable)) {
            return false;
        }
        LookupTable other = (LookupTable) object;
        if ((this.lookupTablePK == null && other.lookupTablePK != null) || (this.lookupTablePK != null && !this.lookupTablePK.equals(other.lookupTablePK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ph.devcon.rapidpass.entities.LookupTable[ lookupTablePK=" + lookupTablePK + " ]";
    }
    
}
