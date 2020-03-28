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
import ph.devcon.rapidpass.enums.APORType;
import ph.devcon.rapidpass.models.RapidPassRequest;

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
 * Data model representing an access pass, that maps out directly to the table definition in the database.
 * @author eric
 */
@Entity
@Table(name = "access_pass", schema = "public")
@Data
@NoArgsConstructor
public class AccessPass implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 30)
    @Column(name = "reference_id")
    private String referenceId;
    @Size(max = 10)
    @Column(name = "pass_type")
    private String passType;
    @Size(max = 10)
    @Column(name = "apor_type")
    private String aporType;
    @Column(name = "control_code")
    private String controlCode;
    @Size(max = 10)
    @Column(name = "id_type")
    private String idType;
    @Size(max = 25)
    @Column(name = "identifier_number")
    private String identifierNumber;
    @Size(max = 100)
    @Column(name = "name")
    private String name;
    @Size(max = 100)
    @Column(name = "company")
    private String company;
    @Size(max = 150)
    @Column(name = "remarks")
    private String remarks;
    @Column(name = "scope")
    private Integer scope;
    @Size(max = 200)
    @Column(name = "limitations")
    private String limitations;
    @Size(max = 100)
    @Column(name = "origin_name")
    private String originName;
    @Size(max = 150)
    @Column(name = "origin_street")
    private String originStreet;
    @Size(max = 50)
    @Column(name = "origin_province")
    private String originProvince;
    @Size(max = 50)
    @Column(name = "origin_city")
    private String originCity;
    @Size(max = 100)
    @Column(name = "destination_name")
    private String destinationName;
    @Size(max = 150)
    @Column(name = "destination_street")
    private String destinationStreet;
    @Size(max = 150)
    @Column(name = "destination_city")
    private String destinationCity;
    @Size(max = 50)
    @Column(name = "destination_province")
    private String destinationProvince;
    @Column(name = "valid_from")
    @Temporal(TemporalType.TIMESTAMP)
    private Date validFrom;
    @Column(name = "valid_to")
    @Temporal(TemporalType.TIMESTAMP)
    private Date validTo;
    @Size(max = 20)
    @Column(name = "issued_by")
    private String issuedBy;
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
    @JoinColumn(name = "registrant_id", referencedColumnName = "id")
    @ManyToOne
    private Registrant registrantId;
    @OneToMany(mappedBy = "accessPassId")
    private Collection<AccessPassLog> accessPassLogCollection;

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AccessPass)) {
            return false;
        }
        AccessPass other = (AccessPass) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ph.devcon.rapidpass.entities.AccessPass[ id=" + id + " ]";
    }

}



