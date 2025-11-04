package com.vivemedellin.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vivemedellin.dto.EventoFiltrosDTO;
import com.vivemedellin.dto.EventoListaDTO;
import com.vivemedellin.dto.EventoMosaicoDTO;
import com.vivemedellin.model.Evento;
import com.vivemedellin.repository.EventoRepository;
import com.vivemedellin.service.EventoService;
import com.vivemedellin.specification.EventoSpecification;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Controlador público para búsqueda de eventos
 * NO requiere autenticación - Accesible para usuarios no registrados
 */
@RestController
@RequestMapping("/api/public/eventos")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Búsqueda Pública de Eventos", description = "Endpoints públicos para búsqueda y consulta de eventos (sin autenticación)")
public class EventoPublicController {

    private final EventoRepository eventoRepository;
    private final EventoService eventoService;

    @GetMapping("/buscar")
    @Operation(
        summary = "Búsqueda avanzada de eventos (PÚBLICA)",
        description = "Busca eventos combinando múltiples filtros. Visible desde la pantalla principal. " +
                     "Busca en título, descripción y nombre del organizador (case-insensitive, ignora acentos). " +
                     "Soporta dos tipos de vista: MOSAICO (20 resultados) y LISTA (50 resultados). " +
                     "Usuarios NO REGISTRADOS pueden utilizar esta búsqueda.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Búsqueda exitosa",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))
            )
        }
    )
    public ResponseEntity<?> buscarEventos(
            @ModelAttribute EventoFiltrosDTO filtros,
            @Parameter(description = "Tipo de vista: MOSAICO (20 por página) o LISTA (50 por página)", example = "MOSAICO")
            @RequestParam(required = false, defaultValue = "MOSAICO") String tipoVista) {
        
        log.info("Búsqueda pública de eventos con filtros: {}, vista: {}", filtros, tipoVista);
        
        // Validar fechas
        if (!filtros.fechasValidas()) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "La fecha de inicio no puede ser posterior a la fecha de fin");
            return ResponseEntity.badRequest().body(error);
        }
        
        // Establecer tipo de vista
        filtros.setTipoVista(tipoVista);
        
        // Construir Specification combinada
        Specification<Evento> spec = EventoSpecification.soloActivos(); // Solo eventos publicados
        
        if (filtros.getTexto() != null && !filtros.getTexto().trim().isEmpty()) {
            spec = spec.and(EventoSpecification.conTexto(filtros.getTexto()));
        }
        
        if (filtros.getUbicacion() != null && !filtros.getUbicacion().trim().isEmpty()) {
            spec = spec.and(EventoSpecification.conUbicacion(filtros.getUbicacion()));
        }
        
        if (filtros.getCategoria() != null && !filtros.getCategoria().trim().isEmpty()) {
            spec = spec.and(EventoSpecification.conCategoria(filtros.getCategoria()));
        }
        
        if (filtros.getFechaDesde() != null) {
            spec = spec.and(EventoSpecification.desdeFecha(filtros.getFechaDesde()));
        }
        
        if (filtros.getFechaHasta() != null) {
            spec = spec.and(EventoSpecification.hastaFecha(filtros.getFechaHasta()));
        }
        
        if (filtros.getGratuito() != null) {
            spec = spec.and(EventoSpecification.esGratuito(filtros.getGratuito()));
        }
        
        if (filtros.getModalidad() != null && !filtros.getModalidad().trim().isEmpty()) {
            spec = spec.and(EventoSpecification.conModalidad(filtros.getModalidad()));
        }
        
        if (filtros.getOrganizador() != null && !filtros.getOrganizador().trim().isEmpty()) {
            spec = spec.and(EventoSpecification.conOrganizador(filtros.getOrganizador()));
        }
        
        if (filtros.getPrecioMinimo() != null || filtros.getPrecioMaximo() != null) {
            spec = spec.and(EventoSpecification.conRangoPrecio(filtros.getPrecioMinimo(), filtros.getPrecioMaximo()));
        }
        
        if (filtros.getHorario() != null && !filtros.getHorario().trim().isEmpty()) {
            spec = spec.and(EventoSpecification.conHorario(filtros.getHorario()));
        }
        
        if (filtros.getServicio() != null && !filtros.getServicio().trim().isEmpty()) {
            spec = spec.and(EventoSpecification.conServicio(filtros.getServicio()));
        }
        
        if (filtros.getDisponible() != null) {
            spec = spec.and(EventoSpecification.esDisponible(filtros.getDisponible()));
        }
        
        // Configurar paginación y ordenamiento
        Sort sort = Sort.by(
            "ASC".equalsIgnoreCase(filtros.getDireccionOrDefault()) ? 
                Sort.Direction.ASC : Sort.Direction.DESC,
            filtros.getOrdenarPorOrDefault()
        );
        
        Pageable pageable = PageRequest.of(
            filtros.getPageOrDefault(),
            filtros.getSizeOrDefault(),
            sort
        );
        
        // Ejecutar búsqueda
        Page<Evento> resultados = eventoRepository.findAll(spec, pageable);
        
        // Convertir según tipo de vista
        if ("LISTA".equalsIgnoreCase(tipoVista)) {
            Page<EventoListaDTO> resultadosLista = resultados.map(this::convertirAEventoLista);
            log.info("Búsqueda completada: {} resultados en vista LISTA", resultadosLista.getTotalElements());
            return ResponseEntity.ok(resultadosLista);
        } else {
            Page<EventoMosaicoDTO> resultadosMosaico = resultados.map(this::convertirAEventoMosaico);
            log.info("Búsqueda completada: {} resultados en vista MOSAICO", resultadosMosaico.getTotalElements());
            return ResponseEntity.ok(resultadosMosaico);
        }
    }

    @GetMapping("/buscar-simple")
    @Operation(
        summary = "Búsqueda simple por palabra clave (PÚBLICA)",
        description = "Busca eventos por una palabra o frase en título, descripción y nombre del organizador. " +
                     "Ignora mayúsculas/minúsculas y acentos. Campo visible desde pantalla principal."
    )
    public ResponseEntity<?> busquedaSimple(
            @Parameter(description = "Palabra o frase a buscar", example = "concierto", required = true)
            @RequestParam String q,
            @Parameter(description = "Número de página (inicia en 0)", example = "0")
            @RequestParam(required = false, defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página", example = "20")
            @RequestParam(required = false, defaultValue = "20") int size,
            @Parameter(description = "Tipo de vista", example = "MOSAICO")
            @RequestParam(required = false, defaultValue = "MOSAICO") String tipoVista) {
        
        log.info("Búsqueda simple pública: '{}', página: {}, tamaño: {}, vista: {}", q, page, size, tipoVista);
        
        if (q == null || q.trim().isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Debes ingresar una palabra o frase para buscar");
            return ResponseEntity.badRequest().body(error);
        }
        
        // Buscar solo en eventos activos
        Specification<Evento> spec = EventoSpecification.soloActivos()
            .and(EventoSpecification.conTexto(q));
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "fecha"));
        Page<Evento> resultados = eventoRepository.findAll(spec, pageable);
        
        // Si no hay resultados
        if (resultados.isEmpty()) {
            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("mensaje", "No se encontraron eventos que coincidan con tu búsqueda.");
            respuesta.put("totalResultados", 0);
            respuesta.put("busqueda", q);
            return ResponseEntity.ok(respuesta);
        }
        
        // Convertir según tipo de vista
        if ("LISTA".equalsIgnoreCase(tipoVista)) {
            return ResponseEntity.ok(resultados.map(this::convertirAEventoLista));
        } else {
            return ResponseEntity.ok(resultados.map(this::convertirAEventoMosaico));
        }
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Obtener detalle completo de un evento (PÚBLICO)",
        description = """
            Obtiene información completa y detallada de un evento específico.
            
            **Información incluida:**
            - Información básica (título, descripción, categoría)
            - Todas las funciones (fechas y horarios múltiples)
            - Ubicación completa con enlace a Google Maps
            - Datos del organizador con información de contacto
            - Capacidad (aforo) y precio de entrada
            - Material complementario (imágenes, videos, enlaces)
            - Estado del evento: ACTIVO, CANCELADO o FINALIZADO
            - Información adicional y recomendaciones
            
            **Estados especiales:**
            - Si el evento está CANCELADO, se muestra el mensaje: "Este evento ha sido cancelado"
            - Si el evento FINALIZÓ (fecha/hora pasada), se muestra: "Evento finalizado"
            
            **Uso típico:**
            Se usa al hacer clic en un resultado de búsqueda para ver el detalle completo
            y poder regresar fácilmente a la vista previa.
            """,
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Evento encontrado",
                content = @Content(schema = @Schema(implementation = com.vivemedellin.dto.EventoDetalleDTO.class))
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Evento no encontrado"
            )
        }
    )
    public ResponseEntity<?> obtenerEventoDetalle(
            @Parameter(description = "ID del evento", required = true, example = "1")
            @PathVariable Long id) {
        
        log.info("Obteniendo detalle público completo del evento ID: {}", id);
        
        // Buscar el evento directamente del repositorio
        Optional<Evento> eventoOpt = eventoRepository.findById(id);
        
        if (eventoOpt.isEmpty()) {
            log.warn("Evento con ID {} no encontrado", id);
            return ResponseEntity.notFound().build();
        }
        
        Evento evento = eventoOpt.get();
        com.vivemedellin.dto.EventoDetalleDTO detalle = eventoService.convertirAEventoDetalle(evento);
        
        return ResponseEntity.ok(detalle);
    }

    @GetMapping("/proximos")
    @Operation(
        summary = "Eventos próximos (PÚBLICO)",
        description = "Obtiene eventos próximos ordenados por fecha. Útil para la pantalla principal."
    )
    public ResponseEntity<?> eventosProximos(
            @Parameter(description = "Número de días hacia adelante", example = "30")
            @RequestParam(required = false, defaultValue = "30") int dias,
            @Parameter(description = "Número de página", example = "0")
            @RequestParam(required = false, defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página", example = "20")
            @RequestParam(required = false, defaultValue = "20") int size,
            @Parameter(description = "Tipo de vista", example = "MOSAICO")
            @RequestParam(required = false, defaultValue = "MOSAICO") String tipoVista) {
        
        log.info("Eventos próximos públicos: {} días", dias);
        
        LocalDate fechaLimite = LocalDate.now().plusDays(dias);
        
        Specification<Evento> spec = EventoSpecification.soloActivos()
            .and(EventoSpecification.proximosEventos())
            .and(EventoSpecification.hastaFecha(fechaLimite));
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "fecha"));
        Page<Evento> resultados = eventoRepository.findAll(spec, pageable);
        
        if ("LISTA".equalsIgnoreCase(tipoVista)) {
            return ResponseEntity.ok(resultados.map(this::convertirAEventoLista));
        } else {
            return ResponseEntity.ok(resultados.map(this::convertirAEventoMosaico));
        }
    }

    @GetMapping("/destacados")
    @Operation(
        summary = "Eventos destacados (PÚBLICO)",
        description = "Obtiene eventos marcados como destacados. Ideal para banner principal."
    )
    public ResponseEntity<?> eventosDestacados(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size) {
        
        log.info("Eventos destacados públicos");
        
        Specification<Evento> spec = EventoSpecification.soloActivos()
            .and(EventoSpecification.esDestacado(true))
            .and(EventoSpecification.proximosEventos());
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "fecha"));
        Page<Evento> resultados = eventoRepository.findAll(spec, pageable);
        
        return ResponseEntity.ok(resultados.map(this::convertirAEventoMosaico));
    }

    /**
     * ENDPOINT 6: Obtener eventos destacados vigentes (CARRUSEL)
     */
    @GetMapping("/destacados-carrusel")
    @Operation(
        summary = "Obtener eventos destacados vigentes para carrusel",
        description = """
            Devuelve hasta 3 eventos destacados que tengan al menos una función con fecha y horario vigentes.
            
            **Características del carrusel:**
            - Máximo 3 eventos simultáneamente
            - Solo eventos PUBLISHED
            - Solo eventos con funciones futuras (fecha >= hoy)
            - Se ordenan por fecha de actualización (más recientes primero)
            - Incluye badge/etiqueta "DESTACADO" en cada evento
            
            **Uso:** Mostrar en la pantalla principal como carrusel de eventos destacados.
            
            **Regla de negocio:** Los eventos dejan de aparecer en el carrusel automáticamente 
            cuando todas sus funciones hayan pasado, incluso si siguen marcados como destacados 
            en la base de datos.
            """,
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Lista de eventos destacados vigentes (0 a 3 eventos)",
                content = @Content(schema = @Schema(implementation = EventoMosaicoDTO.class))
            )
        }
    )
    public ResponseEntity<?> obtenerDestacadosParaCarrusel() {
        log.info("Obteniendo eventos destacados vigentes para carrusel");
        
        try {
            // Obtener eventos destacados vigentes del repositorio
            var eventosDestacados = eventoRepository.findDestacadosVigentes();
            
            // Limitar a máximo 3 eventos
            var eventosLimitados = eventosDestacados.stream()
                .limit(3)
                .toList();
            
            // Convertir a DTO de mosaico (incluye campo destacado)
            var eventosDTO = eventosLimitados.stream()
                .map(this::convertirAEventoMosaico)
                .toList();
            
            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("eventos", eventosDTO);
            respuesta.put("cantidad", eventosDTO.size());
            respuesta.put("mensaje", eventosDTO.isEmpty() ? 
                "No hay eventos destacados vigentes en este momento" : 
                "Eventos destacados cargados exitosamente");
            
            log.info("Se encontraron {} eventos destacados vigentes", eventosDTO.size());
            
            return ResponseEntity.ok(respuesta);
            
        } catch (Exception e) {
            log.error("Error al obtener eventos destacados vigentes: {}", e.getMessage());
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Error al cargar eventos destacados");
            error.put("mensaje", "Ocurrió un error al obtener los eventos destacados. Intente nuevamente.");
            return ResponseEntity.internalServerError().body(error);
        }
    }

    // ========================================================================
    // MÉTODOS DE CONVERSIÓN
    // ========================================================================

    private EventoMosaicoDTO convertirAEventoMosaico(Evento evento) {
        LocalTime horaEvento = evento.getFunciones().isEmpty() ? null : 
            evento.getFunciones().get(0).getHorario();
        
        boolean disponible = evento.getStatus() == Evento.EstadoEvento.PUBLISHED &&
                           evento.getFecha().isAfter(LocalDate.now().minusDays(1));
        
        return EventoMosaicoDTO.builder()
            .id(evento.getId())
            .imagenCaratula(evento.getImagenCaratula())
            .titulo(evento.getTitulo())
            .categoria(evento.getCategoria())
            .fechaEvento(evento.getFecha())
            .horaEvento(horaEvento)
            .ubicacion(evento.getUbicacion() != null ? evento.getUbicacion().getComunaBarrio() : null)
            .direccionCompleta(evento.getUbicacion() != null ? evento.getUbicacion().getDireccionCompleta() : null)
            .nombreOrganizador(evento.getOrganizador() != null ? evento.getOrganizador().getNombre() : null)
            .valorIngreso(evento.getValorIngreso())
            .destacado(evento.getDestacado())
            .modalidad(evento.getModalidad() != null ? evento.getModalidad().name() : null)
            .disponible(disponible)
            .build();
    }

    private EventoListaDTO convertirAEventoLista(Evento evento) {
        LocalTime horaEvento = evento.getFunciones().isEmpty() ? null : 
            evento.getFunciones().get(0).getHorario();
        
        boolean disponible = evento.getStatus() == Evento.EstadoEvento.PUBLISHED &&
                           evento.getFecha().isAfter(LocalDate.now().minusDays(1));
        
        return EventoListaDTO.builder()
            .id(evento.getId())
            .titulo(evento.getTitulo())
            .fechaEvento(evento.getFecha())
            .horaEvento(horaEvento)
            .ubicacion(evento.getUbicacion() != null ? evento.getUbicacion().getComunaBarrio() : null)
            .direccionCompleta(evento.getUbicacion() != null ? evento.getUbicacion().getDireccionCompleta() : null)
            .nombreOrganizador(evento.getOrganizador() != null ? evento.getOrganizador().getNombre() : null)
            .categoria(evento.getCategoria())
            .valorIngreso(evento.getValorIngreso())
            .destacado(evento.getDestacado())
            .disponible(disponible)
            .build();
    }
}
