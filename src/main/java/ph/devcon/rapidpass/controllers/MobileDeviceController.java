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

package ph.devcon.rapidpass.controllers;

import com.google.common.collect.ImmutableMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ph.devcon.rapidpass.api.controllers.NotFoundException;
import ph.devcon.rapidpass.api.models.MobileDevice;
import ph.devcon.rapidpass.entities.ScannerDevice;
import ph.devcon.rapidpass.services.MobileDeviceService;

import java.net.URI;
import java.util.Optional;

/**
 * The {@link MobileDeviceController} class defines endpoints for scanner device CRUD operations.
 *
 * @author jonasespelita@gmail.com
 */
@RestController
@RequestMapping("/registry")
@RequiredArgsConstructor
@Slf4j
public class MobileDeviceController {

    private final MobileDeviceService mobileDeviceService;

    /**
     * Gets scanner devices with optional filters
     *
     * @param id           filter for id = %{id}%
     * @param imei         filter for imei = %{imei}%
     * @param brand        filter for brand = %{brand}%
     * @param mobileNumber filter for mobileNumber = %{mobileNumber}%
     * @param model        filter for model = %{model}%
     * @param pageSize     filter for page size. defaults to 15
     * @param pageNum      filter for page number. defaults to 0 (first page)
     * @return 200 list of access passes matching given filters
     */
    @GetMapping("/scanner-devices")
    public ResponseEntity<?> getMobileDevices(
            @RequestParam(value = "id", required = false) String id,
            @RequestParam(value = "imei", required = false) String imei,
            @RequestParam(value = "brand", required = false) String brand,
            @RequestParam(value = "mobile_number", required = false) String mobileNumber,
            @RequestParam(value = "model", required = false) String model,
            @RequestParam(value = "pageSize", required = false, defaultValue = "15") int pageSize,
            @RequestParam(value = "pageNum", required = false, defaultValue = "0")int pageNum) {
        final MobileDeviceService.MobileDeviceFilter filter =
                MobileDeviceService.MobileDeviceFilter.builder()
                        .deviceId(id)
                        .imei(imei)
                        .brand(brand)
                        .mobileNumber(mobileNumber)
                        .model(model)
                        .pageSize(pageSize)
                        .pageNum(pageNum)
                        .build();

        log.debug("GET scanner-devices {}", filter);
        return ResponseEntity.ok(mobileDeviceService.getMobileDevices(filter));
    }

    /**
     * Registers a new scanner device.
     *
     * @param newDevice scanner device details to create
     * @return 204 on create
     */
    @PostMapping("/scanner-devices")
    public ResponseEntity<?> postScannerDevices(@RequestBody MobileDevice newDevice) {
            final MobileDevice registeredDevice = mobileDeviceService.registerMobileDevice(newDevice);
            return ResponseEntity.created(URI.create(String.valueOf(registeredDevice.getImei()))).build();
    }

    /**
     * Gets the scanner device associated with the unique id
     *
     * @param uniqueDeviceId unique device id
     * @return 200 with scanner device properties if found, 404 if not found
     */
    @GetMapping("/scanner-devices/{unique_id}")
    public ResponseEntity<?> getScannerDevice(@PathVariable("unique_id") String uniqueDeviceId) {
        final Optional<MobileDevice> optScannerDevice = mobileDeviceService.getMobileDevice(uniqueDeviceId);
        return optScannerDevice.isPresent() ? ResponseEntity.ok(optScannerDevice.get()) : ResponseEntity.notFound().build();
    }

    /**
     * Updates the scanner device associated with the unique device id
     *
     * @param uniqueDeviceId unique device id
     * @return 200 on successful update, 400 if scanner device not found
     */
    @PutMapping("/scanner-devices/{unique_id}")
    public ResponseEntity<?> updateScannerDevice(@PathVariable("unique_id") String uniqueDeviceId, @RequestBody MobileDevice mobileDevice) {
        try {
            Optional<ScannerDevice> scannerDevice = mobileDeviceService.findScannerDevice(uniqueDeviceId, uniqueDeviceId);
            if (scannerDevice.isPresent()) {
                MobileDevice device = mobileDeviceService.updateMobileDevice(uniqueDeviceId, mobileDevice);
                return ResponseEntity.ok(device);
            }
            return ResponseEntity.notFound().build();
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Deletes the scanner device associated with the unique device id
     *
     * @param uniqueDeviceId unique device id
     * @return 200 on successful delete, 400 if scanner device not found
     */
    @DeleteMapping("/scanner-devices/{unique_id}")
    public ResponseEntity<?> deleteScannerDevice(@PathVariable("unique_id") String uniqueDeviceId) {
        final Optional<MobileDevice> optScannerDevice = mobileDeviceService.getMobileDevice(uniqueDeviceId);
        if (optScannerDevice.isPresent()) {
            mobileDeviceService.removeMobileDevice(uniqueDeviceId);
            return ResponseEntity.ok().build();
        } else
            return ResponseEntity.badRequest().body(ImmutableMap.of("message", "No scanner device found with unique id " + uniqueDeviceId));

    }
}
