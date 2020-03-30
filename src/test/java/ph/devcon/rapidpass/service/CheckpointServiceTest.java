package ph.devcon.rapidpass.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import ph.devcon.rapidpass.RapidpassApplication;
import ph.devcon.rapidpass.entities.AccessPass;
import ph.devcon.rapidpass.enums.IdType;
import ph.devcon.rapidpass.enums.PassType;
import ph.devcon.rapidpass.repositories.AccessPassRepository;
import ph.devcon.rapidpass.services.ICheckpointService;
import ph.devcon.rapidpass.services.CheckpointServiceImpl;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = RapidpassApplication.class)
public class CheckpointServiceTest
{
    private ICheckpointService checkpointService;
    
    private AccessPassRepository accessPassRepository;

    @BeforeEach
    void initializeMocks()
    {
        accessPassRepository = Mockito.mock(AccessPassRepository.class);
    }
    
    @Test
    public void TestRetrieveAccessPassByQrCode()
    {
        checkpointService = new CheckpointServiceImpl(accessPassRepository);
        // GIVEN
        AccessPass accessPassEntity = createAccessPassEntity();
        String controlCode = "12345A";
        accessPassEntity.setControlCode(controlCode);
    
        // WHEN
        Mockito.when(accessPassRepository.findByControlCode(controlCode))
            .thenReturn(accessPassEntity);
        
        // THEN
        AccessPass accessPass = checkpointService.retrieveAccessPassByControlCode(controlCode);
        assertNotNull(accessPass);
        // check the data elements needed by the UX
    
        
        assertEquals(accessPassEntity.getIdentifierNumber(),accessPass.getIdentifierNumber(),"Plate or ID");
        assertEquals(accessPassEntity.getAporType(),accessPass.getAporType(),"APOR Type");
        assertEquals(controlCode,accessPass.getControlCode(),"Control Code");
        assertEquals(accessPassEntity.getIssuedBy(),accessPass.getIssuedBy(),"Approved By");
        assertEquals(accessPassEntity.getValidTo(),accessPass.getValidTo(),"Valid Until");
        assertEquals(accessPassEntity.getLastUsedOn(),accessPass.getLastUsedOn(),"LastUsed");
        assertEquals(accessPassEntity.getReferenceID(),accessPass.getReferenceID(),"Reference ID");
    }

    @Test
    public void TestRetrieveAccessPassByPlateNumber() {
        checkpointService = new CheckpointServiceImpl(accessPassRepository);
        // GIVEN
        AccessPass accessPassEntity = createAccessPassEntity();
        String idNumber = "xxx-1234";
        accessPassEntity.setIdentifierNumber(idNumber);

        // WHEN
        Mockito.when(accessPassRepository.findByIdTypeAndIdentifierNumber(IdType.VehicleID.toString(), idNumber))
                .thenReturn(accessPassEntity);

        // THEN
        AccessPass accessPass = checkpointService.retrieveAccessPassByPlateNo(idNumber);

        assertNotNull(accessPass);
        assertEquals(accessPassEntity.getIdentifierNumber(), idNumber);
    }

    private AccessPass createAccessPassEntity() {
        OffsetDateTime validUntil = OffsetDateTime.now().plusDays(1);
        AccessPass accessPassEntity = new AccessPass();
        accessPassEntity.setValidTo(validUntil);
        accessPassEntity.setLastUsedOn(OffsetDateTime.now());
        accessPassEntity.setAporType("ME");
        accessPassEntity.setPassType(PassType.INDIVIDUAL.toString());
        accessPassEntity.setReferenceID("Sample");
        accessPassEntity.setCompany("Sample company");
        accessPassEntity.setDestinationCity("Sample City");
        accessPassEntity.setIdType("VehicleID");
        accessPassEntity.setReferenceID("M-JIV9H149");
        accessPassEntity.setIssuedBy("ApprovingOrg");
        return accessPassEntity;
    }
}
