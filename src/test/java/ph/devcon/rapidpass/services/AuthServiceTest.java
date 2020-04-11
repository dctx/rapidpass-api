package ph.devcon.rapidpass.services;

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
import ph.devcon.rapidpass.utilities.JwtGenerator;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ph.devcon.rapidpass.utilities.CryptUtils.passwordHash;

class AuthServiceTest {

    private RegistrarRepository registrarRepository;
    private RegistrarUserRepository registrarUserRepository;
    private JwtSecretsConfig jwtSecretsConfig;

    private ApproverAuthService authService;

    @BeforeEach
    void before() {
        this.registrarUserRepository = Mockito.mock(RegistrarUserRepository.class);
        this.registrarRepository = Mockito.mock(RegistrarRepository.class);
        this.jwtSecretsConfig = Mockito.mock(JwtSecretsConfig.class);
        this.authService = new ApproverAuthService(registrarUserRepository, registrarRepository, jwtSecretsConfig);
    }

    @Test
    void testCreateNewUser() {
        final String username = "username";
        final String password = "password";

        final AgencyUser user = AgencyUser.builder()
                .registrar("DOH")
                .username(username)
                .password(password)
                .build();

        // has registrar
        final Registrar registrar = Registrar.builder()
                .shortName("DOH")
                .id(1)
                .build();

        when(this.registrarRepository.findByShortName(anyString())).thenReturn(registrar);
        // no existing user
        when(this.registrarUserRepository.findByUsername(anyString())).thenReturn(Collections.emptyList());

        try {
            this.authService.createAgencyCredentials(user);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            fail(e);
        }

        final ArgumentCaptor<RegistrarUser> argCaptor = ArgumentCaptor.forClass(RegistrarUser.class);
        verify(this.registrarUserRepository).saveAndFlush(argCaptor.capture());

        final RegistrarUser capturedEntity = argCaptor.getValue();
        assertEquals(capturedEntity.getUsername(), username);
        assertEquals(capturedEntity.getRegistrarId(), registrar);
        assertNotNull(capturedEntity.getPassword(), password);
        assertNotEquals(capturedEntity.getPassword(), "");
        assertNotEquals(capturedEntity.getPassword(), password);
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
            final AgencyAuth login = this.authService.login(username, password);
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
            final AgencyAuth login = this.authService.login(username, "a different password");
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
            final AgencyAuth login = this.authService.login(username, password);
            Assertions.assertNull(login);
        } catch (NoSuchAlgorithmException | DecoderException | InvalidKeySpecException e) {
            fail(e);
        }
    }

}
