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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ph.devcon.rapidpass.api.models.MobileDevice;
import ph.devcon.rapidpass.models.MobileDevicesPageView;
import ph.devcon.rapidpass.services.MobileDeviceService;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit Tests for {@link MobileDeviceController}
 *
 * @author jonasespelita@gmail.com
 */
@WebMvcTest(controllers = MobileDeviceController.class)
@AutoConfigureMockMvc(addFilters = false)
class MobileDeviceControllerTest {

    ObjectMapper jsonMapper = new JsonMapper();
    @Autowired
    MockMvc mockMvc;

    @MockBean
    MobileDeviceService mockMobileDeviceService;

    @Test
    void getScannerDevices() throws Exception {
        when(mockMobileDeviceService.getMobileDevices(any()))
                .thenReturn(MobileDevicesPageView.builder()
                        .currentPage(0)
                        .currentPageRows(100)
                        .data(Collections.singletonList(new MobileDevice().imei("TEST")))
                        .build());

        mockMvc.perform(get("/registry/scanner-devices"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentPage").value(0))
                .andExpect(jsonPath("$.currentPageRows").value(100))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].imei").value("TEST"));
    }


    @Test
    void postScannerDevices() throws Exception {
        final MobileDevice mobileDevice = new MobileDevice().imei("test123").mobileNumber("123456");
        when(mockMobileDeviceService.registerMobileDevice(any(MobileDevice.class)))
                .thenReturn(mobileDevice);

        mockMvc.perform(post("/registry/scanner-devices")
                .contentType(APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(mobileDevice)))
                .andDo(print()).andExpect(status().isCreated());

        verify(mockMobileDeviceService, only()).registerMobileDevice(mobileDevice);
    }

    @Test
    void getScannerDevice() throws Exception {
        final MobileDevice mobileDevice = new MobileDevice().imei("test123").mobileNumber("123456");

        when(mockMobileDeviceService.getMobileDevice(anyString())).thenReturn(Optional.empty());
        when(mockMobileDeviceService.getMobileDevice(eq("test123"))).thenReturn(Optional.of(mobileDevice));

        // test found
        mockMvc.perform(get("/registry/scanner-devices/{unique_id}", "test123"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.imei").value("test123"))
                .andExpect(jsonPath("$.mobileNumber").value("123456"));

        // test not found
        mockMvc.perform(get("/registry/scanner-devices/{unique_id}", "notfound"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}