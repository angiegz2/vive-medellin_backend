-- Crear extensiones para vivemedellin
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pg_trgm";

-- Verificar que la base de datos está lista
SELECT current_database() as database_name, 
       current_user as current_user, 
       version() as postgresql_version;