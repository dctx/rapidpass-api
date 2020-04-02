package ph.devcon.rapidpass.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CheckpointAuthResponse {
    private String qrKey = null;
    private String accessCode = null;
}
