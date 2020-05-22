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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ph.devcon.rapidpass.models.MobileDevice;
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
public class MobileDeviceController {

    private final MobileDeviceService mobileDeviceService;

    /**
     * Gets scanner devices with optional filters
     *
     * @param id           filter for id = %{id}%
     * @param brand        filter for brand = %{brand}%
     * @param mobileNumber filter for mobileNumber = %{mobileNumber}%
     * @param model        filter for model = %{model}%
     * @return 200 list of access passes matching given filters
     */
    @GetMapping("/scanner-devices")
    public ResponseEntity<?> getMobileDevices(
            @RequestParam(value = "id", required = false) String id,
            @RequestParam(value = "brand", required = false) String brand,
            @RequestParam(value = "mobile_number", required = false) String mobileNumber,
            @RequestParam(value = "model", required = false) String model) {
        final MobileDeviceService.MobileDeviceFilter filter =
                MobileDeviceService.MobileDeviceFilter.builder()
                        .id(id)
                        .brand(brand)
                        .mobileNumber(mobileNumber)
                        .model(model)
                        .build();

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
    public ResponseEntity<?> updateScannerDevice(@PathVariable("unique_id") String uniqueDeviceId) {
        final Optional<MobileDevice> optScannerDevice = mobileDeviceService.getMobileDevice(uniqueDeviceId);
        if (optScannerDevice.isPresent()) {
            mobileDeviceService.updateMobileDevice(optScannerDevice.get());
            return ResponseEntity.ok().build();
        } else
            return ResponseEntity.badRequest().body(ImmutableMap.of("message", "No scanner device found with unique id " + uniqueDeviceId));
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
