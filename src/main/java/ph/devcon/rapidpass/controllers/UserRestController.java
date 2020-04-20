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

package ph.devcon.rapidpass.controllers;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.common.collect.ImmutableMap;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.DecoderException;
import org.apache.kafka.common.errors.AuthorizationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ph.devcon.rapidpass.api.models.RegistrarUserChangePasswordRequest;
import ph.devcon.rapidpass.exceptions.AccountLockedException;
import ph.devcon.rapidpass.models.AgencyAuth;
import ph.devcon.rapidpass.models.Login;
import ph.devcon.rapidpass.models.UserActivationRequest;
import ph.devcon.rapidpass.services.ApproverAuthService;
import ph.devcon.rapidpass.services.LookupTableService;
import ph.devcon.rapidpass.utilities.JwtGenerator;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserRestController {

    private final LookupTableService lookupTableService;
    private final ApproverAuthService approverAuthService;

//    public UserRestController(final ApproverAuthService approverAuthService) {
//        this.approverAuthService = approverAuthService;
//    }

    @PostMapping("/auth")
    public ResponseEntity<AgencyAuth> login(HttpServletResponse response, @RequestBody Login login) throws AccountLockedException {
        try {
            final AgencyAuth auth = this.approverAuthService.login(login.getUsername(), login.getPassword());
            if (auth == null) {
                log.debug(" wrong password/username {}", login.getUsername());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            DecodedJWT decodedJWT = JwtGenerator.decodedJWT(auth.getAccessCode());

            String xsrfToken = decodedJWT.getClaim("xsrfToken").asString();
            Cookie cookie = new Cookie("xsrfToken", xsrfToken);
            cookie.setMaxAge(86400); // 1 day constant
            response.addCookie(cookie);

            return ResponseEntity.ok().body(auth);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException | DecoderException e) {
            log.error("hashing function error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (AccountLockedException e) {
            throw e;
        } catch (Exception e) {
            log.error("something went wrong", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{username}/activate")
    public ResponseEntity<?> activateUser(
            @PathVariable("username") final String username,
            @RequestBody final UserActivationRequest userActivationRequest) {
        try {
            this.approverAuthService.activateUser(username, userActivationRequest.getPassword(), userActivationRequest.getActivationCode());
            log.info("activation for {} successful", username);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (final IllegalArgumentException e) {
            log.warn("activation for {} failed. a parameter is invalid", username);
            return ResponseEntity.badRequest().build();
        } catch (final IllegalStateException e) {
            log.warn("activation for {} failed. may not exist or activation code is invalid", username);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (final Exception e) {
            log.error("activation failed, an error has occurred", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/{username}/change-password")
    public ResponseEntity<?> changePassword(
            @PathVariable("username") final String username,
            @RequestBody final RegistrarUserChangePasswordRequest request
            ) {

        try {
            this.approverAuthService.changePassword(username, request.getCurrentPassword(), request.getNewPassword());

            return ResponseEntity.ok(
                    ImmutableMap.of("message", "Successfully changed your password.")
            );

        }
        catch (AuthorizationException e) {
            log.error("Attempt to change password of a different user! critical. {}", username, e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    ImmutableMap.of("message", e.getMessage())
            );
        } catch (IllegalArgumentException e) {
            log.error("Failed to change password of user {}", username, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ImmutableMap.of("message", e.getMessage())
            );
        } catch (InvalidKeySpecException | NoSuchAlgorithmException | DecoderException e) {
            log.error("Failed to change password of user {}", username, e);
            throw new IllegalStateException("Failed to change password.");
        }
    }

    @GetMapping("/{username}/active")
    public ResponseEntity<Boolean> isActive(@PathVariable("username") final String username) {
        final boolean active = this.approverAuthService.isActive(username);
        return ResponseEntity.ok(active);
    }

    @GetMapping("/{userName}/apor-types")
    public final ResponseEntity<List<String>> getAporTypesByUser(@PathVariable String userName) {
        List<String> aporTypesForUser = lookupTableService.getAporTypesForUser(userName);
        if (aporTypesForUser == null || aporTypesForUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(aporTypesForUser);
        }
    }

}
