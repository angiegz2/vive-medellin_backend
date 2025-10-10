# ENTREGABLE 1: DEFINICIÓN DE ENTIDADES
## Proyecto ViveMedellin - Módulo de Gestión de Eventos

---

## 1. CONTEXTO Y ALCANCE DEL MÓDULO

### 1.1 Descripción del Módulo
El módulo de gestión de eventos de ViveMedellin es la funcionalidad central que permite a organizadores culturales crear, gestionar y promocionar eventos en la ciudad de Medellín, mientras que los ciudadanos pueden descubrir, valorar y participar en estas actividades culturales.

### 1.2 Objetivos del Módulo
- **Centralizar** la oferta cultural de Medellín en una plataforma única
- **Facilitar** la gestión de eventos para organizadores locales
- **Promover** la participación ciudadana en actividades culturales
- **Generar** datos para políticas públicas culturales

---

## 2. REGLAS DE NEGOCIO IDENTIFICADAS

### RN-01: Gestión de Eventos
**Descripción:** Todo evento cultural debe cumplir requisitos mínimos de información
- Un evento debe tener título, descripción, fecha, hora y ubicación
- Todo evento debe estar asociado a un organizador validado
- Los eventos pueden ser presenciales, virtuales o híbridos
- Un evento puede tener múltiples funciones/horarios
- Los eventos deben estar geolocalizados dentro de Medellín o área metropolitana

**Impacto en BD:** Tablas `eventos`, `organizadores`, `ubicaciones`, `funciones`

### RN-02: Sistema de Organizadores
**Descripción:** Los organizadores deben ser entidades verificadas y responsables
- Todo organizador debe proporcionar información de contacto válida
- Los organizadores requieren proceso de validación inicial
- Un organizador puede crear múltiples eventos
- Los organizadores pueden ser personas naturales o jurídicas

**Impacto en BD:** Tabla `organizadores`, validaciones de integridad

### RN-03: Gestión de Ubicaciones
**Descripción:** Toda ubicación debe estar georreferenciada y ser accesible
- Toda ubicación debe tener dirección completa y verificable
- Debe asociarse a comuna y barrio oficial de Medellín
- Puede incluir coordenadas GPS y enlace a mapa digital
- Las ubicaciones pueden reutilizarse para múltiples eventos

**Impacto en BD:** Tabla `ubicaciones`, índices geoespaciales

### RN-04: Sistema de Valoraciones y Retroalimentación
**Descripción:** Los usuarios pueden calificar eventos para generar confianza
- Las valoraciones van de 1 a 5 estrellas obligatoriamente
- Un usuario solo puede valorar una vez por evento
- Las valoraciones incluyen comentarios opcionales
- Solo usuarios registrados pueden valorar eventos

**Impacto en BD:** Tablas `valoraciones`, `usuarios`, restricciones únicas

### RN-05: Clasificación y Categorización
**Descripción:** Los eventos deben clasificarse para facilitar descubrimiento
- Todo evento debe asignarse a una categoría cultural válida
- Las categorías son predefinidas por la administración
- Los eventos pueden tener servicios adicionales opcionales
- Los eventos pueden marcarse como "destacados" por la administración

**Impacto en BD:** Tablas `categorias`, `servicios_adicionales`, campos booleanos

### RN-06: Gestión de Usuarios
**Descripción:** El sistema maneja diferentes tipos de usuarios con permisos diferenciados
- Ciudadanos: Pueden ver eventos, valorar y recibir notificaciones
- Organizadores: Pueden crear y gestionar sus eventos
- Administradores: Pueden moderar contenido y gestionar plataforma
- Todo usuario debe tener email único y verificado

**Impacto en BD:** Tabla `usuarios`, enumeraciones de tipo de usuario

---

## 3. ENTIDADES IDENTIFICADAS Y JUSTIFICACIÓN

### 3.1 Entidades Principales (Core Business)

#### EVENTO
**Propósito:** Representa la actividad cultural central del sistema
**Justificación:** Es la entidad nuclear alrededor de la cual gira todo el ecosistema
**Atributos clave:**
- Información básica: título, descripción, categoría
- Información temporal: fecha, hora, duración
- Información económica: valor de entrada, aforo
- Estados: activo, destacado, modalidad

#### ORGANIZADOR  
**Propósito:** Entidad responsable de crear y gestionar eventos
**Justificación:** Necesario para garantizar responsabilidad y contacto directo
**Atributos clave:**
- Información personal/empresarial: nombre, identificación
- Información de contacto: email, celular
- Estados: validado, fecha de validación

#### USUARIO
**Propósito:** Ciudadanos que interactúan con la plataforma
**Justificación:** Base para sistema de valoraciones y personalización
**Atributos clave:**
- Información personal: nombre, email, documento
- Información de actividad: fecha registro, última actividad
- Tipo de usuario: ciudadano, organizador, administrador

### 3.2 Entidades de Soporte (Supporting Entities)

#### UBICACION
**Propósito:** Geolocalización precisa de eventos
**Justificación:** Fundamental para búsquedas por proximidad y navegación
**Atributos clave:**
- Dirección completa y detallada
- Comuna y barrio (alineado con división política de Medellín)
- Coordenadas GPS opcionales
- Enlace a mapa digital

#### VALORACION
**Propósito:** Sistema de retroalimentación y calidad
**Justificación:** Genera confianza y mejora la experiencia del usuario
**Atributos clave:**
- Calificación numérica (1-5 estrellas)
- Comentario opcional
- Fechas de valoración
- Estado de moderación

#### FUNCION
**Propósito:** Manejo de múltiples horarios por evento
**Justificación:** Los eventos pueden repetirse en diferentes fechas/horas
**Atributos clave:**
- Fecha y hora específica
- Aforo particular de la función
- Estado activo/inactivo

### 3.3 Entidades de Configuración (Configuration Entities)

#### CATEGORIA_EVENTO
**Propósito:** Clasificación temática de eventos
**Justificación:** Facilita búsqueda y organización del contenido
**Atributos clave:**
- Nombre único de categoría
- Descripción y criterios
- Icono o símbolo asociado

#### MULTIMEDIA
**Propósito:** Contenido visual y promocional
**Justificación:** Mejora la presentación y atractivo de los eventos
**Atributos clave:**
- Tipo de multimedia (imagen, video, audio)
- URL del archivo
- Orden de visualización

#### SERVICIO_ADICIONAL  
**Propósito:** Información de servicios complementarios
**Justificación:** Proporciona valor agregado y información completa
**Atributos clave:**
- Nombre del servicio
- Descripción detallada
- Costo adicional si aplica

---

## 4. MATRIZ DE RELACIONES ENTRE ENTIDADES

| **Entidad A** | **Relación** | **Entidad B** | **Cardinalidad** | **Justificación de Negocio** |
|---------------|--------------|---------------|------------------|------------------------------|
| ORGANIZADOR | organiza | EVENTO | 1:N | Un organizador puede crear múltiples eventos |
| EVENTO | se_realiza_en | UBICACION | N:1 | Una ubicación puede hospedar múltiples eventos |
| EVENTO | tiene | FUNCION | 1:N | Un evento puede tener múltiples horarios |
| EVENTO | recibe | VALORACION | 1:N | Un evento puede ser valorado por múltiples usuarios |
| USUARIO | realiza | VALORACION | 1:N | Un usuario puede valorar múltiples eventos |
| EVENTO | pertenece_a | CATEGORIA_EVENTO | N:1 | Cada evento debe tener una categoría |
| EVENTO | incluye | MULTIMEDIA | 1:N | Un evento puede tener múltiples archivos multimedia |
| EVENTO | ofrece | SERVICIO_ADICIONAL | 1:N | Un evento puede ofrecer múltiples servicios |

---

## 5. ATRIBUTOS DETALLADOS POR ENTIDAD

### 5.1 EVENTO
| **Atributo** | **Tipo** | **Obligatorio** | **Descripción** | **Regla de Negocio** |
|--------------|----------|-----------------|-----------------|----------------------|
| id | UUID | Sí | Identificador único | Generado automáticamente |
| titulo | VARCHAR(200) | Sí | Título del evento | Min 5, max 200 caracteres |
| descripcion | TEXT | Sí | Descripción detallada | Min 10, max 2000 caracteres |
| fecha_evento | DATE | Sí | Fecha de realización | No puede ser fecha pasada |
| hora_evento | TIME | Sí | Hora de inicio | Formato 24 horas |
| categoria | VARCHAR(100) | Sí | Categoría cultural | Debe existir en catálogo |
| modalidad | ENUM | Sí | Presencial/Virtual/Híbrida | Valores predefinidos |
| aforo | INTEGER | No | Capacidad máxima | Mayor a 0 si se especifica |
| valor_ingreso | DECIMAL(10,2) | Sí | Precio entrada | Valor >= 0, gratuito = 0 |
| destacado | BOOLEAN | Sí | Marcado como destacado | Solo admin puede modificar |
| activo | BOOLEAN | Sí | Estado del evento | Por defecto true |
| imagen_caratula | VARCHAR(500) | No | URL imagen principal | URL válida si se especifica |

### 5.2 ORGANIZADOR
| **Atributo** | **Tipo** | **Obligatorio** | **Descripción** | **Regla de Negocio** |
|--------------|----------|-----------------|-----------------|----------------------|
| id | UUID | Sí | Identificador único | Generado automáticamente |
| nombre | VARCHAR(200) | Sí | Nombre o razón social | Min 2, max 200 caracteres |
| celular | VARCHAR(10) | Sí | Número celular | Exactamente 10 dígitos |
| identificacion | VARCHAR(20) | Sí | Cédula o NIT | Único en el sistema |
| email | VARCHAR(255) | Sí | Correo electrónico | Formato email válido, único |
| validado | BOOLEAN | Sí | Estado de validación | Por defecto false |
| fecha_validacion | TIMESTAMP | No | Cuándo fue validado | Se asigna al validar |

### 5.3 USUARIO
| **Atributo** | **Tipo** | **Obligatorio** | **Descripción** | **Regla de Negocio** |
|--------------|----------|-----------------|-----------------|----------------------|
| id | UUID | Sí | Identificador único | Generado automáticamente |
| nombre | VARCHAR(200) | Sí | Nombre completo | Min 2, max 200 caracteres |
| email | VARCHAR(255) | Sí | Correo electrónico | Formato válido, único |
| celular | VARCHAR(15) | No | Número de contacto | Formato válido si se especifica |
| documento_identificacion | VARCHAR(20) | No | Cédula | Único si se especifica |
| tipo_usuario | ENUM | Sí | Tipo de usuario | CIUDADANO/ORGANIZADOR/ADMIN |
| activo | BOOLEAN | Sí | Estado de la cuenta | Por defecto true |

### 5.4 UBICACION
| **Atributo** | **Tipo** | **Obligatorio** | **Descripción** | **Regla de Negocio** |
|--------------|----------|-----------------|-----------------|----------------------|
| id | UUID | Sí | Identificador único | Generado automáticamente |
| direccion_completa | VARCHAR(300) | Sí | Dirección completa | Min 5, max 300 caracteres |
| comuna_barrio | VARCHAR(100) | Sí | Comuna o barrio | Según división oficial Medellín |
| direccion_detallada | VARCHAR(300) | Sí | Detalles específicos | Puntos de referencia |
| enlace_mapa | VARCHAR(500) | No | URL a Google Maps | URL válida si se especifica |
| coordenadas_gps | POINT | No | Latitud y longitud | Coordenadas válidas |

### 5.5 VALORACION
| **Atributo** | **Tipo** | **Obligatorio** | **Descripción** | **Regla de Negocio** |
|--------------|----------|-----------------|-----------------|----------------------|
| id | UUID | Sí | Identificador único | Generado automáticamente |
| evento_id | UUID | Sí | Referencia al evento | Debe existir evento |
| usuario_id | UUID | Sí | Referencia al usuario | Debe existir usuario |
| calificacion | INTEGER | Sí | Puntuación 1-5 | Valor entre 1 y 5 |
| comentario | TEXT | No | Comentario opcional | Max 1000 caracteres |
| fecha_valoracion | TIMESTAMP | Sí | Cuándo se valoró | Automático al crear |
| moderada | BOOLEAN | Sí | Si fue revisada | Por defecto false |

---

## 6. VALIDACIONES Y RESTRICCIONES DE NEGOCIO

### 6.1 Restricciones de Integridad Referencial
- **Eventos → Organizadores:** Todo evento debe tener un organizador válido y activo
- **Eventos → Ubicaciones:** Todo evento debe tener una ubicación válida
- **Valoraciones → Eventos:** No se pueden valorar eventos que no existan
- **Valoraciones → Usuarios:** Solo usuarios registrados pueden valorar

### 6.2 Restricciones de Dominio
- **Fechas:** Los eventos no pueden programarse en fechas pasadas
- **Valoraciones:** Solo valores de 1 a 5 estrellas
- **Emails:** Deben cumplir formato estándar de email
- **Celulares:** Solo números de 10 dígitos para Colombia

### 6.3 Restricciones de Unicidad
- **Email de organizadores:** Un email solo puede estar asociado a un organizador
- **Email de usuarios:** Un email solo puede estar asociado a un usuario
- **Identificación:** Cédulas/NITs únicos en organizadores
- **Valoración por usuario:** Un usuario solo puede valorar una vez el mismo evento

### 6.4 Restricciones de Estado
- **Eventos activos:** Solo eventos activos aparecen en búsquedas públicas
- **Organizadores validados:** Solo organizadores validados pueden crear eventos nuevos
- **Usuarios activos:** Solo usuarios activos pueden valorar eventos

---

## 7. CASOS DE USO PRINCIPALES QUE SOPORTAN LAS ENTIDADES

### CU-01: Crear Evento
**Actor:** Organizador validado  
**Entidades involucradas:** EVENTO, ORGANIZADOR, UBICACION, CATEGORIA_EVENTO  
**Flujo:** Organizador → crea → Evento → en → Ubicación → de → Categoría

### CU-02: Valorar Evento
**Actor:** Usuario registrado  
**Entidades involucradas:** VALORACION, EVENTO, USUARIO  
**Flujo:** Usuario → crea → Valoración → para → Evento

### CU-03: Buscar Eventos
**Actor:** Ciudadano (cualquier usuario)  
**Entidades involucradas:** EVENTO, UBICACION, CATEGORIA_EVENTO, VALORACION  
**Flujo:** Sistema → busca → Eventos → por → Ubicación/Categoría → muestra → Valoraciones

### CU-04: Gestionar Funciones de Evento
**Actor:** Organizador  
**Entidades involucradas:** FUNCION, EVENTO  
**Flujo:** Organizador → gestiona → Funciones → de → sus Eventos

---

## 8. CONSIDERACIONES TÉCNICAS Y DE ESCALABILIDAD

### 8.1 Volumen de Datos Esperado
- **Eventos:** ~10,000 eventos anuales
- **Usuarios:** ~100,000 usuarios registrados
- **Valoraciones:** ~50,000 valoraciones anuales
- **Ubicaciones:** ~5,000 ubicaciones únicas

### 8.2 Patrones de Acceso
- **Lectura intensiva:** Búsquedas y consultas de eventos
- **Escritura moderada:** Creación de eventos y valoraciones
- **Picos de carga:** Festivales y eventos masivos

### 8.3 Requerimientos Especiales
- **Geolocalización:** Búsquedas por proximidad geográfica
- **Texto completo:** Búsqueda en títulos y descripciones
- **Análisis temporal:** Reportes por períodos de tiempo
- **Moderación:** Sistema de revisión de contenido

---

**Documento:** Entregable 1 - Definición de Entidades  
**Fecha:** Septiembre 2025  
**Estado:** Revisión Product Owner  
**Próximo paso:** Validación con stakeholders técnicos