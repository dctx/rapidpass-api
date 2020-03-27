package ph.devcon.rapidpass.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ph.devcon.rapidpass.model.RapidPass;
import ph.devcon.rapidpass.model.RapidPassRequest;
import ph.devcon.rapidpass.service.RegistryService;

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

    @PostMapping("/accessPasses")
    RapidPass newRequestPass(@RequestBody RapidPassRequest rapidPassRequest) {
        return registryService.newRequestPass(rapidPassRequest);
    }
}
