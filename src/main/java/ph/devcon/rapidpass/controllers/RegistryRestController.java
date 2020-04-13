package ph.devcon.rapidpass.controllers;

import com.google.common.collect.ImmutableMap;
import com.google.zxing.WriterException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ph.devcon.rapidpass.entities.AccessPass;
import ph.devcon.rapidpass.entities.ScannerDevice;
import ph.devcon.rapidpass.enums.RecordSource;
import ph.devcon.rapidpass.models.*;
import ph.devcon.rapidpass.services.ApproverAuthService;
import ph.devcon.rapidpass.services.QrPdfService;
import ph.devcon.rapidpass.services.RegistryService;
import ph.devcon.rapidpass.services.RegistryService.UpdateAccessPassException;

import javax.validation.Valid;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.ParseException;
import java.util.List;
import java.util.Optional;

/**
 * Registry API Rest Controller
 */
@CrossOrigin
@RestController
@RequestMapping("/registry")
@Slf4j
@RequiredArgsConstructor
public class RegistryRestController {

    private final RegistryService registryService;
    private final ApproverAuthService approverAuthService;
    private final QrPdfService qrPdfService;

    @PreAuthorize("hasAuthority('approver')")
    @GetMapping("/access-passes")
    public ResponseEntity<RapidPassPageView> getAccessPasses(Optional<QueryFilter> queryParameter) {

        QueryFilter q = queryParameter.orElse(new QueryFilter());

        return ResponseEntity.ok().body(registryService.findRapidPass(q));
    }

    @GetMapping("/access-passes/{referenceId}")
    ResponseEntity<RapidPass> getAccessPassDetails(@PathVariable String referenceId) {

        AccessPass accessPass = registryService.findByNonUniqueReferenceId(referenceId);

        if (accessPass == null) return ResponseEntity.notFound().build();

        RapidPass rapidPass = RapidPass.buildFrom(accessPass);

        if (rapidPass == null) ResponseEntity.notFound().build();

        return ResponseEntity.ok().body(rapidPass);
    }

    @PostMapping("/access-passes")
    ResponseEntity<?> newRequestPass(@Valid @RequestBody RapidPassRequest rapidPassRequest) {
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

    @PutMapping("/access-passes/{referenceId}")
    ResponseEntity<?> updateAccessPass(@PathVariable String referenceId, @Valid @RequestBody RapidPassStatus rapidPassStatus) throws UpdateAccessPassException {
        RapidPass updatedRapidPass = registryService.updateAccessPass(referenceId, rapidPassStatus);

        if (updatedRapidPass == null)
            throw new UpdateAccessPassException("Failed to update Access Pass because there was nothing updated.");

        return ResponseEntity.ok().body(updatedRapidPass);
    }

    @DeleteMapping("/access-passes/{referenceId}")
    HttpEntity<RapidPass> revokeAccessPass(@PathVariable String referenceId) {
        RapidPass rapidPass = registryService.revoke(referenceId);
        return (rapidPass == null) ? ResponseEntity.notFound().build() : ResponseEntity.ok(rapidPass);
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
    public ResponseEntity<List<MobileDevice>> getScannerDevices(@RequestBody Optional<QueryFilter> queryFilter) {
        Pageable pageView = null;
        if (queryFilter.isPresent() && queryFilter.get().getPageNo() != null) {
            int pageSize = (null != queryFilter.get().getMaxPageRows()) ? queryFilter.get().getMaxPageRows() : QueryFilter.DEFAULT_PAGE_SIZE;
            pageView = PageRequest.of(queryFilter.get().getPageNo(), pageSize);
        }
        List<MobileDevice> scannerDevices = registryService.getScannerDevices(Optional.ofNullable(pageView));
        return ResponseEntity.ok().body(scannerDevices);
    }

    @PostMapping("/scanner-devices")
    public ResponseEntity<?> registerScannerDevice(@RequestBody MobileDevice deviceRequest) {
        ScannerDevice device = this.registryService.registerScannerDevice(deviceRequest);

        return ResponseEntity.status(201).body(ImmutableMap.of("deviceId", deviceRequest.getImei()));
    }

    /**
     * Depecreated. Use {@link UserRestController#login(Login)}
     *
     * @param login
     * @return
     */
    @Deprecated
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
