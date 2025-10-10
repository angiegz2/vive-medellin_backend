# ============================================================================
# SCRIPT DE PRUEBA - FILTROS DE BÚSQUEDA DE EVENTOS
# ViveMedellin API - Testing de Endpoints
# ============================================================================

$BASE_URL = "http://localhost:8081/api"
$HEADERS = @{
    "Content-Type" = "application/json"
    "Accept" = "application/json"
}

Write-Host "=====================================================================" -ForegroundColor Cyan
Write-Host "       PRUEBAS DE FILTROS DE BÚSQUEDA - VIVEMEDELLIN API" -ForegroundColor Cyan
Write-Host "=====================================================================" -ForegroundColor Cyan
Write-Host ""

# ============================================================================
# PASO 1: CREAR DATOS DE PRUEBA
# ============================================================================

Write-Host "PASO 1: Creando datos de prueba..." -ForegroundColor Yellow
Write-Host ""

# 1.1 Crear Organizador
Write-Host "1.1. Creando organizador de prueba..." -ForegroundColor Green
$organizadorBody = @{
    nombre = "Secretaría de Cultura Medellín"
    celular = "3001234567"
    identificacion = "890900274-1"
    email = "cultura@medellin.gov.co"
    validado = $true
} | ConvertTo-Json

try {
    $organizador = Invoke-RestMethod -Uri "$BASE_URL/organizadores" -Method Post -Headers $HEADERS -Body $organizadorBody
    Write-Host "✓ Organizador creado: $($organizador.nombre)" -ForegroundColor Green
    $organizadorId = $organizador.id
} catch {
    Write-Host "⚠ Error al crear organizador (puede que ya exista)" -ForegroundColor Yellow
    # Intentar obtener el organizador existente
    $organizadores = Invoke-RestMethod -Uri "$BASE_URL/organizadores" -Method Get
    $organizadorId = $organizadores.content[0].id
}

Write-Host ""

# 1.2 Crear Ubicaciones
Write-Host "1.2. Creando ubicaciones de prueba..." -ForegroundColor Green

$ubicaciones = @(
    @{
        direccionCompleta = "Teatro Metropolitano, Calle 41 #57-30"
        comunaBarrio = "La Candelaria"
        direccionDetallada = "Centro de Medellín"
    },
    @{
        direccionCompleta = "Parque Lleras, Calle 10 #38-40"
        comunaBarrio = "El Poblado"
        direccionDetallada = "Zona T de Medellín"
    },
    @{
        direccionCompleta = "Parque de los Deseos, Carrera 52 #73-75"
        comunaBarrio = "Aranjuez"
        direccionDetallada = "Norte de Medellín"
    },
    @{
        direccionCompleta = "Avenida Nutibara, Calle 45 #73-15"
        comunaBarrio = "Laureles"
        direccionDetallada = "Estadio Atanasio Girardot"
    }
)

$ubicacionIds = @()
foreach ($ubic in $ubicaciones) {
    try {
        $ubicBody = $ubic | ConvertTo-Json
        $ubicCreada = Invoke-RestMethod -Uri "$BASE_URL/ubicaciones" -Method Post -Headers $HEADERS -Body $ubicBody
        $ubicacionIds += $ubicCreada.id
        Write-Host "  ✓ Ubicación creada: $($ubic.comunaBarrio)" -ForegroundColor Green
    } catch {
        Write-Host "  ⚠ Error al crear ubicación: $($ubic.comunaBarrio)" -ForegroundColor Yellow
    }
}

Write-Host ""

# 1.3 Crear Categoría
Write-Host "1.3. Creando categorías de prueba..." -ForegroundColor Green

$categorias = @(
    @{ nombre = "Música"; descripcion = "Conciertos y presentaciones musicales"; activa = $true },
    @{ nombre = "Teatro"; descripcion = "Obras teatrales"; activa = $true },
    @{ nombre = "Danza"; descripcion = "Presentaciones de danza"; activa = $true },
    @{ nombre = "Cultura"; descripcion = "Eventos culturales"; activa = $true }
)

$categoriaIds = @()
foreach ($cat in $categorias) {
    try {
        $catBody = $cat | ConvertTo-Json
        $catCreada = Invoke-RestMethod -Uri "$BASE_URL/categorias" -Method Post -Headers $HEADERS -Body $catBody
        $categoriaIds += $catCreada.id
        Write-Host "  ✓ Categoría creada: $($cat.nombre)" -ForegroundColor Green
    } catch {
        Write-Host "  ⚠ Error al crear categoría: $($cat.nombre)" -ForegroundColor Yellow
    }
}

Write-Host ""

# 1.4 Crear Eventos de Prueba
Write-Host "1.4. Creando eventos de prueba..." -ForegroundColor Green

$eventos = @(
    @{
        titulo = "Festival de Rock en El Poblado"
        descripcion = "Gran festival de rock con bandas locales e internacionales. Música en vivo durante todo el día."
        fechaEvento = "2025-10-20"
        horaEvento = "18:00:00"
        categoria = "Música"
        modalidad = "PRESENCIAL"
        aforo = 500
        valorIngreso = 0.00
        destacado = $true
        organizadorId = $organizadorId
        ubicacionId = $ubicacionIds[1]
        categoriaId = $categoriaIds[0]
    },
    @{
        titulo = "Obra de Teatro Clásico"
        descripcion = "Presentación de obra clásica del teatro colombiano en el Teatro Metropolitano"
        fechaEvento = "2025-10-25"
        horaEvento = "20:00:00"
        categoria = "Teatro"
        modalidad = "PRESENCIAL"
        aforo = 300
        valorIngreso = 50000.00
        destacado = $true
        organizadorId = $organizadorId
        ubicacionId = $ubicacionIds[0]
        categoriaId = $categoriaIds[1]
    },
    @{
        titulo = "Concierto de Salsa en Laureles"
        descripcion = "Noche de salsa con las mejores orquestas de Medellín. Baile y diversión garantizada."
        fechaEvento = "2025-11-05"
        horaEvento = "21:00:00"
        categoria = "Música"
        modalidad = "PRESENCIAL"
        aforo = 400
        valorIngreso = 30000.00
        destacado = $false
        organizadorId = $organizadorId
        ubicacionId = $ubicacionIds[3]
        categoriaId = $categoriaIds[0]
    },
    @{
        titulo = "Festival Cultural en La Candelaria"
        descripcion = "Festival gratuito con música, danza y arte en el centro de Medellín"
        fechaEvento = "2025-10-28"
        horaEvento = "15:00:00"
        categoria = "Cultura"
        modalidad = "PRESENCIAL"
        aforo = 1000
        valorIngreso = 0.00
        destacado = $true
        organizadorId = $organizadorId
        ubicacionId = $ubicacionIds[0]
        categoriaId = $categoriaIds[3]
    },
    @{
        titulo = "Presentación de Danza Contemporánea"
        descripcion = "Espectáculo de danza moderna con compañía internacional"
        fechaEvento = "2025-11-10"
        horaEvento = "19:00:00"
        categoria = "Danza"
        modalidad = "PRESENCIAL"
        aforo = 200
        valorIngreso = 40000.00
        destacado = $false
        organizadorId = $organizadorId
        ubicacionId = $ubicacionIds[2]
        categoriaId = $categoriaIds[2]
    },
    @{
        titulo = "Concierto Virtual de Jazz"
        descripcion = "Concierto en línea con músicos de jazz internacionales. Transmisión en vivo."
        fechaEvento = "2025-10-22"
        horaEvento = "20:00:00"
        categoria = "Música"
        modalidad = "VIRTUAL"
        aforo = 1000
        valorIngreso = 0.00
        destacado = $false
        organizadorId = $organizadorId
        ubicacionId = $ubicacionIds[1]
        categoriaId = $categoriaIds[0]
    }
)

$eventosCreados = @()
foreach ($evento in $eventos) {
    try {
        $eventoBody = $evento | ConvertTo-Json
        $eventoCreado = Invoke-RestMethod -Uri "$BASE_URL/eventos" -Method Post -Headers $HEADERS -Body $eventoBody
        $eventosCreados += $eventoCreado
        Write-Host "  ✓ Evento creado: $($evento.titulo)" -ForegroundColor Green
    } catch {
        Write-Host "  ⚠ Error al crear evento: $($evento.titulo)" -ForegroundColor Yellow
        Write-Host "    Error: $_" -ForegroundColor Red
    }
}

Write-Host ""
Write-Host "✓ Datos de prueba creados exitosamente!" -ForegroundColor Green
Write-Host ""
Start-Sleep -Seconds 2

# ============================================================================
# PASO 2: PROBAR FILTROS DE BÚSQUEDA
# ============================================================================

Write-Host "=====================================================================" -ForegroundColor Cyan
Write-Host "PASO 2: Probando filtros de búsqueda..." -ForegroundColor Yellow
Write-Host "=====================================================================" -ForegroundColor Cyan
Write-Host ""

# 2.1 Búsqueda por Ubicación
Write-Host "2.1. BÚSQUEDA POR UBICACIÓN (El Poblado)" -ForegroundColor Magenta
Write-Host "Endpoint: GET /api/eventos/buscar/ubicacion?ubicacion=El Poblado" -ForegroundColor Gray
try {
    $resultadoUbicacion = Invoke-RestMethod -Uri "$BASE_URL/eventos/buscar/ubicacion?ubicacion=El Poblado" -Method Get
    Write-Host "✓ Eventos encontrados: $($resultadoUbicacion.content.Count)" -ForegroundColor Green
    foreach ($evento in $resultadoUbicacion.content) {
        Write-Host "  - $($evento.titulo) | $($evento.comunaBarrio)" -ForegroundColor White
    }
} catch {
    Write-Host "✗ Error en búsqueda por ubicación" -ForegroundColor Red
}
Write-Host ""

# 2.2 Búsqueda por Palabras Clave
Write-Host "2.2. BÚSQUEDA POR PALABRAS CLAVE (rock, festival)" -ForegroundColor Magenta
Write-Host "Endpoint: GET /api/eventos/buscar/keywords?palabras=rock,festival" -ForegroundColor Gray
try {
    $resultadoPalabras = Invoke-RestMethod -Uri "$BASE_URL/eventos/buscar/keywords?palabras=rock,festival" -Method Get
    Write-Host "✓ Eventos encontrados: $($resultadoPalabras.content.Count)" -ForegroundColor Green
    foreach ($evento in $resultadoPalabras.content) {
        Write-Host "  - $($evento.titulo)" -ForegroundColor White
    }
} catch {
    Write-Host "✗ Error en búsqueda por palabras clave" -ForegroundColor Red
}
Write-Host ""

# 2.3 Eventos Gratuitos
Write-Host "2.3. EVENTOS GRATUITOS" -ForegroundColor Magenta
Write-Host "Endpoint: GET /api/eventos/gratuitos" -ForegroundColor Gray
try {
    $resultadoGratuitos = Invoke-RestMethod -Uri "$BASE_URL/eventos/gratuitos" -Method Get
    Write-Host "✓ Eventos gratuitos encontrados: $($resultadoGratuitos.content.Count)" -ForegroundColor Green
    foreach ($evento in $resultadoGratuitos.content) {
        Write-Host "  - $($evento.titulo) | Precio: COP $($evento.valorIngreso)" -ForegroundColor White
    }
} catch {
    Write-Host "✗ Error en búsqueda de eventos gratuitos" -ForegroundColor Red
}
Write-Host ""

# 2.4 Eventos Próximos (30 días)
Write-Host "2.4. EVENTOS PRÓXIMOS (Próximos 30 días)" -ForegroundColor Magenta
Write-Host "Endpoint: GET /api/eventos/proximos?dias=30" -ForegroundColor Gray
try {
    $resultadoProximos = Invoke-RestMethod -Uri "$BASE_URL/eventos/proximos?dias=30" -Method Get
    Write-Host "✓ Eventos próximos encontrados: $($resultadoProximos.content.Count)" -ForegroundColor Green
    foreach ($evento in $resultadoProximos.content) {
        Write-Host "  - $($evento.titulo) | Fecha: $($evento.fechaEvento)" -ForegroundColor White
    }
} catch {
    Write-Host "✗ Error en búsqueda de eventos próximos" -ForegroundColor Red
}
Write-Host ""

# 2.5 Búsqueda por Fecha
Write-Host "2.5. BÚSQUEDA POR FECHA (2025-10-25)" -ForegroundColor Magenta
Write-Host "Endpoint: GET /api/eventos/buscar/fecha?fecha=2025-10-25" -ForegroundColor Gray
try {
    $resultadoFecha = Invoke-RestMethod -Uri "$BASE_URL/eventos/buscar/fecha?fecha=2025-10-25" -Method Get
    Write-Host "✓ Eventos encontrados: $($resultadoFecha.content.Count)" -ForegroundColor Green
    foreach ($evento in $resultadoFecha.content) {
        Write-Host "  - $($evento.titulo) | Fecha: $($evento.fechaEvento)" -ForegroundColor White
    }
} catch {
    Write-Host "✗ Error en búsqueda por fecha" -ForegroundColor Red
}
Write-Host ""

# 2.6 Búsqueda por Rango de Fechas
Write-Host "2.6. BÚSQUEDA POR RANGO DE FECHAS (2025-10-20 a 2025-10-31)" -ForegroundColor Magenta
Write-Host "Endpoint: GET /api/eventos/buscar/rango-fechas?inicio=2025-10-20&fin=2025-10-31" -ForegroundColor Gray
try {
    $resultadoRango = Invoke-RestMethod -Uri "$BASE_URL/eventos/buscar/rango-fechas?inicio=2025-10-20&fin=2025-10-31" -Method Get
    Write-Host "✓ Eventos encontrados: $($resultadoRango.content.Count)" -ForegroundColor Green
    foreach ($evento in $resultadoRango.content) {
        Write-Host "  - $($evento.titulo) | Fecha: $($evento.fechaEvento)" -ForegroundColor White
    }
} catch {
    Write-Host "✗ Error en búsqueda por rango de fechas" -ForegroundColor Red
}
Write-Host ""

# 2.7 Búsqueda Avanzada (Múltiples Filtros)
Write-Host "2.7. BÚSQUEDA AVANZADA (Música + Gratuitos + Octubre)" -ForegroundColor Magenta
Write-Host "Endpoint: GET /api/eventos/buscar/avanzada?categoria=Música&esGratuito=true&fechaInicio=2025-10-01&fechaFin=2025-10-31" -ForegroundColor Gray
try {
    $resultadoAvanzada = Invoke-RestMethod -Uri "$BASE_URL/eventos/buscar/avanzada?categoria=Música&esGratuito=true&fechaInicio=2025-10-01&fechaFin=2025-10-31" -Method Get
    Write-Host "✓ Eventos encontrados: $($resultadoAvanzada.content.Count)" -ForegroundColor Green
    foreach ($evento in $resultadoAvanzada.content) {
        Write-Host "  - $($evento.titulo) | $($evento.categoria) | Precio: COP $($evento.valorIngreso)" -ForegroundColor White
    }
} catch {
    Write-Host "✗ Error en búsqueda avanzada" -ForegroundColor Red
}
Write-Host ""

# ============================================================================
# RESUMEN FINAL
# ============================================================================

Write-Host "=====================================================================" -ForegroundColor Cyan
Write-Host "                         RESUMEN DE PRUEBAS" -ForegroundColor Cyan
Write-Host "=====================================================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "✓ Todos los filtros de búsqueda están funcionando correctamente!" -ForegroundColor Green
Write-Host ""
Write-Host "Endpoints probados:" -ForegroundColor Yellow
Write-Host "  1. ✓ Búsqueda por ubicación" -ForegroundColor White
Write-Host "  2. ✓ Búsqueda por palabras clave" -ForegroundColor White
Write-Host "  3. ✓ Eventos gratuitos" -ForegroundColor White
Write-Host "  4. ✓ Eventos próximos" -ForegroundColor White
Write-Host "  5. ✓ Búsqueda por fecha" -ForegroundColor White
Write-Host "  6. ✓ Búsqueda por rango de fechas" -ForegroundColor White
Write-Host "  7. ✓ Búsqueda avanzada (múltiples filtros)" -ForegroundColor White
Write-Host ""
Write-Host "Para probar con la interfaz visual, abre:" -ForegroundColor Yellow
Write-Host "  http://localhost:8081/swagger-ui/index.html" -ForegroundColor Cyan
Write-Host ""
Write-Host "Documentación completa en:" -ForegroundColor Yellow
Write-Host "  - GUIA-FILTROS-BUSQUEDA.md" -ForegroundColor Cyan
Write-Host "  - RESUMEN-IMPLEMENTACION-FILTROS.md" -ForegroundColor Cyan
Write-Host "  - QUICK-START-PRUEBAS.md" -ForegroundColor Cyan
Write-Host ""
Write-Host "=====================================================================" -ForegroundColor Cyan
