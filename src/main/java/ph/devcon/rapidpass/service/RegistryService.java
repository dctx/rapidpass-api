package ph.devcon.rapidpass.service;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ph.devcon.rapidpass.jpa.AccessPassRepository;
import ph.devcon.rapidpass.jpa.RegistrantRepository;
import ph.devcon.rapidpass.jpa.RegistryRepository;
import ph.devcon.rapidpass.model.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        Registrar registrar = registrarResult.orElse(null);

        Registrant registrant = new Registrant();
        // set essential fields for registrant
        if (registrarResult.isPresent()) {
            registrant.setRegistrarId(registrarResult.get());
        } else {
            log.error("Unable to retrieve Registrar");
        }
        registrant.setRegistrantType(1);
        registrant.setRegistrantName(rapidPassRequest.getFirstName() + " " + rapidPassRequest.getLastName());
        registrant.setFirstName(rapidPassRequest.getFirstName());
        registrant.setLastName(rapidPassRequest.getLastName());
        registrant.setEmail(rapidPassRequest.getEmail());
        registrant.setMobile(rapidPassRequest.getMobileNumber());
        registrant.setReferenceId(rapidPassRequest.getPlateOrId());
        registrant = registrantRepository.save(registrant);
        // map an access pass to the registrant
        AccessPass accessPass = new AccessPass();
        accessPass.setRegistrantId(registrant);
        accessPass.setReferenceId(registrant.getMobile());
        accessPass.setName(registrant.getFirstName() + " " + registrant.getLastName());
        accessPass.setPlateOrId(rapidPassRequest.getPlateOrId());
        accessPass.setPassType(rapidPassRequest.getPassType().toString());
        accessPass.setStatus("pending");

        log.info("Persisting Registrant: {}", registrant.toString());
        accessPass = accessPassRepository.saveAndFlush(accessPass);

        return RapidPass.buildFrom(accessPass);
    }

    public List<RapidPass> findAll() {
        List<AccessPass> accessPassList = accessPassRepository.findAll();

        return accessPassList
                .stream()
                .map(RapidPass::buildFrom)
                .collect(Collectors.toList());
    }

    public RapidPass find(String referenceId) {
        Optional<AccessPass> potentialAccessPass = accessPassRepository.findAll()
                .stream()
                .filter(accessPass -> accessPass.getReferenceId().equals(referenceId))
                .findFirst();

        return potentialAccessPass.map(RapidPass::buildFrom)
                .orElse(null);

    }
}
