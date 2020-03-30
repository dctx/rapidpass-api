package ph.devcon.rapidpass.controllers;

import com.google.zxing.WriterException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ph.devcon.rapidpass.entities.ControlCode;
import ph.devcon.rapidpass.enums.AccessPassStatus;
import ph.devcon.rapidpass.models.RapidPass;
import ph.devcon.rapidpass.models.RapidPassRequest;
import ph.devcon.rapidpass.services.QrPdfService;
import ph.devcon.rapidpass.services.RegistryService;
import ph.devcon.rapidpass.services.RegistryService.UpdateAccessPassException;

import javax.validation.Valid;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

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
    private final QrPdfService qrPdfService;

    @GetMapping("/access-passes")
    public List<RapidPass> getAccessPasses() {
        return registryService.findAllRapidPasses();
    }

    @GetMapping("/access-passes/{referenceId}")
    RapidPass getAccessPassDetails(@PathVariable String referenceId) {
        return registryService.find(referenceId);
    }

    @PostMapping("/access-passes")
    ResponseEntity<?> newRequestPass(@Valid @RequestBody RapidPassRequest rapidPassRequest) {
        RapidPass rapidPass = registryService.newRequestPass(rapidPassRequest);
        return ResponseEntity.status(201).body(rapidPass);
    }

    @GetMapping("/control-codes")
    public ResponseEntity<Iterable<ControlCode>> getControlCodes() {
        Iterable<ControlCode> controlCodes = registryService.getControlCodes();
        return ResponseEntity.ok(controlCodes);
    }

    @PutMapping("/access-passes/{referenceId}")
    ResponseEntity<?> updateAccessPass(@PathVariable String referenceId, @RequestBody RapidPass rapidPass) {
        if (!AccessPassStatus.isValid(rapidPass.getStatus())) {
            return ResponseEntity.badRequest().body("Unknown status code.");
        } else {
            try {
                RapidPass result = registryService.updateAccessPass(referenceId, rapidPass);
                return (result != null) ? ResponseEntity.ok().body(result) : ResponseEntity.notFound().build();
            } catch (UpdateAccessPassException e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }
    }

    @DeleteMapping("/access-passes/{referenceId}")
    HttpEntity<RapidPass> revokeAccessPass(@PathVariable String referenceId) {
        RapidPass rapidPass = registryService.revoke(referenceId);
        return (rapidPass == null) ? ResponseEntity.notFound().build() : ResponseEntity.ok(rapidPass);
    }

    /**
     * Downloads the QR Code pdf associated with control code
     *
     * @param referenceId control code
     * @return PDF download
     */
    @GetMapping("/qr-codes/{referenceId}")
    public HttpEntity<byte[]> downloadQrCode(@PathVariable String referenceId) throws IOException, WriterException, ParseException {
        log.debug("Processing /qr-codes/{}", referenceId);
        byte[] responseBody = qrPdfService.generateQrPdf(referenceId);

        if (responseBody == null || responseBody.length == 0) return ResponseEntity.notFound().build();

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.set(HttpHeaders.CONTENT_DISPOSITION,
                String.format("attachment; filename=%s.pdf", referenceId));
        headers.setContentLength(responseBody.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(responseBody);
    }

}
