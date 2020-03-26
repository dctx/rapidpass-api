package ph.devcon.rapidpass.service.notification;

import java.util.HashMap;
import java.util.Map;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequestWithBody;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Qualifier("sms")
public class SMSNotification implements NotificationService {

    @Value("${semaphore.key}")
    private String apiKey;

    @Value("${semaphore.url}")
    private String url;



    @Override
    public void send(NotificationMessage message) throws NotificationException {
        if (this.apiKey == null || this.apiKey == "") {
            throw new NotificationException("api key is not provided");
        }
        if (this.url == null || this.url == "") {
            throw new NotificationException("url is not provided");
        }
        // TODO: might need to change this a diff client? HTTPClient should be injected
        // This is hard to mock and test.
        Map<String, Object> entries = new HashMap<String, Object>();
        entries.put("apikey", this.apiKey);
        entries.put("number", message.getTo());
        entries.put("message", message.getMessage());
        String sender = message.getFrom();
        if (sender != null && sender != "") {
            entries.put("sendername", sender);
        }
        HttpRequestWithBody req = Unirest.post(this.url).
            basicAuth("api", this.apiKey).
            queryString(entries);

        try {
            HttpResponse<JsonNode> res = req.asJson();
            int status = res.getStatus();
            if (status != 200) {
                throw new NotificationException("SMS was not sent");
            }
            // TODO: semaphore's response is weird, but should check here. 
            // if (semStatus == "Failed" || semStatus == "Refunded") {
            //     throw new NotificationException("SMS Gateway was not able to send message");
            // }
        } catch(UnirestException e) {
            throw new NotificationException(e);
        }

    }
}