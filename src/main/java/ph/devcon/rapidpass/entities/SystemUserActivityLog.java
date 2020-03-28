/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.devcon.rapidpass.entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
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
@Table(name = "system_user_activity_log")
@NamedQueries({
    @NamedQuery(name = "SystemUserActivityLog.findAll", query = "SELECT s FROM SystemUserActivityLog s"),
    @NamedQuery(name = "SystemUserActivityLog.findById", query = "SELECT s FROM SystemUserActivityLog s WHERE s.id = :id"),
    @NamedQuery(name = "SystemUserActivityLog.findBySessionId", query = "SELECT s FROM SystemUserActivityLog s WHERE s.sessionId = :sessionId"),
    @NamedQuery(name = "SystemUserActivityLog.findByIpAddress", query = "SELECT s FROM SystemUserActivityLog s WHERE s.ipAddress = :ipAddress"),
    @NamedQuery(name = "SystemUserActivityLog.findByType", query = "SELECT s FROM SystemUserActivityLog s WHERE s.type = :type"),
    @NamedQuery(name = "SystemUserActivityLog.findByActionTimestamp", query = "SELECT s FROM SystemUserActivityLog s WHERE s.actionTimestamp = :actionTimestamp"),
    @NamedQuery(name = "SystemUserActivityLog.findByAction", query = "SELECT s FROM SystemUserActivityLog s WHERE s.action = :action"),
    @NamedQuery(name = "SystemUserActivityLog.findByComments", query = "SELECT s FROM SystemUserActivityLog s WHERE s.comments = :comments")})
public class SystemUserActivityLog implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    private Integer id;
    @Size(max = 45)
    @Column(name = "session_id")
    private String sessionId;
    @Size(max = 40)
    @Column(name = "ip_address")
    private String ipAddress;
    @Size(max = 25)
    @Column(name = "type")
    private String type;
    @Column(name = "action_timestamp")
    @Temporal(TemporalType.TIMESTAMP)
    private Date actionTimestamp;
    @Size(max = 50)
    @Column(name = "action")
    private String action;
    @Size(max = 2147483647)
    @Column(name = "comments")
    private String comments;
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @ManyToOne
    private SystemUser userId;

    public SystemUserActivityLog() {
    }

    public SystemUserActivityLog(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getActionTimestamp() {
        return actionTimestamp;
    }

    public void setActionTimestamp(Date actionTimestamp) {
        this.actionTimestamp = actionTimestamp;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public SystemUser getUserId() {
        return userId;
    }

    public void setUserId(SystemUser userId) {
        this.userId = userId;
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
        if (!(object instanceof SystemUserActivityLog)) {
            return false;
        }
        SystemUserActivityLog other = (SystemUserActivityLog) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ph.devcon.rapidpass.entities.SystemUserActivityLog[ id=" + id + " ]";
    }
    
}
