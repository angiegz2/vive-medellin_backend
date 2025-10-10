# Arquitectura ViveMedellin - Modelo C4

Esta carpeta contiene la documentación arquitectónica completa de la Plataforma ViveMedellin siguiendo el modelo C4 (Context, Containers, Components, Code) de Simon Brown.

## 🏗️ Estructura de la Documentación

```
architecture/
├── C4-Model-ViveMedellin.md          # Documentación completa con diagramas Mermaid
├── diagrams/
│   ├── 01-context-diagram.puml       # Nivel 1: Contexto del Sistema
│   ├── 02-container-diagram.puml     # Nivel 2: Contenedores
│   ├── 03-component-diagram.puml     # Nivel 3: Componentes Backend
│   ├── 04-domain-model.puml          # Nivel 4: Modelo de Dominio
│   └── README.md                     # Este archivo
└── exports/                          # Diagramas exportados (PNG/SVG)
```

## 📊 Niveles del Modelo C4

### 1. **Contexto del Sistema** 
- **Audiencia:** Stakeholders, usuarios finales
- **Propósito:** Muestra cómo ViveMedellin interactúa con usuarios y sistemas externos
- **Incluye:** Ciudadanos, organizadores, administradores, APIs externas

### 2. **Contenedores**
- **Audiencia:** Arquitectos de software, líderes técnicos
- **Propósito:** Descompone ViveMedellin en contenedores desplegables
- **Incluye:** Web App, Mobile App, Backend API, Base de datos, Cache

### 3. **Componentes**
- **Audiencia:** Desarrolladores senior, arquitectos
- **Propósito:** Muestra los componentes principales del Backend API
- **Incluye:** Controllers, Services, Repositories, Configuraciones

### 4. **Código (Modelo de Dominio)**
- **Audiencia:** Desarrolladores
- **Propósito:** Muestra las entidades principales y sus relaciones
- **Incluye:** Evento, Usuario, Valoración, Notificación, etc.

## 🛠️ Herramientas de Visualización

### Para Diagramas Mermaid (.md)
1. **VS Code:** Instalar extensión "Mermaid Preview"
2. **GitHub:** Visualización nativa en archivos .md
3. **Mermaid Live Editor:** https://mermaid.live/

### Para Diagramas PlantUML (.puml)
1. **VS Code:** Instalar extensión "PlantUML"
2. **IntelliJ IDEA:** Soporte nativo o plugin PlantUML
3. **Online:** http://www.plantuml.com/plantuml/

### Exportar Diagramas
```bash
# Usando PlantUML CLI
java -jar plantuml.jar -tpng diagrams/*.puml
java -jar plantuml.jar -tsvg diagrams/*.puml

# Usando VS Code
# Clic derecho en archivo .puml > "Export Current Diagram"
```

## 🚀 Tecnologías Implementadas

### Backend Stack
- **Framework:** Spring Boot 3.5.6
- **Java Version:** Java 21/23
- **Database:** PostgreSQL 18
- **Cache:** Redis
- **Documentation:** SpringDoc OpenAPI (Swagger)
- **Security:** Spring Security + JWT

### Arquitectura
- **Pattern:** Layered Architecture (Controller → Service → Repository → Entity)
- **ORM:** JPA/Hibernate
- **Connection Pool:** HikariCP
- **Profiles:** dev (H2), prod (PostgreSQL)

### API Features
- **Endpoints:** 15+ REST endpoints
- **Operations:** Full CRUD + special operations (highlight, soft delete)
- **Documentation:** Auto-generated with Swagger UI
- **Validation:** Bean Validation (JSR-303)

## 📋 Decisiones Arquitectónicas Clave

### 1. **Separación de Responsabilidades**
```
Controller → HTTP Request/Response handling only
Service    → Business logic and orchestration
Repository → Data access layer
Entity     → Domain model with JPA annotations
```

### 2. **Data Management**
- **Soft Delete:** Events are deactivated, not physically deleted
- **Audit Trail:** Automatic timestamps for creation/modification
- **Validation:** Multi-layer validation (bean validation + business rules)

### 3. **Scalability Considerations**
- **Cache Strategy:** Redis for frequent data and sessions  
- **Connection Pooling:** HikariCP with optimized settings
- **Database Indexing:** Strategic indexes for search operations

### 4. **Security Implementation**
- **Authentication:** JWT-based stateless authentication
- **Authorization:** Role-based access control (CIUDADANO, ORGANIZADOR, ADMIN)
- **CORS:** Configured for web and mobile clients

## 🔄 Flujos Principales

### Crear Evento
```
1. OrganizadorController.crearEvento()
2. EventoService.crear()
3. EventoRepository.save()
4. NotificationService.notificarNuevoEvento()
5. SearchService.indexarEvento()
```

### Buscar Eventos
```
1. EventoController.buscar()
2. EventoService.buscarConFiltros()
3. EventoRepository.findWithFilters()
4. CacheService.guardarResultado()
```

### Sistema de Valoraciones
```
1. ValoracionController.valorar()
2. ValoracionService.crear()
3. ValoracionRepository.save()
4. EventoService.actualizarPromedioValoracion()
```

## 📈 Métricas y KPIs

### Performance Targets
- **Response Time API:** < 200ms para operaciones CRUD
- **Database Query Time:** < 50ms para consultas simples
- **Cache Hit Ratio:** > 80% para datos frecuentes

### Business KPIs
- **Eventos Activos:** Número de eventos no cancelados
- **Participación:** Ratio de asistencia vs inscripciones
- **Satisfacción:** Promedio de valoraciones por evento

## 🔗 Referencias

- [C4 Model](https://c4model.com/) - Documentación oficial del modelo C4
- [Spring Boot Documentation](https://spring.io/projects/spring-boot) - Documentación de Spring Boot
- [PostgreSQL 18 Features](https://www.postgresql.org/docs/18/) - Características de PostgreSQL 18
- [Swagger/OpenAPI](https://swagger.io/) - Documentación de API

---

**Nota:** Esta documentación es un artefacto vivo que debe actualizarse con cada cambio arquitectónico significativo en la plataforma ViveMedellin.

*Última actualización: Septiembre 2025*