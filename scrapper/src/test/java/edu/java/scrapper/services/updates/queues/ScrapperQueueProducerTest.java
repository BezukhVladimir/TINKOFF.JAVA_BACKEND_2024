package edu.java.scrapper.services.updates.queues;

import edu.java.scrapper.api.models.request.LinkUpdateRequest;
import edu.java.scrapper.configurations.ApplicationConfig;
import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ScrapperQueueProducerTest {
    @Mock
    private KafkaTemplate<Integer, LinkUpdateRequest> kafkaTemplate;
    @Mock
    private ApplicationConfig applicationConfig;
    @Mock
    private ApplicationConfig.Kafka kafka;
    @InjectMocks
    private ScrapperQueueProducer scrapperQueueProducer;

    @Test
    void sendUpdate() {
        // Arrange
        LinkUpdateRequest update = new LinkUpdateRequest(
            1L,
            URI.create("https://github.com/author/repo/"),
            "test",
            List.of(1L, 2L, 3L)
        );
        when(kafkaTemplate.send(any(), any())).thenReturn(null);
        when(applicationConfig.kafka()).thenReturn(kafka);

        // Act
        scrapperQueueProducer.sendUpdate(update);

        // Assert
        verify(kafkaTemplate).send(any(), any());
    }
}
