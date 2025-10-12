-- ==================================================
-- SCRIPTS DE DATOS DE PRUEBA PARA SISTEMA DE BÚSQUEDA
-- ==================================================

-- PASO 1: Habilitar extensión unaccent (CRÍTICO)
CREATE EXTENSION IF NOT EXISTS unaccent;

-- Verificar que funciona
SELECT unaccent('Música'); -- Debe devolver: Musica
SELECT unaccent('Bogotá'); -- Debe devolver: Bogota

-- ==================================================
-- PASO 2: Insertar Categorías (si no existen)
-- ==================================================
INSERT INTO categoria (nombre, descripcion) VALUES 
('Música', 'Eventos de música y conciertos'),
('Teatro', 'Obras de teatro y drama'),
('Danza', 'Presentaciones de danza'),
('Gastronomía', 'Eventos gastronómicos y culinarios'),
('Deportes', 'Eventos deportivos'),
('Arte', 'Exhibiciones y eventos artísticos'),
('Cine', 'Proyecciones y festivales de cine'),
('Literatura', 'Eventos literarios y presentaciones')
ON CONFLICT DO NOTHING;

-- ==================================================
-- PASO 3: Insertar Ubicaciones/Comunas (si no existen)
-- ==================================================
INSERT INTO ubicacion (nombre, tipo) VALUES 
('El Poblado', 'COMUNA'),
('Laureles', 'COMUNA'),
('La Candelaria', 'COMUNA'),
('Belén', 'COMUNA'),
('Envigado', 'MUNICIPIO'),
('Rionegro', 'MUNICIPIO'),
('Centro', 'COMUNA'),
('Buenos Aires', 'COMUNA')
ON CONFLICT DO NOTHING;

-- ==================================================
-- PASO 4: Insertar Organizadores de Ejemplo
-- ==================================================
INSERT INTO organizador (nombre, email, telefono, tipo_documento, numero_documento) VALUES 
('Secretaría de Cultura Medellín', 'cultura@medellin.gov.co', '3001234567', 'NIT', '890123456-1'),
('Festival Música Viva', 'info@musicaviva.com', '3009876543', 'NIT', '890234567-2'),
('Teatro Universitario', 'teatro@universidad.edu.co', '3112223333', 'NIT', '890345678-3'),
('Corporación Arte y Vida', 'contacto@arteyvida.org', '3124445555', 'NIT', '890456789-4')
ON CONFLICT DO NOTHING;

-- ==================================================
-- PASO 5: Insertar Eventos de Prueba
-- ==================================================

-- Evento 1: Concierto con acento (prueba de búsqueda sin acentos)
INSERT INTO evento (
    titulo, descripcion, fecha_evento, hora_evento, 
    ubicacion, direccion_completa, categoria, modalidad, 
    valor_ingreso, cupo_total, destacado, estado,
    organizador_id, imagen_caratula
) VALUES (
    'Festival de Música Rock',
    'Gran festival de rock con bandas locales e internacionales. Disfruta de música en vivo.',
    CURRENT_DATE + INTERVAL '15 days',
    '18:00:00',
    'El Poblado',
    'Parque Lleras, Calle 10 #38-12',
    (SELECT id FROM categoria WHERE nombre = 'Música' LIMIT 1),
    'PRESENCIAL',
    'Gratuito',
    500,
    true,
    'ACTIVO',
    (SELECT id FROM organizador WHERE nombre LIKE '%Música%' LIMIT 1),
    'https://picsum.photos/400/300?random=1'
);

-- Evento 2: Teatro nocturno (prueba filtro horario)
INSERT INTO evento (
    titulo, descripcion, fecha_evento, hora_evento, 
    ubicacion, direccion_completa, categoria, modalidad, 
    valor_ingreso, cupo_total, destacado, estado,
    organizador_id, imagen_caratula
) VALUES (
    'Obra de Teatro: La Casa de Bernarda Alba',
    'Clásica obra de Federico García Lorca presentada por el Teatro Universitario',
    CURRENT_DATE + INTERVAL '20 days',
    '20:00:00',
    'La Candelaria',
    'Teatro Metropolitano, Calle 41 #57-30',
    (SELECT id FROM categoria WHERE nombre = 'Teatro' LIMIT 1),
    'PRESENCIAL',
    '25000',
    200,
    true,
    'ACTIVO',
    (SELECT id FROM organizador WHERE nombre LIKE '%Teatro%' LIMIT 1),
    'https://picsum.photos/400/300?random=2'
);

-- Evento 3: Danza diurna (prueba filtro horario DIURNO)
INSERT INTO evento (
    titulo, descripcion, fecha_evento, hora_evento, 
    ubicacion, direccion_completa, categoria, modalidad, 
    valor_ingreso, cupo_total, destacado, estado,
    organizador_id, imagen_caratula
) VALUES (
    'Presentación de Ballet Clásico',
    'Compañía de ballet presenta El Cascanueces en matinée dominical',
    CURRENT_DATE + INTERVAL '25 days',
    '11:00:00',
    'Laureles',
    'Avenida 70 #42-34',
    (SELECT id FROM categoria WHERE nombre = 'Danza' LIMIT 1),
    'PRESENCIAL',
    '35000',
    150,
    false,
    'ACTIVO',
    (SELECT id FROM organizador WHERE nombre LIKE '%Arte%' LIMIT 1),
    'https://picsum.photos/400/300?random=3'
);

-- Evento 4: Evento Virtual (prueba filtro modalidad)
INSERT INTO evento (
    titulo, descripcion, fecha_evento, hora_evento, 
    ubicacion, direccion_completa, categoria, modalidad, 
    valor_ingreso, cupo_total, destacado, estado,
    organizador_id, imagen_caratula
) VALUES (
    'Taller Virtual de Fotografía',
    'Aprende técnicas de fotografía profesional desde casa',
    CURRENT_DATE + INTERVAL '10 days',
    '15:00:00',
    'Virtual',
    'Plataforma Zoom',
    (SELECT id FROM categoria WHERE nombre = 'Arte' LIMIT 1),
    'VIRTUAL',
    'Gratuito',
    100,
    false,
    'ACTIVO',
    (SELECT id FROM organizador WHERE nombre LIKE '%Cultura%' LIMIT 1),
    'https://picsum.photos/400/300?random=4'
);

-- Evento 5: Con rango de precio (prueba filtro precio)
INSERT INTO evento (
    titulo, descripcion, fecha_evento, hora_evento, 
    ubicacion, direccion_completa, categoria, modalidad, 
    valor_ingreso, cupo_total, destacado, estado,
    organizador_id, imagen_caratula
) VALUES (
    'Cena Gastronómica Gourmet',
    'Experiencia culinaria de 5 tiempos con maridaje de vinos',
    CURRENT_DATE + INTERVAL '30 days',
    '19:30:00',
    'El Poblado',
    'Calle 10 #43A-30',
    (SELECT id FROM categoria WHERE nombre = 'Gastronomía' LIMIT 1),
    'PRESENCIAL',
    '120000',
    50,
    true,
    'ACTIVO',
    (SELECT id FROM organizador WHERE nombre LIKE '%Arte%' LIMIT 1),
    'https://picsum.photos/400/300?random=5'
);

-- Evento 6: Evento con organizador específico (prueba búsqueda por organizador)
INSERT INTO evento (
    titulo, descripcion, fecha_evento, hora_evento, 
    ubicacion, direccion_completa, categoria, modalidad, 
    valor_ingreso, cupo_total, destacado, estado,
    organizador_id, imagen_caratula
) VALUES (
    'Festival Alcaldía de Medellín',
    'Gran festival organizado por la Secretaría de Cultura con múltiples actividades',
    CURRENT_DATE + INTERVAL '45 days',
    '10:00:00',
    'Centro',
    'Plaza Botero',
    (SELECT id FROM categoria WHERE nombre = 'Música' LIMIT 1),
    'PRESENCIAL',
    'Gratuito',
    1000,
    true,
    'ACTIVO',
    (SELECT id FROM organizador WHERE nombre LIKE '%Secretaría%' LIMIT 1),
    'https://picsum.photos/400/300?random=6'
);

-- Evento 7: Evento con acentos en título (prueba búsqueda sin acentos)
INSERT INTO evento (
    titulo, descripcion, fecha_evento, hora_evento, 
    ubicacion, direccion_completa, categoria, modalidad, 
    valor_ingreso, cupo_total, destacado, estado,
    organizador_id, imagen_caratula
) VALUES (
    'Concierto de Música Clásica y Sinfónica',
    'Orquesta filarmónica presenta obras de Beethoven y Mozart',
    CURRENT_DATE + INTERVAL '35 days',
    '19:00:00',
    'Laureles',
    'Teatro Pablo Tobón Uribe',
    (SELECT id FROM categoria WHERE nombre = 'Música' LIMIT 1),
    'PRESENCIAL',
    '45000',
    300,
    true,
    'ACTIVO',
    (SELECT id FROM organizador WHERE nombre LIKE '%Música%' LIMIT 1),
    'https://picsum.photos/400/300?random=7'
);

-- Evento 8: Evento pasado (NO debe aparecer en búsqueda con disponible=true)
INSERT INTO evento (
    titulo, descripcion, fecha_evento, hora_evento, 
    ubicacion, direccion_completa, categoria, modalidad, 
    valor_ingreso, cupo_total, destacado, estado,
    organizador_id, imagen_caratula
) VALUES (
    'Evento Pasado - No debe aparecer',
    'Este evento ya ocurrió',
    CURRENT_DATE - INTERVAL '5 days',
    '18:00:00',
    'El Poblado',
    'Calle 1 #1-1',
    (SELECT id FROM categoria WHERE nombre = 'Música' LIMIT 1),
    'PRESENCIAL',
    'Gratuito',
    100,
    false,
    'ACTIVO',
    (SELECT id FROM organizador LIMIT 1),
    'https://picsum.photos/400/300?random=8'
);

-- Evento 9: Evento cancelado (NO debe aparecer en búsqueda con disponible=true)
INSERT INTO evento (
    titulo, descripcion, fecha_evento, hora_evento, 
    ubicacion, direccion_completa, categoria, modalidad, 
    valor_ingreso, cupo_total, destacado, estado,
    organizador_id, imagen_caratula
) VALUES (
    'Evento Cancelado - No debe aparecer',
    'Este evento fue cancelado',
    CURRENT_DATE + INTERVAL '10 days',
    '18:00:00',
    'El Poblado',
    'Calle 2 #2-2',
    (SELECT id FROM categoria WHERE nombre = 'Música' LIMIT 1),
    'PRESENCIAL',
    'Gratuito',
    100,
    false,
    'CANCELADO',
    (SELECT id FROM organizador LIMIT 1),
    'https://picsum.photos/400/300?random=9'
);

-- ==================================================
-- PASO 6: Insertar Servicios Adicionales (Opcional)
-- ==================================================
-- Suponiendo que existe una tabla de servicios adicionales

-- Comentar/descomentar según estructura de base de datos:
/*
INSERT INTO servicio_adicional (nombre, descripcion) VALUES 
('Parqueadero', 'Servicio de parqueadero disponible'),
('Comida', 'Servicio de alimentación incluido'),
('Bebidas', 'Bebidas disponibles'),
('Transporte', 'Servicio de transporte incluido'),
('Accesibilidad', 'Acceso para personas con movilidad reducida')
ON CONFLICT DO NOTHING;

-- Asociar servicios a eventos
INSERT INTO evento_servicio (evento_id, servicio_id) VALUES
((SELECT id FROM evento WHERE titulo LIKE '%Festival de Música%' LIMIT 1), (SELECT id FROM servicio_adicional WHERE nombre = 'Parqueadero' LIMIT 1)),
((SELECT id FROM evento WHERE titulo LIKE '%Festival de Música%' LIMIT 1), (SELECT id FROM servicio_adicional WHERE nombre = 'Comida' LIMIT 1)),
((SELECT id FROM evento WHERE titulo LIKE '%Teatro%' LIMIT 1), (SELECT id FROM servicio_adicional WHERE nombre = 'Parqueadero' LIMIT 1)),
((SELECT id FROM evento WHERE titulo LIKE '%Ballet%' LIMIT 1), (SELECT id FROM servicio_adicional WHERE nombre = 'Accesibilidad' LIMIT 1));
*/

-- ==================================================
-- PASO 7: CONSULTAS DE VERIFICACIÓN
-- ==================================================

-- Verificar que los eventos se insertaron correctamente
SELECT 
    id,
    titulo,
    fecha_evento,
    hora_evento,
    ubicacion,
    valor_ingreso,
    estado,
    destacado
FROM evento
ORDER BY fecha_evento;

-- Probar búsqueda con unaccent
SELECT 
    titulo,
    unaccent(lower(titulo)) as titulo_normalizado
FROM evento
WHERE unaccent(lower(titulo)) LIKE '%' || unaccent(lower('musica')) || '%';

-- Debe encontrar eventos con "Música" aunque busquemos "musica"

-- Contar eventos por categoría
SELECT 
    c.nombre as categoria,
    COUNT(*) as total_eventos
FROM evento e
JOIN categoria c ON e.categoria = c.id
GROUP BY c.nombre;

-- Eventos disponibles (futuros y no cancelados)
SELECT 
    titulo,
    fecha_evento,
    estado
FROM evento
WHERE fecha_evento >= CURRENT_DATE 
  AND estado != 'CANCELADO'
ORDER BY fecha_evento;

-- Eventos destacados
SELECT 
    titulo,
    fecha_evento,
    destacado
FROM evento
WHERE destacado = true
ORDER BY fecha_evento;

-- ==================================================
-- PASO 8: CONSULTAS DE PRUEBA PARA VALIDAR BÚSQUEDA
-- ==================================================

-- Prueba 1: Búsqueda case-insensitive con acentos
-- Buscar "musica" debe encontrar "Música"
SELECT titulo FROM evento 
WHERE unaccent(lower(titulo)) LIKE '%' || unaccent(lower('musica')) || '%'
   OR unaccent(lower(descripcion)) LIKE '%' || unaccent(lower('musica')) || '%';

-- Prueba 2: Búsqueda por organizador
SELECT e.titulo, o.nombre as organizador 
FROM evento e
JOIN organizador o ON e.organizador_id = o.id
WHERE unaccent(lower(o.nombre)) LIKE '%' || unaccent(lower('cultura')) || '%';

-- Prueba 3: Filtro por horario diurno (6:00 - 18:00)
SELECT titulo, hora_evento 
FROM evento 
WHERE hora_evento >= '06:00:00' AND hora_evento <= '18:00:00';

-- Prueba 4: Filtro por horario nocturno (18:01 - 05:59)
SELECT titulo, hora_evento 
FROM evento 
WHERE hora_evento > '18:00:00' OR hora_evento < '06:00:00';

-- Prueba 5: Filtro por rango de precio
SELECT titulo, valor_ingreso 
FROM evento 
WHERE CASE 
    WHEN valor_ingreso = 'Gratuito' THEN 0
    ELSE CAST(valor_ingreso AS NUMERIC)
END BETWEEN 0 AND 50000;

-- Prueba 6: Eventos disponibles (futuros y no cancelados)
SELECT titulo, fecha_evento, estado 
FROM evento 
WHERE fecha_evento >= CURRENT_DATE 
  AND estado != 'CANCELADO';

-- ==================================================
-- RESUMEN DE DATOS INSERTADOS
-- ==================================================
SELECT 
    'Total Eventos' as metrica, 
    COUNT(*)::text as valor 
FROM evento

UNION ALL

SELECT 
    'Eventos Destacados', 
    COUNT(*)::text 
FROM evento 
WHERE destacado = true

UNION ALL

SELECT 
    'Eventos Futuros', 
    COUNT(*)::text 
FROM evento 
WHERE fecha_evento >= CURRENT_DATE

UNION ALL

SELECT 
    'Eventos Disponibles', 
    COUNT(*)::text 
FROM evento 
WHERE fecha_evento >= CURRENT_DATE AND estado != 'CANCELADO';

-- ==================================================
-- FIN DE SCRIPT
-- ==================================================

-- ✅ Después de ejecutar este script:
-- 1. Verificar que la extensión unaccent funciona
-- 2. Confirmar que se insertaron 9 eventos
-- 3. Reiniciar el servidor Spring Boot
-- 4. Probar endpoints en Swagger UI
-- ==================================================