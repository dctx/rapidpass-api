package ph.devcon.rapidpass.utilities.validators.entities.accesspass.rules;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ph.devcon.rapidpass.models.RapidPassRequest;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IsValidMobileNumber implements Validator {
    protected static boolean isValidMobileNumber(String mobileNumber) {
        final String MOBILE_NUMBER_REGEX = "^(9|09|639|\\+639)\\d{9}$";
        Pattern p = Pattern.compile(MOBILE_NUMBER_REGEX);
        Matcher m = p.matcher(mobileNumber);
        return m.matches();
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return RapidPassRequest.class.equals(aClass);
    }

    @Override
    public void validate(Object object, Errors errors) {
        RapidPassRequest request = object instanceof RapidPassRequest ? ((RapidPassRequest) object) : null;

        if (request != null && !isValidMobileNumber(request.getMobileNumber())) {
            errors.rejectValue("mobileNumber", "invalid.mobileNumber", "Invalid mobile input.");
        }
    }
}
