package com.meli.inventario.tienda1.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockRequestDto {
    private Integer idProducto;
    private Integer cantidad;
}
