package edu.java.bot.listeners;

import edu.java.bot.api.models.requests.LinkUpdateRequest;
import edu.java.bot.configurations.ApplicationConfig;
import java.net.URI;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.DescribeTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DirtiesContext
public class ScrapperQueueListenerIntegrationTest extends IntegrationTest {
    @Autowired
    private ApplicationConfig applicationConfig;

    @BeforeEach
    public void setup() {
        createTopic(applicationConfig.kafka().topicName());
        createTopic(applicationConfig.kafka().badResponseTopicName());
    }

    private void createTopic(String topicName) {
        Properties properties = new Properties();
        properties.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, applicationConfig.kafka().bootstrapServers());

        try (AdminClient adminClient = AdminClient.create(properties)) {
            DescribeTopicsResult result = adminClient.describeTopics(Collections.singleton(topicName));

            if (!result.topicNameValues().containsKey(topicName)) {
                NewTopic newTopic = new NewTopic(topicName, 1, (short) 1);
                adminClient.createTopics(Collections.singleton(newTopic)).all().get();
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to create topic " + topicName, e);
        }
    }

    @Test
    void correctUpdate() {
        // Arrange
        LinkUpdateRequest correctUpdate = new LinkUpdateRequest(
            1L,
            URI.create("https://github.com/author/repo/"),
            "test",
            List.of(1L, 2L, 3L)
        );

        // Act
        sendToKafkaTopic(applicationConfig.kafka().topicName(), correctUpdate);

        // Assert
        assertThat(receiveFromKafkaTopic(applicationConfig.kafka().topicName())).isEqualTo(correctUpdate);
    }

    @Test
    void incorrectMessage() {
        // Arrange
        String invalidMessage = "invalid message";

        // Act
        sendToKafkaTopic(applicationConfig.kafka().badResponseTopicName(), invalidMessage);

        // Assert
        assertThat(receiveFromKafkaTopic(applicationConfig.kafka().badResponseTopicName()).toString()).isEqualTo(invalidMessage);
    }


    private void sendToKafkaTopic(String topic, Object message) {
        try (KafkaProducer<Integer, Object> producer = new KafkaProducer<>(getJsonProducerProps())) {
            producer.send(new ProducerRecord<>(topic, 1, message));
        }
    }

    private Object receiveFromKafkaTopic(String topic) {
        try (Consumer<Integer, Object> consumer = new KafkaConsumer<>(getDLQConsumerProps())) {
            consumer.subscribe(Collections.singletonList(topic));
            ConsumerRecords<Integer, Object> records = consumer.poll(Duration.ofSeconds(5));
            assertThat(records).isNotEmpty();
            ConsumerRecord<Integer, Object> record = records.iterator().next();
            return record.value();
        }
    }

    private Map<String, Object> getJsonProducerProps() {
        Map<String, Object> props = new HashMap<>();

        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, applicationConfig.kafka().bootstrapServers());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return props;
    }

    private Map<String, Object> getDLQConsumerProps() {
        Map<String, Object> props = KafkaTestUtils.consumerProps(applicationConfig.kafka().bootstrapServers(), "test-group", "false");

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, applicationConfig.kafka().bootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "test-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.TYPE_MAPPINGS, applicationConfig.kafka().consumer().mappings());
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

        return props;
    }
}
