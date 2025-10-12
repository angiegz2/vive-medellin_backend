# 🎨 GUÍA DE INTEGRACIÓN FRONTEND - PÁGINA DE DETALLE DEL EVENTO

## 📋 **Resumen**

Esta guía explica cómo integrar la página de detalle del evento en tu aplicación frontend (React, Angular, Vue, etc.).

---

## 🎯 **Flujo de Usuario**

```
1. Usuario ve listado de eventos (Mosaico o Lista)
   ↓
2. Usuario hace clic en:
   - Título del evento
   - Imagen del evento
   - Botón "Ver más"
   ↓
3. Frontend navega a /eventos/{id}
   ↓
4. Frontend llama a GET /api/public/eventos/{id}
   ↓
5. Backend devuelve EventoDetalleDTO completo
   ↓
6. Frontend renderiza página de detalle
   ↓
7. Usuario puede:
   - Ver toda la información
   - Abrir Google Maps
   - Regresar a resultados (botón atrás)
```

---

## 🔌 **Endpoint a Consumir**

```http
GET /api/public/eventos/{id}
```

**Características:**
- ✅ No requiere autenticación
- ✅ Devuelve información completa del evento
- ✅ Incluye estado (ACTIVO, CANCELADO, FINALIZADO)
- ✅ Incluye enlace a Google Maps generado automáticamente

---

## 📱 **Ejemplo de Integración por Framework**

### **React (con React Router)**

#### **1. Configurar Ruta**

```jsx
// App.jsx
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import ResultadosBusqueda from './pages/ResultadosBusqueda';
import DetalleEvento from './pages/DetalleEvento';

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<ResultadosBusqueda />} />
        <Route path="/eventos/:id" element={<DetalleEvento />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
```

#### **2. Componente de Resultado con Navegación**

```jsx
// components/TarjetaEvento.jsx
import { useNavigate } from 'react-router-dom';

function TarjetaEvento({ evento }) {
  const navigate = useNavigate();
  
  const verDetalle = () => {
    navigate(`/eventos/${evento.id}`);
  };
  
  return (
    <div className="tarjeta-evento">
      {/* Imagen clickeable */}
      <img 
        src={evento.imagenCaratula} 
        alt={evento.titulo}
        onClick={verDetalle}
        style={{ cursor: 'pointer' }}
      />
      
      {/* Título clickeable */}
      <h3 onClick={verDetalle} style={{ cursor: 'pointer' }}>
        {evento.titulo}
      </h3>
      
      <p>{evento.ubicacion}</p>
      <p>{evento.fechaEvento} - {evento.horaEvento}</p>
      <p>{evento.valorIngreso}</p>
      
      {/* Botón "Ver más" */}
      <button onClick={verDetalle}>Ver más detalles</button>
    </div>
  );
}

export default TarjetaEvento;
```

#### **3. Página de Detalle Completa**

```jsx
// pages/DetalleEvento.jsx
import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';

function DetalleEvento() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [evento, setEvento] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  
  useEffect(() => {
    cargarEvento();
  }, [id]);
  
  const cargarEvento = async () => {
    try {
      setLoading(true);
      const response = await fetch(`/api/public/eventos/${id}`);
      
      if (!response.ok) {
        throw new Error('Evento no encontrado');
      }
      
      const data = await response.json();
      setEvento(data);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };
  
  const volver = () => {
    navigate(-1); // Regresa a la página anterior
  };
  
  if (loading) return <div>Cargando...</div>;
  if (error) return <div>Error: {error}</div>;
  if (!evento) return null;
  
  return (
    <div className="detalle-evento">
      {/* Botón para volver */}
      <button onClick={volver} className="btn-volver">
        ← Volver a resultados
      </button>
      
      {/* Alerta de cancelación o finalización */}
      {evento.estadoEvento === 'CANCELADO' && (
        <div className="alerta alerta-cancelado">
          ⚠️ {evento.mensajeEstado}
        </div>
      )}
      
      {evento.estadoEvento === 'FINALIZADO' && (
        <div className="alerta alerta-finalizado">
          ℹ️ {evento.mensajeEstado}
        </div>
      )}
      
      {/* Imagen de carátula */}
      <img 
        src={evento.imagenCaratula} 
        alt={evento.titulo}
        className="imagen-caratula"
      />
      
      {/* Información básica */}
      <h1>{evento.titulo}</h1>
      <span className="categoria">{evento.categoria}</span>
      {evento.destacado && <span className="badge-destacado">⭐ Destacado</span>}
      
      {/* Descripción */}
      <div className="descripcion">
        <h2>Descripción</h2>
        <p>{evento.descripcion}</p>
      </div>
      
      {/* Funciones (fechas y horarios) */}
      <div className="funciones">
        <h2>📅 Fechas y Horarios</h2>
        {evento.funciones.map(funcion => (
          <div key={funcion.id} className={`funcion ${funcion.estaFinalizada ? 'finalizada' : ''}`}>
            <span className="dia">{funcion.dia}</span>
            <span className="hora">{funcion.horario}</span>
            {funcion.estaFinalizada && <span className="tag-finalizada">Finalizada</span>}
          </div>
        ))}
      </div>
      
      {/* Ubicación con mapa */}
      <div className="ubicacion">
        <h2>📍 Ubicación</h2>
        <p><strong>{evento.ubicacion.direccionCompleta}</strong></p>
        <p>{evento.ubicacion.comunaBarrio}, {evento.ubicacion.ciudad}</p>
        <p>{evento.ubicacion.indicacionesAcceso}</p>
        
        {/* Botón para abrir Google Maps */}
        <a 
          href={evento.ubicacion.enlaceMapa} 
          target="_blank" 
          rel="noopener noreferrer"
          className="btn-mapa"
        >
          🗺️ Ver en Google Maps
        </a>
      </div>
      
      {/* Capacidad y precio */}
      <div className="info-adicional">
        <div className="aforo">
          <h3>👥 Capacidad</h3>
          <p>{evento.aforo} personas</p>
        </div>
        
        <div className="precio">
          <h3>💰 Entrada</h3>
          <p className={evento.esGratuito ? 'gratuito' : ''}>
            {evento.valorIngreso}
          </p>
        </div>
        
        <div className="modalidad">
          <h3>📌 Modalidad</h3>
          <p>{evento.modalidad}</p>
        </div>
      </div>
      
      {/* Organizador */}
      <div className="organizador">
        <h2>👤 Organizador</h2>
        <p><strong>{evento.organizador.nombre}</strong></p>
        <p>📧 {evento.organizador.email}</p>
        <p>📞 {evento.organizador.telefono}</p>
        {evento.organizador.sitioWeb && (
          <a href={evento.organizador.sitioWeb} target="_blank">
            🌐 Sitio web
          </a>
        )}
      </div>
      
      {/* Galería de imágenes */}
      {evento.imagenes.length > 0 && (
        <div className="galeria">
          <h2>🖼️ Galería</h2>
          <div className="grid-imagenes">
            {evento.imagenes.map((img, index) => (
              <img key={index} src={img} alt={`Imagen ${index + 1}`} />
            ))}
          </div>
        </div>
      )}
      
      {/* Videos */}
      {evento.videos.length > 0 && (
        <div className="videos">
          <h2>🎥 Videos</h2>
          {evento.videos.map((video, index) => (
            <a key={index} href={video} target="_blank">
              Video {index + 1}
            </a>
          ))}
        </div>
      )}
      
      {/* Información complementaria */}
      {evento.requisitos && (
        <div className="requisitos">
          <h3>📋 Requisitos</h3>
          <p>{evento.requisitos}</p>
        </div>
      )}
      
      {evento.recomendaciones && (
        <div className="recomendaciones">
          <h3>💡 Recomendaciones</h3>
          <p>{evento.recomendaciones}</p>
        </div>
      )}
      
      {evento.informacionAdicional && (
        <div className="servicios">
          <h3>✨ Servicios adicionales</h3>
          <p>{evento.informacionAdicional}</p>
        </div>
      )}
      
      {/* Botón para volver (también al final) */}
      <button onClick={volver} className="btn-volver-abajo">
        ← Volver a resultados
      </button>
    </div>
  );
}

export default DetalleEvento;
```

---

### **Angular**

#### **1. Configurar Ruta**

```typescript
// app-routing.module.ts
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ResultadosComponent } from './resultados/resultados.component';
import { DetalleEventoComponent } from './detalle-evento/detalle-evento.component';

const routes: Routes = [
  { path: '', component: ResultadosComponent },
  { path: 'eventos/:id', component: DetalleEventoComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
```

#### **2. Servicio para Eventos**

```typescript
// services/evento.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface EventoDetalle {
  id: number;
  titulo: string;
  descripcion: string;
  // ... otros campos según EventoDetalleDTO
}

@Injectable({
  providedIn: 'root'
})
export class EventoService {
  private apiUrl = '/api/public/eventos';
  
  constructor(private http: HttpClient) {}
  
  obtenerDetalle(id: number): Observable<EventoDetalle> {
    return this.http.get<EventoDetalle>(`${this.apiUrl}/${id}`);
  }
}
```

#### **3. Componente de Detalle**

```typescript
// detalle-evento/detalle-evento.component.ts
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { EventoService, EventoDetalle } from '../services/evento.service';

@Component({
  selector: 'app-detalle-evento',
  templateUrl: './detalle-evento.component.html',
  styleUrls: ['./detalle-evento.component.css']
})
export class DetalleEventoComponent implements OnInit {
  evento: EventoDetalle | null = null;
  loading = true;
  error: string | null = null;
  
  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private eventoService: EventoService
  ) {}
  
  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.cargarEvento(id);
  }
  
  cargarEvento(id: number): void {
    this.eventoService.obtenerDetalle(id).subscribe({
      next: (data) => {
        this.evento = data;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Evento no encontrado';
        this.loading = false;
      }
    });
  }
  
  volver(): void {
    this.router.navigate(['../'], { relativeTo: this.route });
  }
}
```

#### **4. Template HTML**

```html
<!-- detalle-evento.component.html -->
<div class="detalle-evento" *ngIf="!loading && evento">
  <!-- Botón volver -->
  <button (click)="volver()" class="btn-volver">
    ← Volver a resultados
  </button>
  
  <!-- Alerta de estado -->
  <div *ngIf="evento.estadoEvento === 'CANCELADO'" class="alerta-cancelado">
    ⚠️ {{ evento.mensajeEstado }}
  </div>
  
  <!-- Información del evento -->
  <img [src]="evento.imagenCaratula" [alt]="evento.titulo" class="imagen-caratula">
  <h1>{{ evento.titulo }}</h1>
  <p>{{ evento.descripcion }}</p>
  
  <!-- Funciones -->
  <div class="funciones">
    <h2>📅 Fechas y Horarios</h2>
    <div *ngFor="let funcion of evento.funciones" 
         [class.finalizada]="funcion.estaFinalizada">
      <span>{{ funcion.dia }}</span>
      <span>{{ funcion.horario }}</span>
    </div>
  </div>
  
  <!-- Ubicación -->
  <div class="ubicacion">
    <h2>📍 Ubicación</h2>
    <p>{{ evento.ubicacion.direccionCompleta }}</p>
    <a [href]="evento.ubicacion.enlaceMapa" target="_blank" class="btn-mapa">
      🗺️ Ver en Google Maps
    </a>
  </div>
  
  <!-- Resto de la información... -->
</div>

<div *ngIf="loading">Cargando...</div>
<div *ngIf="error">{{ error }}</div>
```

---

### **Vue.js 3**

#### **1. Configurar Router**

```javascript
// router/index.js
import { createRouter, createWebHistory } from 'vue-router';
import Resultados from '../views/Resultados.vue';
import DetalleEvento from '../views/DetalleEvento.vue';

const routes = [
  { path: '/', component: Resultados },
  { path: '/eventos/:id', component: DetalleEvento }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

export default router;
```

#### **2. Composable para API**

```javascript
// composables/useEventos.js
import { ref } from 'vue';

export function useEventos() {
  const obtenerDetalle = async (id) => {
    const response = await fetch(`/api/public/eventos/${id}`);
    if (!response.ok) {
      throw new Error('Evento no encontrado');
    }
    return await response.json();
  };
  
  return { obtenerDetalle };
}
```

#### **3. Componente de Detalle**

```vue
<!-- views/DetalleEvento.vue -->
<template>
  <div class="detalle-evento">
    <!-- Botón volver -->
    <button @click="volver" class="btn-volver">
      ← Volver a resultados
    </button>
    
    <!-- Alerta -->
    <div v-if="evento?.estadoEvento === 'CANCELADO'" class="alerta-cancelado">
      ⚠️ {{ evento.mensajeEstado }}
    </div>
    
    <!-- Contenido -->
    <div v-if="!loading && evento">
      <img :src="evento.imagenCaratula" :alt="evento.titulo" class="imagen-caratula">
      <h1>{{ evento.titulo }}</h1>
      <p>{{ evento.descripcion }}</p>
      
      <!-- Funciones -->
      <div class="funciones">
        <h2>📅 Fechas y Horarios</h2>
        <div 
          v-for="funcion in evento.funciones" 
          :key="funcion.id"
          :class="{ finalizada: funcion.estaFinalizada }"
        >
          <span>{{ funcion.dia }}</span>
          <span>{{ funcion.horario }}</span>
        </div>
      </div>
      
      <!-- Ubicación -->
      <div class="ubicacion">
        <h2>📍 Ubicación</h2>
        <p>{{ evento.ubicacion.direccionCompleta }}</p>
        <a :href="evento.ubicacion.enlaceMapa" target="_blank" class="btn-mapa">
          🗺️ Ver en Google Maps
        </a>
      </div>
      
      <!-- Resto de la información... -->
    </div>
    
    <div v-if="loading">Cargando...</div>
    <div v-if="error">{{ error }}</div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useEventos } from '../composables/useEventos';

const route = useRoute();
const router = useRouter();
const { obtenerDetalle } = useEventos();

const evento = ref(null);
const loading = ref(true);
const error = ref(null);

onMounted(async () => {
  try {
    const id = route.params.id;
    evento.value = await obtenerDetalle(id);
  } catch (err) {
    error.value = err.message;
  } finally {
    loading.value = false;
  }
});

const volver = () => {
  router.back();
};
</script>
```

---

## 🎨 **Estilos CSS Recomendados**

```css
/* Alertas de estado */
.alerta {
  padding: 15px 20px;
  margin: 20px 0;
  border-radius: 8px;
  font-weight: bold;
  text-align: center;
}

.alerta-cancelado {
  background-color: #fee;
  border: 2px solid #f00;
  color: #c00;
}

.alerta-finalizado {
  background-color: #e8f4f8;
  border: 2px solid #2196F3;
  color: #1976D2;
}

/* Botón volver */
.btn-volver {
  padding: 10px 20px;
  background: #f5f5f5;
  border: 1px solid #ddd;
  border-radius: 5px;
  cursor: pointer;
  margin-bottom: 20px;
}

.btn-volver:hover {
  background: #e0e0e0;
}

/* Funciones finalizadas */
.funcion.finalizada {
  opacity: 0.5;
  text-decoration: line-through;
}

/* Botón de mapa */
.btn-mapa {
  display: inline-block;
  padding: 12px 24px;
  background: #4285F4;
  color: white;
  text-decoration: none;
  border-radius: 5px;
  margin-top: 10px;
}

.btn-mapa:hover {
  background: #3367D6;
}

/* Galería de imágenes */
.grid-imagenes {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 10px;
  margin-top: 20px;
}

.grid-imagenes img {
  width: 100%;
  height: 200px;
  object-fit: cover;
  border-radius: 8px;
  cursor: pointer;
}

/* Responsive */
@media (max-width: 768px) {
  .grid-imagenes {
    grid-template-columns: repeat(2, 1fr);
  }
}
```

---

## ✅ **Checklist de Implementación**

- [ ] Configurar rutas en el router
- [ ] Crear componente de detalle
- [ ] Hacer componentes clickeables (título, imagen, botón)
- [ ] Implementar botón "Volver"
- [ ] Mostrar alerta si evento está CANCELADO
- [ ] Mostrar etiqueta si evento está FINALIZADO
- [ ] Renderizar todas las funciones (fechas/horarios)
- [ ] Mostrar botón "Ver en Google Maps"
- [ ] Implementar galería de imágenes (carrusel o grid)
- [ ] Mostrar información del organizador con contactos
- [ ] Diseño responsive para móvil
- [ ] Loading state mientras carga
- [ ] Error state si no se encuentra el evento

---

## 🚀 **Optimizaciones Adicionales**

### **1. Caché de Datos**

```javascript
// Guardar en sessionStorage para volver rápido
sessionStorage.setItem('evento-' + id, JSON.stringify(evento));

// Al volver, recuperar del caché
const cached = sessionStorage.getItem('evento-' + id);
if (cached) {
  setEvento(JSON.parse(cached));
}
```

### **2. SEO (Server-Side Rendering)**

```javascript
// Next.js - getServerSideProps
export async function getServerSideProps({ params }) {
  const res = await fetch(`http://localhost:8081/api/public/eventos/${params.id}`);
  const evento = await res.json();
  
  return { props: { evento } };
}
```

### **3. Meta Tags Dinámicos**

```jsx
// React Helmet
import { Helmet } from 'react-helmet';

<Helmet>
  <title>{evento.titulo} - Vive Medellín</title>
  <meta name="description" content={evento.descripcion} />
  <meta property="og:image" content={evento.imagenCaratula} />
</Helmet>
```

---

✅ **¡Implementación completa lista para integrar!** 🎉