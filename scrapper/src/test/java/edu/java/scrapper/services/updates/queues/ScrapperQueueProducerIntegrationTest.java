package edu.java.scrapper.services.updates.queues;

import edu.java.scrapper.api.models.request.LinkUpdateRequest;
import edu.java.scrapper.configurations.ApplicationConfig;
import java.net.URI;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DirtiesContext
public class ScrapperQueueProducerIntegrationTest extends IntegrationTest {
    @Autowired
    private KafkaTemplate<String, LinkUpdateRequest> kafkaTemplate;

    @Autowired
    private ApplicationConfig applicationConfig;

    @Test
    void sendUpdate() {
        // Arrange
        LinkUpdateRequest updateRequest = new LinkUpdateRequest(
            1L,
            URI.create("https://github.com/author/repo/"),
            "test",
            List.of(1L, 2L, 3L)
        );
        ScrapperQueueProducer producer = new ScrapperQueueProducer(kafkaTemplate, applicationConfig);

        // Act
        producer.sendUpdate(updateRequest);

        // Assert
        assertThat(receiveFromKafkaTopic(applicationConfig.kafka().topicName())).isEqualTo(updateRequest);
    }

    private Map<String, Object> getConsumerProps() {
        Map<String, Object> props = KafkaTestUtils.consumerProps(applicationConfig.kafka().bootstrapServers(), "test-group", "false");

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, applicationConfig.kafka().bootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "test-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, LinkUpdateRequest.class);
        props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, "false");
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

        return props;
    }

    private Object receiveFromKafkaTopic(String topic) {
        try (Consumer<String, Object> consumer = new KafkaConsumer<>(getConsumerProps())) {
            consumer.subscribe(Collections.singletonList(topic));
            ConsumerRecords<String, Object> records = consumer.poll(Duration.ofSeconds(5));
            assertThat(records).isNotEmpty();
            ConsumerRecord<String, Object> record = records.iterator().next();
            return record.value();
        }
    }
}
