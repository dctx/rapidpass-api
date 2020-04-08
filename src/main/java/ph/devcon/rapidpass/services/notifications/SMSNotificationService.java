package ph.devcon.rapidpass.services.notifications;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
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

@Service
@Qualifier("sms")
@Slf4j
@RequiredArgsConstructor
@Setter
public class SMSNotificationService implements NotificationService {

    @NonNull
    private final RestTemplate restTemplate;

    @Value("${semaphore.key}")
    private String apiKey;

    @Value("${semaphore.url}")
    private String url;

    @Value("${semaphore.sender:RAPIDPASS}")
    private String semaphoreSender;

    @Override
    public void send(NotificationMessage message) throws NotificationException {
        log.debug("sending SMS msg to {}", message.getTo());
        if (StringUtils.isEmpty(this.apiKey)) throw new NotificationException("api key is not provided");
        if (StringUtils.isEmpty(this.url)) throw new NotificationException("url is not provided");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED); // important!
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>(); // important!

        params.add("apikey", this.apiKey);
        params.add("number", formatNumber(message.getTo()));
        params.add("message", message.getMessage());
        params.add("sendername", semaphoreSender);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(this.url, request, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new NotificationException("Error POSTing to semaphore API");
        }


        log.info("response: {}", response.getBody());

        // todo: more checks on semaphore response...


        log.debug("  SMS msg sent! {}", message.getTo());
    }

    protected String formatNumber(String phone) {
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        try {
            PhoneNumber phoneNumber = phoneUtil.parse(phone, "PH");
            return phoneUtil.format(phoneNumber, PhoneNumberFormat.E164);
        } catch (NumberParseException e) {
            log.error("Error parsing mobile " + phone, e);
        }

        // return un-parseable number as it is
        return phone;
    }
}
