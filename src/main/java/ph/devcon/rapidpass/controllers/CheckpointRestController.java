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
import ph.devcon.rapidpass.api.models.AccessPass;
import ph.devcon.rapidpass.services.CheckpointService;

@CrossOrigin
@RestController
@Slf4j
@Api(tags = "checkpoint")
public class CheckpointRestController implements CheckpointApi
{
    @Autowired
    private CheckpointService checkpointService;
    
    @Override
    public ResponseEntity getAccessPassByControlCode(String controlCode)
    {
        ResponseEntity response = null;
        try
        {
            final AccessPass accessPass = checkpointService.retrieveAccessPassByControlCode(controlCode);
            response = new ResponseEntity(accessPass, HttpStatus.OK);
        }
        catch (Exception e)
        {
            log.error(e.getMessage(),e);
            response = new ResponseEntity(e,HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return response;
    }
    
    @Override
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
    
    @Override
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
