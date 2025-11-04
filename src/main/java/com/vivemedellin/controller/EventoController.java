package com.vivemedellin.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vivemedellin.dto.ActualizarEventoRequest;
import com.vivemedellin.dto.CrearEventoRequest;
import com.vivemedellin.dto.EventoFiltrosDTO;
import com.vivemedellin.dto.EventoResponse;
import com.vivemedellin.service.EventoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/eventos")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Eventos", description = "API para gestión de eventos de ViveMedellin")
@CrossOrigin(origins = "*")
public class EventoController {
    
    private final EventoService eventoService;
    
    @Operation(summary = "Crear un nuevo evento", 
               description = "Crea un nuevo evento en el sistema con toda la información necesaria")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Evento creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping
    public ResponseEntity<EventoResponse> crearEvento(
            @Valid @RequestBody CrearEventoRequest request,
            @RequestHeader(value = "X-User-Id", defaultValue = "system") String userId) {
        
        log.info("Solicitud para crear evento: {} por usuario: {}", request.getTitulo(), userId);
        
        try {
            EventoResponse evento = eventoService.crearEvento(request, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(evento);
        } catch (Exception e) {
            log.error("Error al crear evento: {}", e.getMessage(), e);
            throw new RuntimeException("Error al crear evento: " + e.getMessage());
        }
    }
    
    @Operation(summary = "Obtener evento por ID", 
               description = "Obtiene la información completa de un evento específico")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Evento encontrado"),
        @ApiResponse(responseCode = "404", description = "Evento no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EventoResponse> obtenerEvento(
            @Parameter(description = "ID del evento a obtener") @PathVariable Long id,
            @Parameter(description = "Incluir eventos cancelados") @RequestParam(defaultValue = "false") boolean includeCancelled) {
        
        log.info("Solicitud para obtener evento ID: {}", id);
        
        var eventoOpt = includeCancelled ? 
            eventoService.obtenerEventoPorIdCompleto(id) : 
            eventoService.obtenerEventoPorId(id);
            
        return eventoOpt
            .map(evento -> ResponseEntity.ok(evento))
            .orElse(ResponseEntity.notFound().build());
    }
    
    @Operation(summary = "Actualizar evento", 
               description = "Actualiza la información de un evento existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Evento actualizado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Evento no encontrado"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    @PutMapping("/{id}")
    public ResponseEntity<EventoResponse> actualizarEvento(
            @Parameter(description = "ID del evento a actualizar") @PathVariable Long id,
            @Valid @RequestBody ActualizarEventoRequest request,
            @RequestHeader(value = "X-User-Id", defaultValue = "system") String userId) {
        
        log.info("Solicitud para actualizar evento ID: {} por usuario: {}", id, userId);
        
        try {
            EventoResponse evento = eventoService.actualizarEvento(id, request, userId);
            return ResponseEntity.ok(evento);
        } catch (RuntimeException e) {
            log.error("Error al actualizar evento {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    @Operation(summary = "Cancelar evento", 
               description = "Cancela un evento (soft delete) sin eliminarlo definitivamente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Evento cancelado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Evento no encontrado")
    })
    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<EventoResponse> cancelarEvento(
            @Parameter(description = "ID del evento a cancelar") @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", defaultValue = "system") String userId) {
        
        log.info("Solicitud para cancelar evento ID: {} por usuario: {}", id, userId);
        
        try {
            EventoResponse evento = eventoService.cancelarEvento(id, userId);
            return ResponseEntity.ok(evento);
        } catch (RuntimeException e) {
            log.error("Error al cancelar evento {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    @Operation(summary = "Reactivar evento", 
               description = "Reactiva un evento previamente cancelado")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Evento reactivado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Evento no encontrado")
    })
    @PatchMapping("/{id}/reactivar")
    public ResponseEntity<EventoResponse> reactivarEvento(
            @Parameter(description = "ID del evento a reactivar") @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", defaultValue = "system") String userId) {
        
        log.info("Solicitud para reactivar evento ID: {} por usuario: {}", id, userId);
        
        try {
            EventoResponse evento = eventoService.reactivarEvento(id, userId);
            return ResponseEntity.ok(evento);
        } catch (RuntimeException e) {
            log.error("Error al reactivar evento {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    @Operation(summary = "Destacar evento", 
               description = "Marca un evento como destacado para darle mayor visibilidad")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Evento destacado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Evento no encontrado")
    })
    @PatchMapping("/{id}/destacar")
    public ResponseEntity<EventoResponse> destacarEvento(
            @Parameter(description = "ID del evento a destacar") @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", defaultValue = "system") String userId) {
        
        log.info("Solicitud para destacar evento ID: {} por usuario: {}", id, userId);
        
        try {
            EventoResponse evento = eventoService.destacarEvento(id, userId);
            return ResponseEntity.ok(evento);
        } catch (RuntimeException e) {
            log.error("Error al destacar evento {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    @Operation(summary = "Quitar destaque de evento", 
               description = "Remueve el estado de destacado de un evento")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Destaque removido exitosamente"),
        @ApiResponse(responseCode = "404", description = "Evento no encontrado")
    })
    @PatchMapping("/{id}/quitar-destaque")
    public ResponseEntity<EventoResponse> quitarDestaque(
            @Parameter(description = "ID del evento para quitar destaque") @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", defaultValue = "system") String userId) {
        
        log.info("Solicitud para quitar destaque del evento ID: {} por usuario: {}", id, userId);
        
        try {
            EventoResponse evento = eventoService.quitarDestaque(id, userId);
            return ResponseEntity.ok(evento);
        } catch (RuntimeException e) {
            log.error("Error al quitar destaque del evento {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    @Operation(summary = "Listar todos los eventos activos", 
               description = "Obtiene una lista de todos los eventos publicados y activos")
    @ApiResponse(responseCode = "200", description = "Lista de eventos obtenida exitosamente")
    @GetMapping
    public ResponseEntity<List<EventoResponse>> listarEventosActivos() {
        log.info("Solicitud para listar todos los eventos activos");
        
        List<EventoResponse> eventos = eventoService.listarEventosActivos();
        return ResponseEntity.ok(eventos);
    }
    
    @Operation(summary = "Listar eventos destacados", 
               description = "Obtiene una lista de eventos marcados como destacados")
    @ApiResponse(responseCode = "200", description = "Lista de eventos destacados obtenida exitosamente")
    @GetMapping("/destacados")
    public ResponseEntity<List<EventoResponse>> listarEventosDestacados() {
        log.info("Solicitud para listar eventos destacados");
        
        List<EventoResponse> eventos = eventoService.listarEventosDestacados();
        return ResponseEntity.ok(eventos);
    }
    
    @Operation(summary = "Buscar eventos con filtros", 
               description = "Busca eventos aplicando múltiples filtros con paginación")
    @ApiResponse(responseCode = "200", description = "Resultados de búsqueda obtenidos exitosamente")
    @GetMapping("/buscar")
    public ResponseEntity<Page<EventoResponse>> buscarEventosConFiltros(
            @Parameter(description = "Categoría del evento") @RequestParam(required = false) String categoria,
            @Parameter(description = "Fecha de inicio del rango") @RequestParam(required = false) 
                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @Parameter(description = "Fecha de fin del rango") @RequestParam(required = false) 
                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @Parameter(description = "Ubicación (barrio o comuna)") @RequestParam(required = false) String ubicacion,
            @Parameter(description = "Solo eventos destacados") @RequestParam(required = false) Boolean destacado,
            Pageable pageable) {
        
        log.info("Solicitud de búsqueda con filtros - Categoría: {}, Fechas: {} a {}, Ubicación: {}, Destacado: {}", 
                 categoria, fechaInicio, fechaFin, ubicacion, destacado);
        
        Page<EventoResponse> eventos = eventoService.buscarEventosConFiltros(
            categoria, fechaInicio, fechaFin, ubicacion, destacado, pageable);
        
        return ResponseEntity.ok(eventos);
    }
    
    @Operation(summary = "Búsqueda de texto completo", 
               description = "Busca eventos por texto en título, descripción u organizador")
    @ApiResponse(responseCode = "200", description = "Resultados de búsqueda obtenidos exitosamente")
    @GetMapping("/buscar/texto")
public ResponseEntity<List<EventoResponse>> busquedaTextoCompleto(
        @RequestParam(required = false) String q) {

    log.info("Búsqueda de texto completo: {}", q);

    if (q == null || q.isBlank()) {
        return ResponseEntity.badRequest().build();
    }

    String textoBusqueda = q.trim();
    List<EventoResponse> eventos = eventoService.busquedaTextoCompleto(textoBusqueda);
    return ResponseEntity.ok(eventos);
}
    
    @Operation(summary = "Búsqueda avanzada con filtros múltiples", 
               description = "Busca eventos usando combinación de filtros: texto, ubicación, categoría, fechas, etc.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Resultados de búsqueda obtenidos exitosamente"),
        @ApiResponse(responseCode = "400", description = "Parámetros de búsqueda inválidos")
    })
    @GetMapping("/buscar/avanzada")
    public ResponseEntity<Page<EventoResponse>> busquedaAvanzada(
            @Parameter(description = "Texto para buscar en título/descripción") @RequestParam(required = false) String texto,
            @Parameter(description = "Ubicación (comuna, barrio o dirección)") @RequestParam(required = false) String ubicacion,
            @Parameter(description = "Categoría del evento") @RequestParam(required = false) String categoria,
            @Parameter(description = "Fecha inicial del rango") @RequestParam(required = false) 
                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
            @Parameter(description = "Fecha final del rango") @RequestParam(required = false) 
                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @Parameter(description = "Solo eventos destacados") @RequestParam(required = false) Boolean destacado,
            @Parameter(description = "Solo eventos gratuitos") @RequestParam(required = false) Boolean gratuito,
            @Parameter(description = "Modalidad del evento") @RequestParam(required = false) String modalidad,
            @Parameter(description = "Nombre del organizador") @RequestParam(required = false) String organizador,
            @Parameter(description = "Solo eventos activos") @RequestParam(required = false, defaultValue = "true") Boolean soloActivos,
            @Parameter(description = "Número de página") @RequestParam(required = false, defaultValue = "0") Integer page,
            @Parameter(description = "Tamaño de página") @RequestParam(required = false, defaultValue = "10") Integer size,
            @Parameter(description = "Campo para ordenar") @RequestParam(required = false, defaultValue = "fecha") String ordenarPor,
            @Parameter(description = "Dirección de ordenamiento") @RequestParam(required = false, defaultValue = "ASC") String direccion) {
        
        log.info("Búsqueda avanzada - Texto: {}, Ubicación: {}, Categoría: {}, Fechas: {} a {}", 
                 texto, ubicacion, categoria, fechaDesde, fechaFin);
        
        try {
            EventoFiltrosDTO filtros = EventoFiltrosDTO.builder()
                .texto(texto)
                .ubicacion(ubicacion)
                .categoria(categoria)
                .fechaDesde(fechaDesde)
                .fechaHasta(fechaFin)
                .destacado(destacado)
                .gratuito(gratuito)
                .modalidad(modalidad)
                .organizador(organizador)
                .soloActivos(soloActivos)
                .page(page)
                .size(size)
                .ordenarPor(ordenarPor)
                .direccion(direccion)
                .build();
            
            Page<EventoResponse> eventos = eventoService.busquedaAvanzada(filtros);
            return ResponseEntity.ok(eventos);
            
        } catch (IllegalArgumentException e) {
            log.error("Parámetros de búsqueda inválidos: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    @Operation(summary = "Búsqueda por palabras clave", 
               description = "Busca eventos por palabras clave en múltiples campos (título, descripción, categoría, organizador, ubicación)")
    @ApiResponse(responseCode = "200", description = "Resultados de búsqueda obtenidos exitosamente")
    @GetMapping("/buscar/keywords")
    public ResponseEntity<Page<EventoResponse>> buscarPorPalabrasClaves(
            @Parameter(description = "Palabras clave separadas por espacios", required = true) @RequestParam String q,
            @Parameter(description = "Número de página") @RequestParam(required = false, defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página") @RequestParam(required = false, defaultValue = "10") int size) {
        
        log.info("Búsqueda por palabras clave: {}", q);
        
        if (q == null || q.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "destacado", "fecha"));
            Page<EventoResponse> eventos = eventoService.buscarPorPalabrasClaves(q.trim(), pageable);
            return ResponseEntity.ok(eventos);
        } catch (IllegalArgumentException e) {
            log.error("Error en búsqueda por palabras clave: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    @Operation(summary = "Buscar eventos por ubicación", 
               description = "Busca eventos próximos por ubicación (comuna, barrio o dirección)")
    @ApiResponse(responseCode = "200", description = "Eventos encontrados exitosamente")
    @GetMapping("/buscar/ubicacion")
    public ResponseEntity<List<EventoResponse>> buscarPorUbicacion(
            @Parameter(description = "Ubicación a buscar", required = true) @RequestParam String ubicacion) {
        
        log.info("Búsqueda por ubicación: {}", ubicacion);
        
        if (ubicacion == null || ubicacion.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        try {
            List<EventoResponse> eventos = eventoService.buscarPorUbicacion(ubicacion.trim());
            return ResponseEntity.ok(eventos);
        } catch (IllegalArgumentException e) {
            log.error("Error en búsqueda por ubicación: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    @Operation(summary = "Buscar eventos por fecha específica", 
               description = "Busca todos los eventos que ocurren en una fecha específica")
    @ApiResponse(responseCode = "200", description = "Eventos encontrados exitosamente")
    @GetMapping("/buscar/fecha")
    public ResponseEntity<List<EventoResponse>> buscarPorFecha(
            @Parameter(description = "Fecha a buscar", required = true) @RequestParam 
                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        
        log.info("Búsqueda por fecha: {}", fecha);
        
        if (fecha == null) {
            return ResponseEntity.badRequest().build();
        }
        
        try {
            List<EventoResponse> eventos = eventoService.buscarPorFecha(fecha);
            return ResponseEntity.ok(eventos);
        } catch (IllegalArgumentException e) {
            log.error("Error en búsqueda por fecha: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    @Operation(summary = "Buscar eventos por rango de fechas", 
               description = "Busca eventos que ocurren en un rango de fechas")
    @ApiResponse(responseCode = "200", description = "Eventos encontrados exitosamente")
    @GetMapping("/buscar/rango-fechas")
    public ResponseEntity<List<EventoResponse>> buscarPorRangoFechas(
            @Parameter(description = "Fecha inicial") @RequestParam(required = false) 
                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
            @Parameter(description = "Fecha final") @RequestParam(required = false) 
                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta) {
        
        log.info("Búsqueda por rango de fechas: {} a {}", fechaDesde, fechaHasta);
        
        try {
            List<EventoResponse> eventos = eventoService.buscarPorRangoFechas(fechaDesde, fechaHasta);
            return ResponseEntity.ok(eventos);
        } catch (IllegalArgumentException e) {
            log.error("Error en búsqueda por rango de fechas: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    @Operation(summary = "Listar eventos próximos", 
               description = "Obtiene eventos con fecha mayor o igual a hoy, ordenados por fecha")
    @ApiResponse(responseCode = "200", description = "Eventos próximos obtenidos exitosamente")
    @GetMapping("/proximos")
    public ResponseEntity<List<EventoResponse>> listarEventosProximos(
            @Parameter(description = "Número de página") @RequestParam(required = false, defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página") @RequestParam(required = false, defaultValue = "20") int size) {
        
        log.info("Listando eventos próximos");
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "fecha"));
        List<EventoResponse> eventos = eventoService.buscarEventosProximos(pageable);
        return ResponseEntity.ok(eventos);
    }
    
    @Operation(summary = "Listar eventos gratuitos", 
               description = "Obtiene todos los eventos próximos con entrada gratuita")
    @ApiResponse(responseCode = "200", description = "Eventos gratuitos obtenidos exitosamente")
    @GetMapping("/gratuitos")
    public ResponseEntity<List<EventoResponse>> listarEventosGratuitos(
            @Parameter(description = "Número de página") @RequestParam(required = false, defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página") @RequestParam(required = false, defaultValue = "20") int size) {
        
        log.info("Listando eventos gratuitos");
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "fecha"));
        List<EventoResponse> eventos = eventoService.buscarEventosGratuitos(pageable);
        return ResponseEntity.ok(eventos);
    }
    
    // Endpoints adicionales para estadísticas y categorías
    
    @Operation(summary = "Obtener categorías de eventos", 
               description = "Lista todas las categorías disponibles para eventos")
    @ApiResponse(responseCode = "200", description = "Lista de categorías obtenida exitosamente")
    @GetMapping("/categorias")
    public ResponseEntity<List<String>> obtenerCategorias() {
        // Esta lista debería venir de una configuración o base de datos
        List<String> categorias = List.of(
            "Sociales",
            "Corporativos / Empresariales", 
            "Académicos",
            "Culturales y Artísticos",
            "Deportivos",
            "Comerciales y de Marca",
            "Comunitarios o Gubernamentales",
            "Religiosos",
            "Tecnológicos",
            "Gastronómicos",
            "Medioambientales",
            "Políticos",
            "Virtuales / Híbridos",
            "Benéficos / Solidarios",
            "Inmobiliarios",
            "Turísticos",
            "Familiares",
            "Para adultos"
        );
        
        return ResponseEntity.ok(categorias);
    }
    
    @Operation(summary = "Obtener estadísticas de eventos", 
               description = "Obtiene estadísticas generales sobre los eventos")
    @ApiResponse(responseCode = "200", description = "Estadísticas obtenidas exitosamente")
    @GetMapping("/estadisticas")
    public ResponseEntity<Object> obtenerEstadisticas() {
        // Implementar estadísticas básicas
        return ResponseEntity.ok(Map.of(
            "totalEventosActivos", eventoService.listarEventosActivos().size(),
            "totalEventosDestacados", eventoService.listarEventosDestacados().size(),
            "mensaje", "Estadísticas básicas - implementación completa pendiente"
        ));
    }
    
    // Manejo de excepciones a nivel de controlador
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> handleRuntimeException(RuntimeException e) {
        log.error("Error en EventoController: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(Map.of(
                "error", "Error interno del servidor",
                "mensaje", e.getMessage(),
                "timestamp", System.currentTimeMillis()
            ));
    }
    
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("Argumento inválido: {}", e.getMessage());
        return ResponseEntity.badRequest()
            .body(Map.of(
                "error", "Solicitud inválida",
                "mensaje", e.getMessage(),
                "timestamp", System.currentTimeMillis()
            ));
    }
}