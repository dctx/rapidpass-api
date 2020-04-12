package ph.devcon.rapidpass.models;

import lombok.Data;
import ph.devcon.rapidpass.entities.Registrar;
import ph.devcon.rapidpass.entities.RegistrarUser;
import ph.devcon.rapidpass.enums.RegistrarUserSource;

import javax.validation.constraints.NotEmpty;

/**
 * This data model maps out to the database table {@link RegistrarUser}.
 */
@Data
public class AgencyUser {

    /**
     * The short name of the {@link Registrar} that this user is associated with.
     */
    @NotEmpty
    private String registrar;

    /**
     * The user's personal username.
     */
    @NotEmpty
    private String username;

    /**
     * The user's personal password.
     */
    @NotEmpty
    private String password;

    /**
     * The user's email.
     */
    @NotEmpty
    private String email;

    /**
     * Can be "INDIVIDUAL" or "BATCH_UPLOAD";
     *
     * Please see {@link RegistrarUserSource}.
     */
    @NotEmpty
    private String source;

    /**
     * The user's first name.
     */
    @NotEmpty
    private String firstName;

    /**
     * The user's last name.
     */
    @NotEmpty
    private String lastName;

    public static AgencyUser buildFrom(RegistrarUser registryUser) {
        AgencyUser agencyUser = new AgencyUser();
        agencyUser.setFirstName(registryUser.getFirstName());
        agencyUser.setLastName(registryUser.getLastName());
        agencyUser.setEmail(registryUser.getEmail());
        agencyUser.setUsername(registryUser.getUsername());
        agencyUser.setPassword(registryUser.getPassword());
        agencyUser.setRegistrar(registryUser.getRegistrarId().getShortName());
        return agencyUser;
    }

    /**
     * If the source value of this {@link AgencyUser} is null, then it defaults to an single ONLINE registration.
     * @return true if the {@link AgencyUser} <code>source</code> is equal to <code>BULK</code>.
     */
    public boolean isBatchUpload() {
        return RegistrarUserSource.BULK.toString().equalsIgnoreCase(this.source);
    }

    public boolean isIndividualRegistration() {
        return !isBatchUpload();
    }
}
