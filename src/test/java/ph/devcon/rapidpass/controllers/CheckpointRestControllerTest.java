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


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.util.UriComponentsBuilder;
import ph.devcon.rapidpass.entities.AccessPass;
import ph.devcon.rapidpass.enums.PassType;
import ph.devcon.rapidpass.services.ICheckpointService;
import ph.devcon.rapidpass.services.controlcode.ControlCodeService;

import java.time.OffsetDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@Disabled
public class CheckpointRestControllerTest extends BaseApiTest
{

    private static Logger LOGGER = Logger.getLogger(CheckpointRestControllerTest.class.getName());

    @MockBean
    private ControlCodeService controlCodeService;

    @MockBean
    private ICheckpointService checkpointService;

    @Test
    public void testGetRapidPassByControlCode() throws Exception {
        // GIVEN
        AccessPass accessPass = createAccessPassEntity();

        String controlCode = "12345A";

        when(controlCodeService.findAccessPassByControlCode(controlCode)).thenReturn(accessPass);

        String endpoint = UriComponentsBuilder.fromUriString("/checkpoint/access-passes/control-codes/").path("/{control-code}").buildAndExpand(controlCode).toString();
        final MvcResult gotData = getData(endpoint);
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        AccessPass retrievedAccessPass = mapper.readValue(gotData.getResponse().getContentAsString(), AccessPass.class);
        assertEquals(controlCode, retrievedAccessPass.getControlCode());
        LOGGER.log(Level.INFO, "AccessPass:" + accessPass);

    }

    @Test
    public void testGetRapidPassByPlateNo() throws Exception {
        // GIVEN
        AccessPass accessPass = createAccessPassEntity();

        String plateNo = "xxx-1234";

        when(checkpointService.retrieveAccessPassByPlateNo(plateNo)).thenReturn(accessPass);

        String endpoint = UriComponentsBuilder.fromUriString("/checkpoint/access-passes/plate-numbers/").path("/{plate-no}").buildAndExpand(plateNo).toString();
        final MvcResult gotData = getData(endpoint);
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        AccessPass retrievedAccessPass = mapper.readValue(gotData.getResponse().getContentAsString(), AccessPass.class);
        assertEquals(plateNo, retrievedAccessPass.getIdentifierNumber());
        LOGGER.log(Level.INFO, "AccessPass:" + accessPass);

    }

    private AccessPass createAccessPassEntity() {
        OffsetDateTime validUntil = OffsetDateTime.now().plusDays(1);
        AccessPass accessPassEntity = new AccessPass();
        accessPassEntity.setValidTo(validUntil);
        accessPassEntity.setAporType("ME");
        accessPassEntity.setPassType(PassType.INDIVIDUAL.toString());
        accessPassEntity.setReferenceID("Sample");
        accessPassEntity.setCompany("Sample company");
        accessPassEntity.setDestinationCity("Sample City");
        accessPassEntity.setIdType("VehicleID");
        accessPassEntity.setReferenceID("M-JIV9H149");
        accessPassEntity.setIssuedBy("ApprovingOrg");
        accessPassEntity.setControlCode("12345A");
        accessPassEntity.setIdentifierNumber("xxx-1234");
        return accessPassEntity;
    }

}
