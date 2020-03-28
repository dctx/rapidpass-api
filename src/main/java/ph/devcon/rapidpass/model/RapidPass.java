package ph.devcon.rapidpass.model;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class RapidPass {
    private String referenceId;
    private String controlCode;
    private String passType;
    private String accessType;
    private String plateOrId;
    private String status;
    private Date validFrom;
    private Date validUntil;

    public static RapidPass buildFrom(AccessPass accessPass) {
        return RapidPass.builder()
                .referenceId(accessPass.getReferenceId())
                .controlCode(accessPass.getControlCode() == null? "" : accessPass.getControlCode().toString())
                .passType(accessPass.getPassType())
                .accessType(accessPass.getAporType())
                .plateOrId(accessPass.getPlateOrId())
                .status(accessPass.getStatus())
                .validFrom(accessPass.getValidFrom())
                .validUntil(accessPass.getValidTo())
                .build();
    }
}
