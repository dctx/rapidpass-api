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

import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import ph.devcon.rapidpass.api.models.RevocationLogResponse;
import ph.devcon.rapidpass.config.CheckpointConfig;
import ph.devcon.rapidpass.entities.AccessPass;
import ph.devcon.rapidpass.enums.IdTypeVehicle;
import ph.devcon.rapidpass.enums.PassType;
import ph.devcon.rapidpass.repositories.AccessPassRepository;
import ph.devcon.rapidpass.repositories.ScannerDeviceRepository;
import ph.devcon.rapidpass.services.controlcode.ControlCodeService;

import java.time.OffsetDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CheckpointServiceTest {
    @Mock
    private ICheckpointService checkpointService;

    @Mock
    private AccessPassRepository accessPassRepository;

    @Mock
    private ScannerDeviceRepository scannerDeviceRepository;

    @Mock
    private ControlCodeService controlCodeService;

    @Mock
    private CheckpointConfig checkpointConfig;

    @Mock
    private JdbcTemplate mockJdbcTemplate;

    @BeforeEach
    void initializeMocks() {
        checkpointService = new CheckpointServiceImpl(accessPassRepository, scannerDeviceRepository, controlCodeService, checkpointConfig, mockJdbcTemplate);
    }

    //    @Test
    public void TestRetrieveAccessPassByQrCode() {
        checkpointService = new CheckpointServiceImpl(accessPassRepository, scannerDeviceRepository, controlCodeService, checkpointConfig, mockJdbcTemplate);

        // GIVEN
        AccessPass accessPassEntity = createAccessPassEntity();
        String controlCode = "12345A";
        accessPassEntity.setControlCode(controlCode);

        // WHEN
        when(controlCodeService.findAccessPassByControlCode(any()))
                .thenReturn(accessPassEntity);
        when(controlCodeService.bindControlCodeForAccessPass(any()))
                .thenReturn(accessPassEntity);


        // THEN
        AccessPass accessPass = controlCodeService.findAccessPassByControlCode(controlCode);
        assertNotNull(accessPass);
        // check the data elements needed by the UX


        assertEquals(accessPassEntity.getIdentifierNumber(), accessPass.getIdentifierNumber(), "Plate or ID");
        assertEquals(accessPassEntity.getAporType(), accessPass.getAporType(), "APOR Type");
        assertEquals(controlCode, accessPass.getControlCode(), "Control Code");
        assertEquals(accessPassEntity.getIssuedBy(), accessPass.getIssuedBy(), "Approved By");
        assertEquals(accessPassEntity.getValidTo(), accessPass.getValidTo(), "Valid Until");
        assertEquals(accessPassEntity.getReferenceID(), accessPass.getReferenceID(), "Reference ID");
    }

    //    @Test
    public void TestRetrieveAccessPassByPlateNumber() {
        checkpointService = new CheckpointServiceImpl(accessPassRepository, scannerDeviceRepository, controlCodeService, checkpointConfig, mockJdbcTemplate);
        // GIVEN
        AccessPass accessPassEntity = createAccessPassEntity();
        accessPassEntity.setPassType(PassType.VEHICLE.toString());
        accessPassEntity.setIdType(IdTypeVehicle.PLT.toString());
        String idNumber = "xxx-1234";
        accessPassEntity.setIdentifierNumber(idNumber);

        // WHEN
        when(accessPassRepository.findByPassTypeAndIdentifierNumber(PassType.VEHICLE.toString(), idNumber))
                .thenReturn(accessPassEntity);

        when(controlCodeService.bindControlCodeForAccessPass(any()))
                .thenReturn(accessPassEntity);

        // THEN
        AccessPass accessPass = checkpointService.retrieveAccessPassByPlateNo(idNumber);

        assertNotNull(accessPass);
        assertEquals(accessPassEntity.getIdType(), IdTypeVehicle.PLT.toString());
        assertEquals(accessPassEntity.getIdentifierNumber(), idNumber);
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
        accessPassEntity.setReferenceID("M-JIV9H149");
        accessPassEntity.setIssuedBy("ApprovingOrg");
        return accessPassEntity;
    }

    @Test
    public void TestRetrieveRevokedPasses() {
        AccessPass accessPassEntity = new AccessPass();

        OffsetDateTime now = OffsetDateTime.now();

        accessPassEntity.setAporType("ME");
        accessPassEntity.setPassType(PassType.INDIVIDUAL.toString());

        accessPassEntity.setReferenceID("Sample");
        accessPassEntity.setCompany("Sample company");
        accessPassEntity.setDestinationCity("Sample City");
        accessPassEntity.setReferenceID("M-JIV9H149");
        accessPassEntity.setIssuedBy("ApprovingOrg");
        accessPassEntity.setControlCode("SAMPLE_CONTROL_CODE");
        accessPassEntity.setDateTimeUpdated(now);
        accessPassEntity.setStatus("SUSPENDED");

        when(accessPassRepository.findAllByStatus(anyString())).thenReturn(ImmutableList.of(
                accessPassEntity
        ));

        when(controlCodeService.bindControlCodeForAccessPass(any())).thenReturn(accessPassEntity);

        RevocationLogResponse revocationLogResponse = checkpointService.retrieveRevokedAccessPasses(null);

        assertThat(revocationLogResponse.getData().size(), equalTo(1));
        assertThat(revocationLogResponse.getData().get(0).getControlCode(), equalTo("SAMPLE_CONTROL_CODE"));
        assertThat(revocationLogResponse.getData().get(0).getTimestamp().longValue(), equalTo(now.toEpochSecond()));
    }
}
