# 🔧 Documentación de Microservicios REST - ViveMedellín

## 📋 Arquitectura de Microservicios

El backend de ViveMedellín está diseñado con una arquitectura modular que separa las responsabilidades en **3 microservicios REST** independientes pero cohesivos.

---

## 🎯 Microservicio 1: Búsqueda y Consulta de Eventos

### Descripción
Microservicio público responsable de la búsqueda, filtrado y consulta de eventos culturales.

### Responsabilidades
- Búsqueda avanzada con múltiples filtros
- Búsqueda simple por texto
- Consulta de próximos eventos
- Paginación y ordenamiento
- Vista de carrusel de destacados

### Endpoints

#### 1. Búsqueda Avanzada
```http
GET /api/public/eventos/buscar
```

**Parámetros Query (13 filtros):**
- `texto` (String): Busca en título, descripción, organizador
- `ubicacion` (String): Comuna o barrio
- `categoria` (String): Categoría del evento
- `fechaDesde` (LocalDate): Fecha inicio rango
- `fechaHasta` (LocalDate): Fecha fin rango
- `gratuito` (Boolean): Solo eventos gratuitos
- `modalidad` (String): PRESENCIAL/VIRTUAL/HIBRIDA
- `organizador` (String): Nombre del organizador
- `precioMinimo` (Double): Precio mínimo
- `precioMaximo` (Double): Precio máximo
- `horario` (String): DIURNO/NOCTURNO
- `servicio` (String): Servicios adicionales
- `disponible` (Boolean): Solo eventos disponibles
- `tipoVista` (String): MOSAICO/LISTA
- `page` (Integer): Número de página
- `size` (Integer): Tamaño de página
- `ordenarPor` (String): Campo ordenamiento
- `direccion` (String): ASC/DESC

**Respuesta:**
```json
{
  "content": [
    {
      "id": 1,
      "titulo": "Festival de Jazz",
      "categoria": "Música",
      "fechaEvento": "2025-11-20",
      "horaEvento": "18:00:00",
      "ubicacion": "El Poblado",
      "valorIngreso": "Gratuito",
      "destacado": true,
      "modalidad": "PRESENCIAL",
      "disponible": true
    }
  ],
  "totalElements": 95,
  "totalPages": 5,
  "size": 20,
  "number": 0
}
```

#### 2. Búsqueda Simple
```http
GET /api/public/eventos/buscar-simple?q={texto}
```

**Características:**
- Búsqueda rápida sin acentos
- Case-insensitive
- Busca en título, descripción y organizador

#### 3. Carrusel de Destacados
```http
GET /api/public/eventos/destacados-carrusel
```

**Respuesta:**
```json
{
  "eventos": [...],
  "cantidad": 3,
  "mensaje": "Eventos destacados cargados exitosamente"
}
```

**Lógica de Negocio:**
- Máximo 3 eventos simultáneamente
- Solo eventos con funciones vigentes (futuras)
- Verificación en tiempo real: `fecha > HOY OR (fecha = HOY AND hora > AHORA)`

#### 4. Próximos Eventos
```http
GET /api/public/eventos/proximos?dias={dias}
```

**Parámetros:**
- `dias` (Integer, default=30): Número de días hacia adelante

### Componentes Técnicos

**Controller:**
```java
@RestController
@RequestMapping("/api/public/eventos")
@CrossOrigin(origins = "*")
public class EventoPublicController {
    // 5 endpoints públicos
}
```

**Especificaciones JPA:**
```java
@Component
public class EventoSpecification {
    // 13 filtros dinámicos
    // Construcción de Predicates con JPA Criteria API
}
```

**Repository:**
```java
public interface EventoRepository extends 
    JpaRepository<Evento, Long>, 
    JpaSpecificationExecutor<Evento> {
    
    @Query("""
        SELECT COUNT(DISTINCT e.id) 
        FROM Evento e JOIN e.funciones f 
        WHERE e.destacado = true 
        AND e.status = 'PUBLISHED'
        AND (f.fecha > CURRENT_DATE 
             OR (f.fecha = CURRENT_DATE 
                 AND f.horario > CURRENT_TIME))
    """)
    Long countDestacadosVigentes();
}
```

### DTOs

**EventoMosaicoDTO** (Vista tarjetas, 20 por página):
```java
public record EventoMosaicoDTO(
    Long id,
    String imagenCaratula,
    String titulo,
    String categoria,
    LocalDate fechaEvento,
    LocalTime horaEvento,
    String ubicacion,
    String direccionCompleta,
    String nombreOrganizador,
    String valorIngreso,
    Boolean destacado,
    String modalidad,
    Boolean disponible
) {}
```

**EventoListaDTO** (Vista compacta, 50 por página):
```java
public record EventoListaDTO(
    Long id,
    String titulo,
    LocalDate fechaEvento,
    LocalTime horaEvento,
    String ubicacion,
    String direccionCompleta,
    String nombreOrganizador,
    String categoria,
    String valorIngreso,
    Boolean destacado,
    Boolean disponible
) {}
```

### Características Destacadas

1. **Búsqueda sin Acentos:**
   - Extensión PostgreSQL `unaccent`
   - "música" = "musica" = "MÚSICA"

2. **Paginación Inteligente:**
   - MOSAICO: 20 eventos por página
   - LISTA: 50 eventos por página
   - Personalizable con parámetro `size`

3. **Performance:**
   - Queries optimizadas con índices
   - DISTINCT para evitar duplicados
   - JOIN FETCH para evitar N+1

---

## 🎭 Microservicio 2: Gestión de Eventos

### Descripción
Microservicio responsable de proporcionar información detallada de eventos individuales.

### Responsabilidades
- Consulta de detalle completo de evento
- Información de funciones (fechas/horarios)
- Datos de ubicación y organizador
- Cálculo dinámico de estado del evento
- Material complementario (imágenes, videos, enlaces)

### Endpoints

#### 1. Detalle de Evento
```http
GET /api/public/eventos/{id}
```

**Parámetros Path:**
- `id` (Long, requerido): ID del evento

**Respuesta Completa:**
```json
{
  "id": 1,
  "titulo": "Festival de Jazz Internacional",
  "descripcion": "Gran festival con artistas nacionales e internacionales...",
  "categoria": "Música",
  "imagenCaratula": "https://...",
  
  "funciones": [
    {
      "id": 1,
      "fecha": "2025-11-20",
      "horario": "18:00:00",
      "dia": "Miércoles 20 de Noviembre de 2025",
      "estaFinalizada": false
    }
  ],
  
  "ubicacion": {
    "direccionCompleta": "Parque Lleras, Calle 10 #40-50",
    "comunaBarrio": "El Poblado",
    "ciudad": "Medellín",
    "departamento": "Antioquia",
    "latitud": 6.2088,
    "longitud": -75.5712,
    "enlaceMapa": "https://www.google.com/maps/search/?api=1&query=...",
    "indicacionesAcceso": "Entrada por la calle 10"
  },
  
  "aforo": 500,
  "valorIngreso": "Gratuito",
  "esGratuito": true,
  
  "organizador": {
    "nombre": "Secretaría de Cultura",
    "email": "cultura@medellin.gov.co",
    "telefono": "3001234567",
    "sitioWeb": "https://cultura.medellin.gov.co"
  },
  
  "modalidad": "PRESENCIAL",
  "estadoEvento": "ACTIVO",
  "destacado": true,
  
  "imagenes": ["https://..."],
  "videos": [],
  "enlaces": [],
  
  "requisitos": "Mayores de 18 años",
  "recomendaciones": "Llegar 30 minutos antes",
  "informacionAdicional": "Parqueadero, WiFi, Accesibilidad",
  
  "fechaCreacion": "2025-10-01",
  "fechaActualizacion": "2025-11-01",
  
  "accionesAdmin": null
}
```

### Lógica de Negocio

#### Cálculo de Estado del Evento
```java
public String calcularEstadoEvento(Evento evento) {
    if (evento.getStatus() == EventoStatus.CANCELLED) {
        return "CANCELADO";
    }
    
    boolean tieneFuncionesVigentes = evento.getFunciones().stream()
        .anyMatch(f -> !estaFinalizada(f));
    
    return tieneFuncionesVigentes ? "ACTIVO" : "FINALIZADO";
}
```

#### Verificación de Función Finalizada
```java
private boolean estaFinalizada(Funcion funcion) {
    LocalDate hoy = LocalDate.now();
    LocalTime ahora = LocalTime.now();
    
    return funcion.getFecha().isBefore(hoy) ||
           (funcion.getFecha().isEqual(hoy) && 
            funcion.getHorario().isBefore(ahora));
}
```

#### Auto-generación de Enlace a Google Maps
```java
private String generarEnlaceMaps(Ubicacion ubicacion) {
    if (ubicacion.getEnlaceMapa() != null) {
        return ubicacion.getEnlaceMapa();
    }
    
    String query = URLEncoder.encode(
        ubicacion.getDireccionCompleta() + ", Medellín, Colombia",
        StandardCharsets.UTF_8
    );
    
    return "https://www.google.com/maps/search/?api=1&query=" + query;
}
```

### DTOs

**EventoDetalleDTO** (15 secciones):
```java
public record EventoDetalleDTO(
    // Información básica
    Long id,
    String titulo,
    String descripcion,
    String categoria,
    String imagenCaratula,
    
    // Funciones
    List<FuncionDTO> funciones,
    
    // Ubicación
    UbicacionDTO ubicacion,
    
    // Capacidad y precio
    Integer aforo,
    String valorIngreso,
    Boolean esGratuito,
    
    // Organizador
    OrganizadorDTO organizador,
    
    // Estado
    String modalidad,
    String estadoEvento,
    String mensajeEstado,
    Boolean destacado,
    
    // Material
    List<String> imagenes,
    List<String> videos,
    List<String> enlaces,
    
    // Información adicional
    String requisitos,
    String recomendaciones,
    String informacionAdicional,
    
    // Metadatos
    LocalDate fechaCreacion,
    LocalDate fechaActualizacion,
    
    // Acciones admin
    EventoAdminActionsDTO accionesAdmin
) {}
```

### Componentes Técnicos

**Service:**
```java
@Service
public class EventoService {
    
    public EventoDetalleDTO convertirAEventoDetalle(Evento evento) {
        return new EventoDetalleDTO(
            evento.getId(),
            evento.getTitulo(),
            // ... mapeo de todos los campos
            calcularEstadoEvento(evento),
            // ...
            null // accionesAdmin para usuarios públicos
        );
    }
}
```

### Características Destacadas

1. **Estados Dinámicos:**
   - ACTIVO: Tiene funciones futuras
   - FINALIZADO: Todas las funciones pasaron
   - CANCELADO: Cancelado por admin

2. **Formateo de Fechas:**
   - "Miércoles 20 de Noviembre de 2025"
   - Español automático

3. **Material Complementario:**
   - Múltiples imágenes
   - Videos embebidos
   - Enlaces externos

---

## 👨‍💼 Microservicio 3: Administración de Eventos

### Descripción
Microservicio protegido para gestión administrativa de eventos (cancelar, destacar).

### Responsabilidades
- Cancelación de eventos
- Gestión de destacados (destacar/quitar)
- Validación de límites de destacados
- Información de espacios disponibles
- Cálculo de permisos administrativos

### Endpoints

#### 1. Cancelar Evento
```http
POST /api/admin/eventos/{id}/cancelar
```

**Headers:**
```
Authorization: Bearer {token}
```

**Respuesta:**
```json
{
  "success": true,
  "mensaje": "Evento cancelado exitosamente",
  "eventoId": 1,
  "estadoActual": "CANCELADO"
}
```

**Validaciones:**
- ✅ Evento existe
- ✅ Evento no está ya cancelado
- ✅ Usuario tiene permisos de administrador

#### 2. Destacar/Quitar Destacado
```http
PUT /api/admin/eventos/{id}/destacar?destacar={true|false}
```

**Parámetros:**
- `id` (Path, Long): ID del evento
- `destacar` (Query, Boolean): true/false

**Respuesta:**
```json
{
  "success": true,
  "mensaje": "Evento destacado exitosamente",
  "eventoId": 1,
  "destacado": true,
  "cantidadDestacadosActuales": 3,
  "espaciosDisponibles": 0
}
```

**Validaciones:**
- ✅ Evento existe y está publicado
- ✅ Evento no está cancelado
- ✅ Si destacar=true: Máximo 3 eventos vigentes
- ✅ Vigencia: Evento tiene al menos 1 función futura

**Errores:**
```json
{
  "success": false,
  "error": "Ya existen 3 eventos destacados con fechas vigentes. Debe quitar el destacado de otro evento primero."
}
```

#### 3. Información de Destacados
```http
GET /api/admin/eventos/destacados/info
```

**Respuesta:**
```json
{
  "cantidadDestacados": 2,
  "limiteMaximo": 3,
  "espaciosDisponibles": 1,
  "puedeDestacarMas": true
}
```

#### 4. Validar si Puede Destacar
```http
GET /api/admin/eventos/{id}/puede-destacar
```

**Respuesta (SÍ puede):**
```json
{
  "puedeDestacar": true,
  "cantidadDestacados": 2,
  "espaciosDisponibles": 1
}
```

**Respuesta (NO puede - límite):**
```json
{
  "puedeDestacar": false,
  "razon": "Ya existen 3 eventos destacados con fechas vigentes",
  "cantidadDestacados": 3,
  "limiteMaximo": 3
}
```

**Respuesta (ya destacado):**
```json
{
  "puedeDestacar": false,
  "razon": "Este evento ya está destacado",
  "accionDisponible": "QUITAR_DESTACADO"
}
```

### Lógica de Negocio

#### Sistema de Destacados Inteligente

**Regla:** Máximo 3 eventos destacados con funciones vigentes (futuras).

**Implementación SQL:**
```sql
SELECT COUNT(DISTINCT e.id) 
FROM eventos e 
JOIN funciones f ON f.evento_id = e.id
WHERE e.destacado = true 
  AND e.status = 'PUBLISHED'
  AND (f.fecha > CURRENT_DATE 
       OR (f.fecha = CURRENT_DATE 
           AND f.horario > CURRENT_TIME))
```

**Ventajas:**
- ✅ No requiere cron jobs
- ✅ Cálculo en tiempo real
- ✅ Expiración automática
- ✅ Precisión al segundo

#### Validación de Destacados
```java
@Service
public class EventoService {
    
    public void validarPuedeDestacar(Evento evento) {
        if (evento.getStatus() != EventoStatus.PUBLISHED) {
            throw new IllegalStateException("Solo eventos publicados pueden destacarse");
        }
        
        if (evento.getDestacado()) {
            throw new IllegalStateException("Este evento ya está destacado");
        }
        
        Long countVigentes = eventoRepository.countDestacadosVigentes();
        if (countVigentes >= 3) {
            throw new IllegalStateException(
                "Ya existen 3 eventos destacados con fechas vigentes. " +
                "Debe quitar el destacado de otro evento primero."
            );
        }
        
        // Verificar que tiene funciones vigentes
        boolean tieneFuncionesVigentes = evento.getFunciones().stream()
            .anyMatch(f -> !estaFinalizada(f));
            
        if (!tieneFuncionesVigentes) {
            throw new IllegalStateException(
                "El evento no tiene funciones vigentes"
            );
        }
    }
}
```

### DTOs

**EventoAdminActionsDTO** (Permisos):
```java
public record EventoAdminActionsDTO(
    Boolean puedeEditar,
    Boolean puedeCancelar,
    Boolean puedeDestacar,
    Boolean puedeQuitarDestacado,
    String razonNoDestacar,
    Long cantidadDestacados,
    Integer espaciosDisponibles,
    Boolean estaDestacado,
    Boolean estaCancelado
) {
    // Métodos helper
    public static EventoAdminActionsDTO todasHabilitadas() {
        return new EventoAdminActionsDTO(
            true, true, true, false, null, 0L, 3, false, false
        );
    }
    
    public static EventoAdminActionsDTO eventoCancelado() {
        return new EventoAdminActionsDTO(
            false, false, false, false, 
            "El evento está cancelado", 0L, 0, false, true
        );
    }
}
```

### Componentes Técnicos

**Controller:**
```java
@RestController
@RequestMapping("/api/admin/eventos")
// @PreAuthorize("hasRole('ADMIN')") // Preparado para Spring Security
@CrossOrigin(origins = "*")
public class EventoAdminController {
    
    @PostMapping("/{id}/cancelar")
    public ResponseEntity<?> cancelarEvento(@PathVariable Long id) {
        // Implementación
    }
    
    @PutMapping("/{id}/destacar")
    public ResponseEntity<?> destacarEvento(
        @PathVariable Long id,
        @RequestParam Boolean destacar
    ) {
        // Implementación con validaciones
    }
}
```

### Características Destacadas

1. **Seguridad Preparada:**
   - Annotations Spring Security comentadas
   - Listas para activar cuando se configure autenticación

2. **Validaciones Robustas:**
   - Verificación de límites
   - Verificación de vigencia
   - Mensajes de error descriptivos

3. **Sistema Inteligente:**
   - Expiración automática de destacados
   - No requiere jobs programados
   - Cálculo en tiempo real

---

## 🏗️ Arquitectura General

### Capas de la Aplicación

```
┌─────────────────────────────────────────┐
│         CAPA DE CONTROLADORES           │
│  (REST API Endpoints)                   │
│  - EventoPublicController (5 endpoints) │
│  - EventoAdminController (4 endpoints)  │
└─────────────────┬───────────────────────┘
                  │
┌─────────────────▼───────────────────────┐
│         CAPA DE SERVICIOS               │
│  (Lógica de Negocio)                    │
│  - EventoService                        │
│  - Conversión de DTOs                   │
│  - Cálculo de estados                   │
│  - Validaciones de negocio              │
└─────────────────┬───────────────────────┘
                  │
┌─────────────────▼───────────────────────┐
│      CAPA DE ESPECIFICACIONES           │
│  (Queries Dinámicas)                    │
│  - EventoSpecification                  │
│  - 13 filtros de búsqueda               │
└─────────────────┬───────────────────────┘
                  │
┌─────────────────▼───────────────────────┐
│       CAPA DE REPOSITORIOS              │
│  (Acceso a Datos)                       │
│  - EventoRepository                     │
│  - Queries personalizadas               │
│  - JPA + PostgreSQL                     │
└─────────────────┬───────────────────────┘
                  │
┌─────────────────▼───────────────────────┐
│         CAPA DE MODELO                  │
│  (Entidades JPA)                        │
│  - Evento                               │
│  - Funcion                              │
│  - Ubicacion (Embeddable)               │
│  - Organizador (Embeddable)             │
└─────────────────────────────────────────┘
```

### Comunicación entre Microservicios

```
┌─────────────────────────────────────────────────────────────────┐
│                    MÓDULO MONOLÍTICO MODULAR                     │
│                                                                   │
│  ┌─────────────────┐    ┌─────────────────┐    ┌─────────────┐│
│  │  Microservicio  │    │  Microservicio  │    │Microservicio││
│  │    Búsqueda     │───▶│    Gestión      │◀───│    Admin    ││
│  │                 │    │                 │    │             ││
│  │  5 endpoints    │    │  1 endpoint     │    │ 4 endpoints ││
│  │  públicos       │    │  público        │    │ protegidos  ││
│  └─────────────────┘    └─────────────────┘    └─────────────┘│
│           │                      │                      │        │
│           └──────────────────────┼──────────────────────┘        │
│                                  │                                │
│                    ┌─────────────▼──────────────┐               │
│                    │      EventoService          │               │
│                    │    (Shared Service)         │               │
│                    └─────────────────────────────┘               │
│                                  │                                │
│                    ┌─────────────▼──────────────┐               │
│                    │    EventoRepository         │               │
│                    │    (Shared Repository)      │               │
│                    └─────────────────────────────┘               │
└───────────────────────────────────────────────────────────────────┘
```

**Nota:** Los 3 microservicios están implementados en un **monolito modular** que puede escalarse a microservicios independientes en el futuro.

---

## 📊 Métricas de los Microservicios

| Microservicio | Endpoints | Líneas de Código | DTOs | Complejidad |
|---------------|-----------|------------------|------|-------------|
| Búsqueda | 5 | ~450 | 2 | Media |
| Gestión | 1 | ~200 | 1 | Baja |
| Admin | 4 | ~370 | 1 | Alta |
| **Total** | **10** | **~1020** | **4** | - |

---

## 🔒 Seguridad

### Endpoints Públicos
- ✅ Sin autenticación
- ✅ Rate limiting recomendado
- ✅ CORS configurado

### Endpoints Admin
- 🔒 Preparados para Spring Security
- 🔒 Requieren JWT token (cuando se implemente)
- 🔒 Rol: ADMINISTRADOR

---

## 📖 Documentación API

**Swagger UI:** `http://localhost:8081/swagger-ui/index.html`

**OpenAPI JSON:** `http://localhost:8081/v3/api-docs`

**Actuator Health:** `http://localhost:8081/actuator/health`

---

## ✅ Resumen

**3 Microservicios REST implementados:**
1. ✅ **Búsqueda y Consulta** - 5 endpoints públicos
2. ✅ **Gestión de Eventos** - 1 endpoint público
3. ✅ **Administración** - 4 endpoints protegidos

**Características:**
- ✅ 10 endpoints REST totales
- ✅ Arquitectura modular y escalable
- ✅ Lógica de negocio compleja (destacados inteligentes)
- ✅ DTOs especializados por caso de uso
- ✅ Documentación completa con Swagger
- ✅ Preparado para autenticación/autorización

**Estado:** ✅ **3 Microservicios REST implementados y documentados**
