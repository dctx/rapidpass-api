package ph.devcon.rapidpass.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ph.devcon.rapidpass.enums.RequestStatus;
import ph.devcon.rapidpass.models.RapidPass;
import ph.devcon.rapidpass.models.RapidPassRequest;
import ph.devcon.rapidpass.services.RegistryService;

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
        return registryService.findAll();
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
    public Iterable<RapidPass> getControlCodes() {
        return registryService.findAll();
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
}
