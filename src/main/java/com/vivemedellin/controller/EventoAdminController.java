package com.vivemedellin.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vivemedellin.model.Evento;
import com.vivemedellin.repository.EventoRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Controlador de acciones administrativas sobre eventos
 * Requiere rol ADMINISTRADOR (pendiente de configurar Spring Security)
 * 
 * TODO: Agregar Spring Security y descomentar las anotaciones @PreAuthorize
 */
@RestController
@RequestMapping("/api/admin/eventos")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Acciones de Administrador", description = "Endpoints para acciones administrativas sobre eventos (requiere rol ADMINISTRADOR)")
// @SecurityRequirement(name = "bearer-jwt") // TODO: Descomentar cuando se configure Spring Security
public class EventoAdminController {
    
    private final EventoRepository eventoRepository;
    
    /**
     * Cancelar un evento
     * Solo accesible para administradores
     * TODO: Agregar validación de rol cuando se configure Spring Security
     */
    @PostMapping("/{id}/cancelar")
    // @PreAuthorize("hasRole('ADMINISTRADOR')") // TODO: Descomentar cuando se configure Spring Security
    @Operation(
        summary = "Cancelar un evento (ADMIN)",
        description = """
            Marca un evento como CANCELADO. Solo accesible para administradores.
            
            **Flujo:**
            1. Administrador hace clic en botón "Cancelar"
            2. Frontend muestra diálogo de confirmación
            3. Si confirma, se llama a este endpoint
            4. Evento se marca como CANCELADO
            5. Frontend actualiza la vista en tiempo real
            
            **Efecto:**
            - Estado cambia a CANCELADO
            - Se registra fecha y usuario que canceló
            - Se muestra mensaje "Este evento ha sido cancelado" en el detalle
            """,
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Evento cancelado exitosamente",
                content = @Content(schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Evento no encontrado"
            ),
            @ApiResponse(
                responseCode = "403",
                description = "No tiene permisos de administrador"
            ),
            @ApiResponse(
                responseCode = "409",
                description = "El evento ya está cancelado"
            )
        }
    )
    public ResponseEntity<?> cancelarEvento(
            @Parameter(description = "ID del evento a cancelar", required = true, example = "1")
            @PathVariable Long id) {
        
        log.info("Administrador cancelando evento ID: {}", id);
        
        Optional<Evento> eventoOpt = eventoRepository.findById(id);
        
        if (eventoOpt.isEmpty()) {
            log.warn("Evento con ID {} no encontrado", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(crearRespuestaError("Evento no encontrado"));
        }
        
        Evento evento = eventoOpt.get();
        
        // Validar que no esté ya cancelado
        if (evento.getStatus() == Evento.EstadoEvento.CANCELLED) {
            log.warn("Evento ID {} ya está cancelado", id);
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(crearRespuestaError("Este evento ya está cancelado"));
        }
        
        // Cancelar evento
        try {
            evento.setStatus(Evento.EstadoEvento.CANCELLED);
            evento.setCancelledAt(java.time.LocalDateTime.now());
            // TODO: Obtener usuario autenticado y guardarlo en cancelledBy
            evento.setCancelledBy("admin"); // Por ahora hardcoded
            
            eventoRepository.save(evento);
            
            log.info("Evento ID {} cancelado exitosamente", id);
            
            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("success", true);
            respuesta.put("mensaje", "Evento cancelado exitosamente");
            respuesta.put("eventoId", id);
            respuesta.put("estadoActual", "CANCELADO");
            
            return ResponseEntity.ok(respuesta);
            
        } catch (Exception e) {
            log.error("Error al cancelar evento ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(crearRespuestaError("Error al cancelar el evento. Intente nuevamente."));
        }
    }
    
    /**
     * Destacar un evento
     * Solo accesible para administradores
     * Validación: máximo 3 eventos destacados activos
     * TODO: Agregar validación de rol cuando se configure Spring Security
     */
    @PutMapping("/{id}/destacar")
    // @PreAuthorize("hasRole('ADMINISTRADOR')") // TODO: Descomentar cuando se configure Spring Security
    @Operation(
        summary = "Destacar o quitar destacado de un evento (ADMIN)",
        description = """
            Marca o desmarca un evento como DESTACADO. Solo accesible para administradores.
            
            **Validación importante:**
            - Solo puede haber máximo 3 eventos destacados activos simultáneamente
            - Si ya hay 3 destacados y se intenta destacar otro, se retorna error 409
            
            **Flujo:**
            1. Administrador hace clic en botón "Destacar"/"Quitar Destacado"
            2. Sistema valida que no haya más de 3 destacados (si se va a destacar)
            3. Si la validación pasa, se actualiza el estado
            4. Frontend actualiza la vista en tiempo real (badge de "Destacado")
            
            **Parámetros de Query:**
            - destacar=true: Marca como destacado
            - destacar=false: Quita el destacado
            """,
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Estado de destacado actualizado exitosamente",
                content = @Content(schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Evento no encontrado"
            ),
            @ApiResponse(
                responseCode = "403",
                description = "No tiene permisos de administrador"
            ),
            @ApiResponse(
                responseCode = "409",
                description = "Ya existen 3 eventos destacados activos. Debe quitar el destacado de otro evento primero."
            )
        }
    )
    public ResponseEntity<?> destacarEvento(
            @Parameter(description = "ID del evento", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "true para destacar, false para quitar destacado", required = true, example = "true")
            @RequestParam boolean destacar) {
        
        log.info("Administrador {} destacado del evento ID: {}", destacar ? "agregando" : "quitando", id);
        
        Optional<Evento> eventoOpt = eventoRepository.findById(id);
        
        if (eventoOpt.isEmpty()) {
            log.warn("Evento con ID {} no encontrado", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(crearRespuestaError("Evento no encontrado"));
        }
        
        Evento evento = eventoOpt.get();
        
        // Si se va a destacar, validar que no haya más de 3 destacados VIGENTES
        if (destacar && !evento.getDestacado()) {
            long cantidadDestacados = eventoRepository.countDestacadosVigentes(Evento.EstadoEvento.PUBLISHED);
            
            if (cantidadDestacados >= 3) {
                log.warn("Ya existen {} eventos destacados vigentes. No se puede destacar el evento ID {}", 
                    cantidadDestacados, id);
                
                Map<String, Object> error = crearRespuestaError(
                    "Ya existen 3 eventos destacados activos con fechas vigentes. Debe quitar el destacado de otro evento primero."
                );
                error.put("cantidadDestacadosActuales", cantidadDestacados);
                error.put("limiteMaximo", 3);
                
                return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
            }
        }
        
        try {
            // Actualizar estado de destacado
            evento.setDestacado(destacar);
            eventoRepository.save(evento);
            
            log.info("Evento ID {} {} exitosamente", id, destacar ? "destacado" : "quitado de destacados");
            
            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("success", true);
            respuesta.put("mensaje", destacar ? 
                "Evento destacado exitosamente" : 
                "Destacado removido exitosamente");
            respuesta.put("eventoId", id);
            respuesta.put("destacado", destacar);
            
            // Información adicional útil para el frontend (solo vigentes)
            long destacadosActuales = eventoRepository.countDestacadosVigentes(Evento.EstadoEvento.PUBLISHED);
            respuesta.put("cantidadDestacadosActuales", destacadosActuales);
            respuesta.put("espaciosDisponibles", Math.max(0, 3 - destacadosActuales));
            
            return ResponseEntity.ok(respuesta);
            
        } catch (Exception e) {
            log.error("Error al actualizar destacado del evento ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(crearRespuestaError("Error al actualizar el evento. Intente nuevamente."));
        }
    }
    
    /**
     * Obtener información de eventos destacados
     * Útil para saber cuántos espacios quedan disponibles
     * TODO: Agregar validación de rol cuando se configure Spring Security
     */
    @GetMapping("/destacados/info")
    // @PreAuthorize("hasRole('ADMINISTRADOR')") // TODO: Descomentar cuando se configure Spring Security
    @Operation(
        summary = "Obtener información de eventos destacados (ADMIN)",
        description = """
            Devuelve información sobre los eventos destacados actuales.
            Útil para mostrar al administrador cuántos espacios de destacados quedan disponibles.
            """,
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Información obtenida exitosamente",
                content = @Content(schema = @Schema(implementation = Map.class))
            )
        }
    )
    public ResponseEntity<?> infoDestacados() {
        log.info("Consultando información de eventos destacados");
        
        long cantidadDestacados = eventoRepository.countDestacadosVigentes(Evento.EstadoEvento.PUBLISHED);
        
        Map<String, Object> info = new HashMap<>();
        info.put("cantidadDestacados", cantidadDestacados);
        info.put("limiteMaximo", 3);
        info.put("espaciosDisponibles", Math.max(0, 3 - cantidadDestacados));
        info.put("puedeDestacarMas", cantidadDestacados < 3);
        
        return ResponseEntity.ok(info);
    }
    
    /**
     * Validar si se puede destacar un evento
     * Útil para habilitar/deshabilitar el botón en el frontend
     * TODO: Agregar validación de rol cuando se configure Spring Security
     */
    @GetMapping("/{id}/puede-destacar")
    // @PreAuthorize("hasRole('ADMINISTRADOR')") // TODO: Descomentar cuando se configure Spring Security
    @Operation(
        summary = "Validar si un evento puede ser destacado (ADMIN)",
        description = """
            Verifica si un evento específico puede ser marcado como destacado.
            Útil para habilitar/deshabilitar el botón de destacar en el frontend.
            
            **Retorna:**
            - puedeDestacar: true/false
            - razon: Explicación si no puede destacarse
            """,
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Validación exitosa",
                content = @Content(schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Evento no encontrado"
            )
        }
    )
    public ResponseEntity<?> puedeDestacar(@PathVariable Long id) {
        log.info("Validando si evento ID {} puede ser destacado", id);
        
        Optional<Evento> eventoOpt = eventoRepository.findById(id);
        
        if (eventoOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(crearRespuestaError("Evento no encontrado"));
        }
        
        Evento evento = eventoOpt.get();
        Map<String, Object> validacion = new HashMap<>();
        
        // Ya está destacado
        if (evento.getDestacado()) {
            validacion.put("puedeDestacar", false);
            validacion.put("razon", "Este evento ya está destacado");
            validacion.put("accionDisponible", "QUITAR_DESTACADO");
            return ResponseEntity.ok(validacion);
        }
        
        // Evento no está publicado
        if (evento.getStatus() != Evento.EstadoEvento.PUBLISHED) {
            validacion.put("puedeDestacar", false);
            validacion.put("razon", "Solo se pueden destacar eventos publicados");
            return ResponseEntity.ok(validacion);
        }
        
        // Verificar límite de destacados
        long cantidadDestacados = eventoRepository.countDestacadosVigentes(Evento.EstadoEvento.PUBLISHED);
        
        if (cantidadDestacados >= 3) {
            validacion.put("puedeDestacar", false);
            validacion.put("razon", "Ya existen 3 eventos destacados con fechas vigentes. Debe quitar el destacado de otro evento primero.");
            validacion.put("cantidadDestacados", cantidadDestacados);
            validacion.put("limiteMaximo", 3);
            return ResponseEntity.ok(validacion);
        }
        
        // Puede destacarse
        validacion.put("puedeDestacar", true);
        validacion.put("cantidadDestacados", cantidadDestacados);
        validacion.put("espaciosDisponibles", 3 - cantidadDestacados);
        
        return ResponseEntity.ok(validacion);
    }
    
    /**
     * Método auxiliar para crear respuestas de error consistentes
     */
    private Map<String, Object> crearRespuestaError(String mensaje) {
        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("error", mensaje);
        return error;
    }
}
