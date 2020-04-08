package ph.devcon.rapidpass.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import ph.devcon.rapidpass.RapidpassApplication;
import ph.devcon.rapidpass.entities.AccessPass;
import ph.devcon.rapidpass.enums.IdTypeVehicle;
import ph.devcon.rapidpass.enums.PassType;
import ph.devcon.rapidpass.repositories.AccessPassRepository;
import ph.devcon.rapidpass.repositories.ScannerDeviceRepository;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest(classes = RapidpassApplication.class)
public class CheckpointServiceTest
{
    private ICheckpointService checkpointService;
    
    private AccessPassRepository accessPassRepository;

    private ScannerDeviceRepository scannerDeviceRepository;

    private QrPdfService qrPdfService;

    private QrGeneratorService qrGeneratorService;

    @BeforeEach
    void initializeMocks()
    {
        accessPassRepository = Mockito.mock(AccessPassRepository.class);
        scannerDeviceRepository = Mockito.mock(ScannerDeviceRepository.class);
        qrPdfService = Mockito.mock(QrPdfService.class);
        qrGeneratorService = Mockito.mock(QrGeneratorService.class);
    }
    
    @Test
    public void TestRetrieveAccessPassByQrCode()
    {
        checkpointService = new CheckpointServiceImpl(accessPassRepository, scannerDeviceRepository, qrPdfService);

        // GIVEN
        AccessPass accessPassEntity = createAccessPassEntity();
        String controlCode = "12345A";
        accessPassEntity.setControlCode(controlCode);
    
        // WHEN

        Mockito.when(accessPassRepository.findById(any()))
            .thenReturn(Optional.of(accessPassEntity));
        
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
        checkpointService = new CheckpointServiceImpl(accessPassRepository, scannerDeviceRepository, qrPdfService);
        // GIVEN
        AccessPass accessPassEntity = createAccessPassEntity();
        accessPassEntity.setPassType(PassType.VEHICLE.toString());
        accessPassEntity.setIdType(IdTypeVehicle.PLT.toString());
        String idNumber = "xxx-1234";
        accessPassEntity.setIdentifierNumber(idNumber);

        // WHEN
        Mockito.when(accessPassRepository.findByPassTypeAndIdentifierNumber(PassType.VEHICLE.toString(), idNumber))
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
        accessPassEntity.setLastUsedOn(OffsetDateTime.now());
        accessPassEntity.setAporType("ME");
        accessPassEntity.setPassType(PassType.INDIVIDUAL.toString());
        accessPassEntity.setReferenceID("Sample");
        accessPassEntity.setCompany("Sample company");
        accessPassEntity.setDestinationCity("Sample City");
        accessPassEntity.setReferenceID("M-JIV9H149");
        accessPassEntity.setIssuedBy("ApprovingOrg");
        return accessPassEntity;
    }
}
