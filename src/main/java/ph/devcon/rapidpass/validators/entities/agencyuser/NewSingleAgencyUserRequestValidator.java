package ph.devcon.rapidpass.validators.entities.agencyuser;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import ph.devcon.rapidpass.entities.AccessPass;
import ph.devcon.rapidpass.models.AgencyUser;
import ph.devcon.rapidpass.models.RapidPassRequest;
import ph.devcon.rapidpass.repositories.RegistrarRepository;
import ph.devcon.rapidpass.repositories.RegistrarUserRepository;

/**
 * Tests whether a{@link RapidPassRequest} or {@link AccessPass} is valid, and ready for creation.
 *
 * <h2>Validation rules</h2>
 * It ensures the following validation rules:
 * 1. ID type is valid (as checked from the database look up tables).
 * 2. APOR type is valid (as checked from the database look up tables).
 */
public class NewSingleAgencyUserRequestValidator extends BaseAgencyUserRequestValidator {

    public NewSingleAgencyUserRequestValidator(RegistrarUserRepository registrarUserRepository, RegistrarRepository registrarRepository) {
        super(registrarUserRepository, registrarRepository);
    }

    protected void validateRequiredFields(AgencyUser agencyUser, Errors errors) {
        ValidationUtils.rejectIfEmpty(errors, "password", "missing.password", "Missing password.");
        super.validateRequiredFields(agencyUser, errors);
    }
}
