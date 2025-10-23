-- Insertar usuarios de ejemplo
INSERT INTO usuarios (rol, correo, username, password) VALUES ('ADMIN', 'admin@easysave.com', 'admin', 'admin123');
INSERT INTO usuarios (rol, correo, username, password) VALUES ('USER', 'juan@gmail.com', 'juan', 'juan123');
INSERT INTO usuarios (rol, correo, username, password) VALUES ('USER', 'maria@gmail.com', 'maria', 'maria123');

-- Insertar ingresos de ejemplo para el usuario juan (id=2)
INSERT INTO ingresos (nombre_ingreso, valor_ingreso, estado_ingreso, usuario_id) VALUES ('Salario', 3000000, 'fijo', 2);
INSERT INTO ingresos (nombre_ingreso, valor_ingreso, estado_ingreso, usuario_id) VALUES ('Freelance', 500000, 'variable', 2);
INSERT INTO ingresos (nombre_ingreso, valor_ingreso, estado_ingreso, usuario_id) VALUES ('Ventas Online', 200000, 'variable', 2);

-- Insertar gastos de ejemplo para el usuario juan (id=2)
INSERT INTO gastos (nombre_gasto, valor_gasto, estado_gasto, usuario_id) VALUES ('Arriendo', 800000, 'fijo', 2);
INSERT INTO gastos (nombre_gasto, valor_gasto, estado_gasto, usuario_id) VALUES ('Servicios', 200000, 'fijo', 2);
INSERT INTO gastos (nombre_gasto, valor_gasto, estado_gasto, usuario_id) VALUES ('Mercado', 400000, 'variable', 2);
INSERT INTO gastos (nombre_gasto, valor_gasto, estado_gasto, usuario_id) VALUES ('Entretenimiento', 150000, 'variable', 2);

-- Insertar ingresos de ejemplo para el usuario maria (id=3)
INSERT INTO ingresos (nombre_ingreso, valor_ingreso, estado_ingreso, usuario_id) VALUES ('Salario', 2500000, 'fijo', 3);
INSERT INTO ingresos (nombre_ingreso, valor_ingreso, estado_ingreso, usuario_id) VALUES ('Bonificación', 300000, 'variable', 3);

-- Insertar gastos de ejemplo para el usuario maria (id=3)
INSERT INTO gastos (nombre_gasto, valor_gasto, estado_gasto, usuario_id) VALUES ('Arriendo', 700000, 'fijo', 3);
INSERT INTO gastos (nombre_gasto, valor_gasto, estado_gasto, usuario_id) VALUES ('Transporte', 150000, 'fijo', 3);
INSERT INTO gastos (nombre_gasto, valor_gasto, estado_gasto, usuario_id) VALUES ('Alimentación', 300000, 'variable', 3);
