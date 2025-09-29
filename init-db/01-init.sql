-- Script de inicialización de la base de datos para ViveMedellin
-- Este archivo se ejecuta automáticamente cuando se crea el contenedor PostgreSQL

-- Crear extensiones útiles
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Crear esquemas si es necesario
-- CREATE SCHEMA IF NOT EXISTS vivemedellin;

-- Insertar datos iniciales si es necesario
-- INSERT INTO usuarios (nombre, email) VALUES ('Admin', 'admin@vivemedellin.com');

-- Configurar permisos adicionales
GRANT ALL PRIVILEGES ON DATABASE mydatabase TO myuser;

-- Log de inicialización
DO $$
BEGIN
    RAISE NOTICE 'Base de datos ViveMedellin inicializada correctamente';
END $$;