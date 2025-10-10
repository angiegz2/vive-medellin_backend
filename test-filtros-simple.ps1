# ============================================================================
# SCRIPT SIMPLE DE PRUEBA - FILTROS DE BÚSQUEDA
# ViveMedellin API - Testing de Endpoints
# ============================================================================

$BASE_URL = "http://localhost:8081/api"

Write-Host "=====================================================================" -ForegroundColor Cyan
Write-Host "   PRUEBAS DE FILTROS DE BÚSQUEDA - VIVEMEDELLIN API" -ForegroundColor Cyan
Write-Host "=====================================================================" -ForegroundColor Cyan
Write-Host ""

# ============================================================================
# PROBAR FILTROS DE BÚSQUEDA (Asumiendo que ya existen eventos en la BD)
# ============================================================================

Write-Host "Probando filtros de búsqueda..." -ForegroundColor Yellow
Write-Host ""

# 1. Listar todos los eventos
Write-Host "1. LISTAR TODOS LOS EVENTOS" -ForegroundColor Magenta
Write-Host "Endpoint: GET /api/eventos" -ForegroundColor Gray
try {
    $todos = Invoke-RestMethod -Uri "$BASE_URL/eventos" -Method Get
    Write-Host "Eventos totales encontrados: $($todos.totalElements)" -ForegroundColor Green
    if ($todos.content.Count -gt 0) {
        foreach ($evento in $todos.content) {
            Write-Host "  - $($evento.titulo)" -ForegroundColor White
        }
    } else {
        Write-Host "  (No hay eventos creados aún)" -ForegroundColor Yellow
    }
} catch {
    Write-Host "Error al listar eventos: $_" -ForegroundColor Red
}
Write-Host ""

# 2. Eventos Gratuitos
Write-Host "2. EVENTOS GRATUITOS" -ForegroundColor Magenta
Write-Host "Endpoint: GET /api/eventos/gratuitos" -ForegroundColor Gray
try {
    $gratuitos = Invoke-RestMethod -Uri "$BASE_URL/eventos/gratuitos" -Method Get
    Write-Host "Eventos gratuitos encontrados: $($gratuitos.totalElements)" -ForegroundColor Green
    foreach ($evento in $gratuitos.content) {
        Write-Host "  - $($evento.titulo) | Precio: `$$($evento.valorIngreso)" -ForegroundColor White
    }
} catch {
    Write-Host "Error al buscar eventos gratuitos" -ForegroundColor Red
}
Write-Host ""

# 3. Eventos Próximos
Write-Host "3. EVENTOS PRÓXIMOS (30 días)" -ForegroundColor Magenta
Write-Host "Endpoint: GET /api/eventos/proximos?dias=30" -ForegroundColor Gray
try {
    $proximos = Invoke-RestMethod -Uri "$BASE_URL/eventos/proximos?dias=30" -Method Get
    Write-Host "Eventos próximos encontrados: $($proximos.totalElements)" -ForegroundColor Green
    foreach ($evento in $proximos.content) {
        Write-Host "  - $($evento.titulo) | Fecha: $($evento.fechaEvento)" -ForegroundColor White
    }
} catch {
    Write-Host "Error al buscar eventos próximos" -ForegroundColor Red
}
Write-Host ""

# 4. Búsqueda por Palabras Clave
Write-Host "4. BÚSQUEDA POR PALABRAS CLAVE (concierto)" -ForegroundColor Magenta
Write-Host "Endpoint: GET /api/eventos/buscar/keywords?palabras=concierto" -ForegroundColor Gray
try {
    $keywords = Invoke-RestMethod -Uri "$BASE_URL/eventos/buscar/keywords?palabras=concierto" -Method Get
    Write-Host "Eventos encontrados: $($keywords.totalElements)" -ForegroundColor Green
    foreach ($evento in $keywords.content) {
        Write-Host "  - $($evento.titulo)" -ForegroundColor White
    }
} catch {
    Write-Host "Error al buscar por palabras clave" -ForegroundColor Red
}
Write-Host ""

# 5. Búsqueda Avanzada
Write-Host "5. BÚSQUEDA AVANZADA (eventos gratuitos)" -ForegroundColor Magenta
Write-Host "Endpoint: GET /api/eventos/buscar/avanzada?esGratuito=true" -ForegroundColor Gray
try {
    $avanzada = Invoke-RestMethod -Uri "$BASE_URL/eventos/buscar/avanzada?esGratuito=true" -Method Get
    Write-Host "Eventos encontrados: $($avanzada.totalElements)" -ForegroundColor Green
    foreach ($evento in $avanzada.content) {
        Write-Host "  - $($evento.titulo) | Precio: `$$($evento.valorIngreso)" -ForegroundColor White
    }
} catch {
    Write-Host "Error en búsqueda avanzada" -ForegroundColor Red
}
Write-Host ""

# ============================================================================
# RESUMEN
# ============================================================================

Write-Host "=====================================================================" -ForegroundColor Cyan
Write-Host "                    RESUMEN DE PRUEBAS" -ForegroundColor Cyan
Write-Host "=====================================================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Endpoints probados:" -ForegroundColor Yellow
Write-Host "  1. Listado general de eventos" -ForegroundColor White
Write-Host "  2. Eventos gratuitos" -ForegroundColor White
Write-Host "  3. Eventos próximos" -ForegroundColor White
Write-Host "  4. Búsqueda por palabras clave" -ForegroundColor White
Write-Host "  5. Búsqueda avanzada" -ForegroundColor White
Write-Host ""
Write-Host "Para ver todos los endpoints disponibles, abre:" -ForegroundColor Yellow
Write-Host "  http://localhost:8081/swagger-ui/index.html" -ForegroundColor Cyan
Write-Host ""
Write-Host "=====================================================================" -ForegroundColor Cyan
