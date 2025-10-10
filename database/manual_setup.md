# Comandos para configurar PostgreSQL manualmente para ViveMedellin

## PREREQUISITOS
# 1. Instalar PostgreSQL 12+ desde: https://www.postgresql.org/download/windows/
# 2. Durante la instalación, configurar la contraseña del usuario 'postgres' como 'root12345'
# 3. Asegurarse de que PostgreSQL esté en el PATH del sistema

## COMANDOS DE CONFIGURACIÓN

# 1. Verificar que PostgreSQL esté instalado y funcionando
psql --version

# 2. Conectar como usuario postgres (pedirá contraseña: root12345)
psql -U postgres -h localhost

# 3. Crear la base de datos (ejecutar en psql)
CREATE DATABASE vivemedellin;

# 4. Conectar a la nueva base de datos
\c vivemedellin;

# 5. Crear extensiones útiles
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pg_trgm";

# 6. Verificar que todo esté configurado correctamente
SELECT current_database(), current_user, version();

# 7. Salir de psql
\q

## CONFIGURACIÓN ALTERNATIVA USANDO COMANDOS DIRECTOS

# Si PostgreSQL está en el PATH, ejecutar estos comandos en PowerShell:
$env:PGPASSWORD = "root12345"
psql -h localhost -U postgres -c "CREATE DATABASE vivemedellin;"
psql -h localhost -U postgres -d vivemedellin -c "CREATE EXTENSION IF NOT EXISTS ""uuid-ossp"";"
psql -h localhost -U postgres -d vivemedellin -c "CREATE EXTENSION IF NOT EXISTS ""pg_trgm"";"

## VERIFICAR CONFIGURACIÓN
psql -h localhost -U postgres -d vivemedellin -c "SELECT current_database();"