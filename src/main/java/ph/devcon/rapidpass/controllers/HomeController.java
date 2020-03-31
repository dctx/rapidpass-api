package ph.devcon.rapidpass.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Home redirection to swagger api documentation 
 */
@Controller
public class HomeController
{
    @RequestMapping(method = RequestMethod.GET,value = "/")
    public String index() {
        return "redirect:swagger-ui.html";
    }
}
