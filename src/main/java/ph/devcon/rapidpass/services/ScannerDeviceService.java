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
import java.util.stream.Collectors;

import static ph.devcon.rapidpass.repositories.ScannerDeviceRepository.ScannerDeviceSpecs.*;

/**
 * The {@link ScannerDeviceService} class provides operations to support CRUD for {@link ScannerDevice}.
 *
 * @author jonasespelita@gmail.com
 */
@Service
@RequiredArgsConstructor
public class ScannerDeviceService {
    private final ScannerDeviceRepository scannerDeviceRepository;

    /**
     * Searches for all devices that matches filter.
     *
     * @param scannerDeviceFilter filter with optional parameters
     * @return list of all scanner devices matching filter
     */
    public List<MobileDevice> getScannerDevices(ScannerDeviceFilter scannerDeviceFilter) {
        return scannerDeviceRepository.findAll(
                byBrand(scannerDeviceFilter.getBrand())
                        .and(byMobileNumber(scannerDeviceFilter.getMobileNumber()))
                        .and(byModel(scannerDeviceFilter.getModel()))
                        .and(byId(scannerDeviceFilter.getId())))
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
    public ScannerDevice registerScannerDevice(@Valid ScannerDevice device) {
        return scannerDeviceRepository.saveAndFlush(device);
    }

    /**
     * Finds for a scanner device with unique device id.
     *
     * @param uniqueDeviceId unique id of device being searched for
     * @return matching scanner device
     */
    public ScannerDevice getScannerDevice(String uniqueDeviceId) {
        return scannerDeviceRepository.findByUniqueDeviceId(uniqueDeviceId);
    }

    /**
     * Deletes from the database a scanner device.
     *
     * @param uniqueDeviceId device id of scanner device to delete
     */
    public void removeScannerDevice(String uniqueDeviceId) {
        scannerDeviceRepository.delete(scannerDeviceRepository.findByUniqueDeviceId(uniqueDeviceId));
    }

    /**
     * Updates a scanner device. Finds a device with unique device id and updates its attributes.
     *
     * @param updateDevice update to device
     * @return updated scanner device
     */
    public ScannerDevice updateScannerDevice(@Valid ScannerDevice updateDevice) {
        final ScannerDevice toUpdate = scannerDeviceRepository.findByUniqueDeviceId(updateDevice.getUniqueDeviceId());
        updateDevice.setId(toUpdate.getId());
        return scannerDeviceRepository.saveAndFlush(updateDevice);
    }

    /**
     * Attributes used for filtering scanner devices.
     */
    @Data
    @Builder
    public static class ScannerDeviceFilter {
        private String id;
        private String model;
        private String brand;
        private String mobileNumber;
    }

}
