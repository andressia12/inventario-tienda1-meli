package com.meli.inventario.tienda1.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para las peticiones de actualización de stock.
 * 
 * <p>Este DTO encapsula los datos necesarios para actualizar el stock
 * de un producto en la tienda local, incluyendo el identificador del producto
 * y la nueva cantidad disponible.</p>
 * 
 * <p>Utilizado principalmente en los endpoints de escritura de stock
 * de la tienda local.</p>
 * 
 * @author Sistema de Inventario MELI
 * @version 1.0
 * @since 2024
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockRequestDto {
    /** Identificador único del producto a actualizar */
    private Integer idProducto;
    
    /** Nueva cantidad disponible en stock */
    private Integer cantidad;
}
