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

package ph.devcon.rapidpass.models;

import lombok.Builder;
import lombok.Data;
import ph.devcon.rapidpass.entities.ScannerDevice;

@Data
@Builder
public class MobileDevice {
    private String imei;
    private String brand;
    private String model;
    private String mobileNumber;
    private String status;

    public ScannerDevice toScannerDevice() {
        final ScannerDevice scannerDevice = new ScannerDevice();
        scannerDevice.setUniqueDeviceId(getImei());
        scannerDevice.setBrand(getBrand());
        scannerDevice.setModel(getModel());
        scannerDevice.setMobileNumber(getMobileNumber());
        scannerDevice.setStatus(getStatus());
        return scannerDevice;
    }

    public static MobileDevice buildFrom(ScannerDevice scannerDevice) {
        return MobileDevice.builder()
                .brand(scannerDevice.getBrand())
                .model(scannerDevice.getModel())
                .imei(scannerDevice.getUniqueDeviceId())
                .status(scannerDevice.getStatus())
                .mobileNumber(scannerDevice.getMobileNumber())
                .build();
    }
}
