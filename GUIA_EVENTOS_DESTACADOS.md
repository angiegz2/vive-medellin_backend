# 🌟 Sistema de Eventos Destacados - Documentación Completa

## 📋 Resumen de Funcionalidades

Se ha implementado un **sistema inteligente de eventos destacados** con las siguientes características:

✅ **Límite de 3 eventos destacados** vigentes simultáneamente  
✅ **Expiración automática** cuando las funciones dejan de ser vigentes  
✅ **Carrusel en pantalla principal** con los eventos destacados  
✅ **Badge/Etiqueta visual** indicando "DESTACADO"  
✅ **Validación inteligente** de fechas y horarios  
✅ **API REST completa** para gestión de destacados  

---

## 🎯 Reglas de Negocio

### 1. Límite de Destacados

- **Máximo 3 eventos destacados vigentes** pueden estar activos simultáneamente
- Se cuentan solo los eventos con **funciones futuras** (fecha >= hoy)
- Eventos con todas las funciones pasadas NO cuentan para el límite

### 2. Vigencia Automática

Un evento destacado **deja de aparecer en el carrusel** automáticamente cuando:

```
TODAS las funciones del evento tienen:
  - Fecha < Hoy, O
  - Fecha = Hoy Y Hora < Hora actual
```

**Ejemplo:**
```
Evento: "Concierto de Rock"
Destacado: true
Funciones:
  - Viernes 11/Oct/2025 20:00 ❌ (pasada)
  - Sábado 12/Oct/2025 14:00 ✅ (vigente - hoy a las 10:00)
  
Resultado: El evento APARECE en el carrusel (tiene al menos 1 función vigente)
```

### 3. Estado en Base de Datos

- El campo `destacado` en la tabla `eventos` NO se modifica automáticamente
- La vigencia se calcula dinámicamente mediante queries SQL
- Los administradores pueden marcar/desmarcar manualmente

---

## 🔌 Endpoints Implementados

### 1. Obtener Eventos Destacados para Carrusel (PÚBLICO)

```http
GET /api/public/eventos/destacados-carrusel
```

**Descripción:** Devuelve hasta 3 eventos destacados con funciones vigentes para mostrar en el carrusel de la pantalla principal.

**No requiere autenticación** ✅

**Respuesta (200):**
```json
{
  "eventos": [
    {
      "id": 1,
      "imagenCaratula": "https://...",
      "titulo": "Festival de Jazz",
      "categoria": "Música",
      "fechaEvento": "2025-10-20",
      "horaEvento": "18:00",
      "ubicacion": "El Poblado",
      "direccionCompleta": "Parque Lleras",
      "nombreOrganizador": "Secretaría de Cultura",
      "valorIngreso": "Gratuito",
      "destacado": true,  // <-- Usar para mostrar badge
      "modalidad": "PRESENCIAL",
      "disponible": true
    },
    {
      "id": 5,
      "titulo": "Obra de Teatro",
      "destacado": true,
      ...
    },
    {
      "id": 8,
      "titulo": "Concierto de Rock",
      "destacado": true,
      ...
    }
  ],
  "cantidad": 3,
  "mensaje": "Eventos destacados cargados exitosamente"
}
```

**Respuesta cuando NO hay destacados:**
```json
{
  "eventos": [],
  "cantidad": 0,
  "mensaje": "No hay eventos destacados vigentes en este momento"
}
```

**Ejemplo con cURL:**
```bash
curl "http://localhost:8081/api/public/eventos/destacados-carrusel"
```

**Ejemplo con JavaScript:**
```javascript
async function cargarCarruselDestacados() {
  const response = await fetch(
    'http://localhost:8081/api/public/eventos/destacados-carrusel'
  );
  const data = await response.json();
  
  if (data.cantidad > 0) {
    mostrarCarrusel(data.eventos);
  } else {
    ocultarCarrusel();
  }
}
```

---

### 2. Destacar Evento (ADMIN)

```http
PUT /api/admin/eventos/{id}/destacar?destacar=true
```

**Descripción:** Marca un evento como destacado. Valida que no haya más de 3 destacados vigentes.

**Requiere autenticación** 🔒 (rol ADMINISTRADOR)

**Validaciones:**
- ✅ El evento debe estar PUBLISHED
- ✅ Solo puede haber máximo 3 destacados vigentes
- ✅ Se cuentan solo eventos con funciones futuras

**Respuesta exitosa (200):**
```json
{
  "success": true,
  "mensaje": "Evento destacado exitosamente",
  "eventoId": 1,
  "destacado": true,
  "cantidadDestacadosActuales": 3,
  "espaciosDisponibles": 0
}
```

**Error - Límite alcanzado (409):**
```json
{
  "success": false,
  "error": "Ya existen 3 eventos destacados activos con fechas vigentes. Debe quitar el destacado de otro evento primero.",
  "cantidadDestacadosActuales": 3,
  "limiteMaximo": 3
}
```

---

### 3. Información de Destacados (ADMIN)

```http
GET /api/admin/eventos/destacados/info
```

**Descripción:** Obtiene información sobre espacios disponibles para destacados.

**Respuesta (200):**
```json
{
  "cantidadDestacados": 2,
  "limiteMaximo": 3,
  "espaciosDisponibles": 1,
  "puedeDestacarMas": true
}
```

---

### 4. Validar si se Puede Destacar (ADMIN)

```http
GET /api/admin/eventos/{id}/puede-destacar
```

**Descripción:** Valida si un evento específico puede ser destacado.

**Respuesta cuando SÍ puede (200):**
```json
{
  "puedeDestacar": true,
  "cantidadDestacados": 2,
  "espaciosDisponibles": 1
}
```

**Respuesta cuando NO puede (200):**
```json
{
  "puedeDestacar": false,
  "razon": "Ya existen 3 eventos destacados con fechas vigentes. Debe quitar el destacado de otro evento primero.",
  "cantidadDestacados": 3,
  "limiteMaximo": 3
}
```

---

## 🎨 Implementación Frontend

### Componente de Carrusel (React)

```jsx
import React, { useState, useEffect } from 'react';
import './Carrusel.css';

function CarruselDestacados() {
  const [eventos, setEventos] = useState([]);
  const [indiceActual, setIndiceActual] = useState(0);
  const [cargando, setCargando] = useState(true);

  useEffect(() => {
    cargarEventosDestacados();
  }, []);

  const cargarEventosDestacados = async () => {
    try {
      const response = await fetch(
        'http://localhost:8081/api/public/eventos/destacados-carrusel'
      );
      const data = await response.json();
      
      if (data.eventos && data.eventos.length > 0) {
        setEventos(data.eventos);
      }
    } catch (error) {
      console.error('Error cargando destacados:', error);
    } finally {
      setCargando(false);
    }
  };

  const siguiente = () => {
    setIndiceActual((prev) => (prev + 1) % eventos.length);
  };

  const anterior = () => {
    setIndiceActual((prev) => (prev - 1 + eventos.length) % eventos.length);
  };

  if (cargando) {
    return <div className="carrusel-loading">Cargando eventos destacados...</div>;
  }

  if (eventos.length === 0) {
    return null; // No mostrar carrusel si no hay eventos
  }

  const eventoActual = eventos[indiceActual];

  return (
    <div className="carrusel-destacados">
      <h2 className="carrusel-titulo">Eventos Destacados</h2>
      
      <div className="carrusel-contenedor">
        {/* Badge "DESTACADO" */}
        {eventoActual.destacado && (
          <div className="badge-destacado">
            ⭐ DESTACADO
          </div>
        )}

        {/* Imagen del evento */}
        <img
          src={eventoActual.imagenCaratula}
          alt={eventoActual.titulo}
          className="carrusel-imagen"
        />

        {/* Información del evento */}
        <div className="carrusel-info">
          <h3>{eventoActual.titulo}</h3>
          <p className="categoria">{eventoActual.categoria}</p>
          <p className="fecha">
            📅 {new Date(eventoActual.fechaEvento).toLocaleDateString('es-CO')}
            {eventoActual.horaEvento && ` - ⏰ ${eventoActual.horaEvento}`}
          </p>
          <p className="ubicacion">📍 {eventoActual.ubicacion}</p>
          <p className="precio">{eventoActual.valorIngreso}</p>
          
          <button 
            className="btn-ver-detalle"
            onClick={() => window.location.href = `/eventos/${eventoActual.id}`}
          >
            Ver Detalle
          </button>
        </div>

        {/* Controles de navegación */}
        {eventos.length > 1 && (
          <>
            <button className="carrusel-btn btn-anterior" onClick={anterior}>
              ‹
            </button>
            <button className="carrusel-btn btn-siguiente" onClick={siguiente}>
              ›
            </button>
          </>
        )}

        {/* Indicadores */}
        <div className="carrusel-indicadores">
          {eventos.map((_, index) => (
            <span
              key={index}
              className={`indicador ${index === indiceActual ? 'activo' : ''}`}
              onClick={() => setIndiceActual(index)}
            />
          ))}
        </div>
      </div>
    </div>
  );
}

export default CarruselDestacados;
```

### CSS para el Carrusel

```css
.carrusel-destacados {
  margin: 2rem 0;
  padding: 2rem;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 16px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
}

.carrusel-titulo {
  color: white;
  text-align: center;
  margin-bottom: 1.5rem;
  font-size: 2rem;
  font-weight: bold;
}

.carrusel-contenedor {
  position: relative;
  max-width: 800px;
  margin: 0 auto;
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
}

/* Badge DESTACADO */
.badge-destacado {
  position: absolute;
  top: 20px;
  right: 20px;
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  color: white;
  padding: 8px 16px;
  border-radius: 20px;
  font-weight: bold;
  font-size: 0.9rem;
  z-index: 10;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.05);
  }
}

.carrusel-imagen {
  width: 100%;
  height: 400px;
  object-fit: cover;
}

.carrusel-info {
  padding: 1.5rem;
}

.carrusel-info h3 {
  font-size: 1.8rem;
  margin-bottom: 0.5rem;
  color: #333;
}

.categoria {
  display: inline-block;
  background: #667eea;
  color: white;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 0.85rem;
  margin-bottom: 1rem;
}

.fecha, .ubicacion, .precio {
  margin: 0.5rem 0;
  color: #666;
  font-size: 1rem;
}

.btn-ver-detalle {
  width: 100%;
  padding: 12px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 1rem;
  font-weight: bold;
  cursor: pointer;
  margin-top: 1rem;
  transition: transform 0.2s;
}

.btn-ver-detalle:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

/* Botones de navegación */
.carrusel-btn {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  background: rgba(255, 255, 255, 0.9);
  border: none;
  width: 50px;
  height: 50px;
  border-radius: 50%;
  font-size: 2rem;
  cursor: pointer;
  z-index: 10;
  transition: all 0.3s;
  color: #667eea;
}

.carrusel-btn:hover {
  background: white;
  transform: translateY(-50%) scale(1.1);
}

.btn-anterior {
  left: 10px;
}

.btn-siguiente {
  right: 10px;
}

/* Indicadores */
.carrusel-indicadores {
  display: flex;
  justify-content: center;
  gap: 10px;
  padding: 1rem;
}

.indicador {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: #ddd;
  cursor: pointer;
  transition: all 0.3s;
}

.indicador.activo {
  background: #667eea;
  transform: scale(1.3);
}

/* Loading */
.carrusel-loading {
  text-align: center;
  padding: 3rem;
  color: #666;
  font-size: 1.2rem;
}

/* Responsive */
@media (max-width: 768px) {
  .carrusel-imagen {
    height: 250px;
  }
  
  .carrusel-info h3 {
    font-size: 1.4rem;
  }
  
  .carrusel-btn {
    width: 40px;
    height: 40px;
    font-size: 1.5rem;
  }
}
```

---

### Badge "DESTACADO" en Tarjetas de Evento

Para mostrar el badge en las tarjetas de búsqueda:

```jsx
function TarjetaEvento({ evento }) {
  return (
    <div className="tarjeta-evento">
      {/* Badge destacado */}
      {evento.destacado && (
        <div className="badge-destacado-small">
          ⭐ Destacado
        </div>
      )}
      
      <img src={evento.imagenCaratula} alt={evento.titulo} />
      <div className="tarjeta-contenido">
        <h3>{evento.titulo}</h3>
        <p>{evento.categoria}</p>
        <p>{evento.fechaEvento}</p>
        <p>{evento.ubicacion}</p>
      </div>
    </div>
  );
}
```

```css
.badge-destacado-small {
  position: absolute;
  top: 10px;
  right: 10px;
  background: linear-gradient(135deg, #FFD700 0%, #FFA500 100%);
  color: white;
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 0.75rem;
  font-weight: bold;
  z-index: 5;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
}
```

---

## 🔍 Queries SQL Implementadas

### Query 1: Contar Destacados Vigentes

```sql
SELECT COUNT(DISTINCT e) 
FROM eventos e 
JOIN funciones f ON f.evento_id = e.id
WHERE e.destacado = true 
  AND e.status = 'PUBLISHED' 
  AND (
    f.fecha > CURRENT_DATE 
    OR (f.fecha = CURRENT_DATE AND f.horario > CURRENT_TIME)
  )
```

### Query 2: Obtener Destacados Vigentes

```sql
SELECT DISTINCT e.* 
FROM eventos e 
JOIN funciones f ON f.evento_id = e.id
WHERE e.destacado = true 
  AND e.status = 'PUBLISHED' 
  AND (
    f.fecha > CURRENT_DATE 
    OR (f.fecha = CURRENT_DATE AND f.horario > CURRENT_TIME)
  )
ORDER BY e.updated_at DESC
LIMIT 3
```

---

## 📊 Casos de Uso

### Caso 1: Usuario Visita la Página Principal

1. Frontend llama a `/api/public/eventos/destacados-carrusel`
2. Backend ejecuta query de eventos destacados vigentes
3. Devuelve máximo 3 eventos
4. Frontend muestra carrusel con badge "DESTACADO"
5. Usuario puede navegar entre los eventos

### Caso 2: Administrador Destaca un Evento

1. Admin abre detalle del evento
2. Clic en botón "Destacar"
3. Frontend llama a `PUT /api/admin/eventos/{id}/destacar?destacar=true`
4. Backend valida que no haya 3 destacados vigentes
5. Si pasa validación, marca el evento como destacado
6. Evento aparece en el carrusel de la página principal

### Caso 3: Evento Destacado Expira Automáticamente

**Situación inicial:**
- Evento ID 5 está destacado
- Tiene 2 funciones: Viernes 11/Oct y Sábado 12/Oct
- Aparece en el carrusel

**Domingo 13/Oct (todas las funciones pasaron):**
- Query de destacados vigentes NO incluye el evento ID 5
- El evento desaparece automáticamente del carrusel
- El campo `destacado` en BD sigue siendo `true` (no se modifica)
- Un nuevo evento puede ser destacado (espacio disponible)

---

## 🧪 Testing

### Test 1: Carrusel con 3 Destacados

```bash
# Destacar 3 eventos
curl -X PUT "http://localhost:8081/api/admin/eventos/1/destacar?destacar=true"
curl -X PUT "http://localhost:8081/api/admin/eventos/2/destacar?destacar=true"
curl -X PUT "http://localhost:8081/api/admin/eventos/3/destacar?destacar=true"

# Obtener carrusel
curl "http://localhost:8081/api/public/eventos/destacados-carrusel"
```

**Esperado:** 3 eventos en el carrusel

### Test 2: Intentar Destacar un Cuarto

```bash
# Intentar destacar evento 4
curl -X PUT "http://localhost:8081/api/admin/eventos/4/destacar?destacar=true"
```

**Esperado:** Error 409 - "Ya existen 3 eventos destacados..."

### Test 3: Evento Expira Automáticamente

1. Destacar un evento con función hoy a las 10:00
2. Esperar hasta las 10:01
3. Llamar al endpoint del carrusel

**Esperado:** El evento NO aparece en el carrusel

---

## ✅ Checklist de Implementación

- [x] Query para contar destacados vigentes
- [x] Query para obtener destacados vigentes (carrusel)
- [x] Endpoint público `/destacados-carrusel`
- [x] Validación de límite de 3 en endpoints admin
- [x] Campo `destacado` en EventoMosaicoDTO
- [x] Campo `destacado` en EventoListaDTO
- [x] Componente React de carrusel
- [x] CSS con badge "DESTACADO"
- [x] Documentación completa
- [ ] Tests unitarios (pendiente)
- [ ] Tests de integración (pendiente)

---

**Fecha de implementación**: Octubre 2025  
**Versión**: 2.0  
**Estado**: ✅ Completo y funcional
