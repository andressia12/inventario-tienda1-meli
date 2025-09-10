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

@RestController
@RequestMapping("/stock/tienda1")
public class StockController {
    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @PostMapping("/writer")
    public ResponseEntity<ApiResponse<String>> writerStock(
            @RequestBody StockRequestDto request,
            @RequestHeader(value = "X-Request-Id", required = false) String requestId) {

        if (request.getIdProducto() == null || request.getIdProducto() <= 0 || request.getCantidad() == null) {
            throw new IllegalArgumentException("idProducto y cantidad son requeridos y deben ser válidos");
        }

        stockService.writerStock(request, requestId);

    return ResponseEntity.ok(ApiResponse.success("Stock actualizado en tienda local y evento publicado", null));        }
}
