package ph.devcon.rapidpass.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CheckpointAuthResponse {
    private String signingKey;
    private String encryptionKey;
    private String initializationVector;
    private String accessCode;
}
