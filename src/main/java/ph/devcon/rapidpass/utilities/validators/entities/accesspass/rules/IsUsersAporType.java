package ph.devcon.rapidpass.utilities.validators.entities.accesspass.rules;

import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ph.devcon.rapidpass.models.RapidPassRequest;

import java.util.List;
import java.util.stream.Stream;

public class IsUsersAporType implements Validator {

    final private List<String> allowedAporTypes;

    public IsUsersAporType(List<String> allowedAporTypes) {
        this.allowedAporTypes = allowedAporTypes;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return RapidPassRequest.class.equals(aClass);
    }

    private Stream<String> getAporTypes() {
        return allowedAporTypes.stream();
    }

    protected boolean isUsersAporType(String aporType) {
        return getAporTypes()
                .filter(type -> type.equals(aporType))
                .count() == 1L;
    }

    @Override
    public void validate(Object object, Errors errors) {
        RapidPassRequest request = object instanceof RapidPassRequest ? ((RapidPassRequest) object) : null;

        if (request != null && !StringUtils.isEmpty(request.getAporType()) && !isUsersAporType(request.getAporType())) {
            String defaultMessage = String.format("You are not authorized to upload for APOR type %s.", request.getAporType());
            errors.rejectValue("aporType", "notOwned.aporType", defaultMessage);
        }
    }
}
