package ph.devcon.rapidpass.service;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ph.devcon.rapidpass.jpa.AccessPassRepository;
import ph.devcon.rapidpass.jpa.RegistrantRepository;
import ph.devcon.rapidpass.jpa.RegistryRepository;
import ph.devcon.rapidpass.model.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;
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

    /**
     * Used when the inspector or approver wishes to view more details about the
     * @param referenceId The reference id of the {@link AccessPass} you are retrieving.
     * @return Data stored on the database
     */
    public RapidPass find(String referenceId) {
        AccessPass accessPass = accessPassRepository.findByReferenceId(referenceId);
        return RapidPass.buildFrom(accessPass);
    }

    /**
     * After updating the target {@link AccessPass}, this returns a {@link RapidPass} whose status is granted.
     * @param referenceId The reference id of the {@link AccessPass} you are retrieving.
     * @param updateRequest The data update for the rapid pass request
     * @return Data stored on the database
     */
    public RapidPass update(String referenceId, UpdateRapidPassRequest updateRequest) throws UpdateAccessPassException {
        AccessPass accessPass = accessPassRepository.findByReferenceId(referenceId);

        String status = accessPass.getStatus();

        boolean isPending = RapidPassRequest.RequestStatus.PENDING.toString().equals(status);

        if (!isPending) {
            throw new UpdateAccessPassException("An access pass can only be updated if it is pending. Afterwards, the access pass can only be revoked.");
        }

        if (updateRequest.getStatus() != null)
            accessPass.setStatus(updateRequest.getStatus());

        accessPass.setPlateOrId(updateRequest.getPlateOrId());

        if (updateRequest.getPassType() != null)
            accessPass.setPassType(updateRequest.getPassType().toString());

        // TODO: We need to verify that only the authorized people to modify this pass are allowed.
        // E.g. approvers, or the owner of this pass. People should not be able to re-associate an existing pass from one registrant to another.
        // accessPass.setRegistrantId();

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
            sdf.setTimeZone(TimeZone.getTimeZone("Asia/Manila"));

            String text = sdf.format(new Date());
            System.err.println(text);

            Date validUntil = sdf.parse(updateRequest.getValidUntil());
            accessPass.setValidTo(validUntil);
        } catch (ParseException e) {
            throw new UpdateAccessPassException("Invalid date format for property `validUntil`. " + e.getMessage());
        }

        System.out.println(updateRequest);
        System.out.println(accessPass);

        AccessPass savedAccessPass = accessPassRepository.saveAndFlush(accessPass);
        return RapidPass.buildFrom(savedAccessPass);
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

    /**
     * This is thrown when updates are not allowed for the AccessPass.
     */
    public class UpdateAccessPassException extends Throwable {
        public UpdateAccessPassException(String s) {
            super(s);
        }
    }
}
