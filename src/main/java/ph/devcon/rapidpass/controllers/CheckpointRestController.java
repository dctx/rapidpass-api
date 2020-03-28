package ph.devcon.rapidpass.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ph.devcon.rapidpass.models.RapidPass;
import ph.devcon.rapidpass.services.RegistryService;

import java.util.List;

/**
 * Registry API Rest Controller
 */
@CrossOrigin
@RestController
@RequestMapping("/checkpoint")
@Slf4j
public class CheckpointRestController {


    private RegistryService registryService;

    @Autowired
    public CheckpointRestController(RegistryService registryService) {
        this.registryService = registryService;
    }


    /**
     * The front end inspector app calls it qrCode, but we use referenceId.
     * @param referenceId The reference ID used to find the access pass
     * @return A {@link RapidPass} if it exists
     */
    @GetMapping("/access-pass/verify-qr/{referenceId}")
    public ResponseEntity<RapidPass> getAccessPasses(@PathVariable String referenceId) {
        RapidPass rapidPassWithQrCode = registryService.find(referenceId);
        return rapidPassWithQrCode == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(rapidPassWithQrCode);
    }
}
