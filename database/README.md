# Configuración de Base de Datos - ViveMedellin

## Configuraciones Disponibles

### 🔄 Perfiles de Configuración

La aplicación ViveMedellin soporta múltiples perfiles de base de datos:

#### **Desarrollo (dev)** - H2 en memoria
- **Perfil:** `dev`
- **Base de datos:** H2 en memoria
- **Uso:** Desarrollo local rápido
- **Datos:** Se pierden al reiniciar
- **Console H2:** http://localhost:8081/h2-console

#### **Producción (prod)** - PostgreSQL
- **Perfil:** `prod`  
- **Base de datos:** PostgreSQL
- **Uso:** Producción y testing persistente
- **Datos:** Persistentes
- **Host:** localhost:5432

---

## 🐘 Configuración PostgreSQL

### Requisitos Previos
1. **PostgreSQL 12+** instalado
2. **Usuario:** `postgres`
3. **Contraseña:** `root12345`
4. **Puerto:** `5432` (default)

### Instalación Automática

Ejecutar el script de configuración:

```powershell
# En PowerShell como administrador
cd C:\Users\carlo\Desktop\ViveMedellin\database
.\setup_postgresql.ps1
```

### Instalación Manual

```sql
-- 1. Crear base de datos
CREATE DATABASE vivemedellin;

-- 2. Conectar a la base de datos
\c vivemedellin;

-- 3. Crear extensiones útiles
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pg_trgm";
```

---

## 🚀 Ejecutar la Aplicación

### Con PostgreSQL (Producción)
```bash
# Opción 1: Variable de entorno
mvn spring-boot:run -Dspring.profiles.active=prod

# Opción 2: Cambiar en application.properties
spring.profiles.active=prod
mvn spring-boot:run
```

### Con H2 (Desarrollo)
```bash
# Opción 1: Variable de entorno  
mvn spring-boot:run -Dspring.profiles.active=dev

# Opción 2: Cambiar en application.properties
spring.profiles.active=dev
mvn spring-boot:run
```

---

## 📊 Configuración de Conexiones

### PostgreSQL (application-prod.properties)
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/vivemedellin
spring.datasource.username=postgres
spring.datasource.password=root12345
spring.jpa.hibernate.ddl-auto=update
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
```

### H2 (application-dev.properties)
```properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.username=sa
spring.datasource.password=password
spring.h2.console.enabled=true
spring.jpa.hibernate.ddl-auto=create-drop
```

---

## 🔧 Solución de Problemas

### Error: "database does not exist"
```bash
# Ejecutar script de configuración
.\database\setup_postgresql.ps1
```

### Error: "password authentication failed"
```bash
# Verificar contraseña en PostgreSQL
psql -h localhost -U postgres
# Cambiar contraseña si es necesario
ALTER USER postgres PASSWORD 'root12345';
```

### Error: "connection refused"
```bash
# Verificar que PostgreSQL esté ejecutándose
# Windows: Servicios -> postgresql-x64-xx
# O reiniciar el servicio
net start postgresql-x64-14
```

---

## 📋 Estructura de Tablas

Las tablas se crean automáticamente con Hibernate DDL:

- **eventos** - Información principal de eventos
- **usuarios** - Perfiles de usuarios
- **funciones** - Horarios y fechas de eventos  
- **comentarios** - Sistema de comentarios
- **valoraciones** - Calificaciones de eventos
- **grupos** - Comunidades de usuarios
- **notificaciones** - Sistema de notificaciones

---

## 🌐 Accesos Útiles

### PostgreSQL
- **Cliente:** `psql -h localhost -U postgres -d vivemedellin`
- **PgAdmin:** http://localhost (si está instalado)

### H2 Console (solo desarrollo)
- **URL:** http://localhost:8081/h2-console
- **JDBC URL:** `jdbc:h2:mem:testdb`
- **Usuario:** `sa`
- **Contraseña:** `password`

### Swagger API
- **URL:** http://localhost:8081/swagger-ui/index.html

### Actuator
- **Health:** http://localhost:8081/actuator/health
- **Info:** http://localhost:8081/actuator/info