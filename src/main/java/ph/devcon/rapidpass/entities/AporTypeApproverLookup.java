/*
 * Apache License 2.0
 */
package ph.devcon.rapidpass.entities;

import lombok.Data;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;

/**
 *
 * @author eric
 */
@Entity
@Data
@Table(name = "apor_type_approver_lookup")
public class AporTypeApproverLookup implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 30)
    @Column(name = "registrar_short_name")
    private String registrarShortName;
    @Size(max = 10)
    @Column(name = "apor_type")
    private String aporType;

    public AporTypeApproverLookup() {
    }

    public AporTypeApproverLookup(Integer id) {
        this.id = id;
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
        if (!(object instanceof AporTypeApproverLookup)) {
            return false;
        }
        AporTypeApproverLookup other = (AporTypeApproverLookup) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ph.devcon.rapidpass.entities.AporTypeApproverLookup[ id=" + id + " ]";
    }

}