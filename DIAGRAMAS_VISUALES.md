# 🎨 Diagramas Visuales de Despliegue - ViveMedellín

Este documento contiene versiones visuales de los diagramas de despliegue usando **Mermaid** y **PlantUML**, que se renderizan automáticamente en GitHub, VSCode y otras herramientas.

---

## 📊 Nivel 1: Diagrama de Contexto del Sistema

```mermaid
graph TB
    subgraph "Sistema ViveMedellín"
        SISTEMA[ViveMedellín Backend<br/>Plataforma de Eventos Culturales]
    end
    
    CIUDADANO[👤 Usuario Ciudadano<br/>Consulta eventos<br/>Busca y filtra]
    ADMIN[👨‍💼 Administrador<br/>Gestiona eventos<br/>Administra destacados]
    
    CIUDADANO -->|Buscar eventos<br/>Consultar detalles<br/>Ver carrusel| SISTEMA
    ADMIN -->|Cancelar eventos<br/>Destacar eventos<br/>Administrar| SISTEMA
    
    style SISTEMA fill:#4CAF50,stroke:#2E7D32,stroke-width:3px,color:#fff
    style CIUDADANO fill:#2196F3,stroke:#1565C0,stroke-width:2px,color:#fff
    style ADMIN fill:#FF9800,stroke:#E65100,stroke-width:2px,color:#fff
```

---

## 🏢 Nivel 2: Diagrama de Contenedores

```mermaid
graph TB
    subgraph CLIENTE["🖥️ Cliente (Frontend)"]
        WEB[Navegador Web<br/>React/Vue/Angular]
        MOBILE[Aplicación Móvil<br/>React Native]
    end
    
    subgraph BACKEND["⚙️ Backend API (Contenedor)"]
        SPRING[Spring Boot 3.5.6<br/>Java 21<br/>Puerto: 8081<br/><br/>🔹 Microservicio Búsqueda<br/>🔹 Microservicio Gestión<br/>🔹 Microservicio Admin]
        SWAGGER[Swagger UI<br/>/swagger-ui]
        ACTUATOR[Actuator<br/>/actuator/health]
    end
    
    subgraph DATABASE["🗄️ Base de Datos"]
        POSTGRES[PostgreSQL 18<br/>Puerto: 5432<br/>Extension: unaccent<br/>Volumen: postgres-data]
    end
    
    subgraph DOCKER["🐳 Docker"]
        COMPOSE[Docker Compose<br/>Orquestación]
    end
    
    WEB -->|HTTPS REST/JSON| SPRING
    MOBILE -->|HTTPS REST/JSON| SPRING
    SPRING -->|JPA/Hibernate| POSTGRES
    COMPOSE -.->|Gestiona| SPRING
    COMPOSE -.->|Gestiona| POSTGRES
    
    style CLIENTE fill:#2196F3,stroke:#1565C0,stroke-width:2px,color:#fff
    style BACKEND fill:#4CAF50,stroke:#2E7D32,stroke-width:2px,color:#fff
    style DATABASE fill:#FF9800,stroke:#E65100,stroke-width:2px,color:#fff
    style DOCKER fill:#00BCD4,stroke:#006064,stroke-width:2px,color:#fff
    style SPRING fill:#4CAF50,stroke:#2E7D32,stroke-width:2px,color:#fff
    style POSTGRES fill:#FF9800,stroke:#E65100,stroke-width:2px,color:#fff
```

---

## 🔧 Nivel 3: Diagrama de Componentes (Backend)

```mermaid
graph TB
    subgraph CONTROLLERS["📡 Capa de Controladores"]
        PUBLIC_CTRL[EventoPublicController<br/>5 endpoints públicos]
        ADMIN_CTRL[EventoAdminController<br/>4 endpoints admin]
        ACTUATOR_CTRL[Actuator Endpoints<br/>Health, Metrics]
    end
    
    subgraph SERVICES["🔄 Capa de Servicios"]
        EVENTO_SVC[EventoService<br/>• Lógica de negocio<br/>• Conversión DTOs<br/>• Cálculo estados<br/>• Validaciones]
    end
    
    subgraph SPECS["🔍 Capa de Especificaciones"]
        EVENTO_SPEC[EventoSpecification<br/>• 13 filtros dinámicos<br/>• JPA Criteria API<br/>• Búsqueda sin acentos]
    end
    
    subgraph REPOS["💾 Capa de Repositorios"]
        EVENTO_REPO[EventoRepository<br/>• JpaRepository<br/>• Queries personalizadas<br/>• countDestacadosVigentes<br/>• findDestacadosVigentes]
    end
    
    subgraph MODELS["📦 Capa de Modelo"]
        EVENTO[Evento<br/>@Entity]
        FUNCION[Funcion<br/>@Entity]
        UBICACION[Ubicacion<br/>@Embeddable]
        ORGANIZADOR[Organizador<br/>@Embeddable]
    end
    
    subgraph CONFIG["⚙️ Capa de Configuración"]
        OPENAPI[OpenAPI Config<br/>Swagger]
        CORS[CORS Config]
        JPA[JPA Properties]
    end
    
    PUBLIC_CTRL --> EVENTO_SVC
    ADMIN_CTRL --> EVENTO_SVC
    EVENTO_SVC --> EVENTO_SPEC
    EVENTO_SVC --> EVENTO_REPO
    EVENTO_SPEC --> EVENTO_REPO
    EVENTO_REPO --> EVENTO
    EVENTO_REPO --> FUNCION
    EVENTO --> UBICACION
    EVENTO --> ORGANIZADOR
    
    style CONTROLLERS fill:#2196F3,stroke:#1565C0,stroke-width:2px,color:#fff
    style SERVICES fill:#4CAF50,stroke:#2E7D32,stroke-width:2px,color:#fff
    style SPECS fill:#9C27B0,stroke:#6A1B9A,stroke-width:2px,color:#fff
    style REPOS fill:#FF9800,stroke:#E65100,stroke-width:2px,color:#fff
    style MODELS fill:#F44336,stroke:#C62828,stroke-width:2px,color:#fff
    style CONFIG fill:#607D8B,stroke:#37474F,stroke-width:2px,color:#fff
```

---

## 🚀 Nivel 4: Diagrama de Despliegue - Desarrollo Local

```mermaid
graph TB
    subgraph LOCAL["💻 Máquina de Desarrollo"]
        subgraph DOCKER_DESKTOP["🐳 Docker Desktop"]
            subgraph NETWORK["🌐 vivemedellin_network"]
                BACKEND_CONTAINER[🔷 Contenedor: backend<br/>Imagen: vivemedellin:latest<br/>Puerto: 8081<br/>JDK 21 + Spring Boot]
                POSTGRES_CONTAINER[🔶 Contenedor: postgres<br/>Imagen: postgres:18<br/>Puerto: 5432<br/>Volumen: postgres-data]
            end
        end
        
        LOCALHOST[localhost:8081<br/>• API REST<br/>• Swagger UI<br/>• Actuator]
    end
    
    DEV[👨‍💻 Desarrollador<br/>Browser/Postman]
    
    DEV -->|http://localhost:8081| LOCALHOST
    LOCALHOST --> BACKEND_CONTAINER
    BACKEND_CONTAINER -->|JDBC| POSTGRES_CONTAINER
    
    style LOCAL fill:#E3F2FD,stroke:#1565C0,stroke-width:2px
    style DOCKER_DESKTOP fill:#B3E5FC,stroke:#0277BD,stroke-width:2px
    style NETWORK fill:#81D4FA,stroke:#01579B,stroke-width:2px
    style BACKEND_CONTAINER fill:#4CAF50,stroke:#2E7D32,stroke-width:2px,color:#fff
    style POSTGRES_CONTAINER fill:#FF9800,stroke:#E65100,stroke-width:2px,color:#fff
    style LOCALHOST fill:#2196F3,stroke:#1565C0,stroke-width:2px,color:#fff
    style DEV fill:#9C27B0,stroke:#6A1B9A,stroke-width:2px,color:#fff
```

---

## ☁️ Nivel 4: Diagrama de Despliegue - Producción Cloud

```mermaid
graph TB
    subgraph CLOUD["☁️ Infraestructura Cloud (AWS/Azure/GCP)"]
        subgraph LB_LAYER["🌐 Capa de Red"]
            LOAD_BALANCER[Load Balancer<br/>HTTPS/SSL<br/>Health Checks<br/>API Gateway]
        end
        
        subgraph COMPUTE["🖥️ Clúster de Contenedores (K8s/ECS)"]
            POD1[⚙️ Backend Instance 1<br/>Auto-scaling<br/>Health checks]
            POD2[⚙️ Backend Instance 2<br/>Auto-scaling<br/>Health checks]
            POD3[⚙️ Backend Instance 3<br/>Auto-scaling<br/>Health checks]
        end
        
        subgraph DATABASE_LAYER["🗄️ Capa de Datos"]
            RDS[Managed Database<br/>PostgreSQL 18<br/>Multi-AZ<br/>Backups automáticos<br/>Réplicas de lectura]
        end
        
        subgraph SERVICES["🔧 Servicios Adicionales"]
            REDIS[Redis Cache<br/>Session/Cache]
            MONITORING[CloudWatch/Grafana<br/>Monitoring & Logs]
            SECRETS[Secrets Manager<br/>Credenciales]
        end
    end
    
    INTERNET[🌍 Internet<br/>Usuarios]
    
    INTERNET -->|HTTPS| LOAD_BALANCER
    LOAD_BALANCER --> POD1
    LOAD_BALANCER --> POD2
    LOAD_BALANCER --> POD3
    POD1 -->|Pool JDBC| RDS
    POD2 -->|Pool JDBC| RDS
    POD3 -->|Pool JDBC| RDS
    POD1 -.->|Cache| REDIS
    POD2 -.->|Cache| REDIS
    POD3 -.->|Cache| REDIS
    POD1 -.->|Logs/Metrics| MONITORING
    POD2 -.->|Logs/Metrics| MONITORING
    POD3 -.->|Logs/Metrics| MONITORING
    POD1 -.->|Secretos| SECRETS
    POD2 -.->|Secretos| SECRETS
    POD3 -.->|Secretos| SECRETS
    
    style CLOUD fill:#E8F5E9,stroke:#2E7D32,stroke-width:3px
    style LB_LAYER fill:#C8E6C9,stroke:#388E3C,stroke-width:2px
    style COMPUTE fill:#A5D6A7,stroke:#43A047,stroke-width:2px
    style DATABASE_LAYER fill:#FFE0B2,stroke:#EF6C00,stroke-width:2px
    style SERVICES fill:#B3E5FC,stroke:#0277BD,stroke-width:2px
    style LOAD_BALANCER fill:#2196F3,stroke:#1565C0,stroke-width:2px,color:#fff
    style POD1 fill:#4CAF50,stroke:#2E7D32,stroke-width:2px,color:#fff
    style POD2 fill:#4CAF50,stroke:#2E7D32,stroke-width:2px,color:#fff
    style POD3 fill:#4CAF50,stroke:#2E7D32,stroke-width:2px,color:#fff
    style RDS fill:#FF9800,stroke:#E65100,stroke-width:2px,color:#fff
    style INTERNET fill:#9C27B0,stroke:#6A1B9A,stroke-width:2px,color:#fff
```

---

## 🔄 Pipeline CI/CD Completo

```mermaid
graph TB
    START([🚀 Push/PR a main/develop])
    
    subgraph STAGE1["📦 Stage 1: Build & Test"]
        CHECKOUT[Checkout Code<br/>Git Clone]
        SETUP_JAVA[Setup JDK 21<br/>Cache Maven]
        BUILD[Maven Build<br/>mvn compile]
        TEST[Run Tests<br/>mvn test<br/>PostgreSQL Container]
        UPLOAD_TESTS[Upload Test Results]
    end
    
    subgraph STAGE2["🔒 Stage 2: Security"]
        OWASP[OWASP Dependency Check<br/>CVE Scanner]
        TRIVY[Trivy Vulnerability Scan<br/>Filesystem]
        UPLOAD_SARIF[Upload to GitHub Security]
    end
    
    subgraph STAGE3["📊 Stage 3: Quality"]
        SONAR[SonarCloud Analysis<br/>Code Quality]
    end
    
    subgraph STAGE4["🐳 Stage 4: Docker"]
        DOCKER_BUILD[Build Docker Image<br/>Multi-stage]
        DOCKER_TAG[Tag Image<br/>latest, version, SHA]
        DOCKER_PUSH[Push to Registry<br/>Docker Hub + GHCR]
        TRIVY_IMAGE[Trivy Image Scan]
    end
    
    subgraph STAGE5["🚀 Stage 5: Deploy"]
        DEPLOY_STAGING{Branch = develop?}
        DEPLOY_PROD{Branch = main?}
        STAGING[Deploy to Staging<br/>Health Check]
        PRODUCTION[Deploy to Production<br/>Health Check]
    end
    
    NOTIFY[📧 Notifications<br/>Slack/Email<br/>GitHub Status]
    
    START --> CHECKOUT
    CHECKOUT --> SETUP_JAVA
    SETUP_JAVA --> BUILD
    BUILD --> TEST
    TEST --> UPLOAD_TESTS
    
    UPLOAD_TESTS --> OWASP
    OWASP --> TRIVY
    TRIVY --> UPLOAD_SARIF
    
    UPLOAD_SARIF --> SONAR
    
    SONAR --> DOCKER_BUILD
    DOCKER_BUILD --> DOCKER_TAG
    DOCKER_TAG --> DOCKER_PUSH
    DOCKER_PUSH --> TRIVY_IMAGE
    
    TRIVY_IMAGE --> DEPLOY_STAGING
    DEPLOY_STAGING -->|Yes| STAGING
    DEPLOY_STAGING -->|No| DEPLOY_PROD
    DEPLOY_PROD -->|Yes| PRODUCTION
    DEPLOY_PROD -->|No| NOTIFY
    
    STAGING --> NOTIFY
    PRODUCTION --> NOTIFY
    
    style START fill:#9C27B0,stroke:#6A1B9A,stroke-width:2px,color:#fff
    style STAGE1 fill:#E3F2FD,stroke:#1565C0,stroke-width:2px
    style STAGE2 fill:#FFEBEE,stroke:#C62828,stroke-width:2px
    style STAGE3 fill:#FFF3E0,stroke:#EF6C00,stroke-width:2px
    style STAGE4 fill:#E0F2F1,stroke:#00695C,stroke-width:2px
    style STAGE5 fill:#F3E5F5,stroke:#6A1B9A,stroke-width:2px
    style NOTIFY fill:#4CAF50,stroke:#2E7D32,stroke-width:2px,color:#fff
```

---

## 🏗️ Arquitectura de Microservicios

```mermaid
graph LR
    subgraph FRONTEND["🖥️ Cliente"]
        CLIENT[Frontend App<br/>React/Vue/Angular]
    end
    
    subgraph BACKEND["⚙️ Backend Monolito Modular"]
        subgraph MS1["🔍 Microservicio 1: Búsqueda"]
            SEARCH_API[API Búsqueda<br/>5 endpoints<br/>• /buscar<br/>• /buscar-simple<br/>• /proximos<br/>• /destacados-carrusel]
        end
        
        subgraph MS2["📋 Microservicio 2: Gestión"]
            MANAGE_API[API Gestión<br/>1 endpoint<br/>• /eventos/:id<br/>Detalle completo]
        end
        
        subgraph MS3["👨‍💼 Microservicio 3: Admin"]
            ADMIN_API[API Admin<br/>4 endpoints<br/>• /cancelar<br/>• /destacar<br/>• /info<br/>• /puede-destacar]
        end
        
        subgraph SHARED["🔄 Servicios Compartidos"]
            SERVICE[EventoService]
            REPO[EventoRepository]
            SPEC[EventoSpecification]
        end
    end
    
    subgraph DB["🗄️ Base de Datos"]
        POSTGRES[(PostgreSQL 18<br/>• eventos<br/>• funciones<br/>• ubicacion<br/>• organizador)]
    end
    
    CLIENT -->|REST/JSON| SEARCH_API
    CLIENT -->|REST/JSON| MANAGE_API
    CLIENT -->|REST/JSON| ADMIN_API
    
    SEARCH_API --> SERVICE
    MANAGE_API --> SERVICE
    ADMIN_API --> SERVICE
    
    SERVICE --> REPO
    SERVICE --> SPEC
    SPEC --> REPO
    REPO --> POSTGRES
    
    style FRONTEND fill:#2196F3,stroke:#1565C0,stroke-width:2px,color:#fff
    style BACKEND fill:#E8F5E9,stroke:#2E7D32,stroke-width:2px
    style MS1 fill:#4CAF50,stroke:#2E7D32,stroke-width:2px,color:#fff
    style MS2 fill:#8BC34A,stroke:#558B2F,stroke-width:2px,color:#fff
    style MS3 fill:#FF9800,stroke:#E65100,stroke-width:2px,color:#fff
    style SHARED fill:#9C27B0,stroke:#6A1B9A,stroke-width:2px,color:#fff
    style DB fill:#FF5722,stroke:#BF360C,stroke-width:2px,color:#fff
```

---

## 🔐 Arquitectura de Seguridad (Futura)

```mermaid
graph TB
    USER[👤 Usuario]
    
    subgraph SECURITY["🔒 Capa de Seguridad"]
        GATEWAY[API Gateway<br/>Rate Limiting<br/>CORS]
        AUTH[Authentication Service<br/>JWT Tokens]
        AUTHZ[Authorization Service<br/>Roles & Permissions]
    end
    
    subgraph BACKEND["⚙️ Backend"]
        PUBLIC[Public Endpoints<br/>Sin autenticación]
        PROTECTED[Admin Endpoints<br/>@PreAuthorize<br/>Requiere: ROLE_ADMIN]
    end
    
    subgraph DB["🗄️ Datos"]
        MAIN_DB[(PostgreSQL<br/>Datos principales)]
        AUDIT_DB[(Audit Log<br/>Acciones admin)]
    end
    
    USER -->|HTTP Request| GATEWAY
    GATEWAY -->|Verificar Rate Limit| AUTH
    AUTH -->|Validar Token| AUTHZ
    AUTHZ -->|Verificar Permisos| PUBLIC
    AUTHZ -->|Verificar Permisos| PROTECTED
    
    PUBLIC --> MAIN_DB
    PROTECTED --> MAIN_DB
    PROTECTED -->|Log acciones| AUDIT_DB
    
    style USER fill:#9C27B0,stroke:#6A1B9A,stroke-width:2px,color:#fff
    style SECURITY fill:#FFEBEE,stroke:#C62828,stroke-width:2px
    style BACKEND fill:#E8F5E9,stroke:#2E7D32,stroke-width:2px
    style DB fill:#FFF3E0,stroke:#EF6C00,stroke-width:2px
    style GATEWAY fill:#F44336,stroke:#C62828,stroke-width:2px,color:#fff
    style AUTH fill:#FF5722,stroke:#BF360C,stroke-width:2px,color:#fff
    style AUTHZ fill:#FF9800,stroke:#E65100,stroke-width:2px,color:#fff
    style PUBLIC fill:#4CAF50,stroke:#2E7D32,stroke-width:2px,color:#fff
    style PROTECTED fill:#FF9800,stroke:#E65100,stroke-width:2px,color:#fff
```

---

## 📱 Diagrama de Flujo - Búsqueda de Eventos

```mermaid
flowchart TD
    START([Usuario busca evento])
    
    INPUT{Tipo de búsqueda?}
    SIMPLE[Búsqueda Simple<br/>GET /buscar-simple?q=texto]
    ADVANCED[Búsqueda Avanzada<br/>GET /buscar + filtros]
    
    CONTROLLER[EventoPublicController<br/>Recibe request]
    
    SERVICE[EventoService<br/>Procesa parámetros]
    
    SPEC[EventoSpecification<br/>Construye query dinámica<br/>13 filtros]
    
    REPO[EventoRepository<br/>Ejecuta query JPA]
    
    DB[(PostgreSQL<br/>unaccent<br/>Búsqueda sin acentos)]
    
    CONVERT[Convertir a DTO<br/>EventoMosaicoDTO<br/>EventoListaDTO]
    
    PAGINATE[Aplicar paginación<br/>MOSAICO: 20<br/>LISTA: 50]
    
    RESPONSE[Respuesta JSON<br/>Page con eventos]
    
    END([Usuario recibe resultados])
    
    START --> INPUT
    INPUT -->|Simple| SIMPLE
    INPUT -->|Avanzada| ADVANCED
    SIMPLE --> CONTROLLER
    ADVANCED --> CONTROLLER
    CONTROLLER --> SERVICE
    SERVICE --> SPEC
    SPEC --> REPO
    REPO --> DB
    DB --> REPO
    REPO --> SERVICE
    SERVICE --> CONVERT
    CONVERT --> PAGINATE
    PAGINATE --> RESPONSE
    RESPONSE --> END
    
    style START fill:#9C27B0,stroke:#6A1B9A,stroke-width:2px,color:#fff
    style END fill:#4CAF50,stroke:#2E7D32,stroke-width:2px,color:#fff
    style INPUT fill:#2196F3,stroke:#1565C0,stroke-width:2px,color:#fff
    style CONTROLLER fill:#FF9800,stroke:#E65100,stroke-width:2px,color:#fff
    style SERVICE fill:#4CAF50,stroke:#2E7D32,stroke-width:2px,color:#fff
    style SPEC fill:#9C27B0,stroke:#6A1B9A,stroke-width:2px,color:#fff
    style REPO fill:#FF5722,stroke:#BF360C,stroke-width:2px,color:#fff
    style DB fill:#795548,stroke:#4E342E,stroke-width:2px,color:#fff
```

---

## 🎯 Diagrama de Secuencia - Sistema de Destacados

```mermaid
sequenceDiagram
    actor Admin as 👨‍💼 Administrador
    participant Controller as EventoAdminController
    participant Service as EventoService
    participant Repo as EventoRepository
    participant DB as PostgreSQL
    
    Admin->>Controller: PUT /eventos/{id}/destacar?destacar=true
    activate Controller
    
    Controller->>Service: destacarEvento(id, true)
    activate Service
    
    Service->>Repo: findById(id)
    activate Repo
    Repo->>DB: SELECT * FROM eventos WHERE id = ?
    DB-->>Repo: Evento
    Repo-->>Service: Evento
    deactivate Repo
    
    Service->>Service: Validar evento no cancelado
    Service->>Service: Validar evento publicado
    
    Service->>Repo: countDestacadosVigentes()
    activate Repo
    Note over Repo,DB: Query con fecha/hora vigentes
    Repo->>DB: SELECT COUNT(DISTINCT e.id)<br/>FROM eventos e JOIN funciones f<br/>WHERE destacado=true AND status='PUBLISHED'<br/>AND (fecha > TODAY OR (fecha=TODAY AND hora>NOW))
    DB-->>Repo: count = 2
    Repo-->>Service: 2
    deactivate Repo
    
    alt count >= 3
        Service-->>Controller: ❌ Error: Límite alcanzado
        Controller-->>Admin: 409 Conflict<br/>"Ya hay 3 destacados vigentes"
    else count < 3
        Service->>Service: evento.setDestacado(true)
        Service->>Repo: save(evento)
        activate Repo
        Repo->>DB: UPDATE eventos SET destacado=true
        DB-->>Repo: OK
        Repo-->>Service: Evento actualizado
        deactivate Repo
        
        Service-->>Controller: ✅ Success
        Controller-->>Admin: 200 OK<br/>Evento destacado exitosamente
    end
    
    deactivate Service
    deactivate Controller
```

---

## 📊 Diagrama de Estados - Ciclo de Vida del Evento

```mermaid
stateDiagram-v2
    [*] --> DRAFT: Crear evento
    
    DRAFT --> PUBLISHED: Publicar evento
    DRAFT --> [*]: Eliminar borrador
    
    PUBLISHED --> DESTACADO: Destacar<br/>(si hay espacio)
    DESTACADO --> PUBLISHED: Quitar destacado
    
    PUBLISHED --> ACTIVO: Tiene funciones futuras
    DESTACADO --> ACTIVO: Tiene funciones futuras
    
    ACTIVO --> FINALIZADO: Todas funciones<br/>pasadas
    DESTACADO --> FINALIZADO: Todas funciones<br/>pasadas<br/>(auto-expira)
    
    PUBLISHED --> CANCELADO: Admin cancela
    DESTACADO --> CANCELADO: Admin cancela
    ACTIVO --> CANCELADO: Admin cancela
    
    CANCELADO --> [*]
    FINALIZADO --> [*]
    
    note right of ACTIVO
        Estado calculado
        dinámicamente
    end note
    
    note right of DESTACADO
        Max 3 eventos
        con funciones vigentes
    end note
    
    note right of FINALIZADO
        Todas las funciones
        ya ocurrieron
    end note
```

---

## 🛠️ Cómo Ver Estos Diagramas

### En GitHub
✅ Los diagramas Mermaid se renderizan **automáticamente** al visualizar este archivo en GitHub.

### En VS Code
1. Instalar extensión: **Markdown Preview Mermaid Support**
2. Abrir preview: `Ctrl+Shift+V` (Windows/Linux) o `Cmd+Shift+V` (Mac)
3. Los diagramas se renderizan automáticamente

### En Otras Herramientas
- **Notion**: Soporta Mermaid nativamente
- **Confluence**: Plugin de Mermaid
- **GitLab**: Soporte nativo de Mermaid
- **Obsidian**: Soporte nativo de Mermaid

### Exportar a Imagen
**Opción 1: Mermaid Live Editor**
1. Ir a https://mermaid.live/
2. Pegar el código del diagrama
3. Exportar como PNG/SVG

**Opción 2: VS Code**
1. Instalar: **Markdown PDF**
2. Click derecho → "Markdown PDF: Export (png)"

**Opción 3: CLI**
```bash
npm install -g @mermaid-js/mermaid-cli
mmdc -i DIAGRAMAS_VISUALES.md -o diagrama.png
```

---

## 🎨 Paleta de Colores Usada

| Color | Uso | Hex |
|-------|-----|-----|
| 🔵 Azul | Frontend, Usuario | #2196F3 |
| 🟢 Verde | Backend, Success | #4CAF50 |
| 🟠 Naranja | Admin, Database | #FF9800 |
| 🟣 Morado | Seguridad, Config | #9C27B0 |
| 🔴 Rojo | Errores, Crítico | #F44336 |
| ⚫ Gris | Infraestructura | #607D8B |

---

## 📝 Notas Técnicas

### Ventajas de Mermaid
- ✅ Renderizado automático en GitHub/GitLab
- ✅ Versionado en Git (texto plano)
- ✅ Fácil de actualizar
- ✅ No requiere herramientas externas
- ✅ Soportado por múltiples plataformas

### Limitaciones
- ⚠️ Menos personalización que tools gráficos
- ⚠️ Sintaxis puede ser compleja para diagramas grandes
- ⚠️ No todos los tipos de diagramas UML están soportados

### Alternativas
- **PlantUML**: Más potente, requiere servidor
- **Draw.io**: Visual, archivos binarios
- **Lucidchart**: Comercial, colaborativo
- **Excalidraw**: Hand-drawn style

---

## ✅ Resumen

Este documento contiene **9 diagramas visuales** renderizables:

1. ✅ Contexto del Sistema
2. ✅ Diagrama de Contenedores
3. ✅ Diagrama de Componentes
4. ✅ Despliegue Local (Docker)
5. ✅ Despliegue Producción (Cloud)
6. ✅ Pipeline CI/CD
7. ✅ Arquitectura de Microservicios
8. ✅ Arquitectura de Seguridad
9. ✅ Flujo de Búsqueda
10. ✅ Secuencia de Destacados
11. ✅ Estados del Evento

**Total**: 11 diagramas interactivos en formato Mermaid

---

**Visualiza este archivo en GitHub para ver los diagramas renderizados automáticamente! 🎉**
