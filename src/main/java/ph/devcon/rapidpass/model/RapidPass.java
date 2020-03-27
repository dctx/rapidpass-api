package ph.devcon.rapidpass.model;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class RapidPass {

    private String referenceId;
    private String controlCode;
    private String plateOrId;
    private String status;
    private Date validFrom;
    private Date validUntil;
}
