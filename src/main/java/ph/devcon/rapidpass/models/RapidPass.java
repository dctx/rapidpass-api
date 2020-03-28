package ph.devcon.rapidpass.models;

import lombok.Builder;
import lombok.Data;
import ph.devcon.rapidpass.entities.AccessPass;

import java.util.Date;

/**
 * Data model representing an {@link AccessPass}, but is only a subset of the model's properties.
 *
 * API consumers send and receive {@link RapidPass} when interacting with the API for registering a rapid pass (GET, PUT).
 *
 * API consumers send {@link RapidPassRequest} when they send a query for creating a {@link AccessPass} (POST).
 *
 * This is JSON format returned to the user when they request for a GET on the AccessPass Resource.
 */
@Data
@Builder
public class RapidPass {
    private String referenceId;
    private String controlCode;
    private String passType;
    private String aporType;
    private String identifierNumber;
    private String name;
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
                .identifierNumber(accessPass.getIdentifierNumber())
                .name(accessPass.getName())
                .status(accessPass.getStatus())
                .validFrom(accessPass.getValidFrom())
                .validUntil(accessPass.getValidTo())
                .build();
    }
}
