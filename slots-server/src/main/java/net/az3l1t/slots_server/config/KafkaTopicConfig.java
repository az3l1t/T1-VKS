package net.az3l1t.slots_server.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    public NewTopic createTopicNotification() {
        return TopicBuilder.name("notification-topic")
                .partitions(1)
                .replicas(1)
                .build();
    }

    public NewTopic createTopicEmployee() {
        return TopicBuilder.name("employee-topic")
                .partitions(1)
                .replicas(1)
                .build();
    }

    public NewTopic createTopicCancelling() {
        return TopicBuilder.name("cancelling-topic")
                .partitions(1)
                .replicas(1)
                .build();
    }
}
