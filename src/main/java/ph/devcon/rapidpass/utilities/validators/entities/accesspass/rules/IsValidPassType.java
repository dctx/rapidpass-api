package ph.devcon.rapidpass.utilities.validators.entities.accesspass.rules;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ph.devcon.rapidpass.models.RapidPassRequest;

public class IsValidPassType implements Validator {

    public IsValidPassType() {
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return RapidPassRequest.class.equals(aClass);
    }

    protected boolean isValidPassType(String passType) {
        return passType.equals("INDIVIDUAL");
        // As of April 27, 2020 - We only allow individual passes.
        // return passType.equals("INDIVIDUAL") || passType.equals("VEHICLE");
    }

    @Override
    public void validate(Object object, Errors errors) {
        RapidPassRequest rapidPassRequest = object instanceof RapidPassRequest ? ((RapidPassRequest) object) : null;

        if (rapidPassRequest != null && (rapidPassRequest.getPassType() == null || !isValidPassType(rapidPassRequest.getPassType().name())))
            // errors.rejectValue("passType", "invalid.passType", "Invalid Pass Type.");
            // As of April 27, 2020 - We only allow individual passes.
            errors.rejectValue("passType", "invalid.passType", "Allowed pass type is INDIVIDUAL. Please register for an INDIVIDUAL pass, not vehicle passes.");

    }
}
