# 🔧 Comandos Útiles - ViveMedellín Backend

## 📋 Comandos de Desarrollo

### Iniciar el Proyecto

```bash
# Con Docker Compose (Recomendado)
docker-compose up -d

# Sin Docker (requiere PostgreSQL local)
mvn spring-boot:run

# Con Maven Wrapper
./mvnw spring-boot:run
```

### Detener el Proyecto

```bash
# Docker Compose
docker-compose down

# Docker Compose con limpieza de volúmenes
docker-compose down -v
```

### Ver Logs

```bash
# Logs del backend
docker-compose logs -f backend

# Logs de postgres
docker-compose logs -f postgres

# Logs de ambos
docker-compose logs -f
```

---

## 🔨 Compilación y Build

### Compilar

```bash
# Compilar sin tests
mvn clean compile

# Compilar con tests
mvn clean install

# Compilar y crear JAR
mvn clean package
```

### Limpiar

```bash
# Limpiar target/
mvn clean

# Limpiar y borrar dependencies descargadas
mvn clean dependency:purge-local-repository
```

---

## 🧪 Tests

### Ejecutar Tests

```bash
# Todos los tests
mvn test

# Tests con reporte de cobertura
mvn test jacoco:report

# Tests de integración
mvn verify

# Tests específicos
mvn test -Dtest=EventoServiceTest

# Tests con logs verbose
mvn test -X
```

### Ver Reportes

```bash
# Abrir reporte de cobertura
start target/site/jacoco/index.html

# Abrir reporte de surefire
start target/site/surefire-report.html
```

---

## 🐳 Docker

### Construir Imagen

```bash
# Build local
docker build -t vivemedellin:latest .

# Build con tag específico
docker build -t vivemedellin:1.0.0 .

# Build sin cache
docker build --no-cache -t vivemedellin:latest .
```

### Ejecutar Contenedor

```bash
# Run simple
docker run -p 8081:8081 vivemedellin:latest

# Run con variables de entorno
docker run -p 8081:8081 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/vivemedellin \
  -e SPRING_DATASOURCE_USERNAME=postgres \
  -e SPRING_DATASOURCE_PASSWORD=postgres \
  vivemedellin:latest

# Run en background
docker run -d -p 8081:8081 --name vivemedellin-backend vivemedellin:latest
```

### Gestionar Contenedores

```bash
# Ver contenedores corriendo
docker ps

# Ver todos los contenedores
docker ps -a

# Detener contenedor
docker stop vivemedellin-backend

# Eliminar contenedor
docker rm vivemedellin-backend

# Ver logs
docker logs -f vivemedellin-backend

# Entrar al contenedor
docker exec -it vivemedellin-backend sh
```

### Limpiar Docker

```bash
# Limpiar contenedores detenidos
docker container prune

# Limpiar imágenes no usadas
docker image prune

# Limpiar todo (cuidado!)
docker system prune -a

# Ver uso de espacio
docker system df
```

---

## 🗄️ Base de Datos

### Conectar a PostgreSQL

```bash
# Con Docker Compose
docker-compose exec postgres psql -U postgres -d vivemedellin

# Con cliente local
psql -h localhost -p 5432 -U postgres -d vivemedellin
```

### Comandos SQL Útiles

```sql
-- Listar tablas
\dt

-- Describir tabla
\d eventos

-- Ver extensiones
\dx

-- Crear extensión unaccent
CREATE EXTENSION IF NOT EXISTS unaccent;

-- Contar eventos
SELECT COUNT(*) FROM eventos;

-- Ver eventos destacados
SELECT id, titulo, destacado FROM eventos WHERE destacado = true;

-- Ver funciones vigentes
SELECT e.titulo, f.fecha, f.horario 
FROM eventos e 
JOIN funciones f ON f.evento_id = e.id
WHERE f.fecha >= CURRENT_DATE
ORDER BY f.fecha, f.horario;

-- Salir
\q
```

### Backup y Restore

```bash
# Backup
docker-compose exec postgres pg_dump -U postgres vivemedellin > backup.sql

# Restore
docker-compose exec -T postgres psql -U postgres vivemedellin < backup.sql

# Backup con Docker
docker exec vivemedellin-postgres pg_dump -U postgres vivemedellin > backup.sql
```

---

## 🔍 Testing de APIs

### Health Check

```bash
# Verificar que el backend está corriendo
curl http://localhost:8081/actuator/health

# Con formato
curl http://localhost:8081/actuator/health | jq
```

### Endpoints Públicos

```bash
# Búsqueda simple
curl "http://localhost:8081/api/public/eventos/buscar-simple?q=musica"

# Búsqueda avanzada
curl "http://localhost:8081/api/public/eventos/buscar?texto=concierto&ubicacion=Poblado&page=0"

# Detalle de evento
curl http://localhost:8081/api/public/eventos/1 | jq

# Próximos eventos
curl "http://localhost:8081/api/public/eventos/proximos?dias=30"

# Carrusel de destacados
curl http://localhost:8081/api/public/eventos/destacados-carrusel | jq
```

### Endpoints Admin (requieren auth)

```bash
# Cancelar evento
curl -X POST http://localhost:8081/api/admin/eventos/1/cancelar

# Destacar evento
curl -X PUT "http://localhost:8081/api/admin/eventos/1/destacar?destacar=true"

# Info de destacados
curl http://localhost:8081/api/admin/eventos/destacados/info | jq

# Validar si puede destacar
curl http://localhost:8081/api/admin/eventos/5/puede-destacar | jq
```

---

## 📊 Swagger y Documentación

### Abrir Swagger UI

```bash
# Windows
start http://localhost:8081/swagger-ui/index.html

# Linux/Mac
open http://localhost:8081/swagger-ui/index.html

# O manualmente:
# http://localhost:8081/swagger-ui/index.html
```

### OpenAPI JSON

```bash
# Descargar spec
curl http://localhost:8081/v3/api-docs > openapi.json

# Ver spec formateada
curl http://localhost:8081/v3/api-docs | jq

# Guardar spec formateada
curl http://localhost:8081/v3/api-docs | jq > openapi-formatted.json
```

---

## 🔒 Seguridad

### Escaneo de Vulnerabilidades

```bash
# OWASP Dependency Check
mvn org.owasp:dependency-check-maven:check

# Ver reporte
start target/dependency-check-report.html

# Trivy scan (requiere Trivy instalado)
trivy fs .

# Trivy scan de imagen Docker
trivy image vivemedellin:latest
```

### Actualizar Dependencias

```bash
# Ver dependencias desactualizadas
mvn versions:display-dependency-updates

# Ver plugins desactualizados
mvn versions:display-plugin-updates

# Actualizar versión de dependencia
mvn versions:use-latest-versions

# Ver árbol de dependencias
mvn dependency:tree
```

---

## 🚀 Despliegue

### Push a Docker Hub

```bash
# Login
docker login

# Tag
docker tag vivemedellin:latest username/vivemedellin:latest
docker tag vivemedellin:latest username/vivemedellin:1.0.0

# Push
docker push username/vivemedellin:latest
docker push username/vivemedellin:1.0.0
```

### GitHub Actions

```bash
# Ver status del workflow
gh run list

# Ver logs del último run
gh run view

# Re-run workflow fallido
gh run rerun

# Ver workflows disponibles
gh workflow list
```

---

## 📝 Git

### Commits Convencionales

```bash
# Feature
git commit -m "feat: agregar endpoint de búsqueda avanzada"

# Fix
git commit -m "fix: corregir paginación en búsqueda"

# Docs
git commit -m "docs: actualizar README con instrucciones Docker"

# Refactor
git commit -m "refactor: simplificar lógica de destacados"

# Test
git commit -m "test: agregar tests para EventoService"

# Chore
git commit -m "chore: actualizar dependencias"
```

### Branches

```bash
# Crear feature branch
git checkout -b feature/nueva-funcionalidad

# Crear fix branch
git checkout -b fix/corregir-bug

# Merge a main
git checkout main
git merge feature/nueva-funcionalidad

# Delete branch
git branch -d feature/nueva-funcionalidad
```

---

## 📊 Monitoreo

### Ver Logs en Tiempo Real

```bash
# Backend logs
tail -f logs/spring.log

# Docker logs
docker-compose logs -f --tail=100

# Filtrar por nivel
docker-compose logs -f | grep ERROR
```

### Métricas con Actuator

```bash
# Info general
curl http://localhost:8081/actuator/info | jq

# Métricas
curl http://localhost:8081/actuator/metrics | jq

# Métrica específica
curl http://localhost:8081/actuator/metrics/jvm.memory.used | jq

# Beans
curl http://localhost:8081/actuator/beans | jq
```

---

## 🛠️ Utilidades

### Verificar Java y Maven

```bash
# Versión de Java
java -version

# Versión de Maven
mvn -version

# JAVA_HOME
echo $JAVA_HOME  # Linux/Mac
echo %JAVA_HOME%  # Windows
```

### Verificar Puerto

```bash
# Windows
netstat -ano | findstr :8081

# Linux/Mac
lsof -i :8081
netstat -an | grep 8081
```

### Matar Proceso en Puerto

```bash
# Windows (buscar PID con netstat)
taskkill /PID <PID> /F

# Linux/Mac
kill -9 $(lsof -ti:8081)
```

---

## 📦 Maven Útiles

### Dependency Management

```bash
# Analizar dependencias
mvn dependency:analyze

# Copiar dependencias
mvn dependency:copy-dependencies

# Tree de dependencias
mvn dependency:tree -Dverbose

# Resolve
mvn dependency:resolve
```

### Plugins

```bash
# Ejecutar específico
mvn spring-boot:run

# Con perfil
mvn spring-boot:run -Pdev

# Con propiedades
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=9090
```

---

## 🔧 Troubleshooting

### Limpiar Todo

```bash
# Maven clean
mvn clean

# Docker clean
docker-compose down -v
docker system prune -a

# Git clean
git clean -fdx
```

### Reiniciar Desde Cero

```bash
# 1. Detener todo
docker-compose down -v

# 2. Limpiar Maven
mvn clean

# 3. Rebuild
mvn clean install -DskipTests

# 4. Rebuild Docker
docker-compose build --no-cache

# 5. Reiniciar
docker-compose up -d
```

### Ver Variables de Entorno

```bash
# En contenedor
docker-compose exec backend env | grep SPRING

# Local
mvn help:system | grep spring
```

---

## ✅ Checklist de Desarrollo

```bash
# Antes de commit
mvn clean test                    # Tests
mvn clean package                 # Build
docker-compose up -d              # Verificar Docker
curl http://localhost:8081/actuator/health  # Health check
```

---

## 📚 Referencias Rápidas

- **Swagger UI**: http://localhost:8081/swagger-ui/index.html
- **OpenAPI**: http://localhost:8081/v3/api-docs
- **Health**: http://localhost:8081/actuator/health
- **Documentación**: Ver archivos `.md` en raíz del proyecto

---

**Tip**: Guarda este archivo en favoritos para acceso rápido a comandos comunes.
