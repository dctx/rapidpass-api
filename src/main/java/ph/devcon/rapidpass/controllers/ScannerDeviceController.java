package ph.devcon.rapidpass.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ph.devcon.rapidpass.entities.ScannerDevice;
import ph.devcon.rapidpass.services.ScannerDeviceService;

import java.net.URI;

/**
 * The {@link ScannerDeviceController} class defines endpoints for scanner device CRUD operations.
 *
 * @author jonasespelita@gmail.com
 */
@RestController
@RequestMapping("/registry")
@RequiredArgsConstructor
public class ScannerDeviceController {

    private final ScannerDeviceService scannerDeviceService;

    /**
     * Gets scanner devices with optional filters
     *
     * @param id           filter for id = %{id}%
     * @param brand        filter for brand %{brand}%
     * @param mobileNumber filter for brand %{mobileNumber}%
     * @param model        filter for brand %{model}%
     * @return 200 list of access passes matching given filters
     */
    @GetMapping("/scanner-devices")
    public ResponseEntity<?> getScannerDevices(
            @RequestParam(value = "id", required = false) String id,
            @RequestParam(value = "brand", required = false) String brand,
            @RequestParam(value = "mobile_number", required = false) String mobileNumber,
            @RequestParam(value = "model", required = false) String model) {
        final ScannerDeviceService.ScannerDeviceFilter filter =
                ScannerDeviceService.ScannerDeviceFilter.builder()
                        .id(id)
                        .brand(brand)
                        .mobileNumber(mobileNumber)
                        .model(model)
                        .build();

        return ResponseEntity.ok(scannerDeviceService.getScannerDevices(filter));
    }

    /**
     * Registers a new scanner device.
     *
     * @param newDevice scanner device details to create
     * @return 204 on create
     */
    @PostMapping("/scanner-devices")
    public ResponseEntity<?> postScannerDevices(@RequestBody ScannerDevice newDevice) {
        final ScannerDevice registeredDevice = scannerDeviceService.registerScannerDevice(newDevice);
        return ResponseEntity.created(URI.create(String.valueOf(registeredDevice.getId()))).build();
    }
}
