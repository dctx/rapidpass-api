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

import ph.devcon.rapidpass.api.models.KeyEntry;
import ph.devcon.rapidpass.api.models.RevocationLogResponse;
import ph.devcon.rapidpass.entities.AccessPass;
import ph.devcon.rapidpass.entities.ScannerDevice;

import java.util.List;
import java.util.Map;

public interface ICheckpointService
{
    AccessPass retrieveAccessPassByPlateNo(String plateNo);
    AccessPass retrieveAccessPassByQrCode(String qrCode);
    ScannerDevice retrieveDeviceByImei(String imei);
    RevocationLogResponse retrieveRevokedAccessPasses(Integer since);

    boolean validateByMasterKey(String masterKey);
    boolean validateByUniqueDeviceId(String masterKey, String uniqueDeviceId);
    boolean validateByImei(String masterKey, String imei);
    List<KeyEntry> getAllKeys();
    KeyEntry getLatestKeys();

    List<Map<String, Object>> retrieveRevokedAccessPassesJdbc(Integer since);
}
