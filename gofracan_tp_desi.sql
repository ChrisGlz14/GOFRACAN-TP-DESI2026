CREATE DATABASE IF NOT EXISTS gofracan;
USE gofracan;

--  tabla  PROVINCIA

CREATE TABLE provincia (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE
);

--  tabla CIUDAD

CREATE TABLE ciudad (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    provincia_id BIGINT NOT NULL,
    CONSTRAINT fk_ciudad_provincia
        FOREIGN KEY (provincia_id)
        REFERENCES provincia(id)
);

-- PERSONA

CREATE TABLE persona (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    dni_cuit VARCHAR(11) NOT NULL UNIQUE,
    telefono VARCHAR(30),
    email VARCHAR(150),
    domicilio VARCHAR(200),
    ciudad_id BIGINT,

    CONSTRAINT fk_persona_ciudad
        FOREIGN KEY (ciudad_id)
        REFERENCES ciudad(id)
);

-- PROPIEDAD

CREATE TABLE propiedad (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    direccion VARCHAR(255) NOT NULL,
    tipo ENUM('CASA','DEPARTAMENTO','LOCAL','OTRO') NOT NULL,
    cantidad_ambientes INT,
    metros_cuadrados DOUBLE,
    descripcion TEXT,
    comodidades TEXT,

    estado_disponibilidad ENUM(
        'DISPONIBLE',
        'RESERVADA',
        'ALQUILADA',
        'INACTIVA'
    ) NOT NULL,

    eliminada BOOLEAN DEFAULT FALSE,

    propietario_id BIGINT NOT NULL,

    CONSTRAINT fk_propiedad_propietario
        FOREIGN KEY (propietario_id)
        REFERENCES persona(id)
);

-- HISTORIAL ESTADO PROPIEDAD

CREATE TABLE historial_estado_propiedad (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    estado ENUM(
        'DISPONIBLE',
        'RESERVADA',
        'ALQUILADA',
        'INACTIVA'
    ) NOT NULL,

    fecha_hora DATETIME NOT NULL,

    propiedad_id BIGINT NOT NULL,

    CONSTRAINT fk_hist_propiedad
        FOREIGN KEY (propiedad_id)
        REFERENCES propiedad(id)
);

-- PUBLICACION

CREATE TABLE publicacion (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    precio_mensual DECIMAL(15,2) NOT NULL,
    condiciones TEXT NOT NULL,
    fecha_publicacion DATE NOT NULL,

    estado ENUM(
        'ACTIVA',
        'PAUSADA',
        'FINALIZADA'
    ) NOT NULL,

    eliminada BOOLEAN DEFAULT FALSE,

    descripcion TEXT,

    propiedad_id BIGINT NOT NULL,

    CONSTRAINT fk_publicacion_propiedad
        FOREIGN KEY (propiedad_id)
        REFERENCES propiedad(id)
);

-- HISTORIAL ESTADO PUBLICACION

CREATE TABLE historial_estado_publicacion (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    estado ENUM(
        'ACTIVA',
        'PAUSADA',
        'FINALIZADA'
    ) NOT NULL,

    fecha_hora DATETIME NOT NULL,

    publicacion_id BIGINT NOT NULL,

    CONSTRAINT fk_hist_publicacion
        FOREIGN KEY (publicacion_id)
        REFERENCES publicacion(id)
);

-- VISITA

CREATE TABLE visita (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    fecha_hora DATETIME NOT NULL,

    estado ENUM(
        'PENDIENTE',
        'REALIZADA',
        'CANCELADA'
    ) NOT NULL,

    publicacion_id BIGINT NOT NULL,

    CONSTRAINT fk_visita_publicacion
        FOREIGN KEY (publicacion_id)
        REFERENCES publicacion(id)
);

-- CONTRATO

CREATE TABLE contrato (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    fecha_inicio DATE NOT NULL,
    duracion_meses INT NOT NULL,
    importe_mensual DECIMAL(10,2) NOT NULL,
    dia_vencimiento_mensual INT NOT NULL,

    descripcion TEXT,

    estado ENUM(
        'BORRADOR',
        'ACTIVO',
        'FINALIZADO',
        'RESCINDIDO'
    ) NOT NULL,

    eliminado BOOLEAN DEFAULT FALSE,

    propiedad_id BIGINT NOT NULL,
    propietario_id BIGINT NOT NULL,
    inquilino_id BIGINT NOT NULL,

    CONSTRAINT fk_contrato_propiedad
        FOREIGN KEY (propiedad_id)
        REFERENCES propiedad(id),

    CONSTRAINT fk_contrato_propietario
        FOREIGN KEY (propietario_id)
        REFERENCES persona(id),

    CONSTRAINT fk_contrato_inquilino
        FOREIGN KEY (inquilino_id)
        REFERENCES persona(id)
);

-- HISTORIAL ESTADO CONTRATO

CREATE TABLE historial_estado_contrato (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    estado ENUM(
        'BORRADOR',
        'ACTIVO',
        'FINALIZADO',
        'RESCINDIDO'
    ) NOT NULL,

    fecha_hora DATETIME NOT NULL,

    contrato_id BIGINT NOT NULL,

    CONSTRAINT fk_hist_contrato
        FOREIGN KEY (contrato_id)
        REFERENCES contrato(id)
);

-- FACTURA

CREATE TABLE factura (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    fecha_emision DATE NOT NULL,
    fecha_vencimiento DATE NOT NULL,

    importe DECIMAL(15,2) NOT NULL,

    estado ENUM(
        'PENDIENTE',
        'PAGADA',
        'VENCIDA',
        'ANULADA'
    ) NOT NULL,

    eliminado BOOLEAN DEFAULT FALSE,

    fecha_pago DATE,

    medio_pago ENUM(
        'TRANSFERENCIA',
        'EFECTIVO',
        'DEBITO',
        'CREDITO'
    ),

    importe_pagado DECIMAL(15,2),
    interes DECIMAL(15,2),

    concepto_facturado VARCHAR(255),

    contrato_id BIGINT NOT NULL,

    CONSTRAINT fk_factura_contrato
        FOREIGN KEY (contrato_id)
        REFERENCES contrato(id)
);

-- HISTORIAL ESTADO FACTURA

CREATE TABLE historial_estado_factura (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    estado ENUM(
        'PENDIENTE',
        'PAGADA',
        'VENCIDA',
        'ANULADA'
    ) NOT NULL,

    fecha_hora DATETIME NOT NULL,

    factura_id BIGINT NOT NULL,

    CONSTRAINT fk_hist_factura
        FOREIGN KEY (factura_id)
        REFERENCES factura(id)
);

-- INCIDENTE

CREATE TABLE incidente (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    titulo VARCHAR(255) NOT NULL,
    descripcion TEXT,

    categoria ENUM(
        'PLOMERIA',
        'ELECTRICIDAD',
        'GAS',
        'GENERAL'
    ) NOT NULL,

    fecha_alta DATETIME NOT NULL,

    prioridad ENUM(
        'BAJA',
        'MEDIA',
        'ALTA'
    ) NOT NULL,

    estado ENUM(
        'ABIERTO',
        'EN_PROCESO',
        'RESUELTO',
        'CANCELADO',
        'REABIERTO'
    ) NOT NULL,

    eliminado BOOLEAN DEFAULT FALSE,

    fecha_resolucion DATETIME,
    observaciones_resolucion TEXT,
    costo_resolucion DECIMAL(15,2),
    responsable_tecnico VARCHAR(255),

    contrato_id BIGINT NOT NULL,

    CONSTRAINT fk_incidente_contrato
        FOREIGN KEY (contrato_id)
        REFERENCES contrato(id)
);

-- HISTORIAL ESTADO INCIDENTE

CREATE TABLE historial_estado_incidente (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    estado ENUM(
        'ABIERTO',
        'EN_PROCESO',
        'RESUELTO',
        'CANCELADO',
        'REABIERTO'
    ) NOT NULL,

    fecha_hora DATETIME NOT NULL,

    incidente_id BIGINT NOT NULL,

    CONSTRAINT fk_hist_incidente
        FOREIGN KEY (incidente_id)
        REFERENCES incidente(id)
);

-- ─────────────────────────────────────────────────────────────
-- Datos de prueba
-- ─────────────────────────────────────────────────────────────

INSERT INTO provincia (nombre) VALUES 
('Buenos Aires'),
('Córdoba'),
('Santa Fe'),
('Mendoza');

INSERT INTO ciudad (nombre, provincia_id) VALUES 
('La Plata', 1),
('Mar del Plata', 1),
('Córdoba Capital', 2),
('Villa Carlos Paz', 2),
('Rosario', 3),
('Santa Fe Capital', 3),
('Mendoza Capital', 4);

INSERT INTO persona (nombre, apellido, dni_cuit, telefono, email, domicilio, ciudad_id) VALUES 
('Juan', 'Pérez', '20345678901', '221-4567890', 'juan.perez@email.com', 'Calle 50 N° 123', 1),
('María', 'González', '27456789012', '223-5678901', 'maria.gonzalez@email.com', 'Av. Colón 456', 1),
('Carlos', 'Rodríguez', '20567890123', '351-6789012', 'carlos.rodriguez@email.com', 'San Martín 789', 3),
('Ana', 'Martínez', '27678901234', '351-7890123', 'ana.martinez@email.com', 'Belgrano 321', 3),
('Luis', 'Fernández', '20789012345', '341-8901234', 'luis.fernandez@email.com', 'Mitre 654', 5),
('Laura', 'López', '27890123456', '342-9012345', 'laura.lopez@email.com', 'Rivadavia 987', 6),
('Pedro', 'Sánchez', '20901234567', '261-1234567', 'pedro.sanchez@email.com', 'San Martín 147', 7),
('Sofía', 'Ramírez', '27012345678', '261-2345678', 'sofia.ramirez@email.com', 'Las Heras 258', 7);
