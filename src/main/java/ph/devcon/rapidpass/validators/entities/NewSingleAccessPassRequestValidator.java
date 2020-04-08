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
        ValidationUtils.rejectIfEmpty(errors, "email", "missing.email", "Missing Email.");
        super.validateRequiredFields(request, errors);
    }

}
