package ph.devcon.rapidpass.service;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ph.devcon.rapidpass.jpa.AccessPassRepository;
import ph.devcon.rapidpass.jpa.RegistrantRepository;
import ph.devcon.rapidpass.jpa.RegistryRepository;
import ph.devcon.rapidpass.model.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class RegistryService {

    private RegistryRepository registryRepository;
    private RegistrantRepository registrantRepository;
    private AccessPassRepository accessPassRepository;

    @Autowired // this should be mapped to a service
    public RegistryService(RegistryRepository registryRepository, RegistrantRepository registrantRepository, AccessPassRepository accessPassRepository) {
        this.registryRepository = registryRepository;
        this.registrantRepository = registrantRepository;
        this.accessPassRepository = accessPassRepository;
    }

    public RapidPass newRequestPass(RapidPassRequest rapidPassRequest) {
        log.info("New RapidPass Request: {}", rapidPassRequest);

        Optional<Registrar> registrarResult = registryRepository.findById(1 );
        Registrar registrar = registrarResult.isPresent() ? registrarResult.get() : null;

        Registrant registrant = new Registrant();
        // set essential fields for registrant
        if (registrarResult.isPresent()) {
            registrant.setRegistrarId(registrarResult.get());
        } else {
            log.error("Unable to retrieve Registrar");
        }
        registrant.setFirstName(rapidPassRequest.getFirstName());
        registrant.setLastName(rapidPassRequest.getLastName());
        registrant.setEmail(rapidPassRequest.getEmail());
        registrant.setMobile(rapidPassRequest.getMobileNumber());
        registrant.setReferenceId(rapidPassRequest.getPlateOrId());
        // map an access pass to the registrant
        AccessPass accessPass = new AccessPass();
        accessPass.setReferenceId(registrant.getMobile());
        accessPass.setName(registrant.getFirstName() + " " + registrant.getLastName());
        accessPass.setPlateOrId(rapidPassRequest.getPlateOrId());
        accessPass.setPassType(rapidPassRequest.getPassType().toString());
        accessPass.setStatus("pending");
        Collection<AccessPass> accessPasses = new ArrayList<>();
        accessPasses.add(accessPass);

        registrant.setAccessPassCollection(accessPasses);

        log.info("Persisting Registrant: {}", registrant.toString());
        registrantRepository.saveAndFlush(registrant);

//        registrar.getRegistrantCollection().add(registrant);
//        registryRepository.save(registrar);
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
