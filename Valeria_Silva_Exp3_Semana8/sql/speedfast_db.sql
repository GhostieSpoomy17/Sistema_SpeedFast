-- SpeedFast: esquema de la actividad sumativa, semana 8.
-- No borra tablas ni registros existentes.
CREATE DATABASE IF NOT EXISTS speedfast_db CHARACTER SET utf8mb4;
USE speedfast_db;

CREATE TABLE IF NOT EXISTS repartidores (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS pedidos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    direccion VARCHAR(100) NOT NULL,
    tipo ENUM('COMIDA', 'ENCOMIENDA', 'EXPRESS') NOT NULL,
    estado ENUM('PENDIENTE', 'EN_REPARTO', 'ENTREGADO') NOT NULL DEFAULT 'PENDIENTE'
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS entregas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_pedido INT NOT NULL,
    id_repartidor INT NOT NULL,
    fecha DATE NOT NULL,
    hora TIME NOT NULL,
    CONSTRAINT fk_entregas_pedido FOREIGN KEY (id_pedido)
        REFERENCES pedidos(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT fk_entregas_repartidor FOREIGN KEY (id_repartidor)
        REFERENCES repartidores(id) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB;

SHOW TABLES;
