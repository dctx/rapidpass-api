package ph.devcon.rapidpass.utilities.validators.entities.accesspass.rules;

import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ph.devcon.rapidpass.entities.AporLookup;
import ph.devcon.rapidpass.models.RapidPassRequest;

import java.util.List;
import java.util.stream.Stream;

public class IsValidAporType implements Validator {

    final private List<AporLookup> aporTypes;

    public IsValidAporType(List<AporLookup> aporTypes) {
        this.aporTypes = aporTypes;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return RapidPassRequest.class.equals(aClass);
    }

    private Stream<String> getAporTypes() {
        return aporTypes.stream().map(AporLookup::getAporCode);
    }

    protected boolean isValidAporType(String aporType) {
        return getAporTypes()
                .filter(type -> type.equals(aporType))
                .count() == 1L;
    }

    @Override
    public void validate(Object object, Errors errors) {
        RapidPassRequest request = object instanceof RapidPassRequest ? ((RapidPassRequest) object) : null;

        if (request != null && (StringUtils.isEmpty(request.getAporType()) || !isValidAporType(request.getAporType()))) {
            errors.rejectValue("aporType", "invalid.aporType", "Invalid APOR Type.");
        }
    }
}
