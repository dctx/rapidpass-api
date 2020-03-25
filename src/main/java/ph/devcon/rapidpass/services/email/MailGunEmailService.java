package ph.devcon.rapidpass.services.email;

import java.io.File;
import java.util.Date;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.body.MultipartBody;

public class MailGunEmailService implements EmailService<EmailPayload, JsonNode> {

    private EmailPayload payload;

    public MailGunEmailService(EmailPayload payload) {
        this.payload = payload;
    }

    @Override
    public JsonNode send() throws UnirestException {
        System.out.println("Sending...");

        String YOUR_DOMAIN_NAME = "rapidpass.ph";

        /*
          For testing, attached to Darren's account - for later replacement once provisioned by the DCTx team.

          API restrictions- Free 5000 emails per month for the first 3 months. Be gentle, onegaishimasu.
         */
        String API_KEY = "6b796e4df8ff29f68b84f4fb96f151de-ed4dc7c4-42e4bede";

        /*
          Note: Sandbox domains are restricted to authorized recipients only.
         */
        String SANDBOX_URL = "https://api.mailgun.net/v3/sandbox8ebc1f23c6fb46c9ac1e00750239bcb5.mailgun.org/messages";
        String ACTUAL_URL = "https://api.mailgun.net/v3/" + YOUR_DOMAIN_NAME + "/messages";

        String SANDBOX_SOURCE_EMAIL = "no-reply@sandbox8ebc1f23c6fb46c9ac1e00750239bcb5.mailgun.org";

        /*
          For actual usage.
         */

        String SENDER = "RapidPass";
        String SOURCE_EMAIL = SANDBOX_SOURCE_EMAIL;

        File value = new File("resources/test-pdf.pdf");

        String timestamp = new Date().toString();

        MultipartBody httpRequestWithBody = Unirest.post(SANDBOX_URL)
                .basicAuth("api", API_KEY)
                .queryString("from", SENDER + " <" + SOURCE_EMAIL + ">")
                .queryString("to", "darren.sapalo@gmail.com")
                .queryString("subject", "RapidPass Registration")
                .queryString("text", payload.toString() + "\n" + timestamp)
                .field("attachment", value);

        HttpResponse<JsonNode> request = httpRequestWithBody.asJson();

        return request.getBody();
    }
}
