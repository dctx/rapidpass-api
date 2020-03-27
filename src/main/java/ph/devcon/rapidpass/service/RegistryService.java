package ph.devcon.rapidpass.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ph.devcon.rapidpass.jpa.AccessPassRepository;
import ph.devcon.rapidpass.model.AccessPass;
import ph.devcon.rapidpass.model.RapidPass;
import ph.devcon.rapidpass.model.RapidPassRequest;

import java.util.ArrayList;
import java.util.List;

@Component
public class RegistryService {

    private AccessPassRepository accessPassRepository;

    @Autowired // this should be mapped to a service
    public RegistryService(AccessPassRepository accessPassRepository) {
        this.accessPassRepository = accessPassRepository;
    }

    public RapidPass newRequestPass(RapidPassRequest rapidPassRequestDTO) {
        return null;
    }

    public List<RapidPass> findAll() {
        List<AccessPass> accessPassList = accessPassRepository.findAll();
        List<RapidPass> dtoList = new ArrayList<>();
        accessPassList.forEach((a) -> {
            RapidPass dto = new RapidPass();
            dto.setControlCode(a.getControlCode().toString());
            dto.setPlateOrId(a.getPlateOrId());
            dto.setReferenceId(a.getReferenceId());
            dto.setStatus(a.getStatus());
            dto.setValidUntil(a.getValidTo());
            dto.setValidFrom(a.getValidFrom());
            dtoList.add(dto);
        });
        return dtoList;
    }
}
