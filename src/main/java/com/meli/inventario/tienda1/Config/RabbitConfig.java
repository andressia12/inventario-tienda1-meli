package com.meli.inventario.tienda1.Config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;

/**
 * Configuración de RabbitMQ para la Tienda 1.
 * 
 * <p>Esta clase configura los componentes necesarios para la comunicación
 * asíncrona entre la tienda local y el sistema central de inventario.</p>
 * 
 * <p>Configuración incluye:</p>
 * <ul>
 *   <li>Exchange directo para enrutamiento de mensajes</li>
 *   <li>Cola para eventos de actualización de stock</li>
 *   <li>Binding entre exchange y cola</li>
 *   <li>Convertidor JSON para serialización de mensajes</li>
 *   <li>Template de RabbitMQ para envío de mensajes</li>
 * </ul>
 * 
 * <p>La tienda 1 actúa como productor de eventos, enviando actualizaciones
 * de stock al sistema central.</p>
 * 
 * @author Sistema de Inventario MELI
 * @version 1.0
 * @since 2024
 */
public class RabbitConfig {
    /** Nombre del exchange para eventos de stock */
    public static final String EXCHANGE = "stock.exchange";
    
    /** Nombre de la cola para eventos de actualización de stock */
    public static final String QUEUE = "stock.updated.queue";
    
    /** Clave de enrutamiento para eventos de stock */
    public static final String ROUTING_KEY = "stock.updated";

    /**
     * Configura el exchange directo para enrutamiento de mensajes de stock.
     * 
     * @return DirectExchange configurado
     */
    @Bean
    public DirectExchange stockExchange() {
        return new DirectExchange(EXCHANGE);
    }

    /**
     * Configura la cola para eventos de actualización de stock.
     * 
     * @return Queue configurada como durable
     */
    @Bean
    public Queue stockUpdatedQueue() {
        return new Queue(QUEUE, true);
    }

    /**
     * Configura el binding entre la cola y el exchange.
     * 
     * @param stockUpdatedQueue cola de eventos de stock
     * @param stockExchange exchange de stock
     * @return Binding configurado
     */
    @Bean
    public Binding bindingStockUpdated(Queue stockUpdatedQueue, DirectExchange stockExchange) {
        return BindingBuilder.bind(stockUpdatedQueue).to(stockExchange).with(ROUTING_KEY);
    }

    /**
     * Configura el convertidor JSON para serialización de mensajes.
     * 
     * @return Jackson2JsonMessageConverter configurado
     */
    @Bean
    public Jackson2JsonMessageConverter producerJackson2MessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * Configura el convertidor JSON alternativo para compatibilidad.
     * 
     * @return Jackson2JsonMessageConverter configurado
     */
    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * Configura el template de RabbitMQ para envío de mensajes.
     * 
     * @param connectionFactory factory de conexiones RabbitMQ
     * @param converter convertidor de mensajes JSON
     * @return RabbitTemplate configurado
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, 
                                         Jackson2JsonMessageConverter converter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(converter);
        return template;
    }
}
