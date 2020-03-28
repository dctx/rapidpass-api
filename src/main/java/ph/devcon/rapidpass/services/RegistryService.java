package ph.devcon.rapidpass.services;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import ph.devcon.rapidpass.repositories.AccessPassRepository;
import ph.devcon.rapidpass.repositories.RegistrantRepository;
import ph.devcon.rapidpass.repositories.RegistryRepository;
import ph.devcon.rapidpass.entities.AccessPass;
import ph.devcon.rapidpass.models.RapidPass;
import ph.devcon.rapidpass.models.RapidPassBatchRequest;
import ph.devcon.rapidpass.models.RapidPassRequest;
import ph.devcon.rapidpass.entities.Registrant;
import ph.devcon.rapidpass.entities.Registrar;

@Component
@Slf4j
public class RegistryService {

    public static final int DEFAULT_VALIDITY_DAYS = 15;
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
        registrant.setMiddleName(rapidPassRequest.getMiddleName());
        registrant.setLastName(rapidPassRequest.getLastName());
        registrant.setSuffix(rapidPassRequest.getSuffix());
        registrant.setEmail(rapidPassRequest.getEmail());
        registrant.setMobile(rapidPassRequest.getMobileNumber());
        registrant.setReferenceIdType(rapidPassRequest.getIdType());
        registrant.setReferenceId(rapidPassRequest.getPlateOrId());
        registrant = registrantRepository.save(registrant);
        // map an access pass to the registrant
        AccessPass accessPass = new AccessPass();
        accessPass.setRegistrantId(registrant);
        accessPass.setReferenceId(registrant.getMobile());
        accessPass.setPassType(rapidPassRequest.getPassType().toString());
        accessPass.setAporType(rapidPassRequest.getAporType());
        accessPass.setName(registrant.getFirstName() + " " + registrant.getLastName());
        accessPass.setIdType(rapidPassRequest.getIdType());
        accessPass.setPlateOrId(rapidPassRequest.getPlateOrId());
        Calendar c = Calendar.getInstance();
        accessPass.setValidFrom(c.getTime());
        c.add(Calendar.DATE, DEFAULT_VALIDITY_DAYS);
        accessPass.setValidTo(c.getTime());
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

    /**
     * Used when the inspector or approver wishes to view more details about the
     * @param referenceId The reference id of the {@link AccessPass} you are retrieving.
     * @return Data stored on the database
     */
    public RapidPass find(String referenceId) {
//        AccessPass accessPass = accessPassRepository.findByReferenceId(referenceId);
        // TODO: how to deal with 'renewals'? i.e.
        List<AccessPass> accessPasses = accessPassRepository.findAllByReferenceIdOrderByValidToDesc(referenceId);
        if (accessPasses.size() > 1) {
            log.error("Multiple Access Pass found for reference ID: {}", referenceId);
        } else if (accessPasses.size() <= 0){
            return null;
        }
        return RapidPass.buildFrom(accessPasses.get(0));
    }

    /**
     * After updating the target {@link AccessPass}, this returns a {@link RapidPass} whose status is granted.
     * @param referenceId The reference id of the {@link AccessPass} you are retrieving.
     * @param rapidPassRequest The data for the rapid pass request
     * @return Data stored on the database
     */
    public RapidPass update(String referenceId, RapidPassRequest rapidPassRequest) {
        // TODO: implement
        return null;
    }

    /**
     * After updating the target {@link AccessPass}, this returns a {@link RapidPass} whose status is revoked.
     * @param referenceId The reference id of the {@link AccessPass} you are retrieving.
     * @return Data stored on the database
     */
    public RapidPass revoke(String referenceId) {
        // TODO: implement
        return null;
    }

    /**
     * Returns a list of rapid passes that were requested for granting or approval.
     *
     * TODO: This needs to be reworked such that the batch data is not uploaded via json request body, but by excel file or csv.
     *
     * @param rapidPassBatchRequest JSON object containing an array of rapid pass requests
     * @return a list of generated rapid passes, whose status are all pending (because they have just been requested).
     */
    public Iterable<RapidPass> batchUpload(RapidPassBatchRequest rapidPassBatchRequest) {
        // TODO: implement
        return null;
    }
}
