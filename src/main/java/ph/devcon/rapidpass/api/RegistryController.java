package ph.devcon.rapidpass.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ph.devcon.rapidpass.model.RapidPassRequest;
import ph.devcon.rapidpass.service.RegistryService;

import static ph.devcon.rapidpass.model.RapidPassRequest.RequestStatus.PENDING;

/**
 * The {@link RegistryController} class provides the API mappings for PWA/Webapp operations.
 */
@RestController
@RequestMapping("/api/v1/registry/")
@Slf4j
@RequiredArgsConstructor
public class RegistryController {

    // TODO error handling via ErrorHandler @ControllerAdvice or something

    /**
     * Service containing business logic for PWA operations.
     */
    private final RegistryService registryService;

    /**
     * POST /api/v1/registry/accessPasses - Creates a new request for a new RapidPass Pass. Can be for individual or vehicle depending on pass type.
     *
     * @param rapidPassRequest RapidPass request payload
     * @return Status 201 if created.
     */
    @PostMapping("accessPasses")
    public HttpEntity<?> newPassRequest(
            @RequestBody RapidPassRequest rapidPassRequest) {
        log.debug("POST /api/v1/registry/accessPasses {}", rapidPassRequest);

        // make sure rapidPassRequest is PENDING
        rapidPassRequest.setRequestStatus(PENDING);
        registryService.createPassRequest(rapidPassRequest);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("OK");
    }

    /**
     * GET /api/v1/registry/accessPasses/{referenceID} - Gets the status (and other info) of a request pass.
     *
     * @param referenceID Reference ID of the access pass request, mobile number for individuals and plate numbers for vehicles
     * @return JSON response of a {@link RapidPassRequest}
     */
    @GetMapping("accessPasses/{referenceID}")
    public HttpEntity<?> getPassRequest(
            @PathVariable String referenceID) {
        final RapidPassRequest passRequest = registryService.getPassRequest(referenceID);
        return passRequest == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(passRequest);
    }

}
