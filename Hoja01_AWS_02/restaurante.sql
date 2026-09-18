CREATE DATABASE IF NOT EXISTS restaurante;
USE restaurante;

CREATE TABLE categorias (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    descripcion VARCHAR(150) NOT NULL
);

CREATE TABLE platos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    categoria_id INT NOT NULL,
    precio DECIMAL(5,2) NOT NULL,
    descripcion VARCHAR(200) NOT NULL,
    FOREIGN KEY (categoria_id) REFERENCES categorias(id)
);

INSERT INTO categorias (nombre, descripcion) VALUES
('Entrantes', 'Para abrir el apetito'),
('Arroces', 'Elaborados en paellera tradicional'),
('Carnes', 'Carnes a la brasa y guisadas'),
('Pescados', 'Pescado fresco de lonja'),
('Postres', 'Dulces caseros de la casa');

INSERT INTO platos (nombre, categoria_id, precio, descripcion) VALUES
('Croquetas de jamón', 1, 8.50, 'Croquetas caseras de jamón ibérico'),
('Pulpo a la gallega', 1, 12.90, 'Pulpo cocido con pimentón y aceite de oliva'),
('Paella valenciana', 2, 14.00, 'Arroz con pollo, conejo y verduras'),
('Arroz negro', 2, 15.50, 'Arroz con tinta de calamar y alioli'),
('Solomillo a la pimienta', 3, 18.90, 'Solomillo de ternera con salsa de pimienta'),
('Costillas al horno', 3, 16.50, 'Costillas de cerdo glaseadas al horno'),
('Lubina a la sal', 4, 19.90, 'Lubina fresca cocinada a la sal marina'),
('Bacalao al pil pil', 4, 17.50, 'Bacalao confitado en aceite de oliva y ajo'),
('Crema catalana', 5, 5.50, 'Crema quemada con azúcar caramelizado'),
('Tarta de queso', 5, 6.00, 'Tarta de queso horneada con mermelada de frutos rojos')
;
