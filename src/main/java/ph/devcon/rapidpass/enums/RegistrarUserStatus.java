package ph.devcon.rapidpass.enums;

import ph.devcon.rapidpass.entities.RegistrarUser;

public enum RegistrarUserStatus {
    /**
     * <p>
     * Refers to a {@link RegistrarUser} who has its authentication details configured (password, username) and
     * is allowed to log in already.
     * </p>
     */
    ACTIVE,

    /**
     * <p>
     *     Refers to a {@link RegistrarUser} who has their email sent out already.
     * </p>
     */
    PENDING,

    /**
     * <p>
     *      Refers to a {@link RegistrarUser} who was registered using batch upload, and does not have their password
     * configured yet.
     * </p>
     * <p>
     *      This means that an email has not yet been sent to them yet, and the email notifier has not yet picked up
     *      this registrar user before they are emailed to have them activate and configure their password.
     * </p>
     */
    INACTIVE
}
