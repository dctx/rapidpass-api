package ph.devcon.rapidpass.models;

import lombok.Builder;
import lombok.Data;
import ph.devcon.rapidpass.entities.LookupTable;

@Data
@Builder
public final class LookupValue {

    private String value;
    private String description;

    public static final LookupValue from(final LookupTable lookupTable) {
        return LookupValue.builder()
                .value(lookupTable.getLookupTablePK().getValue())
                .description(lookupTable.getDescription())
                .build();
    }
}
