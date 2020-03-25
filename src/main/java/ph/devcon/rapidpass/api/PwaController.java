package ph.devcon.rapidpass.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.plexus.util.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ph.devcon.rapidpass.model.RapidPassRequest;
import ph.devcon.rapidpass.service.PwaService;

import static ph.devcon.rapidpass.model.RapidPassRequest.RequestStatus.PENDING;
import static ph.devcon.rapidpass.model.RapidPassRequest.RequestType.INDIVIDUAL;
import static ph.devcon.rapidpass.model.RapidPassRequest.RequestType.VEHICLE;

/**
 * The {@link PwaController} class provides the API mappings for PWA/Webapp operations.
 */
@RestController
@RequestMapping("/api/v1/pwa/")
@Slf4j
@RequiredArgsConstructor
public class PwaController {

    // TODO error handling via ErrorHandler @ControllerAdvice or something

    /**
     * Service containing business logic for PWA operations.
     */
    private final PwaService pwaService;

    /**
     * Creates a new request for a new RapidPass Pass. Can be for individual or vehicle depending on pass type.
     *
     * @param rapidPassRequest RapidPass request payload
     * @return Status 201 if created.
     */
    @PostMapping("requestPass")
    public HttpEntity<String> newPassRequest(
            @RequestBody RapidPassRequest rapidPassRequest) {
        log.debug("POST /api/v1/pwa/requestPass {}", rapidPassRequest);

        // make sure rapidPassRequest is PENDING
        rapidPassRequest.setRequestStatus(PENDING);
        pwaService.createPassRequest(rapidPassRequest);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("OK");
    }

    /**
     * GET /api/v1/pwa/requestPass Gets the status (and other info) of a request pass. Either {@code plateNum} or {@code mobileNum} MUST be passed.
     *
     * @param plateNum  vehicle plate number
     * @param mobileNum user mobile phone
     * @return JSON response of a {@link RapidPassRequest}
     */
    @GetMapping("requestPass")
    public HttpEntity<?> getPassRequest(
            @RequestParam(value = "plateNum", required = false) String plateNum,
            @RequestParam(value = "mobileNum", required = false) String mobileNum) {
        final RapidPassRequest passRequest;
        if (!StringUtils.isBlank(plateNum)) passRequest = pwaService.getPassRequest(plateNum, VEHICLE);
        else if (!StringUtils.isBlank(mobileNum)) passRequest = pwaService.getPassRequest(mobileNum, INDIVIDUAL);
        else return ResponseEntity.badRequest().body("Either plateNum or mobileNum must be set!");

        return passRequest == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(passRequest);
    }

}
