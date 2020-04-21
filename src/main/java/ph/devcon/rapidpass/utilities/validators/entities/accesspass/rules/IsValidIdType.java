package ph.devcon.rapidpass.utilities.validators.entities.accesspass.rules;


import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ph.devcon.rapidpass.entities.LookupTable;
import ph.devcon.rapidpass.entities.LookupTablePK;
import ph.devcon.rapidpass.models.RapidPassRequest;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

public class IsValidIdType implements Validator {

    final private List<LookupTable> IdTypes;

    public IsValidIdType(List<LookupTable> individualTypes, List<LookupTable> vehicleTypes) {
        this.IdTypes = new LinkedList<>(individualTypes);
        this.IdTypes.addAll(vehicleTypes);
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return RapidPassRequest.class.equals(aClass);
    }

    private Stream<String> getIdTypes() {
        return IdTypes.stream().map(LookupTable::getLookupTablePK).map(LookupTablePK::getValue);
    }

    protected boolean isValidIdType(String aporType) {
        return getIdTypes()
                .filter(type -> type.equals(aporType))
                .count() == 1L;
    }

    @Override
    public void validate(Object object, Errors errors) {
        RapidPassRequest request = object instanceof RapidPassRequest ? ((RapidPassRequest) object) : null;

        if (request != null && (StringUtils.isEmpty(request.getIdType()) && !isValidIdType(request.getPassType().name()))) {
            errors.rejectValue("idType", "invalid.idType", "Invalid ID Type.");
        }

    }
}
