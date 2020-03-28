package ph.devcon.rapidpass.models;

import lombok.Builder;
import lombok.Data;
import ph.devcon.rapidpass.entities.AccessPass;
import ph.devcon.rapidpass.enums.APORType;

import java.util.Date;

/**
 * Data model representing an {@link AccessPass}, but is only a subset of the model's properties.
 *
 * This is the format/schema that is used as an interface within the API.
 *
 * API consumers receive {@link RapidPass} when they retrieve responses regarding a {@link AccessPass}.
 * API consumers send {@link RapidPassRequest} when they send queries related to a {@link AccessPass}.
 *
 * This is JSON format returned to the user when they request for a GET on the AccessPass Resource.
 */
@Data
@Builder
public class RapidPass {
    private String referenceId;
    private String controlCode;
    private String passType;
    private APORType aporType;
    private String plateOrId;
    private String status;
    private Date validFrom;
    private Date validUntil;

    public static RapidPass buildFrom(AccessPass accessPass) {
        // Returns only a subset of properties from {@link AccessPass}.
        return RapidPass.builder()
                .referenceId(accessPass.getReferenceId())
                .controlCode(accessPass.getControlCode() == null? "" : accessPass.getControlCode().toString())
                .passType(accessPass.getPassType())
                .aporType(accessPass.getAporType())
                .plateOrId(accessPass.getPlateOrId())
                .status(accessPass.getStatus())
                .validFrom(accessPass.getValidFrom())
                .validUntil(accessPass.getValidTo())
                .build();
    }
}
