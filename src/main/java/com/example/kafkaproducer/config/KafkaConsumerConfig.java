package com.example.kafkaproducer.config;


import com.example.kafkaproducer.model.Profile;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.SeekToCurrentErrorHandler;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConsumerConfig {

    @Value(value = "${kafka.bootstrapAddress}")
    private String bootstrapAddress;


    public ConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                bootstrapAddress);
        props.put(
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class);
        props.put(
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }


//    @KafkaListener(topics = "first_topic", groupId = "my-first-app")
//    public void listenGroupFoo(String message) {
//        System.out.println("Received Message in group my-first-app: " + message);
//    }

    @KafkaListener(topics = {"first_topic", "second_topic"}, groupId = "topic1-2-group")
    public void listen2topic(String message) {
        System.out.println("listen2topic - Received Message in listen2topic: " + message);
    }

    @KafkaListener(topics = "second_topic", groupId = "second-group")
    public void listenTopic(@Payload String message,
                            @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition) {
        System.out.println("Received Message in topic 2: " + message + " partition " + partition);
    }

    @Bean
    public ConsumerFactory<String, Profile> profileConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                bootstrapAddress);
        // to handle error deserializer
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, JsonDeserializer.class);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class.getName());
//        props.put(JsonDeserializer.KEY_DEFAULT_TYPE, "com.example.MyKey");
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, "com.example.kafkaproducer.model.Profile");
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "com.example");
        return new DefaultKafkaConsumerFactory<>(
                props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Profile>
    profileKafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, Profile> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(profileConsumerFactory());
        factory.setErrorHandler(new SeekToCurrentErrorHandler());
        return factory;
    }

    @KafkaListener(
            topics = "original_user_topic",
            groupId = "user-app",
            containerFactory = "profileKafkaListenerContainerFactory")
    public void greetingListener(Profile profile) {
        System.out.println("listener - user_topic " + profile.toString());
    }
}