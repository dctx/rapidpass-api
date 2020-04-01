package ph.devcon.rapidpass.controllers;

import com.google.common.collect.ImmutableMap;
import com.google.zxing.WriterException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ph.devcon.rapidpass.entities.ScannerDevice;
import ph.devcon.rapidpass.enums.AccessPassStatus;
import ph.devcon.rapidpass.models.MobileDevice;
import ph.devcon.rapidpass.models.QueryFilter;
import ph.devcon.rapidpass.models.RapidPass;
import ph.devcon.rapidpass.models.RapidPassRequest;
import ph.devcon.rapidpass.services.QrPdfService;
import ph.devcon.rapidpass.services.RegistryService;
import ph.devcon.rapidpass.services.RegistryService.UpdateAccessPassException;

import javax.validation.Valid;
import java.io.IOException;
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
    private final QrPdfService qrPdfService;

    @GetMapping("/access-passes")
    public ResponseEntity<List<RapidPass>> getAccessPasses(@RequestBody Optional<QueryFilter> queryParameter) {
        Pageable pageView = null;
        if (queryParameter.isPresent()) {
            QueryFilter queryFilter = queryParameter.get();
            if (null != queryFilter.getPageNo()) {
                int pageSize = (null != queryFilter.getPageSize()) ? queryFilter.getPageSize() : QueryFilter.DEFAULT_PAGE_SIZE;
                pageView = PageRequest.of(queryFilter.getPageNo(), pageSize);
            }
        } else {
        }
        return ResponseEntity.ok().body(registryService.findAllRapidPasses(Optional.ofNullable(pageView)));
    }

    @GetMapping("/access-passes/{referenceId}")
    ResponseEntity<RapidPass> getAccessPassDetails(@PathVariable String referenceId) {
        RapidPass rapidPass = registryService.find(referenceId);
        return (rapidPass != null) ? ResponseEntity.ok().body(rapidPass) : ResponseEntity.notFound().build();
    }

    @PostMapping("/access-passes")
    ResponseEntity<?> newRequestPass(@Valid @RequestBody RapidPassRequest rapidPassRequest) {
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

    @GetMapping("/scanner-devices")
    public ResponseEntity<List<MobileDevice>> getScannerDevices(@RequestBody Optional<QueryFilter> queryFilter) {
        Pageable pageView = null;
        if (queryFilter.isPresent() && queryFilter.get().getPageNo() != null) {
            int pageSize = (null != queryFilter.get().getPageSize()) ? queryFilter.get().getPageSize() : QueryFilter.DEFAULT_PAGE_SIZE;
            pageView = PageRequest.of(queryFilter.get().getPageNo(), pageSize);
        }
        List<MobileDevice> scannerDevices = registryService.getScannerDevices(Optional.ofNullable(pageView));
        return ResponseEntity.ok().body(scannerDevices);
    }

}
