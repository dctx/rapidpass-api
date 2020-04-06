package ph.devcon.rapidpass.controllers;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.DecoderException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ph.devcon.rapidpass.models.AgencyAuth;
import ph.devcon.rapidpass.models.Login;
import ph.devcon.rapidpass.models.UserActivationRequest;
import ph.devcon.rapidpass.services.ApproverAuthService;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/users")
public class UserRestController {

    private final ApproverAuthService approverAuthService;

    public UserRestController(final ApproverAuthService approverAuthService) {
        this.approverAuthService = approverAuthService;
    }

    @PostMapping("/auth")
    public ResponseEntity<AgencyAuth> login(@RequestBody Login login) {
        try {
            final AgencyAuth auth = this.approverAuthService.login(login.getUsername(), login.getPassword());
            if (auth == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            return ResponseEntity.ok().body(auth);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException | DecoderException e) {
            log.error("hashing function error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
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

    @GetMapping("/{username}/active")
    public ResponseEntity<Boolean> isActive(@PathVariable("username") final String username) {
        final boolean active = this.approverAuthService.isActive(username);
        return ResponseEntity.ok(active);
    }

}