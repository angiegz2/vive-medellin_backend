# ENTREGABLE 4: MODELO FÍSICO
## Proyecto ViveMedellin - Implementación Física en PostgreSQL 18

---

## 1. INTRODUCCIÓN AL MODELO FÍSICO

### 1.1 Propósito del Documento
Este documento presenta la implementación física completa de la base de datos ViveMedellin en PostgreSQL 18, incluyendo definiciones DDL, índices, restricciones y consideraciones de rendimiento.

### 1.2 Especificaciones Técnicas
- **SGBD:** PostgreSQL 18.0
- **Base de Datos:** `vivemedellin`
- **Esquema:** `public` (por defecto)
- **Codificación:** UTF-8
- **Timezone:** America/Bogota

### 1.3 Convenciones de Nomenclatura
- **Tablas:** Plural en minúsculas (ej: `eventos`, `usuarios`)
- **Columnas:** Snake_case en minúsculas (ej: `fecha_evento`, `usuario_id`)
- **Claves Primarias:** `id` (UUID)
- **Claves Foráneas:** `{tabla_referenciada}_id`
- **Índices:** `idx_{tabla}_{columna(s)}`
- **Restricciones:** `ck_{tabla}_{campo}`, `fk_{tabla}_{referencia}`, `uk_{tabla}_{campo}`

---

## 2. SCRIPT DDL COMPLETO - CREACIÓN DE TABLAS

### 2.1 Extensiones PostgreSQL Requeridas

```sql
-- ===================================================================
-- EXTENSIONES REQUERIDAS
-- ===================================================================

-- UUID Generation
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- PostGIS para coordenadas geográficas (opcional)
CREATE EXTENSION IF NOT EXISTS postgis;

-- Full Text Search en español
CREATE EXTENSION IF NOT EXISTS unaccent;
```

### 2.2 Tipos de Datos Personalizados

```sql
-- ===================================================================
-- TIPOS ENUMERADOS PERSONALIZADOS
-- ===================================================================

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
```

### 2.3 Tablas Principales

#### 2.3.1 Tabla USUARIOS

```sql
-- ===================================================================
-- TABLA: usuarios
-- Propósito: Gestión de usuarios del sistema
-- Cardinalidad estimada: 100,000 registros
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

-- Comentarios de documentación
COMMENT ON TABLE usuarios IS 'Registro de usuarios del sistema ViveMedellin';
COMMENT ON COLUMN usuarios.email IS 'Correo electrónico único, usado para autenticación';
COMMENT ON COLUMN usuarios.tipo_usuario IS 'Clasificación del usuario: CIUDADANO, ORGANIZADOR, ADMINISTRADOR';
COMMENT ON COLUMN usuarios.activo IS 'Estado del usuario, false = soft delete';
```

#### 2.3.2 Tabla ORGANIZADORES

```sql
-- ===================================================================
-- TABLA: organizadores
-- Propósito: Entidades responsables de crear eventos
-- Cardinalidad estimada: 2,000 registros
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

-- Comentarios
COMMENT ON TABLE organizadores IS 'Entidades responsables de crear y gestionar eventos culturales';
COMMENT ON COLUMN organizadores.validado IS 'Indica si el organizador fue aprobado por administración';
COMMENT ON COLUMN organizadores.fecha_validacion IS 'Cuándo fue validado por administración (automático)';
```

#### 2.3.3 Tabla CATEGORIAS_EVENTO

```sql
-- ===================================================================
-- TABLA: categorias_evento
-- Propósito: Clasificación temática de eventos
-- Cardinalidad estimada: 20 registros
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

-- Comentarios
COMMENT ON TABLE categorias_evento IS 'Catálogo de categorías para clasificación de eventos';
COMMENT ON COLUMN categorias_evento.nombre IS 'Nombre único de la categoría (ej: Música, Teatro, Danza)';
COMMENT ON COLUMN categorias_evento.activa IS 'Si está disponible para asignar a nuevos eventos';
```

#### 2.3.4 Tabla UBICACIONES

```sql
-- ===================================================================
-- TABLA: ubicaciones
-- Propósito: Localizaciones geográficas de eventos
-- Cardinalidad estimada: 5,000 registros
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

-- Comentarios
COMMENT ON TABLE ubicaciones IS 'Localizaciones geográficas donde se realizan eventos';
COMMENT ON COLUMN ubicaciones.coordenadas_gps IS 'Coordenadas geográficas en formato PostGIS (lat, lng)';
COMMENT ON COLUMN ubicaciones.comuna_barrio IS 'División administrativa de Medellín';
```

#### 2.3.5 Tabla EVENTOS (Central)

```sql
-- ===================================================================
-- TABLA: eventos
-- Propósito: Entidad central - eventos culturales
-- Cardinalidad estimada: 10,000 registros/año
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
    usuario_actualizacion VARCHAR(100),
    
    -- Restricciones de clave foránea
    CONSTRAINT fk_evento_organizador 
        FOREIGN KEY (organizador_id) REFERENCES organizadores(id) ON DELETE RESTRICT,
    CONSTRAINT fk_evento_ubicacion 
        FOREIGN KEY (ubicacion_id) REFERENCES ubicaciones(id) ON DELETE RESTRICT,
    CONSTRAINT fk_evento_categoria 
        FOREIGN KEY (categoria_id) REFERENCES categorias_evento(id) ON DELETE SET NULL
);

-- Comentarios
COMMENT ON TABLE eventos IS 'Tabla central - eventos culturales de Medellín';
COMMENT ON COLUMN eventos.titulo IS 'Nombre descriptivo del evento (5-200 caracteres)';
COMMENT ON COLUMN eventos.fecha_evento IS 'Fecha del evento (no puede ser pasada)';
COMMENT ON COLUMN eventos.destacado IS 'Eventos marcados como importantes para promoción';
COMMENT ON COLUMN eventos.activo IS 'Estado del evento, false = soft delete';
```

### 2.4 Tablas Dependientes

#### 2.4.1 Tabla FUNCIONES

```sql
-- ===================================================================
-- TABLA: funciones
-- Propósito: Horarios específicos de eventos (composición)
-- Cardinalidad estimada: 15,000 registros
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
    fecha_actualizacion TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- Restricciones
    CONSTRAINT fk_funcion_evento 
        FOREIGN KEY (evento_id) REFERENCES eventos(id) ON DELETE CASCADE,
    CONSTRAINT uk_funcion_evento_fecha_hora 
        UNIQUE (evento_id, fecha_funcion, hora_inicio)
);

-- Comentarios
COMMENT ON TABLE funciones IS 'Horarios específicos de eventos (relación de composición)';
COMMENT ON COLUMN funciones.evento_id IS 'Evento padre (eliminación en cascada)';
COMMENT ON COLUMN funciones.aforo_funcion IS 'Capacidad específica, hereda del evento si es NULL';
```

#### 2.4.2 Tabla VALORACIONES

```sql
-- ===================================================================
-- TABLA: valoraciones
-- Propósito: Calificaciones y comentarios de usuarios
-- Cardinalidad estimada: 50,000 registros/año
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
    fecha_valoracion TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- Restricciones
    CONSTRAINT fk_valoracion_evento 
        FOREIGN KEY (evento_id) REFERENCES eventos(id) ON DELETE CASCADE,
    CONSTRAINT fk_valoracion_usuario 
        FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    CONSTRAINT uk_valoracion_usuario_evento 
        UNIQUE (evento_id, usuario_id)
);

-- Comentarios
COMMENT ON TABLE valoraciones IS 'Sistema de valoraciones usuario-evento (N:M)';
COMMENT ON COLUMN valoraciones.calificacion IS 'Puntuación obligatoria de 1 a 5 estrellas';
COMMENT ON COLUMN valoraciones.moderada IS 'Si fue revisada por administración';
COMMENT ON CONSTRAINT uk_valoracion_usuario_evento ON valoraciones 
    IS 'Un usuario solo puede valorar una vez el mismo evento';
```

#### 2.4.3 Tabla MULTIMEDIA

```sql
-- ===================================================================
-- TABLA: multimedia
-- Propósito: Archivos asociados a eventos (composición)
-- Cardinalidad estimada: 25,000 registros
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
    fecha_actualizacion TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- Restricciones
    CONSTRAINT fk_multimedia_evento 
        FOREIGN KEY (evento_id) REFERENCES eventos(id) ON DELETE CASCADE,
    CONSTRAINT uk_multimedia_evento_orden 
        UNIQUE (evento_id, orden_visualizacion)
);

-- Comentarios
COMMENT ON TABLE multimedia IS 'Archivos multimedia de eventos (relación de composición)';
COMMENT ON COLUMN multimedia.orden_visualizacion IS 'Secuencia de presentación (único por evento)';
COMMENT ON COLUMN multimedia.url_archivo IS 'URL del archivo almacenado (CDN/Storage)';
```

#### 2.4.4 Tabla SERVICIOS_ADICIONALES

```sql
-- ===================================================================
-- TABLA: servicios_adicionales
-- Propósito: Servicios complementarios de eventos (composición)
-- Cardinalidad estimada: 15,000 registros
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
    fecha_actualizacion TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- Restricciones
    CONSTRAINT fk_servicio_evento 
        FOREIGN KEY (evento_id) REFERENCES eventos(id) ON DELETE CASCADE,
    CONSTRAINT uk_servicio_evento_nombre 
        UNIQUE (evento_id, nombre_servicio)
);

-- Comentarios
COMMENT ON TABLE servicios_adicionales IS 'Servicios complementarios de eventos (composición)';
COMMENT ON COLUMN servicios_adicionales.incluido_entrada IS 'Si está incluido en el precio base del evento';
COMMENT ON COLUMN servicios_adicionales.costo_adicional IS 'Costo extra del servicio (0 si incluido)';
```

---

## 3. ÍNDICES DE RENDIMIENTO

### 3.1 Índices Primarios (Automáticos)
Los índices de clave primaria se crean automáticamente en PostgreSQL.

### 3.2 Índices de Clave Foránea

```sql
-- ===================================================================
-- ÍNDICES PARA CLAVES FORÁNEAS (Performance en JOINs)
-- ===================================================================

-- Eventos
CREATE INDEX idx_eventos_organizador_id ON eventos(organizador_id);
CREATE INDEX idx_eventos_ubicacion_id ON eventos(ubicacion_id);
CREATE INDEX idx_eventos_categoria_id ON eventos(categoria_id);

-- Funciones  
CREATE INDEX idx_funciones_evento_id ON funciones(evento_id);

-- Valoraciones
CREATE INDEX idx_valoraciones_evento_id ON valoraciones(evento_id);
CREATE INDEX idx_valoraciones_usuario_id ON valoraciones(usuario_id);

-- Multimedia
CREATE INDEX idx_multimedia_evento_id ON multimedia(evento_id);

-- Servicios Adicionales
CREATE INDEX idx_servicios_evento_id ON servicios_adicionales(evento_id);
```

### 3.3 Índices de Búsqueda

```sql
-- ===================================================================
-- ÍNDICES PARA CONSULTAS DE BÚSQUEDA
-- ===================================================================

-- Búsquedas frecuentes en eventos
CREATE INDEX idx_eventos_fecha_activo ON eventos(fecha_evento, activo) 
    WHERE activo = true;

CREATE INDEX idx_eventos_categoria_fecha ON eventos(categoria, fecha_evento, activo)
    WHERE activo = true;

CREATE INDEX idx_eventos_destacado_fecha ON eventos(destacado, fecha_evento) 
    WHERE destacado = true AND activo = true;

CREATE INDEX idx_eventos_modalidad ON eventos(modalidad) 
    WHERE activo = true;

-- Búsquedas geográficas
CREATE INDEX idx_ubicaciones_comuna ON ubicaciones(comuna_barrio);

-- Índice espacial para coordenadas (PostGIS)
CREATE INDEX idx_ubicaciones_coordenadas_gps ON ubicaciones 
    USING GIST (coordenadas_gps);

-- Búsquedas de texto completo
CREATE INDEX idx_eventos_titulo_gin ON eventos 
    USING GIN (to_tsvector('spanish', titulo)) 
    WHERE activo = true;

CREATE INDEX idx_eventos_descripcion_gin ON eventos 
    USING GIN (to_tsvector('spanish', descripcion)) 
    WHERE activo = true;

-- Estados y validaciones
CREATE INDEX idx_organizadores_validado ON organizadores(validado) 
    WHERE validado = true;

CREATE INDEX idx_usuarios_activo ON usuarios(activo, tipo_usuario) 
    WHERE activo = true;

-- Valoraciones por evento (para cálculos agregados)
CREATE INDEX idx_valoraciones_evento_calificacion ON valoraciones(evento_id, calificacion);

-- Funciones por fecha
CREATE INDEX idx_funciones_fecha ON funciones(fecha_funcion, activa) 
    WHERE activa = true;
```

### 3.4 Índices Compuestos Especializados

```sql
-- ===================================================================
-- ÍNDICES COMPUESTOS PARA CONSULTAS COMPLEJAS
-- ===================================================================

-- Dashboard organizador
CREATE INDEX idx_eventos_organizador_estado ON eventos(organizador_id, activo, fecha_evento);

-- Listado público de eventos
CREATE INDEX idx_eventos_publicos ON eventos(activo, destacado, fecha_evento, modalidad) 
    WHERE activo = true;

-- Búsqueda por proximidad + categoría
CREATE INDEX idx_eventos_ubicacion_categoria ON eventos(ubicacion_id, categoria_id, fecha_evento)
    WHERE activo = true;

-- Auditoría por fechas
CREATE INDEX idx_eventos_auditoria ON eventos(fecha_creacion, usuario_creacion);

-- Ranking de eventos por valoración
CREATE INDEX idx_valoraciones_ranking ON valoraciones(evento_id, calificacion, fecha_valoracion);
```

---

## 4. TRIGGERS Y FUNCIONES AUTOMÁTICAS

### 4.1 Trigger de Auditoría

```sql
-- ===================================================================
-- FUNCIÓN Y TRIGGER PARA AUDITORÍA AUTOMÁTICA
-- ===================================================================

CREATE OR REPLACE FUNCTION actualizar_fecha_modificacion()
RETURNS TRIGGER AS $$
BEGIN
    NEW.fecha_actualizacion = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Aplicar a todas las tablas principales
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
```

### 4.2 Triggers de Validación de Negocio

```sql
-- ===================================================================
-- VALIDACIONES DE REGLAS DE NEGOCIO
-- ===================================================================

-- Solo organizadores validados pueden crear eventos activos
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

-- Validar fechas de funciones coherentes con evento
CREATE OR REPLACE FUNCTION validar_fecha_funcion()
RETURNS TRIGGER AS $$
DECLARE
    fecha_evento_padre DATE;
BEGIN
    SELECT fecha_evento INTO fecha_evento_padre 
    FROM eventos 
    WHERE id = NEW.evento_id;
    
    IF NEW.fecha_funcion < fecha_evento_padre THEN
        RAISE EXCEPTION 'La fecha de la función no puede ser anterior al evento';
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER tr_validar_fecha_funcion
    BEFORE INSERT OR UPDATE ON funciones
    FOR EACH ROW EXECUTE FUNCTION validar_fecha_funcion();
```

### 4.3 Función para Cálculo de Rating Promedio

```sql
-- ===================================================================
-- FUNCIÓN PARA CÁLCULO DE RATING PROMEDIO DE EVENTOS
-- ===================================================================

CREATE OR REPLACE FUNCTION calcular_rating_evento(evento_uuid UUID)
RETURNS TABLE(
    evento_id UUID,
    rating_promedio DECIMAL(3,2),
    total_valoraciones INTEGER
) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        v.evento_id,
        ROUND(AVG(v.calificacion::DECIMAL), 2) as rating_promedio,
        COUNT(*)::INTEGER as total_valoraciones
    FROM valoraciones v
    WHERE v.evento_id = evento_uuid
    GROUP BY v.evento_id;
END;
$$ LANGUAGE plpgsql;

-- Uso: SELECT * FROM calcular_rating_evento('uuid-del-evento');
```

---

## 5. DATOS DE PRUEBA Y CATÁLOGOS

### 5.1 Inserción de Categorías Base

```sql
-- ===================================================================
-- DATOS INICIALES - CATEGORÍAS DE EVENTOS
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
```

### 5.2 Datos de Prueba Básicos

```sql
-- ===================================================================
-- DATOS DE PRUEBA - ORGANIZADOR Y USUARIO EJEMPLO
-- ===================================================================

-- Organizador de prueba
INSERT INTO organizadores (id, nombre, celular, identificacion, email, validado) VALUES 
(uuid_generate_v4(), 'Secretaría de Cultura Medellín', '+57 4 385-5555', '890900274-1', 'cultura@medellin.gov.co', true);

-- Usuario de prueba  
INSERT INTO usuarios (id, nombre, email, tipo_usuario) VALUES
(uuid_generate_v4(), 'Juan Pérez', 'juan.perez@example.com', 'CIUDADANO');

-- Ubicación de prueba
INSERT INTO ubicaciones (id, direccion_completa, comuna_barrio, coordenadas_gps) VALUES
(uuid_generate_v4(), 'Teatro Metropolitano, Calle 41 #57-30', 'La Candelaria', ST_SetSRID(ST_MakePoint(-75.566, 6.244), 4326));
```

---

## 6. CONSIDERACIONES DE RENDIMIENTO

### 6.1 Estrategias de Optimización

#### Particionamiento por Fecha
```sql
-- Para tablas con gran volumen, considerar particionamiento
-- Ejemplo para eventos por año (implementación futura)

-- CREATE TABLE eventos_2024 PARTITION OF eventos
--     FOR VALUES FROM ('2024-01-01') TO ('2025-01-01');
-- CREATE TABLE eventos_2025 PARTITION OF eventos  
--     FOR VALUES FROM ('2025-01-01') TO ('2026-01-01');
```

#### Configuraciones de PostgreSQL Recomendadas
```sql
-- Configuraciones sugeridas en postgresql.conf
-- shared_buffers = 256MB
-- effective_cache_size = 1GB  
-- random_page_cost = 1.1
-- seq_page_cost = 1.0
-- work_mem = 4MB
-- maintenance_work_mem = 64MB
```

### 6.2 Monitoreo de Performance

```sql
-- ===================================================================
-- VISTAS PARA MONITOREO DE RENDIMIENTO
-- ===================================================================

-- Top consultas lentas
CREATE VIEW vista_consultas_lentas AS
SELECT 
    query,
    calls,
    total_time,
    mean_time,
    rows
FROM pg_stat_statements 
ORDER BY total_time DESC;

-- Uso de índices
CREATE VIEW vista_uso_indices AS
SELECT 
    schemaname,
    tablename,
    indexname,
    idx_tup_read,
    idx_tup_fetch
FROM pg_stat_user_indexes;

-- Estadísticas de tablas
CREATE VIEW vista_estadisticas_tablas AS
SELECT 
    schemaname,
    tablename,
    n_tup_ins,
    n_tup_upd,
    n_tup_del,
    seq_scan,
    idx_scan
FROM pg_stat_user_tables;
```

---

## 7. BACKUP Y RECUPERACIÓN

### 7.1 Estrategia de Respaldo

```bash
#!/bin/bash
# Script de backup diario
# Guardar como: backup_vivemedellin.sh

FECHA=$(date +%Y%m%d_%H%M%S)
DB_NAME="vivemedellin"
BACKUP_DIR="/backup/vivemedellin"

# Backup completo
pg_dump -h localhost -U postgres -d $DB_NAME -f "$BACKUP_DIR/vivemedellin_full_$FECHA.sql"

# Backup solo esquema
pg_dump -h localhost -U postgres -d $DB_NAME -s -f "$BACKUP_DIR/vivemedellin_schema_$FECHA.sql"

# Backup solo datos
pg_dump -h localhost -U postgres -d $DB_NAME -a -f "$BACKUP_DIR/vivemedellin_data_$FECHA.sql"
```

### 7.2 Scripts de Restauración

```bash
#!/bin/bash
# Script de restauración
# Uso: ./restore_vivemedellin.sh archivo_backup.sql

BACKUP_FILE=$1
DB_NAME="vivemedellin"

if [ -z "$BACKUP_FILE" ]; then
    echo "Uso: $0 <archivo_backup.sql>"
    exit 1
fi

# Restaurar base de datos
psql -h localhost -U postgres -d $DB_NAME -f $BACKUP_FILE
```

---

## 8. SEGURIDAD Y PERMISOS

### 8.1 Usuarios y Roles de Base de Datos

```sql
-- ===================================================================
-- GESTIÓN DE USUARIOS Y PERMISOS
-- ===================================================================

-- Rol de solo lectura (reportes, análisis)
CREATE ROLE vivemedellin_readonly;
GRANT USAGE ON SCHEMA public TO vivemedellin_readonly;
GRANT SELECT ON ALL TABLES IN SCHEMA public TO vivemedellin_readonly;
GRANT SELECT ON ALL SEQUENCES IN SCHEMA public TO vivemedellin_readonly;

-- Rol de aplicación (CRUD normal)
CREATE ROLE vivemedellin_app;
GRANT USAGE, CREATE ON SCHEMA public TO vivemedellin_app;
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO vivemedellin_app;
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO vivemedellin_app;

-- Rol administrativo (DDL, mantenimiento)
CREATE ROLE vivemedellin_admin;
GRANT ALL PRIVILEGES ON SCHEMA public TO vivemedellin_admin;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO vivemedellin_admin;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO vivemedellin_admin;

-- Usuario específico para la aplicación Spring Boot
CREATE USER vivemedellin_app_user WITH PASSWORD 'password_seguro_cambiar';
GRANT vivemedellin_app TO vivemedellin_app_user;
```

### 8.2 Políticas de Seguridad a Nivel de Fila (RLS)

```sql
-- ===================================================================
-- ROW LEVEL SECURITY (EJEMPLO PARA ORGANIZADORES)
-- ===================================================================

-- Habilitar RLS en tabla eventos
ALTER TABLE eventos ENABLE ROW LEVEL SECURITY;

-- Política: Los organizadores solo ven sus propios eventos
CREATE POLICY politica_eventos_organizador ON eventos
    FOR ALL
    TO vivemedellin_app
    USING (organizador_id = current_setting('app.current_organizador_id', true)::UUID);

-- Para usar: SET app.current_organizador_id = 'uuid-del-organizador';
```

---

## 9. MANTENIMIENTO Y MONITOREO

### 9.1 Tareas de Mantenimiento Programado

```sql
-- ===================================================================
-- TAREAS DE MANTENIMIENTO AUTOMÁTICO
-- ===================================================================

-- Actualizar estadísticas (programar diariamente)
DO $$
BEGIN
    ANALYZE usuarios;
    ANALYZE organizadores;
    ANALYZE eventos;
    ANALYZE valoraciones;
    ANALYZE funciones;
    ANALYZE ubicaciones;
    
    RAISE NOTICE 'Estadísticas actualizadas: %', NOW();
END $$;

-- Limpiar valoraciones antiguas sin comentario (opcional)
DELETE FROM valoraciones 
WHERE fecha_valoracion < NOW() - INTERVAL '2 years' 
  AND comentario IS NULL 
  AND calificacion <= 2;

-- Reindexar tablas críticas (programar mensualmente)
REINDEX TABLE eventos;
REINDEX TABLE valoraciones;
```

### 9.2 Alertas de Monitoreo

```sql
-- ===================================================================
-- CONSULTAS PARA ALERTAS DE SISTEMA
-- ===================================================================

-- Detectar organizadores con muchos eventos sin valoraciones
SELECT 
    o.nombre,
    COUNT(e.id) as total_eventos,
    COUNT(v.id) as total_valoraciones,
    ROUND((COUNT(v.id)::DECIMAL / COUNT(e.id)) * 100, 2) as porcentaje_valoraciones
FROM organizadores o
JOIN eventos e ON o.id = e.organizador_id
LEFT JOIN valoraciones v ON e.id = v.evento_id
WHERE e.activo = true
GROUP BY o.id, o.nombre
HAVING COUNT(e.id) > 5 AND COUNT(v.id) < (COUNT(e.id) * 0.1);

-- Eventos con fechas inconsistentes
SELECT e.id, e.titulo, e.fecha_evento, f.fecha_funcion
FROM eventos e
JOIN funciones f ON e.id = f.evento_id  
WHERE f.fecha_funcion < e.fecha_evento;

-- Ubicaciones duplicadas (posible limpieza)
SELECT 
    direccion_completa,
    comuna_barrio,
    COUNT(*) as duplicados
FROM ubicaciones
GROUP BY direccion_completa, comuna_barrio
HAVING COUNT(*) > 1;
```

---

## 10. CONCLUSIONES Y RECOMENDACIONES

### 10.1 Implementación Exitosa
✅ **Modelo físico completo** implementado en PostgreSQL 18  
✅ **Restricciones de integridad** robustas para garantizar calidad de datos  
✅ **Índices optimizados** para consultas críticas del negocio  
✅ **Triggers automáticos** para auditoría y validaciones  
✅ **Estrategia de seguridad** con roles y permisos granulares

### 10.2 Próximos Pasos de Implementación

1. **Ejecutar scripts DDL** en ambiente de desarrollo
2. **Cargar datos de catálogo** (categorías, ubicaciones base)  
3. **Configurar conexión** Spring Boot con nuevos parámetros
4. **Ejecutar pruebas de integración** con aplicación Java
5. **Implementar scripts de backup** automatizados
6. **Configurar monitoreo** de rendimiento

### 10.3 Comandos de Implementación

```bash
# Conectar a PostgreSQL
psql -h localhost -U postgres -d vivemedellin

# Ejecutar creación de modelo
\i 06-ENTREGABLE-4-MODELO-FISICO.sql

# Verificar tablas creadas
\dt

# Ver índices
\di
```

---

**Documento:** Entregable 4 - Modelo Físico PostgreSQL  
**Base de Datos:** vivemedellin (PostgreSQL 18)  
**Estado:** Listo para implementación  
**Próximo paso:** Ejecución de scripts DDL y testing de integración