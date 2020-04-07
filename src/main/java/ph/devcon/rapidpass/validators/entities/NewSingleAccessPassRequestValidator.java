package ph.devcon.rapidpass.validators.entities;

import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import ph.devcon.rapidpass.entities.AccessPass;
import ph.devcon.rapidpass.entities.LookupTable;
import ph.devcon.rapidpass.enums.PassType;
import ph.devcon.rapidpass.models.RapidPassRequest;
import ph.devcon.rapidpass.repositories.AccessPassRepository;
import ph.devcon.rapidpass.services.LookupTableService;

import java.util.List;

/**
 * Subset of {@link BatchAccessPassRequestValidator}, that doesn't do id type checking.
 *
 * The id type checking will be handled by the front end for now (so they won't cram too much work).
 */
public class NewSingleAccessPassRequestValidator extends BaseAccessPassRequestValidator {

    public NewSingleAccessPassRequestValidator(LookupTableService lookupTableService, AccessPassRepository accessPassRepository) {
        super(lookupTableService, accessPassRepository);
    }

    @Override
    protected void validateRapidPassRequest(RapidPassRequest request, Errors errors) {
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
            errors.reject("existing.accessPass", String.format("An existing PENDING/APPROVED RapidPass already exists for %s.", identifier));
        }
        
        if(StringUtils.isEmpty(request.getMobileNumber()) || !isValidMobileNumber(request.getMobileNumber())){
        	errors.rejectValue("mobileNumber", "incorrectFormat.mobileNumber", "Incorrect mobile number format.");
        }
    }
}
