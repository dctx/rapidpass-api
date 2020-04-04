package ph.devcon.rapidpass.validators.entities.access_pass;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Tests whether a{@link RapidPassRequest} or {@link AccessPass} is valid, and ready for creation.
 *
 * <h2>Validation rules</h2>
 * It ensures the following validation rules:
 * 1. ID type is valid (as checked from the database look up tables).
 * 2. APOR type is valid (as checked from the database look up tables).
 * 3. Pass type is valid (INDIVIDUAL or VEHICLE, enum checking).
 * 4. Plate number is a required field if the pass type is vehicle.
 * 5. Create new access pass fails if there is already an existing approved or pending access pass.
 *
 * <h2>Errors</h2>
 * Each error produced is written as a sentence so when combining them, don't use commas. Just use a space to
 * combine the sentences.
 *
 * <h2>Benefits</h2>
 * By generating this class, this validator could be reused with the convenience of memoizing the look up tables,
 * rather than having to repeatedly query the look up tables.
 *
 * This cannot be done with the checking for the existing access passes, as it is possible that the access passes may
 * have changed since the last query.
 */
public class NewAccessPassRequestValidator implements Validator {

    final LookupTableService lookupTableService;
    final AccessPassRepository accessPassRepository;

    private List<LookupTable> aporTypes;
    private List<LookupTable> individualIdTypes;
    private List<LookupTable> vehicleIdTypes;


    public NewAccessPassRequestValidator(LookupTableService lookupTableService, AccessPassRepository accessPassRepository) {
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
    
    
    private static boolean isValidMobileNumber(String mobileNumber){
    	final String MOBILE_NUMBER_REGEX = "^09\\d{9}$";
    	Pattern p = Pattern.compile(MOBILE_NUMBER_REGEX);
    	Matcher m = p.matcher(mobileNumber);
        return m.matches();
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

    private void validateRapidPassRequest(RapidPassRequest request, Errors errors) {
        if (!isValidAporType(request.getAporType()))
            errors.rejectValue("aporType", "invalid.aporType", "Invalid APOR Type.");

        ValidationUtils.rejectIfEmpty(errors, "passType", "missing.passType", "Missing Pass Type.");

        if (request.getPassType() == null || !isValidPassType(request.getPassType().toString()))
            errors.rejectValue("passType", "invalid.passType", "Invalid Pass Type.");

        if (request.getPassType() == null || !isValidIdType(request.getPassType().toString(), request.getIdType()))
            errors.rejectValue("idType", "invalid.idType", "Invalid ID Type.");

        ValidationUtils.rejectIfEmpty(errors, "identifierNumber", "missing.identifierNumber", "Missing identifier number.");

        if (request.getPassType() != null && !hasPlateNumberIfVehicle(request.getPassType().toString(), request.getPlateNumber()))
            errors.rejectValue("plateNumber", "missing.plateNumber", "Missing plate number.");

        String identifier = PassType.INDIVIDUAL == request.getPassType() ? request.getMobileNumber() : request.getPlateNumber();

        if (identifier != null && !hasNoExistingApprovedOrPendingPasses(identifier)) {
            errors.reject("existing.accessPass", String.format("An existing PENDING/APPROVED RapidPass already exists for %s.", identifier));
        }
        
        if(StringUtils.isEmpty(request.getMobileNumber()) || !isValidMobileNumber(request.getMobileNumber())){
        	errors.rejectValue("mobileNumber", "incorrectFormat.mobileNumber", "Incorrect mobile number format.");
        }
    }
}
