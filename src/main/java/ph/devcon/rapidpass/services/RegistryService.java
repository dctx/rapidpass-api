/*
 * Copyright (c) 2020.  DevConnect Philippines, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and limitations under the License.
 */

package ph.devcon.rapidpass.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.representations.AccessToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ph.devcon.rapidpass.api.models.ControlCodeResponse;
import ph.devcon.rapidpass.api.models.RapidPassUpdateRequest;
import ph.devcon.rapidpass.entities.*;
import ph.devcon.rapidpass.enums.AccessPassStatus;
import ph.devcon.rapidpass.enums.PassType;
import ph.devcon.rapidpass.enums.RecordSource;
import ph.devcon.rapidpass.enums.RegistrarUserSource;
import ph.devcon.rapidpass.kafka.RapidPassEventProducer;
import ph.devcon.rapidpass.kafka.RapidPassRequestProducer;
import ph.devcon.rapidpass.models.*;
import ph.devcon.rapidpass.repositories.*;
import ph.devcon.rapidpass.services.controlcode.ControlCodeService;
import ph.devcon.rapidpass.utilities.KeycloakUtils;
import ph.devcon.rapidpass.utilities.StringFormatter;
import ph.devcon.rapidpass.utilities.validators.ReadableValidationException;
import ph.devcon.rapidpass.utilities.validators.StandardDataBindingValidation;
import ph.devcon.rapidpass.utilities.validators.entities.accesspass.BatchAccessPassRequestValidator;
import ph.devcon.rapidpass.utilities.validators.entities.accesspass.NewSingleAccessPassRequestValidator;
import ph.devcon.rapidpass.utilities.validators.entities.agencyuser.BatchAgencyUserRequestValidator;

import javax.validation.constraints.NotEmpty;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.spec.InvalidKeySpecException;
import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@Slf4j
@RequiredArgsConstructor
public class RegistryService {

    public static final int DEFAULT_VALIDITY_DAYS = 7;

    @Value("${rapidpass.expiration.year}")
    protected Integer expirationYear;

    @Value("${rapidpass.expiration.month}")
    protected Integer expirationMonth;

    @Value("${rapidpass.expiration.day}")
    protected Integer expirationDay;

    @Value("${kafka.enabled:false}")
    protected boolean isKafaEnabled;

    private final RapidPassRequestProducer requestProducer;
    private final RapidPassEventProducer eventProducer;

    private final AccessPassEventRepository accessPassEventRepository;
    private final LookupService lookupService;
    private final AccessPassNotifierService accessPassNotifierService;
    private final RegistrarRepository registrarRepository;

    private final RegistryRepository registryRepository;
    private final ControlCodeService controlCodeService;
    private final RegistrantRepository registrantRepository;
    private final AccessPassRepository accessPassRepository;
    private final ScannerDeviceRepository scannerDeviceRepository;
    private final RegistrarUserRepository registrarUserRepository;

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
        NewSingleAccessPassRequestValidator newAccessPassRequestValidator = new NewSingleAccessPassRequestValidator(this.lookupService, this.accessPassRepository);
        StandardDataBindingValidation validation = new StandardDataBindingValidation(newAccessPassRequestValidator);
        validation.validate(rapidPassRequest);

        RapidPass rapidPass = persistAccessPass(rapidPassRequest, AccessPassStatus.PENDING);
        if (isKafaEnabled)
            eventProducer.sendMessage(rapidPass.getReferenceId(), rapidPass);

        return rapidPass;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
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
        registrant = registrantRepository.saveAndFlush(registrant);

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

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentLoggedInUser = null;

        if (authentication != null && authentication.getPrincipal() instanceof HashMap) {
            HashMap<String, Object> principal = (HashMap<String, Object>) authentication.getPrincipal();
            currentLoggedInUser = principal.get("sub").toString();
        }

        accessPass.setIssuedBy(currentLoggedInUser);
        if (status == AccessPassStatus.PENDING) {
            // just set the validity period to today until the request is approved
            accessPass.setValidTo(now);
        } else if (status == AccessPassStatus.APPROVED) {
            accessPass.setValidTo(getExpirationDate());
        }

        log.debug("Persisting Registrant: {}", registrant.toString());
        accessPass = accessPassRepository.saveAndFlush(accessPass);

        String controlCode = this.controlCodeService.encode(accessPass.getId());
        accessPass.setControlCode(controlCode);
        accessPassRepository.saveAndFlush(accessPass);

        return RapidPass.buildFrom(accessPass);
    }

    public boolean updateNotified(String referenceId) {
        List<AccessPass> AccessPasses = accessPassRepository.findAllByReferenceIDOrderByValidToDesc(referenceId);
        Optional<AccessPass> firstAccessPass = AccessPasses.stream().findFirst();
        AccessPass accessPass = firstAccessPass.orElse(null);
        if (accessPass == null) return false;
        switch (AccessPassStatus.valueOf(accessPass.getStatus())) {
            case APPROVED:
                accessPass.setDateTimeUpdated(OffsetDateTime.now());
                accessPass.setNotified(false);
                accessPassRepository.saveAndFlush(accessPass);
                log.info("Resending Text Message for {}", referenceId);
                return true;
        }
        return false;
    }

    /**
     * Please use normalization rules instead, for composable rules.
     *
     * @param rapidPassRequest The rapidpass to normalize.
     * @deprecated
     */
    private void normalizeIdMobileAndPlateNumber(RapidPassRequest rapidPassRequest) {
        if (rapidPassRequest.getPlateNumber() != null) {
            rapidPassRequest.setPlateNumber(StringFormatter.normalizeAlphanumeric(rapidPassRequest.getPlateNumber()));
        }
        rapidPassRequest.setMobileNumber(StringFormatter.normalizeAlphanumeric(rapidPassRequest.getMobileNumber()));
        rapidPassRequest.setIdentifierNumber(StringFormatter.normalizeAlphanumeric(rapidPassRequest.getIdentifierNumber()));
    }

    public RapidPassPageView findRapidPass(QueryFilter q, List<String> secAporTypes) {

        PageRequest pageView = PageRequest.of(0, QueryFilter.DEFAULT_PAGE_SIZE);
        if (null != q.getPageNo()) {
            int pageSize = (null != q.getMaxPageRows()) ? q.getMaxPageRows() : QueryFilter.DEFAULT_PAGE_SIZE;
            pageView = PageRequest.of(q.getPageNo(), pageSize, Sort.by("validTo").descending());
        }

        String[] aporTypes = StringUtils.split(q.getAporType(), ",");
        List<String> aporList = null;
        if (aporTypes != null)
            aporList = Arrays.asList(aporTypes);

        Specification<AccessPass> bySecAporTypes = null;
        bySecAporTypes = AccessPassSpecifications.byAporTypes(secAporTypes);
//        try {
//            // impose limit by apor type when logged in
//            Principal p = principal.orElse(null);
//            if (p instanceof KeycloakPrincipal) {
//                Identity identity = new Identity(((KeycloakPrincipal) p).getKeycloakSecurityContext());
//                final List<String> secAporTypes = Arrays.asList(identity.getOtherClains()
//                        .get("aportypes").toString()
//                        .split(","));
//                log.debug("limiting apor types to {}", secAporTypes);
//                bySecAporTypes = AccessPassSpecifications.byAporTypes(secAporTypes);
//            }
//        } catch (Exception e) {
//            bySecAporTypes = AccessPassSpecifications.byAporTypes(null);
//            log.warn("accessing rapid passes unsecured! ", e);
//        }

        Specification<AccessPass> byAporTypes = AccessPassSpecifications.byAporTypes(aporList);
        Specification<AccessPass> byPassType = AccessPassSpecifications.byPassType(q.getPassType());
        Page<AccessPass> accessPassPages = accessPassRepository.findAll(byAporTypes.and(byPassType)
                        .and(AccessPassSpecifications.byCompany(q.getCompany()))
                        .and(AccessPassSpecifications.bySearch(q.getSearch()))
                        .and(AccessPassSpecifications.byName(q.getName()))
                        .and(AccessPassSpecifications.byPlateNumber(q.getPlateNumber()))
                        .and(AccessPassSpecifications.byReferenceId(q.getReferenceId()))
                        .and(AccessPassSpecifications.bySource(q.getSource() != null ? q.getSource().name() : null))
                        .and(AccessPassSpecifications.byNotified(q.getNotifiedState()))
                        .and(AccessPassSpecifications.byStatus(q.getStatus()))
                        .and(bySecAporTypes)
                , pageView);

        log.debug("got {} rows!", accessPassPages.getTotalElements());
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

        if (accessPass.getControlCode() == null) {
            accessPass = controlCodeService.bindControlCodeForAccessPass(accessPass);
            accessPassRepository.saveAndFlush(accessPass);
        }

        return accessPass;
    }

    /**
     * Updates properties of an access pass that is not related to its status.
     * <p>
     * For status related updates, please see the other registry service methods.
     *
     * @param rapidPassUpdateRequest The data update for the rapid pass request
     * @return Data stored on the database
     * @see #approve(String)
     * @see #suspend(String, String)
     * @see #decline(String, String)
     */
    private AccessPass update(AccessPass accessPass, RapidPassUpdateRequest rapidPassUpdateRequest) throws UpdateAccessPassException {

        if (!StringUtils.isEmpty(rapidPassUpdateRequest.getRemarks()))
            accessPass.setUpdates(rapidPassUpdateRequest.getRemarks());

        if (!StringUtils.isEmpty(rapidPassUpdateRequest.getReasonForApplication()))
            accessPass.setRemarks(rapidPassUpdateRequest.getReasonForApplication());

        if (rapidPassUpdateRequest.getAporType() != null)
            accessPass.setAporType(rapidPassUpdateRequest.getAporType().name());
        if (!StringUtils.isEmpty(rapidPassUpdateRequest.getCompany()))
            accessPass.setCompany(rapidPassUpdateRequest.getCompany());
        if (!StringUtils.isEmpty(rapidPassUpdateRequest.getDestCity()))
            accessPass.setDestinationCity(rapidPassUpdateRequest.getDestCity());
        if (!StringUtils.isEmpty(rapidPassUpdateRequest.getDestName()))
            accessPass.setDestinationName(rapidPassUpdateRequest.getDestName());
        if (!StringUtils.isEmpty(rapidPassUpdateRequest.getDestStreet()))
            accessPass.setDestinationStreet(rapidPassUpdateRequest.getDestStreet());
        if (!StringUtils.isEmpty(rapidPassUpdateRequest.getOriginName()))
            accessPass.setOriginName(rapidPassUpdateRequest.getOriginName());
        if (!StringUtils.isEmpty(rapidPassUpdateRequest.getOriginCity()))
            accessPass.setOriginCity(rapidPassUpdateRequest.getOriginCity());
        if (!StringUtils.isEmpty(rapidPassUpdateRequest.getOriginStreet()))
            accessPass.setOriginStreet(rapidPassUpdateRequest.getOriginStreet());

        if (!StringUtils.isEmpty(rapidPassUpdateRequest.getIdentifierNumber()))
            accessPass.setIdentifierNumber(rapidPassUpdateRequest.getIdentifierNumber());
        if (!StringUtils.isEmpty(rapidPassUpdateRequest.getIdType()))
            accessPass.setIdType(rapidPassUpdateRequest.getIdType());
        if (rapidPassUpdateRequest.getPassType() != null)
            accessPass.setPassType(rapidPassUpdateRequest.getPassType().toString());

        if (!StringUtils.isEmpty(rapidPassUpdateRequest.getName())) {
            accessPass.setName(rapidPassUpdateRequest.getName());

            // Update registrant only if it is found
            Registrant registrantId = accessPass.getRegistrantId();
            if (registrantId != null) {
                registrantId.setRegistrantName(rapidPassUpdateRequest.getName());
                registrantRepository.saveAndFlush(registrantId);
            }
        }

        if (!StringUtils.isEmpty(rapidPassUpdateRequest.getValidUntil()))
            accessPass.setValidTo(OffsetDateTime.parse(rapidPassUpdateRequest.getValidUntil()));
        if (!StringUtils.isEmpty(rapidPassUpdateRequest.getValidFrom()))
            accessPass.setValidFrom(OffsetDateTime.parse(rapidPassUpdateRequest.getValidFrom()));

        return accessPassRepository.saveAndFlush(accessPass);
    }

    private AccessPass approve(String referenceId) throws UpdateAccessPassException {
        log.debug("APPROVING refId {}", referenceId);

        AccessPass accessPass = this.updateStatus(referenceId, AccessPassStatus.APPROVED, null);

        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime validUntil = now;

        OffsetDateTime expirationDate = getExpirationDate();
        if (OffsetDateTime.now().isAfter(expirationDate)) {
            validUntil = now.plusDays(DEFAULT_VALIDITY_DAYS);
        } else {
            validUntil = expirationDate;
        }
        accessPass.setValidTo(validUntil);
        accessPass.setValidFrom(now);

        return accessPass;

        // Generate control code using the unique ID specified by the database.
    }


    /**
     * After updating the target {@link AccessPass}, this returns a {@link RapidPass} whose status is granted.
     *
     * @param referenceId The reference id of the {@link AccessPass} you are retrieving.
     * @param status      The status to apply
     * @return Data stored on the database
     * @throws UpdateAccessPassException If the pass could not be found, or if it is not pending.
     */
    private AccessPass updateStatus(String referenceId, AccessPassStatus status, String reason) throws UpdateAccessPassException {
        List<AccessPass> accessPassesRetrieved = accessPassRepository.findAllByReferenceIDOrderByValidToDesc(referenceId);

        if (accessPassesRetrieved.isEmpty()) {
            throw new UpdateAccessPassException("Cannot find the access pass to update (refId=" + referenceId + ").");
        }

        AccessPass accessPass = accessPassesRetrieved.get(0);

        if (accessPassesRetrieved.size() > 1) {
            log.warn("Found " + accessPassesRetrieved.size() + " AccessPasses for referenceId=" + referenceId + ".");
        }

        if (StringUtils.isEmpty(accessPass.getControlCode())) {
            accessPass.setControlCode(controlCodeService.encode(accessPass.getId()));
        }

        String currentStatus = accessPass.getStatus();

        boolean isPending = AccessPassStatus.PENDING.toString().equals(currentStatus);

        if (!isPending) {
            throw new RegistryService.UpdateAccessPassException("An access pass can only be updated if it is pending. Afterwards, it can only be revoked.");
        }

        if (reason != null && (status == AccessPassStatus.DECLINED || status == AccessPassStatus.SUSPENDED)) {
            accessPass.setUpdates(reason);
        }

        if (status == AccessPassStatus.SUSPENDED) {
            // Change validity period to reach only up to now.
            accessPass.setValidTo(OffsetDateTime.now());
        }

        accessPass.setStatus(status.toString());

        // TODO: We need to verify that only the authorized people to modify this pass are allowed.
        // E.g. approvers, or the owner of this pass. People should not be able to re-associate an existing pass from one registrant to another.
        // accessPass.setRegistrantId();

        accessPass.setDateTimeUpdated(OffsetDateTime.now());

        AccessPass savedAccessPass = accessPassRepository.saveAndFlush(accessPass);
        return savedAccessPass;
    }

    /**
     * Updates a rapid pass.
     *
     * @param referenceId            reference id to update
     * @param rapidPassUpdateRequest object containing update status
     * @return updated rapid pass
     * @throws UpdateAccessPassException on error updating access pass
     */
    @Transactional
    public RapidPass updateAccessPass(String referenceId, RapidPassUpdateRequest rapidPassUpdateRequest) throws UpdateAccessPassException {
        AccessPass accessPass = null;
        if (rapidPassUpdateRequest.getStatus() != null) {
            final AccessPassStatus status = AccessPassStatus.valueOf(rapidPassUpdateRequest.getStatus().name());
            switch (status) {
                case APPROVED:
                    accessPass = approve(referenceId);
                    break;
                case DECLINED:
                    accessPass = decline(referenceId, rapidPassUpdateRequest.getRemarks());
                    break;
                case SUSPENDED:
                    accessPass = suspend(referenceId, rapidPassUpdateRequest.getRemarks());
                    break;
                default:
                    accessPass = findByNonUniqueReferenceId(referenceId);
            }
        } else {
            accessPass = findByNonUniqueReferenceId(referenceId);
        }

        boolean shouldUpdateEmail = StringUtils.isNotEmpty(rapidPassUpdateRequest.getEmail());

        if (shouldUpdateEmail) {
            Registrant registrantId = accessPass.getRegistrantId();
            String oldEmail = registrantId.getEmail();
            String newEmail = rapidPassUpdateRequest.getEmail();

            log.info(String.format("Updating rapid pass email from %s to %s.", oldEmail, newEmail));
            registrantId.setEmail(rapidPassUpdateRequest.getEmail());
        }

        accessPass = update(accessPass, rapidPassUpdateRequest);

        log.debug("Sending update event for {}", referenceId);

        final RapidPass rapidPass = RapidPass.buildFrom(accessPass);

        if (isKafaEnabled)
            eventProducer.sendMessage(referenceId, rapidPass);

        return rapidPass;
    }

    public AccessPass decline(String referenceId, String reason) throws RegistryService.UpdateAccessPassException {
        return this.updateStatus(referenceId, AccessPassStatus.DECLINED, reason);
    }

    /**
     * After updating the target {@link AccessPass}, this returns a {@link RapidPass} whose status is revoked.
     *
     * @param referenceId The reference id of the {@link AccessPass} you are retrieving.
     * @return Data stored on the database
     */
    public AccessPass suspend(String referenceId, String reason) {
        List<AccessPass> allAccessPasses = accessPassRepository.findAllByReferenceIDOrderByValidToDesc(referenceId);

        Optional<AccessPass> firstAccessPass = allAccessPasses.stream()
                .filter(ap -> AccessPassStatus.APPROVED.toString().equals(ap.getStatus()))
                .findFirst();

        AccessPass accessPass = firstAccessPass.orElse(null);
        if (accessPass == null) {
            log.warn("Attempting to suspend a non-approved access pass. Failing. You are only allowed to suspend an approved pass.");
            return null;
        }

        accessPass.setStatus(AccessPassStatus.SUSPENDED.toString());
        accessPass.setUpdates(reason);
        accessPassRepository.saveAndFlush(accessPass);

        RapidPass rapidPass = RapidPass.buildFrom(accessPass);

        if (isKafaEnabled)
            eventProducer.sendMessage(rapidPass.getReferenceId(), rapidPass);

        return accessPass;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public String processBatchRapidPassRequest(RapidPassRequest request, BatchAccessPassRequestValidator validator, int counter, String currentLoggedInUser) {

        try {

            request.setSource(RecordSource.BULK.toString());

            StandardDataBindingValidation validation = new StandardDataBindingValidation(validator);
            validation.validate(request);

            if (isKafaEnabled) {
                String key = request.getPassType() == PassType.INDIVIDUAL ? request.getMobileNumber() :
                        request.getPlateNumber();
                requestProducer.sendMessage(key, request);

                return "Record " + counter + ": Processed. ";
            } else {

                String referenceId = request.getPassType().equals(PassType.INDIVIDUAL) ?
                        request.getMobileNumber() : request.getPlateNumber();
                List<String> statuses = Stream.of("APPROVED", "PENDING").collect(Collectors.toList());

                OffsetDateTime now = OffsetDateTime.now();
                List<AccessPass> accessPasses = accessPassRepository
                        .findAllByReferenceIDAndPassTypeAndStatusInOrderByValidToDesc(referenceId, PassType.INDIVIDUAL.name(), statuses);

                // there are at least 1 pending request, this should not happen
                // only 1 pending request per pass type and reference id should be in the system
                if (accessPasses.size() > 1) {
                    log.warn("Multiple Requests found for the pass type: {} with reference id: {}",
                            request.getPassType().name(), referenceId);
                }

                Optional<AccessPass> potentialLatestAccessPass = accessPasses.stream().filter(a -> "APPROVED".equalsIgnoreCase(a.getStatus())).findAny();
                // If the latest approved access pass exists, remove it from the initial list.


                boolean noPendingOrApprovedAccessPasses = accessPasses.isEmpty();

                boolean hasApprovedAccessPass = potentialLatestAccessPass.isPresent();

                if (noPendingOrApprovedAccessPasses) {
                    // no existing approved or pending requests, add a pre-approved request
                    RapidPass pass = persistAccessPass(request, AccessPassStatus.APPROVED);

                    if (pass == null) {
                        throw new ReadableValidationException("Unable to create new Access Pass.");
                    }

                    return "Record " + counter + ": Success. ";
                } else if (hasApprovedAccessPass) {

                    AccessPass latestApprovedAccessPass = potentialLatestAccessPass.get();

                    potentialLatestAccessPass.ifPresent(accessPasses::remove);

                    boolean isLatestApprovedAccessPassExpired = latestApprovedAccessPass.getValidTo().isBefore(OffsetDateTime.now());

                    if (isLatestApprovedAccessPassExpired) {
                        // Found an existing access pass, but its already expired. So we'll extend its validity.
                        this.updateAccessPassValidityToDefault(latestApprovedAccessPass);
                        return "Record " + counter + ": Extended the validity of the Access Pass.";
                    } else {
                        return "Record " + counter + ": No change. An existing approved Access Pass was found.";
                    }

                } else {

                    AccessPass latestPendingAccessPass = accessPasses.remove(0);


                    // let's approve all pending requests with the same reference id and pass type
                    for (AccessPass accessPass : accessPasses) {

                        /*
                         *
                         * The top most is set to be approved with the target expiration date, while the
                         * remaining duplicates are saved as APPROVED and their <code>valid_to</code> is set
                         * to now. Their control codes are also updated.
                         */

                        accessPass.setValidTo(now);
                        accessPass.setDateTimeUpdated(now);
                        accessPass.setStatus(AccessPassStatus.APPROVED.name());
                        accessPass.setIssuedBy(currentLoggedInUser);

                        accessPass.setSource("BULK_OVERRIDE_ONLINE");
                        accessPass.setControlCode(controlCodeService.encode(accessPass.getId()));
                        accessPassRepository.saveAndFlush(accessPass);
                    }


                    latestPendingAccessPass.setValidFrom(now);
                    latestPendingAccessPass.setValidTo(getExpirationDate());

                    latestPendingAccessPass.setSource("BULK");
                    latestPendingAccessPass.setStatus(AccessPassStatus.APPROVED.name());

                    latestPendingAccessPass.setIssuedBy(currentLoggedInUser);
                    latestPendingAccessPass.setDateTimeUpdated(OffsetDateTime.now());
                    latestPendingAccessPass.setControlCode(controlCodeService.encode(latestPendingAccessPass.getId()));

                    accessPassRepository.saveAndFlush(latestPendingAccessPass);
                    return "Record " + counter + ": Success. ";
                }
            }

        } catch (ReadableValidationException e) {
            log.warn("Did not create/update access pass for record {}. error: {}", counter, e.getMessage());

            // https://gitlab.com/dctx/rapidpass/rapidpass-api/-/issues/444
            // Access passes that are declined should still be persisted. Reusing existing code.
            // pass = persistAccessPass(request, AccessPassStatus.PENDING);
            // decline(pass.getReferenceId(), e.getMessage());

            return "Record " + counter++ + ": was declined. " + e.getMessage();
        } catch (Exception e) {
            log.warn("Failed Sending message no. {}, error: {}", counter, e.getMessage());
            // noop
            return "Record " + counter++ + ": Failed. Internal server error.";
        }
    }

    /**
     * Returns a list of rapid passes that were requested for granting or approval.
     *
     * <p>Note that this method should not be performing any Email or SMS sending, because bulk uploads may
     * cause congestion on the API server. Instead, data should be inserted in the database, to be picked up by
     * a polling push notification service.</p>
     *
     * @param approvedRapidPasses Iterable<RapidPass> of Approved passes application
     * @return a list of generated rapid passes, whose status are all approved.
     */
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public List<String> batchUploadRapidPassRequest(List<RapidPassCSVdata> approvedRapidPasses) throws RegistryService.UpdateAccessPassException {

        log.info("Process Batch Approving of AccessPass");
        List<String> passes = new ArrayList<>();

        // Validation
        BatchAccessPassRequestValidator batchAccessPassRequestValidator = new BatchAccessPassRequestValidator(this.lookupService, this.accessPassRepository);

        String currentLoggedInUser = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof HashMap) {
            HashMap<String, Object> principal = (HashMap<String, Object>) authentication.getPrincipal();
            currentLoggedInUser = principal.get("sub").toString();
        }

        RapidPass pass;
        int counter = 1;
        Instant start = Instant.now();
        for (RapidPassCSVdata r : approvedRapidPasses) {
            try {
                RapidPassRequest request = RapidPassRequest.buildFrom(r);
                String result = processBatchRapidPassRequest(request, batchAccessPassRequestValidator, counter, currentLoggedInUser);
                passes.add(result);
                counter++;

            } catch (IllegalArgumentException e) {
                if (e.getMessage().matches("No enum constant")) {
                    log.warn("Illegal argument.", e);
                    passes.add("Record " + counter++ + ": Failed. Invalid pass type (passType=" + r.getPassType() + ").");
                } else {
                    if (r.getPassType().contains("REQUIRED") || r.getPassType().contains("PASS TYPE") || r.getPassType().contains("PASSTYPE")) {
                        // excel header line- noop
                    } else {
                        log.warn("Illegal argument.");
                        passes.add("Record " + counter++ + ": Failed. Internal server error.");
                    }
                }
            }

            accessPassRepository.flush();
            registrantRepository.flush();
        }
        log.info("Execution time: {} seconds", Duration.between(start, Instant.now()).toMillis() / 1000);
        return passes;
    }

    /**
     * Updates the access passes' <code>valid_to</code> property with the default expiration date.
     *
     * @param accessPass
     * @see #getExpirationDate()
     */
    @Transactional
    private void updateAccessPassValidityToDefault(AccessPass accessPass) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof HashMap) {
            HashMap<String, Object> principal = (HashMap<String, Object>) authentication.getPrincipal();
            String currentLoggedInUser = principal.get("sub").toString();

            accessPass.setIssuedBy(currentLoggedInUser);
        }

        accessPass.setValidTo(getExpirationDate());

        accessPassRepository.saveAndFlush(accessPass);
    }

    public List<String> batchUploadApprovers(List<AgencyUser> agencyUsers) {
        log.info("Processing batch registration of approvers.");
        List<String> result = new ArrayList<String>();

        // Validation
        BatchAgencyUserRequestValidator newAccessPassRequestValidator = new BatchAgencyUserRequestValidator(this.registrarUserRepository, this.registrarRepository);

        int counter = 1;
        for (AgencyUser agencyUser : agencyUsers) {
            try {

                agencyUser.setUsername(StringUtils.trim(agencyUser.getUsername()));
                agencyUser.setFirstName(StringUtils.trim(agencyUser.getFirstName()));
                agencyUser.setLastName(StringUtils.trim(agencyUser.getLastName()));

                agencyUser.setSource(RegistrarUserSource.BULK.name());
                StandardDataBindingValidation validation = new StandardDataBindingValidation(newAccessPassRequestValidator);
                validation.validate(agencyUser);

//                RegistrarUser registrarUser = this.authService.createAgencyCredentials(agencyUser);
//                FIXME
                RegistrarUser registrarUser = null;

                result.add("Record " + counter++ + ": Success. ");
            } catch (ReadableValidationException e) {
                result.add("Record " + counter++ + ": Failed. " + e.getMessage());
            } 
        }
        return result;
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
     *
     * @deprecated use {@link MobileDeviceService} instead
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

    public ControlCodeResponse getControlCode(String referenceId) {
        AccessPass accessPass = this.findByNonUniqueReferenceId(referenceId);

        if (accessPass == null) return null;

        if (!accessPass.getStatus().equals(AccessPassStatus.APPROVED.name()))
            throw new IllegalArgumentException(String.format("Successfully found an access pass with reference ID %s, but it is not approved.", referenceId));

        String controlCode = this.controlCodeService.encode(accessPass.getId());
        accessPass.setControlCode(controlCode);
        accessPassRepository.saveAndFlush(accessPass);

        ControlCodeResponse controlCodeResponse = new ControlCodeResponse();
        controlCodeResponse.setControlCode(controlCode);

        return controlCodeResponse;
    }

    private OffsetDateTime getExpirationDate() {
        return OffsetDateTime.of(expirationYear, expirationMonth, expirationDay, 23,
                59, 59, 999, ZoneOffset.ofHours(8));
    }
}
