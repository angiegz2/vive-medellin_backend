-- Script para crear la base de datos ViveMedellin en PostgreSQL
-- Ejecutar con usuario postgres

-- Crear la base de datos si no existe
SELECT 'CREATE DATABASE vivemedellin'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'vivemedellin')\gexec

-- Conectarse a la base de datos vivemedellin
\c vivemedellin;

-- Crear extensiones útiles
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pg_trgm";

-- Comentario sobre la base de datos
COMMENT ON DATABASE vivemedellin IS 'Base de datos para la aplicación ViveMedellin - Gestión de eventos culturales y sociales de Medellín';

-- Las tablas serán creadas automáticamente por Hibernate/JPA al iniciar la aplicación
-- con spring.jpa.hibernate.ddl-auto=update

-- Verificar que la base de datos fue creada correctamente
SELECT current_database() as database_name, 
       current_user as current_user, 
       version() as postgresql_version;