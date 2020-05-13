package ph.devcon.rapidpass.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ph.devcon.rapidpass.entities.ScannerDevice;
import ph.devcon.rapidpass.models.MobileDevice;
import ph.devcon.rapidpass.services.ScannerDeviceService.ScannerDeviceFilter;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration tests for ScannerDeviceService. connects to local db (unless another data source is configured!) use with caution.
 *
 * @author jonasespelita@gmail.com
 */
@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({ScannerDeviceService.class})
class ScannerDeviceServiceIT {

    @Autowired
    ScannerDeviceService scannerDeviceService;

    /**
     * Exercises filters. Should not throw exceptions.
     */
    @Test
    void testWhereClauses() {
        // brand
        scannerDeviceService.getScannerDevices(ScannerDeviceFilter.builder().brand("TEST1").build());
        // mobileNumber
        scannerDeviceService.getScannerDevices(ScannerDeviceFilter.builder().mobileNumber("TEST2").build());
        // id
        scannerDeviceService.getScannerDevices(ScannerDeviceFilter.builder().id("TEST3").build());
        // model
        scannerDeviceService.getScannerDevices(ScannerDeviceFilter.builder().model("TEST4").build());


        // combinations
        scannerDeviceService.getScannerDevices(
                ScannerDeviceFilter.builder()
                        .brand("TEST")
                        .mobileNumber("TEST")
                        .id("TEST")
                        .model("TEST")
                        .build());

        // no errors from exercising filters
        assertTrue(true);
    }


    @Autowired
    JdbcTemplate jdbcTemplate;

    @Test
    void registerGetScannerDevice() {
        // create device
        final ScannerDevice device = new ScannerDevice();
        device.setUniqueDeviceId("test123");
        scannerDeviceService.registerScannerDevice(device);

        // verify device created
        final ScannerDevice test123 = scannerDeviceService.getScannerDevice("test123");
        assertThat(test123, is(not(nullValue())));
        assertThat(test123.getUniqueDeviceId(), is("test123"));

        // cleanup
        jdbcTemplate.update("delete from scanner_device where unique_device_id = 'test123'");
    }

    @Test
    void removeScannerDevice() {
        // create device
        final ScannerDevice device = new ScannerDevice();
        device.setUniqueDeviceId("test123");
        scannerDeviceService.registerScannerDevice(device);

        // verify created
        List<MobileDevice> test123 = scannerDeviceService.getScannerDevices(ScannerDeviceFilter.builder().id("test123").build());
        assertThat(test123, is(not(empty())));

        // do remove
        scannerDeviceService.removeScannerDevice("test123");

        // verify deleted
        test123 = scannerDeviceService.getScannerDevices(ScannerDeviceFilter.builder().id("test123").build());
        assertThat(test123, is((empty())));
    }

    @Test
    void updateScannerDevice() {
        // create device
        final ScannerDevice device = new ScannerDevice();
        device.setUniqueDeviceId("test123");
        scannerDeviceService.registerScannerDevice(device);

        // verify created
        List<MobileDevice> test123 = scannerDeviceService.getScannerDevices(ScannerDeviceFilter.builder().id("test123").build());
        assertThat(test123, is(not(empty())));

        // do update
        device.setModel("update model");
        scannerDeviceService.updateScannerDevice(device);

        // verify updated
        test123 = scannerDeviceService.getScannerDevices(ScannerDeviceFilter.builder().id("test123").build());
        assertThat(test123.get(0).getModel(), is("update model"));

        // cleanup
        scannerDeviceService.removeScannerDevice("test123");
    }
}