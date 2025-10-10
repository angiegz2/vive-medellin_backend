# 🚀 Quick Start: Pruebas de Filtros de Búsqueda

## Iniciar el Servidor

```powershell
cd c:\Users\carlo\Desktop\ViveMedellin
./mvnw.cmd spring-boot:run
```

Espera hasta ver:
```
Started ViveMedellinApplication in X.XX seconds
```

---

## 🧪 Pruebas Rápidas con PowerShell

### 1️⃣ Verificar que el servidor está corriendo

```powershell
curl http://localhost:8081/api/v1/eventos
```

### 2️⃣ Buscar eventos por ubicación

```powershell
# Eventos en El Poblado
curl "http://localhost:8081/api/v1/eventos/buscar/ubicacion?ubicacion=Poblado"

# Eventos en Laureles
curl "http://localhost:8081/api/v1/eventos/buscar/ubicacion?ubicacion=Laureles"

# Eventos en el Centro
curl "http://localhost:8081/api/v1/eventos/buscar/ubicacion?ubicacion=Centro"
```

### 3️⃣ Buscar eventos por fecha

```powershell
# Eventos de hoy
curl "http://localhost:8081/api/v1/eventos/buscar/fecha?fecha=2025-10-08"

# Eventos de una fecha específica
curl "http://localhost:8081/api/v1/eventos/buscar/fecha?fecha=2025-12-24"
```

### 4️⃣ Buscar eventos por rango de fechas

```powershell
# Eventos de octubre
curl "http://localhost:8081/api/v1/eventos/buscar/rango-fechas?fechaDesde=2025-10-01&fechaHasta=2025-10-31"

# Eventos del último trimestre
curl "http://localhost:8081/api/v1/eventos/buscar/rango-fechas?fechaDesde=2025-10-01&fechaHasta=2025-12-31"
```

### 5️⃣ Buscar eventos por palabras clave

```powershell
# Buscar "concierto"
curl "http://localhost:8081/api/v1/eventos/buscar/keywords?q=concierto"

# Buscar "teatro infantil"
curl "http://localhost:8081/api/v1/eventos/buscar/keywords?q=teatro%20infantil"

# Buscar "cultura"
curl "http://localhost:8081/api/v1/eventos/buscar/keywords?q=cultura"
```

### 6️⃣ Búsqueda avanzada (combinar filtros)

```powershell
# Conciertos gratuitos en El Poblado
curl "http://localhost:8081/api/v1/eventos/buscar/avanzada?texto=concierto&ubicacion=Poblado&gratuito=true"

# Eventos culturales destacados
curl "http://localhost:8081/api/v1/eventos/buscar/avanzada?categoria=Culturales&destacado=true"

# Eventos virtuales en diciembre
curl "http://localhost:8081/api/v1/eventos/buscar/avanzada?modalidad=VIRTUAL&fechaDesde=2025-12-01&fechaHasta=2025-12-31"
```

### 7️⃣ Listar eventos especiales

```powershell
# Eventos próximos
curl "http://localhost:8081/api/v1/eventos/proximos"

# Eventos gratuitos
curl "http://localhost:8081/api/v1/eventos/gratuitos"

# Eventos destacados
curl "http://localhost:8081/api/v1/eventos/destacados"
```

---

## 🌐 Abrir Swagger UI

```powershell
Start-Process "http://localhost:8081/swagger-ui/index.html"
```

O abre manualmente en tu navegador:
```
http://localhost:8081/swagger-ui/index.html
```

---

## 📝 Crear Evento de Prueba

```powershell
$body = @{
    titulo = "Concierto de Jazz en El Poblado"
    descripcion = "Gran evento musical gratuito en el corazón de El Poblado"
    fecha = "2025-10-15"
    horario = "19:00:00"
    categoria = "Culturales y Artísticos"
    modalidad = "PRESENCIAL"
    aforo = 500
    valorIngreso = "gratuito"
    destacado = $true
    ubicacion = @{
        direccionCompleta = "Parque Lleras, Calle 10 con Carrera 37"
        comunaBarrio = "El Poblado"
        direccionDetallada = "Tarima principal del parque"
        enlaceMapa = "https://maps.google.com/?q=Parque+Lleras"
    }
    organizador = @{
        nombre = "Secretaría de Cultura"
        celular = "3001234567"
        identificacion = "890999999-1"
        email = "cultura@medellin.gov.co"
    }
} | ConvertTo-Json -Depth 10

Invoke-RestMethod -Uri "http://localhost:8081/api/v1/eventos" -Method Post -Body $body -ContentType "application/json"
```

---

## 🔍 Verificar Búsquedas

Después de crear el evento de prueba, verifica que los filtros funcionen:

```powershell
# 1. Debe aparecer en búsqueda por ubicación
curl "http://localhost:8081/api/v1/eventos/buscar/ubicacion?ubicacion=Poblado"

# 2. Debe aparecer en búsqueda por fecha
curl "http://localhost:8081/api/v1/eventos/buscar/fecha?fecha=2025-10-15"

# 3. Debe aparecer en búsqueda por palabras clave
curl "http://localhost:8081/api/v1/eventos/buscar/keywords?q=jazz"

# 4. Debe aparecer en eventos gratuitos
curl "http://localhost:8081/api/v1/eventos/gratuitos"

# 5. Debe aparecer en eventos destacados
curl "http://localhost:8081/api/v1/eventos/destacados"
```

---

## 📊 Resultados Esperados

Cada búsqueda debe retornar JSON con la estructura:

```json
{
  "content": [
    {
      "id": 1,
      "titulo": "Concierto de Jazz en El Poblado",
      "descripcion": "Gran evento musical gratuito...",
      "fecha": "2025-10-15",
      "horario": "19:00:00",
      "categoria": "Culturales y Artísticos",
      "modalidad": "PRESENCIAL",
      "valorIngreso": "gratuito",
      "destacado": true,
      "ubicacion": {
        "comunaBarrio": "El Poblado",
        "direccionCompleta": "Parque Lleras, Calle 10 con Carrera 37"
      }
    }
  ],
  "pageable": {...},
  "totalElements": 1
}
```

---

## ❌ Solución de Problemas

### El servidor no inicia
```powershell
# Verifica que PostgreSQL esté corriendo
Get-Service -Name postgresql*

# Si no está corriendo, inícialo
Start-Service postgresql-x64-18
```

### Error de conexión a base de datos
```powershell
# Verifica las credenciales en application.properties
# Usuario: postgres
# Password: root12345
# Database: vivemedellin
# Puerto: 5432
```

### Curl no funciona
```powershell
# Usa Invoke-RestMethod en su lugar
Invoke-RestMethod -Uri "http://localhost:8081/api/v1/eventos" -Method Get
```

---

## 📚 Documentación Completa

- **Guía de Uso:** `docs/GUIA-FILTROS-BUSQUEDA.md`
- **Resumen Técnico:** `docs/RESUMEN-IMPLEMENTACION-FILTROS.md`
- **Swagger UI:** http://localhost:8081/swagger-ui/index.html

---

## ✅ Checklist de Pruebas

- [ ] Servidor inicia correctamente
- [ ] Swagger UI accesible
- [ ] GET /api/v1/eventos funciona
- [ ] Búsqueda por ubicación funciona
- [ ] Búsqueda por fecha funciona
- [ ] Búsqueda por palabras clave funciona
- [ ] Búsqueda avanzada funciona
- [ ] Eventos próximos funciona
- [ ] Eventos gratuitos funciona
- [ ] Paginación funciona correctamente

---

**Nota:** Si no tienes eventos en la base de datos, primero crea algunos usando el ejemplo de "Crear Evento de Prueba" anterior, o usa Swagger UI para crearlos interactivamente.
