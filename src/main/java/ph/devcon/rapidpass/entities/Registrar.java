/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.devcon.rapidpass.entities;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Collection;

/**
 *
 * @author eric
 */
@Data
@Entity
@Table(name = "registrar")
@NamedQueries({
    @NamedQuery(name = "Registrar.findAll", query = "SELECT r FROM Registrar r"),
    @NamedQuery(name = "Registrar.findById", query = "SELECT r FROM Registrar r WHERE r.id = :id"),
    @NamedQuery(name = "Registrar.findByName", query = "SELECT r FROM Registrar r WHERE r.name = :name"),
    @NamedQuery(name = "Registrar.findByShortName", query = "SELECT r FROM Registrar r WHERE r.shortName = :shortName"),
    @NamedQuery(name = "Registrar.findByDescription", query = "SELECT r FROM Registrar r WHERE r.description = :description"),
    @NamedQuery(name = "Registrar.findByShardKey", query = "SELECT r FROM Registrar r WHERE r.shardKey = :shardKey"),
    @NamedQuery(name = "Registrar.findByInstitutionType", query = "SELECT r FROM Registrar r WHERE r.institutionType = :institutionType"),
    @NamedQuery(name = "Registrar.findByInstitutionClassification", query = "SELECT r FROM Registrar r WHERE r.institutionClassification = :institutionClassification"),
    @NamedQuery(name = "Registrar.findByStatus", query = "SELECT r FROM Registrar r WHERE r.status = :status"),
    @NamedQuery(name = "Registrar.findByAddress", query = "SELECT r FROM Registrar r WHERE r.address = :address"),
    @NamedQuery(name = "Registrar.findByCountry", query = "SELECT r FROM Registrar r WHERE r.country = :country"),
    @NamedQuery(name = "Registrar.findByRegion", query = "SELECT r FROM Registrar r WHERE r.region = :region"),
    @NamedQuery(name = "Registrar.findByProvince", query = "SELECT r FROM Registrar r WHERE r.province = :province"),
    @NamedQuery(name = "Registrar.findByCity", query = "SELECT r FROM Registrar r WHERE r.city = :city"),
    @NamedQuery(name = "Registrar.findByZipCode", query = "SELECT r FROM Registrar r WHERE r.zipCode = :zipCode"),
    @NamedQuery(name = "Registrar.findByPhone", query = "SELECT r FROM Registrar r WHERE r.phone = :phone"),
    @NamedQuery(name = "Registrar.findByMobile", query = "SELECT r FROM Registrar r WHERE r.mobile = :mobile"),
    @NamedQuery(name = "Registrar.findByEmail", query = "SELECT r FROM Registrar r WHERE r.email = :email"),
    @NamedQuery(name = "Registrar.findByRepresentative", query = "SELECT r FROM Registrar r WHERE r.representative = :representative"),
    @NamedQuery(name = "Registrar.findByRepDesignation", query = "SELECT r FROM Registrar r WHERE r.repDesignation = :repDesignation"),
    @NamedQuery(name = "Registrar.findByRepPhone", query = "SELECT r FROM Registrar r WHERE r.repPhone = :repPhone"),
    @NamedQuery(name = "Registrar.findByRepMobile", query = "SELECT r FROM Registrar r WHERE r.repMobile = :repMobile"),
    @NamedQuery(name = "Registrar.findByRepEmail", query = "SELECT r FROM Registrar r WHERE r.repEmail = :repEmail"),
    @NamedQuery(name = "Registrar.findByWebsite", query = "SELECT r FROM Registrar r WHERE r.website = :website"),
    @NamedQuery(name = "Registrar.findByReference1", query = "SELECT r FROM Registrar r WHERE r.reference1 = :reference1"),
    @NamedQuery(name = "Registrar.findByReference2", query = "SELECT r FROM Registrar r WHERE r.reference2 = :reference2"),
    @NamedQuery(name = "Registrar.findByUpdates", query = "SELECT r FROM Registrar r WHERE r.updates = :updates"),
    @NamedQuery(name = "Registrar.findByDateTimeCreated", query = "SELECT r FROM Registrar r WHERE r.dateTimeCreated = :dateTimeCreated"),
    @NamedQuery(name = "Registrar.findByDateTimeUpdated", query = "SELECT r FROM Registrar r WHERE r.dateTimeUpdated = :dateTimeUpdated")})
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
//    @OneToMany(mappedBy = "registrarId")
//    private Collection<ScannerDevice> scannerDeviceCollection;
//    @OneToMany(mappedBy = "registrarId")
//    private Collection<Registrant> registrantCollection;

    public Registrar() {
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
