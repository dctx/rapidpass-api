package ph.devcon.rapidpass.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ph.devcon.rapidpass.entities.ControlCode;
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
    ResponseEntity<?> updateAccessPass(@PathVariable String referenceId, @RequestBody RapidPassRequest rapidPassRequest) {
        RapidPass rapidPass;
        try {
            rapidPass = registryService.update(referenceId, rapidPassRequest);
        } catch (RegistryService.UpdateAccessPassException exception) {
            String errorMessage = exception.getMessage();
            return ResponseEntity.badRequest().body(errorMessage);
        }
        return rapidPass == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(rapidPass);
    }

    @DeleteMapping("/access-passes/{referenceId}")
    HttpEntity<RapidPass> revokeAccessPass(@PathVariable String referenceId) {
        RapidPass rapidPass = registryService.revoke(referenceId);
        return (rapidPass == null) ? ResponseEntity.notFound().build() : ResponseEntity.ok(rapidPass);
    }
}
