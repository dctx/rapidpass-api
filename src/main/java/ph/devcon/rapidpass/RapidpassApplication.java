package ph.devcon.rapidpass;

import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import ph.devcon.rapidpass.services.email.EmailPayload;
import ph.devcon.rapidpass.services.email.MailGunEmailService;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.concurrent.ExecutionException;

@SpringBootApplication
@EnableSwagger2
@ComponentScan(basePackages = {
    "ph.devcon.rapidpass",
    "ph.devcon.rapidpass_api.controllers" ,
    "ph.devcon.rapidpass-api.configurations"
})
public class RapidpassApplication implements CommandLineRunner
{
    @Override
    public void run(String... arg0) {
        if (arg0.length > 0 && arg0[0].equals("exitcode")) {
            throw new RapidpassApplication.ExitException();
        }
    }
    
    public static void main(String[] args)
    {
        boolean TRY_SEND_EMAIL = false;
        if (TRY_SEND_EMAIL) {
            try {
                JsonNode jsonNode = new MailGunEmailService(new EmailPayload()).send();
                System.out.println("MailGun Response: ");
                System.out.println(jsonNode);
            } catch (UnirestException e) {
                e.printStackTrace();
            }
        }

        new SpringApplication(RapidpassApplication.class).run(args);
    }
    
    class ExitException extends RuntimeException implements ExitCodeGenerator
    {
        private static final long serialVersionUID = 1L;
        
        @Override
        public int getExitCode() {
            return 10;
        }
        
    }

}
