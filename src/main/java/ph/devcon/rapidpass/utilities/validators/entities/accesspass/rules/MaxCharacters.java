package ph.devcon.rapidpass.utilities.validators.entities.accesspass.rules;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import ph.devcon.rapidpass.models.RapidPassRequest;

import java.lang.reflect.Field;

public class MaxCharacters implements Validator {

    private final String field;
    private int count;
    private final String errorCode;
    private final String defaultMesasge;

    public MaxCharacters(String field, int count, String errorCode, String defaultMesasge) {

        this.field = field;
        this.count = count;
        this.errorCode = errorCode;
        this.defaultMesasge = defaultMesasge;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }

    @Override
    public void validate(Object object, Errors errors) {
        RapidPassRequest request = object instanceof RapidPassRequest ? ((RapidPassRequest) object) : null;

        if (request != null && !isWithinMaxCharacters(request)) {
            errors.rejectValue(this.field, this.errorCode, String.format("The field %s should have less than %d characters.", this.field, this.count));
        }
        ValidationUtils.rejectIfEmpty(errors, field, errorCode, defaultMesasge);
    }

    private boolean isWithinMaxCharacters(RapidPassRequest request) {
        try {
            Field declaredField = request.getClass().getDeclaredField(field);
            declaredField.setAccessible(true);

            Class<?> type = declaredField.getType();

            if (!type.equals(String.class)) throw new IllegalArgumentException("This only works for strings.");

            String o = (String) declaredField.get(request);

            return o.length() < this.count;
        }catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
