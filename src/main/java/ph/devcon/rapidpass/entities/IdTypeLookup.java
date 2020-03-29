/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
@Table(name = "id_type_lookup")
@Data
public class IdTypeLookup implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected IdTypeLookupPK idTypeLookupPK;
    @Size(max = 50)
    @Column(name = "description")
    private String description;

    public IdTypeLookup() {
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idTypeLookupPK != null ? idTypeLookupPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof IdTypeLookup)) {
            return false;
        }
        IdTypeLookup other = (IdTypeLookup) object;
        if ((this.idTypeLookupPK == null && other.idTypeLookupPK != null) || (this.idTypeLookupPK != null && !this.idTypeLookupPK.equals(other.idTypeLookupPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ph.devcon.rapidpass.entities.IdTypeLookup[ idTypeLookupPK=" + idTypeLookupPK + " ]";
    }
    
}
