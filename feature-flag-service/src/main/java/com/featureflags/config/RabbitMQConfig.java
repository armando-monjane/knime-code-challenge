package com.featureflags.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    private static final Logger logger = LoggerFactory.getLogger(RabbitMQConfig.class);

    public static final String EXCHANGE_NAME = "feature-flags";
    public static final String QUEUE_NAME = "feature-flag-updates";
    public static final String ROUTING_KEY = "flag.update";

    /**
     * Create the exchange for feature flag events
     */
    @Bean
    public TopicExchange featureFlagsExchange() {
        return new TopicExchange(EXCHANGE_NAME, true, false);
    }

    /**
     * Create the queue for feature flag updates
     */
    @Bean
    public Queue featureFlagsQueue() {
        return QueueBuilder.durable(QUEUE_NAME)
                .withArgument("x-message-ttl", 86400000) // 24 hours TTL
                .withArgument("x-max-length", 10000) // Max 10k messages
                .build();
    }

    /**
     * Bind the queue to the exchange with routing key
     */
    @Bean
    public Binding featureFlagsBinding(Queue featureFlagsQueue, TopicExchange featureFlagsExchange) {
        return BindingBuilder.bind(featureFlagsQueue)
                .to(featureFlagsExchange)
                .with(ROUTING_KEY);
    }

    /**
     * Configure JSON message converter
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
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

        // For reliable delivery, publisher confirms can be handled via CorrelationData futures in Spring AMQP 3+.
        // Deprecated confirm callback removed to avoid build warnings.
        // See: https://docs.spring.io/spring-amqp/docs/current/reference/html/#publisher-confirms
        logger.debug("RabbitTemplate configured with JSON message converter");

        return rabbitTemplate;
    }
}
