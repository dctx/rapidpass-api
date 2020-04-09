package ph.devcon.rapidpass.kafka;

import lombok.AllArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ph.devcon.rapidpass.messaging.models.RapidPassMessage;
import ph.devcon.rapidpass.models.RapidPassRequest;

@Service
@CommonsLog(topic = "Producer Logger")
@AllArgsConstructor
public class KafkaProducer {

    @Value("${topic.name}")
    private String TOPIC;

    private final KafkaTemplate<String, RapidPassRequest> kafkaTemplate;

    @Autowired
    public KafkaProducer(KafkaTemplate<String, RapidPassRequest> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(RapidPassRequest message) {
        this.kafkaTemplate.send(this.TOPIC, message.getIdentifierNumber(), message);
        log.debug(String.format("Produced user -> %s", message));
    }
}
