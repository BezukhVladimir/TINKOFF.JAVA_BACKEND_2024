package edu.java.bot.listeners;

import edu.java.bot.api.models.requests.LinkUpdateRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;

@Slf4j
public class ScrapperDLQListener {
    @KafkaListener(topics = "${app.kafka.badResponseTopicName}", groupId = "${app.kafka.consumer.group-id}")
    public void listenBadResponses(LinkUpdateRequest update) {
        log.info(update.toString());
    }
}
