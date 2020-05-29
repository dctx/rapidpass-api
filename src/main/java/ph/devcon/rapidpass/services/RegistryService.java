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

import com.google.common.collect.ImmutableList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.MutablePair;
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
import java.security.Principal;
import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@Slf4j
@RequiredArgsConstructor
public class RegistryService {

    @Value("${rapidpass.expiration.year}")
    protected Integer expirationYear;

    @Value("${rapidpass.expiration.month}")
    protected Integer expirationMonth;

    @Value("${rapidpass.expiration.day}")
    protected Integer expirationDay;

    @Value("${kafka.enabled:false}")
    protected boolean isKafkaEnabled;

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
    public RapidPass newRequestPass(RapidPassRequest rapidPassRequest, Principal principal) {

        log.debug("New RapidPass Request: {}", rapidPassRequest);

        // normalize id, plate number and mobile number
        normalizeIdMobileAndPlateNumber(rapidPassRequest);

        // Doesn't do id type checking (see https://gitlab.com/dctx/rapidpass/rapidpass-api/-/issues/236).
        NewSingleAccessPassRequestValidator newAccessPassRequestValidator =
                new NewSingleAccessPassRequestValidator(this.lookupService, this.accessPassRepository, principal);
        StandardDataBindingValidation validation = new StandardDataBindingValidation(newAccessPassRequestValidator);
        validation.validate(rapidPassRequest);

        RapidPass rapidPass = persistAccessPass(rapidPassRequest, AccessPassStatus.PENDING, principal);
        if (isKafkaEnabled)
            eventProducer.sendMessage(rapidPass.getReferenceId(), rapidPass);

        return rapidPass;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    RapidPass persistAccessPass(RapidPassRequest rapidPassRequest, AccessPassStatus status, Principal principal) {

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

        String preferredUsername = KeycloakUtils.getPreferredUsername(principal);
        accessPass.setIssuedBy(preferredUsername);

        if (status == AccessPassStatus.PENDING) {
            accessPass.setValidTo(now);
        } else if (status == AccessPassStatus.APPROVED) {
            accessPass.setValidTo(getDefaultExpirationDate());
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
        List<AccessPass> accessPasses = accessPassRepository.findAllByReferenceIDOrderByValidToDesc(referenceId);

        if (accessPasses.size() <= 0) return null;

        if (accessPasses.size() > 1) {
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
     * <p>
     * Updates properties of an access pass that is not related to its status.
     * </p>
     * <p>
     * For status related updates, please see the other registry service methods.
     *</p>
     * @param updateRequest The data update for the AccessPass.
     * @param accessPass The current data for the AccessPass.
     * @param originalReferenceId The original reference ID of the AccessPass.
     * @return An updated {@link AccessPass}.
     * @see #approve(String)
     * @see #suspend(String, String)
     */
    private AccessPass update(AccessPass accessPass, RapidPassUpdateRequest updateRequest, String originalReferenceId) throws UpdateAccessPassException {

        String targetReferenceId = updateRequest.getReferenceId();

        boolean isDifferentMobileNumbers = !originalReferenceId.equals(targetReferenceId);

        boolean isNewMobileNumberSpecified = !StringUtils.isEmpty(targetReferenceId);

        boolean isChangeMobileNumberRequest = isDifferentMobileNumbers && isNewMobileNumberSpecified;

        if (updateRequest.getAporType() != null) {
            lookupService.getAporLookupByAporCode(updateRequest.getAporType())
                    .orElseThrow(() -> {
                        String errorMessage = String.format(
                                "Failed to update Access Pass (referenceId=%s) because APOR code provided is invalid (aporType=%s).",
                                accessPass.getReferenceID(),
                                updateRequest.getAporType()
                        );
                        log.error(errorMessage);
                        return new UpdateAccessPassException(errorMessage);
                    });

            accessPass.setAporType(updateRequest.getAporType());
        }

        if (!StringUtils.isEmpty(updateRequest.getCompany()))
            accessPass.setCompany(updateRequest.getCompany());
        if (!StringUtils.isEmpty(updateRequest.getDestCity()))
            accessPass.setDestinationCity(updateRequest.getDestCity());
        if (!StringUtils.isEmpty(updateRequest.getDestName()))
            accessPass.setDestinationName(updateRequest.getDestName());
        if (!StringUtils.isEmpty(updateRequest.getDestStreet()))
            accessPass.setDestinationStreet(updateRequest.getDestStreet());
        if (!StringUtils.isEmpty(updateRequest.getOriginName()))
            accessPass.setOriginName(updateRequest.getOriginName());
        if (!StringUtils.isEmpty(updateRequest.getOriginCity()))
            accessPass.setOriginCity(updateRequest.getOriginCity());
        if (!StringUtils.isEmpty(updateRequest.getOriginStreet()))
            accessPass.setOriginStreet(updateRequest.getOriginStreet());

        Registrant registrant = accessPass.getRegistrantId();

        if (isChangeMobileNumberRequest) {
            log.info(String.format("Attempting to change mobile number of an access pass from %s to %s.", originalReferenceId, targetReferenceId));
            AccessPass existingAccessPass = findByNonUniqueReferenceId(targetReferenceId);
            Registrant existingRegistrant = registrantRepository.findByReferenceId(targetReferenceId);

            if (existingAccessPass != null) {
                String errorMessage = String.format("You are not allowed to change the mobile number of this access pass from %s to %s, because an existing access pass already uses %s.", originalReferenceId, targetReferenceId, targetReferenceId);
                log.error(errorMessage);
                throw new UpdateAccessPassException(errorMessage);
            } else if (existingRegistrant != null) {
                String errorMessage = String.format("You are not allowed to change the mobile number of this access pass from %s to %s, because an existing registrant already uses %s.", originalReferenceId, targetReferenceId, targetReferenceId);
                log.error(errorMessage);
                throw new UpdateAccessPassException(errorMessage);
            } else {
                // Update access pass and registrant
                accessPass.setReferenceID(targetReferenceId);
                registrant.setReferenceId(targetReferenceId);
                registrant.setMobile(targetReferenceId);
                registrantRepository.saveAndFlush(registrant);
            }
        }

        if (!StringUtils.isEmpty(updateRequest.getRemarks()))
            accessPass.setUpdates(updateRequest.getRemarks());

        if (!StringUtils.isEmpty(updateRequest.getReasonForApplication()))
            accessPass.setRemarks(updateRequest.getReasonForApplication());

        if (updateRequest.getAporType() != null)
            accessPass.setAporType(updateRequest.getAporType());
        if (!StringUtils.isEmpty(updateRequest.getCompany()))
            accessPass.setCompany(updateRequest.getCompany());
        if (!StringUtils.isEmpty(updateRequest.getDestCity()))
            accessPass.setDestinationCity(updateRequest.getDestCity());
        if (!StringUtils.isEmpty(updateRequest.getDestName()))
            accessPass.setDestinationName(updateRequest.getDestName());
        if (!StringUtils.isEmpty(updateRequest.getDestStreet()))
            accessPass.setDestinationStreet(updateRequest.getDestStreet());
        if (!StringUtils.isEmpty(updateRequest.getOriginName()))
            accessPass.setOriginName(updateRequest.getOriginName());
        if (!StringUtils.isEmpty(updateRequest.getOriginCity()))
            accessPass.setOriginCity(updateRequest.getOriginCity());
        if (!StringUtils.isEmpty(updateRequest.getOriginStreet()))
            accessPass.setOriginStreet(updateRequest.getOriginStreet());

        if (!StringUtils.isEmpty(updateRequest.getIdentifierNumber()))
            accessPass.setIdentifierNumber(updateRequest.getIdentifierNumber());
        if (!StringUtils.isEmpty(updateRequest.getIdType()))
            accessPass.setIdType(updateRequest.getIdType());
        if (updateRequest.getPassType() != null)
            accessPass.setPassType(updateRequest.getPassType().toString());

        if (!StringUtils.isEmpty(updateRequest.getName())) {
            accessPass.setName(updateRequest.getName().toUpperCase());

            if (registrant != null) {
                registrant.setDateTimeUpdated(OffsetDateTime.now());
                registrant.setRegistrantName(updateRequest.getName());
                registrantRepository.saveAndFlush(registrant);
            }
        }

        if (!StringUtils.isEmpty(updateRequest.getValidUntil()))
            accessPass.setValidTo(OffsetDateTime.parse(updateRequest.getValidUntil()));
        if (!StringUtils.isEmpty(updateRequest.getValidFrom()))
            accessPass.setValidFrom(OffsetDateTime.parse(updateRequest.getValidFrom()));

        accessPass.setDateTimeUpdated(OffsetDateTime.now());

        return accessPassRepository.saveAndFlush(accessPass);
    }

    /**
     * <p>Marks a specified AccessPass as approved.</p>
     * <p>Note that if the AccessPass is already approved, this will simply overwrite the validity period with
     * the default expiration date.</p>
     * @param referenceId The mobile number of the RapidPass to approve.
     * @return An newly approved RapidPass.
     * @throws UpdateAccessPassException
     */
    private AccessPass approve(String referenceId) throws UpdateAccessPassException {
        log.debug("APPROVING refId {}", referenceId);

        AccessPass accessPass = this.updateStatus(referenceId, AccessPassStatus.APPROVED, null);

        OffsetDateTime now = OffsetDateTime.now();

        OffsetDateTime validTo = getDefaultExpirationDate();

        accessPass.setValidTo(validTo);
        accessPass.setValidFrom(now);

        return accessPass;

        // Generate control code using the unique ID specified by the database.
    }


    /**
     * After updating the target {@link AccessPass}, this returns a {@link RapidPass} whose status is granted.
     *
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
        String targetStatus = status.name();

        // List of status changes from A to B which are not allowed.
        ImmutableList<MutablePair<String, String>> notAllowed = ImmutableList.of(
                new MutablePair<>("APPROVED", "PENDING"),
                new MutablePair<>("APPROVED", "DECLINED"),
                new MutablePair<>("SUSPENDED", "APPROVED"),
                new MutablePair<>("SUSPENDED", "DECLINED"),
                new MutablePair<>("DECLINED", "PENDING"),
                new MutablePair<>("DECLINED", "APPROVED")
        );

        Boolean isNotAllowed = notAllowed.stream()
                .map(pair -> currentStatus.equalsIgnoreCase(pair.left) && targetStatus.equalsIgnoreCase(pair.right))
                .reduce((acc, curr) -> (acc || curr))
                .orElse(false);

        if (isNotAllowed) {
            String message = String.format("You are not allowed to change the status of this access pass from %s to %s.", currentStatus, targetStatus);
            throw new RegistryService.UpdateAccessPassException(message);
        }

        if (reason != null && (status == AccessPassStatus.DECLINED || status == AccessPassStatus.SUSPENDED)) {
            accessPass.setUpdates(reason);
        }

        if (status == AccessPassStatus.SUSPENDED) {
            // Change validity period to reach only up to now.
            accessPass.setValidTo(OffsetDateTime.now());
        }

        accessPass.setStatus(status.toString());
        accessPass.setDateTimeUpdated(OffsetDateTime.now());

        return accessPassRepository.saveAndFlush(accessPass);
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
        AccessPass accessPass;
        if (rapidPassUpdateRequest.getStatus() != null) {
            final AccessPassStatus status = AccessPassStatus.valueOf(rapidPassUpdateRequest.getStatus().name());
            switch (status) {
                case APPROVED:
                    accessPass = approve(referenceId);
                    break;
                case DECLINED:
                    accessPass = this.updateStatus(referenceId, AccessPassStatus.DECLINED, rapidPassUpdateRequest.getRemarks());
                    break;
                case SUSPENDED:
                    accessPass = this.updateStatus(referenceId, AccessPassStatus.SUSPENDED, rapidPassUpdateRequest.getRemarks());
                    break;
                case PENDING:
                    String currentStatus = findByNonUniqueReferenceId(referenceId).getStatus();
                    throw new UpdateAccessPassException(
                            String.format(
                                    "You are not allowed to change the status of this access pass from %s to %s",
                                    currentStatus,
                                    status
                            )
                    );
                default:
                    accessPass = findByNonUniqueReferenceId(referenceId);
            }
        } else {
            accessPass = findByNonUniqueReferenceId(referenceId);
        }

        if (accessPass == null)
            throw new UpdateAccessPassException("There was no access pass found with reference ID " + referenceId + ".");

        boolean shouldUpdateEmail = StringUtils.isNotEmpty(rapidPassUpdateRequest.getEmail());

        if (shouldUpdateEmail) {
            Registrant registrantId = accessPass.getRegistrantId();
            String oldEmail = registrantId.getEmail();
            String newEmail = rapidPassUpdateRequest.getEmail();

            log.info(String.format("Updating rapid pass email for access pass (refId=%s) from %s to %s.", referenceId, oldEmail, newEmail));
            registrantId.setEmail(rapidPassUpdateRequest.getEmail());
        }

        accessPass = update(accessPass, rapidPassUpdateRequest, referenceId);

        final RapidPass rapidPass = RapidPass.buildFrom(accessPass);

        if (isKafkaEnabled)
            eventProducer.sendMessage(referenceId, rapidPass);

        return rapidPass;
    }

    /**
     * After updating the target {@link AccessPass}, this returns a {@link RapidPass} whose status is revoked.
     *
     * @param referenceId The reference id of the {@link AccessPass} you are retrieving.
     * @param reason The reason why the {@link AccessPass} will be suspended.
     * @return Data stored on the database
     */
    public AccessPass suspend(String referenceId, String reason) throws UpdateAccessPassException {

        AccessPass accessPass = this.updateStatus(referenceId, AccessPassStatus.SUSPENDED, reason);

        RapidPass rapidPass = RapidPass.buildFrom(accessPass);

        if (isKafkaEnabled)
            eventProducer.sendMessage(rapidPass.getReferenceId(), rapidPass);

        return accessPass;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public String processBatchRapidPassRequest(RapidPassRequest request, BatchAccessPassRequestValidator validator, int counter, Principal principal) {

        try {

            request.setSource(RecordSource.BULK.toString());

            StandardDataBindingValidation validation = new StandardDataBindingValidation(validator);
            validation.validate(request);

            if (isKafkaEnabled) {
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
                    RapidPass pass = persistAccessPass(request, AccessPassStatus.APPROVED, principal);

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
                        this.updateAccessPassValidityToDefault(latestApprovedAccessPass, principal);
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
                        accessPass.setIssuedBy(KeycloakUtils.getPreferredUsername(principal));

                        accessPass.setSource("BULK_OVERRIDE_ONLINE");
                        accessPass.setControlCode(controlCodeService.encode(accessPass.getId()));
                        accessPassRepository.saveAndFlush(accessPass);
                    }


                    latestPendingAccessPass.setValidFrom(now);
                    latestPendingAccessPass.setValidTo(getDefaultExpirationDate());

                    latestPendingAccessPass.setSource("BULK");
                    latestPendingAccessPass.setStatus(AccessPassStatus.APPROVED.name());

                    latestPendingAccessPass.setIssuedBy(KeycloakUtils.getPreferredUsername(principal));
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
     * @param principal
     * @return a list of generated rapid passes, whose status are all approved.
     */
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public List<String> batchUploadRapidPassRequest(List<RapidPassCSVdata> approvedRapidPasses, Principal principal) throws RegistryService.UpdateAccessPassException {

        log.info("Process Batch Approving of AccessPass");
        List<String> passes = new ArrayList<>();

        // Validation
        BatchAccessPassRequestValidator batchAccessPassRequestValidator =
                new BatchAccessPassRequestValidator(this.lookupService, this.accessPassRepository, principal);

        RapidPass pass;
        int counter = 1;
        Instant start = Instant.now();
        for (RapidPassCSVdata r : approvedRapidPasses) {
            try {
                RapidPassRequest request = RapidPassRequest.buildFrom(r);
                String result = processBatchRapidPassRequest(request, batchAccessPassRequestValidator, counter, principal);

                if (!result.contains("Missing APOR Type. Missing Mobile Number. Missing First Name. Missing Last Name. Missing Destination City. Invalid APOR Type. Invalid mobile input."))
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
     * @see #getDefaultExpirationDate()
     */
    @Transactional
    private void updateAccessPassValidityToDefault(AccessPass accessPass, Principal principal) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        accessPass.setIssuedBy(KeycloakUtils.getPreferredUsername(principal));
        accessPass.setValidTo(getDefaultExpirationDate());

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

    /**
     * <p>See the following application config properties:</p>
     *
     * <code>
     *     rapidpass.expiration.year
     *     rapidpass.expiration.month
     *     rapidpass.expiration.day
     * </code>
     *
     * @return The default expiration date, specified by application config properties.
     */
    private OffsetDateTime getDefaultExpirationDate() {
        return OffsetDateTime.of(expirationYear, expirationMonth, expirationDay, 23,
                59, 59, 999, ZoneOffset.ofHours(8));
    }
}
