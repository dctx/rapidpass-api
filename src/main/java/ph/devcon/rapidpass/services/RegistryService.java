package ph.devcon.rapidpass.services;

import com.google.zxing.WriterException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ph.devcon.rapidpass.entities.AccessPass;
import ph.devcon.rapidpass.entities.ControlCode;
import ph.devcon.rapidpass.entities.Registrant;
import ph.devcon.rapidpass.entities.ScannerDevice;
import ph.devcon.rapidpass.enums.AccessPassStatus;
import ph.devcon.rapidpass.enums.PassType;
import ph.devcon.rapidpass.enums.RecordSource;
import ph.devcon.rapidpass.kafka.KafkaProducer;
import ph.devcon.rapidpass.messaging.models.Address;
import ph.devcon.rapidpass.messaging.models.RapidPassMessage;
import ph.devcon.rapidpass.models.*;
import ph.devcon.rapidpass.repositories.AccessPassRepository;
import ph.devcon.rapidpass.repositories.RegistrantRepository;
import ph.devcon.rapidpass.repositories.RegistryRepository;
import ph.devcon.rapidpass.repositories.ScannerDeviceRepository;
import ph.devcon.rapidpass.services.controlcode.ControlCodeService;
import ph.devcon.rapidpass.utilities.StringFormatter;
import ph.devcon.rapidpass.validators.StandardDataBindingValidation;
import ph.devcon.rapidpass.validators.entities.NewAccessPassRequestValidator;
import ph.devcon.rapidpass.validators.entities.NewSingleAccessPassRequestValidator;

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

import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.contains;

@Component
@Slf4j
@RequiredArgsConstructor
public class RegistryService {

    public static final int DEFAULT_VALIDITY_DAYS = 7;

    @Value("${topic.name}")
    private String Topic;

    private final KafkaProducer producer;

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
     * @see <a href="https://gitlab.com/dctx/rapidpass/rapidpass-api/-/issues/64">documentation</a> on the flow.
     *
     * @throws IllegalArgumentException If the plate number is empty and the pass type is for a vehicle
     * @throws IllegalArgumentException Attempting to create a new access pass while an existing pending or approved pass exists
     * @param rapidPassRequest rapid passs request.
     * @return new rapid pass with PENDING status
     */
    @Transactional
    public RapidPass newRequestPass(RapidPassRequest rapidPassRequest) {
        // see
        OffsetDateTime now = OffsetDateTime.now();
        log.debug("New RapidPass Request: {}", rapidPassRequest);

        // normalize id, plate number and mobile number
        if (rapidPassRequest.getPlateNumber() != null) {
            rapidPassRequest.setPlateNumber(StringFormatter.normalizeAlphanumeric(rapidPassRequest.getPlateNumber()));
        }
        rapidPassRequest.setMobileNumber(StringFormatter.normalizeAlphanumeric(rapidPassRequest.getMobileNumber()));
        rapidPassRequest.setIdentifierNumber(StringFormatter.normalizeAlphanumeric(rapidPassRequest.getIdentifierNumber()));

        // Doesn't do id type checking (see https://gitlab.com/dctx/rapidpass/rapidpass-api/-/issues/236).
        NewSingleAccessPassRequestValidator newAccessPassRequestValidator = new NewSingleAccessPassRequestValidator(this.lookupTableService, this.accessPassRepository);
        StandardDataBindingValidation validation = new StandardDataBindingValidation(newAccessPassRequestValidator);
        validation.validate(rapidPassRequest);

        // check if registrant is already in the system
        Registrant registrant = registrantRepository.findByMobile(rapidPassRequest.getMobileNumber());
        if (registrant == null) {
            registrant = new Registrant();
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
        // just set the validity period to today until the request is approved
        accessPass.setValidFrom(now);
        accessPass.setValidTo(now);
        accessPass.setDateTimeCreated(now);
        accessPass.setDateTimeUpdated(now);
        accessPass.setRemarks(rapidPassRequest.getRemarks());
        accessPass.setSource(rapidPassRequest.getSource());
        accessPass.setStatus("PENDING");

        log.debug("Persisting Registrant: {}", registrant.toString());
        accessPass = accessPassRepository.saveAndFlush(accessPass);

        return RapidPass.buildFrom(accessPass);
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

    public RapidPassBulkData findAllApprovedSince(OffsetDateTime lastUpdatedOn, Pageable page)
    {
        Page<AccessPass> dataPage = accessPassRepository
                .findAllByStatusAndDateTimeUpdatedIsAfter(AccessPassStatus.APPROVED.name(), lastUpdatedOn, page);

        List<?> columnNames = RapidPassBulkData.getColumnNames();

        List<Object> columnValues =  dataPage.stream()
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

        return  rapidPassBulkData;
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

            // is it April 30 yet?
            OffsetDateTime now = OffsetDateTime.now();
            OffsetDateTime aprilThirty = OffsetDateTime.of(2020,4,30,23,59,
                    59,99999, ZoneOffset.ofHours(8));
            OffsetDateTime validUntil = now;
            if (OffsetDateTime.now().isAfter(aprilThirty)) {
                validUntil = now.plusDays(DEFAULT_VALIDITY_DAYS);
            } else {
                validUntil = aprilThirty;
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
     * @param referenceId reference id to update
     * @param rapidPassStatus   object containing update status
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

        log.debug("Sending out notifs for {}", referenceId);
        // push APPROVED/DENIED notifications.
        // TODO: someday let's do this asynchronously
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
        Instant start = Instant.now();
        for (RapidPassCSVdata r : approvedRapidPasses) {
            try {
                RapidPassRequest request = RapidPassRequest.buildFrom(r);
                request.setSource(RecordSource.BULK.toString());

                StandardDataBindingValidation validation = new StandardDataBindingValidation(newAccessPassRequestValidator);
                validation.validate(request);

                RapidPassMessage message = RapidPassMessage.newBuilder()
                        .setAporType(r.getAporType())
                        .setCompany(r.getCompany())
                        .setControlCode("")
                        .setDestination(Address.newBuilder()
                                .setStreet("")
                                .setCity("")
                                .setProvince("")
                                .build())
                        .setFirstName(r.getFirstName())
                        .setLastName(r.getLastName())
                        .setIdNumber(r.getIdType())
                        .setIdType(r.getIdentifierNumber())
                        .setMobile(r.getMobileNumber())
                        .setOrigin(Address.newBuilder()
                                .setStreet("")
                                .setCity("")
                                .setProvince("")
                                .build())
                        .setPassType(r.getPassType())
                        .setPlateNumber(r.getPlateNumber() == null ? "NA" : r.getPlateNumber())
                        .setPreApproved(true)
                        .setRemarks("")
                        .setSource(RecordSource.BULK.name())
                        .setStatus("APPROVED")
                        .build();

//                log.debug("Sending message no. {}, {}", counter, message);
                producer.sendMessage(request);
//                kafkaTemplate.send(this.Topic, r.getMobileNumber(), message);

//                pass = this.newRequestPass(request);
//                if (pass != null) {
//
//                    RapidPassStatus rapidPassStatus = RapidPassStatus.builder()
//                            .remarks(null)
//                            .status(AccessPassStatus.APPROVED)
//                            .build();
//                    updateAccessPass(pass.getReferenceId(), rapidPassStatus);

//                log.debug("Sent message no. {}, {}", counter, message);
                passes.add("Record " + counter++ + ": Success. ");
//                }
            } catch (Exception e) {
                log.warn("Failed Sending message no. {}, error: {}", counter, e.getMessage());
                passes.add("Record " + counter++ + ": Failed. " + e.getMessage());
            }
        }
        log.info("Execution time: {} seconds", Duration.between(start, Instant.now()).toMillis()/1000);
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
}
