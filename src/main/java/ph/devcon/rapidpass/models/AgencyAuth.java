package ph.devcon.rapidpass.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AgencyAuth {

    private String accessCode;

}
