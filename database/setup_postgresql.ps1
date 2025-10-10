# Script de configuracion de PostgreSQL para ViveMedellin
# Ejecutar este script en PowerShell como administrador

Write-Host "=== Configuracion de PostgreSQL para ViveMedellin ===" -ForegroundColor Green

# Verificar si PostgreSQL esta instalado
try {
    $pgVersion = psql --version
    Write-Host "PostgreSQL encontrado: $pgVersion" -ForegroundColor Green
} catch {
    Write-Host "PostgreSQL no esta instalado o no esta en PATH" -ForegroundColor Red
    Write-Host "Por favor instalar PostgreSQL desde: https://www.postgresql.org/download/" -ForegroundColor Yellow
    exit 1
}

Write-Host ""
Write-Host "Configuracion de la base de datos:" -ForegroundColor Cyan
Write-Host "- Host: localhost" -ForegroundColor White
Write-Host "- Puerto: 5432" -ForegroundColor White
Write-Host "- Usuario: postgres" -ForegroundColor White
Write-Host "- Contraseña: root12345" -ForegroundColor White
Write-Host "- Base de datos: vivemedellin" -ForegroundColor White

Write-Host ""
Write-Host "Creando base de datos..." -ForegroundColor Cyan

# Crear la base de datos
$env:PGPASSWORD = "root12345"

try {
    # Intentar crear la base de datos
    psql -h localhost -U postgres -c "CREATE DATABASE vivemedellin;" 2>$null
    if ($LASTEXITCODE -eq 0) {
        Write-Host "Base de datos 'vivemedellin' creada exitosamente" -ForegroundColor Green
    } else {
        Write-Host "La base de datos 'vivemedellin' probablemente ya existe" -ForegroundColor Yellow
    }

    # Conectar y crear extensiones
    psql -h localhost -U postgres -d vivemedellin -c "CREATE EXTENSION IF NOT EXISTS ""uuid-ossp"";"
    psql -h localhost -U postgres -d vivemedellin -c "CREATE EXTENSION IF NOT EXISTS ""pg_trgm"";"
    
    Write-Host "Extensiones de PostgreSQL configuradas" -ForegroundColor Green

    # Verificar conexion
    $result = psql -h localhost -U postgres -d vivemedellin -t -c "SELECT current_database();"
    if ($result.Trim() -eq "vivemedellin") {
        Write-Host "Conexion a la base de datos verificada" -ForegroundColor Green
    }

} catch {
    Write-Host "Error al crear la base de datos" -ForegroundColor Red
    Write-Host "Verifica que PostgreSQL este ejecutandose y las credenciales sean correctas" -ForegroundColor Yellow
    exit 1
}

Write-Host ""
Write-Host "=== Configuracion completada exitosamente ===" -ForegroundColor Green
Write-Host ""
Write-Host "Para usar PostgreSQL:" -ForegroundColor Cyan
Write-Host "  mvn spring-boot:run -Dspring.profiles.active=prod" -ForegroundColor White
Write-Host ""
Write-Host "Para usar H2 (desarrollo):" -ForegroundColor Cyan
Write-Host "  mvn spring-boot:run -Dspring.profiles.active=dev" -ForegroundColor White
Write-Host ""
Write-Host "Para usar PostgreSQL por defecto, edita application.properties:" -ForegroundColor Cyan
Write-Host "  spring.profiles.active=prod" -ForegroundColor White