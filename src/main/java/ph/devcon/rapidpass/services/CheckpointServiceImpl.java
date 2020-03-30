package ph.devcon.rapidpass.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ph.devcon.rapidpass.entities.AccessPass;
import ph.devcon.rapidpass.enums.IdType;
import ph.devcon.rapidpass.repositories.AccessPassRepository;

@Service
public class CheckpointServiceImpl implements ICheckpointService {
    private AccessPassRepository accessPassRepository;
    
    @Autowired
    public CheckpointServiceImpl(AccessPassRepository accessPassRepository)
    {
        this.accessPassRepository = accessPassRepository;
    }
    
    @Override
    public AccessPass retrieveAccessPassByControlCode(String controlCode) {
        return this.accessPassRepository.findByControlCode(controlCode);
    }
    
    @Override
    public AccessPass retrieveAccessPassByPlateNo(String plateNo) {
        return this.accessPassRepository.findByIdTypeAndIdentifierNumber(IdType.VehicleID.toString(), plateNo);
    }
    
    @Override
    public AccessPass retrieveAccessPassByQrCode(String qrCode)
    {
        return null;
    }
}
