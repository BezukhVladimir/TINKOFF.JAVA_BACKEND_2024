package edu.java.bot.listeners;

import edu.java.bot.api.models.requests.LinkUpdateRequest;
import edu.java.bot.services.UpdatesListenersService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScrapperQueueListener {
    private final UpdatesListenersService updatesListenersService;

    @KafkaListener(topics = "${app.kafka.topicName}", groupId = "${app.kafka.consumer.group-id}")
    public void listen(LinkUpdateRequest update) {
        try {
            updatesListenersService.process(update);
        } catch (Exception ignored) {
        }
    }
}
