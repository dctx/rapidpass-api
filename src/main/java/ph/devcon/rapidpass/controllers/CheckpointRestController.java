package ph.devcon.rapidpass.controllers;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import ph.devcon.rapidpass.api.controllers.CheckpointApi;
import ph.devcon.rapidpass.api.models.AccessPass;
import ph.devcon.rapidpass.repositories.AccessPassRepository;

@CrossOrigin
@RestController
@Slf4j
@Api(tags = "checkpoint")
public class CheckpointRestController implements CheckpointApi
{
    @Override
    public ResponseEntity<AccessPass> getAccessPassByControlCode(String controlCode)
    {
        return null;
    }
    
    @Override
    public ResponseEntity<AccessPass> getAccessPassByPlateNumber(String plateNo)
    {
        return null;
    }
    
    @Override
    public ResponseEntity<AccessPass> getAccessPassByQrCode(String qrCode)
    {
        return null;
    }
}
