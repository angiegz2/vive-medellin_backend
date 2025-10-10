-- ===================================================================
-- VIVEMEDELLIN - MODELO FÍSICO COMPLETO
-- Base de Datos: PostgreSQL 18
-- Proyecto: Plataforma de Eventos Culturales Medellín
-- Fecha: Septiembre 2025
-- ===================================================================

-- Configuración inicial
SET client_encoding = 'UTF8';
SET timezone = 'America/Bogota';

-- ===================================================================
-- 1. EXTENSIONES REQUERIDAS
-- ===================================================================

-- UUID Generation
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- PostGIS para coordenadas geográficas
CREATE EXTENSION IF NOT EXISTS postgis;

-- Full Text Search en español
CREATE EXTENSION IF NOT EXISTS unaccent;

-- Estadísticas de consultas (opcional)
-- CREATE EXTENSION IF NOT EXISTS pg_stat_statements;

-- ===================================================================
-- 2. TIPOS ENUMERADOS PERSONALIZADOS
-- ===================================================================

-- Limpiar tipos existentes
DROP TYPE IF EXISTS tipo_usuario_enum CASCADE;
DROP TYPE IF EXISTS modalidad_evento_enum CASCADE;
DROP TYPE IF EXISTS tipo_multimedia_enum CASCADE;

-- Tipos de usuario
CREATE TYPE tipo_usuario_enum AS ENUM (
    'CIUDADANO',
    'ORGANIZADOR', 
    'ADMINISTRADOR'
);

-- Modalidades de evento
CREATE TYPE modalidad_evento_enum AS ENUM (
    'PRESENCIAL',
    'VIRTUAL',
    'HIBRIDA'
);

-- Tipos de multimedia
CREATE TYPE tipo_multimedia_enum AS ENUM (
    'IMAGEN',
    'VIDEO',
    'AUDIO',
    'DOCUMENTO'
);

-- ===================================================================
-- 3. ELIMINACIÓN SEGURA DE TABLAS (SI EXISTEN)
-- ===================================================================

DROP TABLE IF EXISTS servicios_adicionales CASCADE;
DROP TABLE IF EXISTS multimedia CASCADE;
DROP TABLE IF EXISTS valoraciones CASCADE;
DROP TABLE IF EXISTS funciones CASCADE;
DROP TABLE IF EXISTS eventos CASCADE;
DROP TABLE IF EXISTS ubicaciones CASCADE;
DROP TABLE IF EXISTS categorias_evento CASCADE;
DROP TABLE IF EXISTS organizadores CASCADE;
DROP TABLE IF EXISTS usuarios CASCADE;

-- ===================================================================
-- 4. CREACIÓN DE TABLAS PRINCIPALES
-- ===================================================================

-- ===================================================================
-- TABLA: usuarios
-- Propósito: Gestión de usuarios del sistema
-- ===================================================================

CREATE TABLE usuarios (
    -- Clave primaria
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    
    -- Información personal
    nombre VARCHAR(200) NOT NULL 
        CHECK (LENGTH(TRIM(nombre)) >= 2),
    email VARCHAR(320) NOT NULL UNIQUE
        CHECK (email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$'),
    celular VARCHAR(15)
        CHECK (celular IS NULL OR celular ~ '^[0-9+\-\s()]{10,15}$'),
    documento_identificacion VARCHAR(20) UNIQUE
        CHECK (documento_identificacion IS NULL OR LENGTH(TRIM(documento_identificacion)) >= 6),
    
    -- Clasificación
    tipo_usuario tipo_usuario_enum NOT NULL DEFAULT 'CIUDADANO',
    
    -- Estado
    activo BOOLEAN NOT NULL DEFAULT true,
    
    -- Auditoría
    fecha_registro TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ultima_actividad TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    fecha_creacion TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    usuario_creacion VARCHAR(100),
    usuario_actualizacion VARCHAR(100)
);

-- ===================================================================
-- TABLA: organizadores
-- Propósito: Entidades responsables de crear eventos
-- ===================================================================

CREATE TABLE organizadores (
    -- Clave primaria
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    
    -- Información de contacto
    nombre VARCHAR(200) NOT NULL
        CHECK (LENGTH(TRIM(nombre)) >= 2),
    celular VARCHAR(15) NOT NULL
        CHECK (celular ~ '^[0-9+\-\s()]{10,15}$'),
    identificacion VARCHAR(20) NOT NULL UNIQUE
        CHECK (LENGTH(TRIM(identificacion)) >= 6),
    email VARCHAR(320) NOT NULL UNIQUE
        CHECK (email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$'),
    
    -- Validación administrativa
    validado BOOLEAN NOT NULL DEFAULT false,
    fecha_validacion TIMESTAMP WITH TIME ZONE,
    informacion_adicional TEXT,
    
    -- Auditoría
    fecha_creacion TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    usuario_creacion VARCHAR(100),
    usuario_actualizacion VARCHAR(100)
);

-- ===================================================================
-- TABLA: categorias_evento
-- Propósito: Clasificación temática de eventos
-- ===================================================================

CREATE TABLE categorias_evento (
    -- Clave primaria
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    
    -- Información de la categoría
    nombre VARCHAR(100) NOT NULL UNIQUE
        CHECK (LENGTH(TRIM(nombre)) >= 2),
    descripcion TEXT,
    icono VARCHAR(50),
    
    -- Estado
    activa BOOLEAN NOT NULL DEFAULT true,
    
    -- Auditoría
    fecha_creacion TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    usuario_creacion VARCHAR(100),
    usuario_actualizacion VARCHAR(100)
);

-- ===================================================================
-- TABLA: ubicaciones
-- Propósito: Localizaciones geográficas de eventos
-- ===================================================================

CREATE TABLE ubicaciones (
    -- Clave primaria
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    
    -- Información de ubicación
    direccion_completa VARCHAR(300) NOT NULL
        CHECK (LENGTH(TRIM(direccion_completa)) >= 5),
    comuna_barrio VARCHAR(100) NOT NULL
        CHECK (LENGTH(TRIM(comuna_barrio)) >= 2),
    direccion_detallada VARCHAR(300)
        CHECK (direccion_detallada IS NULL OR LENGTH(TRIM(direccion_detallada)) >= 5),
    enlace_mapa VARCHAR(500)
        CHECK (enlace_mapa IS NULL OR enlace_mapa ~* '^https?://'),
    
    -- Coordenadas geográficas (PostGIS)
    coordenadas_gps GEOMETRY(POINT, 4326),
    
    -- Auditoría
    fecha_creacion TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    usuario_creacion VARCHAR(100),
    usuario_actualizacion VARCHAR(100)
);

-- ===================================================================
-- TABLA: eventos (CENTRAL)
-- Propósito: Entidad central - eventos culturales
-- ===================================================================

CREATE TABLE eventos (
    -- Clave primaria
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    
    -- Información básica del evento
    titulo VARCHAR(200) NOT NULL
        CHECK (LENGTH(TRIM(titulo)) BETWEEN 5 AND 200),
    descripcion TEXT NOT NULL
        CHECK (LENGTH(TRIM(descripcion)) BETWEEN 10 AND 5000),
    
    -- Programación
    fecha_evento DATE NOT NULL
        CHECK (fecha_evento >= CURRENT_DATE),
    hora_evento TIME,
    
    -- Clasificación
    categoria VARCHAR(100) NOT NULL,
    modalidad modalidad_evento_enum NOT NULL DEFAULT 'PRESENCIAL',
    
    -- Capacidad y precio
    aforo INTEGER
        CHECK (aforo IS NULL OR aforo > 0),
    valor_ingreso DECIMAL(10,2) NOT NULL DEFAULT 0.00
        CHECK (valor_ingreso >= 0),
    
    -- Promoción
    destacado BOOLEAN NOT NULL DEFAULT false,
    imagen_caratula VARCHAR(500)
        CHECK (imagen_caratula IS NULL OR imagen_caratula ~* '^https?://'),
    
    -- Estado
    activo BOOLEAN NOT NULL DEFAULT true,
    
    -- Claves foráneas
    organizador_id UUID NOT NULL,
    ubicacion_id UUID NOT NULL,
    categoria_id UUID,
    
    -- Auditoría
    fecha_creacion TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    usuario_creacion VARCHAR(100),
    usuario_actualizacion VARCHAR(100)
);

-- ===================================================================
-- 5. CREACIÓN DE TABLAS DEPENDIENTES
-- ===================================================================

-- ===================================================================
-- TABLA: funciones
-- Propósito: Horarios específicos de eventos (composición)
-- ===================================================================

CREATE TABLE funciones (
    -- Clave primaria
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    
    -- Relación con evento (composición)
    evento_id UUID NOT NULL,
    
    -- Programación específica
    fecha_funcion DATE NOT NULL
        CHECK (fecha_funcion >= CURRENT_DATE),
    hora_inicio TIME NOT NULL,
    hora_fin TIME
        CHECK (hora_fin IS NULL OR hora_fin > hora_inicio),
    
    -- Capacidad específica
    aforo_funcion INTEGER
        CHECK (aforo_funcion IS NULL OR aforo_funcion > 0),
    
    -- Estado
    activa BOOLEAN NOT NULL DEFAULT true,
    
    -- Auditoría
    fecha_creacion TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ===================================================================
-- TABLA: valoraciones
-- Propósito: Calificaciones y comentarios de usuarios
-- ===================================================================

CREATE TABLE valoraciones (
    -- Clave primaria
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    
    -- Relaciones (entidad asociativa N:M)
    evento_id UUID NOT NULL,
    usuario_id UUID NOT NULL,
    
    -- Valoración
    calificacion INTEGER NOT NULL
        CHECK (calificacion BETWEEN 1 AND 5),
    comentario TEXT
        CHECK (comentario IS NULL OR LENGTH(TRIM(comentario)) BETWEEN 1 AND 1000),
    
    -- Moderación
    moderada BOOLEAN NOT NULL DEFAULT false,
    
    -- Auditoría
    fecha_valoracion TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ===================================================================
-- TABLA: multimedia
-- Propósito: Archivos asociados a eventos (composición)
-- ===================================================================

CREATE TABLE multimedia (
    -- Clave primaria
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    
    -- Relación con evento (composición)
    evento_id UUID NOT NULL,
    
    -- Información del archivo
    tipo_multimedia tipo_multimedia_enum NOT NULL,
    url_archivo VARCHAR(500) NOT NULL
        CHECK (url_archivo ~* '^https?://'),
    descripcion TEXT
        CHECK (descripcion IS NULL OR LENGTH(TRIM(descripcion)) <= 500),
    
    -- Presentación
    orden_visualizacion INTEGER NOT NULL DEFAULT 1
        CHECK (orden_visualizacion > 0),
    
    -- Auditoría
    fecha_creacion TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ===================================================================
-- TABLA: servicios_adicionales
-- Propósito: Servicios complementarios de eventos (composición)
-- ===================================================================

CREATE TABLE servicios_adicionales (
    -- Clave primaria
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    
    -- Relación con evento (composición)
    evento_id UUID NOT NULL,
    
    -- Información del servicio
    nombre_servicio VARCHAR(100) NOT NULL
        CHECK (LENGTH(TRIM(nombre_servicio)) BETWEEN 2 AND 100),
    descripcion_servicio TEXT
        CHECK (descripcion_servicio IS NULL OR LENGTH(TRIM(descripcion_servicio)) <= 1000),
    
    -- Costo
    costo_adicional DECIMAL(10,2) NOT NULL DEFAULT 0.00
        CHECK (costo_adicional >= 0),
    incluido_entrada BOOLEAN NOT NULL DEFAULT false,
    
    -- Auditoría
    fecha_creacion TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ===================================================================
-- 6. RESTRICCIONES DE INTEGRIDAD REFERENCIAL
-- ===================================================================

-- Eventos
ALTER TABLE eventos 
ADD CONSTRAINT fk_evento_organizador 
FOREIGN KEY (organizador_id) REFERENCES organizadores(id) ON DELETE RESTRICT;

ALTER TABLE eventos 
ADD CONSTRAINT fk_evento_ubicacion 
FOREIGN KEY (ubicacion_id) REFERENCES ubicaciones(id) ON DELETE RESTRICT;

ALTER TABLE eventos 
ADD CONSTRAINT fk_evento_categoria 
FOREIGN KEY (categoria_id) REFERENCES categorias_evento(id) ON DELETE SET NULL;

-- Funciones (composición - cascada)
ALTER TABLE funciones 
ADD CONSTRAINT fk_funcion_evento 
FOREIGN KEY (evento_id) REFERENCES eventos(id) ON DELETE CASCADE;

-- Valoraciones (entidad asociativa)
ALTER TABLE valoraciones 
ADD CONSTRAINT fk_valoracion_evento 
FOREIGN KEY (evento_id) REFERENCES eventos(id) ON DELETE CASCADE;

ALTER TABLE valoraciones 
ADD CONSTRAINT fk_valoracion_usuario 
FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE;

-- Multimedia (composición - cascada)
ALTER TABLE multimedia 
ADD CONSTRAINT fk_multimedia_evento 
FOREIGN KEY (evento_id) REFERENCES eventos(id) ON DELETE CASCADE;

-- Servicios Adicionales (composición - cascada)
ALTER TABLE servicios_adicionales 
ADD CONSTRAINT fk_servicio_evento 
FOREIGN KEY (evento_id) REFERENCES eventos(id) ON DELETE CASCADE;

-- ===================================================================
-- 7. RESTRICCIONES DE UNICIDAD
-- ===================================================================

-- Un usuario solo puede valorar una vez el mismo evento
ALTER TABLE valoraciones 
ADD CONSTRAINT uk_valoracion_usuario_evento 
UNIQUE (evento_id, usuario_id);

-- Una función específica por evento
ALTER TABLE funciones 
ADD CONSTRAINT uk_funcion_evento_fecha_hora 
UNIQUE (evento_id, fecha_funcion, hora_inicio);

-- Orden de multimedia único por evento
ALTER TABLE multimedia 
ADD CONSTRAINT uk_multimedia_evento_orden 
UNIQUE (evento_id, orden_visualizacion);

-- Nombre de servicio único por evento
ALTER TABLE servicios_adicionales 
ADD CONSTRAINT uk_servicio_evento_nombre 
UNIQUE (evento_id, nombre_servicio);

-- ===================================================================
-- 8. ÍNDICES DE RENDIMIENTO
-- ===================================================================

-- Índices de claves foráneas
CREATE INDEX idx_eventos_organizador_id ON eventos(organizador_id);
CREATE INDEX idx_eventos_ubicacion_id ON eventos(ubicacion_id);
CREATE INDEX idx_eventos_categoria_id ON eventos(categoria_id);
CREATE INDEX idx_funciones_evento_id ON funciones(evento_id);
CREATE INDEX idx_valoraciones_evento_id ON valoraciones(evento_id);
CREATE INDEX idx_valoraciones_usuario_id ON valoraciones(usuario_id);
CREATE INDEX idx_multimedia_evento_id ON multimedia(evento_id);
CREATE INDEX idx_servicios_evento_id ON servicios_adicionales(evento_id);

-- Índices de búsqueda frecuente
CREATE INDEX idx_eventos_fecha_activo ON eventos(fecha_evento, activo) 
    WHERE activo = true;

CREATE INDEX idx_eventos_categoria_fecha ON eventos(categoria, fecha_evento, activo)
    WHERE activo = true;

CREATE INDEX idx_eventos_destacado_fecha ON eventos(destacado, fecha_evento) 
    WHERE destacado = true AND activo = true;

CREATE INDEX idx_eventos_modalidad ON eventos(modalidad) 
    WHERE activo = true;

-- Índices geográficos
CREATE INDEX idx_ubicaciones_comuna ON ubicaciones(comuna_barrio);
CREATE INDEX idx_ubicaciones_coordenadas_gps ON ubicaciones 
    USING GIST (coordenadas_gps);

-- Índices de texto completo
CREATE INDEX idx_eventos_titulo_gin ON eventos 
    USING GIN (to_tsvector('spanish', titulo)) 
    WHERE activo = true;

CREATE INDEX idx_eventos_descripcion_gin ON eventos 
    USING GIN (to_tsvector('spanish', descripcion)) 
    WHERE activo = true;

-- Índices de estado
CREATE INDEX idx_organizadores_validado ON organizadores(validado) 
    WHERE validado = true;

CREATE INDEX idx_usuarios_activo ON usuarios(activo, tipo_usuario) 
    WHERE activo = true;

-- Índices para valoraciones
CREATE INDEX idx_valoraciones_evento_calificacion ON valoraciones(evento_id, calificacion);

-- Índices para funciones
CREATE INDEX idx_funciones_fecha ON funciones(fecha_funcion, activa) 
    WHERE activa = true;

-- ===================================================================
-- 9. TRIGGERS Y FUNCIONES AUTOMÁTICAS
-- ===================================================================

-- Función para actualizar fecha de modificación
CREATE OR REPLACE FUNCTION actualizar_fecha_modificacion()
RETURNS TRIGGER AS $$
BEGIN
    NEW.fecha_actualizacion = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Triggers de auditoría
CREATE TRIGGER tr_usuarios_auditoria
    BEFORE UPDATE ON usuarios
    FOR EACH ROW EXECUTE FUNCTION actualizar_fecha_modificacion();

CREATE TRIGGER tr_organizadores_auditoria
    BEFORE UPDATE ON organizadores  
    FOR EACH ROW EXECUTE FUNCTION actualizar_fecha_modificacion();

CREATE TRIGGER tr_eventos_auditoria
    BEFORE UPDATE ON eventos
    FOR EACH ROW EXECUTE FUNCTION actualizar_fecha_modificacion();

CREATE TRIGGER tr_ubicaciones_auditoria
    BEFORE UPDATE ON ubicaciones
    FOR EACH ROW EXECUTE FUNCTION actualizar_fecha_modificacion();

CREATE TRIGGER tr_categorias_auditoria
    BEFORE UPDATE ON categorias_evento
    FOR EACH ROW EXECUTE FUNCTION actualizar_fecha_modificacion();

-- Validación de organizador para eventos activos
CREATE OR REPLACE FUNCTION validar_organizador_evento()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.activo = true THEN
        IF NOT EXISTS (
            SELECT 1 FROM organizadores 
            WHERE id = NEW.organizador_id AND validado = true
        ) THEN
            RAISE EXCEPTION 'Solo organizadores validados pueden crear eventos activos';
        END IF;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER tr_validar_organizador_evento
    BEFORE INSERT OR UPDATE ON eventos
    FOR EACH ROW EXECUTE FUNCTION validar_organizador_evento();

-- ===================================================================
-- 10. COMENTARIOS DE DOCUMENTACIÓN
-- ===================================================================

-- Tablas principales
COMMENT ON TABLE usuarios IS 'Registro de usuarios del sistema ViveMedellin';
COMMENT ON TABLE organizadores IS 'Entidades responsables de crear y gestionar eventos culturales';
COMMENT ON TABLE eventos IS 'Tabla central - eventos culturales de Medellín';
COMMENT ON TABLE ubicaciones IS 'Localizaciones geográficas donde se realizan eventos';
COMMENT ON TABLE categorias_evento IS 'Catálogo de categorías para clasificación de eventos';

-- Tablas dependientes
COMMENT ON TABLE funciones IS 'Horarios específicos de eventos (relación de composición)';
COMMENT ON TABLE valoraciones IS 'Sistema de valoraciones usuario-evento (N:M)';
COMMENT ON TABLE multimedia IS 'Archivos multimedia de eventos (relación de composición)';
COMMENT ON TABLE servicios_adicionales IS 'Servicios complementarios de eventos (composición)';

-- Columnas importantes
COMMENT ON COLUMN eventos.activo IS 'Estado del evento, false = soft delete';
COMMENT ON COLUMN organizadores.validado IS 'Indica si el organizador fue aprobado por administración';
COMMENT ON COLUMN valoraciones.calificacion IS 'Puntuación obligatoria de 1 a 5 estrellas';
COMMENT ON COLUMN ubicaciones.coordenadas_gps IS 'Coordenadas geográficas en formato PostGIS (lat, lng)';

-- ===================================================================
-- 11. DATOS INICIALES - CATEGORÍAS
-- ===================================================================

INSERT INTO categorias_evento (id, nombre, descripcion, icono) VALUES 
(uuid_generate_v4(), 'Música', 'Conciertos, recitales y presentaciones musicales', 'music'),
(uuid_generate_v4(), 'Teatro', 'Obras teatrales, drama y comedia', 'theater'),
(uuid_generate_v4(), 'Danza', 'Ballet, danza contemporánea y folclórica', 'dance'),
(uuid_generate_v4(), 'Exposiciones', 'Arte visual, galerías y museos', 'gallery'),
(uuid_generate_v4(), 'Cine', 'Proyecciones y festivales de cine', 'movie'),
(uuid_generate_v4(), 'Literatura', 'Presentaciones de libros, poesía y narrativa', 'book'),
(uuid_generate_v4(), 'Festivales', 'Eventos culturales multidisciplinarios', 'festival'),
(uuid_generate_v4(), 'Talleres', 'Actividades formativas y educativas', 'workshop'),
(uuid_generate_v4(), 'Conferencias', 'Charlas y seminarios culturales', 'conference'),
(uuid_generate_v4(), 'Gastronomía', 'Eventos culinarios y gastronómicos', 'food');

-- ===================================================================
-- 12. VERIFICACIÓN FINAL
-- ===================================================================

-- Mostrar resumen de tablas creadas
SELECT 
    schemaname,
    tablename,
    tableowner
FROM pg_tables 
WHERE schemaname = 'public' 
ORDER BY tablename;

-- Mostrar tipos enumerados
SELECT 
    t.typname AS enum_name,
    e.enumlabel AS enum_value
FROM pg_type t 
JOIN pg_enum e ON t.oid = e.enumtypid  
WHERE t.typname LIKE '%_enum'
ORDER BY t.typname, e.enumsortorder;

-- Mostrar índices creados
SELECT 
    tablename,
    indexname,
    indexdef
FROM pg_indexes 
WHERE schemaname = 'public'
ORDER BY tablename, indexname;

-- ===================================================================
-- SCRIPT COMPLETADO EXITOSAMENTE
-- ===================================================================

\echo '=============================================================='
\echo 'ViveMedellin - Modelo Físico Creado Exitosamente'
\echo 'PostgreSQL 18 - Base de datos: vivemedellin'
\echo '=============================================================='
\echo 'Tablas creadas: 9'
\echo 'Tipos enumerados: 3'
\echo 'Restricciones: 15+'
\echo 'Índices: 20+'
\echo 'Triggers: 6'
\echo 'Categorías iniciales: 10'
\echo '=============================================================='
\echo 'Próximos pasos:'
\echo '1. Verificar conexión Spring Boot'
\echo '2. Ejecutar pruebas de integración'
\echo '3. Cargar datos de prueba adicionales'
\echo '=============================================================='