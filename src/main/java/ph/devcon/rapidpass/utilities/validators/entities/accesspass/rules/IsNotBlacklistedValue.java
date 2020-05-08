package ph.devcon.rapidpass.utilities.validators.entities.accesspass.rules;

import lombok.SneakyThrows;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ph.devcon.rapidpass.models.RapidPassRequest;

import java.lang.reflect.Field;
import java.util.List;

public class IsNotBlacklistedValue implements Validator {

    private final String field;
    private final String errorCode;
    private List<String> blacklist;

    public IsNotBlacklistedValue(String field, String errorCode, List<String> blacklist) {

        this.field = field;
        this.errorCode = errorCode;
        this.blacklist = blacklist;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }

    boolean isBlackListed(String value) {
        return this.blacklist.contains(value);
    }

    @SneakyThrows
    @Override
    public void validate(Object input, Errors errors) {
        RapidPassRequest request = input instanceof RapidPassRequest ? ((RapidPassRequest) input) : null;

        Field declaredField = input.getClass().getDeclaredField(field);
        declaredField.setAccessible(true);

        Class<?> type = declaredField.getType();

        if (!type.equals(String.class)) throw new IllegalArgumentException("This only works for strings.");

        String value = (String) declaredField.get(input);

        if (request != null && isBlackListed(value)) {
            errors.rejectValue(this.field, this.errorCode, "Invalid value not allowed: " + value);
        }

    }
}
