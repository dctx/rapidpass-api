package ph.devcon.rapidpass.enums;

public enum RegistrarUserStatus {
    // Refers to a RegistrarUser who is allowed to log in already. This means that their password is configured.
    ACTIVE,

    // Refers to a RegistrarUser who was registered using batch upload, and does not have their password configured yet.
    // This means that an email was sent to them first, to have them activate and configure their password.
    INACTIVE
}
