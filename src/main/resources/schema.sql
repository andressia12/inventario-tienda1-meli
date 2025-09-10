DROP TABLE IF EXISTS inventario;
DROP TABLE IF EXISTS producto;

CREATE TABLE producto (
    id_producto INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre TEXT NOT NULL,
    descripcion TEXT,
    precio REAL NOT NULL
);

CREATE TABLE inventario (
    id_inventario INTEGER PRIMARY KEY AUTOINCREMENT,
    id_producto INTEGER NOT NULL,
    cantidad_disponible INT NOT NULL,
    ultima_actualizacion TIMESTAMP,
    CONSTRAINT fk_inventario_producto FOREIGN KEY (id_producto) REFERENCES producto(id_producto)
);
