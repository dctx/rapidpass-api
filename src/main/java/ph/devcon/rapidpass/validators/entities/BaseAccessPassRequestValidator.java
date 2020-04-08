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
import ph.devcon.rapidpass.utilities.StringFormatter;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public abstract class BaseAccessPassRequestValidator implements Validator {

    final AccessPassRepository accessPassRepository;
    final LookupTableService lookupTableService;

    protected List<LookupTable> aporTypes;
    protected List<LookupTable> individualIdTypes;
    protected List<LookupTable> vehicleIdTypes;

    public BaseAccessPassRequestValidator(LookupTableService lookupTableService, AccessPassRepository accessPassRepository) {
        this.lookupTableService = lookupTableService;
        this.accessPassRepository = accessPassRepository;

        aporTypes = lookupTableService.getAporTypes();
        individualIdTypes = lookupTableService.getIndividualIdTypes();
        vehicleIdTypes = lookupTableService.getVehicleIdTypes();
    }

    protected static boolean isValidMobileNumber(String mobileNumber) {
        final String MOBILE_NUMBER_REGEX = "^(9|09|639|\\+639)\\d{9}$";
        Pattern p = Pattern.compile(MOBILE_NUMBER_REGEX);
        Matcher m = p.matcher(mobileNumber);
        return m.matches();
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

    protected boolean isValidAporType(String aporType) {
        return getAporTypes()
                .filter(type -> type.equals(aporType))
                .count() == 1L;
    }

    protected boolean isValidPassType(String passType) {
        for (PassType value : PassType.values()) {
            if (value.toString().equals(passType))
                return true;
        }
        return false;
    }

    boolean isValidIdType(String passType, String idType) {
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

    protected boolean hasPlateNumberIfVehicle(String passType, String plateNumber) {
        // if this is not for a vehicle, do not perform this validation
        if (!PassType.VEHICLE.toString().equals(passType)) return true;

        return StringUtils.hasLength(plateNumber);
    }

    /**
     * Checks if there is an existing PENDING/APPROVED RapidPass for referenceId which can be mobile number or
     * plate number.
     * <p>
     * The heaviest query, as this needs direct access to the database every time it needs to check.
     *
     * @param referenceId - The plateNumber if the pass is for a vehicle, or the identifierNumber if an individual.
     * @return true if it is clear to create a new access pass (there is no existing approved or pending passes)
     */
    protected boolean hasNoExistingApprovedOrPendingPasses(String referenceId) {

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

    /**
     * This validator works for both {@link RapidPassRequest} and {@link AccessPass}.
     *
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
    }

    protected void validateRequiredFields(RapidPassRequest request, Errors errors) {
        ValidationUtils.rejectIfEmpty(errors, "passType", "missing.passType", "Missing Pass Type.");
        ValidationUtils.rejectIfEmpty(errors, "aporType", "missing.aporType", "Missing APOR Type.");
        ValidationUtils.rejectIfEmpty(errors, "idType", "missing.idType", "Missing ID Type.");
        ValidationUtils.rejectIfEmpty(errors, "identifierNumber", "missing.identifierNumber", "Missing identifier number.");
        ValidationUtils.rejectIfEmpty(errors, "firstName", "missing.firstName", "Missing First Name.");
        ValidationUtils.rejectIfEmpty(errors, "lastName", "missing.lastName", "Missing Last Name.");
        ValidationUtils.rejectIfEmpty(errors, "originStreet", "missing.originStreet", "Missing Origin Street.");
        if (request.getPassType() != null && request.getPassType().equals(PassType.VEHICLE)) {
            ValidationUtils.rejectIfEmpty(errors, "plateNumber", "missing.plateNumber", "Missing Plate Number.");
        } else {
            ValidationUtils.rejectIfEmpty(errors, "mobileNumber", "missing.mobileNumber", "Missing Mobile Number.");
        }
    }

    protected void validateRapidPassRequest(RapidPassRequest request, Errors errors) {

        validateRequiredFields(request, errors);

        if (errors.hasErrors())
            return;

        if (!isValidAporType(request.getAporType()))
            errors.rejectValue("aporType", "invalid.aporType", "Invalid APOR Type.");

        if (request.getPassType() == null || !isValidPassType(request.getPassType().toString()))
            errors.rejectValue("passType", "invalid.passType", "Invalid Pass Type.");

        if (request.getPassType() == null || !isValidIdType(request.getPassType().toString(), request.getIdType()))
            errors.rejectValue("idType", "invalid.idType", "Invalid ID Type.");

        if (request.getPassType() != null && !hasPlateNumberIfVehicle(request.getPassType().toString(), request.getPlateNumber()))
            errors.rejectValue("plateNumber", "missing.plateNumber", "Missing plate number.");

        if (StringUtils.isEmpty(request.getMobileNumber()) || !isValidMobileNumber(request.getMobileNumber())) {
            errors.rejectValue("mobileNumber", "incorrectFormat.mobileNumber", "Incorrect mobile number format.");
        }

        if (errors.hasErrors())
            return;

        String identifier = PassType.INDIVIDUAL == request.getPassType() ?
                "0" + org.apache.commons.lang3.StringUtils.right(request.getMobileNumber(), 10) :
                request.getPlateNumber();

        if (identifier != null && !hasNoExistingApprovedOrPendingPasses(identifier)) {
            errors.reject("existing.accessPass", String.format("An existing PENDING/APPROVED RapidPass already exists for %s.", identifier));
        }
    }
}

