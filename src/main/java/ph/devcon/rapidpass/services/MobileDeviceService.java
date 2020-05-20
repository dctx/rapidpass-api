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

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ph.devcon.rapidpass.entities.ScannerDevice;
import ph.devcon.rapidpass.models.MobileDevice;
import ph.devcon.rapidpass.repositories.ScannerDeviceRepository;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ph.devcon.rapidpass.repositories.ScannerDeviceRepository.ScannerDeviceSpecs.*;

/**
 * The {@link MobileDeviceService} class provides operations to support CRUD for {@link MobileDevice}.
 *
 * @author jonasespelita@gmail.com
 */
@Service
@RequiredArgsConstructor
public class MobileDeviceService {
    private final ScannerDeviceRepository scannerDeviceRepository;

    /**
     * Searches for all devices that matches filter.
     *
     * @param mobileDeviceFilter filter with optional parameters
     * @return list of all scanner devices matching filter
     */
    public List<MobileDevice> getMobileDevices(MobileDeviceFilter mobileDeviceFilter) {
        return scannerDeviceRepository.findAll(
                byBrand(mobileDeviceFilter.getBrand())
                        .and(byMobileNumber(mobileDeviceFilter.getMobileNumber()))
                        .and(byModel(mobileDeviceFilter.getModel()))
                        .and(byId(mobileDeviceFilter.getId())))
                .stream()
                .map(MobileDevice::buildFrom)
                .collect(Collectors.toList());
    }

    /**
     * Registers a new scanner device.
     *
     * @param device device to register
     * @return the registered device
     */
    public MobileDevice registerMobileDevice(@Valid MobileDevice device) {
        return MobileDevice.buildFrom(
                scannerDeviceRepository
                        .saveAndFlush(device.toScannerDevice()));
    }

    /**
     * Finds for a scanner device with unique device id.
     *
     * @param uniqueDeviceId unique id of device being searched for
     * @return matching scanner device
     */
    public Optional<MobileDevice> getMobileDevice(String uniqueDeviceId) {
        return Optional.ofNullable(MobileDevice.buildFrom(scannerDeviceRepository.findByUniqueDeviceId(uniqueDeviceId)));
    }

    /**
     * Deletes from the database a scanner device.
     *
     * @param uniqueDeviceId device id of scanner device to delete
     */
    public void removeMobileDevice(String uniqueDeviceId) {
        scannerDeviceRepository.delete(scannerDeviceRepository.findByUniqueDeviceId(uniqueDeviceId));
    }

    /**
     * Updates a scanner device. Finds a device with unique device id and updates its attributes.
     *
     * @param updateDevice update to device
     * @return updated scanner device
     */
    public MobileDevice updateMobileDevice(@Valid MobileDevice updateDevice) {
        final ScannerDevice toUpdate = scannerDeviceRepository.findByUniqueDeviceId(updateDevice.getImei());
        final ScannerDevice updateScannerDevice = updateDevice.toScannerDevice();
        updateScannerDevice.setId(toUpdate.getId());
        return MobileDevice.buildFrom(scannerDeviceRepository.saveAndFlush(updateScannerDevice));
    }

    /**
     * Attributes used for filtering scanner devices.
     */
    @Data
    @Builder
    public static class MobileDeviceFilter {
        private String id;
        private String model;
        private String brand;
        private String mobileNumber;
    }

}
