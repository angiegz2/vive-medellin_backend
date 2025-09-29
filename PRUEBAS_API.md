# Pruebas de la API de Eventos ViveMedellin

## Base URL
```
http://localhost:8081/api/v1/eventos
```

## 1. Listar todas las categorías disponibles
```bash
curl -X GET http://localhost:8081/api/v1/eventos/categorias \
  -H "Content-Type: application/json"
```

## 2. Crear un nuevo evento
```bash
curl -X POST http://localhost:8081/api/v1/eventos \
  -H "Content-Type: application/json" \
  -H "X-User-Id: admin1" \
  -d '{
    "titulo": "Festival de Jazz Internacional",
    "descripcion": "Disfruta de una noche mágica con los mejores exponentes del jazz nacional e internacional. Un evento que reúne a talentosos músicos en un espectáculo único con improvisaciones, solos destacados y fusiones innovadoras.",
    "fecha": "2025-11-15",
    "horario": "19:00",
    "categoria": "Culturales y Artísticos",
    "modalidad": "PRESENCIAL",
    "aforo": 500,
    "valorIngreso": "85000",
    "destacado": true,
    "ubicacion": {
      "direccionCompleta": "Comuna 10, Teatro Pablo Tobón Uribe",
      "comunaBarrio": "La Candelaria",
      "direccionDetallada": "Carrera 40 #51-24, Teatro Pablo Tobón Uribe",
      "enlaceMapa": "https://maps.google.com/?q=Teatro+Pablo+Tobón+Uribe+Medellín"
    },
    "organizador": {
      "nombre": "Fundación Cultural Jazz Colombia",
      "celular": "3101234567",
      "identificacion": "901234567-8",
      "email": "contacto@jazzcolombia.org"
    },
    "imagenCaratula": "/assets/images/eventos/jazz.jpeg",
    "serviciosAdicionales": [
      "Sonido",
      "Iluminación",
      "Escenarios",
      "Artistas en vivo",
      "Climatización"
    ],
    "funciones": [
      {
        "numeroFuncion": 1,
        "fecha": "2025-11-15",
        "horario": "19:00"
      },
      {
        "numeroFuncion": 2,
        "fecha": "2025-11-16",
        "horario": "19:00"
      }
    ]
  }'
```

## 3. Crear un evento gratuito
```bash
curl -X POST http://localhost:8081/api/v1/eventos \
  -H "Content-Type: application/json" \
  -H "X-User-Id: admin1" \
  -d '{
    "titulo": "Conferencia de Tecnología Blockchain",
    "descripcion": "Únete a los líderes de la industria tecnológica en esta conferencia exclusiva sobre Blockchain y criptomonedas.",
    "fecha": "2025-11-22",
    "horario": "09:00",
    "categoria": "Tecnológicos",
    "modalidad": "PRESENCIAL",
    "aforo": 200,
    "valorIngreso": "gratuito",
    "destacado": true,
    "ubicacion": {
      "direccionCompleta": "Comuna 10, Centro de Innovación Digital",
      "comunaBarrio": "La Candelaria",
      "direccionDetallada": "Carrera 50 #42-15, Centro de Innovación Digital",
      "enlaceMapa": "https://maps.google.com/?q=Carrera+50+42-15+La+Candelaria+Medellín"
    },
    "organizador": {
      "nombre": "TechMed Community",
      "celular": "3207654321",
      "identificacion": "123456789",
      "email": "info@techmed.co"
    },
    "imagenCaratula": "/assets/images/eventos/blockchain.jpeg",
    "serviciosAdicionales": [
      "Audiovisuales",
      "Climatización",
      "Catering",
      "Coordinador de eventos"
    ]
  }'
```

## 4. Listar todos los eventos activos
```bash
curl -X GET http://localhost:8081/api/v1/eventos \
  -H "Content-Type: application/json"
```

## 5. Obtener evento por ID
```bash
curl -X GET http://localhost:8081/api/v1/eventos/1 \
  -H "Content-Type: application/json"
```

## 6. Listar eventos destacados
```bash
curl -X GET http://localhost:8081/api/v1/eventos/destacados \
  -H "Content-Type: application/json"
```

## 7. Buscar eventos con filtros
```bash
# Buscar por categoría
curl -X GET "http://localhost:8081/api/v1/eventos/buscar?categoria=Tecnológicos" \
  -H "Content-Type: application/json"

# Buscar eventos destacados
curl -X GET "http://localhost:8081/api/v1/eventos/buscar?destacado=true" \
  -H "Content-Type: application/json"

# Buscar por ubicación
curl -X GET "http://localhost:8081/api/v1/eventos/buscar?ubicacion=La Candelaria" \
  -H "Content-Type: application/json"

# Buscar por rango de fechas
curl -X GET "http://localhost:8081/api/v1/eventos/buscar?fechaInicio=2025-11-01&fechaFin=2025-11-30" \
  -H "Content-Type: application/json"
```

## 8. Búsqueda de texto completo
```bash
curl -X GET "http://localhost:8081/api/v1/eventos/buscar/texto?q=jazz" \
  -H "Content-Type: application/json"
```

## 9. Actualizar evento
```bash
curl -X PUT http://localhost:8081/api/v1/eventos/1 \
  -H "Content-Type: application/json" \
  -H "X-User-Id: admin1" \
  -d '{
    "titulo": "Festival de Jazz Internacional - ACTUALIZADO",
    "descripcion": "Descripción actualizada del evento de jazz.",
    "destacado": false
  }'
```

## 10. Destacar evento
```bash
curl -X PATCH http://localhost:8081/api/v1/eventos/1/destacar \
  -H "Content-Type: application/json" \
  -H "X-User-Id: admin1"
```

## 11. Quitar destaque de evento
```bash
curl -X PATCH http://localhost:8081/api/v1/eventos/1/quitar-destaque \
  -H "Content-Type: application/json" \
  -H "X-User-Id: admin1"
```

## 12. Cancelar evento
```bash
curl -X PATCH http://localhost:8081/api/v1/eventos/1/cancelar \
  -H "Content-Type: application/json" \
  -H "X-User-Id: admin1"
```

## 13. Reactivar evento cancelado
```bash
curl -X PATCH http://localhost:8081/api/v1/eventos/1/reactivar \
  -H "Content-Type: application/json" \
  -H "X-User-Id: admin1"
```

## 14. Obtener evento cancelado (con parámetro includeCancelled)
```bash
curl -X GET "http://localhost:8081/api/v1/eventos/1?includeCancelled=true" \
  -H "Content-Type: application/json"
```

## 15. Obtener estadísticas
```bash
curl -X GET http://localhost:8081/api/v1/eventos/estadisticas \
  -H "Content-Type: application/json"
```

## Ejemplos con PowerShell (Windows)

### Crear evento con PowerShell
```powershell
$headers = @{
    "Content-Type" = "application/json"
    "X-User-Id" = "admin1"
}

$body = @{
    titulo = "Evento de Prueba PowerShell"
    descripcion = "Evento creado desde PowerShell para pruebas"
    fecha = "2025-12-01"
    horario = "18:00"
    categoria = "Tecnológicos"
    modalidad = "PRESENCIAL"
    aforo = 100
    valorIngreso = "gratuito"
    destacado = $false
    ubicacion = @{
        direccionCompleta = "Comuna 1, Centro de Medellín"
        comunaBarrio = "Centro"
        direccionDetallada = "Calle 50 #50-50"
        enlaceMapa = ""
    }
    organizador = @{
        nombre = "Organizador PowerShell"
        celular = "3001234567"
        identificacion = "1234567890"
        email = "test@powershell.com"
    }
    serviciosAdicionales = @("Sonido", "Proyección")
} | ConvertTo-Json -Depth 10

Invoke-RestMethod -Uri "http://localhost:8081/api/v1/eventos" -Method POST -Headers $headers -Body $body
```

### Listar eventos con PowerShell
```powershell
Invoke-RestMethod -Uri "http://localhost:8081/api/v1/eventos" -Method GET -ContentType "application/json"
```
