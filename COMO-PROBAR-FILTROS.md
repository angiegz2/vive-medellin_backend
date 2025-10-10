# 🎯 GUÍA RÁPIDA - PROBAR FILTROS DE BÚSQUEDA

## ✅ **Opción 1: Usa Swagger UI (MÁS FÁCIL)** ⭐ RECOMENDADO

1. **Abre tu navegador en:**
   ```
   http://localhost:8081/swagger-ui/index.html
   ```

2. **Busca la sección:** `evento-controller`

3. **Haz clic en cualquier endpoint de búsqueda:**
   - `/api/eventos/buscar/avanzada` - Búsqueda combinada
   - `/api/eventos/buscar/keywords` - Por palabras clave
   - `/api/eventos/buscar/ubicacion` - Por ubicación
   - `/api/eventos/gratuitos` - Solo eventos gratis
   - `/api/eventos/proximos` - Próximos eventos

4. **Clic en "Try it out"** → Llena parámetros → **"Execute"**

---

## ✅ **Opción 2: Script Automático de Prueba**

Ejecuta este comando en PowerShell:

```powershell
cd c:\Users\carlo\Desktop\ViveMedellin
.\scripts-prueba\test-filtros-busqueda.ps1
```

**Este script:**
- ✅ Crea automáticamente organizadores, ubicaciones y categorías
- ✅ Inserta 6 eventos de prueba variados
- ✅ Ejecuta todos los 7 filtros de búsqueda
- ✅ Muestra resultados coloridos en la consola

---

## 🔍 **Ejemplos Directos con cURL**

### 1. Eventos Gratuitos
```powershell
curl "http://localhost:8081/api/eventos/gratuitos"
```

### 2. Buscar por Ubicación
```powershell
curl "http://localhost:8081/api/eventos/buscar/ubicacion?ubicacion=El Poblado"
```

### 3. Buscar por Palabras Clave
```powershell
curl "http://localhost:8081/api/eventos/buscar/keywords?palabras=rock,festival"
```

### 4. Eventos Próximos (30 días)
```powershell
curl "http://localhost:8081/api/eventos/proximos?dias=30"
```

### 5. Búsqueda por Fecha
```powershell
curl "http://localhost:8081/api/eventos/buscar/fecha?fecha=2025-10-25"
```

### 6. Rango de Fechas
```powershell
curl "http://localhost:8081/api/eventos/buscar/rango-fechas?inicio=2025-10-20&fin=2025-10-31"
```

### 7. Búsqueda Avanzada (Múltiples Filtros)
```powershell
curl "http://localhost:8081/api/eventos/buscar/avanzada?categoria=Música&esGratuito=true&fechaInicio=2025-10-01&fechaFin=2025-10-31"
```

---

## 📊 **Filtros Disponibles en Búsqueda Avanzada**

Puedes combinar todos estos parámetros:

| Parámetro | Tipo | Ejemplo | Descripción |
|-----------|------|---------|-------------|
| `texto` | String | `festival` | Busca en título y descripción |
| `ubicacion` | String | `El Poblado` | Comuna o barrio |
| `categoria` | String | `Música` | Categoría del evento |
| `fechaInicio` | Date | `2025-10-01` | Fecha desde (YYYY-MM-DD) |
| `fechaFin` | Date | `2025-10-31` | Fecha hasta (YYYY-MM-DD) |
| `esGratuito` | Boolean | `true` | Solo eventos gratis |
| `modalidad` | String | `PRESENCIAL` | PRESENCIAL/VIRTUAL/HIBRIDA |
| `precioMinimo` | Number | `0` | Precio mínimo |
| `precioMaximo` | Number | `50000` | Precio máximo |

---

## 🎯 **RECOMENDACIÓN: Comienza con Swagger UI**

Es la forma más fácil de probar los filtros visualmente sin escribir código.

**URL:** http://localhost:8081/swagger-ui/index.html

¡Ya puedes filtrar y buscar tus eventos! 🚀
