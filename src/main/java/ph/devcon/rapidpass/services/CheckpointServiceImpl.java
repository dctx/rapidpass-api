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

import com.google.common.collect.ImmutableMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.beans.factory.annotation.Value;
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

import java.sql.Timestamp;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Slf4j
public class CheckpointServiceImpl implements ICheckpointService {
    private final AccessPassRepository accessPassRepository;
    private final ScannerDeviceRepository scannerDeviceRepository;
    private final ControlCodeService controlCodeService;

    private final CheckpointConfig checkpointConfig;

    @Value("${endpointswitch.checkpoint.validateImeiDeviceId:true}")
    private Boolean shouldValidateImeiDeviceId;

    @Override
    public AccessPass retrieveAccessPassByPlateNo(String plateNo) {
        AccessPass accessPass = this.accessPassRepository.findByPassTypeAndIdentifierNumber(PassType.VEHICLE.toString(), plateNo);
        accessPass = controlCodeService.bindControlCodeForAccessPass(accessPass);
        return (null != accessPass && StringUtils.equals(IdTypeVehicle.PLT.toString(), accessPass.getIdType())) ? accessPass : null;
    }

    @Override
    public AccessPass retrieveAccessPassByQrCode(String qrCode) {
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
        log.debug("Retrieving revoked access passes since {}", since);
        List<AccessPass> revokeList;

        if (since == null) {
            revokeList = accessPassRepository.findAllByStatus(AccessPassStatus.SUSPENDED.name());
        } else {
            Instant instant = Instant.ofEpochSecond(since);
            revokeList = accessPassRepository.findAllByStatusAndDateTimeUpdatedAfter(
                    AccessPassStatus.SUSPENDED.name(),
                    OffsetDateTime.ofInstant(instant, ZoneId.systemDefault()));
        }

        RevocationLogResponse revocationLogResponse = new RevocationLogResponse();

        List<InternalRevocationEvent> events = revokeList.stream()
                .map(controlCodeService::bindControlCodeForAccessPass)         // Ensure revoke list has control codes
                .map(InternalRevocationEvent::buildFrom)
                .collect(toList());

        RevocationLog revocationLog = new RevocationLog();
        revocationLog.addAll(events);

        log.debug("found {} passes", events.size());

        revocationLogResponse.setData(revocationLog);
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
        if (!shouldValidateImeiDeviceId) return true;

        boolean masterKeyCorrect = checkpointConfig.getKeyEntry(masterKey) != null;

        boolean deviceExists = scannerDeviceRepository.findByUniqueDeviceId(deviceId) != null;

        return masterKeyCorrect && deviceExists;
    }

    @Override
    public boolean validateByImei(String masterKey, String imei) {
        if (!shouldValidateImeiDeviceId) return true;

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

    private final JdbcTemplate jdbcTemplate;

    /**
     * Retrieves revoked access passes via JDBC.
     *
     * @param since seconds since epoch
     * @return list of revoked access passes
     */
    @Override
    public List<Map<String, Object>> retrieveRevokedAccessPassesJdbc(Integer since) {
        if (since == null) since = 0;

        final List<Map<String, Object>> revokedAccessPasses = jdbcTemplate.queryForList(
                "SELECT id, date_time_updated\n" +
                        "FROM access_pass\n" +
                        "where status = 'SUSPENDED'\n" +
                        "and date_time_updated > ?",
                OffsetDateTime.ofInstant(Instant.ofEpochSecond(since), ZoneId.systemDefault()));

        // convert all id's into control codes
        return revokedAccessPasses.stream()
                .map(row -> ImmutableMap.<String, Object>of(
                        "controlCode", controlCodeService.encode((Integer) row.get("id")),
                        "timestamp", ((Timestamp) row.get("date_time_updated")).toInstant().getEpochSecond(),
                        "eventType", "RapidPassRevoked"
                ))
                .collect(toList());
    }
}
