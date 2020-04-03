package ph.devcon.rapidpass.validators.entities;

import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import ph.devcon.rapidpass.entities.AccessPass;
import ph.devcon.rapidpass.entities.LookupTable;
import ph.devcon.rapidpass.entities.LookupTablePK;
import ph.devcon.rapidpass.enums.AccessPassStatus;
import ph.devcon.rapidpass.enums.PassType;
import ph.devcon.rapidpass.models.RapidPassRequest;
import ph.devcon.rapidpass.repositories.AccessPassRepository;
import ph.devcon.rapidpass.services.LookupTableService;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Subset of {@link NewAccessPassRequestValidator}, that doesn't do id type checking.
 *
 * The id type checking will be handled by the front end for now (so they won't cram too much work).
 */
public class NewSingleAccessPassRequestValidator implements Validator {

    final LookupTableService lookupTableService;
    final AccessPassRepository accessPassRepository;

    private List<LookupTable> aporTypes;
    private List<LookupTable> individualIdTypes;
    private List<LookupTable> vehicleIdTypes;


    public NewSingleAccessPassRequestValidator(LookupTableService lookupTableService, AccessPassRepository accessPassRepository) {
        this.lookupTableService = lookupTableService;
        this.accessPassRepository = accessPassRepository;

        aporTypes = lookupTableService.getAporTypes();
        individualIdTypes = lookupTableService.getIndividualIdTypes();
        vehicleIdTypes = lookupTableService.getVehicleIdTypes();
    }

    private Stream<String> getVehicleIdTypes() {
        return vehicleIdTypes
                .stream().map(LookupTable::getLookupTablePK).map(LookupTablePK::getValue);
    }

    private Stream<String> getIndividualIdTypes() {
        return individualIdTypes
                .stream().map(LookupTable::getLookupTablePK).map(LookupTablePK::getValue);
    }

    private Stream<String> getAporTypes() {
        return aporTypes.stream().map(LookupTable::getLookupTablePK).map(LookupTablePK::getValue);
    }

    /**
     * This validator works for both {@link RapidPassRequest} and {@link AccessPass}.
     * @return true if the class is supported.
     */
    @Override
    public boolean supports(Class<?> aClass) {
        if (RapidPassRequest.class.equals(aClass))
            return true;
        return AccessPass.class.equals(aClass);
    }

    @Override
    public void validate(Object object, Errors errors) {
        RapidPassRequest rapidPassRequest = object instanceof RapidPassRequest ? ((RapidPassRequest) object) : null;
        if (rapidPassRequest != null)
            validateRapidPassRequest(rapidPassRequest, errors);

        AccessPass accessPass = object instanceof AccessPass ? ((AccessPass) object) : null;
        if (accessPass != null)
            validateAccessPass(accessPass, errors);
    }

    private boolean isValidAporType(String aporType) {
        return getAporTypes()
                .filter(type -> type.equals(aporType))
                .count() == 1L;
    }

    private boolean isValidPassType(String passType) {
        for (PassType value : PassType.values()) {
            if (value.toString().equals(passType))
                return true;
        }
        return false;
    }

    private boolean isValidIdType(String passType, String idType) {
        if (PassType.INDIVIDUAL.toString().equals(passType))
            return getIndividualIdTypes()
                .filter(type -> type.equals(idType))
                .count() == 1L;

        else if (PassType.VEHICLE.toString().equals(passType))
            return getVehicleIdTypes()
                .filter(type -> type.equals(idType))
                .count() == 1L;

        return false;
    }

    private boolean hasPlateNumberIfVehicle(String passType, String plateNumber) {
        // if this is not for a vehicle, do not perform this validation
        if (!PassType.VEHICLE.toString().equals(passType)) return true;

        return StringUtils.hasLength(plateNumber);
    }

    /**
     * Checks if there is an existing PENDING/APPROVED RapidPass for referenceId which can be mobile number or
     * plate number.
     *
     * The heaviest query, as this needs direct access to the database every time it needs to check.
     * @param referenceId - The plateNumber if the pass is for a vehicle, or the identifierNumber if an individual.
     *
     * @return true if it is clear to create a new access pass (there is no existing approved or pending passes)
     */
    private boolean hasNoExistingApprovedOrPendingPasses(String referenceId) {
        referenceId = referenceId.trim();

        OffsetDateTime now = OffsetDateTime.now();

        final List<AccessPass> existingAccessPasses = accessPassRepository.findAllByReferenceIDOrderByValidToDesc(referenceId);

        // get all valid PENDING or APPROVED rapid pass requests for referenceId
        final Optional<AccessPass> existingAccessPass = existingAccessPasses
                .stream()
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

        return !existingAccessPass.isPresent();
    }

    private void validateAccessPass(AccessPass accessPass, Errors errors) {

        if (!isValidAporType(accessPass.getAporType()))
            errors.rejectValue("aporType", "invalid.aporType", "Invalid APOR Type.");

        ValidationUtils.rejectIfEmpty(errors, "passType", "missing.passType", "Missing Pass Type.");

        if (accessPass.getPassType() == null || !isValidPassType(accessPass.getPassType()))
            errors.rejectValue("passType", "invalid.passType", "Invalid Pass Type.");
//
//        if (!isValidIdType(accessPass.getPassType(), accessPass.getIdType()))
//            errors.rejectValue("idType", "invalid.idType", "Invalid ID Type.");

        ValidationUtils.rejectIfEmpty(errors, "identifierNumber", "missing.identifierNumber", "Missing identifier number.");

        if (!hasPlateNumberIfVehicle(accessPass.getPassType(), accessPass.getPlateNumber()))
            errors.rejectValue("plateNumber", "missing.plateNumber", "Missing plate number.");

        String identifier = accessPass.getReferenceID();

        if (identifier != null && !hasNoExistingApprovedOrPendingPasses(identifier)) {
            errors.reject("existing.accessPass", String.format("An existing PENDING/APPROVED RapidPass already exists for %s", identifier));
        }
    }

    private void validateRapidPassRequest(RapidPassRequest request, Errors errors) {
        if (!isValidAporType(request.getAporType()))
            errors.rejectValue("aporType", "invalid.aporType", "Invalid APOR Type.");

        ValidationUtils.rejectIfEmpty(errors, "passType", "missing.passType", "Missing Pass Type.");

        if (request.getPassType() == null || !isValidPassType(request.getPassType().toString()))
            errors.rejectValue("passType", "invalid.passType", "Invalid Pass Type.");
//
//        if (!isValidIdType(request.getPassType().toString(), request.getIdType()))
//            errors.rejectValue("idType", "invalid.idType", "Invalid ID Type.");

        ValidationUtils.rejectIfEmpty(errors, "identifierNumber", "missing.identifierNumber", "Missing identifier number.");

        if (request.getPassType() != null && !hasPlateNumberIfVehicle(request.getPassType().toString(), request.getPlateNumber()))
            errors.rejectValue("plateNumber", "missing.plateNumber", "Missing plate number.");

        String identifier = PassType.INDIVIDUAL == request.getPassType() ? request.getMobileNumber() : request.getPlateNumber();

        if (identifier != null && !hasNoExistingApprovedOrPendingPasses(identifier)) {
            errors.reject("existing.accessPass", String.format("An existing PENDING/APPROVED RapidPass already exists for %s", identifier));
        }
    }
}