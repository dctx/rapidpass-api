package ph.devcon.rapidpass.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Login {

    private String username;
    private String password;

}
