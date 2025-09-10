package com.meli.inventario.tienda1.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.meli.inventario.tienda1.Dto.ApiResponse;
import com.meli.inventario.tienda1.Dto.StockRequestDto;
import com.meli.inventario.tienda1.Service.StockService;

/**
 * Controlador REST para la gestión de inventario de la Tienda 1.
 * 
 * <p>Este controlador maneja las operaciones de escritura de stock para la tienda local,
 * incluyendo la actualización del inventario local y la publicación de eventos
 * asíncronos para sincronización con el sistema central.</p>
 * 
 * <p>La tienda 1 utiliza una base de datos SQLite local y se comunica con el sistema
 * central a través de RabbitMQ para mantener la consistencia de datos.</p>
 * 
 * @author Sistema de Inventario MELI
 * @version 1.0
 * @since 2024
 */
@RestController
@RequestMapping("/stock/tienda1")
public class StockController {
    private final StockService stockService;

    /**
     * Constructor del controlador de stock.
     * 
     * @param stockService servicio para la gestión de operaciones de stock
     */
    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    /**
     * Actualiza el stock de un producto en la tienda local.
     * 
     * <p>Este endpoint realiza las siguientes operaciones:</p>
     * <ul>
     *   <li>Valida que el ID del producto y la cantidad sean válidos</li>
     *   <li>Actualiza el inventario en la base de datos SQLite local</li>
     *   <li>Publica un evento asíncrono para sincronizar con el sistema central</li>
     * </ul>
     * 
     * <p>La operación es transaccional, garantizando que tanto la actualización local
     * como la publicación del evento se ejecuten correctamente.</p>
     * 
     * @param request objeto que contiene el ID del producto y la nueva cantidad
     * @param requestId identificador único de la petición para trazabilidad (opcional)
     * @return ResponseEntity con el resultado de la operación
     * @throws IllegalArgumentException si el ID del producto o la cantidad no son válidos
     * @throws IllegalStateException si no se puede actualizar el inventario local
     * 
     * @see StockRequestDto
     * @see ApiResponse
     */
    @PostMapping("/writer")
    public ResponseEntity<ApiResponse<String>> writerStock(
            @RequestBody StockRequestDto request,
            @RequestHeader(value = "X-Request-Id", required = false) String requestId) {

        if (request.getIdProducto() == null || request.getIdProducto() <= 0 || request.getCantidad() == null) {
            throw new IllegalArgumentException("idProducto y cantidad son requeridos y deben ser válidos");
        }

        stockService.writerStock(request, requestId);

        return ResponseEntity.ok(ApiResponse.success("Stock actualizado en tienda local y evento publicado", null));
    }
}
