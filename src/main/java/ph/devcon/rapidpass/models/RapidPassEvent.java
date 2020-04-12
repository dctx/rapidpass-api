package ph.devcon.rapidpass.models;

import lombok.Builder;
import lombok.Data;
import ph.devcon.rapidpass.entities.AccessPassEvent;
import ph.devcon.rapidpass.utilities.ControlCodeGenerator;

@Data
@Builder
public class RapidPassEvent {

    private Integer eventID;
    private String referenceId;
    private String passType;
    private String aporType;
    private String controlCode;
    private String name;
    private String plateNumber;
    private String status;
    private Long validFrom;
    private Long validTo;
    private Long eventTimestamp;

    public static RapidPassEvent buildFrom(AccessPassEvent accessPassEvent, String secretKey){
        return RapidPassEvent.builder()
                .eventID(accessPassEvent.getId())
                .referenceId(accessPassEvent.getReferenceId())
                .passType(accessPassEvent.getPassType())
                .aporType(accessPassEvent.getAporType())
                .controlCode(ControlCodeGenerator.generate(secretKey, accessPassEvent.getAccessPassID()))
                .name(accessPassEvent.getName())
                .plateNumber(accessPassEvent.getPlateNumber())
                .status(accessPassEvent.getStatus())
                .validFrom(accessPassEvent.getValidFrom().toEpochSecond())
                .validTo(accessPassEvent.getValidTo().toEpochSecond())
                .eventTimestamp(accessPassEvent.getEventTimestamp().toEpochSecond())
                .build();
    }
}
