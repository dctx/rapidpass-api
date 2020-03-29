package ph.devcon.rapidpass.controllers;


import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MvcResult;
import ph.devcon.rapidpass.api.models.AccessPass;
import ph.devcon.rapidpass.api.models.Address;
import ph.devcon.rapidpass.api.models.IdentificationType;
import ph.devcon.rapidpass.api.models.PassRequestType;
import ph.devcon.rapidpass.services.CheckpointService;

import java.time.OffsetDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;


public class CheckpointRestControllerTest extends BaseApiTest
{

    private static Logger LOGGER = Logger.getLogger(CheckpointRestControllerTest.class.getName());
    @MockBean
    private CheckpointService checkpointService;

    @Test
    public void testGetRapidPassByControlCode() throws Exception
    {
        // GIVEN
        OffsetDateTime validUntil = OffsetDateTime.now().plusDays(1);
        ph.devcon.rapidpass.api.models.AccessPass accessPass = new ph.devcon.rapidpass.api.models.AccessPass();
        accessPass.setValidUntil(validUntil);
        accessPass.setLastUsedOn(OffsetDateTime.now());
        accessPass.setAporType("ME");
        accessPass.setPassType(PassRequestType.INDIVIDUAL);
        accessPass.setReferenceID("Sample");
        accessPass.setCompany("Sample company");
        Address address = new Address();
        address.city("Paranaque");
        address.street("my street");
        accessPass.setDestAddress(address);
        accessPass.setIdType(IdentificationType.PERSONALID);
        accessPass.setReferenceID("M-JIV9H149");
        accessPass.setApprovedBy("ApprovingOrg");
        String controlCode = "12345A";
        accessPass.setControlCode(controlCode);

        when(checkpointService.retrieveAccessPassByControlCode(controlCode)).thenReturn(accessPass);

        final MvcResult gotData = getData("/checkpoint/access-pass/verify-control-code/" + controlCode);
        ph.devcon.rapidpass.api.models.AccessPass retrievedAccessPass = mapFromJson(gotData.getResponse().getContentAsString(), AccessPass.class);
        assertEquals(controlCode, retrievedAccessPass.getControlCode());
        LOGGER.log(Level.INFO, "AccessPass:" + accessPass);

    }

}
