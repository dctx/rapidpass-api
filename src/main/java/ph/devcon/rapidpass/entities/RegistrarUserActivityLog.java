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
@Table(name = "registrar_user_activity_log")
@NamedQueries({
    @NamedQuery(name = "RegistrarUserActivityLog.findAll", query = "SELECT r FROM RegistrarUserActivityLog r"),
    @NamedQuery(name = "RegistrarUserActivityLog.findById", query = "SELECT r FROM RegistrarUserActivityLog r WHERE r.id = :id"),
    @NamedQuery(name = "RegistrarUserActivityLog.findBySessionId", query = "SELECT r FROM RegistrarUserActivityLog r WHERE r.sessionId = :sessionId"),
    @NamedQuery(name = "RegistrarUserActivityLog.findByIpAddress", query = "SELECT r FROM RegistrarUserActivityLog r WHERE r.ipAddress = :ipAddress"),
    @NamedQuery(name = "RegistrarUserActivityLog.findByType", query = "SELECT r FROM RegistrarUserActivityLog r WHERE r.type = :type"),
    @NamedQuery(name = "RegistrarUserActivityLog.findByActionTimestamp", query = "SELECT r FROM RegistrarUserActivityLog r WHERE r.actionTimestamp = :actionTimestamp"),
    @NamedQuery(name = "RegistrarUserActivityLog.findByAction", query = "SELECT r FROM RegistrarUserActivityLog r WHERE r.action = :action"),
    @NamedQuery(name = "RegistrarUserActivityLog.findByComments", query = "SELECT r FROM RegistrarUserActivityLog r WHERE r.comments = :comments")})
public class RegistrarUserActivityLog implements Serializable {

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
    private RegistrarUser userId;

    public RegistrarUserActivityLog() {
    }

    public RegistrarUserActivityLog(Integer id) {
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

    public RegistrarUser getUserId() {
        return userId;
    }

    public void setUserId(RegistrarUser userId) {
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
        if (!(object instanceof RegistrarUserActivityLog)) {
            return false;
        }
        RegistrarUserActivityLog other = (RegistrarUserActivityLog) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ph.devcon.rapidpass.entities.RegistrarUserActivityLog[ id=" + id + " ]";
    }
    
}
