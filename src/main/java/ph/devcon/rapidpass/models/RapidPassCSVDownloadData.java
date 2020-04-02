package ph.devcon.rapidpass.models;

import lombok.Builder;
import lombok.Data;
import ph.devcon.rapidpass.entities.AccessPass;
import ph.devcon.rapidpass.enums.PassType;

@Data
@Builder
public class RapidPassCSVDownloadData
{
    private String controlCode;
    private String passType;
    private String aporType;
    private long validFrom;
    private long validUntil;
    private long issuedOn;
    private String idType;
    private String identifierNumber;
    private String status;
    
    
    public static RapidPassCSVDownloadData buildFrom(AccessPass accessPass)
    {
        return RapidPassCSVDownloadData.builder()
            .controlCode(accessPass.getControlCode())
            .passType(accessPass.getPassType())
            .aporType(accessPass.getAporType())
            .validFrom(accessPass.getValidFrom().toEpochSecond())
            .validUntil(accessPass.getValidTo().toEpochSecond())
            .idType(accessPass.getIdType())
            .identifierNumber(accessPass.getIdentifierNumber())
            .status(accessPass.getStatus())
            .issuedOn(accessPass.getDateTimeUpdated().toEpochSecond())
            .build();
    }
}
