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

@Service
public class StockService {
    private final JdbcTemplate jdbcTemplate;
    private final RabbitTemplate rabbitTemplate;

    @Value("${tienda.id}")
    private int tiendaId;

    public StockService(JdbcTemplate jdbcTemplate, RabbitTemplate rabbitTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.rabbitTemplate = rabbitTemplate;
    }

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
