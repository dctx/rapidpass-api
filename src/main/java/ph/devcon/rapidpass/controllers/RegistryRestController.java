package ph.devcon.rapidpass.controllers;

import com.google.zxing.WriterException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ph.devcon.rapidpass.enums.AccessPassStatus;
import ph.devcon.rapidpass.models.*;
import ph.devcon.rapidpass.services.AuthService;
import ph.devcon.rapidpass.services.QrPdfService;
import ph.devcon.rapidpass.services.RegistryService;
import ph.devcon.rapidpass.services.RegistryService.UpdateAccessPassException;

import javax.validation.Valid;
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
    private final AuthService authService;
    private final QrPdfService qrPdfService;

    @GetMapping("/access-passes")
    public ResponseEntity<List<RapidPass>> getAccessPasses(@RequestBody Optional<QueryFilter> queryParameter) {
        Pageable pageView = null;
        String aporType = null;

        if (queryParameter.isPresent()) {
            QueryFilter queryFilter = queryParameter.get();

            if (null != queryFilter.getPageNo()) {
                int pageSize = (null != queryFilter.getPageSize()) ? queryFilter.getPageSize() : QueryFilter.DEFAULT_PAGE_SIZE;
                pageView = PageRequest.of(queryFilter.getPageNo(), pageSize);
            }

            if (!StringUtils.isBlank(queryFilter.getAporType())) {
                aporType = queryFilter.getAporType();
            }
        }

        return ResponseEntity.ok().body(registryService.findAllRapidPasses(aporType, Optional.ofNullable(pageView)));
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
    ResponseEntity<?> updateAccessPass(@PathVariable String referenceId, @RequestBody RequestResult requestResult) {
        if (!AccessPassStatus.isValid(requestResult.getResult())) {
            return ResponseEntity.badRequest().body("Unknown status code.");
        } else {
            try {
                RapidPass result = registryService.updateAccessPass(referenceId, requestResult);
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

    @PostMapping("/auth")
    public ResponseEntity<AgencyAuth> login(@RequestBody Login login) {
        try {
            final AgencyAuth auth = this.authService.login(login.getUsername(), login.getPassword());
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

    @PostMapping("/registrar-users")
    public ResponseEntity<?> createAgencyUser(@RequestBody AgencyUser user) {
        try {
            this.authService.createAgencyCredentials(user);
            return ResponseEntity.ok().build();
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

}
