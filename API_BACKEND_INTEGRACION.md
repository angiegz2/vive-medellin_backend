# 🔌 API REST - ViveMedellín Backend

## 📋 Documentación Completa del Backend

Este documento contiene **toda la información necesaria** para que cualquier equipo de frontend pueda integrarse con el backend de ViveMedellín.

---

## 🎯 Resumen del Backend

### Tecnologías
- **Framework:** Spring Boot 3.5.6
- **Java:** 21
- **Base de Datos:** PostgreSQL 18
- **ORM:** JPA/Hibernate
- **Documentación API:** Swagger/OpenAPI 3.0
- **Puerto:** 8081

### URL Base
```
http://localhost:8081
```

### Swagger UI
```
http://localhost:8081/swagger-ui/index.html
```

---

## 📌 ENDPOINTS PÚBLICOS (Sin Autenticación)

### 1. Búsqueda Avanzada de Eventos

```http
GET /api/public/eventos/buscar
```

**Parámetros Query (todos opcionales):**

| Parámetro | Tipo | Descripción | Ejemplo |
|-----------|------|-------------|---------|
| `texto` | String | Busca en título, descripción y organizador | `concierto` |
| `ubicacion` | String | Comuna o barrio | `El Poblado` |
| `categoria` | String | Categoría del evento | `Música` |
| `fechaDesde` | LocalDate | Fecha desde (YYYY-MM-DD) | `2025-10-15` |
| `fechaHasta` | LocalDate | Fecha hasta (YYYY-MM-DD) | `2025-12-31` |
| `gratuito` | Boolean | Solo eventos gratuitos | `true` |
| `modalidad` | String | PRESENCIAL, VIRTUAL, HIBRIDA | `PRESENCIAL` |
| `organizador` | String | Nombre del organizador | `Alcaldía` |
| `precioMinimo` | Double | Precio mínimo | `0` |
| `precioMaximo` | Double | Precio máximo | `50000` |
| `horario` | String | DIURNO (6-18h) o NOCTURNO (18-6h) | `NOCTURNO` |
| `servicio` | String | Servicio adicional | `Parqueadero` |
| `disponible` | Boolean | Solo eventos disponibles | `true` |
| `tipoVista` | String | MOSAICO (20 por página) o LISTA (50 por página) | `MOSAICO` |
| `page` | Integer | Número de página (empieza en 0) | `0` |
| `size` | Integer | Resultados por página (sobrescribe tipoVista) | `20` |
| `ordenarPor` | String | Campo para ordenar | `fecha` |
| `direccion` | String | ASC o DESC | `ASC` |

**Respuesta (200 OK):**

```json
{
  "content": [
    {
      "id": 1,
      "imagenCaratula": "https://...",
      "titulo": "Festival de Jazz",
      "categoria": "Música",
      "fechaEvento": "2025-10-20",
      "horaEvento": "18:00:00",
      "ubicacion": "El Poblado",
      "direccionCompleta": "Parque Lleras, Calle 10",
      "nombreOrganizador": "Secretaría de Cultura",
      "valorIngreso": "Gratuito",
      "destacado": true,
      "modalidad": "PRESENCIAL",
      "disponible": true
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 20,
    "sort": {
      "sorted": true,
      "unsorted": false,
      "empty": false
    },
    "offset": 0,
    "paged": true,
    "unpaged": false
  },
  "totalPages": 5,
  "totalElements": 95,
  "last": false,
  "first": true,
  "size": 20,
  "number": 0,
  "numberOfElements": 20,
  "empty": false
}
```

**Ejemplo de Uso:**
```bash
curl "http://localhost:8081/api/public/eventos/buscar?texto=musica&ubicacion=Poblado&tipoVista=MOSAICO&page=0"
```

---

### 2. Búsqueda Simple

```http
GET /api/public/eventos/buscar-simple?q={texto}
```

**Parámetros Query:**
- `q` (String, requerido): Texto a buscar

**Descripción:** Búsqueda rápida en título, descripción y organizador. Ignora acentos.

**Respuesta:** Igual que búsqueda avanzada pero solo con eventos que coincidan con el texto.

**Ejemplo:**
```bash
curl "http://localhost:8081/api/public/eventos/buscar-simple?q=concierto"
```

---

### 3. Detalle Completo de Evento

```http
GET /api/public/eventos/{id}
```

**Parámetros Path:**
- `id` (Long, requerido): ID del evento

**Respuesta (200 OK):**

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
      "fecha": "2025-10-20",
      "horario": "18:00:00",
      "dia": "Domingo 20 de Octubre de 2025",
      "estaFinalizada": false
    },
    {
      "id": 2,
      "fecha": "2025-10-21",
      "horario": "19:00:00",
      "dia": "Lunes 21 de Octubre de 2025",
      "estaFinalizada": false
    }
  ],
  "ubicacion": {
    "direccionCompleta": "Parque Lleras, Calle 10 #40-50",
    "comunaBarrio": "El Poblado",
    "ciudad": "Medellín",
    "departamento": "Antioquia",
    "latitud": null,
    "longitud": null,
    "enlaceMapa": "https://www.google.com/maps/search/?api=1&query=Parque+Lleras...",
    "indicacionesAcceso": "Entrada por la calle 10"
  },
  "aforo": 500,
  "valorIngreso": "Gratuito",
  "esGratuito": true,
  "organizador": {
    "id": null,
    "nombre": "Secretaría de Cultura",
    "email": "cultura@medellin.gov.co",
    "telefono": "3001234567",
    "sitioWeb": null,
    "descripcion": null,
    "logoUrl": null
  },
  "modalidad": "PRESENCIAL",
  "estadoEvento": "ACTIVO",
  "mensajeEstado": null,
  "destacado": true,
  "imagenes": [
    "https://..."
  ],
  "videos": [],
  "enlaces": [],
  "requisitos": null,
  "recomendaciones": null,
  "informacionAdicional": "Parqueadero, WiFi, Accesibilidad",
  "fechaCreacion": "2025-10-01",
  "fechaActualizacion": "2025-10-10",
  "accionesAdmin": null
}
```

**Campos Especiales:**
- `estadoEvento`: Puede ser `"ACTIVO"`, `"CANCELADO"` o `"FINALIZADO"` (calculado dinámicamente)
- `estaFinalizada` (en funciones): `true` si la función ya pasó
- `enlaceMapa`: URL a Google Maps (auto-generado si no existe)
- `dia`: Fecha formateada en español
- `accionesAdmin`: `null` para usuarios públicos, objeto para administradores

**Ejemplo:**
```bash
curl "http://localhost:8081/api/public/eventos/1"
```

---

### 4. Próximos Eventos

```http
GET /api/public/eventos/proximos?dias={dias}
```

**Parámetros Query:**
- `dias` (Integer, opcional, default=30): Número de días hacia adelante

**Descripción:** Eventos con fecha >= hoy, ordenados por fecha ascendente.

**Respuesta:** Lista de eventos en formato mosaico (igual que búsqueda).

**Ejemplo:**
```bash
curl "http://localhost:8081/api/public/eventos/proximos?dias=15"
```

---

### 5. Carrusel de Eventos Destacados ⭐

```http
GET /api/public/eventos/destacados-carrusel
```

**Descripción:** Devuelve hasta 3 eventos destacados que tengan al menos una función con fecha y horario vigentes.

**Características:**
- Máximo 3 eventos simultáneamente
- Solo eventos PUBLISHED
- Solo eventos con funciones futuras (fecha >= hoy)
- Ordenados por fecha de actualización (más recientes primero)
- Campo `destacado: true` en todos los eventos

**Respuesta (200 OK):**

```json
{
  "eventos": [
    {
      "id": 1,
      "imagenCaratula": "https://...",
      "titulo": "Festival de Jazz",
      "categoria": "Música",
      "fechaEvento": "2025-10-20",
      "horaEvento": "18:00:00",
      "ubicacion": "El Poblado",
      "direccionCompleta": "Parque Lleras",
      "nombreOrganizador": "Secretaría de Cultura",
      "valorIngreso": "Gratuito",
      "destacado": true,
      "modalidad": "PRESENCIAL",
      "disponible": true
    },
    {
      "id": 5,
      "titulo": "Obra de Teatro",
      "destacado": true,
      ...
    }
  ],
  "cantidad": 2,
  "mensaje": "Eventos destacados cargados exitosamente"
}
```

**Respuesta cuando NO hay destacados (200 OK):**
```json
{
  "eventos": [],
  "cantidad": 0,
  "mensaje": "No hay eventos destacados vigentes en este momento"
}
```

**Ejemplo:**
```bash
curl "http://localhost:8081/api/public/eventos/destacados-carrusel"
```

**Uso recomendado:** 
- Mostrar en carrusel/slider en la página principal
- Actualizar cada 5-10 minutos
- Renderizar badge "DESTACADO" si `destacado: true`

---

## 🔐 ENDPOINTS DE ADMINISTRADOR (Requieren Autenticación)

**Nota:** Estos endpoints están preparados para requerir autenticación con Spring Security (rol ADMINISTRADOR).

### Headers Requeridos:
```http
Authorization: Bearer {token}
Content-Type: application/json
```

---

### 6. Cancelar Evento

```http
POST /api/admin/eventos/{id}/cancelar
```

**Parámetros Path:**
- `id` (Long, requerido): ID del evento a cancelar

**Descripción:** Marca un evento como CANCELADO. Es irreversible.

**Respuesta (200 OK):**
```json
{
  "success": true,
  "mensaje": "Evento cancelado exitosamente",
  "eventoId": 1,
  "estadoActual": "CANCELADO"
}
```

**Errores:**
- `404`: Evento no encontrado
- `403`: Sin permisos de administrador
- `409`: El evento ya está cancelado

**Ejemplo:**
```bash
curl -X POST "http://localhost:8081/api/admin/eventos/1/cancelar" \
  -H "Authorization: Bearer {token}"
```

---

### 7. Destacar/Quitar Destacado

```http
PUT /api/admin/eventos/{id}/destacar?destacar={true|false}
```

**Parámetros:**
- `id` (Path, Long, requerido): ID del evento
- `destacar` (Query, Boolean, requerido): `true` para destacar, `false` para quitar

**Validaciones:**
- Máximo 3 eventos destacados con funciones vigentes
- Solo eventos PUBLISHED pueden destacarse
- Eventos cancelados no pueden destacarse

**Respuesta (200 OK):**
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

**Errores:**
- `404`: Evento no encontrado
- `403`: Sin permisos de administrador
- `409`: Ya hay 3 eventos destacados vigentes (límite alcanzado)

**Ejemplo destacar:**
```bash
curl -X PUT "http://localhost:8081/api/admin/eventos/1/destacar?destacar=true" \
  -H "Authorization: Bearer {token}"
```

**Ejemplo quitar destacado:**
```bash
curl -X PUT "http://localhost:8081/api/admin/eventos/1/destacar?destacar=false" \
  -H "Authorization: Bearer {token}"
```

---

### 8. Información de Destacados

```http
GET /api/admin/eventos/destacados/info
```

**Descripción:** Obtiene información sobre espacios disponibles para destacar eventos.

**Respuesta (200 OK):**
```json
{
  "cantidadDestacados": 2,
  "limiteMaximo": 3,
  "espaciosDisponibles": 1,
  "puedeDestacarMas": true
}
```

**Ejemplo:**
```bash
curl "http://localhost:8081/api/admin/eventos/destacados/info" \
  -H "Authorization: Bearer {token}"
```

---

### 9. Validar si se Puede Destacar

```http
GET /api/admin/eventos/{id}/puede-destacar
```

**Parámetros Path:**
- `id` (Long, requerido): ID del evento

**Descripción:** Verifica si un evento específico puede ser destacado. Útil para habilitar/deshabilitar botones.

**Respuesta cuando SÍ puede (200 OK):**
```json
{
  "puedeDestacar": true,
  "cantidadDestacados": 2,
  "espaciosDisponibles": 1
}
```

**Respuesta cuando NO puede (200 OK):**
```json
{
  "puedeDestacar": false,
  "razon": "Ya existen 3 eventos destacados con fechas vigentes. Debe quitar el destacado de otro evento primero.",
  "cantidadDestacados": 3,
  "limiteMaximo": 3
}
```

**Respuesta cuando ya está destacado (200 OK):**
```json
{
  "puedeDestacar": false,
  "razon": "Este evento ya está destacado",
  "accionDisponible": "QUITAR_DESTACADO"
}
```

**Ejemplo:**
```bash
curl "http://localhost:8081/api/admin/eventos/5/puede-destacar" \
  -H "Authorization: Bearer {token}"
```

---

## 📊 DTOs (Data Transfer Objects)

### EventoMosaicoDTO
**Uso:** Vista de tarjetas/mosaico (20 por página)

```typescript
{
  id: number;
  imagenCaratula: string;
  titulo: string;
  categoria: string;
  fechaEvento: string; // "YYYY-MM-DD"
  horaEvento: string; // "HH:mm:ss"
  ubicacion: string; // Comuna/barrio
  direccionCompleta: string;
  nombreOrganizador: string;
  valorIngreso: string;
  destacado: boolean; // ⭐ Usar para mostrar badge
  modalidad: string; // "PRESENCIAL" | "VIRTUAL" | "HIBRIDA"
  disponible: boolean;
}
```

---

### EventoListaDTO
**Uso:** Vista de lista compacta (50 por página)

```typescript
{
  id: number;
  titulo: string;
  fechaEvento: string; // "YYYY-MM-DD"
  horaEvento: string; // "HH:mm:ss"
  ubicacion: string;
  direccionCompleta: string;
  nombreOrganizador: string;
  categoria: string;
  valorIngreso: string;
  destacado: boolean; // ⭐ Usar para mostrar badge
  disponible: boolean;
}
```

---

### EventoDetalleDTO
**Uso:** Página de detalle completo

```typescript
{
  // Información básica
  id: number;
  titulo: string;
  descripcion: string;
  categoria: string;
  imagenCaratula: string;
  
  // Funciones (fechas y horarios)
  funciones: Array<{
    id: number;
    fecha: string; // "YYYY-MM-DD"
    horario: string; // "HH:mm:ss"
    dia: string; // "Domingo 20 de Octubre de 2025"
    estaFinalizada: boolean;
  }>;
  
  // Ubicación
  ubicacion: {
    direccionCompleta: string;
    comunaBarrio: string;
    ciudad: string;
    departamento: string;
    latitud: number | null;
    longitud: number | null;
    enlaceMapa: string; // URL a Google Maps
    indicacionesAcceso: string;
  };
  
  // Capacidad y precio
  aforo: number;
  valorIngreso: string;
  esGratuito: boolean;
  
  // Organizador
  organizador: {
    id: number | null;
    nombre: string;
    email: string;
    telefono: string;
    sitioWeb: string | null;
    descripcion: string | null;
    logoUrl: string | null;
  };
  
  // Modalidad y estado
  modalidad: string; // "PRESENCIAL" | "VIRTUAL" | "HIBRIDA"
  estadoEvento: string; // "ACTIVO" | "CANCELADO" | "FINALIZADO"
  mensajeEstado: string | null;
  destacado: boolean;
  
  // Material complementario
  imagenes: string[];
  videos: string[];
  enlaces: string[];
  
  // Información complementaria
  requisitos: string | null;
  recomendaciones: string | null;
  informacionAdicional: string;
  
  // Metadatos
  fechaCreacion: string; // "YYYY-MM-DD"
  fechaActualizacion: string; // "YYYY-MM-DD"
  
  // Acciones admin (null si no es admin)
  accionesAdmin: EventoAdminActionsDTO | null;
}
```

---

### EventoAdminActionsDTO
**Uso:** Información de acciones disponibles para administradores

```typescript
{
  puedeEditar: boolean;
  puedeCancelar: boolean;
  puedeDestacar: boolean;
  puedeQuitarDestacado: boolean;
  razonNoDestacar: string | null;
  cantidadDestacados: number;
  espaciosDisponibles: number;
  estaDestacado: boolean;
  estaCancelado: boolean;
}
```

---

## 🎯 Reglas de Negocio

### 1. Búsqueda sin Acentos
- La búsqueda es **case-insensitive** e **ignora acentos**
- "música" = "musica" = "MÚSICA" = "MUSICA"
- Implementado con PostgreSQL `unaccent` extension

### 2. Sistema de Destacados
- **Límite:** Máximo 3 eventos destacados con funciones vigentes
- **Vigencia:** Un evento aparece en el carrusel solo si tiene al menos 1 función futura
- **Cálculo:** Se verifica fecha Y hora: `fecha > HOY OR (fecha = HOY AND hora > HORA_ACTUAL)`
- **Automático:** No requiere cron jobs, se calcula en tiempo real con query SQL

### 3. Estado de Eventos
El campo `estadoEvento` se calcula dinámicamente:
- `"ACTIVO"`: Evento publicado con al menos una función futura
- `"CANCELADO"`: status = CANCELLED en BD
- `"FINALIZADO"`: Todas las funciones ya pasaron

### 4. Google Maps
- Si el evento tiene dirección pero no `enlaceMapa`, se auto-genera:
  ```
  https://www.google.com/maps/search/?api=1&query={direccion},Medellín,Colombia
  ```

### 5. Formateo de Fechas
- Las fechas en `funciones[].dia` están formateadas en español:
  ```
  "Domingo 20 de Octubre de 2025"
  ```

---

## 🔍 Filtros Disponibles

### Por Texto
- **texto**: Busca en título, descripción y nombre del organizador
- **organizador**: Filtra por nombre del organizador específicamente

### Por Ubicación
- **ubicacion**: Filtra por comuna o barrio

### Por Categoría
- **categoria**: Valores posibles: "Música", "Teatro", "Arte", "Deporte", etc.

### Por Fecha
- **fechaDesde** / **fechaHasta**: Rango de fechas (YYYY-MM-DD)

### Por Precio
- **gratuito** (boolean): Solo eventos gratuitos
- **precioMinimo** / **precioMaximo** (double): Rango de precio

### Por Modalidad
- **modalidad**: "PRESENCIAL", "VIRTUAL", "HIBRIDA"

### Por Horario
- **horario**: 
  - "DIURNO": Eventos entre 6:00 y 18:00
  - "NOCTURNO": Eventos entre 18:00 y 6:00

### Por Servicios
- **servicio**: Filtra por servicio adicional (ej: "Parqueadero", "WiFi", "Accesibilidad")

### Por Disponibilidad
- **disponible** (boolean): Solo eventos disponibles (status=PUBLISHED y fecha futura)

---

## 📄 Paginación

### Respuesta Estándar
```json
{
  "content": [...],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 20,
    "offset": 0
  },
  "totalPages": 5,
  "totalElements": 95,
  "last": false,
  "first": true,
  "size": 20,
  "number": 0,
  "numberOfElements": 20,
  "empty": false
}
```

### Parámetros
- `page`: Número de página (empieza en 0)
- `size`: Resultados por página
- `tipoVista`: "MOSAICO" (20) o "LISTA" (50)

---

## ❌ Manejo de Errores

### Estructura de Error
```json
{
  "success": false,
  "error": "Mensaje descriptivo del error"
}
```

### Códigos HTTP
- `200 OK`: Operación exitosa
- `404 Not Found`: Recurso no encontrado
- `409 Conflict`: Conflicto de negocio (ej: límite de destacados)
- `403 Forbidden`: Sin permisos
- `500 Internal Server Error`: Error del servidor

---

## 🚀 Configuración del Backend

### Variables de Entorno
```properties
# Base de datos
spring.datasource.url=jdbc:postgresql://localhost:5432/vivemedellin
spring.datasource.username=postgres
spring.datasource.password=postgres

# Puerto
server.port=8081

# PostgreSQL unaccent (requerido para búsqueda sin acentos)
# Ejecutar: CREATE EXTENSION IF NOT EXISTS unaccent;
```

### Iniciar el Backend
```bash
# Con Maven
mvn spring-boot:run

# Con JAR
java -jar target/ViveMedellin-0.0.1-SNAPSHOT.jar

# Con Docker
docker-compose up
```

---

## 📝 Swagger/OpenAPI

### URL
```
http://localhost:8081/swagger-ui/index.html
```

### Secciones Disponibles
1. **Búsqueda Pública de Eventos** - 5 endpoints públicos
2. **Acciones de Administrador** - 4 endpoints admin

### Probar Endpoints
- Swagger UI permite probar todos los endpoints directamente
- No requiere autenticación para endpoints públicos
- Endpoints admin requieren agregar token en "Authorize"

---

## 🔄 CORS

El backend está configurado para aceptar peticiones desde cualquier origen en desarrollo.

**Configuración actual:**
```java
@CrossOrigin(origins = "*")
```

**Para producción, cambiar a:**
```java
@CrossOrigin(origins = "https://tu-dominio.com")
```

---

## 📊 Base de Datos

### Extensión Requerida
```sql
CREATE EXTENSION IF NOT EXISTS unaccent;
```

### Tablas Principales
- **eventos**: Información del evento
- **funciones**: Fechas y horarios múltiples por evento
- **ubicacion**: Embebido en eventos
- **organizador**: Embebido en eventos

---

## ✅ Checklist de Integración

### Para el Frontend
- [ ] Configurar URL base del backend
- [ ] Implementar llamadas a endpoints públicos
- [ ] Implementar paginación
- [ ] Mostrar badge "DESTACADO" cuando `destacado: true`
- [ ] Implementar carrusel con endpoint `/destacados-carrusel`
- [ ] Manejar estados de carga y errores
- [ ] Implementar autenticación para endpoints admin
- [ ] Implementar confirmación antes de cancelar
- [ ] Validar límite de destacados en UI

---

## 🎉 Resumen

**9 Endpoints Implementados:**
- 5 públicos (sin autenticación)
- 4 admin (requieren autenticación)

**Funcionalidades Clave:**
- ✅ Búsqueda avanzada con 13 filtros
- ✅ Búsqueda sin acentos
- ✅ Detalle completo de evento
- ✅ Carrusel de destacados (máx 3)
- ✅ Sistema de destacados con expiración automática
- ✅ Acciones administrativas (Editar, Cancelar, Destacar)

**Estado:** ✅ **Backend 100% Funcional y Listo para Integración**

---

**Documentación adicional:**
- Ver Swagger UI para detalles completos de cada endpoint
- Todos los endpoints están documentados con ejemplos
- DTOs tienen validaciones y documentación

**Contacto:** Revisar logs del servidor en caso de errores
