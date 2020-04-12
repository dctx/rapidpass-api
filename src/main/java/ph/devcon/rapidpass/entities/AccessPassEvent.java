/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
    @Transient
    private String controlCode;
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

    @PostLoad
    private void postLoad() {
        controlCode = ControlCodeGenerator.generate(accessPassID);
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
