package com.example.transaction_service.kafkaConfig;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopic {

    @Bean
    public NewTopic transactionTopic(){
        return TopicBuilder.name("transaction_service")
                .partitions(1)
                .build();
    }
}
