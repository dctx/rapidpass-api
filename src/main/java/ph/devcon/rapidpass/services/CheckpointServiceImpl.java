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

package ph.devcon.rapidpass.services;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import ph.devcon.rapidpass.entities.AccessPass;
import ph.devcon.rapidpass.entities.ScannerDevice;
import ph.devcon.rapidpass.enums.IdTypeVehicle;
import ph.devcon.rapidpass.enums.PassType;
import ph.devcon.rapidpass.repositories.AccessPassRepository;
import ph.devcon.rapidpass.repositories.ScannerDeviceRepository;
import ph.devcon.rapidpass.services.controlcode.ControlCodeService;

@Service
@RequiredArgsConstructor
public class CheckpointServiceImpl implements ICheckpointService {
    private final AccessPassRepository accessPassRepository;
    private final ScannerDeviceRepository scannerDeviceRepository;
    private final ControlCodeService controlCodeService;

    @Override
    public AccessPass retrieveAccessPassByPlateNo(String plateNo) {
        AccessPass accessPass = this.accessPassRepository.findByPassTypeAndIdentifierNumber(PassType.VEHICLE.toString(), plateNo);
        accessPass = controlCodeService.bindControlCodeForAccessPass(accessPass);
        return (null != accessPass && StringUtils.equals(IdTypeVehicle.PLT.toString(), accessPass.getIdType())) ? accessPass : null;
    }

    @Override
    public AccessPass retrieveAccessPassByQrCode(String qrCode)
    {
        return null;
    }

    @Override
    public ScannerDevice retrieveDeviceByImei(String imei) {
        ScannerDevice scannerDevice = this.scannerDeviceRepository.findByUniqueDeviceId(imei);
        if (scannerDevice == null) {
            throw new IllegalArgumentException(String.format("Device with IMEI %s is not registered.", imei));
        }

        return scannerDevice;
    }
}
