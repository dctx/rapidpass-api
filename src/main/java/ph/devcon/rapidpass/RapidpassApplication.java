package ph.devcon.rapidpass;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
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
