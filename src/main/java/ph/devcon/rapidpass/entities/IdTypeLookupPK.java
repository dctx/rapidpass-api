/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.devcon.rapidpass.entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author eric
 */
@Embeddable
public class IdTypeLookupPK implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 5)
    @Column(name = "pass_type")
    private String passType;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 10)
    @Column(name = "id_type")
    private String idType;

    public IdTypeLookupPK() {
    }

    public IdTypeLookupPK(String passType, String idType) {
        this.passType = passType;
        this.idType = idType;
    }

    public String getPassType() {
        return passType;
    }

    public void setPassType(String passType) {
        this.passType = passType;
    }

    public String getIdType() {
        return idType;
    }

    public void setIdType(String idType) {
        this.idType = idType;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (passType != null ? passType.hashCode() : 0);
        hash += (idType != null ? idType.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof IdTypeLookupPK)) {
            return false;
        }
        IdTypeLookupPK other = (IdTypeLookupPK) object;
        if ((this.passType == null && other.passType != null) || (this.passType != null && !this.passType.equals(other.passType))) {
            return false;
        }
        if ((this.idType == null && other.idType != null) || (this.idType != null && !this.idType.equals(other.idType))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ph.devcon.rapidpass.entities.IdTypeLookupPK[ passType=" + passType + ", idType=" + idType + " ]";
    }
    
}
