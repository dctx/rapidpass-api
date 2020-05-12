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
import com.google.zxing.WriterException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.keycloak.KeycloakSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import ph.devcon.rapidpass.api.models.ControlCodeResponse;
import ph.devcon.rapidpass.api.models.RapidPassUpdateRequest;
import ph.devcon.rapidpass.entities.AccessPass;
import ph.devcon.rapidpass.entities.ScannerDevice;
import ph.devcon.rapidpass.enums.AccessPassStatus;
import ph.devcon.rapidpass.enums.RecordSource;
import ph.devcon.rapidpass.exceptions.AccessPassNotFoundException;
import ph.devcon.rapidpass.exceptions.AccountLockedException;
import ph.devcon.rapidpass.models.*;
import ph.devcon.rapidpass.services.QrPdfService;
import ph.devcon.rapidpass.services.RegistryService;
import ph.devcon.rapidpass.services.RegistryService.UpdateAccessPassException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.spec.InvalidKeySpecException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Registry API Rest Controller
 */
@RestController
@RequestMapping("/registry")
@Slf4j
@RequiredArgsConstructor
public class RegistryRestController {

    private final RegistryService registryService;
    private final QrPdfService qrPdfService;
    private final HttpServletRequest request;

    @Value("${endpointswitch.registry.accesspasses:false}")
    private boolean isRegisterSinglePassEnabled;

    @GetMapping("/access-passes")
        public ResponseEntity<RapidPassPageView> getAccessPasses(Optional<QueryFilter> queryParameter, Principal principal) {

        QueryFilter q = queryParameter.orElse(new QueryFilter());

        return ResponseEntity.ok().body(registryService.findRapidPass(q, Optional.ofNullable(principal)));
    }

    @GetMapping("/access-passes/{referenceId}")
    ResponseEntity<RapidPass> getAccessPassDetails(@PathVariable String referenceId) throws AccessPassNotFoundException {

        AccessPass accessPass = registryService.findByNonUniqueReferenceId(referenceId);

        if (accessPass == null) {
            throw new AccessPassNotFoundException(
                    String.format("There is no RapidPass found with reference ID `%s`", referenceId)
            );
        }

        RapidPass rapidPass = RapidPass.buildFrom(accessPass);

        if (rapidPass == null) ResponseEntity.notFound().build();

        return ResponseEntity.ok().body(rapidPass);
    }

    @GetMapping("/access-passes/{referenceId}/status")
    ResponseEntity<?> getAccessPassStatus(@PathVariable String referenceId) throws AccessPassNotFoundException {
        AccessPass accessPass = registryService.findByNonUniqueReferenceId(referenceId);

        if (accessPass == null) {
            throw new AccessPassNotFoundException(
                    String.format("There is no RapidPass found with reference ID `%s`", referenceId)
            );
        }

        Map<String, String> response = new HashMap<>();
        response.put("status", accessPass.getStatus());
        response.put("validUntil", accessPass.getValidTo().toString());

        if (AccessPassStatus.INVALID_STATUSES.contains(accessPass.getStatus())) {
            response.put("reason", accessPass.getUpdates());
        }

        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/access-passes")
    ResponseEntity<?> newRequestPass(@Valid @RequestBody RapidPassRequest rapidPassRequest) {

        if (!isRegisterSinglePassEnabled) {
            return ResponseEntity.status(200).body("Coming soon!");
        }

        rapidPassRequest.setSource(RecordSource.ONLINE.toString());
        RapidPass rapidPass = registryService.newRequestPass(rapidPassRequest);
//        return ResponseEntity.status(201).body(ImmutableMap.of("referenceId", rapidPass.getReferenceId()));
        return ResponseEntity.status(201).body(rapidPass);
    }

//    @GetMapping("/control-codes")
//    public ResponseEntity<Iterable<ControlCode>> getControlCodes() {
//        Iterable<ControlCode> controlCodes = registryService.getControlCodes();
//        return ResponseEntity.ok(controlCodes);
//    }

    @GetMapping("/access-passes/{referenceId}/control-code")
    ResponseEntity<?> getControlCode(@PathVariable String referenceId) {

        ControlCodeResponse controlCode = registryService.getControlCode(referenceId);

        if (controlCode == null) return ResponseEntity.notFound().build();

        return ResponseEntity.ok(controlCode);
    }

    @PutMapping("/access-passes/{referenceId}")
    ResponseEntity<?> updateAccessPass(@PathVariable String referenceId, @Valid @RequestBody RapidPassUpdateRequest rapidPassUpdate) throws UpdateAccessPassException {
        RapidPass updatedRapidPass = registryService.updateAccessPass(referenceId, rapidPassUpdate);

        if (updatedRapidPass == null)
            throw new UpdateAccessPassException("Failed to update Access Pass because there was nothing updated.");

        return ResponseEntity.ok().body(updatedRapidPass);
    }

    /**
     * Note that using the delete method will perform suspend access pass but without specifying the reason why the
     * rapid pass was suspended.
     *
     * To specify the reason why it was suspended, use the PUT request.
     */
    @DeleteMapping("/access-passes/{referenceId}")
    HttpEntity<RapidPass> revokeAccessPass(@PathVariable String referenceId) {
        AccessPass suspendedAccessPass = registryService.suspend(referenceId, null);
        RapidPass rapidPass = RapidPass.buildFrom(suspendedAccessPass);
        return (rapidPass == null) ? ResponseEntity.notFound().build() : ResponseEntity.ok(rapidPass);
    }

    /* Re-sending a text Message */
    @PostMapping("/access-passes/{referenceId}/resend")
    ResponseEntity<?> resendTextMessage(@PathVariable String referenceId) {
        boolean notified = registryService.updateNotified(referenceId);
        Map<Object, Object> response = new HashMap<>();
        if (notified) {
            response.put("message", "RapidPass email and sms has been queued for processing.");
            return ResponseEntity.status(200).body(response);
        }
        response.put("message", String.format("There is no RapidPass found with reference ID `%s`", referenceId));
        return ResponseEntity.status(404).body(response);
    }


    /**
     * Downloads the QR Code pdf associated with control code
     * <p>
     * For retrieving the image base 64 data of the QR code of an access pass, please see the method
     * {@link #downloadRapidPassQrImageDataBase64(String)}.
     *
     * @param controlCode the control code that uniquely identifies the access pass
     * @return The file data containing of PDF for this access pass
     */
    @GetMapping("/qr-codes/{controlCode}")
    public HttpEntity<byte[]> downloadRapidPassPdf(@PathVariable String controlCode) throws IOException, WriterException, ParseException {
        log.debug("Processing /qr-codes/{}", controlCode);
        ByteArrayOutputStream bos = (ByteArrayOutputStream) qrPdfService.generateQrPdf(controlCode);
        final byte[] responseBody = bos.toByteArray();
        if (responseBody == null || responseBody.length == 0) return ResponseEntity.notFound().build();

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.set(HttpHeaders.CONTENT_DISPOSITION,
                String.format("attachment; filename=%s.pdf", controlCode));
        headers.setContentLength(responseBody.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(responseBody);
    }

    @GetMapping("/scanner-devices")
    public ResponseEntity<?> getScannerDevices(@RequestBody Optional<QueryFilter> queryFilter) {

        boolean isDisabled = true;
        if (isDisabled) {
            return ResponseEntity.status(403).body(
                    ImmutableMap.of("message", "This endpoint has been temporarily disabled.")
            );
        } else {
            Pageable pageView = null;
            if (queryFilter.isPresent() && queryFilter.get().getPageNo() != null) {
                int pageSize = (null != queryFilter.get().getMaxPageRows()) ? queryFilter.get().getMaxPageRows() : QueryFilter.DEFAULT_PAGE_SIZE;
                pageView = PageRequest.of(queryFilter.get().getPageNo(), pageSize);
            }
            List<MobileDevice> scannerDevices = registryService.getScannerDevices(Optional.ofNullable(pageView));
            return ResponseEntity.ok().body(scannerDevices);
        }
    }

    @PostMapping("/scanner-devices")
    public ResponseEntity<?> registerScannerDevice(@RequestBody MobileDevice deviceRequest) {
        ScannerDevice device = this.registryService.registerScannerDevice(deviceRequest);

        return ResponseEntity.status(201).body(ImmutableMap.of("deviceId", deviceRequest.getImei()));
    }

    /**
     * This endpoint returns the base64 image data of a qr code.
     * <p>
     * For retrieving the PDF data of an access pass, please see the method {@link #downloadRapidPassPdf(String)}.
     *
     * @param referenceId the reference ID that uniquely identifies the access pass
     * @return The base 64 image data of the QR for this access pass
     */
    @GetMapping("/qr-codes/{referenceId}/qr-code")
    public ResponseEntity<?> downloadRapidPassQrImageDataBase64(@PathVariable String referenceId) {
        AccessPass accessPass = registryService.findByNonUniqueReferenceId(referenceId);
        try {
            if (accessPass == null)
                throw new IllegalArgumentException("No Access Pass found for " + referenceId + ".");

            // Image as a file
            final byte[] bytes = qrPdfService.generateQrImageData(accessPass);

            // Image data encoded as base 64
            byte[] base64EncodedBytes = Base64.encodeBase64(bytes);

            return ResponseEntity.ok(new String(base64EncodedBytes));

        } catch (IOException | WriterException e) {
            throw new IllegalStateException("Failed to generate QR Code for " + referenceId + ". " + e.getMessage());
        }
    }
}
