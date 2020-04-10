package ph.devcon.rapidpass.kafka;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ph.devcon.rapidpass.models.RapidPass;
import ph.devcon.rapidpass.models.RapidPassRequest;

@Service
@AllArgsConstructor
@Slf4j
public class RapidPassEventProducer {

    @Value("${topic.rapidpass-events}")
    private String TOPIC;

    private final KafkaTemplate<String, RapidPass> kafkaTemplate;

    @Autowired
    public RapidPassEventProducer(KafkaTemplate<String, RapidPass> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(String key, RapidPass message) {
        this.kafkaTemplate.send(this.TOPIC, key, message);
        log.debug(String.format("Sent event -> %s", message));
    }
}
