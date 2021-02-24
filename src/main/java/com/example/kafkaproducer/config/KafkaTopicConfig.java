package com.example.kafkaproducer.config;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequestMapping(value = "api/demo")
public class KafkaTopicConfig {
//
    @Value(value = "${kafka.bootstrapAddress}")
    private String bootstrapAddress;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        return new KafkaAdmin(configs);
    }

//    @Bean
//    public NewTopic topic1() {
//        return new NewTopic("baeldung", 1, (short) 1);
//    }

//    @Bean
//    NewTopic topic1() {
//        return TopicBuilder.name("first_topic").build();
//    }
}
