package ph.devcon.rapidpass.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ph.devcon.rapidpass.model.RapidPass;
import ph.devcon.rapidpass.model.RapidPassRequest;
import ph.devcon.rapidpass.service.RegistryService;


/**
 * Registry API Rest Controller specifically for batch operations
 */
@RestController
@RequestMapping("/batch")
@Slf4j
public class RegistryBatchRestController {
    private RegistryService registryService;

    @Autowired
    public RegistryBatchRestController(RegistryService registryService) {
        this.registryService = registryService;
    }

    /**
     * Upload CSV or excel file of approved control numbers
     * @param rapidPassRequest
     * @return
     */
    @PostMapping("/accessPasses")
    Iterable<RapidPass> newRequestPass(@RequestBody RapidPassRequest rapidPassRequest) {
        // todo: implement on registry service
        return null;
    }


}
