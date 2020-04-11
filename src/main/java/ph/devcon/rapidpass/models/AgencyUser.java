package ph.devcon.rapidpass.models;

import lombok.Builder;
import lombok.Data;
import ph.devcon.rapidpass.entities.Registrar;
import ph.devcon.rapidpass.entities.RegistrarUser;
import ph.devcon.rapidpass.enums.RegistrarUserSource;

/**
 * This data model maps out to the database table {@link RegistrarUser}.
 */
@Data
@Builder
public class AgencyUser {

    /**
     * The {@link Registrar} or agency that this user is associated with.
     */
    private String registrar;

    /**
     * The user's personal username.
     */
    private String username;

    /**
     * The user's personal password.
     */
    private String password;

    /**
     * The user's email.
     */
    private String email;

    /**
     * Can be "INDIVIDUAL" or "BATCH_UPLOAD";
     *
     * Please see {@link RegistrarUserSource}.
     */
    private String source;


    private String firstName;

    private String lastName;

    public static AgencyUser buildFrom(RegistrarUser registryUser) {
        return AgencyUser.builder()
                .firstName(registryUser.getFirstName())
                .lastName(registryUser.getLastName())
                .email(registryUser.getEmail())
                .username(registryUser.getUsername())
                .password(registryUser.getPassword())
                .registrar(registryUser.getRegistrarId().getShortName())
                .build();
    }

    /**
     * If the source value of this AgencyUser is null, then it defaults to an individual (single) registration.
     * @return true if the AgencyUser was generated from batch upload.
     */
    public boolean isBatchUpload() {
        return RegistrarUserSource.BATCH_UPLOAD.toString().equalsIgnoreCase(this.source);
    }

    public boolean isIndividualRegistration() {
        return !isBatchUpload();
    }
}
