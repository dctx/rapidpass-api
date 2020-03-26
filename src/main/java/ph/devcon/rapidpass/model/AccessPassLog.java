/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.devcon.rapidpass.model;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;
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
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateTimeCreated;
    @Column(name = "date_time_updated")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateTimeUpdated;
    @JoinColumn(name = "access_pass_id", referencedColumnName = "id")
    @ManyToOne
    private AccessPass accessPassId;

    public AccessPassLog() {
    }

    public AccessPassLog(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public BigInteger getLatitude() {
        return latitude;
    }

    public void setLatitude(BigInteger latitude) {
        this.latitude = latitude;
    }

    public BigInteger getLongitude() {
        return longitude;
    }

    public void setLongitude(BigInteger longitude) {
        this.longitude = longitude;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Integer getCheckpointId() {
        return checkpointId;
    }

    public void setCheckpointId(Integer checkpointId) {
        this.checkpointId = checkpointId;
    }

    public Integer getScannerDeviceId() {
        return scannerDeviceId;
    }

    public void setScannerDeviceId(Integer scannerDeviceId) {
        this.scannerDeviceId = scannerDeviceId;
    }

    public Date getDateTimeCreated() {
        return dateTimeCreated;
    }

    public void setDateTimeCreated(Date dateTimeCreated) {
        this.dateTimeCreated = dateTimeCreated;
    }

    public Date getDateTimeUpdated() {
        return dateTimeUpdated;
    }

    public void setDateTimeUpdated(Date dateTimeUpdated) {
        this.dateTimeUpdated = dateTimeUpdated;
    }

    public AccessPass getAccessPassId() {
        return accessPassId;
    }

    public void setAccessPassId(AccessPass accessPassId) {
        this.accessPassId = accessPassId;
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
        return "ph.devcon.rapidpass.model.AccessPassLog[ id=" + id + " ]";
    }
    
}
