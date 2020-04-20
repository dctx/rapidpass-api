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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ph.devcon.rapidpass.config.JwtSecretsConfig;
import ph.devcon.rapidpass.entities.AccessPass;
import ph.devcon.rapidpass.models.CheckpointAuthRequest;
import ph.devcon.rapidpass.services.ICheckpointService;
import ph.devcon.rapidpass.services.controlcode.ControlCodeService;


/**
 *  Checkpoint API Rest Controller
 */
@RestController
@Slf4j
@Api(tags = "checkpoint")
@RequestMapping("/checkpoint")
@RequiredArgsConstructor
public class CheckpointRestController
{
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

    public ResponseEntity getAccessPassByQrCode(String qrCode)
    {
        ResponseEntity response = null;
        try
        {
            final AccessPass accessPass = checkpointService.retrieveAccessPassByQrCode(qrCode);
            response = new ResponseEntity(accessPass, HttpStatus.OK);
        }
        catch (Exception e)
        {
            log.error(e.getMessage(),e);
            response = new ResponseEntity(e,HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return response;
    }

    @PostMapping("/auth")
    public ResponseEntity<?> authenticateDevice(@RequestBody CheckpointAuthRequest authRequest) {

//        // check master key first
//        if (!this.masterKey.equals(authRequest.getMasterKey())) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//        }
//
//        final ScannerDevice scannerDevice = this.checkpointService.retrieveDeviceByImei(authRequest.getImei());
//
//        OffsetDateTime expiry = OffsetDateTime.now();
//        // TODO replace with default expiry
//        expiry.plusHours(24);
//
//        Map<String, Object> claims = new HashMap<>();
//        claims.put("group", JWT_GROUP);
//        claims.put("sub", scannerDevice.getUniqueDeviceId());
//        claims.put("xsrfToken", UUID.randomUUID().toString());
//        claims.put("exp", expiry.toEpochSecond());
//
//        String jwt = JwtGenerator.generateToken(claims, this.jwtSecretsConfig.findGroupSecret(JWT_GROUP));
//
//        CheckpointAuthResponse authResponse = CheckpointAuthResponse.builder()
//                .signingKey(this.signingKey)
//                .encryptionKey(this.encryptionKey)
//                .accessCode(jwt)
//                .build();
//
//        return ResponseEntity.ok().body(authResponse);
        return ResponseEntity.ok("Coming Soon!");
    }
}
