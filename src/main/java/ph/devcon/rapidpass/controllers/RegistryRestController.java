package ph.devcon.rapidpass.controllers;

import com.google.zxing.WriterException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ph.devcon.rapidpass.entities.ControlCode;
import ph.devcon.rapidpass.enums.RequestStatus;
import ph.devcon.rapidpass.models.RapidPass;
import ph.devcon.rapidpass.models.RapidPassRequest;
import ph.devcon.rapidpass.services.RegistryService;

import java.io.IOException;
import java.util.List;

/**
 * Registry API Rest Controller
 */
@CrossOrigin
@RestController
@RequestMapping("/registry")
@Slf4j
public class RegistryRestController {

    private RegistryService registryService;

    @Autowired
    public RegistryRestController(RegistryService registryService) {
        this.registryService = registryService;
    }

    @GetMapping("/access-passes")
    public List<RapidPass> getAccessPasses() {
        return registryService.findAllRapidPasses();
    }

    @GetMapping("/access-passes/{referenceId}")
    RapidPass getAccessPassDetails(@PathVariable String referenceId) {
        return registryService.find(referenceId);
    }

    @PostMapping("/access-passes")
    ResponseEntity<RapidPass> newRequestPass(@RequestBody RapidPassRequest rapidPassRequest) {
        RapidPass rapidPass = registryService.newRequestPass(rapidPassRequest);
        return ResponseEntity.status(201).body(rapidPass);
    }

    @GetMapping("/control-codes")
    public ResponseEntity<Iterable<ControlCode>> getControlCodes() {
        Iterable<ControlCode> controlCodes = registryService.getControlCodes();
        return ResponseEntity.ok(controlCodes);
    }

    @PutMapping("/access-passes/{referenceId}")
    ResponseEntity<RapidPass> updateAccessPass(@PathVariable String referenceId, @RequestBody RapidPass rapidPass) {
        String status = rapidPass.getStatus();

        RapidPass result = null;

        try {

            if (RequestStatus.APPROVED.toString().equals(status)) {
                result = registryService.grant(referenceId);
            } else if (RequestStatus.DENIED.toString().equals(status)) {
                result = registryService.decline(referenceId);
            }

        } catch (RegistryService.UpdateAccessPassException e) {
            e.printStackTrace();
        }

        return (result != null) ? ResponseEntity.ok().body(result) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/access-passes/{referenceId}")
    RapidPass revokeAccessPass(@PathVariable String referenceId) {
        return registryService.revoke(referenceId);
    }

    /**
     * Downloads the QR Code pdf associated with control code
     *
     * @param referenceId control code
     * @return PDF download
     */
    @GetMapping("/qr-codes/{referenceId}")
    public HttpEntity<byte[]> downloadQrCode(@PathVariable String referenceId) throws IOException, WriterException {
        log.debug("Processing /qr-codes/{}", referenceId);
        byte[] responseBody = registryService.generateQrPdf(referenceId);

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
