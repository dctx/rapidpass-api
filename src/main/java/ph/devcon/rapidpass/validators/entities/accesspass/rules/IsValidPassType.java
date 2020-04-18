package ph.devcon.rapidpass.validators.entities.accesspass.rules;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ph.devcon.rapidpass.enums.PassType;
import ph.devcon.rapidpass.models.RapidPassRequest;

import java.util.List;

public class IsValidPassType implements Validator {

    final private List<PassType> passType;

    public IsValidPassType(List<PassType> aporTypes) {
        this.passType = aporTypes;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return RapidPassRequest.class.equals(aClass);
    }

    protected boolean isValidPassType(String passType) {
        try {
            PassType.valueOf(passType);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void validate(Object object, Errors errors) {
        RapidPassRequest request = object instanceof RapidPassRequest ? ((RapidPassRequest) object) : null;
        if (request == null) return;

        if (request.getPassType() == null || !isValidPassType(request.getPassType().name()))
            errors.rejectValue("passType", "invalid.passType", "Invalid Pass Type.");
    }
}
