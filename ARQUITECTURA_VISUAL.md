# Arquitectura Visual Simple - ViveMedellin

## 🎯 Vista General del Sistema

```
┌─────────────────────────────────────────────────────────────────┐
│                     🌐 USUARIOS FINALES                         │
├─────────────────┬─────────────────┬─────────────────────────────┤
│   👨‍💼 Ciudadano   │  👩‍💼 Organizador   │      👨‍💻 Administrador     │
│   • Busca eventos │  • Crea eventos    │   • Gestiona plataforma   │
│   • Valora        │  • Gestiona        │   • Supervisa contenido   │
│   • Comenta       │  • Promociona      │   • Analiza métricas      │
└─────────┬───────┴─────────┬───────┴─────────────────┬───────────┘
          │                 │                         │
          └─────────────────┼─────────────────────────┘
                            │
                     📱💻🖥️ INTERFACES
          ┌─────────────────┼─────────────────────────┐
          │                 │                         │
    ┌─────▼─────┐    ┌─────▼─────┐           ┌─────▼─────┐
    │ 📱 Mobile  │    │ 💻 Web App │           │ 🖥️ Admin   │
    │   App      │    │ (React.js) │           │  Panel    │
    │(React      │    │            │           │           │
    │ Native)    │    │            │           │           │
    └─────┬─────┘    └─────┬─────┘           └─────┬─────┘
          │                │                       │
          └────────────────┼───────────────────────┘
                           │
                    🚪 API GATEWAY
              ┌───────────▼────────────┐
              │   Spring Cloud Gateway  │
              │   • Enrutamiento       │
              │   • Autenticación      │
              │   • Rate Limiting      │
              └───────────┬────────────┘
                          │
               🏗️ BACKEND SERVICES
    ┌─────────────────────┼─────────────────────┐
    │                     │                     │
┌───▼────┐    ┌──────▼──────┐    ┌──────▼──────┐
│🔐 Auth  │    │ 🎪 Events    │    │📢 Notify     │
│Service │    │   Service    │    │ Service     │
└───┬────┘    └──────┬──────┘    └──────┬──────┘
    │                │                  │
    └────────────────┼──────────────────┘
                     │
        📊 BACKEND API (Spring Boot 3.5.6)
┌────────────────────┼────────────────────────────┐
│                                                 │
│  🎯 CONTROLLERS (REST API)                      │
│  ├── EventoController     ├── UsuarioController │
│  ├── ValoracionController ├── NotificacionController │
│                                                 │
│  ⚙️ SERVICES (Business Logic)                   │
│  ├── EventoService        ├── UsuarioService    │
│  ├── ValoracionService    ├── SearchService     │
│                                                 │
│  🗃️ REPOSITORIES (Data Access)                  │
│  ├── EventoRepository     ├── UsuarioRepository │
│  ├── ValoracionRepository ├── FuncionRepository │
│                                                 │
└─────────────────────┬───────────────────────────┘
                      │
        💾 CAPA DE PERSISTENCIA
┌─────────────────────┼─────────────────────────────┐
│                     │                             │
│   🐘 PostgreSQL 18  │  🚀 Redis Cache  │ 🔍 Search │
│   ┌───────────────┐ │ ┌─────────────┐  │┌─────────┐│
│   │ • eventos     │ │ │ • sesiones  │  ││Elasticsearch││
│   │ • usuarios    │ │ │ • cache API │  │└─────────┘│
│   │ • valoraciones│ │ │ • temp data │  │           │
│   │ • funciones   │ │ └─────────────┘  │           │
│   └───────────────┘ │                  │           │
└─────────────────────┴──────────────────┴───────────┘
```

## 🏛️ Arquitectura en Capas

```
┌─────────────────────────────────────────────────┐
│                🌐 PRESENTATION                   │ 
│  React Web App | React Native | Admin Panel     │
└─────────────────────┬───────────────────────────┘
                      │ HTTPS/JSON
┌─────────────────────▼───────────────────────────┐
│                📡 API LAYER                     │
│     Spring MVC Controllers + OpenAPI/Swagger   │
└─────────────────────┬───────────────────────────┘
                      │ Java Method Calls
┌─────────────────────▼───────────────────────────┐
│               ⚙️ BUSINESS LAYER                  │
│          Spring Services + Business Logic       │
└─────────────────────┬───────────────────────────┘
                      │ JPA/Repository Pattern  
┌─────────────────────▼───────────────────────────┐
│              🗃️ PERSISTENCE LAYER                │
│      Spring Data JPA + Hibernate + PostgreSQL  │
└─────────────────────────────────────────────────┘
```

## 🔄 Flujos de Datos Principales

### 1. Crear Evento
```
👨‍💼 Organizador 
    │
    └── POST /api/eventos
         │
    ┌────▼─────┐    ┌─────────────┐    ┌──────────────┐
    │Controller│───▶│EventoService│───▶│EventoRepository│
    └──────────┘    └─────┬───────┘    └──────┬───────┘
                          │                   │
                    ┌─────▼────────┐         ▼
                    │NotifyService │    💾 PostgreSQL
                    └──────────────┘         
```

### 2. Buscar Eventos
```
👨‍💼 Usuario 
    │
    └── GET /api/eventos?categoria=MUSICA
         │
    ┌────▼─────┐    ┌─────────────┐    ┌──────────────┐
    │Controller│───▶│EventoService│───▶│ Cache Redis  │
    └──────┬───┘    └─────┬───────┘    └──────┬───────┘
           │              │                   │
           │              └─── SI NO EN CACHE ─┘
           │                      │
           │              ┌───────▼────────┐
           │              │EventoRepository│
           │              └────────────────┘
           │                      │
           └◀────── JSON Response ◀┘
```

### 3. Sistema de Valoraciones
```
👨‍💼 Usuario Valora Evento
    │
    └── POST /api/valoraciones
         │
    ┌────▼─────┐    ┌────────────────┐    ┌─────────────────┐
    │Controller│───▶│ValoracionService│───▶│ValoracionRepository│
    └──────────┘    └────────┬───────┘    └─────────────────┘
                             │                      │
                    ┌────────▼────────┐            ▼
                    │ EventoService   │      💾 PostgreSQL
                    │actualizarPromedio│           
                    └─────────────────┘           
```

## 📊 Entidades y Relaciones Clave

```
    👤 USUARIO ──┐
         │       │
         │   ┌───▼────┐     ┌─────────┐
         │   │VALORACIÓN│──▶│ EVENTO  │◀┐
         │   └────────┘     └─────┬───┘ │
         │                        │     │
         └──▶ 📝 COMENTARIO ──────┘     │
                                        │
              🎭 ORGANIZADOR ────────────┘
                     │
              📍 UBICACION ──────────────┘
                     │
              🎪 FUNCIÓN ────────────────┘
```

## 🚀 Tecnologías por Capa

| Capa | Tecnología | Propósito |
|------|------------|-----------|
| **Frontend** | React.js + TypeScript | Web responsiva |
| **Mobile** | React Native | App nativa iOS/Android |
| **API** | Spring Boot 3.5.6 | Backend REST API |
| **Security** | Spring Security + JWT | Autenticación/Autorización |
| **Database** | PostgreSQL 18 | Persistencia ACID |
| **Cache** | Redis | Performance y sesiones |
| **Search** | Elasticsearch | Búsqueda avanzada |
| **Docs** | SpringDoc OpenAPI | Documentación automática |

## 🎯 Características Implementadas

### ✅ CRUD Completo
- ✅ Crear eventos
- ✅ Leer/Listar eventos  
- ✅ Actualizar eventos
- ✅ Cancelar eventos (Soft Delete)

### ✅ Funcionalidades Especiales
- ✅ Destacar eventos
- ✅ Sistema de valoraciones (1-5 estrellas)
- ✅ Comentarios moderados
- ✅ Búsqueda por categoría/fecha/ubicación
- ✅ Notificaciones automáticas
- ✅ Gestión de funciones/horarios

### ✅ Aspectos Técnicos
- ✅ Multi-perfil (dev/prod)
- ✅ Pool de conexiones optimizado
- ✅ Cache distribuido
- ✅ Documentación Swagger automática
- ✅ Validaciones multi-capa
- ✅ Manejo centralizado de errores

---

*Esta arquitectura soporta el crecimiento y la escalabilidad de ViveMedellin como plataforma líder de eventos en Medellín* 🏙️🎉