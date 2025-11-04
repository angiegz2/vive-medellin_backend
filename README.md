# 🎉 ViveMedellín Backend - Sistema de Gestión de Eventos# ViveMedellin 🌆



## 📋 DescripciónAplicación Spring Boot para descubrir y vivir lo mejor de Medellín.



Backend REST API para la plataforma **ViveMedellín**, un sistema de gestión y consulta de eventos culturales, deportivos y recreativos en la ciudad de Medellín.## 🚀 Características



## 🚀 Tecnologías- **Framework**: Spring Boot 3.5.6

- **Base de datos**: PostgreSQL 15

- **Java 21** - Lenguaje de programación- **Java**: 21

- **Spring Boot 3.5.6** - Framework principal- **Contenedores**: Docker & Docker Compose

- **PostgreSQL 18** - Base de datos relacional- **Documentación API**: OpenAPI/Swagger

- **JPA/Hibernate** - ORM- **Monitoreo**: Spring Boot Actuator

- **Docker** - Contenerización

- **Swagger/OpenAPI 3.0** - Documentación de API## 🛠️ Configuración del Proyecto

- **Maven** - Gestión de dependencias

### Prerrequisitos

## 🏗️ Arquitectura

- Java 21 o superior

El sistema está diseñado con **arquitectura de microservicios REST** modular:- Maven 3.6+

- Docker y Docker Compose

1. **Microservicio de Búsqueda** - 5 endpoints públicos para consulta y filtrado- Git

2. **Microservicio de Gestión** - 1 endpoint público para detalle de eventos

3. **Microservicio de Administración** - 4 endpoints protegidos para gestión### 📦 Instalación



Total: **9 endpoints REST** + 1 endpoint de salud (Actuator)1. **Clonar el repositorio**

```bash

## 📊 Características Principalesgit clone <url-del-repo>

cd ViveMedellin

### Funcionalidades Públicas```

- ✅ Búsqueda avanzada con **13 filtros** combinables

- ✅ Búsqueda simple por texto (sin acentos)2. **Construir el proyecto**

- ✅ Consulta de próximos eventos```bash

- ✅ Sistema de **carrusel de destacados** (máx. 3 eventos)mvn clean install

- ✅ Detalle completo de eventos con 15 secciones de información```

- ✅ Paginación inteligente (MOSAICO: 20/página, LISTA: 50/página)

3. **Ejecutar con Docker Compose**

### Funcionalidades Administrativas```bash

- ✅ Cancelación de eventosdocker-compose up -d

- ✅ Sistema de destacados con límite automático```

- ✅ Validación de espacios disponibles para destacar

- ✅ Información de estado de destacadosLa aplicación estará disponible en: http://localhost:8080



### Características Técnicas### 🏃‍♂️ Ejecución en Desarrollo

- ✅ Búsqueda **sin acentos** (extensión PostgreSQL unaccent)

- ✅ Cálculo **dinámico de estados** (ACTIVO/FINALIZADO/CANCELADO)#### Opción 1: Con Docker Compose (Recomendado)

- ✅ **Expiración automática** de destacados (sin cron jobs)```bash

- ✅ Queries optimizadas con JPA Specifications# Levantar solo PostgreSQL

- ✅ DTOs especializados por caso de usodocker-compose up postgres -d



## 🔧 Instalación y Configuración# Ejecutar la aplicación en modo desarrollo

mvn spring-boot:run -Dspring-boot.run.profiles=dev

### Prerrequisitos```



- Java 21 o superior#### Opción 2: Completamente local

- Docker y Docker Compose```bash

- Maven 3.9+# Si tienes PostgreSQL instalado localmente

- PostgreSQL 18 (si no usas Docker)mvn spring-boot:run

```

### 1. Clonar el Repositorio

### 🔧 Configuración

```bash

git clone https://github.com/CarlosZuluagaU/ViveMedellin_Backend.git#### Perfiles de Aplicación

cd ViveMedellin_Backend

```- **default**: Configuración base con PostgreSQL

- **dev**: Configuración para desarrollo con hot-reload

### 2. Configurar Variables de Entorno- **prod**: Configuración optimizada para producción



Crear archivo `.env` (o configurar en `application.properties`):#### Variables de Entorno (Producción)



```properties```bash

# Base de datosDATABASE_URL=jdbc:postgresql://localhost:5432/mydatabase

SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/vivemedellinDATABASE_USERNAME=myuser

SPRING_DATASOURCE_USERNAME=postgresDATABASE_PASSWORD=secret

SPRING_DATASOURCE_PASSWORD=postgres```



# Puerto del servidor## 📚 API Documentation

SERVER_PORT=8081

Una vez que la aplicación esté ejecutándose, puedes acceder a:

# PostgreSQL (requiere extensión unaccent)

# Ejecutar: CREATE EXTENSION IF NOT EXISTS unaccent;- **Swagger UI**: http://localhost:8080/swagger-ui/index.html

```- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

- **Actuator Health**: http://localhost:8080/actuator/health

### 3. Iniciar con Docker Compose (Recomendado)

## 🗄️ Base de Datos

```bash

# Iniciar todos los servicios (backend + postgres)### Estructura

docker-compose up -d- La aplicación usa PostgreSQL como base de datos principal

- Las migraciones se manejan con Hibernate DDL auto-update

# Ver logs- Scripts de inicialización en `init-db/`

docker-compose logs -f backend

### Acceso Directo a la Base de Datos

# Detener servicios```bash

docker-compose down# Conectar a PostgreSQL del contenedor

```docker-compose exec postgres psql -U myuser -d mydatabase

```

### 4. Iniciar Manualmente

## 🧪 Testing

```bash

# Compilar```bash

mvn clean install# Ejecutar todos los tests

mvn test

# Ejecutar

mvn spring-boot:run# Tests con TestContainers (requiere Docker)

mvn verify

# O con JAR```

java -jar target/ViveMedellin-0.0.1-SNAPSHOT.jar

```## 🏗️ Estructura del Proyecto



### 5. Verificar Instalación```

src/

```bash├── main/

# Health check│   ├── java/com/vivemedellin/

curl http://localhost:8081/actuator/health│   │   ├── config/          # Configuraciones

│   │   ├── controller/      # Controladores REST

# Swagger UI│   │   ├── service/         # Lógica de negocio

http://localhost:8081/swagger-ui/index.html│   │   ├── repository/      # Acceso a datos

```│   │   ├── model/          # Entidades

│   │   └── ViveMedellinApplication.java

## 📚 Documentación│   └── resources/

│       ├── application*.properties

### Documentación de API│       ├── static/         # Recursos estáticos

- **Swagger UI**: [http://localhost:8081/swagger-ui/index.html](http://localhost:8081/swagger-ui/index.html)│       └── templates/      # Plantillas (si usas Thymeleaf)

- **OpenAPI JSON**: [http://localhost:8081/v3/api-docs](http://localhost:8081/v3/api-docs)└── test/                   # Tests unitarios e integración

- **Guía de Integración**: [`API_BACKEND_INTEGRACION.md`](./API_BACKEND_INTEGRACION.md)```



### Documentación Técnica## 🐳 Docker

- **Diagrama de Despliegue**: [`DIAGRAMA_DESPLIEGUE.md`](./DIAGRAMA_DESPLIEGUE.md)

- **Microservicios REST**: [`MICROSERVICIOS_REST.md`](./MICROSERVICIOS_REST.md)### Construir imagen personalizada

- **Análisis de Vulnerabilidades**: [`ANALISIS_VULNERABILIDADES.md`](./ANALISIS_VULNERABILIDADES.md)```bash

docker build -t vivemedellin:latest .

## 🔌 Endpoints Principales```



### Endpoints Públicos (Sin Autenticación)### Ejecutar contenedor individual

```bash

```httpdocker run -p 8080:8080 \

# Búsqueda avanzada  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/mydatabase \

GET /api/public/eventos/buscar  vivemedellin:latest

```

# Búsqueda simple

GET /api/public/eventos/buscar-simple?q={texto}## 🔄 Comandos Útiles



# Detalle de evento```bash

GET /api/public/eventos/{id}# Limpiar y reinstalar dependencias

mvn clean install

# Próximos eventos

GET /api/public/eventos/proximos?dias={dias}# Ejecutar en modo debug

mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"

# Carrusel de destacados

GET /api/public/eventos/destacados-carrusel# Ver logs de la aplicación

```docker-compose logs -f vivemedellin-app



### Endpoints Administrativos (Requieren Autenticación)# Reiniciar solo la base de datos

docker-compose restart postgres

```http

# Cancelar evento# Limpiar volúmenes de Docker

POST /api/admin/eventos/{id}/cancelardocker-compose down -v

```

# Destacar/Quitar destacado

PUT /api/admin/eventos/{id}/destacar?destacar={true|false}## 🌱 Desarrollo



# Info de destacados### Agregar nuevas entidades

GET /api/admin/eventos/destacados/info1. Crear la clase en `src/main/java/com/vivemedellin/model/`

2. Crear el repositorio en `src/main/java/com/vivemedellin/repository/`

# Validar si puede destacar3. Implementar el servicio en `src/main/java/com/vivemedellin/service/`

GET /api/admin/eventos/{id}/puede-destacar4. Crear el controlador en `src/main/java/com/vivemedellin/controller/`

```

### Hot Reload

## 🧪 PruebasEl proyecto incluye Spring Boot DevTools para hot reload automático durante el desarrollo.



### Ejecutar Tests## 📝 Licencia



```bashEste proyecto está bajo la Licencia MIT. Ver el archivo `LICENSE` para más detalles.

# Tests unitarios

mvn test## 🤝 Contribución



# Tests con cobertura1. Fork el proyecto

mvn test jacoco:report2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)

3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)

# Ver reporte de cobertura4. Push a la rama (`git push origin feature/AmazingFeature`)

open target/site/jacoco/index.html5. Abre un Pull Request

```

## 📞 Contacto

### Tests de Integración

- Email: contacto@vivemedellin.com

```bash- Sitio Web: https://vivemedellin.com

# Con TestContainers (PostgreSQL en contenedor)

mvn verify---

```

¡Hecho con ❤️ para Medellín!
## 🔒 Seguridad

### Estado Actual
⚠️ **IMPORTANTE**: Los endpoints administrativos están preparados para Spring Security pero **NO están protegidos actualmente**.

### Para Producción (Pendiente)
1. Implementar Spring Security
2. Configurar JWT Authentication
3. Activar `@PreAuthorize` en endpoints admin
4. Configurar CORS restrictivo
5. Implementar Rate Limiting

Ver detalles en [`ANALISIS_VULNERABILIDADES.md`](./ANALISIS_VULNERABILIDADES.md)

## 🚢 Despliegue

### Docker

```bash
# Build de imagen
docker build -t vivemedellin:latest .

# Run con Docker
docker run -p 8081:8081 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/vivemedellin \
  vivemedellin:latest
```

### CI/CD con GitHub Actions

El proyecto incluye pipeline completo en `.github/workflows/ci-cd.yml`:

- ✅ Build y compilación
- ✅ Tests automatizados
- ✅ Escaneo de vulnerabilidades (OWASP, Trivy)
- ✅ Build de imagen Docker
- ✅ Deploy automático (staging/producción)

**Configurar Secrets en GitHub:**
- `DOCKERHUB_USERNAME`
- `DOCKERHUB_TOKEN`
- Variables de entorno del servidor

## 📊 Base de Datos

### Extensión Requerida

```sql
-- Ejecutar en PostgreSQL
CREATE EXTENSION IF NOT EXISTS unaccent;
```

### Esquema Principal

```sql
-- Tabla de eventos
CREATE TABLE eventos (
    id BIGSERIAL PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    descripcion TEXT,
    categoria VARCHAR(100),
    status VARCHAR(20),
    destacado BOOLEAN DEFAULT FALSE,
    -- ... más campos
);

-- Tabla de funciones (fechas/horarios)
CREATE TABLE funciones (
    id BIGSERIAL PRIMARY KEY,
    evento_id BIGINT NOT NULL,
    fecha DATE NOT NULL,
    horario TIME NOT NULL,
    FOREIGN KEY (evento_id) REFERENCES eventos(id)
);
```

## 🏆 Sprint 2 - Entregables

### ✅ Completado

1. **Diagrama de Despliegue** - C4 completo con 4 niveles ✅
2. **APIs Implementadas** - 9 endpoints REST validados ✅
3. **Microservicios Documentados** - 3 microservicios REST ✅
4. **GitHub Actions CI/CD** - Pipeline completo configurado ✅
5. **Análisis de Vulnerabilidades** - OWASP Top 10 documentado ✅
6. **Documentación de APIs** - Swagger + guías completas ✅

## 📈 Métricas del Proyecto

| Métrica | Valor |
|---------|-------|
| Endpoints REST | 9 |
| Microservicios | 3 |
| Filtros de Búsqueda | 13 |
| DTOs | 7 |
| Líneas de Código (Java) | ~3,500 |
| Líneas de Documentación | ~5,000 |
| Cobertura de Tests | Pendiente |

## 🤝 Contribución

### Flujo de Trabajo

1. Fork del repositorio
2. Crear branch: `git checkout -b feature/nueva-funcionalidad`
3. Commit: `git commit -m 'feat: agregar nueva funcionalidad'`
4. Push: `git push origin feature/nueva-funcionalidad`
5. Crear Pull Request

### Convenciones

- **Commits**: Seguir [Conventional Commits](https://www.conventionalcommits.org/)
- **Código**: Seguir [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)
- **Tests**: Cobertura mínima 80%

## 📝 Licencia

Este proyecto es parte del desarrollo académico de ViveMedellín.

## 👥 Equipo

- **Backend Team**: [CarlosZuluagaU](https://github.com/CarlosZuluagaU)
- **Repositorio**: [ViveMedellin_Backend](https://github.com/CarlosZuluagaU/ViveMedellin_Backend)

## 📞 Soporte

Para reportar problemas o solicitar características:
- **Issues**: [GitHub Issues](https://github.com/CarlosZuluagaU/ViveMedellin_Backend/issues)
- **Documentación**: Ver archivos `.md` en el repositorio

---

**Estado del Proyecto**: 🚀 **Sprint 2 Completado** - Listo para integración con frontend

**Última Actualización**: Noviembre 2025
