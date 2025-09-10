package com.meli.inventario.tienda1.Event;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockUpdateEvent implements Serializable{
    private Integer idProducto;
    private Integer idTienda;
    private Integer cantidad;
    private String requestId;
}
