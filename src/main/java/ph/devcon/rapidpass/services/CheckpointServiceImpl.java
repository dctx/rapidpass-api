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
import ph.devcon.rapidpass.api.models.KeyEntry;
import ph.devcon.rapidpass.api.models.RevocationLog;
import ph.devcon.rapidpass.api.models.RevocationLogResponse;
import ph.devcon.rapidpass.config.CheckpointConfig;
import ph.devcon.rapidpass.entities.AccessPass;
import ph.devcon.rapidpass.entities.ScannerDevice;
import ph.devcon.rapidpass.enums.AccessPassStatus;
import ph.devcon.rapidpass.enums.IdTypeVehicle;
import ph.devcon.rapidpass.enums.PassType;
import ph.devcon.rapidpass.models.InternalRevocationEvent;
import ph.devcon.rapidpass.repositories.AccessPassRepository;
import ph.devcon.rapidpass.repositories.ScannerDeviceRepository;
import ph.devcon.rapidpass.services.controlcode.ControlCodeService;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CheckpointServiceImpl implements ICheckpointService {
    private final AccessPassRepository accessPassRepository;
    private final ScannerDeviceRepository scannerDeviceRepository;
    private final ControlCodeService controlCodeService;

    private final CheckpointConfig checkpointConfig;

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

    @Override
    public RevocationLogResponse retrieveRevokedAccessPasses(Integer since) {

        List<AccessPass> revokeList;

        if (since == null) {
            revokeList = accessPassRepository.findAllByStatus(AccessPassStatus.SUSPENDED.name());
        } else {
            Instant instant = Instant.ofEpochSecond(since);
            revokeList = accessPassRepository.findAllByStatusAndDateTimeUpdatedAfter(AccessPassStatus.SUSPENDED.name(), OffsetDateTime.ofInstant(instant, ZoneId.systemDefault()));
        }

        // Ensure revoke list has control codes
        revokeList = revokeList.stream()
                .map(controlCodeService::bindControlCodeForAccessPass)
                .collect(Collectors.toList());

        RevocationLogResponse revocationLogResponse = new RevocationLogResponse();

        List<InternalRevocationEvent> events = revokeList.stream()
                .map(InternalRevocationEvent::buildFrom)
                .collect(Collectors.toList());

        RevocationLog log = new RevocationLog();
        log.addAll(events);

        revocationLogResponse.setData(log);

        return revocationLogResponse;
    }

    @Override
    public boolean validateByMasterKey(String masterKey) {
        boolean masterKeyCorrect = checkpointConfig.getKeyEntry(masterKey) != null;

        return masterKeyCorrect;
    }

    /**
     * Returns true if there is a master key with their valid to date after the current time.
     */
    @Override
    public boolean validateByUniqueDeviceId(String masterKey, String deviceId) {

        boolean masterKeyCorrect = checkpointConfig.getKeyEntry(masterKey) != null;

        boolean deviceExists = scannerDeviceRepository.findByUniqueDeviceId(deviceId) != null;

        return masterKeyCorrect && deviceExists;
    }

    @Override
    public boolean validateByImei(String masterKey, String imei) {
        boolean masterKeyCorrect = checkpointConfig.getKeyEntry(masterKey) != null;

        boolean imeiExists = scannerDeviceRepository.findByImei(imei) != null;

        return masterKeyCorrect && imeiExists;
    }

    /**
     * Returns all the available keys.
     */
    @Override
    public List<KeyEntry> getAllKeys() {
        return checkpointConfig.getAllKeyEntries();
    }

    /**
     * Returns the latest key.
     */
    @Override
    public KeyEntry getLatestKeys() {
        return getAllKeys().stream()
                .filter(key -> OffsetDateTime.parse(key.getValidTo()).isAfter(OffsetDateTime.now()))
                .min(Comparator.comparing(o -> OffsetDateTime.parse(o.getValidTo())))
                .orElse(null);
    }
}
