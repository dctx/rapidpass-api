/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.devcon.rapidpass.entities;

import lombok.Data;

import java.io.Serializable;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;

/**
 *
 * @author eric
 */
@Entity
@Table(name = "access_pass_log")
@NamedQueries({
    @NamedQuery(name = "AccessPassLog.findAll", query = "SELECT a FROM AccessPassLog a"),
    @NamedQuery(name = "AccessPassLog.findById", query = "SELECT a FROM AccessPassLog a WHERE a.id = :id"),
    @NamedQuery(name = "AccessPassLog.findByEvent", query = "SELECT a FROM AccessPassLog a WHERE a.event = :event"),
    @NamedQuery(name = "AccessPassLog.findByLatitude", query = "SELECT a FROM AccessPassLog a WHERE a.latitude = :latitude"),
    @NamedQuery(name = "AccessPassLog.findByLongitude", query = "SELECT a FROM AccessPassLog a WHERE a.longitude = :longitude"),
    @NamedQuery(name = "AccessPassLog.findByIpAddress", query = "SELECT a FROM AccessPassLog a WHERE a.ipAddress = :ipAddress"),
    @NamedQuery(name = "AccessPassLog.findByCheckpointId", query = "SELECT a FROM AccessPassLog a WHERE a.checkpointId = :checkpointId"),
    @NamedQuery(name = "AccessPassLog.findByScannerDeviceId", query = "SELECT a FROM AccessPassLog a WHERE a.scannerDeviceId = :scannerDeviceId"),
    @NamedQuery(name = "AccessPassLog.findByDateTimeCreated", query = "SELECT a FROM AccessPassLog a WHERE a.dateTimeCreated = :dateTimeCreated"),
    @NamedQuery(name = "AccessPassLog.findByDateTimeUpdated", query = "SELECT a FROM AccessPassLog a WHERE a.dateTimeUpdated = :dateTimeUpdated")})
public class AccessPassLog implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 2147483647)
    @Column(name = "event")
    private String event;
    @Column(name = "latitude")
    private BigInteger latitude;
    @Column(name = "longitude")
    private BigInteger longitude;
    @Size(max = 80)
    @Column(name = "ip_address")
    private String ipAddress;
    @Column(name = "checkpoint_id")
    private Integer checkpointId;
    @Column(name = "scanner_device_id")
    private Integer scannerDeviceId;
    @Column(name = "date_time_created")
    
    private OffsetDateTime dateTimeCreated;
    @Column(name = "date_time_updated")
    
    private OffsetDateTime dateTimeUpdated;
    @JoinColumn(name = "access_pass_id", referencedColumnName = "id")
    @ManyToOne
    private AccessPass accessPassId;

    public AccessPassLog() {
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
        if (!(object instanceof AccessPassLog)) {
            return false;
        }
        AccessPassLog other = (AccessPassLog) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ph.devcon.rapidpass.entities.AccessPassLog[ id=" + id + " ]";
    }
    
}
