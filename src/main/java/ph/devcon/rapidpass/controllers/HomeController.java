package ph.devcon.rapidpass.controllers;

import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

/**
 * Home redirection to swagger api documentation
 */
@Controller
public class HomeController {

    @SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
    @Autowired(required = false)
    private BuildProperties buildProperties;

    @RequestMapping(method = RequestMethod.GET, value = "/")
    public String index() {
        return "redirect:swagger-ui.html";
    }

    /**
     * Exposes build information.
     *
     * @return build info in json fmt
     */
    @GetMapping("/version")
    public HttpEntity<Map<String, String>> getVersion() {
        if (buildProperties == null) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(ImmutableMap.of("version",
                String.format("%s.%d", buildProperties.getVersion(), buildProperties.getTime().getEpochSecond())));
    }
}
