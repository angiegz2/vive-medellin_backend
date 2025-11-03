# 📚 Índice de Documentación - ViveMedellín Backend

## 🎯 Documentos del Sprint 2

Este índice te ayudará a navegar por toda la documentación generada para el Sprint 2.

---

## 🚀 Inicio Rápido

### ¿Primera vez con el proyecto?
👉 **Empieza aquí:** [`README.md`](./README.md)
- Descripción general del proyecto
- Instalación y configuración
- Comandos básicos
- Links a toda la documentación

### ¿Necesitas integrar el frontend?
👉 **Guía de integración:** [`API_BACKEND_INTEGRACION.md`](./API_BACKEND_INTEGRACION.md)
- 9 endpoints REST documentados
- Ejemplos de cURL
- DTOs en formato TypeScript
- Códigos de respuesta
- Reglas de negocio

---

## 📋 Entregables del Sprint 2

### 1️⃣ Diagrama de Despliegue
📄 [`DIAGRAMA_DESPLIEGUE.md`](./DIAGRAMA_DESPLIEGUE.md) - 950 líneas

**Contenido:**
- ✅ Nivel 1: Diagrama de Contexto del Sistema
- ✅ Nivel 2: Diagrama de Contenedores
- ✅ Nivel 3: Diagrama de Componentes (Backend)
- ✅ Nivel 4: Diagrama de Despliegue en Entornos
- ✅ Pipeline CI/CD completo
- ✅ Recursos y escalabilidad
- ✅ Configuración Docker

**Cuándo usar:**
- Entender la arquitectura general
- Planificar despliegues
- Configurar ambientes (dev/staging/prod)
- Presentaciones técnicas

---

### 2️⃣ Documentación de Microservicios REST
📄 [`MICROSERVICIOS_REST.md`](./MICROSERVICIOS_REST.md) - 1,100 líneas

**Contenido:**
- ✅ **Microservicio 1: Búsqueda** (5 endpoints)
  - Búsqueda avanzada con 13 filtros
  - Búsqueda simple
  - Carrusel de destacados
  - Próximos eventos
- ✅ **Microservicio 2: Gestión** (1 endpoint)
  - Detalle completo de eventos
  - Cálculo de estados
- ✅ **Microservicio 3: Administración** (4 endpoints)
  - Cancelar eventos
  - Sistema de destacados
  - Validaciones

**Cuándo usar:**
- Entender la arquitectura modular
- Ver lógica de negocio detallada
- Conocer componentes técnicos
- Planificar microservicios independientes

---

### 3️⃣ Análisis de Vulnerabilidades
📄 [`ANALISIS_VULNERABILIDADES.md`](./ANALISIS_VULNERABILIDADES.md) - 1,000 líneas

**Contenido:**
- ✅ OWASP API Security Top 10 (2023)
- ✅ 10 categorías analizadas
- ✅ Vulnerabilidades identificadas:
  - 🔴 2 Críticas
  - 🟠 3 Altas
  - 🟡 2 Medias
  - 🟢 3 Bajas
- ✅ Soluciones propuestas con código
- ✅ Plan de acción priorizado
- ✅ Herramientas de escaneo

**Cuándo usar:**
- Antes de desplegar a producción
- Auditorías de seguridad
- Planning de seguridad
- Implementar correcciones

---

### 4️⃣ GitHub Actions CI/CD
📄 [`.github/workflows/ci-cd.yml`](./.github/workflows/ci-cd.yml) - 300 líneas

**Contenido:**
- ✅ 8 Jobs configurados:
  1. Build and Test
  2. Security Scan (OWASP + Trivy)
  3. Code Quality (SonarCloud)
  4. Docker Build
  5. Deploy Staging
  6. Deploy Production
  7. Performance Tests
  8. Notifications

**Cuándo usar:**
- Configurar CI/CD en GitHub
- Automatizar builds y tests
- Escaneo de seguridad automático
- Deploy continuo

---

### 5️⃣ Documentación de APIs
📄 [`API_BACKEND_INTEGRACION.md`](./API_BACKEND_INTEGRACION.md) - 600 líneas

**Contenido:**
- ✅ 9 Endpoints REST documentados
- ✅ Parámetros y tipos de datos
- ✅ Ejemplos de cURL
- ✅ DTOs en TypeScript
- ✅ Códigos HTTP
- ✅ Reglas de negocio
- ✅ Paginación y filtros
- ✅ Manejo de errores

**Cuándo usar:**
- Integración con frontend
- Consumir APIs desde cliente
- Testing de endpoints
- Documentación para clientes

---

### 6️⃣ Reporte de Sprint 2
📄 [`SPRINT2_ENTREGABLES.md`](./SPRINT2_ENTREGABLES.md) - 700 líneas

**Contenido:**
- ✅ Resumen ejecutivo
- ✅ Estado de cada entregable
- ✅ Métricas del sprint
- ✅ Checklist de verificación
- ✅ Próximos pasos
- ✅ Recomendaciones

**Cuándo usar:**
- Revisión de sprint
- Presentación a stakeholders
- Validación de entregables
- Planning del siguiente sprint

---

## 🔧 Documentación Técnica Adicional

### Swagger UI (Interactivo)
🌐 http://localhost:8081/swagger-ui/index.html

**Características:**
- Documentación interactiva
- Try it out functionality
- Schemas de DTOs
- Ejemplos de request/response

### OpenAPI Specification
🌐 http://localhost:8081/v3/api-docs

**Formato:** JSON OpenAPI 3.0

### Actuator Health Check
🌐 http://localhost:8081/actuator/health

**Uso:** Verificar estado del backend

---

## 📊 Estructura de la Documentación

```
ViveMedellin/
│
├── 📘 README.md                          ← Inicio aquí
│   └── Descripción, instalación, comandos
│
├── 🔌 API_BACKEND_INTEGRACION.md         ← Para frontend
│   └── 9 endpoints, ejemplos, DTOs
│
├── 🏗️ DIAGRAMA_DESPLIEGUE.md             ← Arquitectura
│   └── C4 completo, 4 niveles
│
├── 🎭 MICROSERVICIOS_REST.md             ← Diseño técnico
│   └── 3 microservicios, componentes
│
├── 🔒 ANALISIS_VULNERABILIDADES.md       ← Seguridad
│   └── OWASP Top 10, soluciones
│
├── 📊 SPRINT2_ENTREGABLES.md             ← Reporte
│   └── Estado, métricas, checklist
│
└── ⚙️ .github/workflows/ci-cd.yml        ← CI/CD
    └── 8 jobs, deploy automático
```

---

## 🎯 Uso por Rol

### 👨‍💻 Desarrollador Backend
1. [`README.md`](./README.md) - Setup y configuración
2. [`MICROSERVICIOS_REST.md`](./MICROSERVICIOS_REST.md) - Arquitectura
3. [`ANALISIS_VULNERABILIDADES.md`](./ANALISIS_VULNERABILIDADES.md) - Seguridad
4. Swagger UI - Testing de endpoints

### 👨‍💻 Desarrollador Frontend
1. [`API_BACKEND_INTEGRACION.md`](./API_BACKEND_INTEGRACION.md) - **PRINCIPAL**
2. Swagger UI - Explorar endpoints
3. [`README.md`](./README.md) - Iniciar backend localmente

### 🏗️ Arquitecto de Software
1. [`DIAGRAMA_DESPLIEGUE.md`](./DIAGRAMA_DESPLIEGUE.md) - Arquitectura C4
2. [`MICROSERVICIOS_REST.md`](./MICROSERVICIOS_REST.md) - Diseño modular
3. [`SPRINT2_ENTREGABLES.md`](./SPRINT2_ENTREGABLES.md) - Métricas

### 🔒 Security Engineer
1. [`ANALISIS_VULNERABILIDADES.md`](./ANALISIS_VULNERABILIDADES.md) - **PRINCIPAL**
2. `.github/workflows/ci-cd.yml` - Security scans
3. [`DIAGRAMA_DESPLIEGUE.md`](./DIAGRAMA_DESPLIEGUE.md) - Seguridad en despliegue

### 🚀 DevOps Engineer
1. [`DIAGRAMA_DESPLIEGUE.md`](./DIAGRAMA_DESPLIEGUE.md) - Infraestructura
2. `.github/workflows/ci-cd.yml` - Pipeline CI/CD
3. `Dockerfile` + `compose.yaml` - Contenedores
4. [`README.md`](./README.md) - Comandos de despliegue

### 📊 Project Manager / Scrum Master
1. [`SPRINT2_ENTREGABLES.md`](./SPRINT2_ENTREGABLES.md) - **PRINCIPAL**
2. [`README.md`](./README.md) - Overview del proyecto

---

## 📖 Guías Rápidas

### ¿Cómo iniciar el backend?
```bash
# Ver README.md sección "Instalación y Configuración"
docker-compose up -d
```

### ¿Cómo probar un endpoint?
```bash
# Ver API_BACKEND_INTEGRACION.md con ejemplos de cURL
curl "http://localhost:8081/api/public/eventos/buscar?texto=musica"
```

### ¿Cómo ver la documentación interactiva?
```
http://localhost:8081/swagger-ui/index.html
```

### ¿Cómo desplegar con CI/CD?
```bash
# Ver .github/workflows/ci-cd.yml
# Push a branch main o develop activa el pipeline
git push origin main
```

### ¿Cómo corregir vulnerabilidades?
```bash
# Ver ANALISIS_VULNERABILIDADES.md sección "Plan de Acción"
# Prioridad: Implementar Spring Security + JWT
```

---

## 🔍 Búsqueda Rápida

### Buscar información sobre...

**Endpoints públicos** → [`API_BACKEND_INTEGRACION.md`](./API_BACKEND_INTEGRACION.md#-endpoints-públicos-sin-autenticación)

**Endpoints admin** → [`API_BACKEND_INTEGRACION.md`](./API_BACKEND_INTEGRACION.md#-endpoints-de-administrador-requieren-autenticación)

**Filtros de búsqueda** → [`API_BACKEND_INTEGRACION.md`](./API_BACKEND_INTEGRACION.md#-filtros-disponibles)

**DTOs** → [`MICROSERVICIOS_REST.md`](./MICROSERVICIOS_REST.md) (sección de cada microservicio)

**Arquitectura** → [`DIAGRAMA_DESPLIEGUE.md`](./DIAGRAMA_DESPLIEGUE.md)

**Microservicios** → [`MICROSERVICIOS_REST.md`](./MICROSERVICIOS_REST.md)

**Seguridad** → [`ANALISIS_VULNERABILIDADES.md`](./ANALISIS_VULNERABILIDADES.md)

**CI/CD** → [`.github/workflows/ci-cd.yml`](./.github/workflows/ci-cd.yml)

**Docker** → [`DIAGRAMA_DESPLIEGUE.md`](./DIAGRAMA_DESPLIEGUE.md#-nivel-4-diagrama-de-despliegue-en-entornos)

**Base de datos** → [`DIAGRAMA_DESPLIEGUE.md`](./DIAGRAMA_DESPLIEGUE.md#-base-de-datos)

**Instalación** → [`README.md`](./README.md#-instalación-y-configuración)

**Testing** → [`README.md`](./README.md#-pruebas)

**Métricas** → [`SPRINT2_ENTREGABLES.md`](./SPRINT2_ENTREGABLES.md#-métricas-del-sprint-2)

---

## ✅ Checklist de Documentos

### Documentos Principales
- [x] README.md - 400 líneas
- [x] API_BACKEND_INTEGRACION.md - 600 líneas
- [x] DIAGRAMA_DESPLIEGUE.md - 950 líneas
- [x] MICROSERVICIOS_REST.md - 1,100 líneas
- [x] ANALISIS_VULNERABILIDADES.md - 1,000 líneas
- [x] SPRINT2_ENTREGABLES.md - 700 líneas
- [x] INDICE_DOCUMENTACION.md - Este archivo

### Archivos de Configuración
- [x] .github/workflows/ci-cd.yml - 300 líneas
- [x] Dockerfile - Multi-stage build
- [x] compose.yaml - Docker Compose
- [x] pom.xml - Dependencias Maven

### Documentación Generada Automáticamente
- [x] Swagger UI - /swagger-ui/index.html
- [x] OpenAPI JSON - /v3/api-docs
- [x] Actuator Health - /actuator/health

---

## 📊 Estadísticas de Documentación

| Tipo | Archivos | Líneas | Tamaño |
|------|----------|--------|--------|
| Documentación Markdown | 7 | ~4,750 | ~320 KB |
| Configuración CI/CD | 1 | 300 | 12 KB |
| Configuración Docker | 2 | 50 | 3 KB |
| Código Java | ~15 | ~3,500 | ~180 KB |
| **Total Proyecto** | **~25** | **~8,600** | **~515 KB** |

---

## 🎉 Resumen

### Documentación Completa ✅
- ✅ 7 documentos Markdown
- ✅ 4,750+ líneas de documentación
- ✅ 100% de entregables del Sprint 2
- ✅ Swagger interactivo
- ✅ CI/CD configurado

### Navegación Rápida
1. **Nuevos usuarios** → [`README.md`](./README.md)
2. **Integración frontend** → [`API_BACKEND_INTEGRACION.md`](./API_BACKEND_INTEGRACION.md)
3. **Arquitectura** → [`DIAGRAMA_DESPLIEGUE.md`](./DIAGRAMA_DESPLIEGUE.md)
4. **Seguridad** → [`ANALISIS_VULNERABILIDADES.md`](./ANALISIS_VULNERABILIDADES.md)
5. **Sprint review** → [`SPRINT2_ENTREGABLES.md`](./SPRINT2_ENTREGABLES.md)

---

**¿Dudas?** Consulta el documento específico según tu necesidad.

**¿Quieres contribuir?** Lee [`README.md`](./README.md#-contribución)

**Estado**: ✅ Documentación completa y actualizada - Noviembre 2025
