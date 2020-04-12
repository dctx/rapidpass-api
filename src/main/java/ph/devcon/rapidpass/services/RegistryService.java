package ph.devcon.rapidpass.services;

import com.google.zxing.WriterException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ph.devcon.rapidpass.entities.*;
import ph.devcon.rapidpass.enums.AccessPassStatus;
import ph.devcon.rapidpass.enums.PassType;
import ph.devcon.rapidpass.enums.RecordSource;
import ph.devcon.rapidpass.kafka.RapidPassEventProducer;
import ph.devcon.rapidpass.kafka.RapidPassRequestProducer;
import ph.devcon.rapidpass.models.*;
import ph.devcon.rapidpass.repositories.*;
import ph.devcon.rapidpass.services.controlcode.ControlCodeService;
import ph.devcon.rapidpass.utilities.StringFormatter;
import ph.devcon.rapidpass.validators.StandardDataBindingValidation;
import ph.devcon.rapidpass.validators.entities.BatchAccessPassRequestValidator;
import ph.devcon.rapidpass.validators.entities.NewSingleAccessPassRequestValidator;

import javax.validation.constraints.NotEmpty;
import java.io.IOException;
import java.text.ParseException;
import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.contains;

@Component
@Slf4j
@RequiredArgsConstructor
public class RegistryService {

    public static final int DEFAULT_VALIDITY_DAYS = 7;
    public static final OffsetDateTime DEFAULT_EXPIRATION_DATE =
            OffsetDateTime.of(2020, 4, 30, 23,
                    59, 59, 999, ZoneOffset.ofHours(8));

    @Value("${kafka.enabled:false}")
    protected boolean isKafaEnabled;

    private final RapidPassRequestProducer requestProducer;
    private final RapidPassEventProducer eventProducer;

    private final AccessPassEventRepository accessPassEventRepository;
    private final RegistryRepository registryRepository;
    private final ControlCodeService controlCodeService;
    private final RegistrantRepository registrantRepository;
    private final LookupTableService lookupTableService;
    private final AccessPassRepository accessPassRepository;
    private final AccessPassNotifierService accessPassNotifierService;
    private final ScannerDeviceRepository scannerDeviceRepository;

    /**
     * Creates a new {@link RapidPass} with PENDING status.
     *
     * @param rapidPassRequest rapid passs request.
     * @return new rapid pass with PENDING status
     * @throws IllegalArgumentException If the plate number is empty and the pass type is for a vehicle
     * @throws IllegalArgumentException Attempting to create a new access pass while an existing pending or approved pass exists
     * @see <a href="https://gitlab.com/dctx/rapidpass/rapidpass-api/-/issues/64">documentation</a> on the flow.
     */
    @Transactional
    public RapidPass newRequestPass(RapidPassRequest rapidPassRequest) {

        log.debug("New RapidPass Request: {}", rapidPassRequest);

        // normalize id, plate number and mobile number
        normalizeIdMobileAndPlateNumber(rapidPassRequest);

        // Doesn't do id type checking (see https://gitlab.com/dctx/rapidpass/rapidpass-api/-/issues/236).
        NewSingleAccessPassRequestValidator newAccessPassRequestValidator = new NewSingleAccessPassRequestValidator(this.lookupTableService, this.accessPassRepository);
        StandardDataBindingValidation validation = new StandardDataBindingValidation(newAccessPassRequestValidator);
        validation.validate(rapidPassRequest);

        RapidPass rapidPass = persistAccessPass(rapidPassRequest, AccessPassStatus.PENDING);
        if (isKafaEnabled)
            eventProducer.sendMessage(rapidPass.getReferenceId(), rapidPass);

        return rapidPass;
    }

    private RapidPass persistAccessPass(RapidPassRequest rapidPassRequest, AccessPassStatus status) {

        OffsetDateTime now = OffsetDateTime.now();

        // check if registrant is already in the system
        @NotEmpty String mobileNumber = "0" + StringUtils.right(rapidPassRequest.getMobileNumber(), 10);
        Registrant registrant = registrantRepository.findByMobile(mobileNumber);
        if (registrant == null) {
            registrant = new Registrant();
            registrant.setDateTimeCreated(now);
        } else {
            // check for consistency
            if (!registrant.getFirstName().equals(rapidPassRequest.getFirstName()) ||
                    !registrant.getLastName().equals(rapidPassRequest.getLastName())) {
                // we will allow name change for now, just log the change
                log.warn("Review registrant name change:.\n- previous: {}.\n- new: {}",
                        registrant.toString(), rapidPassRequest.toString());
            }
        }

        registrant.setRegistrantType(1);
        registrant.setRegistrantName(rapidPassRequest.getName());
        registrant.setFirstName(rapidPassRequest.getFirstName());
        registrant.setMiddleName(rapidPassRequest.getMiddleName());
        registrant.setLastName(rapidPassRequest.getLastName());
        registrant.setSuffix(rapidPassRequest.getSuffix());
        registrant.setEmail(rapidPassRequest.getEmail());
        registrant.setMobile(mobileNumber);
        registrant.setReferenceIdType(rapidPassRequest.getIdType());
        registrant.setReferenceId(rapidPassRequest.getIdentifierNumber());
        registrant.setDateTimeUpdated(now);

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
        accessPass.setReferenceID(rapidPassRequest.getPassType().equals(PassType.INDIVIDUAL) ?
                registrant.getMobile() : rapidPassRequest.getPlateNumber());
        accessPass.setPassType(rapidPassRequest.getPassType().toString());
        accessPass.setAporType(rapidPassRequest.getAporType());
        accessPass.setIdType(rapidPassRequest.getIdType());
        accessPass.setIdentifierNumber(rapidPassRequest.getIdentifierNumber());
        accessPass.setPlateNumber(rapidPassRequest.getPlateNumber());
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
        accessPass.setDateTimeCreated(now);
        accessPass.setDateTimeUpdated(now);
        accessPass.setRemarks(rapidPassRequest.getRemarks());
        accessPass.setSource(rapidPassRequest.getSource());
        accessPass.setStatus(status.name());
        accessPass.setValidFrom(now);
        if (status == AccessPassStatus.PENDING) {
            // just set the validity period to today until the request is approved
            accessPass.setValidTo(now);
        } else if (status == AccessPassStatus.APPROVED) {
            accessPass.setValidTo(DEFAULT_EXPIRATION_DATE);
        }

        log.debug("Persisting Registrant: {}", registrant.toString());
        accessPass = accessPassRepository.saveAndFlush(accessPass);

        return RapidPass.buildFrom(accessPass);
    }

    private void normalizeIdMobileAndPlateNumber(RapidPassRequest rapidPassRequest) {
        if (rapidPassRequest.getPlateNumber() != null) {
            rapidPassRequest.setPlateNumber(StringFormatter.normalizeAlphanumeric(rapidPassRequest.getPlateNumber()));
        }
        rapidPassRequest.setMobileNumber(StringFormatter.normalizeAlphanumeric(rapidPassRequest.getMobileNumber()));
        rapidPassRequest.setIdentifierNumber(StringFormatter.normalizeAlphanumeric(rapidPassRequest.getIdentifierNumber()));
    }

    public RapidPassPageView findRapidPass(QueryFilter q) {

        Example<AccessPass> accessPassExample = Example.of(AccessPass.fromQueryFilter(q),
                ExampleMatcher.matchingAll()
                        .withIgnoreCase()
                        .withMatcher("name", contains())
                        .withMatcher("company", contains())
        );

        PageRequest pageView = PageRequest.of(0, QueryFilter.DEFAULT_PAGE_SIZE);
        if (null != q.getPageNo()) {
            int pageSize = (null != q.getMaxPageRows()) ? q.getMaxPageRows() : QueryFilter.DEFAULT_PAGE_SIZE;
            pageView = PageRequest.of(q.getPageNo(), pageSize);
        }

        Page<AccessPass> accessPassPages = accessPassRepository.findAll(accessPassExample, pageView);

        List<RapidPass> rapidPassList = accessPassPages
                .stream()
                .map(RapidPass::buildFrom)
                .collect(Collectors.toList());

        return RapidPassPageView.builder()
                .currentPage(q.getPageNo())
                .currentPageRows(accessPassPages.getNumberOfElements())
                .totalPages(accessPassPages.getTotalPages())
                .totalRows(accessPassPages.getTotalElements())
                .isFirstPage(accessPassPages.isFirst())
                .isLastPage(accessPassPages.isLast())
                .hasNext(accessPassPages.hasNext())
                .hasPrevious(accessPassPages.hasPrevious())
                .rapidPassList(rapidPassList)
                .build();
    }

    public Page<RapidPass> findAllApprovedOrSuspendedRapidPassAfter(OffsetDateTime lastUpdatedOn, Pageable page) {
        final Page<AccessPass> pagedAccessPasses = accessPassRepository.findAllApprovedAndSuspendedSince(lastUpdatedOn, page);
        Function<List<AccessPass>, List<RapidPass>> collectionTransform = accessPasses -> accessPasses.stream()
                .map(RapidPass::buildFrom)
                .collect(Collectors.toList());

        return PageableExecutionUtils.getPage(
                collectionTransform.apply(pagedAccessPasses.getContent()),
                page,
                pagedAccessPasses::getTotalElements);
    }

    public RapidPassBulkData findAllApprovedSince(OffsetDateTime lastUpdatedOn, Pageable page) {
        Page<AccessPass> dataPage = accessPassRepository
                .findAllByStatusAndDateTimeUpdatedIsAfter(AccessPassStatus.APPROVED.name(), lastUpdatedOn, page);

        List<?> columnNames = RapidPassBulkData.getColumnNames();

        List<Object> columnValues = dataPage.stream()
                // Since these are all approved, we may bind the control codes for them.
                .map(controlCodeService::bindControlCodeForAccessPass)
                .map(RapidPassBulkData::values)
                .collect(Collectors.toList());

        List<Object> dataRecords = new ArrayList<>();
        dataRecords.add(columnNames);
        dataRecords.addAll(columnValues);

        RapidPassBulkData rapidPassBulkData = RapidPassBulkData.builder()
                .currentPage(dataPage.getNumber())
                .currentPageRows(dataPage.getNumberOfElements())
                .totalPages(dataPage.getTotalPages())
                .totalRows(dataPage.getTotalElements())
                .data(dataRecords)
                .build();

        return rapidPassBulkData;
    }

    public Iterable<ControlCode> getControlCodes() {
        return accessPassRepository
                .findAll()
                .stream()
                .map(controlCodeService::bindControlCodeForAccessPass)
                .map(ControlCode::buildFrom)
                .collect(Collectors.toList());
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

        AccessPass accessPass = accessPasses.get(0);

        // Only bind the QR code to the access pass IF the access pass is approved.
        if (AccessPassStatus.APPROVED.toString().equals(accessPass.getStatus()))
            accessPass = controlCodeService.bindControlCodeForAccessPass(accessPass);

        return accessPass;
    }

    /**
     * After updating the target {@link AccessPass}, this returns a {@link RapidPass} whose status is granted.
     *
     * @param referenceId      The reference id of the {@link AccessPass} you are retrieving.
     * @param rapidPassRequest The data update for the rapid pass request
     * @return Data stored on the database
     */
    private RapidPass update(String referenceId, RapidPassRequest rapidPassRequest) throws UpdateAccessPassException {
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

    private RapidPass grant(String referenceId) throws UpdateAccessPassException {
        log.debug("APPROVING refId {}", referenceId);
        RapidPass rapidPass = this.updateStatus(referenceId, AccessPassStatus.APPROVED, null);

        List<AccessPass> accessPasses = accessPassRepository.findAllByReferenceIDOrderByValidToDesc(referenceId);
        if (accessPasses.size() > 0) {
            AccessPass accessPass = accessPasses.get(0);

            // is it April 30 yet?
            OffsetDateTime now = OffsetDateTime.now();
            OffsetDateTime validUntil = now;
            if (OffsetDateTime.now().isAfter(DEFAULT_EXPIRATION_DATE)) {
                validUntil = now.plusDays(DEFAULT_VALIDITY_DAYS);
            } else {
                validUntil = DEFAULT_EXPIRATION_DATE;
            }
            accessPass.setValidTo(validUntil);
            accessPass.setValidFrom(now);

            accessPass.setControlCode(controlCodeService.encode(accessPass.getId()));
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
    private RapidPass updateStatus(String referenceId, AccessPassStatus status, String reason) throws UpdateAccessPassException {
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

        if (reason != null && (status == AccessPassStatus.DECLINED || status == AccessPassStatus.SUSPENDED)) {
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
     * @param referenceId     reference id to update
     * @param rapidPassStatus object containing update status
     * @return updated rapid pass
     * @throws UpdateAccessPassException on error updating access pass
     */
    @Transactional
    public RapidPass updateAccessPass(String referenceId, RapidPassStatus rapidPassStatus) throws UpdateAccessPassException {
        final RapidPass updatedRapidPass;
        final AccessPassStatus status = rapidPassStatus.getStatus();
        switch (status) {
            case APPROVED:
                updatedRapidPass = grant(referenceId);
                break;
            case DECLINED:
                updatedRapidPass = decline(referenceId, rapidPassStatus.getRemarks());
                break;
            case SUSPENDED:
                updatedRapidPass = revoke(referenceId);
                break;
            default:
                throw new IllegalArgumentException("Request Status not yet supported!");
        }

        log.debug("Sending {} event for {}", status, referenceId);
        if (isKafaEnabled)
            eventProducer.sendMessage(referenceId, updatedRapidPass);

        log.debug("Sending {} SMS/Email notification for {}", status, referenceId);
        // TODO: someday let's do this asynchronously
        if (!isKafaEnabled) {
            accessPassRepository.findAllByReferenceIDOrderByValidToDesc(referenceId)
                    .stream()
                    .findFirst()
                    .map(controlCodeService::bindControlCodeForAccessPass)
                    .ifPresent(accessPass -> {
                        try {
                            accessPassNotifierService.pushApprovalDeniedNotifs(accessPass);
                        } catch (ParseException | IOException | WriterException e) {
                            log.error("Error sending out notifications for " + accessPass, e);
                        }
                    });
        }


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

        RapidPass rapidPass = RapidPass.buildFrom(accessPass);
        if (isKafaEnabled)
            eventProducer.sendMessage(rapidPass.getReferenceId(), rapidPass);
        return rapidPass;
    }


    /**
     * Returns a list of rapid passes that were requested for granting or approval.
     *
     * @param approvedRapidPasses Iterable<RapidPass> of Approved passes application
     * @return a list of generated rapid passes, whose status are all approved.
     */
    public List<String> batchUpload(List<RapidPassCSVdata> approvedRapidPasses) throws RegistryService.UpdateAccessPassException {

        log.info("Process Batch Approving of AccessPass");
        List<String> passes = new ArrayList<>();

        // Validation
        BatchAccessPassRequestValidator batchAccessPassRequestValidator = new BatchAccessPassRequestValidator(this.lookupTableService, this.accessPassRepository);

        RapidPass pass;
        int counter = 1;
        Instant start = Instant.now();
        for (RapidPassCSVdata r : approvedRapidPasses) {
            try {
                RapidPassRequest request = RapidPassRequest.buildFrom(r);
                request.setSource(RecordSource.BULK.toString());

                normalizeIdMobileAndPlateNumber(request);
                StandardDataBindingValidation validation = new StandardDataBindingValidation(batchAccessPassRequestValidator);
                validation.validate(request);

                if (isKafaEnabled) {
                    String key = request.getPassType() == PassType.INDIVIDUAL ? request.getMobileNumber() :
                            request.getPlateNumber();
                    requestProducer.sendMessage(key, request);

                    passes.add("Record " + counter++ + ": Processed. ");
                } else {

                    String referenceId = request.getPassType().equals(PassType.INDIVIDUAL) ?
                            request.getMobileNumber() : request.getPlateNumber();
                    List<String> statuses = Stream.of("APPROVED", "PENDING").collect(Collectors.toList());
                    OffsetDateTime now = OffsetDateTime.now();
                    List<AccessPass> accessPasses = accessPassRepository
                            .findAllByReferenceIDAndPassTypeAndValidToAfterAndStatusIn(referenceId,
                                    request.getPassType().name(), now, statuses);

                    if (accessPasses.isEmpty()) {
                        // no existing approved or pending requests, add a pre-approved request
                        pass = persistAccessPass(request, AccessPassStatus.APPROVED);

                        if (pass == null) {
                            throw new Exception("Unable to create new Access Pass.");
                        }
                    } else  if (accessPasses.stream().filter(a -> "APPROVED".equalsIgnoreCase(a.getStatus())).count() > 0) {
                        // there is already an approved and valid access pass with the same reference id and pass type
                        throw new Exception("Duplicate (Approved) Access Pass found.");
                    } else {
                        // there are at least 1 pending request, this should not happen
                        // only 1 pending request per pass type and reference id should be in the system
                        if (accessPasses.size() > 1) {
                            log.warn("Multiple Requests found for the pass type: {} with reference id: {}",
                                    request.getPassType().name(), referenceId);
                        }
                        // let's approve all pending requests with the same reference id and pass type
                        for (AccessPass accessPass: accessPasses) {
                            accessPass.setValidFrom(now);
                            if (now.isAfter(DEFAULT_EXPIRATION_DATE)) {
                                accessPass.setValidTo(now.plusDays(DEFAULT_VALIDITY_DAYS));
                            } else {
                                accessPass.setValidTo(DEFAULT_EXPIRATION_DATE);
                            }
                            accessPass.setControlCode(controlCodeService.encode(accessPass.getId()));
                            accessPassRepository.saveAndFlush(accessPass);
                        }
                    }
                    passes.add("Record " + counter++ + ": Success. ");

                }
            } catch (Exception e) {
                log.warn("Failed Sending message no. {}, error: {}", counter, e.getMessage());
                passes.add("Record " + counter++ + ": Failed. " + e.getMessage());
            }
        }
        log.info("Execution time: {} seconds", Duration.between(start, Instant.now()).toMillis() / 1000);
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
                    .map(MobileDevice::buildFrom)
                    .collect(Collectors.toList());
        } else {
            return scannerDeviceRepository.findAll()
                    .stream()
                    .map(MobileDevice::buildFrom)
                    .collect(Collectors.toList());
        }
    }

    public ScannerDevice registerScannerDevice(MobileDevice request) {
        ScannerDevice device = scannerDeviceRepository.findByUniqueDeviceId(request.getImei());
        if (device == null) {
            device = new ScannerDevice();
        }

        device.setUniqueDeviceId(request.getImei());
        device.setBrand(request.getBrand());
        device.setMobileNumber(request.getMobileNumber());
        device.setModel(request.getModel());
        device.setStatus(request.getStatus());

        return scannerDeviceRepository.saveAndFlush(device);
    }

    public RapidPassEventLog getAccessPassEvent(Integer eventId, Pageable pageable) {
        Page<AccessPassEvent> accessPassEvents = accessPassEventRepository.findAllByIdIsGreaterThanEqual(eventId, pageable);
        if (accessPassEvents == null || accessPassEvents.isEmpty()) {
            return null;
        } else {
            return RapidPassEventLog.buildFrom(accessPassEvents, controlCodeService);
        }
    }
}
