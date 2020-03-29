package ph.devcon.rapidpass.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.Date;

/**
 *
 * @author eric
 */
@Entity
@Table(name = "registrant")
@Data
@Builder
@AllArgsConstructor
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
    
    private LocalDate birthDate;
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
    
    private OffsetDateTime dateTimeCreated;
    @Column(name = "date_time_updated")
    
    private OffsetDateTime dateTimeUpdated;
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
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }

    @Override
    public String toString() {
        return "ph.devcon.rapidpass.entities.Registrant[ id=" + id + " ]";
    }
    
}
