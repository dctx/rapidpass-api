package ph.devcon.rapidpass.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.postgresql.util.PSQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ph.devcon.rapidpass.api.controllers.NotFoundException;
import ph.devcon.rapidpass.api.models.MobileDevice;
import ph.devcon.rapidpass.entities.ScannerDevice;
import ph.devcon.rapidpass.models.MobileDevicesPageView;
import ph.devcon.rapidpass.services.MobileDeviceService.MobileDeviceFilter;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration tests for ScannerDeviceService. connects to local db (unless another data source is configured!) use with caution.
 *
 * @author jonasespelita@gmail.com
 */
@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({MobileDeviceService.class})
class MobileDeviceServiceIT {

    @Autowired
    MobileDeviceService mobileDeviceService;

    /**
     * Exercises filters. Should not throw exceptions.
     */
    @Test
    void testWhereClauses() {
        // brand
        mobileDeviceService.getMobileDevices(MobileDeviceFilter.builder().brand("TEST1").build());
        // mobileNumber
        mobileDeviceService.getMobileDevices(MobileDeviceFilter.builder().mobileNumber("TEST2").build());
        // id
        mobileDeviceService.getMobileDevices(MobileDeviceFilter.builder().deviceId("TEST3").build());
        // model
        mobileDeviceService.getMobileDevices(MobileDeviceFilter.builder().model("TEST4").build());


        // combinations
        mobileDeviceService.getMobileDevices(
                MobileDeviceFilter.builder()
                        .brand("TEST")
                        .mobileNumber("TEST")
                        .deviceId("TEST")
                        .model("TEST")
                        .build());

        // no errors from exercising filters
        assertTrue(true);
    }


    @Autowired
    JdbcTemplate jdbcTemplate;

    @Test
    void getPagedMobileDevices() throws PSQLException {
        // create test data
        mobileDeviceService.registerMobileDevice(new MobileDevice().imei("test1234").id("deviceId1234"));
        mobileDeviceService.registerMobileDevice(new MobileDevice().imei("test1235").id("deviceId1235"));
        mobileDeviceService.registerMobileDevice(new MobileDevice().imei("test1236").id("deviceId1236"));

        try {
            final MobileDevicesPageView mobileDevicesPage =
                    mobileDeviceService.getMobileDevices(MobileDeviceFilter.builder().pageSize(1).build());
            System.out.println("mobileDevicesPage = " + mobileDevicesPage);
            assertThat(mobileDevicesPage.getData(), is(not(empty())));
            assertThat(mobileDevicesPage.getCurrentPage(), is(0));
            assertThat(mobileDevicesPage.getTotalPages(), is(3));
            assertThat(mobileDevicesPage.getCurrentPageRows(), is(1));
            assertThat(mobileDevicesPage.getTotalRows(), is(3L));
        } finally {
            mobileDeviceService.removeMobileDevice("test1234");
            mobileDeviceService.removeMobileDevice("test1235");
            mobileDeviceService.removeMobileDevice("test1236");
        }


    }

    @Test
    void registerGetScannerDevice() throws PSQLException {
        // create device
        mobileDeviceService.registerMobileDevice(new MobileDevice().imei("test123").id("deviceId123"));

        // verify device created
        final Optional<MobileDevice> optMobileDevice = mobileDeviceService.getMobileDevice("deviceId123");
        assertThat(optMobileDevice.isPresent(), is(true));

        final MobileDevice mobileDevice = optMobileDevice.get();
        assertThat(mobileDevice.getImei(), is("test123"));
        assertThat(mobileDevice.getId(), is("deviceId123"));

        // cleanup
        jdbcTemplate.update("delete from scanner_device where unique_device_id = 'deviceId123'");
    }

    @Test
    void removeScannerDevice() throws PSQLException {
        // create device
        mobileDeviceService.registerMobileDevice(new MobileDevice().imei("test123").id("deviceId123"));

        // verify created
        MobileDevicesPageView test123 = mobileDeviceService.getMobileDevices(MobileDeviceFilter.builder().deviceId("deviceId123").build());
        assertThat(test123.getData(), is(not(empty())));

        // do remove
        mobileDeviceService.removeMobileDevice("deviceId123");

        // verify deleted
        test123 = mobileDeviceService.getMobileDevices(MobileDeviceFilter.builder().deviceId("deviceId123").build());
        assertThat(test123.getData(), is((empty())));
    }

    @Test
    void updateScannerDevice() throws PSQLException {

        String SAMPLE_DEVICE_ID = "SAMPLE_DEVICE_ID";
        String SAMPLE_IMEI = "SAMEPLE_IMEI_FFFFFFFFFF";
        String SAMPLE_MODEL = "SAMPLE_MODEL_MYPHONE";

        MobileDevice newMobileDevicePayload = new MobileDevice().id(SAMPLE_DEVICE_ID);
        mobileDeviceService.registerMobileDevice(newMobileDevicePayload);

        // Verify created
        MobileDeviceFilter mobileDeviceFilter = MobileDeviceFilter.builder().deviceId(SAMPLE_DEVICE_ID).build();
        MobileDevicesPageView matchedDevice = mobileDeviceService.getMobileDevices(mobileDeviceFilter);
        assertThat(matchedDevice.getData(), is(not(empty())));

        try {

            // do update on the sample model
            MobileDevice updateMobileDevicePayload = new MobileDevice()
                    .id(SAMPLE_DEVICE_ID)
                    .imei(SAMPLE_IMEI)
                    .model(SAMPLE_MODEL);
            mobileDeviceService.updateMobileDevice(SAMPLE_DEVICE_ID, updateMobileDevicePayload);

            // verify updated
            Optional<ScannerDevice> scannerDevice = mobileDeviceService.findScannerDevice(SAMPLE_DEVICE_ID, SAMPLE_DEVICE_ID);

            assertTrue(scannerDevice.isPresent(), "Scanner device should be present");
            ScannerDevice sd = scannerDevice.get();

            assertThat(sd.getUniqueDeviceId(), is(SAMPLE_DEVICE_ID));
            assertThat(sd.getModel(), is(SAMPLE_MODEL));
            assertThat(sd.getImei(), is(SAMPLE_IMEI));

            // cleanup
            mobileDeviceService.removeMobileDevice(SAMPLE_IMEI);
        } catch (NotFoundException e) {
            assertFalse(false, "Thrown an unexpected exception: " + e.getMessage());
        }
    }
}