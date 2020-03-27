package ph.devcon.rapidpass.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ph.devcon.rapidpass.model.RapidPass;
import ph.devcon.rapidpass.model.RapidPassRequest;
import ph.devcon.rapidpass.service.RegistryService;

import java.util.List;

@RestController
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
