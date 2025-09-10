package com.meli.inventario.tienda1.Service;

import java.util.UUID;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.meli.inventario.tienda1.Config.RabbitConfig;
import com.meli.inventario.tienda1.Dto.StockRequestDto;
import com.meli.inventario.tienda1.Event.StockUpdateEvent;

/**
 * Servicio para la gestión de operaciones de stock en la Tienda 1.
 * 
 * <p>Este servicio maneja las operaciones de escritura de inventario para la tienda local,
 * incluyendo la actualización de la base de datos SQLite y la publicación de eventos
 * asíncronos para sincronización con el sistema central.</p>
 * 
 * <p>Características principales:</p>
 * <ul>
 *   <li>Operaciones transaccionales para garantizar consistencia</li>
 *   <li>Integración con RabbitMQ para comunicación asíncrona</li>
 *   <li>Validación de existencia de productos antes de actualizar</li>
 *   <li>Generación automática de IDs de petición para trazabilidad</li>
 * </ul>
 * 
 * <p>El servicio implementa el patrón de Event Sourcing para mantener
 * la sincronización con el sistema central de inventario.</p>
 * 
 * @author Sistema de Inventario MELI
 * @version 1.0
 * @since 2024
 */
@Service
public class StockService {
    private final JdbcTemplate jdbcTemplate;
    private final RabbitTemplate rabbitTemplate;

    @Value("${tienda.id}")
    private int tiendaId;

    /**
     * Constructor del servicio de stock.
     * 
     * @param jdbcTemplate template para operaciones de base de datos SQLite
     * @param rabbitTemplate template para envío de mensajes RabbitMQ
     */
    public StockService(JdbcTemplate jdbcTemplate, RabbitTemplate rabbitTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * Actualiza el stock de un producto en la tienda local y publica un evento de sincronización.
     * 
     * <p>Este método realiza las siguientes operaciones de forma transaccional:</p>
     * <ol>
     *   <li>Verifica que el producto exista en el inventario local</li>
     *   <li>Actualiza la cantidad disponible en la base de datos SQLite</li>
     *   <li>Publica un evento de actualización de stock via RabbitMQ</li>
     * </ol>
     * 
     * <p>Si cualquiera de estas operaciones falla, toda la transacción se revierte,
     * garantizando la consistencia de datos.</p>
     * 
     * @param request objeto que contiene el ID del producto y la nueva cantidad
     * @param requestId identificador único de la petición para trazabilidad (opcional)
     * @throws IllegalArgumentException si el producto no existe en el inventario local
     * @throws IllegalStateException si no se puede actualizar el inventario local
     * 
     * @see StockRequestDto
     * @see StockUpdateEvent
     */
    @Transactional
    public void writerStock(StockRequestDto request, String requestId) {
        // 1) Verificar existencia del producto en inventario local
        Integer current = jdbcTemplate.queryForObject(
                "SELECT cantidad_disponible FROM inventario WHERE id_producto = ?",
                new Object[]{request.getIdProducto()},
                Integer.class
        );

        if (current == null) {
            throw new IllegalArgumentException("Inventario no encontrado para producto=" + request.getIdProducto());
        }

        // 2) Actualizar inventario local (SQLite)
        int updated = jdbcTemplate.update(
                "UPDATE inventario SET cantidad_disponible = ?, ultima_actualizacion = datetime('now') " +
                        "WHERE id_producto = ?",
                request.getCantidad(), request.getIdProducto()
        );

        if (updated <= 0) {
            throw new IllegalStateException("No se actualizó el inventario local (producto=" + request.getIdProducto() + ")");
        }

        // 3) Publicar evento para central
        StockUpdateEvent event = new StockUpdateEvent(
                request.getIdProducto(),
                Integer.valueOf(1),
                request.getCantidad(),
                requestId != null ? requestId : UUID.randomUUID().toString()
        );
        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE, RabbitConfig.ROUTING_KEY, event);
    }
}
