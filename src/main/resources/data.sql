-- Productos disponibles en la tienda 1
INSERT INTO producto (nombre, descripcion, precio) VALUES 
('Computador', 'Computador de 14 pulgadas', 1200.00),
('Mouse', 'Mouse inalámbrico', 25.50),
('Teclado', 'Teclado mecánico', 75.00);

-- Inventario inicial de la tienda 1
INSERT INTO inventario (id_producto, cantidad_disponible, ultima_actualizacion) VALUES
(1, 10, CURRENT_TIMESTAMP),
(2, 50, CURRENT_TIMESTAMP),
(3, 20, CURRENT_TIMESTAMP);
