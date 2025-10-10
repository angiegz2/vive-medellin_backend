# 📋 Guía de Uso: Sistema de Filtros de Búsqueda - ViveMedellin

## 🎯 Descripción General

El sistema de filtros implementado permite buscar eventos de múltiples formas:
- **Por ubicación** (comuna, barrio o dirección)
- **Por fecha** (fecha específica o rango de fechas)
- **Por palabras clave** (búsqueda en título, descripción, categoría, organizador)
- **Filtros combinados** (búsqueda avanzada con múltiples criterios)

---

## 🚀 Endpoints Disponibles

### **Base URL**: `http://localhost:8081/api/v1/eventos`

---

## 1️⃣ Búsqueda Avanzada (Recomendada)

**Endpoint más potente** que combina múltiples filtros.

### **GET** `/buscar/avanzada`

#### **Parámetros disponibles:**

| Parámetro | Tipo | Descripción | Ejemplo |
|-----------|------|-------------|---------|
| `texto` | String | Busca en título y descripción | `concierto` |
| `ubicacion` | String | Comuna, barrio o dirección | `Poblado`, `Laureles` |
| `categoria` | String | Categoría específica | `Culturales y Artísticos` |
| `fechaDesde` | Date | Fecha inicial (YYYY-MM-DD) | `2025-10-01` |
| `fechaFin` | Date | Fecha final (YYYY-MM-DD) | `2025-12-31` |
| `destacado` | Boolean | Solo eventos destacados | `true` |
| `gratuito` | Boolean | Solo eventos gratuitos | `true` |
| `modalidad` | String | PRESENCIAL, VIRTUAL, HIBRIDA | `PRESENCIAL` |
| `organizador` | String | Nombre del organizador | `Alcaldía` |
| `soloActivos` | Boolean | Solo eventos publicados | `true` (default) |
| `page` | Integer | Número de página | `0` (default) |
| `size` | Integer | Tamaño de página | `10` (default) |
| `ordenarPor` | String | Campo de ordenamiento | `fecha`, `titulo` |
| `direccion` | String | ASC o DESC | `ASC` (default) |

#### **Ejemplos de uso:**

```bash
# 1. Buscar eventos en El Poblado
curl "http://localhost:8081/api/v1/eventos/buscar/avanzada?ubicacion=Poblado"

# 2. Buscar conciertos gratuitos en octubre
curl "http://localhost:8081/api/v1/eventos/buscar/avanzada?texto=concierto&gratuito=true&fechaDesde=2025-10-01&fechaFin=2025-10-31"

# 3. Buscar eventos culturales destacados en Laureles
curl "http://localhost:8081/api/v1/eventos/buscar/avanzada?categoria=Culturales%20y%20Artisticos&ubicacion=Laureles&destacado=true"

# 4. Buscar eventos virtuales en diciembre
curl "http://localhost:8081/api/v1/eventos/buscar/avanzada?modalidad=VIRTUAL&fechaDesde=2025-12-01&fechaFin=2025-12-31"

# 5. Búsqueda completa con paginación
curl "http://localhost:8081/api/v1/eventos/buscar/avanzada?texto=cultura&ubicacion=Centro&fechaDesde=2025-10-08&gratuito=true&page=0&size=20&ordenarPor=fecha&direccion=ASC"
```

#### **Respuesta ejemplo:**

```json
{
  "content": [
    {
      "id": 1,
      "titulo": "Concierto de Jazz en El Poblado",
      "descripcion": "Evento musical gratuito...",
      "fecha": "2025-10-15",
      "horario": "19:00:00",
      "categoria": "Culturales y Artísticos",
      "modalidad": "PRESENCIAL",
      "valorIngreso": "gratuito",
      "destacado": true,
      "ubicacion": {
        "comunaBarrio": "El Poblado",
        "direccionCompleta": "Parque Lleras, Calle 10 con Carrera 37",
        "direccionDetallada": "Tarima principal del parque"
      },
      "organizador": {
        "nombre": "Secretaría de Cultura",
        "celular": "3001234567",
        "email": "cultura@medellin.gov.co"
      }
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10
  },
  "totalElements": 1,
  "totalPages": 1,
  "last": true,
  "first": true
}
```

---

## 2️⃣ Búsqueda por Palabras Clave

Busca en **múltiples campos** simultáneamente: título, descripción, categoría, organizador y ubicación.

### **GET** `/buscar/keywords`

#### **Parámetros:**

| Parámetro | Tipo | Requerido | Descripción |
|-----------|------|-----------|-------------|
| `q` | String | ✅ Sí | Palabras clave separadas por espacios |
| `page` | Integer | ❌ No | Número de página (default: 0) |
| `size` | Integer | ❌ No | Tamaño de página (default: 10) |

#### **Ejemplos:**

```bash
# Buscar por una palabra
curl "http://localhost:8081/api/v1/eventos/buscar/keywords?q=teatro"

# Buscar por múltiples palabras (AND lógico)
curl "http://localhost:8081/api/v1/eventos/buscar/keywords?q=concierto%20jazz%20gratis"

# Buscar con paginación
curl "http://localhost:8081/api/v1/eventos/buscar/keywords?q=cultura&page=0&size=20"
```

---

## 3️⃣ Búsqueda por Ubicación

Busca eventos **próximos** en una ubicación específica.

### **GET** `/buscar/ubicacion`

#### **Parámetros:**

| Parámetro | Tipo | Requerido | Descripción |
|-----------|------|-----------|-------------|
| `ubicacion` | String | ✅ Sí | Comuna, barrio o parte de dirección |

#### **Ejemplos:**

```bash
# Por comuna
curl "http://localhost:8081/api/v1/eventos/buscar/ubicacion?ubicacion=Comuna%2010"

# Por barrio
curl "http://localhost:8081/api/v1/eventos/buscar/ubicacion?ubicacion=Laureles"
curl "http://localhost:8081/api/v1/eventos/buscar/ubicacion?ubicacion=Poblado"
curl "http://localhost:8081/api/v1/eventos/buscar/ubicacion?ubicacion=Envigado"

# Por parte de dirección
curl "http://localhost:8081/api/v1/eventos/buscar/ubicacion?ubicacion=Parque"
```

---

## 4️⃣ Búsqueda por Fecha Específica

Busca eventos que ocurren en una fecha exacta.

### **GET** `/buscar/fecha`

#### **Parámetros:**

| Parámetro | Tipo | Requerido | Descripción |
|-----------|------|-----------|-------------|
| `fecha` | Date | ✅ Sí | Fecha en formato YYYY-MM-DD |

#### **Ejemplos:**

```bash
# Eventos de hoy
curl "http://localhost:8081/api/v1/eventos/buscar/fecha?fecha=2025-10-08"

# Eventos de una fecha futura
curl "http://localhost:8081/api/v1/eventos/buscar/fecha?fecha=2025-12-24"
```

---

## 5️⃣ Búsqueda por Rango de Fechas

Busca eventos entre dos fechas.

### **GET** `/buscar/rango-fechas`

#### **Parámetros:**

| Parámetro | Tipo | Requerido | Descripción |
|-----------|------|-----------|-------------|
| `fechaDesde` | Date | ❌ No | Fecha inicial (YYYY-MM-DD) |
| `fechaHasta` | Date | ❌ No | Fecha final (YYYY-MM-DD) |

#### **Ejemplos:**

```bash
# Eventos de octubre 2025
curl "http://localhost:8081/api/v1/eventos/buscar/rango-fechas?fechaDesde=2025-10-01&fechaHasta=2025-10-31"

# Eventos desde hoy en adelante (omitir fechaHasta)
curl "http://localhost:8081/api/v1/eventos/buscar/rango-fechas?fechaDesde=2025-10-08"

# Eventos hasta fin de año (omitir fechaDesde)
curl "http://localhost:8081/api/v1/eventos/buscar/rango-fechas?fechaHasta=2025-12-31"

# Eventos del último trimestre
curl "http://localhost:8081/api/v1/eventos/buscar/rango-fechas?fechaDesde=2025-10-01&fechaHasta=2025-12-31"
```

---

## 6️⃣ Eventos Próximos

Lista eventos con fecha mayor o igual a hoy, ordenados por fecha.

### **GET** `/proximos`

#### **Parámetros:**

| Parámetro | Tipo | Requerido | Descripción |
|-----------|------|-----------|-------------|
| `page` | Integer | ❌ No | Número de página (default: 0) |
| `size` | Integer | ❌ No | Tamaño de página (default: 20) |

#### **Ejemplos:**

```bash
# Primeros 20 eventos próximos
curl "http://localhost:8081/api/v1/eventos/proximos"

# 50 eventos próximos
curl "http://localhost:8081/api/v1/eventos/proximos?size=50"

# Segunda página de eventos próximos
curl "http://localhost:8081/api/v1/eventos/proximos?page=1&size=20"
```

---

## 7️⃣ Eventos Gratuitos

Lista eventos próximos con entrada gratuita.

### **GET** `/gratuitos`

#### **Parámetros:**

| Parámetro | Tipo | Requerido | Descripción |
|-----------|------|-----------|-------------|
| `page` | Integer | ❌ No | Número de página (default: 0) |
| `size` | Integer | ❌ No | Tamaño de página (default: 20) |

#### **Ejemplos:**

```bash
# Primeros 20 eventos gratuitos
curl "http://localhost:8081/api/v1/eventos/gratuitos"

# 100 eventos gratuitos
curl "http://localhost:8081/api/v1/eventos/gratuitos?size=100"
```

---

## 8️⃣ Endpoints Existentes (Conservados)

### **GET** `/buscar`
Búsqueda con filtros básicos (versión anterior, sigue funcionando)

```bash
curl "http://localhost:8081/api/v1/eventos/buscar?categoria=Culturales&ubicacion=Poblado&destacado=true&page=0&size=10"
```

### **GET** `/buscar/texto`
Búsqueda de texto completo simple

```bash
curl "http://localhost:8081/api/v1/eventos/buscar/texto?q=concierto"
```

### **GET** `/destacados`
Lista solo eventos destacados

```bash
curl "http://localhost:8081/api/v1/eventos/destacados"
```

---

## 🧪 Casos de Uso Comunes

### **Caso 1: Usuario busca eventos en su barrio este fin de semana**

```bash
curl "http://localhost:8081/api/v1/eventos/buscar/avanzada?ubicacion=Laureles&fechaDesde=2025-10-11&fechaHasta=2025-10-13"
```

### **Caso 2: Usuario busca conciertos gratuitos**

```bash
curl "http://localhost:8081/api/v1/eventos/buscar/avanzada?texto=concierto&gratuito=true&ordenarPor=fecha&direccion=ASC"
```

### **Caso 3: Usuario busca eventos culturales para el próximo mes**

```bash
curl "http://localhost:8081/api/v1/eventos/buscar/avanzada?categoria=Culturales%20y%20Artisticos&fechaDesde=2025-11-01&fechaHasta=2025-11-30"
```

### **Caso 4: Administrador busca eventos virtuales destacados**

```bash
curl "http://localhost:8081/api/v1/eventos/buscar/avanzada?modalidad=VIRTUAL&destacado=true"
```

### **Caso 5: Usuario busca "teatro infantil" en El Poblado**

```bash
curl "http://localhost:8081/api/v1/eventos/buscar/keywords?q=teatro%20infantil%20poblado"
```

---

## 📊 Swagger UI

Accede a la documentación interactiva en:

```
http://localhost:8081/swagger-ui/index.html
```

Desde Swagger UI puedes:
- ✅ Ver todos los endpoints
- ✅ Probar cada endpoint directamente
- ✅ Ver ejemplos de respuestas
- ✅ Validar parámetros

---

## 🔍 Tips de Búsqueda

### **Para búsquedas más precisas:**
1. **Usa comillas** en palabras compuestas: `"El Poblado"` en lugar de `El Poblado`
2. **Codifica espacios** como `%20`: `El%20Poblado`
3. **Combina filtros** para resultados más específicos
4. **Usa paginación** cuando esperes muchos resultados

### **Ordenamiento:**
- `ordenarPor=fecha&direccion=ASC`: Más próximos primero
- `ordenarPor=fecha&direccion=DESC`: Más lejanos primero
- `ordenarPor=titulo&direccion=ASC`: Orden alfabético
- `ordenarPor=destacado&direccion=DESC`: Destacados primero

### **Rendimiento:**
- Usa `size` apropiado (10-50 registros típicamente)
- Especifica `soloActivos=true` para mejorar velocidad
- Combina filtros para reducir resultados

---

## ⚠️ Errores Comunes

### **400 Bad Request**
- Falta parámetro requerido
- Formato de fecha inválido (debe ser YYYY-MM-DD)
- Fecha inicial posterior a fecha final

### **Ejemplo de respuesta de error:**
```json
{
  "error": "Solicitud inválida",
  "mensaje": "La fecha inicial no puede ser posterior a la fecha final",
  "timestamp": 1728379200000
}
```

---

## 🎓 Ejemplos con Postman

### **Colección de Postman:**

```json
{
  "info": {
    "name": "ViveMedellin - Búsqueda de Eventos",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "Búsqueda Avanzada",
      "request": {
        "method": "GET",
        "url": {
          "raw": "http://localhost:8081/api/v1/eventos/buscar/avanzada?ubicacion=Poblado&gratuito=true",
          "protocol": "http",
          "host": ["localhost"],
          "port": "8081",
          "path": ["api", "v1", "eventos", "buscar", "avanzada"],
          "query": [
            {"key": "ubicacion", "value": "Poblado"},
            {"key": "gratuito", "value": "true"}
          ]
        }
      }
    }
  ]
}
```

---

## 📞 Soporte

Para reportar problemas o sugerencias:
- **Email**: desarrollo@vivemedellin.gov.co
- **GitHub**: [Issues](https://github.com/vivemedellin/backend/issues)
- **Documentación**: http://localhost:8081/swagger-ui/index.html

---

## 🔄 Próximas Mejoras

- [ ] Búsqueda por coordenadas GPS (radio de distancia)
- [ ] Filtro por precio (rango de valores)
- [ ] Búsqueda por tags/etiquetas
- [ ] Autocompletar ubicaciones
- [ ] Sugerencias de búsqueda
- [ ] Búsqueda fuzzy (tolerancia a errores de tipeo)

---

**Última actualización:** 8 de octubre de 2025  
**Versión:** 1.0.0  
**Autor:** Equipo ViveMedellin
