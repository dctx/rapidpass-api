package ph.devcon.rapidpass.controllers;

import io.swagger.annotations.Api;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import ph.devcon.rapidpass.api.controllers.CheckpointApi;
import ph.devcon.rapidpass.entities.AccessPass;
import ph.devcon.rapidpass.models.RapidPass;
import ph.devcon.rapidpass.services.CheckpointService;

@CrossOrigin
@RestController
@Slf4j
@Api(tags = "checkpoint")
public class CheckpointRestController
{
    @Autowired
    private CheckpointService checkpointService;

    public ResponseEntity<?> getAccessPassByControlCode(String controlCode)
    {
        ResponseEntity response = null;
        try
        {
            AccessPass accessPass = checkpointService.retrieveAccessPassByControlCode(controlCode);
            RapidPass rapidPass = RapidPass.buildFrom(accessPass);
            response = new ResponseEntity(rapidPass, HttpStatus.OK);
        }
        catch (Exception e)
        {
            log.error(e.getMessage(),e);
            response = new ResponseEntity(e,HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return response;
    }

    public ResponseEntity getAccessPassByPlateNumber(String plateNo)
    {
        ResponseEntity response = null;
        try
        {
            final AccessPass accessPass = checkpointService.retrieveAccessPassByLicenseNumber(plateNo);
            response = new ResponseEntity(accessPass, HttpStatus.OK);
        }
        catch (Exception e)
        {
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
