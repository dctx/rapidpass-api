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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ph.devcon.rapidpass.api.controllers.NotFoundException;
import ph.devcon.rapidpass.api.models.MobileDevice;
import ph.devcon.rapidpass.entities.ScannerDevice;
import ph.devcon.rapidpass.models.MobileDevicesPageView;
import ph.devcon.rapidpass.repositories.ScannerDeviceRepository;

import javax.validation.Valid;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
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

    public static MobileDevice mapToMobileDevice(ScannerDevice scannerDevice) {
        MobileDevice mobileDevice = new MobileDevice();

        mobileDevice = mobileDevice
                .brand(scannerDevice.getBrand())
                .id(scannerDevice.getUniqueDeviceId())
                .imei(scannerDevice.getImei())
                .mobileNumber(scannerDevice.getMobileNumber())
                .model(scannerDevice.getModel())
                .status(scannerDevice.getStatus());

        if (scannerDevice.getDateTimeCreated() != null)
            mobileDevice.setCreatedAt(scannerDevice.getDateTimeCreated().format(DateTimeFormatter.ISO_INSTANT));

        if (scannerDevice.getDateTimeUpdated() != null)
            mobileDevice.setUpdatedAt(scannerDevice.getDateTimeUpdated().format(DateTimeFormatter.ISO_INSTANT));

        return mobileDevice;
    }


    /**
     * Searches for all devices that matches filter.
     *
     * @param mobileDeviceFilter filter with optional parameters
     * @return page of all scanner devices matching filter
     */
    public MobileDevicesPageView getMobileDevices(MobileDeviceFilter mobileDeviceFilter) {

        // retrieve paged scanner devices
        //noinspection ConstantConditions
        final Page<ScannerDevice> scannerDevicePage = scannerDeviceRepository.findAll(
                byBrand(mobileDeviceFilter.getBrand())
                        .and(byMobileNumber(mobileDeviceFilter.getMobileNumber()))
                        .and(byModel(mobileDeviceFilter.getModel()))
                        .and(byIMEI(mobileDeviceFilter.getImei()))
                        .and(byDeviceId(mobileDeviceFilter.getDeviceId())),
                PageRequest.of(
                        mobileDeviceFilter.pageNum,
                        mobileDeviceFilter.pageSize,
                        Sort.by("dateTimeUpdated").ascending()));

        return MobileDevicesPageView.builder().
                currentPage(mobileDeviceFilter.pageNum)
                .currentPageRows(scannerDevicePage.getNumberOfElements())
                .totalPages(scannerDevicePage.getTotalPages())
                .totalRows(scannerDevicePage.getTotalElements())
                .hasNext(scannerDevicePage.hasNext())
                .hasPrevious(scannerDevicePage.hasPrevious())
                .isFirstPage(scannerDevicePage.isFirst())
                .isLastPage(scannerDevicePage.isLast())
                .data(scannerDevicePage.stream()
                        .map(MobileDeviceService::mapToMobileDevice)
                        .collect(toList()))
                .build();
    }

    /**
     * Registers a new scanner device.
     *
     * @param device device to register
     * @return the registered device
     */
    public MobileDevice registerMobileDevice(@Valid MobileDevice device) {

        ScannerDevice scannerDevice = ScannerDevice.buildFrom(device);

        scannerDevice = scannerDeviceRepository.saveAndFlush(scannerDevice);

        return MobileDeviceService.mapToMobileDevice(scannerDevice);
    }

    /**
     * Finds for a scanner device with unique device id.
     *
     * @param uniqueDeviceId unique id of device being searched for
     * @return matching scanner device
     */
    public Optional<MobileDevice> getMobileDevice(String uniqueDeviceId) {

        ScannerDevice scannerDevice = scannerDeviceRepository.findByUniqueDeviceId(uniqueDeviceId);
        if (scannerDevice == null) return Optional.empty();

        return Optional.of(MobileDeviceService.mapToMobileDevice(scannerDevice));
    }

    /**
     * Deletes from the database a scanner device.
     *
     * @param uniqueDeviceId device id of scanner device to delete
     */
    public void removeMobileDevice(String uniqueDeviceId) {
        Optional<ScannerDevice> scannerDevice = findScannerDevice(uniqueDeviceId, uniqueDeviceId);
        scannerDevice.ifPresent(scannerDeviceRepository::delete);
    }

    public Optional<ScannerDevice> findScannerDevice(String deviceId, String imei) {
        ScannerDevice device = scannerDeviceRepository.findByUniqueDeviceId(deviceId);
        if (device != null) return Optional.of(device);

        return Optional.ofNullable(scannerDeviceRepository.findByImei(imei));
    }

    /**
     * Updates a scanner device. Finds a device with unique device id and updates its attributes.
     *
     * @param mobileDevice update to device
     * @return updated scanner device
     */
    public MobileDevice updateMobileDevice(@Valid MobileDevice mobileDevice) throws NotFoundException {
        Optional<ScannerDevice> scannerDevice = findScannerDevice(mobileDevice.getId(), mobileDevice.getImei());

        // Can potentially be an upsert
        ScannerDevice device = scannerDevice.orElse(ScannerDevice.buildFrom(mobileDevice));
        if (scannerDevice.isPresent()) {
            device.setImei(mobileDevice.getImei());
            device.setModel(mobileDevice.getModel());
            device.setBrand(mobileDevice.getBrand());
            device.setStatus(mobileDevice.getStatus());
            device.setMobileNumber(mobileDevice.getMobileNumber());
            device.setUniqueDeviceId(mobileDevice.getId());
            device.setDateTimeUpdated(OffsetDateTime.now());
        }

        ScannerDevice updatedScannerDevice = scannerDeviceRepository.saveAndFlush(device);

        return MobileDeviceService.mapToMobileDevice(updatedScannerDevice);
    }

    /**
     * Attributes used for filtering scanner devices.
     */
    @Data
    @Builder
    public static class MobileDeviceFilter {
        private String deviceId;
        private String imei;
        private String model;
        private String brand;
        private String mobileNumber;

        // pagination filters
        @Builder.Default
        private int pageNum = 0;
        @Builder.Default
        private int pageSize = 30;
    }

}
