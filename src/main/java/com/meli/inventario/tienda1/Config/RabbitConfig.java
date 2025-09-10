package com.meli.inventario.tienda1.Config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;

public class RabbitConfig {
    public static final String EXCHANGE = "stock.exchange";
    public static final String QUEUE = "stock.updated.queue";
    public static final String ROUTING_KEY = "stock.updated";

    @Bean
    public DirectExchange stockExchange() {
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    public Queue stockUpdatedQueue() {
        return new Queue(QUEUE, true);
    }

    @Bean
    public Binding bindingStockUpdated(Queue stockUpdatedQueue, DirectExchange stockExchange) {
        return BindingBuilder.bind(stockUpdatedQueue).to(stockExchange).with(ROUTING_KEY);
    }

    @Bean
    public Jackson2JsonMessageConverter producerJackson2MessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, 
                                         Jackson2JsonMessageConverter converter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(converter);
        return template;
    }
}
