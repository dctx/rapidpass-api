package ph.devcon.rapidpass.validators.entities.agency_user;

import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import ph.devcon.rapidpass.entities.Registrar;
import ph.devcon.rapidpass.entities.RegistrarUser;
import ph.devcon.rapidpass.models.AgencyUser;
import ph.devcon.rapidpass.repositories.RegistrarRepository;
import ph.devcon.rapidpass.repositories.RegistrarUserRepository;

import java.util.List;

public class NewAgencyUserValidator implements Validator {

    private final RegistrarUserRepository registrarUserRepository;
    private final RegistrarRepository registrarRepository;

    private List<Registrar> registrars;


    public NewAgencyUserValidator(RegistrarUserRepository registrarUserRepository, RegistrarRepository registrarRepository) {
        this.registrarUserRepository = registrarUserRepository;
        this.registrarRepository = registrarRepository;
    }


    private boolean registrarWithShortNameExists(String shortName) {
        if (StringUtils.isEmpty(shortName))
            return false;

        return registrarRepository.findByShortName(shortName) != null;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return  (AgencyUser.class.equals(aClass));
    }

    @Override
    public void validate(Object object, Errors errors) {
        AgencyUser agencyUser = object instanceof AgencyUser ? ((AgencyUser) object) : null;
        if (agencyUser != null)
            validateAgencyUser(agencyUser, errors);
    }

    private void validateAgencyUser(AgencyUser agencyUser, Errors errors) {

        ValidationUtils.rejectIfEmpty(errors, "username", "missing.username", "Missing username.");

        // NOTE: On batch upload, passwords are not configured. The user will be instructed to activate their account
        // and configure their password manually.
        if (!isBatchUpload(agencyUser, errors)) {
            ValidationUtils.rejectIfEmpty(errors, "password", "missing.password", "Missing password.");
        } else {
            // Batch upload requires first name and last name too.
            ValidationUtils.rejectIfEmpty(errors, "firstName", "missing.firstName", "Missing first name.");
            ValidationUtils.rejectIfEmpty(errors, "lastName", "missing.lastName", "Missing last name.");

            ValidationUtils.rejectIfEmpty(errors, "email", "missing.email", "Missing email.");
        }

        ValidationUtils.rejectIfEmpty(errors, "registrar", "missing.registrar", "Missing registrar.");

        if (!hasValidRegistrar(agencyUser))
            errors.rejectValue("registrar", "invalid.registrar", "No registrar found with given short name (shortName=" + agencyUser.getRegistrar() + ").");

        if (usernameAlreadyExists(agencyUser))
            errors.rejectValue("username", "duplicate.username", "Username already exists (username=" + agencyUser.getUsername() +  ")");
    }

    private boolean hasValidRegistrar(AgencyUser agencyUser) {
        return registrarWithShortNameExists(agencyUser.getRegistrar());
    }

    private boolean usernameAlreadyExists(AgencyUser agencyUser) {
        if (StringUtils.isEmpty(agencyUser.getUsername()))
            return false;

        return registrarUserRepository.findByUsername(agencyUser.getUsername()) != null;
    }

    private boolean isBatchUpload(AgencyUser agencyUser, Errors errors) {
        return agencyUser.isBatchUpload();
    }
}
