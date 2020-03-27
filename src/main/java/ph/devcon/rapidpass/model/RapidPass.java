package ph.devcon.rapidpass.model;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

/**
 * Data model representing an {@link AccessPass}, but is only a subset of the model's properties.
 *
 * This is JSON format returned to the user when they request for a GET on the AccessPass Resource.
 */
@Data
@Builder
public class RapidPass {
    private String referenceId;
    private String controlCode;
    private String plateOrId;
    private String status;
    private Date validFrom;
    private Date validUntil;

    public static RapidPass buildFrom(AccessPass accessPass) {
        // Returns only a subset of properties from {@link AccessPass}.
        return RapidPass.builder()
                .controlCode(accessPass.getControlCode() == null? "" : accessPass.getControlCode().toString())
                .plateOrId(accessPass.getPlateOrId())
                .status(accessPass.getStatus())
                .referenceId(accessPass.getReferenceId())
                .validFrom(accessPass.getValidFrom())
                .validUntil(accessPass.getValidTo())
                .build();
    }
}
