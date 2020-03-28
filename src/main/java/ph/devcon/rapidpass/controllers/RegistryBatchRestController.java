package ph.devcon.rapidpass.controllers;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ph.devcon.rapidpass.entities.RapidPass;
import ph.devcon.rapidpass.entities.RapidPassBatchRequest;
import ph.devcon.rapidpass.services.RegistryService;


/**
 * Registry API Rest Controller specifically for batch operations
 */
@CrossOrigin
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
     *
     * TODO: This endpoint needs to be reworked to support receiving file data.
     *
     * @param rapidPassBatchRequest THIS IS INCORRECT, as the batch data should not be delivered by JSON request body,
     *                              but instead by file upload data.
     */
    @PostMapping("/access-passes")
    Iterable<RapidPass> newRequestPass(@RequestBody RapidPassBatchRequest rapidPassBatchRequest) {
        // todo: implement on registry service
        return this.registryService.batchUpload(rapidPassBatchRequest);
    }


}
