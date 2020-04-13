/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.devcon.rapidpass.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author eric
 */
@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "registrar")
public class Registrar implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "registrar_id_generator")
    // FIXME: temporarily set to 1 to compile. will have to fix.
    @SequenceGenerator(name="registrar_id_generator", sequenceName = "registrar_id_seq", allocationSize=1)
    @Basic(optional = false)
    @Column(name = "id", updatable = false, nullable = false)
    private Integer id;
    @Size(max = 150)
    @Column(name = "name")
    private String name;
    @Size(max = 30)
    @Column(name = "short_name")
    private String shortName;
    @Size(max = 2147483647)
    @Column(name = "description")
    private String description;
    @Size(max = 5)
    @Column(name = "shard_key")
    private String shardKey;
    @Size(max = 15)
    @Column(name = "institution_type")
    private String institutionType;
    @Size(max = 25)
    @Column(name = "institution_classification")
    private String institutionClassification;
    @Size(max = 15)
    @Column(name = "status")
    private String status;
    @Size(max = 2147483647)
    @Column(name = "address")
    private String address;
    @Size(max = 50)
    @Column(name = "country")
    private String country;
    @Size(max = 50)
    @Column(name = "region")
    private String region;
    @Size(max = 50)
    @Column(name = "province")
    private String province;
    @Size(max = 50)
    @Column(name = "city")
    private String city;
    @Size(max = 5)
    @Column(name = "zip_code")
    private String zipCode;
    // @Pattern(regexp="^\\(?(\\d{3})\\)?[- ]?(\\d{3})[- ]?(\\d{4})$", message="Invalid phone/fax format, should be as xxx-xxx-xxxx")//if the field contains phone or fax number consider using this annotation to enforce field validation
    @Size(max = 50)
    @Column(name = "phone")
    private String phone;
    @Size(max = 50)
    @Column(name = "mobile")
    private String mobile;
    // @Pattern(regexp="[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", message="Invalid email")//if the field contains email address consider using this annotation to enforce field validation
    @Size(max = 50)
    @Column(name = "email")
    private String email;
    @Size(max = 100)
    @Column(name = "representative")
    private String representative;
    @Size(max = 50)
    @Column(name = "rep_designation")
    private String repDesignation;
    @Size(max = 50)
    @Column(name = "rep_phone")
    private String repPhone;
    @Size(max = 50)
    @Column(name = "rep_mobile")
    private String repMobile;
    @Size(max = 50)
    @Column(name = "rep_email")
    private String repEmail;
    @Size(max = 100)
    @Column(name = "website")
    private String website;
    @Size(max = 50)
    @Column(name = "reference1")
    private String reference1;
    @Size(max = 50)
    @Column(name = "reference2")
    private String reference2;
    @Size(max = 2147483647)
    @Column(name = "updates")
    private String updates;
    @Column(name = "date_time_created")
    
    private OffsetDateTime dateTimeCreated;
    @Column(name = "date_time_updated")
    
    private OffsetDateTime dateTimeUpdated;
    @OneToMany(mappedBy = "parentRegistrarId")
    private Collection<Registrar> registrarCollection;
    @JoinColumn(name = "parent_registrar_id", referencedColumnName = "id")
    @ManyToOne
    private Registrar parentRegistrarId;
    @OneToMany(mappedBy = "registrarId")
    private Collection<RegistrarUser> registrarUserCollection;
    @OneToMany(mappedBy = "registrarShortName", fetch = FetchType.EAGER)
    private List<AporTypeApproverLookup> aporTypeApproverLookupList;
//    @OneToMany(mappedBy = "registrarId")
//    private Collection<ScannerDevice> scannerDeviceCollection;
//    @OneToMany(mappedBy = "registrarId")
//    private Collection<Registrant> registrantCollection;

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Registrar)) {
            return false;
        }
        Registrar other = (Registrar) object;
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }

    @Override
    public String toString() {
        return "ph.devcon.rapidpass.entities.Registrar[ id=" + id + " ]";
    }
    
}
