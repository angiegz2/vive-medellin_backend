# ENTREGABLE 2: PRINCIPALES CONSULTAS IDENTIFICADAS
## Proyecto ViveMedellin - Preguntas de Negocio y Consultas SQL

---

## 1. METODOLOGÍA DE IDENTIFICACIÓN DE CONSULTAS

### 1.1 Criterios de Selección
Las consultas fueron identificadas basándose en:
- **Frecuencia de uso:** Consultas que se ejecutarán regularmente
- **Impacto en el negocio:** Consultas críticas para operaciones diarias
- **Complejidad técnica:** Consultas que requieren optimización especial
- **Valor para stakeholders:** Información clave para toma de decisiones

### 1.2 Categorización
- **Consultas de Negocio Críticas (CN):** Funcionalidad core del sistema
- **Consultas Operacionales (CO):** Gestión y administración
- **Consultas Analíticas (CA):** Business Intelligence y reportes
- **Consultas de Performance (CP):** Optimizadas para alta concurrencia

---

## 2. PREGUNTAS DE NEGOCIO IDENTIFICADAS

### 2.1 Preguntas del Ciudadano/Usuario Final

#### P-01: ¿Qué eventos culturales hay disponibles cerca de mi ubicación?
**Contexto:** Ciudadanos buscan eventos por proximidad geográfica  
**Frecuencia:** Alta (múltiples veces por día)  
**Complejidad:** Media (requiere cálculos geoespaciales)

#### P-02: ¿Cuáles son los eventos mejor valorados en una categoría específica?
**Contexto:** Usuarios buscan calidad y recomendaciones  
**Frecuencia:** Alta  
**Complejidad:** Media (agregaciones y ordenamiento)

#### P-03: ¿Qué eventos gratuitos están disponibles este fin de semana?
**Contexto:** Acceso democrático a la cultura  
**Frecuencia:** Alta (especialmente jueves-viernes)  
**Complejidad:** Baja

#### P-04: ¿Cómo puedo encontrar eventos de un organizador específico?
**Contexto:** Seguimiento de organizadores favoritos  
**Frecuencia:** Media  
**Complejidad:** Baja

### 2.2 Preguntas del Organizador

#### P-05: ¿Cómo están siendo valorados mis eventos?
**Contexto:** Feedback y mejora continua  
**Frecuencia:** Media (semanal)  
**Complejidad:** Media

#### P-06: ¿Cuál es el rendimiento de mis eventos comparado con la competencia?
**Contexto:** Análisis competitivo  
**Frecuencia:** Baja (mensual)  
**Complejidad:** Alta

#### P-07: ¿En qué ubicaciones tienen mejor acogida mis eventos?
**Contexto:** Estrategia de ubicación  
**Frecuencia:** Baja  
**Complejidad:** Media

### 2.3 Preguntas de la Administración/Alcaldía

#### P-08: ¿Cuál es la distribución de eventos culturales por comunas?
**Contexto:** Política pública y equidad territorial  
**Frecuencia:** Media (reportes mensuales)  
**Complejidad:** Media

#### P-09: ¿Qué categorías de eventos tienen mayor demanda ciudadana?
**Contexto:** Asignación de recursos y promoción  
**Frecuencia:** Media  
**Complejidad:** Media

#### P-10: ¿Cuáles son las tendencias temporales de la actividad cultural?
**Contexto:** Planificación de calendario cultural  
**Frecuencia:** Baja (trimestral)  
**Complejidad:** Alta

---

## 3. CONSULTAS DE NEGOCIO CRÍTICAS (CN)

### CN-01: Búsqueda de Eventos por Proximidad Geográfica
**Pregunta de Negocio:** P-01  
**Frecuencia:** 1000+ ejecuciones/día  
**Tiempo de Respuesta Objetivo:** < 200ms

```sql
-- Eventos cercanos por comuna/barrio
SELECT 
    e.id,
    e.titulo,
    e.descripcion,
    e.fecha_evento,
    e.hora_evento,
    e.valor_ingreso,
    u.direccion_completa,
    u.comuna_barrio,
    u.enlace_mapa,
    o.nombre as organizador,
    o.celular as contacto,
    COALESCE(AVG(v.calificacion), 0) as promedio_valoracion,
    COUNT(v.id) as total_valoraciones
FROM eventos e
    JOIN ubicaciones u ON e.ubicacion_id = u.id
    JOIN organizadores o ON e.organizador_id = o.id
    LEFT JOIN valoraciones v ON e.id = v.evento_id
WHERE 
    e.activo = true
    AND e.fecha_evento >= CURRENT_DATE
    AND u.comuna_barrio = ANY($1)  -- Array de comunas de interés
GROUP BY e.id, e.titulo, e.descripcion, e.fecha_evento, e.hora_evento, 
         e.valor_ingreso, u.direccion_completa, u.comuna_barrio, 
         u.enlace_mapa, o.nombre, o.celular
ORDER BY e.fecha_evento ASC, promedio_valoracion DESC
LIMIT 20;
```

**Índices Requeridos:**
- `idx_eventos_activo_fecha` en eventos(activo, fecha_evento)
- `idx_ubicaciones_comuna_barrio` en ubicaciones(comuna_barrio)
- `idx_valoraciones_evento_id` en valoraciones(evento_id)

### CN-02: Ranking de Eventos Mejor Valorados por Categoría
**Pregunta de Negocio:** P-02  
**Frecuencia:** 500+ ejecuciones/día  
**Tiempo de Respuesta Objetivo:** < 300ms

```sql
-- Top eventos por valoración y categoría
SELECT 
    e.id,
    e.titulo,
    e.categoria,
    e.fecha_evento,
    e.valor_ingreso,
    AVG(v.calificacion) as promedio_valoracion,
    COUNT(v.id) as total_valoraciones,
    u.comuna_barrio,
    o.nombre as organizador
FROM eventos e
    JOIN valoraciones v ON e.id = v.evento_id
    JOIN ubicaciones u ON e.ubicacion_id = u.id
    JOIN organizadores o ON e.organizador_id = o.id
WHERE 
    e.activo = true
    AND e.fecha_evento >= CURRENT_DATE
    AND ($1 IS NULL OR e.categoria = $1)  -- Filtro opcional por categoría
GROUP BY e.id, e.titulo, e.categoria, e.fecha_evento, e.valor_ingreso, 
         u.comuna_barrio, o.nombre
HAVING COUNT(v.id) >= 3  -- Mínimo 3 valoraciones para aparecer
ORDER BY promedio_valoracion DESC, total_valoraciones DESC
LIMIT 10;
```

**Índices Requeridos:**
- `idx_eventos_categoria_activo_fecha` en eventos(categoria, activo, fecha_evento)
- `idx_valoraciones_evento_calificacion` en valoraciones(evento_id, calificacion)

### CN-03: Búsqueda de Eventos Gratuitos por Período
**Pregunta de Negocio:** P-03  
**Frecuencia:** 200+ ejecuciones/día (picos jueves-viernes)  
**Tiempo de Respuesta Objetivo:** < 150ms

```sql
-- Eventos gratuitos en rango de fechas
SELECT 
    e.id,
    e.titulo,
    e.descripcion,
    e.fecha_evento,
    e.hora_evento,
    e.categoria,
    u.direccion_completa,
    u.comuna_barrio,
    o.nombre as organizador,
    o.email as contacto_organizador
FROM eventos e
    JOIN ubicaciones u ON e.ubicacion_id = u.id
    JOIN organizadores o ON e.organizador_id = o.id
WHERE 
    e.activo = true
    AND e.valor_ingreso = 0.00
    AND e.fecha_evento BETWEEN $1 AND $2  -- Rango de fechas
    AND ($3 IS NULL OR e.categoria = $3)  -- Filtro opcional por categoría
ORDER BY e.fecha_evento ASC, e.hora_evento ASC;
```

**Índices Requeridos:**
- `idx_eventos_gratuitos_fecha` en eventos(valor_ingreso, fecha_evento) WHERE valor_ingreso = 0
- `idx_eventos_activo_categoria` en eventos(activo, categoria)

### CN-04: Búsqueda por Texto Completo
**Pregunta de Negocio:** Búsqueda general de eventos  
**Frecuencia:** 800+ ejecuciones/día  
**Tiempo de Respuesta Objetivo:** < 250ms

```sql
-- Búsqueda de texto completo en títulos y descripciones
SELECT 
    e.id,
    e.titulo,
    e.descripcion,
    e.fecha_evento,
    e.categoria,
    u.comuna_barrio,
    o.nombre as organizador,
    ts_rank(to_tsvector('spanish', e.titulo || ' ' || e.descripcion), 
            plainto_tsquery('spanish', $1)) as relevancia
FROM eventos e
    JOIN ubicaciones u ON e.ubicacion_id = u.id
    JOIN organizadores o ON e.organizador_id = o.id
WHERE 
    e.activo = true
    AND e.fecha_evento >= CURRENT_DATE
    AND to_tsvector('spanish', e.titulo || ' ' || e.descripcion) @@ 
        plainto_tsquery('spanish', $1)
ORDER BY relevancia DESC, e.fecha_evento ASC
LIMIT 25;
```

**Índices Requeridos:**
- `idx_eventos_texto_busqueda` GIN en to_tsvector('spanish', titulo || ' ' || descripcion)

---

## 4. CONSULTAS OPERACIONALES (CO)

### CO-01: Dashboard del Organizador
**Pregunta de Negocio:** P-05  
**Frecuencia:** 50+ ejecuciones/día  
**Tiempo de Respuesta Objetivo:** < 500ms

```sql
-- Resumen completo para dashboard de organizador
SELECT 
    org.id,
    org.nombre,
    COUNT(DISTINCT e.id) as total_eventos,
    COUNT(DISTINCT CASE WHEN e.fecha_evento >= CURRENT_DATE THEN e.id END) as eventos_futuros,
    COUNT(DISTINCT CASE WHEN e.destacado = true THEN e.id END) as eventos_destacados,
    COALESCE(AVG(v.calificacion), 0) as promedio_general_valoracion,
    COUNT(v.id) as total_valoraciones_recibidas,
    COUNT(DISTINCT v.usuario_id) as usuarios_que_valoraron,
    -- Eventos por categoría
    COUNT(DISTINCT CASE WHEN e.categoria = 'Culturales y Artísticos' THEN e.id END) as eventos_culturales,
    COUNT(DISTINCT CASE WHEN e.categoria = 'Deportivos' THEN e.id END) as eventos_deportivos,
    COUNT(DISTINCT CASE WHEN e.categoria = 'Académicos' THEN e.id END) as eventos_academicos
FROM organizadores org
    LEFT JOIN eventos e ON org.id = e.organizador_id AND e.activo = true
    LEFT JOIN valoraciones v ON e.id = v.evento_id
WHERE org.id = $1
GROUP BY org.id, org.nombre;
```

### CO-02: Eventos Pendientes de Moderación
**Pregunta de Negocio:** Gestión administrativa  
**Frecuencia:** 10+ ejecuciones/día  
**Tiempo de Respuesta Objetivo:** < 100ms

```sql
-- Eventos y valoraciones que requieren moderación
SELECT 
    'evento' as tipo_contenido,
    e.id,
    e.titulo as contenido,
    e.fecha_creacion,
    o.nombre as autor,
    NULL as calificacion
FROM eventos e
    JOIN organizadores o ON e.organizador_id = o.id
WHERE e.activo = true 
    AND e.fecha_creacion >= CURRENT_DATE - INTERVAL '7 days'
    -- Criterios de moderación automática
    AND (
        LENGTH(e.descripcion) < 50 
        OR e.titulo ~* '(contacto|whatsapp|celular|telefono)'
        OR NOT o.validado
    )

UNION ALL

SELECT 
    'valoracion' as tipo_contenido,
    v.id,
    COALESCE(v.comentario, 'Sin comentario') as contenido,
    v.fecha_valoracion as fecha_creacion,
    u.nombre as autor,
    v.calificacion
FROM valoraciones v
    JOIN usuarios u ON v.usuario_id = u.id
WHERE NOT v.moderada
    AND v.fecha_valoracion >= CURRENT_DATE - INTERVAL '3 days'
    AND (
        LENGTH(COALESCE(v.comentario, '')) > 500
        OR v.comentario ~* '(contacto|whatsapp|celular|telefono|spam)'
    )
ORDER BY fecha_creacion DESC;
```

### CO-03: Reporte de Actividad por Organizador
**Pregunta de Negocio:** P-06, P-07  
**Frecuencia:** 5+ ejecuciones/semana  
**Tiempo de Respuesta Objetivo:** < 1000ms

```sql
-- Análisis comparativo de rendimiento por organizador
WITH estadisticas_organizador AS (
    SELECT 
        e.organizador_id,
        COUNT(*) as eventos_creados,
        AVG(v.calificacion) as promedio_valoracion,
        COUNT(v.id) as total_valoraciones,
        COUNT(DISTINCT u.comuna_barrio) as comunas_cubiertas,
        AVG(e.valor_ingreso) as precio_promedio
    FROM eventos e
        LEFT JOIN valoraciones v ON e.id = v.evento_id
        LEFT JOIN ubicaciones u ON e.ubicacion_id = u.id
    WHERE e.fecha_evento BETWEEN $2 AND $3  -- Período de análisis
        AND e.activo = true
    GROUP BY e.organizador_id
),
percentiles_mercado AS (
    SELECT 
        PERCENTILE_CONT(0.5) WITHIN GROUP (ORDER BY promedio_valoracion) as mediana_valoracion,
        PERCENTILE_CONT(0.5) WITHIN GROUP (ORDER BY eventos_creados) as mediana_eventos,
        PERCENTILE_CONT(0.5) WITHIN GROUP (ORDER BY total_valoraciones) as mediana_valoraciones
    FROM estadisticas_organizador
)
SELECT 
    o.nombre,
    eo.eventos_creados,
    eo.promedio_valoracion,
    eo.total_valoraciones,
    eo.comunas_cubiertas,
    eo.precio_promedio,
    -- Comparación con mercado
    CASE 
        WHEN eo.promedio_valoracion > pm.mediana_valoracion THEN 'Sobre la media'
        WHEN eo.promedio_valoracion = pm.mediana_valoracion THEN 'En la media'
        ELSE 'Bajo la media'
    END as posicion_calidad,
    CASE 
        WHEN eo.eventos_creados > pm.mediana_eventos THEN 'Alto'
        WHEN eo.eventos_creados = pm.mediana_eventos THEN 'Medio'
        ELSE 'Bajo'
    END as nivel_actividad
FROM organizadores o
    JOIN estadisticas_organizador eo ON o.id = eo.organizador_id
    CROSS JOIN percentiles_mercado pm
WHERE o.id = $1
ORDER BY eo.promedio_valoracion DESC, eo.eventos_creados DESC;
```

---

## 5. CONSULTAS ANALÍTICAS Y BI (CA)

### CA-01: Distribución Territorial de Eventos
**Pregunta de Negocio:** P-08  
**Frecuencia:** 2-3 ejecuciones/mes  
**Tiempo de Respuesta Objetivo:** < 2000ms

```sql
-- Análisis de equidad territorial en oferta cultural
SELECT 
    u.comuna_barrio,
    COUNT(e.id) as total_eventos,
    COUNT(DISTINCT e.organizador_id) as organizadores_unicos,
    COUNT(DISTINCT e.categoria) as categorias_disponibles,
    AVG(e.valor_ingreso) as precio_promedio,
    COUNT(CASE WHEN e.valor_ingreso = 0 THEN 1 END) as eventos_gratuitos,
    COALESCE(AVG(v.calificacion), 0) as calidad_promedio,
    -- Análisis temporal
    COUNT(CASE WHEN e.fecha_evento BETWEEN 
        DATE_TRUNC('month', CURRENT_DATE) AND 
        DATE_TRUNC('month', CURRENT_DATE) + INTERVAL '1 month' - INTERVAL '1 day' 
        THEN 1 END) as eventos_mes_actual,
    -- Diversidad de horarios
    COUNT(DISTINCT EXTRACT(hour FROM e.hora_evento)) as variedad_horarios
FROM ubicaciones u
    LEFT JOIN eventos e ON u.id = e.ubicacion_id 
        AND e.activo = true 
        AND e.fecha_evento BETWEEN $1 AND $2
    LEFT JOIN valoraciones v ON e.id = v.evento_id
GROUP BY u.comuna_barrio
ORDER BY total_eventos DESC, calidad_promedio DESC;
```

### CA-02: Tendencias de Categorías por Período
**Pregunta de Negocio:** P-09  
**Frecuencia:** 1-2 ejecuciones/mes  
**Tiempo de Respuesta Objetivo:** < 1500ms

```sql
-- Análisis de popularidad y tendencias por categoría
WITH datos_por_categoria AS (
    SELECT 
        e.categoria,
        DATE_TRUNC('month', e.fecha_evento) as mes,
        COUNT(*) as eventos_creados,
        COUNT(DISTINCT e.organizador_id) as organizadores_activos,
        AVG(v.calificacion) as satisfaccion_promedio,
        COUNT(v.id) as total_valoraciones,
        AVG(e.valor_ingreso) as precio_promedio,
        COUNT(CASE WHEN e.valor_ingreso = 0 THEN 1 END) as eventos_gratuitos
    FROM eventos e
        LEFT JOIN valoraciones v ON e.id = v.evento_id
    WHERE e.fecha_evento BETWEEN $1 AND $2
        AND e.activo = true
    GROUP BY e.categoria, DATE_TRUNC('month', e.fecha_evento)
),
tendencias AS (
    SELECT 
        categoria,
        mes,
        eventos_creados,
        LAG(eventos_creados, 1) OVER (PARTITION BY categoria ORDER BY mes) as eventos_mes_anterior,
        organizadores_activos,
        satisfaccion_promedio,
        total_valoraciones,
        precio_promedio,
        eventos_gratuitos
    FROM datos_por_categoria
)
SELECT 
    categoria,
    mes,
    eventos_creados,
    CASE 
        WHEN eventos_mes_anterior IS NULL THEN 'N/A'
        WHEN eventos_creados > eventos_mes_anterior THEN 'Creciendo'
        WHEN eventos_creados < eventos_mes_anterior THEN 'Declinando'
        ELSE 'Estable'
    END as tendencia,
    COALESCE(
        ROUND(((eventos_creados - eventos_mes_anterior) * 100.0 / eventos_mes_anterior), 2), 
        0
    ) as porcentaje_cambio,
    organizadores_activos,
    ROUND(satisfaccion_promedio, 2) as satisfaccion_promedio,
    total_valoraciones,
    ROUND(precio_promedio, 2) as precio_promedio,
    eventos_gratuitos,
    ROUND((eventos_gratuitos * 100.0 / eventos_creados), 2) as porcentaje_gratuitos
FROM tendencias
ORDER BY categoria, mes DESC;
```

### CA-03: Análisis de Comportamiento Temporal
**Pregunta de Negocio:** P-10  
**Frecuencia:** 1 ejecución/trimestre  
**Tiempo de Respuesta Objetivo:** < 3000ms

```sql
-- Patrones temporales de actividad cultural
SELECT 
    -- Análisis por día de la semana
    EXTRACT(dow FROM e.fecha_evento) as dia_semana,
    CASE EXTRACT(dow FROM e.fecha_evento)
        WHEN 0 THEN 'Domingo'
        WHEN 1 THEN 'Lunes'
        WHEN 2 THEN 'Martes'
        WHEN 3 THEN 'Miércoles'
        WHEN 4 THEN 'Jueves'
        WHEN 5 THEN 'Viernes'
        WHEN 6 THEN 'Sábado'
    END as nombre_dia,
    
    -- Análisis por franja horaria
    CASE 
        WHEN EXTRACT(hour FROM e.hora_evento) BETWEEN 6 AND 11 THEN 'Mañana'
        WHEN EXTRACT(hour FROM e.hora_evento) BETWEEN 12 AND 17 THEN 'Tarde'
        WHEN EXTRACT(hour FROM e.hora_evento) BETWEEN 18 AND 23 THEN 'Noche'
        ELSE 'Madrugada'
    END as franja_horaria,
    
    COUNT(*) as total_eventos,
    COUNT(DISTINCT e.categoria) as categorias_diversidad,
    AVG(v.calificacion) as satisfaccion_promedio,
    COUNT(v.id) as total_valoraciones,
    
    -- Análisis de precios
    AVG(e.valor_ingreso) as precio_promedio,
    COUNT(CASE WHEN e.valor_ingreso = 0 THEN 1 END) as eventos_gratuitos,
    COUNT(CASE WHEN e.valor_ingreso BETWEEN 0.01 AND 50000 THEN 1 END) as eventos_economicos,
    COUNT(CASE WHEN e.valor_ingreso > 50000 THEN 1 END) as eventos_premium,
    
    -- Top categorías por franja
    MODE() WITHIN GROUP (ORDER BY e.categoria) as categoria_mas_comun

FROM eventos e
    LEFT JOIN valoraciones v ON e.id = v.evento_id
WHERE e.fecha_evento BETWEEN $1 AND $2
    AND e.activo = true
GROUP BY 
    EXTRACT(dow FROM e.fecha_evento),
    CASE 
        WHEN EXTRACT(hour FROM e.hora_evento) BETWEEN 6 AND 11 THEN 'Mañana'
        WHEN EXTRACT(hour FROM e.hora_evento) BETWEEN 12 AND 17 THEN 'Tarde'
        WHEN EXTRACT(hour FROM e.hora_evento) BETWEEN 18 AND 23 THEN 'Noche'
        ELSE 'Madrugada'
    END
ORDER BY dia_semana, 
    CASE franja_horaria
        WHEN 'Mañana' THEN 1
        WHEN 'Tarde' THEN 2
        WHEN 'Noche' THEN 3
        ELSE 4
    END;
```

---

## 6. CONSULTAS DE PERFORMANCE CRÍTICO (CP)

### CP-01: Contadores para Dashboard Principal
**Propósito:** Métricas en tiempo real para página de inicio  
**Frecuencia:** 2000+ ejecuciones/día  
**Tiempo de Respuesta Objetivo:** < 50ms

```sql
-- Contadores básicos optimizados
SELECT 
    (SELECT COUNT(*) FROM eventos WHERE activo = true AND fecha_evento >= CURRENT_DATE) as eventos_activos,
    (SELECT COUNT(DISTINCT organizador_id) FROM eventos WHERE activo = true) as organizadores_activos,
    (SELECT COUNT(DISTINCT comuna_barrio) FROM ubicaciones u 
     JOIN eventos e ON u.id = e.ubicacion_id 
     WHERE e.activo = true AND e.fecha_evento >= CURRENT_DATE) as comunas_con_eventos,
    (SELECT COUNT(*) FROM valoraciones WHERE fecha_valoracion >= CURRENT_DATE - INTERVAL '30 days') as valoraciones_recientes;
```

### CP-02: Eventos Destacados para Carousel
**Propósito:** Contenido principal de homepage  
**Frecuencia:** 1000+ ejecuciones/día  
**Tiempo de Respuesta Objetivo:** < 100ms

```sql
-- Eventos destacados optimizados
SELECT 
    e.id,
    e.titulo,
    e.descripcion,
    e.fecha_evento,
    e.imagen_caratula,
    u.comuna_barrio,
    o.nombre as organizador
FROM eventos e
    JOIN ubicaciones u ON e.ubicacion_id = u.id
    JOIN organizadores o ON e.organizador_id = o.id
WHERE e.destacado = true 
    AND e.activo = true
    AND e.fecha_evento >= CURRENT_DATE
ORDER BY e.fecha_evento ASC
LIMIT 6;
```

---

## 7. ÍNDICES Y OPTIMIZACIONES REQUERIDAS

### 7.1 Índices Principales de Performance

```sql
-- Índices para búsquedas frecuentes
CREATE INDEX CONCURRENTLY idx_eventos_activo_fecha_destacado 
    ON eventos(activo, fecha_evento, destacado) 
    WHERE activo = true;

CREATE INDEX CONCURRENTLY idx_eventos_categoria_fecha 
    ON eventos(categoria, fecha_evento) 
    WHERE activo = true;

CREATE INDEX CONCURRENTLY idx_ubicaciones_comuna_barrio 
    ON ubicaciones USING hash(comuna_barrio);

CREATE INDEX CONCURRENTLY idx_valoraciones_evento_calificacion 
    ON valoraciones(evento_id, calificacion);

-- Índice para búsqueda de texto completo
CREATE INDEX CONCURRENTLY idx_eventos_texto_completo 
    ON eventos USING gin(to_tsvector('spanish', titulo || ' ' || descripcion));

-- Índices para consultas geoespaciales (futuro)
CREATE INDEX CONCURRENTLY idx_ubicaciones_coordenadas 
    ON ubicaciones USING gist(coordenadas_gps) 
    WHERE coordenadas_gps IS NOT NULL;
```

### 7.2 Vistas Materializadas para Consultas Complejas

```sql
-- Vista materializada para estadísticas de organizadores
CREATE MATERIALIZED VIEW mv_estadisticas_organizadores AS
SELECT 
    o.id,
    o.nombre,
    COUNT(e.id) as total_eventos,
    AVG(v.calificacion) as promedio_valoracion,
    COUNT(v.id) as total_valoraciones,
    COUNT(DISTINCT u.comuna_barrio) as comunas_alcanzadas,
    MAX(e.fecha_evento) as ultimo_evento
FROM organizadores o
    LEFT JOIN eventos e ON o.id = e.organizador_id AND e.activo = true
    LEFT JOIN valoraciones v ON e.id = v.evento_id
    LEFT JOIN ubicaciones u ON e.ubicacion_id = u.id
GROUP BY o.id, o.nombre;

-- Refrescar cada 6 horas
CREATE UNIQUE INDEX ON mv_estadisticas_organizadores(id);
```

---

## 8. CONSIDERACIONES DE ESCALABILIDAD

### 8.1 Particionado de Tablas (Futuro)
- **Eventos:** Partición por año de fecha_evento
- **Valoraciones:** Partición por trimestre de fecha_valoracion
- **Funciones:** Partición por año de fecha_funcion

### 8.2 Caché de Consultas
- Implementar Redis para consultas frecuentes (CN-01, CN-02, CP-01)
- TTL de 15 minutos para búsquedas
- TTL de 5 minutos para contadores

### 8.3 Réplicas de Lectura
- Consultas analíticas (CA-*) en réplica dedicada
- Reportes administrativos en réplica separada
- Búsquedas públicas en réplica de alta disponibilidad

---

## 9. MONITOREO Y MÉTRICAS

### 9.1 KPIs de Performance
- Tiempo promedio de respuesta por tipo de consulta
- Consultas que superan el SLA de tiempo
- Uso de índices vs scans secuenciales
- Cache hit ratio por consulta

### 9.2 Alertas Críticas
- Consulta CN-01 > 500ms (búsqueda principal)
- Consulta CP-01 > 100ms (contadores dashboard)
- Más de 100 consultas lentas por hora
- Uso de CPU > 80% por consultas

---

**Documento:** Entregable 2 - Principales Consultas Identificadas  
**Fecha:** Septiembre 2025  
**Estado:** Listo para implementación  
**Próximo paso:** Creación de índices y pruebas de performance