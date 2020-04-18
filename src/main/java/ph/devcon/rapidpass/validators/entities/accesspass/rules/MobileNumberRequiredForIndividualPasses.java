package ph.devcon.rapidpass.validators.entities.accesspass.rules;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import ph.devcon.rapidpass.enums.PassType;
import ph.devcon.rapidpass.models.RapidPassRequest;

public class MobileNumberRequiredForIndividualPasses implements Validator {
    @Override
    public boolean supports(Class<?> aClass) {
        return RapidPassRequest.class.equals(aClass);
    }

    @Override
    public void validate(Object object, Errors errors) {
        RapidPassRequest request = object instanceof RapidPassRequest ? ((RapidPassRequest) object) : null;
        if (request == null) return;

        boolean isIndividualPass = request.getPassType() != null && request.getPassType().equals(PassType.INDIVIDUAL);

        if (isIndividualPass) {
            ValidationUtils.rejectIfEmpty(errors, "mobileNumber", "missing.mobileNumber", "Missing Mobile Number.");
        }
    }
}
