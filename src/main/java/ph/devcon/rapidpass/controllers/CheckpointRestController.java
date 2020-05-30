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

import com.google.common.collect.ImmutableMap;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ph.devcon.rapidpass.api.models.*;
import ph.devcon.rapidpass.entities.AccessPass;
import ph.devcon.rapidpass.keycloak.KeycloakService;
import ph.devcon.rapidpass.services.ICheckpointService;
import ph.devcon.rapidpass.services.controlcode.ControlCodeService;

import javax.ws.rs.NotAuthorizedException;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;


/**
 * Checkpoint API Rest Controller
 */
@RestController
@Slf4j
@Api(tags = "checkpoint")
@RequestMapping("/checkpoint")
@RequiredArgsConstructor
public class CheckpointRestController {
    private static final String JWT_GROUP = "checkpoint";

    private final ICheckpointService checkpointService;
    private final ControlCodeService controlCodeService;

    private final KeycloakService keycloakService;

    @Value("${endpointswitch.checkpoint.auth:false}")
    private boolean enableCheckpointAuth;

    @Value("${rapidpass.checkpointApkUrl}")
    private String url;

    @Value("${rapidpass.checkpointApkHash}")
    private String hash;

    @Value("${rapidpass.checkpointApkVersion}")
    private String version;

    @GetMapping("/access-passes/control-codes/{control-code}")
    public ResponseEntity<?> getAccessPassByControlCode(@PathVariable("control-code") String controlCode) {
        try {
            final AccessPass accessPass = this.controlCodeService.findAccessPassByControlCode(controlCode);
            if (accessPass != null) {
                return ResponseEntity.ok(ph.devcon.rapidpass.models.RapidPass.buildFrom(accessPass));
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @GetMapping("/access-passes/plate-numbers/{plate-no}")
    public ResponseEntity<?> getAccessPassByPlateNumber(@PathVariable("plate-no") String plateNo) {
        try {
            final AccessPass accessPass = this.checkpointService.retrieveAccessPassByPlateNo(plateNo);

            if (accessPass != null) {
                return ResponseEntity.ok(ph.devcon.rapidpass.models.RapidPass.buildFrom(accessPass));
            }
            return ResponseEntity.notFound().build();

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    /**
     * Deprecated. Use {@link #getRevokedRapidPassesJdbc(Integer)} instead.
     *
     * @param since
     * @return
     */
    @GetMapping("/revocations_old")
    @Deprecated
    public ResponseEntity<RevocationLogResponse> getRevokedRapidPasses(@RequestParam(required = false) Integer since) {
        RevocationLogResponse response = checkpointService.retrieveRevokedAccessPasses(since);
        return ResponseEntity.ok(response);
    }


    /**
     * Gets a list of revoked access passes since seconds from epock
     *
     * @param since seconds from epocj
     * @return 200 JSON with list of revoked access passes
     */
    @GetMapping("/revocations")
    public ResponseEntity<?> getRevokedRapidPassesJdbc(@RequestParam(required = false) Integer since) {
        final List<Map<String, Object>> response = checkpointService.retrieveRevokedAccessPassesJdbc(since);
        return ResponseEntity.ok(ImmutableMap.of("data", response));
    }

    @GetMapping("/update")
    public ResponseEntity<?> getLatestAppVersion() {

        if (StringUtils.isEmpty(this.url))
            throw new IllegalArgumentException("The URL of the latest checkpoint APK was not configured.");

        if (StringUtils.isEmpty(this.version))
            throw new IllegalArgumentException("The version of the latest checkpoint APK was not configured.");

        CheckpointAppVersionResponse response = new CheckpointAppVersionResponse();

        String filename = this.url.substring(this.url.lastIndexOf('/') + 1);

        response.setFile(filename);
        response.setSha1(this.hash);
        response.setVersion(this.version);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/download/{filename}")
    public ResponseEntity<?> downloadSpecifiedFile(@PathVariable("filename") String filename) throws IOException {

        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try (BufferedInputStream in = new BufferedInputStream(new URL(this.url).openStream())) {

            byte[] dataBuffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                bos.write(dataBuffer, 0, bytesRead);
            }
        }

        final byte[] responseBody = bos.toByteArray();
        if (responseBody.length == 0) return ResponseEntity.notFound().build();

        final HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.valueOf("application/vnd.android.package-archive"));
        headers.set(HttpHeaders.CONTENT_DISPOSITION,
                String.format("attachment; filename=%s", filename));
        headers.setContentLength(responseBody.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(responseBody);
    }

    /**
     * Will be replaced in favor of {@link #registerNewDevice(CheckpointRegisterRequest)}.
     *
     * @deprecated
     */
    @PostMapping("/auth")
    public ResponseEntity<?> authenticateDevice(@RequestBody CheckpointAuthRequest authRequest) {

        if (!enableCheckpointAuth) {
            return ResponseEntity.ok("Coming Soon!");
        }

        if (!this.checkpointService.validateByImei(authRequest.getMasterKey(), authRequest.getImei()))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        CheckpointAuthResponse authResponse = new CheckpointAuthResponse();

        KeyEntry latestKeys = this.checkpointService.getLatestKeys();

        authResponse.setSigningKey(latestKeys.getSigningKey());
        authResponse.setEncryptionKey(latestKeys.getEncryptionKey());
        authResponse.setAccessCode("youdonotneedthis");
        authResponse.setValidTo("");

        return ResponseEntity.ok().body(authResponse);
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerNewDevice(@RequestBody CheckpointRegisterRequest authRequest) {

        // Validates that the master key is correct
        boolean isValidImei = this.checkpointService.validateByImei(authRequest.getMasterKey(), authRequest.getImei());

        boolean isValidDeviceId = this.checkpointService.validateByUniqueDeviceId(authRequest.getMasterKey(), authRequest.getDeviceId());

        if (!isValidDeviceId && !isValidImei)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        boolean isValidMasterKey = this.checkpointService.validateByMasterKey(authRequest.getMasterKey());

        if (!isValidMasterKey) {
            log.error("Failed to register for device {} because of incorrect master key.", authRequest.getDeviceId());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        log.info("Attempting to register keycloak user for device {}", authRequest.getDeviceId());

        try {

            if (this.keycloakService.userExists(authRequest.getDeviceId())) {
                this.keycloakService.unregisterUser(authRequest.getDeviceId());
            }
        } catch (NotAuthorizedException e) {
            throw new NotAuthorizedException("The server failed to authenticate to Keycloak, and could not create a new scanner device user.");
        }

        this.keycloakService.createUser(authRequest.getDeviceId(), authRequest.getPassword());

        CheckpointRegisterResponse authResponse = new CheckpointRegisterResponse();

        authResponse.addAll(this.checkpointService.getAllKeys());

        return ResponseEntity.ok().body(authResponse);
    }
}
