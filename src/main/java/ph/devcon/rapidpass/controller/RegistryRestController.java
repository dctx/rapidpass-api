package ph.devcon.rapidpass.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ph.devcon.rapidpass.entities.RapidPass;
import ph.devcon.rapidpass.entities.RapidPassRequest;
import ph.devcon.rapidpass.service.RegistryService;

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
    RapidPass newRequestPass(@RequestBody RapidPassRequest rapidPassRequest) {
        return registryService.newRequestPass(rapidPassRequest);
    }

    @GetMapping("/control-codes")
    public Iterable<RapidPass> getControlCodes() {
        return registryService.findAll();
    }

    @PutMapping("/access-passes/{referenceId}")
    RapidPass updateAccessPass(@PathVariable String referenceId, @RequestBody RapidPassRequest rapidPassRequest) {
        return registryService.update(referenceId, rapidPassRequest);
    }

    @DeleteMapping("/access-passes/{referenceId}")
    RapidPass revokeAccessPass(@PathVariable String referenceId) {
        return registryService.revoke(referenceId);
    }
}
