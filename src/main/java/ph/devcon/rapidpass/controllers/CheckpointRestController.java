package ph.devcon.rapidpass.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ph.devcon.rapidpass.entities.AccessPass;
import ph.devcon.rapidpass.services.ICheckpointService;

/**
 * Checkpoint API Rest Controller
 */
@CrossOrigin
@RestController
@RequestMapping("/checkpoint")
@Slf4j
public class CheckpointRestController {

    private ICheckpointService checkpointService;

    @Autowired
    public CheckpointRestController(ICheckpointService checkpointService) {
        this.checkpointService = checkpointService;
    }

    @GetMapping("/access-pass/verify-control-code/{control-code}")
    public HttpEntity<AccessPass> getAccessPassByControlCode(@PathVariable("control-code") String controlCode) {
        AccessPass byControlCode = this.checkpointService.getAccessPassByControlCode(controlCode);
        return (null != byControlCode) ? ResponseEntity.ok().body(byControlCode) : ResponseEntity.notFound().build();
    }
}