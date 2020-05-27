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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 *
 * @author darren
 */
@Entity
@Table(name = "apor_lookup", schema = "public")
@Data
@Builder
@AllArgsConstructor
public class AporLookup implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Size(max = 10)
    @Column(name = "code")
    private String aporCode;

    @Size(max = 100)
    @Column(name = "description")
    private String description;

    @Size(max = 100)
    @Column(name = "approving_agency")
    private String approvingAgency;

    /**
     * True, if and only if having this APOR code will replace the destination city to 'Multi City'.
     */
    @Column(name = "multi_destination")
    private Boolean multiDestination = false;

    public AporLookup() {

    }

    public void setAporCode(String aporCode) {
        this.aporCode = aporCode.toUpperCase();
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof AporLookup) {
            AporLookup aporLookup = (AporLookup) object;
            if (aporLookup.aporCode == null && this.aporCode == null) return true;
            if (aporLookup.aporCode != null && this.aporCode != null) return aporLookup.aporCode.equals(this.aporCode);
        }
        return false;
    }

    @Override
    public String toString() {
        return "ph.devcon.rapidpass.entities.AporLookUp[ aporCode=" + this.aporCode + " ]";
    }
}
