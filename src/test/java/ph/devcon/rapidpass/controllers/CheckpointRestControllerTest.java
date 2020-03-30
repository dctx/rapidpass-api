package ph.devcon.rapidpass.controllers;


import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MvcResult;
import ph.devcon.rapidpass.api.models.CommonRapidPassFields;
import ph.devcon.rapidpass.api.models.RapidPass;
import ph.devcon.rapidpass.entities.AccessPass;
import ph.devcon.rapidpass.services.ICheckpointService;
import ph.devcon.rapidpass.utilities.DateOnlyFormat;

import java.time.OffsetDateTime;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;


public class CheckpointRestControllerTest extends BaseApiTest
{

    private static Logger LOGGER = Logger.getLogger(CheckpointRestControllerTest.class.getName());
    @MockBean
    private ICheckpointService checkpointService;

    @Test
    @Disabled
    public void testGetRapidPassByControlCode() throws Exception
    {
        // GIVEN
        OffsetDateTime validUntil = OffsetDateTime.now().plusDays(1);
        AccessPass accessPass = new AccessPass();

        accessPass.setValidTo(validUntil);
        accessPass.setLastUsedOn(OffsetDateTime.now());
        accessPass.setAporType("AG");
        accessPass.setPassType("INDIVIDUAL");

        accessPass.setIdType("Driver's License");
        accessPass.setReferenceID("M-JIV9H149");

        String controlCode = "12345A";
        accessPass.setControlCode(controlCode);

        when(checkpointService.retrieveAccessPassByControlCode(controlCode)).thenReturn(accessPass);

        final MvcResult gotData = getData("/checkpoint/access-pass/verify-control-code/" + controlCode);
        AccessPass retrievedAccessPass = mapFromJson(gotData.getResponse().getContentAsString(), AccessPass.class);
        assertEquals(controlCode, retrievedAccessPass.getControlCode());
        LOGGER.log(Level.INFO, "AccessPass:" + accessPass);

    }

}
