package ph.devcon.rapidpass.services;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.DecoderException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ph.devcon.rapidpass.config.JwtSecretsConfig;
import ph.devcon.rapidpass.entities.Registrar;
import ph.devcon.rapidpass.entities.RegistrarUser;
import ph.devcon.rapidpass.enums.RegistrarUserStatus;
import ph.devcon.rapidpass.kafka.RegistrarUserRequestProducer;
import ph.devcon.rapidpass.models.AgencyAuth;
import ph.devcon.rapidpass.models.AgencyUser;
import ph.devcon.rapidpass.repositories.RegistrarRepository;
import ph.devcon.rapidpass.repositories.RegistrarUserRepository;
import ph.devcon.rapidpass.utilities.CryptUtils;
import ph.devcon.rapidpass.utilities.JwtGenerator;
import ph.devcon.rapidpass.validators.StandardDataBindingValidation;
import ph.devcon.rapidpass.validators.entities.agency_user.NewAgencyUserValidator;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static ph.devcon.rapidpass.utilities.CryptUtils.passwordCompare;
import static ph.devcon.rapidpass.utilities.CryptUtils.passwordHash;

/**
 * ApproverAuthService handles authentications for approvers
 */
@Service
@Slf4j
public class ApproverAuthService {

    private static final String GROUP_NAME = "approver";

    private final RegistrarUserRepository registrarUserRepository;
    private final RegistrarRepository registrarRepository;
    private final JwtSecretsConfig jwtSecretsConfig;
    private final RegistrarUserRequestProducer registrarUserRequestProducer;

    @Value("${kafka.enabled:false}")
    protected boolean isKafaEnabled;

    @Autowired
    public ApproverAuthService(final RegistrarUserRepository registrarUserRepository,
                               final RegistrarRepository registrarRepository,
                               final JwtSecretsConfig jwtSecretsConfig,
                               final RegistrarUserRequestProducer registrarUserRequestProducer) {
        this.registrarUserRepository = registrarUserRepository;
        this.registrarRepository = registrarRepository;
        this.jwtSecretsConfig = jwtSecretsConfig;
        this.registrarUserRequestProducer = registrarUserRequestProducer;
    }

    /**
     * The main login function for approvers. This returns null if credentials is invalid.
     *
     * @param username the username
     * @param password the password
     * @return AgencyAuth or null if invalid
     * @throws InvalidKeySpecException this is returned when the hashing algorithm fails. This is an illegal state
     * @throws NoSuchAlgorithmException this is returned when the hashing algorithm fails. This is an illegal state
     * @throws DecoderException this is returned when the hashing algorithm fails. This is an illegal state
     */
    public final AgencyAuth login(final String username, final String password) throws InvalidKeySpecException, NoSuchAlgorithmException, DecoderException {
        final RegistrarUser registrarUser = this.registrarUserRepository.findByUsername(username);
        if (registrarUser == null) {
            log.warn("unregistered user attempted to login");
            return null;
        }
        final String hashedPassword = registrarUser.getPassword();

        final boolean isPasswordCorrect = passwordCompare(hashedPassword, password);
        if (isPasswordCorrect) {
            final Map<String, Object> claims = new HashMap<>();
            claims.put("sub", username);
            claims.put("group", GROUP_NAME);
            // TODO: hardcoded expiry at 1 day
            claims.put("exp", LocalDateTime.now().plus(1, ChronoUnit.DAYS));
            final String token = JwtGenerator.generateToken(claims, this.jwtSecretsConfig.findGroupSecret(GROUP_NAME));
            return AgencyAuth.builder().accessCode(token).build();
        }
        return null;
    }

    /**
     * Create a new approver account
     *
     * @param user the approver details
     * @throws IllegalArgumentException this is returned when the hashing algorithm fails. This is an illegal state
     * @throws InvalidKeySpecException this is returned when the hashing algorithm fails. This is an illegal state
     * @throws NoSuchAlgorithmException this is returned when the hashing algorithm fails. This is an illegal state
     */
    public final RegistrarUser createAgencyCredentials(final AgencyUser user) throws IllegalArgumentException, InvalidKeySpecException, NoSuchAlgorithmException {

        if (user.isBatchUpload()) {
            NewAgencyUserValidator validator = new NewAgencyUserValidator(this.registrarUserRepository, this.registrarRepository);
            StandardDataBindingValidation dataValidator = new StandardDataBindingValidation(validator);
            dataValidator.validate(user);
        }

        Registrar registrar = this.registrarRepository.findByShortName(user.getRegistrar());

        final RegistrarUser registrarUser = new RegistrarUser();

        registrarUser.setRegistrarId(registrar);
        registrarUser.setUsername(user.getUsername());


        if (user.isBatchUpload()) {
            registrarUser.setFirstName(user.getFirstName());
            registrarUser.setLastName(user.getLastName());
            registrar.setEmail(user.getEmail());
            registrarUser.setStatus(RegistrarUserStatus.INACTIVE.toString());

            // Uses a v4 UUID.
            String randomUniqueActivationCode = UUID.randomUUID().toString();
            registrarUser.setAccessKey(randomUniqueActivationCode);

            if (isKafaEnabled)
                registrarUserRequestProducer.sendMessage(registrarUser.getUsername(), registrarUser);

        } else if (user.isIndividualRegistration()) {
            final String hashedPassword = passwordHash(user.getPassword());
            registrarUser.setPassword(hashedPassword);
            registrarUser.setStatus(RegistrarUserStatus.ACTIVE.toString());
        }

        registrarUserRepository.saveAndFlush(registrarUser);
        return registrarUser;
    }

    /**
     * This changes the password of an approver.
     *
     * FIXME: will not be used.
     *
     * @param username the username
     * @param oldPassword the old password
     * @param newPassword the new password
     * @throws InvalidKeySpecException this is returned when the hashing algorithm fails. This is an illegal state
     * @throws NoSuchAlgorithmException this is returned when the hashing algorithm fails. This is an illegal state
     * @throws DecoderException this is returned when the hashing algorithm fails. This is an illegal state
     */
    public final void changePassword(final String username, final String oldPassword, final String newPassword) throws InvalidKeySpecException, NoSuchAlgorithmException, DecoderException {
        final RegistrarUser registrarUser = this.registrarUserRepository.findByUsername(username);
        if (registrarUser == null) {
            throw new IllegalArgumentException("cannot change password if user does not exists");
        }
        final String hashedPassword = registrarUser.getPassword();
        final boolean isOldPasswordCorrect = passwordCompare(hashedPassword, oldPassword);
        if (!isOldPasswordCorrect) {
            throw new IllegalArgumentException("old password does not match!");
        }
        final String newHashedPassword = CryptUtils.passwordHash(newPassword);
        registrarUser.setPassword(newHashedPassword);
        this.registrarUserRepository.save(registrarUser);
    }

    /**
     * Activate an existing user that is of status=pending.
     *
     * @param username the username of the user to activate
     * @param password the password to set for the user
     * @param activationKey the activationKey to activate the user.
     * @throws InvalidKeySpecException this is returned when the hashing algorithm fails. This is an illegal state
     * @throws NoSuchAlgorithmException this is returned when the hashing algorithm fails. This is an illegal state
     */
    public final void activateUser(final String username, final String password, final String activationKey) throws InvalidKeySpecException, NoSuchAlgorithmException {
        if (StringUtils.isEmpty(username)) {
            throw new IllegalArgumentException("username must not be empty");
        }
        if (StringUtils.isEmpty(password)) {
            throw new IllegalArgumentException("password must not be empty");
        }
        if (StringUtils.isEmpty(activationKey)) {
            throw new IllegalArgumentException("activationKey must not be empty");
        }
        final RegistrarUser registrarUser = this.registrarUserRepository.findByUsername(username);
        if (registrarUser == null) {
            throw new IllegalStateException("cannot activate a non existent user");
        } else if (!"pending".equals(registrarUser.getStatus())) {
            throw new IllegalStateException("cannot activate if user is not pending");
        }
        if (!activationKey.equals(registrarUser.getAccessKey())) {
            throw new IllegalStateException("activation code is not correct");
        }
        final String hashedPassword = passwordHash(password);
        registrarUser.setPassword(hashedPassword);
        registrarUser.setAccessKey(null);
        registrarUser.setStatus("active");
        this.registrarUserRepository.save(registrarUser);
    }

    public final boolean isActive(final String username) {
        if (StringUtils.isEmpty(username)) {
            throw new IllegalArgumentException("username must not be empty");
        }
        final RegistrarUser registrarUser = this.registrarUserRepository.findByUsername(username);
        if (registrarUser == null) {
            return false;
        }
        return "active".equals(registrarUser.getStatus());
    }
}
