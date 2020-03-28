package ph.devcon.rapidpass.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ph.devcon.rapidpass.entities.AccessPass;
import ph.devcon.rapidpass.repositories.AccessPassRepository;
import ph.devcon.rapidpass.services.ICheckpointService;

@Component
@Slf4j
public class CheckpointServiceImpl implements ICheckpointService {

    private AccessPassRepository accessPassRepository;

    @Autowired
    public CheckpointServiceImpl(AccessPassRepository accessPassRepository) {
        this.accessPassRepository = accessPassRepository;
    }


    @Override
    public AccessPass getAccessPassByControlCode(String controlCode) {
        return this.accessPassRepository.findByControlCode(controlCode);
    }
}
