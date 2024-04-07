package edu.java.scrapper.services.updates;

import edu.java.scrapper.api.models.request.LinkUpdateRequest;
import edu.java.scrapper.clients.BotWebClient;
import edu.java.scrapper.configurations.ApplicationConfig;
import edu.java.scrapper.services.updates.queues.ScrapperQueueProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final ApplicationConfig applicationConfig;

    private final BotWebClient botWebClient;
    private final ScrapperQueueProducer scrapperQueueProducer;

    public void sendUpdate(LinkUpdateRequest updateRequest) {
        if (applicationConfig.useQueue()) {
            scrapperQueueProducer.sendUpdate(updateRequest);
        } else {
            botWebClient.sendUpdate(updateRequest);
        }
    }
}
