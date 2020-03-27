package ph.devcon.rapidpass.service.notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Service
@Qualifier("sms")
@Slf4j
@RequiredArgsConstructor
@Setter
public class SMSNotificationService implements NotificationService {

    private final RestTemplate restTemplate;

    @Value("${semaphore.key}")
    private String apiKey;

    @Value("${semaphore.url}")
    private String url;
    
    @Autowired
    public SMSNotificationService(RestTemplate mockRestTemplate)
    {
        restTemplate = mockRestTemplate;
    }
    
    @Override
    public void send(NotificationMessage message) throws NotificationException {
        if (StringUtils.isEmpty(this.apiKey)) throw new NotificationException("api key is not provided");
        if (StringUtils.isEmpty(this.url)) throw new NotificationException("url is not provided");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED); // important!
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>(); // important!

        params.add("apikey", this.apiKey);
        params.add("number", message.getTo());
        params.add("message", message.getMessage());
        String sender = message.getFrom();
        if (sender != null && !StringUtils.isEmpty(sender)) {
            params.add("sendername", sender);
        }

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(this.url, request, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new NotificationException("Error POSTing to semaphore API");
        }


        log.info("response: {}", response.getBody());

        // todo: more checks on semaphore response...
    }
}
