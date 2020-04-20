package ph.devcon.rapidpass.utilities.validators.entities.accesspass.rules;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

public class RequiredField implements Validator {

    private final String field;
    private final String errorCode;
    private final String defaultMesasge;

    public RequiredField(String field, String errorCode, String defaultMesasge) {

        this.field = field;
        this.errorCode = errorCode;
        this.defaultMesasge = defaultMesasge;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }

    @Override
    public void validate(Object object, Errors errors) {
        ValidationUtils.rejectIfEmpty(errors, field, errorCode, defaultMesasge);
    }
}
