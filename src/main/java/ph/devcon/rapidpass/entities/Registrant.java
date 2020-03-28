/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.devcon.rapidpass.entities;

import lombok.Data;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;

/**
 *
 * @author eric
 */
@Entity
@Table(name = "registrant")
@Data
public class Registrant implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "registrant_type")
    private Integer registrantType;
    @Size(max = 50)
    @Column(name = "registrant_name")
    private String registrantName;
    @Column(name = "priority")
    private Integer priority;
    @Size(max = 10)
    @Column(name = "organization_name")
    private String organizationName;
    @Size(max = 10)
    @Column(name = "organization_id")
    private String organizationId;
    @Size(max = 10)
    @Column(name = "organization_class")
    private String organizationClass;
    @Size(max = 20)
    @Column(name = "reference_id_type")
    private String referenceIdType;
    @Size(max = 50)
    @Column(name = "reference_id")
    private String referenceId;
    @Size(max = 50)
    @Column(name = "first_name")
    private String firstName;
    @Size(max = 50)
    @Column(name = "middle_name")
    private String middleName;
    @Size(max = 50)
    @Column(name = "last_name")
    private String lastName;
    @Size(max = 10)
    @Column(name = "suffix")
    private String suffix;
    @Column(name = "birth_date")
    @Temporal(TemporalType.DATE)
    private Date birthDate;
    @Size(max = 150)
    @Column(name = "address")
    private String address;
    @Size(max = 50)
    @Column(name = "province")
    private String province;
    @Size(max = 50)
    @Column(name = "city")
    private String city;
    @Size(max = 100)
    @Column(name = "work_name")
    private String workName;
    @Size(max = 150)
    @Column(name = "work_address")
    private String workAddress;
    @Size(max = 50)
    @Column(name = "work_province")
    private String workProvince;
    @Size(max = 50)
    @Column(name = "work_city")
    private String workCity;
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
    @Size(max = 2147483647)
    @Column(name = "comment")
    private String comment;
    @Size(max = 2147483647)
    @Column(name = "updates")
    private String updates;
    @Size(max = 20)
    @Column(name = "status")
    private String status;
    @Column(name = "date_time_created")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateTimeCreated;
    @Column(name = "date_time_updated")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateTimeUpdated;
    @OneToMany(mappedBy = "registrantId")
    private Collection<AccessPass> accessPassCollection;
    @JoinColumn(name = "registrar_id", referencedColumnName = "id")
    @ManyToOne
    private Registrar registrarId;

    public Registrant() {
    }

    public Registrar getRegistrarId() {
        return registrarId;
    }

    public void setRegistrarId(Registrar registrarId) {
        this.registrarId = registrarId;
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
        if (!(object instanceof Registrant)) {
            return false;
        }
        Registrant other = (Registrant) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ph.devcon.rapidpass.entities.Registrant[ id=" + id + " ]";
    }
    
}
