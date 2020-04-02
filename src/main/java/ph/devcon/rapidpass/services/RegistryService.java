package ph.devcon.rapidpass.services;

import com.boivie.skip32.Skip32;
import com.google.zxing.WriterException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.stereotype.Component;
import ph.devcon.dctx.rapidpass.commons.CrockfordBase32;
import ph.devcon.dctx.rapidpass.commons.Damm32;
import ph.devcon.rapidpass.entities.AccessPass;
import ph.devcon.rapidpass.entities.ControlCode;
import ph.devcon.rapidpass.entities.Registrant;
import ph.devcon.rapidpass.entities.ScannerDevice;
import ph.devcon.rapidpass.enums.AccessPassStatus;
import ph.devcon.rapidpass.enums.PassType;
import ph.devcon.rapidpass.models.*;
import ph.devcon.rapidpass.repositories.AccessPassRepository;
import ph.devcon.rapidpass.repositories.RegistrantRepository;
import ph.devcon.rapidpass.repositories.RegistryRepository;
import ph.devcon.rapidpass.repositories.ScannerDeviceRepository;
import ph.devcon.rapidpass.validators.StandardDataBindingValidation;
import ph.devcon.rapidpass.validators.entities.NewAccessPassRequestValidator;

import java.io.IOException;
import java.text.ParseException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class RegistryService {

    public static final int DEFAULT_VALIDITY_DAYS = 15;

    private final RegistryRepository registryRepository;
    private final RegistrantRepository registrantRepository;
    private final LookupTableService lookupTableService;
    private final AccessPassRepository accessPassRepository;
    private final AccessPassNotifierService accessPassNotifierService;
    private final ScannerDeviceRepository scannerDeviceRepository;

    /**
     * Secret key used for control code generation
     */
    @Value("${qrmaster.controlkey:***REMOVED***}")
    private String secretKey = "***REMOVED***";

    /**
     * Creates a new {@link RapidPass} with PENDING status.
     *
     * @see <a href="https://gitlab.com/dctx/rapidpass/rapidpass-api/-/issues/64">documentation</a> on the flow.
     *
     * @throws IllegalArgumentException If the plate number is empty and the pass type is for a vehicle
     * @throws IllegalArgumentException Attempting to create a new access pass while an existing pending or approved pass exists
     * @param rapidPassRequest rapid passs request.
     * @return new rapid pass with PENDING status
     */
    public RapidPass newRequestPass(RapidPassRequest rapidPassRequest) {
        // see
        OffsetDateTime now = OffsetDateTime.now();
        log.debug("New RapidPass Request: {}", rapidPassRequest);

        NewAccessPassRequestValidator newAccessPassRequestValidator = new NewAccessPassRequestValidator(this.lookupTableService, this.accessPassRepository);
        StandardDataBindingValidation validation = new StandardDataBindingValidation(newAccessPassRequestValidator);
        validation.validate(rapidPassRequest);

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
        registrant.setRegistrarId(0);
        registrant = registrantRepository.save(registrant);

        // get default registrar and assign it to registrant
//        Optional<Registrar> registrarResult = registryRepository.findById(1);
//        if (registrarResult.isPresent()) {
//            registrant.setRegistrarId(registrarResult.get());
//        } else {
//            log.error("Unable to retrieve Registrar");
//        }

        // map a new  access pass to the registrant
        AccessPass accessPass = new AccessPass();

        accessPass.setRegistrantId(registrant);
        accessPass.setReferenceID( rapidPassRequest.getPassType().equals(PassType.INDIVIDUAL) ?
                registrant.getMobile() : rapidPassRequest.getPlateNumber());
        accessPass.setPassType(rapidPassRequest.getPassType().toString());
        accessPass.setAporType(rapidPassRequest.getAporType());
        accessPass.setIdType(rapidPassRequest.getIdType());
        accessPass.setIdentifierNumber(rapidPassRequest.getIdentifierNumber());
        if (rapidPassRequest.getPlateNumber() != null) {
            accessPass.setPlateNumber(rapidPassRequest.getPlateNumber().trim());
        }
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
            String encodedString = Base64.getEncoder().encodeToString(originalInput.getBytes());
            byte[] decodedBytes = Base64.getDecoder().decode(encodedString);
            long obfuscatedId = Skip32.encrypt(id, decodedBytes);
            int checkdigit = Damm32.compute(obfuscatedId);
            return CrockfordBase32.encode(obfuscatedId, 7) + CrockfordBase32.encode(checkdigit);
        }
    }

    public List<RapidPass> findAllRapidPasses(String aporType, Optional<Pageable> pageView) {
        return this.findAllAccessPasses(aporType, pageView)
                .stream()
                .map(RapidPass::buildFrom)
                .collect(Collectors.toList());
    }

    public Page<RapidPass> findAllApprovedOrSuspendedRapidPassAfter(OffsetDateTime lastUpdatedOn, Pageable page)
    {
        final Page<AccessPass> pagedAccessPasses = accessPassRepository.findAllApprovedAndSuspendedSince(lastUpdatedOn, page);
        Function<List<AccessPass>, List<RapidPass>> collectionTransform = accessPasses -> accessPasses.stream()
            .map(RapidPass::buildFrom)
            .collect(Collectors.toList());

        return PageableExecutionUtils.getPage(
            collectionTransform.apply(pagedAccessPasses.getContent()),
            page,
            pagedAccessPasses::getTotalElements);
    }

    public Page<RapidPassCSVDownloadData> findAllApprovedOrSuspendedRapidPassCsvAfter(OffsetDateTime lastUpdatedOn, Pageable page)
    {
        final Page<AccessPass> pagedAccessPasses = accessPassRepository.findAllApprovedAndSuspendedSince(lastUpdatedOn, page);
        Function<List<AccessPass>, List<RapidPassCSVDownloadData>> collectionTransform = accessPasses -> accessPasses.stream()
            .map(RapidPassCSVDownloadData::buildFrom)
            .collect(Collectors.toList());

        return PageableExecutionUtils.getPage(
            collectionTransform.apply(pagedAccessPasses.getContent()),
            page,
            pagedAccessPasses::getTotalElements);
    }

    public Iterable<ControlCode> getControlCodes() {
        return accessPassRepository
                .findAll()
                .stream()
                .map(ControlCode::buildFrom)
                .collect(Collectors.toList());
    }

    private List<AccessPass> findAllAccessPasses(String aporType, Optional<Pageable> pageView) {
        Pageable pageable = pageView.orElse(Pageable.unpaged());

        if (!StringUtils.isBlank(aporType)) {
            return accessPassRepository.findAllByAporType(aporType, pageable).toList();
        } else {
            return accessPassRepository.findAll(pageable).toList();
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
    public AccessPass findByNonUniqueReferenceId(String referenceId) {
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
            throw new UpdateAccessPassException("No AccessPass found with referenceId=" + referenceId);

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

    public RapidPass grant(String referenceId) throws UpdateAccessPassException {
        log.debug("APPROVING refId {}", referenceId);
        RapidPass rapidPass = this.updateStatus(referenceId, AccessPassStatus.APPROVED, null);

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
    private RapidPass updateStatus(String referenceId, AccessPassStatus status, String reason) throws RegistryService.UpdateAccessPassException {
        List<AccessPass> accessPassesRetrieved = accessPassRepository.findAllByReferenceIDOrderByValidToDesc(referenceId);

        if (accessPassesRetrieved.isEmpty()) {
            throw new UpdateAccessPassException("Cannot find the access pass to update (refId=" + referenceId + ").");
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

        if (reason != null && status == AccessPassStatus.DECLINED) {
            accessPass.setUpdates(reason);
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
     * @param requestResult   object containing update status
     * @return updated rapid pass
     * @throws UpdateAccessPassException on error updating access pass
     */
    public RapidPass updateAccessPass(String referenceId, RequestResult requestResult) throws UpdateAccessPassException {
        final RapidPass updatedRapidPass;
        final AccessPassStatus status = requestResult.getResult();
        switch (status) {
            case APPROVED:
                updatedRapidPass = grant(referenceId);
                break;
            case DECLINED:
                updatedRapidPass = decline(referenceId, requestResult.getReason());
                break;
            case SUSPENDED:
                updatedRapidPass = revoke(referenceId);
                break;
            default:
                throw new IllegalArgumentException("Request Status not yet supported!");
        }

        log.debug("Sending out notifs for {}", referenceId);
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

    public RapidPass decline(String referenceId, String reason) throws RegistryService.UpdateAccessPassException {
        return this.updateStatus(referenceId, AccessPassStatus.DECLINED, reason);
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
    public List<String> batchUpload(List<RapidPassCSVdata> approvedRapidPasses) throws RegistryService.UpdateAccessPassException {

        log.info("Process Batch Approving of AccessPass");
        List<String> passes = new ArrayList<String>();

        // Validation
        NewAccessPassRequestValidator newAccessPassRequestValidator = new NewAccessPassRequestValidator(this.lookupTableService, this.accessPassRepository);

        RapidPass pass;
        int counter = 1;
        for (RapidPassCSVdata rapidPassRequest : approvedRapidPasses) {
            try {
                RapidPassRequest request = RapidPassRequest.buildFrom(rapidPassRequest);

                StandardDataBindingValidation validation = new StandardDataBindingValidation(newAccessPassRequestValidator);
                validation.validate(request);

                pass = this.newRequestPass(request);

                if (pass != null) {

                    RequestResult requestToUpdateStatus = RequestResult.builder()
                            .reason(null)
                            .referenceId(pass.getReferenceId())
                            .result(AccessPassStatus.APPROVED)
                            .build();

                    updateAccessPass(pass.getReferenceId(), requestToUpdateStatus);

                    passes.add("Record " + counter++ + ": Success. ");
                }
            } catch ( Exception e ) {
                passes.add("Record " + counter++ + ": Failed. " + e.getMessage());
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
