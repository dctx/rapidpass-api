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
import ph.devcon.rapidpass.api.models.CheckpointAppVersionResponse;
import ph.devcon.rapidpass.api.models.RevocationLogResponse;
import ph.devcon.rapidpass.config.JwtSecretsConfig;
import ph.devcon.rapidpass.entities.AccessPass;
import ph.devcon.rapidpass.entities.ScannerDevice;
import ph.devcon.rapidpass.models.CheckpointAuthRequest;
import ph.devcon.rapidpass.models.CheckpointAuthResponse;
import ph.devcon.rapidpass.services.ICheckpointService;
import ph.devcon.rapidpass.services.controlcode.ControlCodeService;
import ph.devcon.rapidpass.utilities.JwtGenerator;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


/**
 *  Checkpoint API Rest Controller
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
    private final JwtSecretsConfig jwtSecretsConfig;

    @Value("${qrmaster.skey}")
    private String signingKey;

    @Value("${qrmaster.encryptionKey}")
    private String encryptionKey;

    @Value("${qrmaster.masterKey}")
    private String masterKey;

    @Value("${endpointswitch.checkpoint.auth:false}")
    private boolean enableCheckpointAuth;

    @Value("${rapidpass.checkpointApkUrl}")
    private String url;

    @Value("${rapidpass.checkpointApkHash}")
    private String hash;

    @Value("${rapidpass.checkpointApkVersion}")
    private String version;

    /*@Autowired
    public CheckpointRestController(ICheckpointService checkpointService) {
        this.checkpointService = checkpointService;
    }*/

    @GetMapping("/access-passes/control-codes/{control-code}")
    public ResponseEntity<?> getAccessPassByControlCode(@PathVariable("control-code") String controlCode) {
//        ResponseEntity response = null;
//        try {
//            final AccessPass accessPass = this.controlCodeService.findAccessPassByControlCode(controlCode);
//            RapidPass rapidPass = (null != accessPass) ? RapidPass.buildFrom(accessPass) : null;
//            response = new ResponseEntity(rapidPass, HttpStatus.OK);
//        }
//        catch (Exception e) {
//            log.error(e.getMessage(),e);
//            response = new ResponseEntity(e,HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//        return response;
        return ResponseEntity.ok("Coming Soon!");
    }

    @GetMapping("/access-passes/plate-numbers/{plate-no}")
    public ResponseEntity<?> getAccessPassByPlateNumber(@PathVariable("plate-no") String plateNo) {
//        ResponseEntity response = null;
//        try {
//            final AccessPass accessPass = this.checkpointService.retrieveAccessPassByPlateNo(plateNo);
//            RapidPass rapidPass = (null != accessPass) ? RapidPass.buildFrom(accessPass) : null;
//            response = new ResponseEntity(rapidPass, HttpStatus.OK);
//        } catch (Exception e) {
//            log.error(e.getMessage(),e);
//            response = new ResponseEntity(e,HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//        return response;
        return ResponseEntity.ok("Coming Soon!");
    }

    public ResponseEntity<?> getAccessPassByQrCode(String qrCode)
    {
        ResponseEntity<?> response = null;
        try
        {
            final AccessPass accessPass = checkpointService.retrieveAccessPassByQrCode(qrCode);
            response = new ResponseEntity<>(accessPass, HttpStatus.OK);
        }
        catch (Exception e)
        {
            log.error(e.getMessage(),e);
            response = new ResponseEntity<>(e,HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return response;
    }

    @GetMapping("/revocations")
    public ResponseEntity<RevocationLogResponse> getRevokedRapidPasses(@RequestParam(required = false) Integer since) {
        RevocationLogResponse response = checkpointService.retrieveRevokedAccessPasses(since);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/update")
    public ResponseEntity<?> getLatestAppVersion() {

        if (StringUtils.isEmpty(this.url))
            throw new IllegalArgumentException("The URL of the latest checkpoint APK was not configured.");

        if (StringUtils.isEmpty(this.version))
            throw new IllegalArgumentException("The version of the latest checkpoint APK was not configured.");

        CheckpointAppVersionResponse response = new CheckpointAppVersionResponse();

        String filename = this.url.substring(this.url.lastIndexOf('/'));

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

    @PostMapping("/auth")
    public ResponseEntity<?> authenticateDevice(@RequestBody CheckpointAuthRequest authRequest) {

        if (!enableCheckpointAuth) {
            return ResponseEntity.ok("Coming Soon!");
        }

        // check master key first
        if (!this.masterKey.equals(authRequest.getMasterKey())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        final ScannerDevice scannerDevice = this.checkpointService.retrieveDeviceByImei(authRequest.getImei());

        OffsetDateTime expiry = OffsetDateTime.now();
        // TODO replace with default expiry
        expiry.plusHours(24);

        Map<String, Object> claims = new HashMap<>();
        claims.put("group", JWT_GROUP);
        claims.put("sub", scannerDevice.getUniqueDeviceId());
        claims.put("xsrfToken", UUID.randomUUID().toString());
        claims.put("exp", expiry.toEpochSecond());

        // FIXME this won't work with new keycloak implementation!
        String jwt = JwtGenerator.generateToken(claims, this.jwtSecretsConfig.findGroupSecret(JWT_GROUP));

        CheckpointAuthResponse authResponse = CheckpointAuthResponse.builder()
                .signingKey(this.signingKey)
                .encryptionKey(this.encryptionKey)
                .accessCode(jwt)
                .build();

        return ResponseEntity.ok().body(authResponse);
    }
}
