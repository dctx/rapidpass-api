package ph.devcon.rapidpass.models;

import lombok.Builder;
import lombok.Data;
import ph.devcon.rapidpass.entities.AccessPass;
import ph.devcon.rapidpass.enums.APORType;

import java.util.Date;

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
