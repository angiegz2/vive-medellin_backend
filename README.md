# ViveMedellin 🌆

Aplicación Spring Boot para descubrir y vivir lo mejor de Medellín.

## 🚀 Características

- **Framework**: Spring Boot 3.5.6
- **Base de datos**: PostgreSQL 15
- **Java**: 21
- **Contenedores**: Docker & Docker Compose
- **Documentación API**: OpenAPI/Swagger
- **Monitoreo**: Spring Boot Actuator

## 🛠️ Configuración del Proyecto

### Prerrequisitos

- Java 21 o superior
- Maven 3.6+
- Docker y Docker Compose
- Git

### 📦 Instalación

1. **Clonar el repositorio**
```bash
git clone <url-del-repo>
cd ViveMedellin
```

2. **Construir el proyecto**
```bash
mvn clean install
```

3. **Ejecutar con Docker Compose**
```bash
docker-compose up -d
```

La aplicación estará disponible en: http://localhost:8080

### 🏃‍♂️ Ejecución en Desarrollo

#### Opción 1: Con Docker Compose (Recomendado)
```bash
# Levantar solo PostgreSQL
docker-compose up postgres -d

# Ejecutar la aplicación en modo desarrollo
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

#### Opción 2: Completamente local
```bash
# Si tienes PostgreSQL instalado localmente
mvn spring-boot:run
```

### 🔧 Configuración

#### Perfiles de Aplicación

- **default**: Configuración base con PostgreSQL
- **dev**: Configuración para desarrollo con hot-reload
- **prod**: Configuración optimizada para producción

#### Variables de Entorno (Producción)

```bash
DATABASE_URL=jdbc:postgresql://localhost:5432/mydatabase
DATABASE_USERNAME=myuser
DATABASE_PASSWORD=secret
```

## 📚 API Documentation

Una vez que la aplicación esté ejecutándose, puedes acceder a:

- **Swagger UI**: http://localhost:8080/swagger-ui/index.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs
- **Actuator Health**: http://localhost:8080/actuator/health

## 🗄️ Base de Datos

### Estructura
- La aplicación usa PostgreSQL como base de datos principal
- Las migraciones se manejan con Hibernate DDL auto-update
- Scripts de inicialización en `init-db/`

### Acceso Directo a la Base de Datos
```bash
# Conectar a PostgreSQL del contenedor
docker-compose exec postgres psql -U myuser -d mydatabase
```

## 🧪 Testing

```bash
# Ejecutar todos los tests
mvn test

# Tests con TestContainers (requiere Docker)
mvn verify
```

## 🏗️ Estructura del Proyecto

```
src/
├── main/
│   ├── java/com/vivemedellin/
│   │   ├── config/          # Configuraciones
│   │   ├── controller/      # Controladores REST
│   │   ├── service/         # Lógica de negocio
│   │   ├── repository/      # Acceso a datos
│   │   ├── model/          # Entidades
│   │   └── ViveMedellinApplication.java
│   └── resources/
│       ├── application*.properties
│       ├── static/         # Recursos estáticos
│       └── templates/      # Plantillas (si usas Thymeleaf)
└── test/                   # Tests unitarios e integración
```

## 🐳 Docker

### Construir imagen personalizada
```bash
docker build -t vivemedellin:latest .
```

### Ejecutar contenedor individual
```bash
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/mydatabase \
  vivemedellin:latest
```

## 🔄 Comandos Útiles

```bash
# Limpiar y reinstalar dependencias
mvn clean install

# Ejecutar en modo debug
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"

# Ver logs de la aplicación
docker-compose logs -f vivemedellin-app

# Reiniciar solo la base de datos
docker-compose restart postgres

# Limpiar volúmenes de Docker
docker-compose down -v
```

## 🌱 Desarrollo

### Agregar nuevas entidades
1. Crear la clase en `src/main/java/com/vivemedellin/model/`
2. Crear el repositorio en `src/main/java/com/vivemedellin/repository/`
3. Implementar el servicio en `src/main/java/com/vivemedellin/service/`
4. Crear el controlador en `src/main/java/com/vivemedellin/controller/`

### Hot Reload
El proyecto incluye Spring Boot DevTools para hot reload automático durante el desarrollo.

## 📝 Licencia

Este proyecto está bajo la Licencia MIT. Ver el archivo `LICENSE` para más detalles.

## 🤝 Contribución

1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

## 📞 Contacto

- Email: contacto@vivemedellin.com
- Sitio Web: https://vivemedellin.com

---

¡Hecho con ❤️ para Medellín!