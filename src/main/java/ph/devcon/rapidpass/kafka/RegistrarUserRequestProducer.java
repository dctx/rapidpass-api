package ph.devcon.rapidpass.kafka;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ph.devcon.rapidpass.entities.RegistrarUser;

@Service
@AllArgsConstructor
@Slf4j
public class RegistrarUserRequestProducer {

    @Value("${topic.new-registrar-user-requests}")
    private String TOPIC;

    private final KafkaTemplate<String, RegistrarUser> kafkaTemplate;

    @Autowired
    public RegistrarUserRequestProducer(KafkaTemplate<String, RegistrarUser> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(String key, RegistrarUser message) {
        this.kafkaTemplate.send(this.TOPIC, key, message);
        log.debug(String.format("Sent request -> %s", message));
    }
}
