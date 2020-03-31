package ph.devcon.rapidpass.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CheckpointAuthRequest {

    private String imei = null;

    private String masterKey = null;

}
