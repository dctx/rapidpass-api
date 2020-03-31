/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.devcon.rapidpass.entities;

import lombok.Data;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author eric
 */
@Entity
@Table(name = "config_lookup")
@Data
public class ConfigLookup implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 25)
    @Column(name = "config_key")
    private String configKey;
    @Size(max = 25)
    @Column(name = "config_value")
    private String configValue;
    @Size(max = 75)
    @Column(name = "description")
    private String description;

    public ConfigLookup() {
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (configKey != null ? configKey.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ConfigLookup)) {
            return false;
        }
        ConfigLookup other = (ConfigLookup) object;
        if ((this.configKey == null && other.configKey != null) || (this.configKey != null && !this.configKey.equals(other.configKey))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ph.devcon.rapidpass.entities.ConfigLookup[ configKey=" + configKey + " ]";
    }
    
}
