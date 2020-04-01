package ph.devcon.rapidpass.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AgencyUser {

    // just a subset for now

    private String registrar;
    private String username;
    private String password;

}
