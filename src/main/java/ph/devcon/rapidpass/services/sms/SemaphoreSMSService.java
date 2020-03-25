package ph.devcon.rapidpass.services.sms;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.request.HttpRequestWithBody;

import java.util.HashMap;
import java.util.Map;

public class SemaphoreSMSService implements SMSService<SMSPayload, JsonNode> {

    private SMSPayload payload;

    public SemaphoreSMSService(SMSPayload payload) {
        this.payload = payload;
    }

    @Override
    public JsonNode send() throws Exception {
        /*
          For testing, attached to Darren's account - for later replacement once provisioned by the DCTx team.

          API restrictions- I have only like 998 credits left, which I funded myself. Be gentle, onegaishimasu.
         */
        String API_KEY = "330c633d6cf0bdba5454129232dc27ca";

        String ACTUAL_URL = "https://api.semaphore.co/api/v4/messages";

        Map<String, Object> map = new HashMap<>();
        map.put("apikey", API_KEY);
        map.put("number", payload.getNumber());
        map.put("message", payload.getMessage());
        if (payload.isSenderEnabled()) {
            map.put("sendername", payload.getSenderName());
        }

        HttpRequestWithBody httpRequestWithBody = Unirest.post(ACTUAL_URL)
                .basicAuth("api", API_KEY)
                .queryString(map);

        HttpResponse<JsonNode> request = httpRequestWithBody.asJson();

        return request.getBody();

    }

}
