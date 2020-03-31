package ph.devcon.rapidpass.services;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ph.devcon.rapidpass.entities.AccessPass;
import ph.devcon.rapidpass.enums.IdTypeVehicle;
import ph.devcon.rapidpass.enums.PassType;
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
        AccessPass accessPass = this.accessPassRepository.findByPassTypeAndIdentifierNumber(PassType.VEHICLE.toString(), plateNo);
        return (null != accessPass && StringUtils.equals(IdTypeVehicle.PLT.toString(), accessPass.getIdType())) ? accessPass : null;
    }
    
    @Override
    public AccessPass retrieveAccessPassByQrCode(String qrCode)
    {
        return null;
    }
}
