# ⚡ GUÍA RÁPIDA DE CONFIGURACIÓN Y PRUEBAS

## 🔧 **Requisitos Previos**

- PostgreSQL 18 instalado
- Java 23 configurado
- Maven instalado
- Base de datos `vivemedellin` creada

---

## 📝 **PASO 1: Habilitar Extensión PostgreSQL (CRÍTICO)**

La búsqueda sin acentos requiere la extensión `unaccent` de PostgreSQL.

### **Opción A: Desde pgAdmin**

1. Abrir pgAdmin
2. Conectar a la base de datos `vivemedellin`
3. Ir a **Tools → Query Tool**
4. Ejecutar:

```sql
CREATE EXTENSION IF NOT EXISTS unaccent;
```

5. Verificar instalación:

```sql
SELECT unaccent('Música');
-- Resultado esperado: Musica
```

### **Opción B: Desde PowerShell**

```powershell
# Conectar a PostgreSQL y ejecutar
psql -U postgres -d vivemedellin -c "CREATE EXTENSION IF NOT EXISTS unaccent;"
```

### **Opción C: Agregar a `application.properties`**

Si tienes permisos en el usuario de base de datos:

```properties
spring.jpa.properties.hibernate.jdbc.lob.non_contextual_creation=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.sql.init.mode=always
```

Crear archivo `src/main/resources/schema.sql`:

```sql
CREATE EXTENSION IF NOT EXISTS unaccent;
```

---

## 🚀 **PASO 2: Iniciar el Servidor**

```powershell
# Desde el directorio raíz del proyecto
./mvnw.cmd clean install
./mvnw.cmd spring-boot:run
```

Esperar hasta ver:

```
Started ViveMedellinApplication in X seconds
```

---

## 🧪 **PASO 3: Verificar que el Servidor está Activo**

Abrir en el navegador:

```
http://localhost:8081/swagger-ui/index.html
```

Deberías ver la interfaz de Swagger con la sección:

**"Búsqueda Pública de Eventos (evento-public-controller)"**

---

## 🔍 **PASO 4: Probar los Endpoints**

### **Prueba 1: Búsqueda Simple**

**Desde PowerShell:**

```powershell
curl "http://localhost:8081/api/public/eventos/buscar-simple?q=concierto"
```

**Desde el navegador:**

```
http://localhost:8081/api/public/eventos/buscar-simple?q=concierto
```

**Resultado esperado:**
- Si hay eventos con "concierto": JSON con resultados
- Si no hay eventos: `{"mensaje": "No se encontraron eventos...", "totalResultados": 0}`

---

### **Prueba 2: Búsqueda con Acentos**

**Sin acento:**

```powershell
curl "http://localhost:8081/api/public/eventos/buscar-simple?q=musica"
```

**Con acento:**

```powershell
curl "http://localhost:8081/api/public/eventos/buscar-simple?q=música"
```

**Resultado esperado:** Ambas búsquedas deben devolver los mismos resultados.

---

### **Prueba 3: Búsqueda Avanzada con Filtros**

```powershell
curl "http://localhost:8081/api/public/eventos/buscar?categoria=Música&gratuito=true&tipoVista=MOSAICO"
```

**Resultado esperado:**
- JSON con paginación
- Campo `pageSize: 20` (vista mosaico)

---

### **Prueba 4: Vista de Lista (50 resultados)**

```powershell
curl "http://localhost:8081/api/public/eventos/buscar?tipoVista=LISTA"
```

**Resultado esperado:**
- Campo `pageSize: 50` (vista lista)

---

### **Prueba 5: Obtener Detalle de un Evento**

```powershell
curl "http://localhost:8081/api/public/eventos/1"
```

**Resultado esperado:**
- JSON completo con toda la información del evento
- O `404 Not Found` si el evento no existe

---

### **Prueba 6: Eventos Destacados**

```powershell
curl "http://localhost:8081/api/public/eventos/destacados"
```

---

### **Prueba 7: Eventos Próximos (30 días)**

```powershell
curl "http://localhost:8081/api/public/eventos/proximos?dias=30"
```

---

## 🐞 **PASO 5: Resolver Problemas Comunes**

### **Error: "unaccent function does not exist"**

**Causa:** La extensión no está instalada.

**Solución:**

```sql
-- Conectar a la base de datos y ejecutar:
CREATE EXTENSION IF NOT EXISTS unaccent;
```

---

### **Error: Puerto 8081 en uso**

**Solución:** Cambiar el puerto en `application.properties`:

```properties
server.port=8082
```

---

### **Error: Cannot connect to database**

**Verificar:**

1. PostgreSQL está corriendo:

```powershell
Get-Service postgresql*
```

2. Credenciales en `application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/vivemedellin
spring.datasource.username=tu_usuario
spring.datasource.password=tu_contraseña
```

---

### **Error: Compilation errors**

```powershell
# Limpiar y reconstruir
./mvnw.cmd clean compile
```

---

## 📊 **PASO 6: Crear Datos de Prueba**

Si no tienes eventos en la base de datos, crea algunos con Swagger UI:

1. Ir a `http://localhost:8081/swagger-ui/index.html`
2. Buscar **"evento-controller"**
3. Usar el endpoint `POST /api/eventos`
4. Ejemplo de JSON:

```json
{
  "titulo": "Concierto de Rock en El Poblado",
  "descripcion": "Festival de música rock al aire libre",
  "fechaEvento": "2025-11-15",
  "horaEvento": "19:00:00",
  "ubicacion": "El Poblado",
  "categoria": "Música",
  "modalidad": "PRESENCIAL",
  "valorIngreso": "Gratuito",
  "destacado": true,
  "organizadorId": 1
}
```

---

## ✅ **PASO 7: Verificación Final**

Ejecuta estas pruebas para asegurar que todo funciona:

### **Checklist de Verificación:**

- [ ] Servidor inicia sin errores
- [ ] Swagger UI se carga correctamente
- [ ] Búsqueda simple devuelve resultados o mensaje de "no encontrado"
- [ ] Búsqueda con acento funciona igual que sin acento
- [ ] Vista MOSAICO devuelve 20 resultados por página
- [ ] Vista LISTA devuelve 50 resultados por página
- [ ] Detalle de evento devuelve información completa
- [ ] Filtros combinados funcionan correctamente
- [ ] Eventos destacados se obtienen correctamente
- [ ] Eventos próximos se filtran por fecha

---

## 📱 **PASO 8: Pruebas desde Frontend**

### **Ejemplo con JavaScript:**

Crear archivo `test-busqueda.html`:

```html
<!DOCTYPE html>
<html>
<head>
    <title>Test Búsqueda Eventos</title>
</head>
<body>
    <h1>Buscador de Eventos</h1>
    
    <input type="text" id="busqueda" placeholder="Buscar eventos...">
    <button onclick="buscar()">Buscar</button>
    
    <div id="resultados"></div>
    
    <script>
        async function buscar() {
            const query = document.getElementById('busqueda').value;
            const response = await fetch(`http://localhost:8081/api/public/eventos/buscar-simple?q=${query}`);
            const data = await response.json();
            
            const resultadosDiv = document.getElementById('resultados');
            
            if (data.mensaje) {
                resultadosDiv.innerHTML = `<p>${data.mensaje}</p>`;
            } else {
                resultadosDiv.innerHTML = data.content.map(evento => `
                    <div>
                        <h3>${evento.titulo}</h3>
                        <p>${evento.ubicacion} - ${evento.fechaEvento}</p>
                    </div>
                `).join('');
            }
        }
    </script>
</body>
</html>
```

Abrir el archivo en el navegador y probar.

---

## 🎯 **Resumen de URLs Importantes**

| Descripción | URL |
|-------------|-----|
| **Swagger UI** | http://localhost:8081/swagger-ui/index.html |
| **Búsqueda simple** | http://localhost:8081/api/public/eventos/buscar-simple?q=texto |
| **Búsqueda avanzada** | http://localhost:8081/api/public/eventos/buscar |
| **Detalle evento** | http://localhost:8081/api/public/eventos/{id} |
| **Eventos destacados** | http://localhost:8081/api/public/eventos/destacados |
| **Eventos próximos** | http://localhost:8081/api/public/eventos/proximos?dias=30 |

---

## 📞 **Siguiente Paso: Integración Frontend**

Después de verificar que todos los endpoints funcionan correctamente:

1. **React/Vue/Angular:** Crear componente de búsqueda usando `fetch()` o `axios`
2. **Diseño:** Implementar vista de mosaico (grid de cards) y lista (tabla)
3. **Interactividad:** Al hacer clic en un evento, redirigir a `/eventos/{id}`
4. **Filtros:** Agregar selectores para categoría, ubicación, fecha, etc.

---

✅ **¡Sistema listo para pruebas!** 🎉

**Próximo paso:** Probar desde Swagger UI y verificar que la extensión `unaccent` esté habilitada.