package ph.devcon.rapidpass.models;

import lombok.Builder;
import lombok.Data;

/**
 * This is the payload for User Activation.
 */
@Data
@Builder
public class UserActivationRequest {

    private String activationCode;
    private String password;

}
