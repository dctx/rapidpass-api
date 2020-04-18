/*
 * Copyright (c) 2020.  DevConnect Philippines, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and limitations under the License.
 */

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
import ph.devcon.rapidpass.enums.RegistrarUserSource;
import ph.devcon.rapidpass.kafka.RegistrarUserRequestProducer;
import ph.devcon.rapidpass.models.AgencyAuth;
import ph.devcon.rapidpass.models.AgencyUser;
import ph.devcon.rapidpass.repositories.RegistrarRepository;
import ph.devcon.rapidpass.repositories.RegistrarUserRepository;
import ph.devcon.rapidpass.utilities.CryptUtils;
import ph.devcon.rapidpass.utilities.JwtGenerator;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
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
    private RegistrarUserRequestProducer registrarUserRequestProducer;

    private ApproverAuthService approverAuthService;

    @BeforeEach
    void before() {
        this.registrarUserRepository = Mockito.mock(RegistrarUserRepository.class);
        this.registrarRepository = Mockito.mock(RegistrarRepository.class);
        this.jwtSecretsConfig = Mockito.mock(JwtSecretsConfig.class);
        this.registrarUserRequestProducer = Mockito.mock(RegistrarUserRequestProducer.class);
        this.approverAuthService = new ApproverAuthService(registrarUserRepository, registrarRepository, jwtSecretsConfig, this.registrarUserRequestProducer);
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
        when(this.registrarUserRepository.findByUsername(anyString())).thenReturn(null);

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
        final Registrar registrarId = new Registrar();
        when(this.registrarRepository.findByShortName(anyString())).thenReturn(registrarId);
        // has existing user
        final RegistrarUser existingUser = new RegistrarUser();
//        final List<RegistrarUser> users = new ArrayList<>();
//        users.add(existingUser);
        when(this.registrarUserRepository.findByUsername(anyString())).thenReturn(existingUser);

        boolean captured = false;
        try {
            this.approverAuthService.createAgencyCredentials(agencyUser);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            fail(e);
        } catch (IllegalArgumentException e) {
            captured = true;
        }

        assertTrue(captured, "should throw illegal argument");
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
        agencyUser.setSource(RegistrarUserSource.ONLINE.name());

        when(this.registrarRepository.findByShortName(anyString())).thenReturn(mockRegistrar);
        // no existing user
        when(this.registrarUserRepository.findByUsername(anyString())).thenReturn(registrarUser);

        assertThrows(IllegalArgumentException.class, () -> {
            this.approverAuthService.createAgencyCredentials(agencyUser);
        });

    }

    @Test
    void testCreateUserWithWrongRegistrar() {
        final String registrar = "DOH";
        final String username = "username";
        final String password = "password";
        final AgencyUser user = new AgencyUser();
        user.setRegistrar(registrar);
        user.setUsername(username);
        user.setPassword(password);

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
//        final List<RegistrarUser> users = new ArrayList<>();
//        users.add(existingUser);
        when(this.registrarUserRepository.findByUsername(username)).thenReturn(existingUser);
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
//        final List<RegistrarUser> users = new ArrayList<>();
//        users.add(existingUser);
        when(this.registrarUserRepository.findByUsername(username)).thenReturn(existingUser);

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
        when(this.registrarUserRepository.findByUsername(username)).thenReturn(registrarUser);

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
        when(this.registrarUserRepository.findByUsername(username)).thenReturn(registrarUser);

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
        when(this.registrarUserRepository.findByUsername(username)).thenReturn(registrarUser);

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
        when(this.registrarUserRepository.findByUsername(username)).thenReturn(registrarUser);

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
        when(this.registrarUserRepository.findByUsername(username)).thenReturn(null);

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
        when(this.registrarUserRepository.findByUsername(username)).thenReturn(registrarUser);
        assertTrue(this.approverAuthService.isActive(username));
    }

    @Test
    void testExistingUserNotActive() {
        final String username = "user@user.com";
        final RegistrarUser registrarUser = new RegistrarUser();
        registrarUser.setUsername(username);
        registrarUser.setStatus("pending");
        when(this.registrarUserRepository.findByUsername(username)).thenReturn(registrarUser);
        assertFalse(this.approverAuthService.isActive(username));
    }

    @Test
    void testNotExistingUserNotActive() {
        final String username = "user@user.com";
        when(this.registrarUserRepository.findByUsername(username)).thenReturn(null);
        assertFalse(this.approverAuthService.isActive(username));
    }


}
