package com.vivemedellin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO que contiene información sobre las acciones administrativas disponibles para un evento
 * Se incluye en EventoDetalleDTO cuando el usuario tiene rol de administrador
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Información sobre las acciones administrativas disponibles para un evento")
public class EventoAdminActionsDTO {
    
    @Schema(description = "Indica si el usuario puede editar el evento", example = "true")
    private boolean puedeEditar;
    
    @Schema(description = "Indica si el usuario puede cancelar el evento", example = "true")
    private boolean puedeCancelar;
    
    @Schema(description = "Indica si el usuario puede destacar el evento", example = "true")
    private boolean puedeDestacar;
    
    @Schema(description = "Indica si el usuario puede quitar el destacado del evento", example = "false")
    private boolean puedeQuitarDestacado;
    
    @Schema(description = "Razón por la cual no se puede destacar (si aplica)", 
            example = "Ya existen 3 eventos destacados. Debe quitar el destacado de otro evento primero.")
    private String razonNoDestacar;
    
    @Schema(description = "Cantidad actual de eventos destacados activos", example = "3")
    private Integer cantidadDestacados;
    
    @Schema(description = "Espacios disponibles para destacar eventos", example = "0")
    private Integer espaciosDisponibles;
    
    @Schema(description = "Indica si el evento ya está destacado", example = "true")
    private boolean estaDestacado;
    
    @Schema(description = "Indica si el evento está cancelado", example = "false")
    private boolean estaCancelado;
    
    /**
     * Crea un DTO con todas las acciones habilitadas
     * Útil para administradores con permisos completos
     */
    public static EventoAdminActionsDTO todasHabilitadas() {
        return EventoAdminActionsDTO.builder()
                .puedeEditar(true)
                .puedeCancelar(true)
                .puedeDestacar(true)
                .puedeQuitarDestacado(false)
                .razonNoDestacar(null)
                .cantidadDestacados(0)
                .espaciosDisponibles(3)
                .estaDestacado(false)
                .estaCancelado(false)
                .build();
    }
    
    /**
     * Crea un DTO sin acciones disponibles
     * Útil para usuarios sin permisos administrativos
     */
    public static EventoAdminActionsDTO sinPermisos() {
        return EventoAdminActionsDTO.builder()
                .puedeEditar(false)
                .puedeCancelar(false)
                .puedeDestacar(false)
                .puedeQuitarDestacado(false)
                .razonNoDestacar("No tiene permisos de administrador")
                .cantidadDestacados(null)
                .espaciosDisponibles(null)
                .estaDestacado(false)
                .estaCancelado(false)
                .build();
    }
    
    /**
     * Crea un DTO para un evento ya cancelado
     */
    public static EventoAdminActionsDTO eventoCancelado() {
        return EventoAdminActionsDTO.builder()
                .puedeEditar(true)  // Puede editar información incluso cancelado
                .puedeCancelar(false)  // Ya está cancelado
                .puedeDestacar(false)  // No se destacan eventos cancelados
                .puedeQuitarDestacado(false)
                .razonNoDestacar("El evento está cancelado")
                .cantidadDestacados(null)
                .espaciosDisponibles(null)
                .estaDestacado(false)
                .estaCancelado(true)
                .build();
    }
}
