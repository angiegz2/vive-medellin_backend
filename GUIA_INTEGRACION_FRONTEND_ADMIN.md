# Guía de Integración Frontend - Acciones Administrativas

Esta guía proporciona ejemplos de implementación para las **acciones administrativas** sobre eventos en el sistema ViveMedellín. Incluye ejemplos completos para React, Angular y Vue.js.

---

## 📋 Tabla de Contenido

1. [Resumen de Funcionalidades](#resumen-de-funcionalidades)
2. [Endpoints Disponibles](#endpoints-disponibles)
3. [Implementación en React](#implementación-en-react)
4. [Implementación en Angular](#implementación-en-angular)
5. [Implementación en Vue.js](#implementación-en-vuejs)
6. [Mejores Prácticas](#mejores-prácticas)

---

## 🎯 Resumen de Funcionalidades

### Acciones Disponibles

1. **Editar Evento**: Redirige a la página de edición del evento
2. **Cancelar Evento**: Marca el evento como cancelado (requiere confirmación)
3. **Destacar/Quitar Destacado**: Marca o desmarca un evento como destacado (máximo 3 activos)

### Características

- ✅ Botones visibles solo para usuarios con rol `ADMINISTRADOR`
- ✅ Validación de permisos en tiempo real
- ✅ Confirmación antes de cancelar
- ✅ Validación automática del límite de 3 destacados
- ✅ Actualizaciones en tiempo real
- ✅ Manejo de errores con mensajes claros
- ✅ Estados de carga (loading)

---

## 🔌 Endpoints Disponibles

### 1. Cancelar Evento

```http
POST /api/admin/eventos/{id}/cancelar
```

**Respuesta exitosa:**
```json
{
  "success": true,
  "mensaje": "Evento cancelado exitosamente",
  "eventoId": 1,
  "estadoActual": "CANCELADO"
}
```

**Errores posibles:**
- `404`: Evento no encontrado
- `403`: No tiene permisos de administrador
- `409`: El evento ya está cancelado

---

### 2. Destacar/Quitar Destacado

```http
PUT /api/admin/eventos/{id}/destacar?destacar=true
PUT /api/admin/eventos/{id}/destacar?destacar=false
```

**Respuesta exitosa:**
```json
{
  "success": true,
  "mensaje": "Evento destacado exitosamente",
  "eventoId": 1,
  "destacado": true,
  "cantidadDestacadosActuales": 2,
  "espaciosDisponibles": 1
}
```

**Errores posibles:**
- `404`: Evento no encontrado
- `403`: No tiene permisos de administrador
- `409`: Ya existen 3 eventos destacados (límite alcanzado)

---

### 3. Información de Destacados

```http
GET /api/admin/eventos/destacados/info
```

**Respuesta:**
```json
{
  "cantidadDestacados": 2,
  "limiteMaximo": 3,
  "espaciosDisponibles": 1,
  "puedeDestacarMas": true
}
```

---

### 4. Validar si se Puede Destacar

```http
GET /api/admin/eventos/{id}/puede-destacar
```

**Respuesta:**
```json
{
  "puedeDestacar": true,
  "cantidadDestacados": 2,
  "espaciosDisponibles": 1
}
```

---

## ⚛️ Implementación en React

### Componente de Botones de Administrador

```jsx
// components/EventoAdminActions.jsx
import React, { useState } from 'react';
import axios from 'axios';

const EventoAdminActions = ({ evento, onUpdate, userRole }) => {
  const [loading, setLoading] = useState(false);
  const [showCancelDialog, setShowCancelDialog] = useState(false);
  
  // Solo mostrar si el usuario es administrador
  if (userRole !== 'ADMINISTRADOR') {
    return null;
  }
  
  // Verificar qué acciones están disponibles
  const accionesAdmin = evento.accionesAdmin;
  if (!accionesAdmin) {
    return null;
  }
  
  // === EDITAR EVENTO ===
  const handleEditar = () => {
    window.location.href = `/admin/eventos/${evento.id}/editar`;
  };
  
  // === CANCELAR EVENTO ===
  const handleCancelarClick = () => {
    setShowCancelDialog(true);
  };
  
  const handleCancelarConfirm = async () => {
    setLoading(true);
    try {
      const response = await axios.post(
        `/api/admin/eventos/${evento.id}/cancelar`,
        {},
        {
          headers: {
            'Authorization': `Bearer ${localStorage.getItem('token')}`
          }
        }
      );
      
      // Mostrar mensaje de éxito
      alert(response.data.mensaje);
      
      // Actualizar la vista
      if (onUpdate) {
        onUpdate();
      }
      
      setShowCancelDialog(false);
    } catch (error) {
      // Manejo de errores
      const mensaje = error.response?.data?.error || 'Error al cancelar el evento';
      alert(mensaje);
    } finally {
      setLoading(false);
    }
  };
  
  // === DESTACAR/QUITAR DESTACADO ===
  const handleDestacar = async () => {
    // Determinar si se va a destacar o quitar destacado
    const destacar = !accionesAdmin.estaDestacado;
    
    // Validar antes de destacar
    if (destacar && !accionesAdmin.puedeDestacar) {
      alert(accionesAdmin.razonNoDestacar);
      return;
    }
    
    setLoading(true);
    try {
      const response = await axios.put(
        `/api/admin/eventos/${evento.id}/destacar?destacar=${destacar}`,
        {},
        {
          headers: {
            'Authorization': `Bearer ${localStorage.getItem('token')}`
          }
        }
      );
      
      // Mostrar información actualizada
      alert(
        `${response.data.mensaje}\n\n` +
        `Destacados actuales: ${response.data.cantidadDestacadosActuales}/3\n` +
        `Espacios disponibles: ${response.data.espaciosDisponibles}`
      );
      
      // Actualizar la vista
      if (onUpdate) {
        onUpdate();
      }
    } catch (error) {
      const mensaje = error.response?.data?.error || 'Error al actualizar el destacado';
      alert(mensaje);
    } finally {
      setLoading(false);
    }
  };
  
  return (
    <div className="admin-actions-container">
      <h3>Acciones de Administrador</h3>
      
      <div className="admin-actions-buttons">
        {/* BOTÓN EDITAR */}
        {accionesAdmin.puedeEditar && (
          <button
            onClick={handleEditar}
            disabled={loading}
            className="btn btn-primary"
          >
            ✏️ Editar Evento
          </button>
        )}
        
        {/* BOTÓN CANCELAR */}
        {accionesAdmin.puedeCancelar && !accionesAdmin.estaCancelado && (
          <button
            onClick={handleCancelarClick}
            disabled={loading}
            className="btn btn-danger"
          >
            ❌ Cancelar Evento
          </button>
        )}
        
        {/* BOTÓN DESTACAR/QUITAR DESTACADO */}
        {(accionesAdmin.puedeDestacar || accionesAdmin.puedeQuitarDestacado) && (
          <button
            onClick={handleDestacar}
            disabled={loading || (!accionesAdmin.estaDestacado && !accionesAdmin.puedeDestacar)}
            className={accionesAdmin.estaDestacado ? "btn btn-warning" : "btn btn-success"}
            title={accionesAdmin.razonNoDestacar || ''}
          >
            {accionesAdmin.estaDestacado ? '⭐ Quitar Destacado' : '⭐ Destacar Evento'}
          </button>
        )}
      </div>
      
      {/* Información de destacados */}
      {accionesAdmin.cantidadDestacados !== null && (
        <div className="destacados-info">
          <small>
            📊 Eventos destacados: {accionesAdmin.cantidadDestacados}/3 
            {accionesAdmin.espaciosDisponibles > 0 && 
              ` (${accionesAdmin.espaciosDisponibles} espacios disponibles)`}
          </small>
        </div>
      )}
      
      {/* Modal de confirmación para cancelar */}
      {showCancelDialog && (
        <div className="modal-overlay">
          <div className="modal-content">
            <h3>⚠️ Confirmar Cancelación</h3>
            <p>
              ¿Está seguro que desea cancelar el evento "{evento.titulo}"?
            </p>
            <p>
              Esta acción marcará el evento como CANCELADO y no se podrá revertir.
            </p>
            <div className="modal-buttons">
              <button
                onClick={handleCancelarConfirm}
                disabled={loading}
                className="btn btn-danger"
              >
                {loading ? 'Cancelando...' : 'Sí, Cancelar Evento'}
              </button>
              <button
                onClick={() => setShowCancelDialog(false)}
                disabled={loading}
                className="btn btn-secondary"
              >
                No, Volver
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default EventoAdminActions;
```

### Estilos CSS para React

```css
/* styles/EventoAdminActions.css */

.admin-actions-container {
  background-color: #fff3cd;
  border: 2px solid #ffc107;
  border-radius: 8px;
  padding: 20px;
  margin: 20px 0;
}

.admin-actions-container h3 {
  color: #856404;
  margin: 0 0 15px 0;
  font-size: 18px;
}

.admin-actions-buttons {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.btn {
  padding: 10px 20px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s;
}

.btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.btn-primary {
  background-color: #007bff;
  color: white;
}

.btn-primary:hover:not(:disabled) {
  background-color: #0056b3;
}

.btn-danger {
  background-color: #dc3545;
  color: white;
}

.btn-danger:hover:not(:disabled) {
  background-color: #c82333;
}

.btn-success {
  background-color: #28a745;
  color: white;
}

.btn-success:hover:not(:disabled) {
  background-color: #218838;
}

.btn-warning {
  background-color: #ffc107;
  color: #212529;
}

.btn-warning:hover:not(:disabled) {
  background-color: #e0a800;
}

.btn-secondary {
  background-color: #6c757d;
  color: white;
}

.btn-secondary:hover:not(:disabled) {
  background-color: #5a6268;
}

.destacados-info {
  margin-top: 15px;
  padding: 10px;
  background-color: #f8f9fa;
  border-radius: 4px;
}

.destacados-info small {
  color: #6c757d;
}

/* Modal */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background-color: white;
  padding: 30px;
  border-radius: 8px;
  max-width: 500px;
  width: 90%;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.modal-content h3 {
  margin: 0 0 15px 0;
  color: #dc3545;
}

.modal-content p {
  margin: 10px 0;
  color: #212529;
}

.modal-buttons {
  display: flex;
  gap: 10px;
  margin-top: 20px;
  justify-content: flex-end;
}
```

### Uso en Página de Detalle

```jsx
// pages/EventoDetalle.jsx
import React, { useState, useEffect } from 'react';
import axios from 'axios';
import EventoAdminActions from '../components/EventoAdminActions';

const EventoDetalle = ({ eventoId, userRole }) => {
  const [evento, setEvento] = useState(null);
  const [loading, setLoading] = useState(true);
  
  const cargarEvento = async () => {
    setLoading(true);
    try {
      const response = await axios.get(`/api/public/eventos/${eventoId}`);
      setEvento(response.data);
    } catch (error) {
      console.error('Error al cargar evento:', error);
    } finally {
      setLoading(false);
    }
  };
  
  useEffect(() => {
    cargarEvento();
  }, [eventoId]);
  
  if (loading) return <div>Cargando...</div>;
  if (!evento) return <div>Evento no encontrado</div>;
  
  return (
    <div className="evento-detalle-page">
      {/* Información del evento */}
      <h1>{evento.titulo}</h1>
      <p>{evento.descripcion}</p>
      
      {/* Mostrar estado si está cancelado o finalizado */}
      {evento.mensajeEstado && (
        <div className={`alert alert-${evento.estadoEvento === 'CANCELADO' ? 'danger' : 'info'}`}>
          {evento.mensajeEstado}
        </div>
      )}
      
      {/* Badge de destacado */}
      {evento.destacado && (
        <span className="badge badge-warning">⭐ Evento Destacado</span>
      )}
      
      {/* Botones de administrador */}
      <EventoAdminActions
        evento={evento}
        userRole={userRole}
        onUpdate={cargarEvento}
      />
      
      {/* Resto de la información del evento */}
      {/* ... */}
    </div>
  );
};

export default EventoDetalle;
```

---

## 🅰️ Implementación en Angular

### Servicio de Administración

```typescript
// services/evento-admin.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface AdminAction {
  success: boolean;
  mensaje: string;
  eventoId?: number;
  estadoActual?: string;
  destacado?: boolean;
  cantidadDestacadosActuales?: number;
  espaciosDisponibles?: number;
}

@Injectable({
  providedIn: 'root'
})
export class EventoAdminService {
  private readonly API_URL = '/api/admin/eventos';
  
  constructor(private http: HttpClient) {}
  
  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
  }
  
  /**
   * Cancela un evento
   */
  cancelarEvento(eventoId: number): Observable<AdminAction> {
    return this.http.post<AdminAction>(
      `${this.API_URL}/${eventoId}/cancelar`,
      {},
      { headers: this.getHeaders() }
    );
  }
  
  /**
   * Destaca o quita el destacado de un evento
   */
  destacarEvento(eventoId: number, destacar: boolean): Observable<AdminAction> {
    return this.http.put<AdminAction>(
      `${this.API_URL}/${eventoId}/destacar?destacar=${destacar}`,
      {},
      { headers: this.getHeaders() }
    );
  }
  
  /**
   * Obtiene información sobre eventos destacados
   */
  getInfoDestacados(): Observable<any> {
    return this.http.get(
      `${this.API_URL}/destacados/info`,
      { headers: this.getHeaders() }
    );
  }
  
  /**
   * Valida si un evento puede ser destacado
   */
  puedeDestacar(eventoId: number): Observable<any> {
    return this.http.get(
      `${this.API_URL}/${eventoId}/puede-destacar`,
      { headers: this.getHeaders() }
    );
  }
}
```

### Componente de Acciones Admin

```typescript
// components/evento-admin-actions/evento-admin-actions.component.ts
import { Component, Input, Output, EventEmitter } from '@angular/core';
import { EventoAdminService } from '../../services/evento-admin.service';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ConfirmDialogComponent } from '../confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-evento-admin-actions',
  templateUrl: './evento-admin-actions.component.html',
  styleUrls: ['./evento-admin-actions.component.css']
})
export class EventoAdminActionsComponent {
  @Input() evento: any;
  @Input() userRole: string = '';
  @Output() onUpdate = new EventEmitter<void>();
  
  loading = false;
  
  constructor(
    private adminService: EventoAdminService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar
  ) {}
  
  get isAdmin(): boolean {
    return this.userRole === 'ADMINISTRADOR';
  }
  
  get accionesAdmin() {
    return this.evento?.accionesAdmin;
  }
  
  /**
   * Editar evento
   */
  handleEditar(): void {
    window.location.href = `/admin/eventos/${this.evento.id}/editar`;
  }
  
  /**
   * Cancelar evento con confirmación
   */
  handleCancelar(): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '400px',
      data: {
        title: '⚠️ Confirmar Cancelación',
        message: `¿Está seguro que desea cancelar el evento "${this.evento.titulo}"?`,
        subMessage: 'Esta acción marcará el evento como CANCELADO y no se podrá revertir.',
        confirmText: 'Sí, Cancelar Evento',
        cancelText: 'No, Volver'
      }
    });
    
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.confirmarCancelacion();
      }
    });
  }
  
  private confirmarCancelacion(): void {
    this.loading = true;
    this.adminService.cancelarEvento(this.evento.id).subscribe({
      next: (response) => {
        this.snackBar.open(response.mensaje, 'Cerrar', {
          duration: 5000,
          panelClass: ['success-snackbar']
        });
        this.onUpdate.emit();
        this.loading = false;
      },
      error: (error) => {
        const mensaje = error.error?.error || 'Error al cancelar el evento';
        this.snackBar.open(mensaje, 'Cerrar', {
          duration: 5000,
          panelClass: ['error-snackbar']
        });
        this.loading = false;
      }
    });
  }
  
  /**
   * Destacar/Quitar destacado
   */
  handleDestacar(): void {
    const destacar = !this.accionesAdmin.estaDestacado;
    
    // Validar antes de destacar
    if (destacar && !this.accionesAdmin.puedeDestacar) {
      this.snackBar.open(this.accionesAdmin.razonNoDestacar, 'Cerrar', {
        duration: 5000,
        panelClass: ['warning-snackbar']
      });
      return;
    }
    
    this.loading = true;
    this.adminService.destacarEvento(this.evento.id, destacar).subscribe({
      next: (response) => {
        const mensaje = `${response.mensaje}\n\n` +
          `Destacados actuales: ${response.cantidadDestacadosActuales}/3\n` +
          `Espacios disponibles: ${response.espaciosDisponibles}`;
        
        this.snackBar.open(mensaje, 'Cerrar', {
          duration: 7000,
          panelClass: ['success-snackbar']
        });
        this.onUpdate.emit();
        this.loading = false;
      },
      error: (error) => {
        const mensaje = error.error?.error || 'Error al actualizar el destacado';
        this.snackBar.open(mensaje, 'Cerrar', {
          duration: 5000,
          panelClass: ['error-snackbar']
        });
        this.loading = false;
      }
    });
  }
}
```

### Template HTML

```html
<!-- components/evento-admin-actions/evento-admin-actions.component.html -->
<div class="admin-actions-container" *ngIf="isAdmin && accionesAdmin">
  <h3>Acciones de Administrador</h3>
  
  <div class="admin-actions-buttons">
    <!-- BOTÓN EDITAR -->
    <button 
      *ngIf="accionesAdmin.puedeEditar"
      mat-raised-button 
      color="primary"
      [disabled]="loading"
      (click)="handleEditar()">
      <mat-icon>edit</mat-icon>
      Editar Evento
    </button>
    
    <!-- BOTÓN CANCELAR -->
    <button 
      *ngIf="accionesAdmin.puedeCancelar && !accionesAdmin.estaCancelado"
      mat-raised-button 
      color="warn"
      [disabled]="loading"
      (click)="handleCancelar()">
      <mat-icon>cancel</mat-icon>
      Cancelar Evento
    </button>
    
    <!-- BOTÓN DESTACAR/QUITAR DESTACADO -->
    <button 
      *ngIf="accionesAdmin.puedeDestacar || accionesAdmin.puedeQuitarDestacado"
      mat-raised-button 
      [color]="accionesAdmin.estaDestacado ? 'accent' : 'primary'"
      [disabled]="loading || (!accionesAdmin.estaDestacado && !accionesAdmin.puedeDestacar)"
      [matTooltip]="accionesAdmin.razonNoDestacar || ''"
      (click)="handleDestacar()">
      <mat-icon>star</mat-icon>
      {{ accionesAdmin.estaDestacado ? 'Quitar Destacado' : 'Destacar Evento' }}
    </button>
  </div>
  
  <!-- Información de destacados -->
  <div class="destacados-info" *ngIf="accionesAdmin.cantidadDestacados !== null">
    <small>
      📊 Eventos destacados: {{ accionesAdmin.cantidadDestacados }}/3
      <span *ngIf="accionesAdmin.espaciosDisponibles > 0">
        ({{ accionesAdmin.espaciosDisponibles }} espacios disponibles)
      </span>
    </small>
  </div>
  
  <!-- Loading spinner -->
  <mat-progress-bar *ngIf="loading" mode="indeterminate"></mat-progress-bar>
</div>
```

---

## 🎨 Implementación en Vue.js

### Componente de Acciones Admin

```vue
<!-- components/EventoAdminActions.vue -->
<template>
  <div v-if="isAdmin && accionesAdmin" class="admin-actions-container">
    <h3>Acciones de Administrador</h3>
    
    <div class="admin-actions-buttons">
      <!-- BOTÓN EDITAR -->
      <button
        v-if="accionesAdmin.puedeEditar"
        @click="handleEditar"
        :disabled="loading"
        class="btn btn-primary">
        ✏️ Editar Evento
      </button>
      
      <!-- BOTÓN CANCELAR -->
      <button
        v-if="accionesAdmin.puedeCancelar && !accionesAdmin.estaCancelado"
        @click="showCancelDialog = true"
        :disabled="loading"
        class="btn btn-danger">
        ❌ Cancelar Evento
      </button>
      
      <!-- BOTÓN DESTACAR/QUITAR DESTACADO -->
      <button
        v-if="accionesAdmin.puedeDestacar || accionesAdmin.puedeQuitarDestacado"
        @click="handleDestacar"
        :disabled="loading || (!accionesAdmin.estaDestacado && !accionesAdmin.puedeDestacar)"
        :class="accionesAdmin.estaDestacado ? 'btn btn-warning' : 'btn btn-success'"
        :title="accionesAdmin.razonNoDestacar || ''">
        {{ accionesAdmin.estaDestacado ? '⭐ Quitar Destacado' : '⭐ Destacar Evento' }}
      </button>
    </div>
    
    <!-- Información de destacados -->
    <div v-if="accionesAdmin.cantidadDestacados !== null" class="destacados-info">
      <small>
        📊 Eventos destacados: {{ accionesAdmin.cantidadDestacados }}/3
        <span v-if="accionesAdmin.espaciosDisponibles > 0">
          ({{ accionesAdmin.espaciosDisponibles }} espacios disponibles)
        </span>
      </small>
    </div>
    
    <!-- Modal de confirmación -->
    <div v-if="showCancelDialog" class="modal-overlay" @click.self="showCancelDialog = false">
      <div class="modal-content">
        <h3>⚠️ Confirmar Cancelación</h3>
        <p>¿Está seguro que desea cancelar el evento "{{ evento.titulo }}"?</p>
        <p>Esta acción marcará el evento como CANCELADO y no se podrá revertir.</p>
        <div class="modal-buttons">
          <button
            @click="handleCancelarConfirm"
            :disabled="loading"
            class="btn btn-danger">
            {{ loading ? 'Cancelando...' : 'Sí, Cancelar Evento' }}
          </button>
          <button
            @click="showCancelDialog = false"
            :disabled="loading"
            class="btn btn-secondary">
            No, Volver
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'EventoAdminActions',
  props: {
    evento: {
      type: Object,
      required: true
    },
    userRole: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      loading: false,
      showCancelDialog: false
    };
  },
  computed: {
    isAdmin() {
      return this.userRole === 'ADMINISTRADOR';
    },
    accionesAdmin() {
      return this.evento.accionesAdmin;
    }
  },
  methods: {
    handleEditar() {
      window.location.href = `/admin/eventos/${this.evento.id}/editar`;
    },
    
    async handleCancelarConfirm() {
      this.loading = true;
      try {
        const response = await axios.post(
          `/api/admin/eventos/${this.evento.id}/cancelar`,
          {},
          {
            headers: {
              'Authorization': `Bearer ${localStorage.getItem('token')}`
            }
          }
        );
        
        alert(response.data.mensaje);
        this.$emit('update');
        this.showCancelDialog = false;
      } catch (error) {
        const mensaje = error.response?.data?.error || 'Error al cancelar el evento';
        alert(mensaje);
      } finally {
        this.loading = false;
      }
    },
    
    async handleDestacar() {
      const destacar = !this.accionesAdmin.estaDestacado;
      
      if (destacar && !this.accionesAdmin.puedeDestacar) {
        alert(this.accionesAdmin.razonNoDestacar);
        return;
      }
      
      this.loading = true;
      try {
        const response = await axios.put(
          `/api/admin/eventos/${this.evento.id}/destacar?destacar=${destacar}`,
          {},
          {
            headers: {
              'Authorization': `Bearer ${localStorage.getItem('token')}`
            }
          }
        );
        
        alert(
          `${response.data.mensaje}\n\n` +
          `Destacados actuales: ${response.data.cantidadDestacadosActuales}/3\n` +
          `Espacios disponibles: ${response.data.espaciosDisponibles}`
        );
        
        this.$emit('update');
      } catch (error) {
        const mensaje = error.response?.data?.error || 'Error al actualizar el destacado';
        alert(mensaje);
      } finally {
        this.loading = false;
      }
    }
  }
};
</script>

<style scoped>
/* Usar los mismos estilos CSS del ejemplo de React */
</style>
```

---

## 💡 Mejores Prácticas

### 1. Seguridad

```typescript
// Siempre validar el rol del usuario en el backend
// NO confiar únicamente en la validación del frontend

// ❌ MAL - Solo validación frontend
if (userRole === 'ADMINISTRADOR') {
  // Mostrar botones
}

// ✅ BIEN - Validación frontend + backend
// Frontend: Ocultar botones si no es admin
// Backend: @PreAuthorize("hasRole('ADMINISTRADOR')") en cada endpoint
```

### 2. Manejo de Errores

```javascript
// Siempre manejar errores de red y del servidor
try {
  const response = await axios.post(...);
  // Manejar éxito
} catch (error) {
  if (error.response) {
    // Error del servidor (4xx, 5xx)
    const mensaje = error.response.data.error || 'Error del servidor';
    mostrarError(mensaje);
  } else if (error.request) {
    // Error de red (sin respuesta)
    mostrarError('Error de conexión. Verifique su conexión a internet.');
  } else {
    // Error desconocido
    mostrarError('Error inesperado. Intente nuevamente.');
  }
}
```

### 3. Estados de Carga

```jsx
// Siempre deshabilitar botones durante operaciones asíncronas
<button
  onClick={handleAction}
  disabled={loading}  // ← Importante
  className="btn btn-primary">
  {loading ? 'Procesando...' : 'Confirmar'}
</button>
```

### 4. Actualizaciones en Tiempo Real

```javascript
// Recargar el evento después de cada acción exitosa
const handleAction = async () => {
  try {
    await adminService.action();
    // ✅ Recargar datos actualizados
    await recargarEvento();
  } catch (error) {
    // Manejar error
  }
};
```

### 5. Confirmaciones

```javascript
// Siempre confirmar acciones destructivas
const handleCancelar = () => {
  if (confirm('¿Está seguro que desea cancelar este evento?')) {
    // Proceder con la cancelación
  }
};
```

---

## 📝 Resumen

### Flujo Completo de una Acción Admin

1. **Usuario hace clic** en botón de acción
2. **Frontend valida** si la acción está permitida (`accionesAdmin.puedeXXX`)
3. Si es acción destructiva, **mostrar confirmación**
4. **Enviar petición** al endpoint correspondiente con token de autenticación
5. **Backend valida** rol de administrador y reglas de negocio
6. **Recibir respuesta** y mostrar mensaje apropiado
7. **Actualizar vista** recargando el evento
8. **Mostrar estado actualizado** (cancelado, destacado, etc.)

### Checklist de Implementación

- ✅ Validar rol de usuario antes de mostrar botones
- ✅ Mostrar confirmación para acciones destructivas
- ✅ Deshabilitar botones durante operaciones
- ✅ Manejar todos los casos de error
- ✅ Mostrar mensajes claros al usuario
- ✅ Actualizar la vista después de cada acción
- ✅ Incluir token de autenticación en headers
- ✅ Validar permisos en backend (no solo frontend)

---

## 🔗 Enlaces Relacionados

- [Documentación de Endpoints Admin](./DOCUMENTACION_BUSQUEDA_FILTROS.md#endpoints-de-administrador)
- [Guía de Integración Frontend (Detalle)](./GUIA_INTEGRACION_FRONTEND_DETALLE.md)
- [Resumen de Implementación](./RESUMEN_IMPLEMENTACION.md)

---

**Nota**: Los ejemplos usan `alert()` para simplicidad. En producción, se recomienda usar librerías de UI como:
- **React**: Material-UI Snackbar, react-toastify
- **Angular**: Angular Material Snackbar
- **Vue.js**: Vue Toastification, Element Plus

