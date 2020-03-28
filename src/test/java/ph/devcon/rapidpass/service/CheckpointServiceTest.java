package ph.devcon.rapidpass.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ph.devcon.rapidpass.RapidpassApplication;
import ph.devcon.rapidpass.entities.AccessPass;
import ph.devcon.rapidpass.repositories.AccessPassRepository;
import ph.devcon.rapidpass.services.CheckpointService;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = RapidpassApplication.class)
public class CheckpointServiceTest
{
    @Autowired
    private CheckpointService checkpointService;
    
    @Autowired
    private AccessPassRepository accessPassRepository;
    
    @Test
    public void TestRetrieveAccessPassByQrCode()
    {
        // GIVEN
        AccessPass accessPassEntity = new AccessPass();
        accessPassEntity.setAporType("ME");
        accessPassEntity.setPassType("INDIVIDUAL");
        accessPassEntity.setReferenceId("Sample");
        accessPassEntity.setCompany("Sample company");
        accessPassEntity.setDestinationAddress("Sample Address");
        accessPassEntity.setIdType("PERSONALID");
        accessPassEntity.setReferenceId("ReferenceID");
        final int controlCode = 12345;
        accessPassEntity.setControlCode(controlCode);
        
        // WHEN
        accessPassRepository.save(accessPassEntity);
    
    
        // THEN
        final ph.devcon.rapidpass.api.models.AccessPass
            accessPass = checkpointService.retrieveAccessPassByControlCode(controlCode);
        assertNotNull(accessPass);
        assertEquals(controlCode,accessPass.getControlCode());
    }
}
