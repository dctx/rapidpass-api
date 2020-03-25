package ph.devcon.rapidpass;

import com.mashape.unirest.http.JsonNode;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import ph.devcon.rapidpass.services.sms.SMSPayload;
import ph.devcon.rapidpass.services.sms.SemaphoreSMSService;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

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
    public void run(String... arg0) throws Exception {
        if (arg0.length > 0 && arg0[0].equals("exitcode")) {
            throw new RapidpassApplication.ExitException();
        }
    }
    
    public static void main(String[] args)
    {
        try {
            JsonNode send = new SemaphoreSMSService(new SMSPayload("09175983424", "Hi Darren")).send();
            System.out.println("SMS response:");
            System.out.println(send);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        new SpringApplication(RapidpassApplication.class).run(args);
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
