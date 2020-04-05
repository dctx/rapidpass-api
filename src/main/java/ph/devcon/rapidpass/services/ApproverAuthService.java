package ph.devcon.rapidpass.services;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.DecoderException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import ph.devcon.rapidpass.config.JwtSecretsConfig;
import ph.devcon.rapidpass.entities.Registrar;
import ph.devcon.rapidpass.entities.RegistrarUser;
import ph.devcon.rapidpass.models.AgencyAuth;
import ph.devcon.rapidpass.models.AgencyUser;
import ph.devcon.rapidpass.repositories.RegistrarRepository;
import ph.devcon.rapidpass.repositories.RegistrarUserRepository;
import ph.devcon.rapidpass.utilities.CryptUtils;
import ph.devcon.rapidpass.utilities.JwtGenerator;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Autowired
    public ApproverAuthService(final RegistrarUserRepository registrarUserRepository,
                               final RegistrarRepository registrarRepository,
                               final JwtSecretsConfig jwtSecretsConfig) {
        this.registrarUserRepository = registrarUserRepository;
        this.registrarRepository = registrarRepository;
        this.jwtSecretsConfig = jwtSecretsConfig;
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
        final List<RegistrarUser> registrarUsers = this.registrarUserRepository.findByUsername(username);
        if (CollectionUtils.isEmpty(registrarUsers)) {
            log.warn("unregistered user attempted to login");
            return null;
        }
        RegistrarUser registrarUser = registrarUsers.get(0);
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
    public final void createAgencyCredentials(final AgencyUser user) throws IllegalArgumentException, InvalidKeySpecException, NoSuchAlgorithmException {
        final String registrarShortName = user.getRegistrar();
        final Registrar registrar = registrarRepository.findByShortName(registrarShortName);
        if (registrar == null) {
            log.error("the registrar provided does not exist");
            throw new IllegalArgumentException("unable to find registrar for user");
        }

        final List<RegistrarUser> users = this.registrarUserRepository.findByUsername(user.getUsername());
        if (!CollectionUtils.isEmpty(users)) {
            log.error("user already exists");
            throw new IllegalArgumentException("user already exists");
        }

        final RegistrarUser registrarUser = new RegistrarUser();
        registrarUser.setRegistrarId(registrar);
        registrarUser.setUsername(user.getUsername());
        final String hashedPassword = passwordHash(user.getPassword());
        registrarUser.setPassword(hashedPassword);
        registrarUser.setStatus("active");

        registrarUserRepository.save(registrarUser);
    }

    /**
     * This changes the password of an approver.
     *
     * FIXME: Not yet used and not yet tested.
     *
     * @param username the username
     * @param oldPassword the old password
     * @param newPassword the new password
     * @throws InvalidKeySpecException this is returned when the hashing algorithm fails. This is an illegal state
     * @throws NoSuchAlgorithmException this is returned when the hashing algorithm fails. This is an illegal state
     * @throws DecoderException this is returned when the hashing algorithm fails. This is an illegal state
     */
    public final void changePassword(final String username, final String oldPassword, final String newPassword) throws InvalidKeySpecException, NoSuchAlgorithmException, DecoderException {
        final List<RegistrarUser> registrarUsers = this.registrarUserRepository.findByUsername(username);
        if (CollectionUtils.isEmpty(registrarUsers)) {
            throw new IllegalArgumentException("cannot change password if user does not exists");
        }
        final RegistrarUser registrarUser = registrarUsers.get(0);
        final String hashedPassword = registrarUser.getPassword();
        final boolean isOldPasswordCorrect = passwordCompare(hashedPassword, oldPassword);
        if (!isOldPasswordCorrect) {
            throw new IllegalArgumentException("old password does not match!");
        }
        final String newHashedPassword = CryptUtils.passwordHash(newPassword);
        registrarUser.setPassword(newHashedPassword);
        this.registrarUserRepository.save(registrarUser);
    }
}
