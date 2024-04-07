package edu.java.scrapper.services.updates.queues;

import edu.java.scrapper.api.models.request.LinkUpdateRequest;
import edu.java.scrapper.configurations.ApplicationConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScrapperQueueProducer {
    private final KafkaTemplate<String, LinkUpdateRequest> kafkaTemplate;
    private final ApplicationConfig applicationConfig;

    public void sendUpdate(LinkUpdateRequest update) {
        kafkaTemplate.send(applicationConfig.kafka().topicName(), update);
    }
}
