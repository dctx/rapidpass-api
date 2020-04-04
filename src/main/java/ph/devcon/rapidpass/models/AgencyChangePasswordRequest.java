package ph.devcon.rapidpass.models;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AgencyChangePasswordRequest {

    private String oldPassword;
    private String newPassword;

}
