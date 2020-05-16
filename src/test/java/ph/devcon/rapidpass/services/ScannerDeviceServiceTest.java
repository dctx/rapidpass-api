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
import ph.devcon.rapidpass.services.MobileDeviceService.MobileDeviceFilter;

import java.util.List;
import java.util.Optional;

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
        mobileDeviceService.getMobileDevices(MobileDeviceFilter.builder().id("TEST3").build());
        // model
        mobileDeviceService.getMobileDevices(MobileDeviceFilter.builder().model("TEST4").build());


        // combinations
        mobileDeviceService.getMobileDevices(
                MobileDeviceFilter.builder()
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
        mobileDeviceService.registerMobileDevice(MobileDevice.builder().imei("test123").build());

        // verify device created
        final Optional<MobileDevice> optMobileDevice = mobileDeviceService.getMobileDevice("test123");
        assertThat(optMobileDevice.isPresent(), is(true));

        final MobileDevice test123 = optMobileDevice.get();
        assertThat(test123.getImei(), is("test123"));

        // cleanup
        jdbcTemplate.update("delete from scanner_device where unique_device_id = 'test123'");
    }

    @Test
    void removeScannerDevice() {
        // create device
        mobileDeviceService.registerMobileDevice(MobileDevice.builder().imei("test123").build());

        // verify created
        List<MobileDevice> test123 = mobileDeviceService.getMobileDevices(MobileDeviceFilter.builder().id("test123").build());
        assertThat(test123, is(not(empty())));

        // do remove
        mobileDeviceService.removeMobileDevice("test123");

        // verify deleted
        test123 = mobileDeviceService.getMobileDevices(MobileDeviceFilter.builder().id("test123").build());
        assertThat(test123, is((empty())));
    }

    @Test
    void updateScannerDevice() {
        // create device
        final ScannerDevice device = new ScannerDevice();

        String SAMPLE_IMEI = "SAMEPLE_IMEI_FFFFFFFFFF";
        String SAMPLE_MODEL = "SAMPLE_MODEL_MYPHONE";
        device.setUniqueDeviceId(SAMPLE_IMEI);
        MobileDevice newMobileDevicePayload = MobileDevice.builder().imei(SAMPLE_IMEI).build();
        mobileDeviceService.registerMobileDevice(newMobileDevicePayload);

        MobileDeviceFilter mobileDeviceFilter = MobileDeviceFilter.builder().id(SAMPLE_IMEI).build();

        // verify created
        List<MobileDevice> matchedDevice = mobileDeviceService.getMobileDevices(mobileDeviceFilter);
        assertThat(matchedDevice, is(not(empty())));

        // do update
        MobileDevice updateMobileDevicePayload = MobileDevice.builder()
                .imei(SAMPLE_IMEI)
                .model(SAMPLE_MODEL)
                .build();
        mobileDeviceService.updateMobileDevice(updateMobileDevicePayload);

        // verify updated
        matchedDevice = mobileDeviceService.getMobileDevices(mobileDeviceFilter);
        assertThat(matchedDevice.get(0).getModel(), is(SAMPLE_MODEL));

        // cleanup
        mobileDeviceService.removeMobileDevice(SAMPLE_IMEI);
    }
}