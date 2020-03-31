package ph.devcon.rapidpass.controllers;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ph.devcon.rapidpass.entities.AccessPass;
import ph.devcon.rapidpass.models.RapidPass;
import ph.devcon.rapidpass.services.ICheckpointService;


/**
 *  Checkpoint API Rest Controller
 */
@CrossOrigin
@RestController
@Slf4j
@Api(tags = "checkpoint")
@RequestMapping("/checkpoint")
public class CheckpointRestController
{
    private ICheckpointService checkpointService;

    @Autowired
    public CheckpointRestController(ICheckpointService checkpointService) {
        this.checkpointService = checkpointService;
    }

    @GetMapping("/access-pass/verify-control-code/{control-code}")
    public ResponseEntity<?> getAccessPassByControlCode(@PathVariable("control-code") String controlCode) {
        ResponseEntity response = null;
        try {
            final AccessPass accessPass = this.checkpointService.retrieveAccessPassByControlCode(controlCode);
            RapidPass rapidPass = (null != accessPass) ? RapidPass.buildFrom(accessPass) : null;
            response = new ResponseEntity(rapidPass, HttpStatus.OK);
        }
        catch (Exception e) {
            log.error(e.getMessage(),e);
            response = new ResponseEntity(e,HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return response;
    }

    @GetMapping("/access-pass/verify-plate-no/{plate-no}")
    public ResponseEntity<?> getAccessPassByPlateNumber(@PathVariable("plate-no") String plateNo) {
        ResponseEntity response = null;
        try {
            final AccessPass accessPass = this.checkpointService.retrieveAccessPassByPlateNo(plateNo);
            RapidPass rapidPass = (null != accessPass) ? RapidPass.buildFrom(accessPass) : null;
            response = new ResponseEntity(rapidPass, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            response = new ResponseEntity(e,HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return response;
    }

    public ResponseEntity getAccessPassByQrCode(String qrCode)
    {
        ResponseEntity response = null;
        try
        {
            final AccessPass accessPass = checkpointService.retrieveAccessPassByQrCode(qrCode);
            response = new ResponseEntity(accessPass, HttpStatus.OK);
        }
        catch (Exception e)
        {
            log.error(e.getMessage(),e);
            response = new ResponseEntity(e,HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return response;
    }
}
