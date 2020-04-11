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

    protected void validateRequiredFields(RapidPassRequest request, Errors errors) {
        super.validateRequiredFields(request, errors);
        ValidationUtils.rejectIfEmpty(errors, "email", "missing.email", "Missing Email.");

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
