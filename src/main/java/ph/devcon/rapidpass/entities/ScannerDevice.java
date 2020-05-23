/*
 * Copyright (c) 2020.  DevConnect Philippines, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and limitations under the License.
 */

package ph.devcon.rapidpass.entities;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import ph.devcon.rapidpass.api.models.MobileDevice;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.OffsetDateTime;

/**
 * @author eric
 */
@Entity
@Table(name = "scanner_device")
@Data
public class ScannerDevice implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)

    @NotNull
    @Size(min = 1, max = 2147483647)
    @Column(name = "unique_device_id")
    private String uniqueDeviceId;

    @Size(min = 1, max = 2147483647)
    @Column(name = "imei")
    private String imei;

    @Column(name = "brand")
    private String brand;
    @Column(name = "model")
    private String model;
    @Column(name = "mobile_number")
    private String mobileNumber;
    @Column(name = "status")
    private String status;

    @Column(name = "date_time_last_used")
    private OffsetDateTime dateTimeLastUsed;

    @CreatedDate
    @Column(name = "date_time_created")
    private OffsetDateTime dateTimeCreated;

    @LastModifiedDate
    @Column(name = "date_time_updated")
    private OffsetDateTime dateTimeUpdated;

    //    @JoinColumn(name = "registrar_id", referencedColumnName = "id")
//    @ManyToOne
//    private Registrar registrarId;
    @Column(name = "registrar_id")
    private Integer registrarId;
    //    @JoinColumn(name = "registrar_user_id", referencedColumnName = "id")
//    @ManyToOne
//    private RegistrarUser registrarUserId;
    @Column(name = "registrar_user_id")
    private Integer registrarUserId;

    public ScannerDevice() {
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
        return "ph.devcon.rapidpass.entities.ScannerDevice[ id=" + id + " ]";
    }


    public static ScannerDevice buildFrom(MobileDevice mobileDevice) {
        ScannerDevice scannerDevice = new ScannerDevice();

        scannerDevice.setUniqueDeviceId(mobileDevice.getId());
        scannerDevice.setImei(mobileDevice.getImei());

        scannerDevice.setBrand(mobileDevice.getBrand());
        scannerDevice.setModel(mobileDevice.getModel());

        scannerDevice.setStatus(mobileDevice.getStatus());
        scannerDevice.setMobileNumber(mobileDevice.getMobileNumber());

        if (mobileDevice.getCreatedAt() != null)
            scannerDevice.setDateTimeCreated(OffsetDateTime.parse(mobileDevice.getCreatedAt()));

        if (mobileDevice.getUpdatedAt() != null)
            scannerDevice.setDateTimeUpdated(OffsetDateTime.parse(mobileDevice.getUpdatedAt()));

        return scannerDevice;
    }

}
