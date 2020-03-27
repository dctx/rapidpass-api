package ph.devcon.rapidpass.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ph.devcon.rapidpass.model.RapidPass;
import ph.devcon.rapidpass.model.RapidPassRequest;
import ph.devcon.rapidpass.model.UpdateRapidPassRequest;
import ph.devcon.rapidpass.service.RegistryService;

import java.util.ArrayList;
import java.util.List;

/**
 * Registry API Rest Controller
 */

@RestController
@RequestMapping("/registry")
@Slf4j
public class RegistryRestController {

    private RegistryService registryService;

    @Autowired
    public RegistryRestController(RegistryService registryService) {
        this.registryService = registryService;
    }

    @GetMapping("/accessPasses")
    public List<RapidPass> getAccessPasses() {
        return registryService.findAll();
    }

    @GetMapping("/accessPasses/{referenceId}")
    RapidPass getAccessPassDetails(@PathVariable String referenceId) {
        return registryService.find(referenceId);
    }

    @PostMapping("/accessPasses")
    RapidPass newRequestPass(@RequestBody RapidPassRequest rapidPassRequest) {
        return registryService.newRequestPass(rapidPassRequest);
    }

    @GetMapping("/controlCodes")
    public Iterable<RapidPass> getControlCodes() {
        return registryService.findAll();
    }

    @PutMapping("/accessPasses/{referenceId}")
    RapidPass updateAccessPass(@PathVariable String referenceId, @RequestBody UpdateRapidPassRequest rapidPassRequest) {
        try {
            return registryService.update(referenceId, rapidPassRequest);
        } catch (RegistryService.UpdateAccessPassException e) {
            e.printStackTrace();
        }
        return null;
    }

    @DeleteMapping("/accessPasses/{referenceId}")
    RapidPass revokeAccessPass(@PathVariable String referenceId) {
        return registryService.revoke(referenceId);
    }
}
