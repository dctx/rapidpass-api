package ph.devcon.rapidpass.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ph.devcon.rapidpass.entities.AccessPass;
import ph.devcon.rapidpass.entities.ControlCode;
import ph.devcon.rapidpass.entities.Registrant;
import ph.devcon.rapidpass.entities.Registrar;
import ph.devcon.rapidpass.enums.AccessPassStatus;
import ph.devcon.rapidpass.models.RapidPass;
import ph.devcon.rapidpass.models.RapidPassBatchRequest;
import ph.devcon.rapidpass.models.RapidPassRequest;
import ph.devcon.rapidpass.repositories.AccessPassRepository;
import ph.devcon.rapidpass.repositories.RegistrantRepository;
import ph.devcon.rapidpass.repositories.RegistryRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class RegistryService {

    public static final int DEFAULT_VALIDITY_DAYS = 15;

    private final RegistryRepository registryRepository;
    private final RegistrantRepository registrantRepository;
    private final AccessPassRepository accessPassRepository;
    private final AccessPassNotifierService accessPassNotifierService;

    /**
     * Creates a new {@link RapidPass} with PENDING status.
     *
     * @param rapidPassRequest rapid passs request.
     * @return new rapid pass with PENDING status
     */
    public RapidPass newRequestPass(RapidPassRequest rapidPassRequest) {
        // see https://gitlab.com/dctx/rapidpass/rapidpass-api/-/issues/64 for documentation on the flow.
        log.debug("New RapidPass Request: {}", rapidPassRequest);

        // check if there is an existing PENDING/APPROVED RapidPass for referenceId
        final List<AccessPass> existingAcessPasses = accessPassRepository
                .findAllByReferenceIDOrderByValidToDesc(rapidPassRequest.getIdentifierNumber());

        final Optional<AccessPass> existingAccessPass;
        if (existingAcessPasses != null) {
            existingAccessPass = existingAcessPasses
                    .stream()
                    // get all valid PENDING or APPROVED rapid pass requests for referenceid
                    .filter(accessPass -> !AccessPassStatus.DECLINED.toString().equalsIgnoreCase(accessPass.getStatus())
                            && accessPass.getValidTo().isAfter(OffsetDateTime.now()))
                    .findAny();
        } else existingAccessPass = Optional.empty();

        if (existingAccessPass.isPresent()) {
            log.debug("  existing pass exists!");
            throw new IllegalArgumentException(
                    String.format("An existing PENDING/APPROVED RapidPass already exists for %s",
                            rapidPassRequest.getIdentifierNumber()));
        }

        // check if registrant is already in the system
        Registrant registrant = registrantRepository.findByReferenceId(rapidPassRequest.getIdentifierNumber());
        if (registrant == null) registrant = new Registrant();

        registrant.setRegistrantType(1);
        registrant.setRegistrantName(rapidPassRequest.getName());
        registrant.setFirstName(rapidPassRequest.getFirstName());
        registrant.setMiddleName(rapidPassRequest.getMiddleName());
        registrant.setLastName(rapidPassRequest.getLastName());
        registrant.setSuffix(rapidPassRequest.getSuffix());
        registrant.setEmail(rapidPassRequest.getEmail());
        registrant.setMobile(rapidPassRequest.getMobileNumber());
        registrant.setReferenceIdType(rapidPassRequest.getIdType());
        registrant.setReferenceId(rapidPassRequest.getIdentifierNumber());

        // create/update registrant
        registrant = registrantRepository.save(registrant);

        Optional<Registrar> registrarResult = registryRepository.findById(1);

        // set essential fields for registrant
        if (registrarResult.isPresent()) {
            registrant.setRegistrarId(registrarResult.get());
        } else {
            log.error("Unable to retrieve Registrar");
        }

        // map a new  access pass to the registrant
        AccessPass accessPass = new AccessPass();
        accessPass.setRegistrantId(registrant);
        accessPass.setReferenceID(registrant.getMobile());
        accessPass.setPassType(rapidPassRequest.getPassType().toString());
        accessPass.setAporType(rapidPassRequest.getAporType());
        accessPass.setIdType(rapidPassRequest.getIdType());
        accessPass.setIdentifierNumber(rapidPassRequest.getIdentifierNumber());
        StringBuilder name = new StringBuilder(registrant.getFirstName());
        name.append(" ").append(registrant.getLastName());
        if (null != registrant.getSuffix() && !registrant.getSuffix().isEmpty()) {
            name.append(" ").append(registrant.getSuffix());
        }
        accessPass.setName(name.toString());
        accessPass.setCompany(rapidPassRequest.getCompany());
        accessPass.setOriginName(rapidPassRequest.getOriginName());
        accessPass.setOriginStreet(rapidPassRequest.getOriginStreet());
        accessPass.setOriginCity(rapidPassRequest.getOriginCity());
        accessPass.setDestinationName(rapidPassRequest.getDestName());
        accessPass.setDestinationStreet(rapidPassRequest.getDestStreet());
        accessPass.setDestinationCity(rapidPassRequest.getDestCity());
        OffsetDateTime now = OffsetDateTime.now();
        accessPass.setValidFrom(now);
        accessPass.setValidTo(now.plusDays(DEFAULT_VALIDITY_DAYS));
        accessPass.setDateTimeCreated(now);
        accessPass.setDateTimeUpdated(now);
        accessPass.setStatus("PENDING");

        log.debug("Persisting Registrant: {}", registrant.toString());
        accessPass = accessPassRepository.saveAndFlush(accessPass);

        return RapidPass.buildFrom(accessPass);
    }

    public List<RapidPass> findAllRapidPasses() {
        return this.findAllAccessPasses()
                .stream()
                .map(RapidPass::buildFrom)
                .collect(Collectors.toList());
    }

    public Iterable<ControlCode> getControlCodes() {
        return accessPassRepository
                .findAll()
                .stream()
                .map(ControlCode::buildFrom)
                .collect(Collectors.toList());
    }

    public List<AccessPass> findAllAccessPasses() {
        return accessPassRepository.findAll();
    }

    /**
     * Used when the inspector or approver wishes to view more details about the
     *
     * @param referenceId The reference id of the {@link AccessPass} you are retrieving.
     * @return Data stored on the database
     */
    public RapidPass find(String referenceId) {
//        AccessPass accessPass = accessPassRepository.findByReferenceId(referenceId);
        // TODO: how to deal with 'renewals'? i.e.
        List<AccessPass> accessPasses = accessPassRepository.findAllByReferenceIDOrderByValidToDesc(referenceId);
        if (accessPasses.size() > 1) {
            log.error("Multiple Access Pass found for reference ID: {}", referenceId);
        } else if (accessPasses.size() <= 0) {
            return null;
        }
        return RapidPass.buildFrom(accessPasses.get(0));
    }

    /**
     * After updating the target {@link AccessPass}, this returns a {@link RapidPass} whose status is granted.
     *
     * @param referenceId      The reference id of the {@link AccessPass} you are retrieving.
     * @param rapidPassRequest The data update for the rapid pass request
     * @return Data stored on the database
     */
    public RapidPass update(String referenceId, RapidPassRequest rapidPassRequest) throws UpdateAccessPassException {
        AccessPass accessPass = accessPassRepository.findByReferenceID(referenceId);

        String status = accessPass.getStatus();

        boolean isPending = AccessPassStatus.PENDING.toString().equals(status);

        if (!isPending) {
            throw new UpdateAccessPassException("An access pass can only be updated if it is pending. Afterwards, it can only be revoked.");
        }

        if (rapidPassRequest.getAccessPassStatus() != null)
            accessPass.setStatus(rapidPassRequest.getAccessPassStatus().toString());

        if (rapidPassRequest.getAccessPassStatus() != null) {
            accessPass.setStatus(rapidPassRequest.getAccessPassStatus().toString());
        }

        accessPass.setRemarks(rapidPassRequest.getRemarks());
        accessPass.setAporType(rapidPassRequest.getAporType());
        accessPass.setCompany(rapidPassRequest.getCompany());
        accessPass.setDestinationCity(rapidPassRequest.getDestCity());
        accessPass.setDestinationName(rapidPassRequest.getDestName());
        accessPass.setDestinationStreet(rapidPassRequest.getDestStreet());

        accessPass.setOriginName(rapidPassRequest.getOriginName());
        accessPass.setOriginCity(rapidPassRequest.getOriginCity());
        accessPass.setOriginStreet(rapidPassRequest.getOriginStreet());

        accessPass.setIdentifierNumber(rapidPassRequest.getIdentifierNumber());
        accessPass.setIdType(rapidPassRequest.getIdType());

        accessPass.setPassType(rapidPassRequest.getPassType().toString());

        if (rapidPassRequest.getPassType() != null)
            accessPass.setPassType(rapidPassRequest.getPassType().toString());

        accessPass.setName(rapidPassRequest.getName());

        // TODO: We need to verify that only the authorized people to modify this pass are allowed.
        // E.g. approvers, or the owner of this pass. People should not be able to re-associate an existing pass from one registrant to another.
        // accessPass.setRegistrantId();

        // TODO: This update operation doesn't update the access pass' validity. we used a constant value for now.

        AccessPass savedAccessPass = accessPassRepository.saveAndFlush(accessPass);
        return RapidPass.buildFrom(savedAccessPass);
    }


    /**
     * After updating the target {@link AccessPass}, this returns a {@link RapidPass} whose status is granted.
     *
     * @param referenceId The reference id of the {@link AccessPass} you are retrieving.
     * @param status      The status to apply
     * @return Data stored on the database
     */
    private RapidPass updateStatus(String referenceId, AccessPassStatus status) throws RegistryService.UpdateAccessPassException {
        List<AccessPass> accessPassesRetrieved = accessPassRepository.findAllByReferenceIDOrderByValidToDesc(referenceId);

        if (accessPassesRetrieved.isEmpty()) {
            throw new NullPointerException("Failed to retrieve access pass with reference ID=" + referenceId + ".");
        }

        AccessPass accessPass = accessPassesRetrieved.get(0);

        if (accessPassesRetrieved.size() > 1) {
            log.warn("Found " + accessPassesRetrieved.size() + " AccessPasses for referenceId=" + referenceId + ".");
        }

        String currentStatus = accessPass.getStatus();

        boolean isPending = AccessPassStatus.PENDING.toString().equals(currentStatus);

        if (!isPending) {
            throw new RegistryService.UpdateAccessPassException("An access pass can only be updated if it is pending. Afterwards, it can only be revoked.");
        }

        accessPass.setStatus(status.toString());

        // TODO: We need to verify that only the authorized people to modify this pass are allowed.
        // E.g. approvers, or the owner of this pass. People should not be able to re-associate an existing pass from one registrant to another.
        // accessPass.setRegistrantId();

        // TODO: This update operation doesn't update the access pass' validity. we used a constant value for now.

        AccessPass savedAccessPass = accessPassRepository.saveAndFlush(accessPass);
        return RapidPass.buildFrom(savedAccessPass);
    }

    public RapidPass grant(String referenceId) throws RegistryService.UpdateAccessPassException {
        return this.updateStatus(referenceId, AccessPassStatus.APPROVED);
    }

    public RapidPass decline(String referenceId) throws RegistryService.UpdateAccessPassException {
        return this.updateStatus(referenceId, AccessPassStatus.DECLINED);
    }

    /**
     * After updating the target {@link AccessPass}, this returns a {@link RapidPass} whose status is revoked.
     *
     * @param referenceId The reference id of the {@link AccessPass} you are retrieving.
     * @return Data stored on the database
     */
    public RapidPass revoke(String referenceId) {
        // TODO: implement
        return null;
    }

    /**
     * Returns a list of rapid passes that were requested for granting or approval.
     * <p>
     * TODO: This needs to be reworked such that the batch data is not uploaded via json request body, but by excel file or csv.
     *
     * @param rapidPassBatchRequest JSON object containing an array of rapid pass requests
     * @return a list of generated rapid passes, whose status are all pending (because they have just been requested).
     */
    public Iterable<RapidPass> batchUpload(RapidPassBatchRequest rapidPassBatchRequest) {
        // TODO: implement
        return null;
    }


    public RapidPass updateAccessPass(String referenceId, RapidPass rapidPass) throws UpdateAccessPassException {
        final RapidPass updatedRapidPass;
        final String status = rapidPass.getStatus();
        if (AccessPassStatus.APPROVED.toString().equals(status)) {
            // persist approval
            updatedRapidPass = grant(referenceId);
            // push APPROVED notifications
            accessPassNotifierService.pushApprovalNotifs(accessPassRepository.findByReferenceID(referenceId));
        } else if (AccessPassStatus.DECLINED.toString().equals(status)) {
            updatedRapidPass = decline(referenceId);
            // push DENIED notifications
            // TODO DENIED NOTIFICATIONS!
        } else {
            throw new IllegalArgumentException("Request Status unknown");
        }

        return updatedRapidPass;
    }

    /**
     * This is thrown when updates are not allowed for the AccessPass.
     */
    public static class UpdateAccessPassException extends Exception {
        public UpdateAccessPassException(String s) {
            super(s);
        }
    }
}
