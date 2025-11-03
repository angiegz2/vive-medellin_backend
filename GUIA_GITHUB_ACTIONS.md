# 🚀 Guía de Configuración de GitHub Actions - ViveMedellín

Esta guía te ayudará a configurar el pipeline CI/CD de GitHub Actions paso a paso.

---

## 📋 Prerrequisitos

- ✅ Repositorio en GitHub: `CarlosZuluagaU/ViveMedellin_Backend`
- ✅ Archivo `.github/workflows/ci-cd.yml` ya creado
- ⚠️ Cuenta de Docker Hub (para publicar imágenes)
- ⚠️ Git configurado localmente

---

## 🎯 Paso 1: Subir el Código a GitHub

### 1.1 Verificar Estado del Repositorio

```powershell
# Ver el estado actual
git status

# Ver archivos sin rastrear
git ls-files --others --exclude-standard
```

### 1.2 Agregar Todos los Archivos Nuevos

```powershell
# Agregar todos los archivos de documentación y código
git add .

# Verificar qué se va a commitear
git status
```

### 1.3 Hacer Commit

```powershell
git commit -m "feat: Sprint 2 complete - CI/CD, Docs, Diagrams, Security Analysis

- Add GitHub Actions CI/CD pipeline (8 jobs)
- Add comprehensive deployment diagrams (C4 model + Mermaid)
- Add microservices documentation (3 REST services)
- Add OWASP API Security Top 10 analysis
- Add API integration documentation
- Add visual diagrams with Mermaid
- Add utility commands reference
- Update README with Sprint 2 deliverables"
```

### 1.4 Push a GitHub

```powershell
# Push a la rama main
git push origin main

# Si tienes rama develop, también:
git push origin develop
```

---

## 🔐 Paso 2: Configurar Secrets en GitHub

### 2.1 Ir a Configuración del Repositorio

1. Abre tu repositorio: https://github.com/CarlosZuluagaU/ViveMedellin_Backend
2. Click en **"Settings"** (⚙️)
3. En el menú lateral, click en **"Secrets and variables"** → **"Actions"**
4. Click en **"New repository secret"**

### 2.2 Secrets Necesarios

#### 🐳 Docker Hub (REQUERIDO para docker-build job)

**DOCKERHUB_USERNAME**
```
Valor: tu_usuario_dockerhub
```

**DOCKERHUB_TOKEN**
```
1. Ve a https://hub.docker.com/settings/security
2. Click en "New Access Token"
3. Nombre: "GitHub Actions ViveMedellin"
4. Permisos: "Read, Write, Delete"
5. Copia el token generado
6. Pégalo como valor del secret
```

#### 📊 SonarCloud (OPCIONAL para code-quality job)

**SONAR_TOKEN**
```
1. Ve a https://sonarcloud.io/
2. Crea una cuenta (gratis para proyectos open-source)
3. Click en "My Account" → "Security"
4. Generate new token
5. Copia el token
6. Pégalo como valor del secret

Luego, descomentar las líneas en ci-cd.yml:
- Líneas 145-150 (SonarCloud Scan)
```

#### 🚀 Deployment (OPCIONAL para deploy jobs)

**STAGING_HOST** (si tienes servidor de staging)
```
Valor: usuario@ip_servidor_staging
```

**STAGING_SSH_KEY** (si usas SSH para deployment)
```
Valor: tu_clave_privada_ssh
```

**SLACK_WEBHOOK** (OPCIONAL para notificaciones)
```
1. Ve a tu workspace de Slack
2. Crea un webhook en https://api.slack.com/messaging/webhooks
3. Copia la URL del webhook
4. Pégala como valor del secret
```

---

## 🎬 Paso 3: Activar GitHub Actions

### 3.1 Primera Ejecución

Una vez que hagas push, GitHub Actions se activará automáticamente:

```powershell
git push origin main
```

### 3.2 Ver el Pipeline en Acción

1. Ve a tu repositorio en GitHub
2. Click en la pestaña **"Actions"**
3. Verás el workflow "ViveMedellin CI/CD Pipeline" ejecutándose
4. Click en el workflow para ver el progreso en tiempo real

### 3.3 Visualización del Pipeline

```
┌─────────────────────────────────────────────────────┐
│         GITHUB ACTIONS PIPELINE                     │
├─────────────────────────────────────────────────────┤
│                                                     │
│  ┌─────────────────┐                               │
│  │ 1. Build & Test │ ✅ (~3-5 min)                 │
│  └────────┬────────┘                               │
│           │                                         │
│     ┌─────┴─────┬─────────────┐                   │
│     │           │             │                    │
│  ┌──▼──┐    ┌──▼──┐      ┌──▼──┐                  │
│  │ 2.  │    │ 3.  │      │ 4.  │                  │
│  │Sec  │    │Code │      │Wait │                  │
│  │Scan │    │Qual │      │     │                  │
│  └──┬──┘    └─────┘      └─────┘                  │
│     │                                              │
│     └─────────┬──────────────────┐                │
│               │                  │                 │
│         ┌─────▼─────┐            │                │
│         │ 4. Docker │ ✅ (~2-4 min)                │
│         │   Build   │            │                │
│         └─────┬─────┘            │                │
│               │                  │                 │
│         ┌─────┴─────┐            │                │
│         │ 5. Deploy │            │                │
│         │  Staging  │ (develop)  │                │
│         └─────┬─────┘            │                │
│               │                  │                 │
│         ┌─────▼─────┐            │                │
│         │ 6. Deploy │            │                │
│         │Production │ (main)     │                │
│         └───────────┘            │                │
│                                  │                 │
│         ┌────────────────────────▼──┐             │
│         │ 8. Notifications       ✅  │             │
│         └────────────────────────────┘             │
│                                                     │
└─────────────────────────────────────────────────────┘
```

---

## 📊 Paso 4: Configurar Protección de Ramas

### 4.1 Proteger Rama Main

1. Ve a **Settings** → **Branches**
2. Click en **"Add branch protection rule"**
3. Branch name pattern: `main`
4. Habilita:
   - ✅ **Require a pull request before merging**
   - ✅ **Require status checks to pass before merging**
   - ✅ **Require branches to be up to date before merging**
   - ✅ Status checks: Selecciona `build-and-test` y `security-scan`
   - ✅ **Include administrators** (opcional)

### 4.2 Configurar Branch Develop

1. Crea la rama develop si no existe:
   ```powershell
   git checkout -b develop
   git push origin develop
   ```

2. Repite la protección de rama para `develop`

---

## 🎯 Paso 5: Probar el Pipeline

### 5.1 Hacer un Cambio Pequeño

```powershell
# Editar README para probar
echo "`n## CI/CD Status`n![CI/CD](https://github.com/CarlosZuluagaU/ViveMedellin_Backend/workflows/ViveMedellin%20CI%2FCD%20Pipeline/badge.svg)" >> README.md

# Commit y push
git add README.md
git commit -m "test: add CI/CD badge to README"
git push origin main
```

### 5.2 Ver Resultados

1. Ve a **Actions** en GitHub
2. Observa los jobs ejecutándose
3. Revisa los logs de cada job
4. Verifica que todos pasen ✅

---

## 🐛 Paso 6: Solución de Problemas Comunes

### ❌ Error: "docker-build failed - Invalid credentials"

**Solución:**
```
1. Verifica que DOCKERHUB_USERNAME y DOCKERHUB_TOKEN estén configurados
2. Regenera el token en Docker Hub si es necesario
3. Asegúrate de usar Access Token, no la contraseña
```

### ❌ Error: "Tests failed - Connection refused to database"

**Solución:**
```
El servicio PostgreSQL tarda en iniciar.
Ya configurado en ci-cd.yml con health checks.
Si persiste, aumenta el timeout en options.
```

### ❌ Error: "Maven build failed - dependency resolution"

**Solución:**
```powershell
# Verifica pom.xml localmente primero
mvn clean install -DskipTests

# Si funciona local, el problema puede ser cache de Maven
# Limpia cache en GitHub Actions (se hace automático)
```

### ❌ Warning: "Trivy found vulnerabilities"

**Solución:**
```
Es normal encontrar algunas vulnerabilidades.
Revisa el reporte en Security → Code scanning alerts.
Actualiza dependencias si es crítico.
```

---

## 📈 Paso 7: Badges para README

### 7.1 Agregar Badges

```markdown
<!-- Agregar al inicio de README.md -->

## CI/CD Status

[![CI/CD Pipeline](https://github.com/CarlosZuluagaU/ViveMedellin_Backend/workflows/ViveMedellin%20CI%2FCD%20Pipeline/badge.svg)](https://github.com/CarlosZuluagaU/ViveMedellin_Backend/actions)
[![Docker Image](https://img.shields.io/docker/v/tu_usuario/vivemedellin?label=Docker&logo=docker)](https://hub.docker.com/r/tu_usuario/vivemedellin)
[![Security Rating](https://img.shields.io/badge/security-A-brightgreen)](https://github.com/CarlosZuluagaU/ViveMedellin_Backend/security)
[![Java Version](https://img.shields.io/badge/Java-21-orange?logo=java)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.6-brightgreen?logo=spring)](https://spring.io/projects/spring-boot)
```

---

## 🔄 Paso 8: Workflow Completo

### 8.1 Flujo de Trabajo Recomendado

```
┌──────────────────────────────────────────────────────┐
│ DEVELOP BRANCH (staging environment)                │
├──────────────────────────────────────────────────────┤
│                                                      │
│ 1. Feature branch → PR a develop                    │
│ 2. GitHub Actions: build + test + security          │
│ 3. Code review + merge                               │
│ 4. Auto-deploy to staging                           │
│ 5. QA testing en staging                            │
│                                                      │
└──────────────────────────────────────────────────────┘
                    │
                    │ Release ready
                    ▼
┌──────────────────────────────────────────────────────┐
│ MAIN BRANCH (production environment)                │
├──────────────────────────────────────────────────────┤
│                                                      │
│ 1. PR de develop → main                             │
│ 2. GitHub Actions: full pipeline                    │
│ 3. Approval required (branch protection)            │
│ 4. Merge                                             │
│ 5. Auto-deploy to production                        │
│ 6. Monitoring & alerts                              │
│                                                      │
└──────────────────────────────────────────────────────┘
```

### 8.2 Comandos Git Útiles

```powershell
# Crear feature branch
git checkout -b feature/nueva-funcionalidad develop

# Hacer cambios y commit
git add .
git commit -m "feat: nueva funcionalidad"

# Push a GitHub
git push origin feature/nueva-funcionalidad

# Crear Pull Request en GitHub UI
# Después de merge automático:

# Actualizar local
git checkout develop
git pull origin develop

# Cuando esté listo para producción:
git checkout main
git merge develop
git push origin main
```

---

## 📊 Paso 9: Monitoreo y Métricas

### 9.1 Ver Métricas en GitHub

1. **Actions** → Ver historial de ejecuciones
2. **Insights** → Ver estadísticas de workflows
3. **Security** → Ver alertas de vulnerabilidades

### 9.2 Artifacts Generados

Cada ejecución genera artifacts descargables:
- **test-results**: Reportes de tests JUnit
- **owasp-report**: Reporte de vulnerabilidades
- **app-jar**: Archivo JAR compilado
- **performance-results**: Resultados de performance tests

Para descargar:
1. Ve a una ejecución del workflow
2. Scroll hasta "Artifacts"
3. Click en el artifact para descargar

---

## ✅ Checklist Final

### Antes de Producción

- [ ] Secrets configurados (DOCKERHUB_USERNAME, DOCKERHUB_TOKEN)
- [ ] Branch protection habilitado en main
- [ ] Al menos 1 ejecución exitosa del pipeline
- [ ] Tests pasando ✅
- [ ] Security scan sin críticos ✅
- [ ] Docker image construida y publicada ✅
- [ ] Badges agregados al README
- [ ] Documentación actualizada

### Opcional pero Recomendado

- [ ] SonarCloud configurado (SONAR_TOKEN)
- [ ] Slack/Teams notifications configuradas
- [ ] Deployment a staging/production configurado
- [ ] Performance tests configurados
- [ ] Branch develop creada y protegida

---

## 🚀 Próximos Pasos

1. **Configurar Deployment Real**
   - Elegir plataforma (AWS, Azure, GCP, DigitalOcean)
   - Configurar SSH keys o cloud credentials
   - Descomentar secciones de deploy en ci-cd.yml

2. **Mejorar Cobertura de Tests**
   - Objetivo: 80% code coverage
   - Agregar más tests unitarios
   - Agregar tests de integración

3. **Configurar Monitoring**
   - Prometheus + Grafana
   - New Relic / DataDog
   - ELK Stack para logs

4. **Implementar Security**
   - Spring Security + JWT (crítico)
   - Rate limiting
   - CORS restrictivo

---

## 📞 Soporte

Si encuentras problemas:

1. **Revisa los logs** en GitHub Actions
2. **Consulta la documentación**:
   - [GitHub Actions Docs](https://docs.github.com/en/actions)
   - [Docker Build Action](https://github.com/docker/build-push-action)
   - [Trivy Scanner](https://github.com/aquasecurity/trivy-action)

3. **Archivos relevantes**:
   - `.github/workflows/ci-cd.yml` - Pipeline configuration
   - `pom.xml` - Maven dependencies
   - `Dockerfile` - Container configuration

---

## 🎉 ¡Listo!

Tu pipeline de CI/CD está configurado y listo para usar. Cada push a `main` o `develop` ejecutará automáticamente:

✅ Build & Test  
✅ Security Scan  
✅ Code Quality  
✅ Docker Build  
✅ Deploy (cuando lo configures)  
✅ Notifications  

**¡Desarrollo moderno con CI/CD automatizado!** 🚀
