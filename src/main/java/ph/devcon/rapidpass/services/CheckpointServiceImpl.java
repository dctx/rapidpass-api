package ph.devcon.rapidpass.services;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ph.devcon.rapidpass.entities.AccessPass;
import ph.devcon.rapidpass.repositories.AccessPassRepository;

import java.time.LocalDate;

@Component
public class CheckpointServiceImpl implements CheckpointService
{
    private AccessPassRepository accessPassRepository;
    
    @Autowired
    public CheckpointServiceImpl(AccessPassRepository accessPassRepository)
    {
        this.accessPassRepository = accessPassRepository;
    }
    
    @Override
    public AccessPass retrieveAccessPassByControlCode(String controlCode)
    {
        final ph.devcon.rapidpass.entities.AccessPass
            entity = accessPassRepository.findByControlCode(controlCode);
        AccessPass accessPass = new AccessPass();
        // Copy similar properties
        BeanUtils.copyProperties(entity,accessPass);
        // Manual copy those that are not the same attribute. Or later change the repo to make it simple
        accessPass.setIssuedBy(entity.getIssuedBy());
        accessPass.setValidTo(entity.getValidTo());
        return accessPass;
    }
    
    @Override
    public AccessPass retrieveAccessPassByLicenseNumber(String licenseNumber)
    {
        return null;
    }
    
    @Override
    public AccessPass retrieveAccessPassByQrCode(String qrCode)
    {
        return null;
    }
}
