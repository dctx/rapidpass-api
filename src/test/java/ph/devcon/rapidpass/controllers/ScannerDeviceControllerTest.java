package ph.devcon.rapidpass.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ph.devcon.rapidpass.entities.ScannerDevice;
import ph.devcon.rapidpass.models.MobileDevice;
import ph.devcon.rapidpass.services.ScannerDeviceService;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit Tests for {@link ScannerDeviceController}
 *
 * @author j-espelita@ti.com
 */
@WebMvcTest(controllers = ScannerDeviceController.class)
@AutoConfigureMockMvc(addFilters = false)
class ScannerDeviceControllerTest {

    ObjectMapper jsonMapper = new JsonMapper();
    @Autowired
    MockMvc mockMvc;

    @MockBean
    ScannerDeviceService scannerDeviceService;

    @Test
    void getScannerDevices() throws Exception {
        when(scannerDeviceService.getScannerDevices(any()))
                .thenReturn(Collections.singletonList(MobileDevice.builder().imei("TEST").build()));

        mockMvc.perform(get("/registry/scanner-devices"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].imei").value("TEST"));
    }


    @Test
    void postScannerDevices() throws Exception {

        final ScannerDevice scannerDevice = new ScannerDevice();
        scannerDevice.setUniqueDeviceId("test123");
        scannerDevice.setMobileNumber("123456");
        scannerDevice.setId(1);

        when(scannerDeviceService.registerScannerDevice(any(ScannerDevice.class)))
                .thenReturn(new ScannerDevice());

        mockMvc.perform(post("/registry/scanner-devices")
                .contentType(APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(scannerDevice)))
                .andDo(print()).andExpect(status().isCreated());

        verify(scannerDeviceService, only()).registerScannerDevice(scannerDevice);

    }
}