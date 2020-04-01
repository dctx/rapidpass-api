package ph.devcon.rapidpass.services;

import com.boivie.skip32.Skip32;
import com.google.zxing.WriterException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import ph.devcon.dctx.rapidpass.commons.CrockfordBase32;
import ph.devcon.dctx.rapidpass.commons.Damm32;
import ph.devcon.rapidpass.entities.AccessPass;
import ph.devcon.rapidpass.entities.ControlCode;
import ph.devcon.rapidpass.entities.Registrant;
import ph.devcon.rapidpass.entities.ScannerDevice;
import ph.devcon.rapidpass.enums.AccessPassStatus;
import ph.devcon.rapidpass.models.MobileDevice;
import ph.devcon.rapidpass.models.RapidPass;
import ph.devcon.rapidpass.models.RapidPassCSVdata;
import ph.devcon.rapidpass.models.RapidPassRequest;
import ph.devcon.rapidpass.repositories.AccessPassRepository;
import ph.devcon.rapidpass.repositories.RegistrantRepository;
import ph.devcon.rapidpass.repositories.RegistryRepository;
import ph.devcon.rapidpass.repositories.ScannerDeviceRepository;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.text.ParseException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
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
    private final ScannerDeviceRepository scannerDeviceRepository;

    /**
     * Secret key used for control code generation
     */
    @Value("${rapidpass.controlCode.secretKey:***REMOVED***}")
    private String secretKey = "***REMOVED***";

    /**
     * Creates a new {@link RapidPass} with PENDING status.
     *
     * @param rapidPassRequest rapid passs request.
     * @return new rapid pass with PENDING status
     */
    public RapidPass newRequestPass(RapidPassRequest rapidPassRequest) {
        // see https://gitlab.com/dctx/rapidpass/rapidpass-api/-/issues/64 for documentation on the flow.
        log.debug("New RapidPass Request: {}", rapidPassRequest);

        // check if there is an existing PENDING/APPROVED RapidPass for referenceId which can be mobile number or plate number
        final List<AccessPass> existingAccessPasses = new ArrayList<>();
        existingAccessPasses.addAll(accessPassRepository
                .findAllByReferenceIDOrderByValidToDesc(rapidPassRequest.getMobileNumber()));
        existingAccessPasses.addAll(accessPassRepository
                .findAllByReferenceIDOrderByValidToDesc(rapidPassRequest.getIdentifierNumber()));

        final Optional<AccessPass> existingAccessPass;
        if (existingAccessPasses != null) {
            existingAccessPass = existingAccessPasses
                    .stream()
                    // get all valid PENDING or APPROVED rapid pass requests for referenceid
                    .filter(accessPass -> {
                        final AccessPassStatus status = AccessPassStatus.valueOf(accessPass.getStatus().toUpperCase());
                        switch (status) {
                            case PENDING:
                            case APPROVED:
                                return true;
                            default:
                                return false;
                        }
                    })
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

        // get default registrar and assign it to registrant
//        Optional<Registrar> registrarResult = registryRepository.findById(1);
//        if (registrarResult.isPresent()) {
//            registrant.setRegistrarId(registrarResult.get());
//        } else {
//            log.error("Unable to retrieve Registrar");
//        }

        registrant.setRegistrarId(0);

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
        accessPass.setOriginProvince(rapidPassRequest.getOriginProvince());
        accessPass.setDestinationName(rapidPassRequest.getDestName());
        accessPass.setDestinationStreet(rapidPassRequest.getDestStreet());
        accessPass.setDestinationCity(rapidPassRequest.getDestCity());
        accessPass.setDestinationProvince(rapidPassRequest.getDestProvince());
        OffsetDateTime now = OffsetDateTime.now();
        accessPass.setValidFrom(now);
        accessPass.setValidTo(now.plusDays(DEFAULT_VALIDITY_DAYS));
        accessPass.setDateTimeCreated(now);
        accessPass.setDateTimeUpdated(now);
        accessPass.setRemarks(rapidPassRequest.getRemarks());
        accessPass.setStatus("PENDING");
        accessPass.setRemarks(rapidPassRequest.getRemarks());

        log.debug("Persisting Registrant: {}", registrant.toString());
        accessPass = accessPassRepository.saveAndFlush(accessPass);

        return RapidPass.buildFrom(accessPass);
    }

    public static class ControlCodeGenerator {
        /**
         * Generates a control code.
         *
         * @param originalInput This is a unique pass phrase which can be configured using @value. See
         *                      {@link RegistryService}.
         * @param id            The id of the access pass being generated. This should be unique, so make sure you create the
         *                      {@link AccessPass} first, then retrieve its ID, then use that as a parameter for generating the control
         *                      code of the AccessPass.
         * @return A control code in string format.
         */
        public static String generate(String originalInput, int id) {
            byte[] encryptionKey = DatatypeConverter.parseBase64Binary(originalInput);
            long obfuscatedId = Skip32.encrypt(id, encryptionKey);
            int checkdigit = Damm32.compute(obfuscatedId);
            return CrockfordBase32.encode(obfuscatedId, 7) + CrockfordBase32.encode(checkdigit);
        }
    }

    public List<RapidPass> findAllRapidPasses(Optional<Pageable> pageView) {
        return this.findAllAccessPasses(pageView)
                .stream()
                .map(RapidPass::buildFrom)
                .collect(Collectors.toList());
    }
    
    public List<RapidPass> findAllRapidPassesByStatus(String status, Optional<Pageable> pageView)
    {
        return this.findAllAccessPassesByStatus(status,pageView)
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

    private List<AccessPass> findAllAccessPasses(Optional<Pageable> pageView) {
        if (pageView.isPresent()) {
            return accessPassRepository.findAll(pageView.get()).toList();
        } else {
            return accessPassRepository.findAll();
        }
    }
    
    private List<AccessPass> findAllAccessPassesByStatus(String status,Optional<Pageable> pageView) {
        if (pageView.isPresent()) {
            return accessPassRepository.findAllByStatus(pageView.get(),status).toList();
        } else {
            return accessPassRepository.findAllByStatus(Pageable.unpaged(),status).toList();
        }
    }

    /**
     * Helper function to retrieve an {@link AccessPass} by referenceId.
     * <p>
     * We need this helper function right now because access passes can have multiple instances given the same
     * referenceId (because we are currently using mobile numbers as the referenceId).
     * <p>
     * Eventually, referenceIds will be unique. Then, this code will become deprecated, because
     * accessPassRepository.findByReferenceId() will be unique.
     *
     * @param referenceId the reference ID, which is the user's mobile number.
     * @return An access pass
     */
    private AccessPass findByNonUniqueReferenceId(String referenceId) {
        // AccessPass accessPass = accessPassRepository.findByReferenceId(referenceId);
        // TODO: how to deal with 'renewals'? i.e.

        List<AccessPass> accessPasses = accessPassRepository.findAllByReferenceIDOrderByValidToDesc(referenceId);

        if (accessPasses.size() <= 0) return null;

        if (accessPasses.size() > 1) {
            // Setting this to warning, as this is an expected use-case while reference IDs are mobile numbers.
            log.warn("Multiple Access Pass found for reference ID: {}", referenceId);
        }

        return accessPasses.get(0);
    }

    /**
     * After updating the target {@link AccessPass}, this returns a {@link RapidPass} whose status is granted.
     *
     * @param referenceId      The reference id of the {@link AccessPass} you are retrieving.
     * @param rapidPassRequest The data update for the rapid pass request
     * @return Data stored on the database
     */
    public RapidPass update(String referenceId, RapidPassRequest rapidPassRequest) throws UpdateAccessPassException {
        List<AccessPass> accessPasses = accessPassRepository.findAllByReferenceIDOrderByValidToDesc(referenceId);

        if (accessPasses.size() == 0)
            throw new IllegalArgumentException("No AccessPass found with referenceId=" + referenceId);

        AccessPass accessPass = accessPasses.get(0);

        String status = accessPass.getStatus();

        boolean isPending = AccessPassStatus.PENDING.toString().equals(status);

        if (!isPending) {
            throw new UpdateAccessPassException("An access pass can only be updated if it is pending. Afterwards, it can only be revoked.");
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
        accessPass.setRemarks(rapidPassRequest.getRemarks());

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
     * Used when the inspector or approver wishes to view more details about the
     *
     * @param referenceId The reference id of the {@link AccessPass} you are retrieving.
     * @return Data stored on the database
     */
    public RapidPass find(String referenceId) {

        AccessPass accessPass = findByNonUniqueReferenceId(referenceId);

        if (accessPass != null) return RapidPass.buildFrom(accessPass);

        return null;
    }

    public RapidPass grant(String referenceId) throws UpdateAccessPassException {
        log.debug("APPROVING refId {}", referenceId);
        RapidPass rapidPass = this.updateStatus(referenceId, AccessPassStatus.APPROVED);

        List<AccessPass> accessPasses = accessPassRepository.findAllByReferenceIDOrderByValidToDesc(referenceId);
        if (accessPasses.size() > 0) {
            AccessPass accessPass = accessPasses.get(0);
            accessPass.setControlCode(ControlCodeGenerator.generate(this.secretKey, accessPass.getId()));
            accessPass = accessPassRepository.saveAndFlush(accessPass);
            return RapidPass.buildFrom(accessPass);
        }

        // Generate control code using the unique ID specified by the database.

        throw new IllegalStateException("No access passes found with reference ID " + referenceId + ".");
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

    /**
     * Updates a referenceId with status of rapidPass.
     *
     * @param referenceId reference id to update
     * @param rapidPass   object containing update status
     * @return updated rapid pass
     * @throws UpdateAccessPassException on error updating access pass
     */
    public RapidPass updateAccessPass(String referenceId, RapidPass rapidPass) throws UpdateAccessPassException {
        final RapidPass updatedRapidPass;
        final AccessPassStatus status = AccessPassStatus.valueOf(rapidPass.getStatus().toUpperCase());
        switch (status) {
            case APPROVED:
                updatedRapidPass = grant(referenceId);
                break;
            case DECLINED:
                updatedRapidPass = decline(referenceId);
                break;
            default:
                // todo implement SUSPENDED
                throw new IllegalArgumentException("Request Status not yet supported!");
        }

        // push APPROVED/DENIED notifications.
        // TODO: someday let's do this asynchronously
        accessPassRepository.findAllByReferenceIDOrderByValidToDesc(referenceId)
                .stream()
                .findFirst()
                .ifPresent(accessPass -> {
                    try {
                        accessPassNotifierService.pushApprovalDeniedNotifs(accessPass);
                    } catch (ParseException | IOException | WriterException e) {
                        log.error("Error sending out notifications for " + accessPass, e);
                    }
                });

        return updatedRapidPass;
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
        List<AccessPass> allAccessPasses = accessPassRepository.findAllByReferenceIDOrderByValidToDesc(referenceId);

        Optional<AccessPass> firstAccessPass = allAccessPasses.stream()
                .filter(ap -> AccessPassStatus.APPROVED.toString().equals(ap.getStatus()))
                .findFirst();

        AccessPass accessPass = firstAccessPass.orElse(null);
        if (accessPass == null) return null;

        accessPass.setStatus(AccessPassStatus.SUSPENDED.toString());
        accessPassRepository.saveAndFlush(accessPass);

        return RapidPass.buildFrom(accessPass);
    }


    /**
     * Returns a list of rapid passes that were requested for granting or approval.
     *
     * @param approvedRapidPasses Iterable<RapidPass> of Approved passes application
     * @return a list of generated rapid passes, whose status are all approved.
     */
    public List<RapidPass> batchUpload(List<RapidPassCSVdata> approvedRapidPasses) throws RegistryService.UpdateAccessPassException {

        log.info("Process Batch Approving of AccessPass");
        List<RapidPass> passes = new ArrayList<RapidPass>();
        RapidPass pass;
        for (RapidPassCSVdata rapidPassRequest : approvedRapidPasses) {
            pass = this.newRequestPass(RapidPassRequest.buildFrom(rapidPassRequest));

            if (pass != null) {
                pass = this.grant(rapidPassRequest.getMobileNumber());
                passes.add(pass);
            }
        }
        return passes;
    }


    /**
     * This is thrown when updates are not allowed for the AccessPass.
     */
    public static class UpdateAccessPassException extends Exception {
        public UpdateAccessPassException(String s) {
            super(s);
        }
    }

    /**
     * Retrieve Scanner Devices
     */
    public List<MobileDevice> getScannerDevices(Optional<Pageable> pageView) {
        List<ScannerDevice> scannerDevices;
        if (pageView.isPresent()) {
            return scannerDeviceRepository.findAll(pageView.get()).toList()
                    .stream()
                    .map(MobileDevice::buildFro)
                    .collect(Collectors.toList());
        } else {
            return scannerDeviceRepository.findAll()
                    .stream()
                    .map(MobileDevice::buildFro)
                    .collect(Collectors.toList());
        }
    }
}
