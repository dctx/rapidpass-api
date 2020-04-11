package ph.devcon.rapidpass.services;

import com.google.common.collect.ImmutableList;
import org.apache.commons.codec.DecoderException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ph.devcon.rapidpass.utilities.CryptUtils.passwordHash;

class ApproverAuthServiceTest {

    private RegistrarRepository registrarRepository;
    private RegistrarUserRepository registrarUserRepository;
    private JwtSecretsConfig jwtSecretsConfig;

    private ApproverAuthService approverAuthService;

    @BeforeEach
    void before() {
        this.registrarUserRepository = Mockito.mock(RegistrarUserRepository.class);
        this.registrarRepository = Mockito.mock(RegistrarRepository.class);
        this.jwtSecretsConfig = Mockito.mock(JwtSecretsConfig.class);
        this.approverAuthService = new ApproverAuthService(registrarUserRepository, registrarRepository, jwtSecretsConfig);
    }

    @Test
    void testCreateNewUser() {
        final String registrar = "DOH";
        final String username = "username";
        final String password = "password";

        // has registrar
        final Registrar mockRegistrar = new Registrar();
        mockRegistrar.setShortName(registrar);
        mockRegistrar.setId(5);

        final RegistrarUser registrarUser = new RegistrarUser();
        registrarUser.setRegistrarId(mockRegistrar);
        registrarUser.setUsername(username);
        registrarUser.setPassword(password);

        final AgencyUser agencyUser = AgencyUser.buildFrom(registrarUser);

        when(this.registrarRepository.findByShortName(anyString())).thenReturn(mockRegistrar);
        // no existing user
        when(this.registrarUserRepository.findByUsername(anyString())).thenReturn(
                ImmutableList.of()
        );

        try {
            this.approverAuthService.createAgencyCredentials(agencyUser);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            fail(e);
        }

        final ArgumentCaptor<RegistrarUser> argCaptor = ArgumentCaptor.forClass(RegistrarUser.class);
        verify(this.registrarUserRepository).saveAndFlush(argCaptor.capture());

        final RegistrarUser capturedEntity = argCaptor.getValue();
        assertEquals(capturedEntity.getUsername(), username);
        assertEquals(capturedEntity.getRegistrarId(), mockRegistrar);
        assertNotNull(capturedEntity.getPassword(), password);
        assertNotEquals(capturedEntity.getPassword(), "");
        assertNotEquals(capturedEntity.getPassword(), password);
    }

    @Test
    void testCreateExistingUser() {
        final String registrar = "DOH";
        final String username = "username";
        final String password = "password";

        // has registrar
        final Registrar mockRegistrar = new Registrar();
        mockRegistrar.setShortName(registrar);
        mockRegistrar.setId(5);

        final RegistrarUser registrarUser = new RegistrarUser();
        registrarUser.setRegistrarId(mockRegistrar);
        registrarUser.setUsername(username);
        registrarUser.setPassword(password);

        final AgencyUser agencyUser = AgencyUser.buildFrom(registrarUser);

        // has registrar
        when(this.registrarRepository.findByShortName(anyString())).thenReturn(mockRegistrar);

        final List<RegistrarUser> existingRegistrarUsers = new ArrayList<>();
        existingRegistrarUsers.add(registrarUser);

        when(this.registrarUserRepository.findByUsername(anyString())).thenReturn(existingRegistrarUsers);

        boolean captured = false;
        try {
            this.approverAuthService.createAgencyCredentials(agencyUser);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            fail(e);
        } catch (IllegalArgumentException e) {
            captured = true;
        }

        assertTrue(captured, "should throw illegalargument");
    }

    @Test
    void testCreateUsernameAlreadyExists() {
        final String registrar = "DOH";
        final String username = "username";
        final String password = "password";

        // has registrar
        final Registrar mockRegistrar = new Registrar();
        mockRegistrar.setShortName(registrar);
        mockRegistrar.setId(5);

        final RegistrarUser registrarUser = new RegistrarUser();
        registrarUser.setRegistrarId(mockRegistrar);
        registrarUser.setUsername(username);
        registrarUser.setPassword(password);

        final AgencyUser agencyUser = AgencyUser.buildFrom(registrarUser);

        when(this.registrarRepository.findByShortName(anyString())).thenReturn(mockRegistrar);
        // no existing user
        when(this.registrarUserRepository.findByUsername(anyString())).thenReturn(
                ImmutableList.of(registrarUser)
        );

        assertThrows(IllegalArgumentException.class, () -> {
            this.approverAuthService.createAgencyCredentials(agencyUser);
        });

    }

    @Test
    void testCreateUserWithWrongRegistrar() {
        final String registrar = "DOH";
        final String username = "username";
        final String password = "password";
        final AgencyUser user = AgencyUser.builder()
                .registrar(registrar)
                .username(username)
                .password(password)
                .build();

        // has no registrar
        when(this.registrarRepository.findByShortName(anyString())).thenReturn(null);

        boolean captured = false;
        try {
            this.approverAuthService.createAgencyCredentials(user);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            fail(e);
        } catch (IllegalArgumentException e) {
            captured = true;
        }

        assertTrue(captured, "should throw illegalargument");
    }

    @Test
    void testCorrectLogin() {
        final String username = "username";
        final String password = "password";
        final String jwtSecret = "supersecret";
        String hashedPassword = "";
        try {
            hashedPassword = passwordHash(password);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            fail(e);
        }

        // has existing user
        final RegistrarUser existingUser = new RegistrarUser();
        existingUser.setStatus("active");
        existingUser.setUsername(username);
        existingUser.setPassword(hashedPassword);
        final List<RegistrarUser> users = new ArrayList<>();
        users.add(existingUser);
        when(this.registrarUserRepository.findByUsername(username)).thenReturn(users);
        when(this.jwtSecretsConfig.findGroupSecret(anyString())).thenReturn(jwtSecret);

        try {
            final AgencyAuth login = this.approverAuthService.login(username, password);
            assertNotNull(login);
            final String accessCode = login.getAccessCode();
            assertNotNull(accessCode);

            final Map<String, Object> claims = JwtGenerator.claimsToMap(accessCode);
            final Boolean validated = JwtGenerator.validateToken(accessCode, claims, jwtSecret);
            assertTrue(validated);
        } catch (NoSuchAlgorithmException | DecoderException | InvalidKeySpecException e) {
            fail(e);
        }
    }

    @Test
    void testIncorrectLogin() {
        final String username = "username";
        final String password = "password";
        String hashedPassword = "";
        try {
            hashedPassword = passwordHash(password);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            fail(e);
        }

        // has existing user
        final RegistrarUser existingUser = new RegistrarUser();
        existingUser.setStatus("active");
        existingUser.setUsername(username);
        existingUser.setPassword(hashedPassword);
        final List<RegistrarUser> users = new ArrayList<>();
        users.add(existingUser);
        when(this.registrarUserRepository.findByUsername(username)).thenReturn(users);

        try {
            final AgencyAuth login = this.approverAuthService.login(username, "a different password");
            Assertions.assertNull(login);
        } catch (NoSuchAlgorithmException | DecoderException | InvalidKeySpecException e) {
            fail(e);
        }
    }

    @Test
    void testNonExistentUserLogin() {
        final String username = "username";
        final String password = "password";

        // has no user
        when(this.registrarUserRepository.findByUsername(username)).thenReturn(null);

        try {
            final AgencyAuth login = this.approverAuthService.login(username, password);
            Assertions.assertNull(login);
        } catch (NoSuchAlgorithmException | DecoderException | InvalidKeySpecException e) {
            fail(e);
        }
    }

    @Test
    void testChangePasswordOk() {
        final String oldPassword = "password";
        String oldPasswordHash = "'";
        try {
            oldPasswordHash = CryptUtils.passwordHash(oldPassword);
        } catch (NoSuchAlgorithmException e) {
            fail(e);
        } catch (InvalidKeySpecException e) {
            fail(e);
        }

        final String username = "username";
        final String password = "password";

        final RegistrarUser registrarUser = new RegistrarUser();
        registrarUser.setUsername(username);
        registrarUser.setPassword(oldPasswordHash);
        registrarUser.setStatus("active");

        // has no user
        when(this.registrarUserRepository.findByUsername(username)).thenReturn(Arrays.asList(registrarUser));

        try {
            this.approverAuthService.changePassword(username, oldPassword, password);
        } catch (NoSuchAlgorithmException | DecoderException | InvalidKeySpecException e) {
            fail(e);
        }
    }

    @Test
    void testChangePasswordNotOk() {
        final String oldPassword = "password";
        String oldPasswordHash = "'";
        try {
            oldPasswordHash = CryptUtils.passwordHash(oldPassword);
        } catch (NoSuchAlgorithmException e) {
            fail(e);
        } catch (InvalidKeySpecException e) {
            fail(e);
        }

        final String username = "username";
        final String password = "password";

        final RegistrarUser registrarUser = new RegistrarUser();
        registrarUser.setUsername(username);
        registrarUser.setPassword(oldPasswordHash);
        registrarUser.setStatus("active");

        // has no user
        when(this.registrarUserRepository.findByUsername(username)).thenReturn(Arrays.asList(registrarUser));

        boolean exceptionThrown = false;
        try {
            this.approverAuthService.changePassword(username, "not the old password", password);
        } catch (NoSuchAlgorithmException | DecoderException | InvalidKeySpecException e) {
            fail(e);
        } catch (IllegalArgumentException e) {
            exceptionThrown = true;
        }

        assertTrue(exceptionThrown);
    }

    @Test
    void testActivationSuccess() {
        final String username = "user@user.com";
        final String password = "password";
        final String activationCode = "activationCode";

        final RegistrarUser registrarUser = new RegistrarUser();
        registrarUser.setUsername(username);
        registrarUser.setStatus("pending");
        registrarUser.setAccessKey(activationCode);

        String hashedPassword = "";
        try {
            hashedPassword = passwordHash(password);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            fail(e);
        }

        // has pending user
        when(this.registrarUserRepository.findByUsername(username)).thenReturn(Arrays.asList(registrarUser));

        try {
            this.approverAuthService.activateUser(username, password, activationCode);
        } catch (Exception e) {
            fail(e);
        }

        final ArgumentCaptor<RegistrarUser> argCaptor = ArgumentCaptor.forClass(RegistrarUser.class);
        verify(this.registrarUserRepository).save(argCaptor.capture());

        final RegistrarUser capturedEntity = argCaptor.getValue();
        assertEquals(capturedEntity.getUsername(), username);
        assertNotNull(capturedEntity.getPassword(), password);
        assertNull(capturedEntity.getAccessKey());
        assertNotEquals(capturedEntity.getPassword(), hashedPassword);
    }

    @Test
    void testActivationFailNoUsername() {
        final String username = "";
        final String password = "password";
        final String activationCode = "activationCode";

        boolean caught = false;
        try {
            this.approverAuthService.activateUser(username, password, activationCode);
        } catch (IllegalArgumentException e) {
            caught = true;
        } catch (Exception e) {
            fail(e);
        }

        assertTrue(caught);
   }

    @Test
    void testActivationFailNoPassword() {
        final String username = "username";
        final String password = "";
        final String activationCode = "activationCode";

        boolean caught = false;
        try {
            this.approverAuthService.activateUser(username, password, activationCode);
        } catch (IllegalArgumentException e) {
            caught = true;
        } catch (Exception e) {
            fail(e);
        }

        assertTrue(caught);
    }

    @Test
    void testActivationWrongActivationKey() {
        final String username = "user@user.com";
        final String password = "password";
        final String activationCode = "activationCode";

        final RegistrarUser registrarUser = new RegistrarUser();
        registrarUser.setUsername(username);
        registrarUser.setStatus("pending");
        registrarUser.setAccessKey(activationCode);

        boolean caught = false;
        // has pending user
        when(this.registrarUserRepository.findByUsername(username)).thenReturn(Arrays.asList(registrarUser));

        try {
            this.approverAuthService.activateUser(username, password, "wrong activation key");
        } catch (IllegalStateException e) {
            caught = true;
        } catch (Exception e) {
            fail(e);
        }

        assertTrue(caught);

    }

    @Test
    void testActivationUserDoesNotExists() {
        final String username = "user@user.com";

        boolean caught = false;
        // has pending user
        when(this.registrarUserRepository.findByUsername(username)).thenReturn(Arrays.asList());

        try {
            this.approverAuthService.activateUser(username, "any", "any");
        } catch (IllegalStateException e) {
            caught = true;
        } catch (Exception e) {
            fail(e);
        }

        assertTrue(caught);
    }

    @Test
    void testExistingUserActive() {
        final String username = "user@user.com";
        final RegistrarUser registrarUser = new RegistrarUser();
        registrarUser.setUsername(username);
        registrarUser.setStatus("active");
        when(this.registrarUserRepository.findByUsername(username)).thenReturn(Arrays.asList(registrarUser));
        assertTrue(this.approverAuthService.isActive(username));
    }

    @Test
    void testExistingUserNotActive() {
        final String username = "user@user.com";
        final RegistrarUser registrarUser = new RegistrarUser();
        registrarUser.setUsername(username);
        registrarUser.setStatus("pending");
        when(this.registrarUserRepository.findByUsername(username)).thenReturn(Arrays.asList(registrarUser));
        assertFalse(this.approverAuthService.isActive(username));
    }

    @Test
    void testNotExistingUserNotActive() {
        final String username = "user@user.com";
        when(this.registrarUserRepository.findByUsername(username)).thenReturn(Arrays.asList());
        assertFalse(this.approverAuthService.isActive(username));
    }


}
