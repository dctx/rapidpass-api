package ph.devcon.rapidpass.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;

/**
 * Manually expose swagger doc
 */
@CrossOrigin
@RestController
@RequestMapping("/spec")
@Slf4j
public class SwaggerDocController {

    @GetMapping(value = "", produces = "application/yml")
    public byte[] getSwaggerSpec() {
        InputStream in = getClass().getResourceAsStream("/rapidpass-openapi.yaml");
        try {
            return StreamUtils.copyToByteArray(in);
        } catch (IOException e) {
//            log.error(e.getMessage());
        }
        return null;
    }
}
