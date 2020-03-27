/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.devcon.rapidpass.model;

import java.io.Serializable;
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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author eric
 */
@Entity
@Table(name = "scanner_device")
@NamedQueries({
    @NamedQuery(name = "ScannerDevice.findAll", query = "SELECT s FROM ScannerDevice s"),
    @NamedQuery(name = "ScannerDevice.findById", query = "SELECT s FROM ScannerDevice s WHERE s.id = :id"),
    @NamedQuery(name = "ScannerDevice.findByDeviceType", query = "SELECT s FROM ScannerDevice s WHERE s.deviceType = :deviceType"),
    @NamedQuery(name = "ScannerDevice.findByUniqueDeviceId", query = "SELECT s FROM ScannerDevice s WHERE s.uniqueDeviceId = :uniqueDeviceId"),
    @NamedQuery(name = "ScannerDevice.findByDateTimeLastUsed", query = "SELECT s FROM ScannerDevice s WHERE s.dateTimeLastUsed = :dateTimeLastUsed"),
    @NamedQuery(name = "ScannerDevice.findByDateTimeCreated", query = "SELECT s FROM ScannerDevice s WHERE s.dateTimeCreated = :dateTimeCreated"),
    @NamedQuery(name = "ScannerDevice.findByDateTimeUpdated", query = "SELECT s FROM ScannerDevice s WHERE s.dateTimeUpdated = :dateTimeUpdated")})
public class ScannerDevice implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "device_type")
    private int deviceType;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2147483647)
    @Column(name = "unique_device_id")
    private String uniqueDeviceId;
    @Column(name = "date_time_last_used")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateTimeLastUsed;
    @Column(name = "date_time_created")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateTimeCreated;
    @Column(name = "date_time_updated")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateTimeUpdated;
    @JoinColumn(name = "registrar_id", referencedColumnName = "id")
    @ManyToOne
    private Registrar registrarId;
    @JoinColumn(name = "registrar_user_id", referencedColumnName = "id")
    @ManyToOne
    private RegistrarUser registrarUserId;

    public ScannerDevice() {
    }

    public ScannerDevice(Integer id) {
        this.id = id;
    }

    public ScannerDevice(Integer id, int deviceType, String uniqueDeviceId) {
        this.id = id;
        this.deviceType = deviceType;
        this.uniqueDeviceId = uniqueDeviceId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }

    public String getUniqueDeviceId() {
        return uniqueDeviceId;
    }

    public void setUniqueDeviceId(String uniqueDeviceId) {
        this.uniqueDeviceId = uniqueDeviceId;
    }

    public Date getDateTimeLastUsed() {
        return dateTimeLastUsed;
    }

    public void setDateTimeLastUsed(Date dateTimeLastUsed) {
        this.dateTimeLastUsed = dateTimeLastUsed;
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

    public Registrar getRegistrarId() {
        return registrarId;
    }

    public void setRegistrarId(Registrar registrarId) {
        this.registrarId = registrarId;
    }

    public RegistrarUser getRegistrarUserId() {
        return registrarUserId;
    }

    public void setRegistrarUserId(RegistrarUser registrarUserId) {
        this.registrarUserId = registrarUserId;
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
        if (!(object instanceof ScannerDevice)) {
            return false;
        }
        ScannerDevice other = (ScannerDevice) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ph.devcon.rapidpass.model.ScannerDevice[ id=" + id + " ]";
    }
    
}
