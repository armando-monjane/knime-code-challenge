package com.moviesearch.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "feature-flags";
    public static final String QUEUE_NAME = "feature-flag-updates";
    public static final String ROUTING_KEY = "flag.update";

    /**
     * Configure JSON message converter for RabbitMQ messages
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter(objectMapper);
        converter.setCreateMessageIds(true);
        return converter;
    }

    /**
     * Configure RabbitTemplate with JSON converter
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter jsonMessageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter);
        return rabbitTemplate;
    }

    /**
     * Declare the queue that we'll listen to
     */
    @Bean
    public Queue featureFlagsQueue() {
        return QueueBuilder.durable(QUEUE_NAME)
                .withArgument("x-message-ttl", 86400000) // 24 hours TTL
                .withArgument("x-max-length", 10000) // Max 10k messages
                .build();
    }
}
