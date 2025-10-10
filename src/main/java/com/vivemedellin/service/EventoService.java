package com.vivemedellin.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vivemedellin.dto.ActualizarEventoRequest;
import com.vivemedellin.dto.CrearEventoRequest;
import com.vivemedellin.dto.EventoFiltrosDTO;
import com.vivemedellin.dto.EventoResponse;
import com.vivemedellin.dto.FuncionDTO;
import com.vivemedellin.dto.OrganizadorDTO;
import com.vivemedellin.dto.UbicacionDTO;
import com.vivemedellin.model.Evento;
import com.vivemedellin.model.Funcion;
import com.vivemedellin.model.Organizador;
import com.vivemedellin.model.Ubicacion;
import com.vivemedellin.repository.EventoRepository;
import com.vivemedellin.repository.FuncionRepository;
import com.vivemedellin.repository.UsuarioRepository;
import com.vivemedellin.specification.EventoSpecification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class EventoService {
    
    private final EventoRepository eventoRepository;
    private final FuncionRepository funcionRepository;
    private final UsuarioRepository usuarioRepository;
    
    /**
     * Crear un nuevo evento
     */
    public EventoResponse crearEvento(CrearEventoRequest request, String createdBy) {
        log.info("Creando nuevo evento: {}", request.getTitulo());
        
        Evento evento = new Evento();
        mapearDatosBasicos(evento, request);
        evento.setCreatedBy(createdBy);
        
        // Buscar usuario creador si existe
        usuarioRepository.findByEmail(createdBy)
            .ifPresent(evento::setCreatedByUser);
        
        // Guardar evento
        Evento eventoGuardado = eventoRepository.save(evento);
        
        // Crear funciones si se proporcionaron
        if (request.getFunciones() != null && !request.getFunciones().isEmpty()) {
            crearFunciones(eventoGuardado, request.getFunciones());
        } else {
            // Crear función por defecto con los datos del evento
            crearFuncionPorDefecto(eventoGuardado, request);
        }
        
        log.info("Evento creado con ID: {}", eventoGuardado.getId());
        return convertirAEventoResponse(eventoGuardado);
    }
    
    /**
     * Obtener evento por ID
     */
    @Transactional(readOnly = true)
    public Optional<EventoResponse> obtenerEventoPorId(Long id) {
        return eventoRepository.findById(id)
            .map(this::convertirAEventoResponse);
    }
    
    /**
     * Obtener evento por ID incluyendo cancelados
     */
    @Transactional(readOnly = true)
    public Optional<EventoResponse> obtenerEventoPorIdCompleto(Long id) {
        return eventoRepository.findByIdIncludingCancelled(id)
            .map(this::convertirAEventoResponse);
    }
    
    /**
     * Actualizar evento
     */
    public EventoResponse actualizarEvento(Long id, ActualizarEventoRequest request, String editedBy) {
        log.info("Actualizando evento ID: {}", id);
        
        Evento evento = eventoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Evento no encontrado con ID: " + id));
        
        // Actualizar solo campos que no son null en el request
        if (request.getTitulo() != null) evento.setTitulo(request.getTitulo());
        if (request.getDescripcion() != null) evento.setDescripcion(request.getDescripcion());
        if (request.getFecha() != null) evento.setFecha(request.getFecha());
        if (request.getHorario() != null) evento.setHorario(request.getHorario());
        if (request.getCategoria() != null) evento.setCategoria(request.getCategoria());
        if (request.getModalidad() != null) evento.setModalidad(request.getModalidad());
        if (request.getAforo() != null) evento.setAforo(request.getAforo());
        if (request.getValorIngreso() != null) evento.setValorIngreso(request.getValorIngreso());
        if (request.getDestacado() != null) evento.setDestacado(request.getDestacado());
        if (request.getImagenCaratula() != null) evento.setImagenCaratula(request.getImagenCaratula());
        if (request.getServiciosAdicionales() != null) evento.setServiciosAdicionales(request.getServiciosAdicionales());
        
        if (request.getUbicacion() != null) {
            if (evento.getUbicacion() == null) {
                evento.setUbicacion(new Ubicacion());
            }
            mapearUbicacion(evento.getUbicacion(), request.getUbicacion());
        }
        
        if (request.getOrganizador() != null) {
            if (evento.getOrganizador() == null) {
                evento.setOrganizador(new Organizador());
            }
            mapearOrganizador(evento.getOrganizador(), request.getOrganizador());
        }
        
        // Actualizar metadatos de edición
        evento.setLastEditedBy(editedBy);
        evento.setLastEditedAt(LocalDateTime.now());
        usuarioRepository.findByEmail(editedBy)
            .ifPresent(evento::setLastEditedByUser);
        
        // Actualizar funciones si se proporcionaron
        if (request.getFunciones() != null) {
            actualizarFunciones(evento, request.getFunciones());
        }
        
        Evento eventoActualizado = eventoRepository.save(evento);
        log.info("Evento actualizado con ID: {}", eventoActualizado.getId());
        
        return convertirAEventoResponse(eventoActualizado);
    }
    
    /**
     * Cancelar evento (soft delete)
     */
    public EventoResponse cancelarEvento(Long id, String cancelledBy) {
        log.info("Cancelando evento ID: {}", id);
        
        Evento evento = eventoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Evento no encontrado con ID: " + id));
        
        evento.cancelar(cancelledBy);
        usuarioRepository.findByEmail(cancelledBy)
            .ifPresent(evento::setCancelledByUser);
        
        // También cancelar todas las funciones activas
        evento.getFunciones().forEach(funcion -> {
            if (funcion.getStatus() == Funcion.EstadoFuncion.PUBLISHED) {
                funcion.cancelar(cancelledBy);
            }
        });
        
        Evento eventoCancelado = eventoRepository.save(evento);
        log.info("Evento cancelado con ID: {}", eventoCancelado.getId());
        
        return convertirAEventoResponse(eventoCancelado);
    }
    
    /**
     * Reactivar evento cancelado
     */
    public EventoResponse reactivarEvento(Long id, String reactivatedBy) {
        log.info("Reactivando evento ID: {}", id);
        
        Evento evento = eventoRepository.findByIdIncludingCancelled(id)
            .orElseThrow(() -> new RuntimeException("Evento no encontrado con ID: " + id));
        
        evento.reactivar();
        evento.setLastEditedBy(reactivatedBy);
        usuarioRepository.findByEmail(reactivatedBy)
            .ifPresent(evento::setLastEditedByUser);
        
        Evento eventoReactivado = eventoRepository.save(evento);
        log.info("Evento reactivado con ID: {}", eventoReactivado.getId());
        
        return convertirAEventoResponse(eventoReactivado);
    }
    
    /**
     * Destacar evento
     */
    public EventoResponse destacarEvento(Long id, String editedBy) {
        log.info("Destacando evento ID: {}", id);
        
        Evento evento = eventoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Evento no encontrado con ID: " + id));
        
        evento.destacar();
        evento.setLastEditedBy(editedBy);
        usuarioRepository.findByEmail(editedBy)
            .ifPresent(evento::setLastEditedByUser);
        
        Evento eventoDestacado = eventoRepository.save(evento);
        log.info("Evento destacado con ID: {}", eventoDestacado.getId());
        
        return convertirAEventoResponse(eventoDestacado);
    }
    
    /**
     * Quitar destaque de evento
     */
    public EventoResponse quitarDestaque(Long id, String editedBy) {
        log.info("Quitando destaque del evento ID: {}", id);
        
        Evento evento = eventoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Evento no encontrado con ID: " + id));
        
        evento.quitarDestaque();
        evento.setLastEditedBy(editedBy);
        usuarioRepository.findByEmail(editedBy)
            .ifPresent(evento::setLastEditedByUser);
        
        Evento eventoActualizado = eventoRepository.save(evento);
        log.info("Destaque removido del evento ID: {}", eventoActualizado.getId());
        
        return convertirAEventoResponse(eventoActualizado);
    }
    
    /**
     * Listar todos los eventos activos
     */
    @Transactional(readOnly = true)
    public List<EventoResponse> listarEventosActivos() {
        return eventoRepository.findByStatus(Evento.EstadoEvento.PUBLISHED)
            .stream()
            .map(this::convertirAEventoResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * Listar eventos destacados
     */
    @Transactional(readOnly = true)
    public List<EventoResponse> listarEventosDestacados() {
        return eventoRepository.findByDestacadoTrue()
            .stream()
            .filter(evento -> evento.getStatus() == Evento.EstadoEvento.PUBLISHED)
            .map(this::convertirAEventoResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * Buscar eventos con filtros
     */
    @Transactional(readOnly = true)
    public Page<EventoResponse> buscarEventosConFiltros(
            String categoria, LocalDate fechaInicio, LocalDate fechaFin, 
            String ubicacion, Boolean destacado, Pageable pageable) {
        
        Page<Evento> eventos = eventoRepository.findEventosConFiltros(
            categoria, fechaInicio, fechaFin, ubicacion, destacado,
            Evento.EstadoEvento.PUBLISHED, pageable
        );
        
        return eventos.map(this::convertirAEventoResponse);
    }
    
    /**
     * Búsqueda de texto completo
     */
    @Transactional(readOnly = true)
    public List<EventoResponse> busquedaTextoCompleto(String texto) {
        return eventoRepository.busquedaTextoCompleto(texto)
            .stream()
            .filter(evento -> evento.getStatus() == Evento.EstadoEvento.PUBLISHED)
            .map(this::convertirAEventoResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * Búsqueda avanzada con filtros usando Specifications
     * Soporta filtrado por: texto, ubicación, categoría, fechas, destacado, gratuito, modalidad
     */
    @Transactional(readOnly = true)
    public Page<EventoResponse> busquedaAvanzada(EventoFiltrosDTO filtros) {
        log.info("Búsqueda avanzada con filtros: {}", filtros);
        
        // Validar que las fechas sean coherentes
        if (!filtros.fechasValidas()) {
            throw new IllegalArgumentException("La fecha inicial no puede ser posterior a la fecha final");
        }
        
        // Construir Specification combinada
        Specification<Evento> spec = EventoSpecification.busquedaAvanzada(
            filtros.getTexto(),
            filtros.getUbicacion(),
            filtros.getCategoria(),
            filtros.getFechaDesde(),
            filtros.getFechaHasta(),
            filtros.getDestacado(),
            filtros.getGratuito(),
            filtros.getModalidad(),
            filtros.getSoloActivos() != null ? filtros.getSoloActivos() : true
        );
        
        // Agregar filtro de organizador si se especificó
        if (filtros.getOrganizador() != null && !filtros.getOrganizador().trim().isEmpty()) {
            spec = spec.and(EventoSpecification.conOrganizador(filtros.getOrganizador()));
        }
        
        // Construir Pageable con ordenamiento
        Sort sort = crearOrdenamiento(filtros.getOrdenarPorOrDefault(), filtros.getDireccionOrDefault());
        Pageable pageable = PageRequest.of(
            filtros.getPageOrDefault(), 
            filtros.getSizeOrDefault(), 
            sort
        );
        
        // Ejecutar búsqueda
        Page<Evento> eventos = eventoRepository.findAll(spec, pageable);
        
        log.info("Búsqueda avanzada encontró {} eventos de {} totales", 
            eventos.getNumberOfElements(), eventos.getTotalElements());
        
        return eventos.map(this::convertirAEventoResponse);
    }
    
    /**
     * Búsqueda simple por palabras clave
     * Busca en título, descripción, categoría, organizador y ubicación
     */
    @Transactional(readOnly = true)
    public Page<EventoResponse> buscarPorPalabrasClaves(String keywords, Pageable pageable) {
        log.info("Búsqueda por palabras clave: {}", keywords);
        
        if (keywords == null || keywords.trim().isEmpty()) {
            throw new IllegalArgumentException("Las palabras clave no pueden estar vacías");
        }
        
        Specification<Evento> spec = EventoSpecification.busquedaPorPalabrasClaves(keywords)
                .and(EventoSpecification.soloActivos());
        
        Page<Evento> eventos = eventoRepository.findAll(spec, pageable);
        
        log.info("Búsqueda por palabras clave encontró {} eventos", eventos.getTotalElements());
        
        return eventos.map(this::convertirAEventoResponse);
    }
    
    /**
     * Buscar eventos próximos (fecha >= hoy)
     */
    @Transactional(readOnly = true)
    public List<EventoResponse> buscarEventosProximos(Pageable pageable) {
        log.info("Buscando eventos próximos");
        
        Specification<Evento> spec = EventoSpecification.proximosEventos()
                .and(EventoSpecification.soloActivos());
        
        Page<Evento> eventos = eventoRepository.findAll(spec, pageable);
        
        return eventos.stream()
                .map(this::convertirAEventoResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Buscar eventos por ubicación (comuna/barrio)
     */
    @Transactional(readOnly = true)
    public List<EventoResponse> buscarPorUbicacion(String ubicacion) {
        log.info("Buscando eventos por ubicación: {}", ubicacion);
        
        if (ubicacion == null || ubicacion.trim().isEmpty()) {
            throw new IllegalArgumentException("La ubicación no puede estar vacía");
        }
        
        Specification<Evento> spec = EventoSpecification.conUbicacion(ubicacion)
                .and(EventoSpecification.soloActivos())
                .and(EventoSpecification.proximosEventos());
        
        List<Evento> eventos = eventoRepository.findAll(spec, Sort.by(Sort.Direction.ASC, "fecha"));
        
        return eventos.stream()
                .map(this::convertirAEventoResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Buscar eventos por fecha específica
     */
    @Transactional(readOnly = true)
    public List<EventoResponse> buscarPorFecha(LocalDate fecha) {
        log.info("Buscando eventos para la fecha: {}", fecha);
        
        if (fecha == null) {
            throw new IllegalArgumentException("La fecha no puede ser nula");
        }
        
        Specification<Evento> spec = EventoSpecification.entreRangoFechas(fecha, fecha)
                .and(EventoSpecification.soloActivos());
        
        List<Evento> eventos = eventoRepository.findAll(spec, Sort.by(Sort.Direction.ASC, "horario"));
        
        return eventos.stream()
                .map(this::convertirAEventoResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Buscar eventos por rango de fechas
     */
    @Transactional(readOnly = true)
    public List<EventoResponse> buscarPorRangoFechas(LocalDate fechaDesde, LocalDate fechaHasta) {
        log.info("Buscando eventos entre {} y {}", fechaDesde, fechaHasta);
        
        if (fechaDesde != null && fechaHasta != null && fechaDesde.isAfter(fechaHasta)) {
            throw new IllegalArgumentException("La fecha inicial no puede ser posterior a la fecha final");
        }
        
        Specification<Evento> spec = EventoSpecification.entreRangoFechas(fechaDesde, fechaHasta)
                .and(EventoSpecification.soloActivos());
        
        List<Evento> eventos = eventoRepository.findAll(spec, Sort.by(Sort.Direction.ASC, "fecha"));
        
        return eventos.stream()
                .map(this::convertirAEventoResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Buscar eventos gratuitos
     */
    @Transactional(readOnly = true)
    public List<EventoResponse> buscarEventosGratuitos(Pageable pageable) {
        log.info("Buscando eventos gratuitos");
        
        Specification<Evento> spec = EventoSpecification.esGratuito(true)
                .and(EventoSpecification.soloActivos())
                .and(EventoSpecification.proximosEventos());
        
        Page<Evento> eventos = eventoRepository.findAll(spec, pageable);
        
        return eventos.stream()
                .map(this::convertirAEventoResponse)
                .collect(Collectors.toList());
    }
    
    // Método auxiliar para crear ordenamiento
    private Sort crearOrdenamiento(String campo, String direccion) {
        Sort.Direction dir = "DESC".equalsIgnoreCase(direccion) ? 
            Sort.Direction.DESC : Sort.Direction.ASC;
        
        return Sort.by(dir, campo);
    }
    
    // Métodos privados de apoyo
    
    private void mapearDatosBasicos(Evento evento, CrearEventoRequest request) {
        evento.setTitulo(request.getTitulo());
        evento.setDescripcion(request.getDescripcion());
        evento.setFecha(request.getFecha());
        evento.setHorario(request.getHorario());
        evento.setCategoria(request.getCategoria());
        evento.setModalidad(request.getModalidad());
        evento.setAforo(request.getAforo());
        evento.setValorIngreso(request.getValorIngreso());
        evento.setDestacado(request.getDestacado());
        evento.setImagenCaratula(request.getImagenCaratula());
        evento.setServiciosAdicionales(request.getServiciosAdicionales());
        
        // Mapear ubicación
        Ubicacion ubicacion = new Ubicacion();
        mapearUbicacion(ubicacion, request.getUbicacion());
        evento.setUbicacion(ubicacion);
        
        // Mapear organizador
        Organizador organizador = new Organizador();
        mapearOrganizador(organizador, request.getOrganizador());
        evento.setOrganizador(organizador);
    }
    
    private void mapearUbicacion(Ubicacion ubicacion, UbicacionDTO dto) {
        ubicacion.setDireccionCompleta(dto.getDireccionCompleta());
        ubicacion.setComunaBarrio(dto.getComunaBarrio());
        ubicacion.setDireccionDetallada(dto.getDireccionDetallada());
        ubicacion.setEnlaceMapa(dto.getEnlaceMapa());
    }
    
    private void mapearOrganizador(Organizador organizador, OrganizadorDTO dto) {
        organizador.setNombre(dto.getNombre());
        organizador.setCelular(dto.getCelular());
        organizador.setIdentificacion(dto.getIdentificacion());
        organizador.setEmail(dto.getEmail());
    }
    
    private void crearFunciones(Evento evento, List<FuncionDTO> funcionesDTO) {
        for (FuncionDTO funcionDTO : funcionesDTO) {
            Funcion funcion = new Funcion();
            funcion.setEvento(evento);
            funcion.setNumeroFuncion(funcionDTO.getNumeroFuncion());
            funcion.setFecha(funcionDTO.getFecha());
            funcion.setHorario(funcionDTO.getHorario());
            funcion.setStatus(Funcion.EstadoFuncion.PUBLISHED);
            
            funcionRepository.save(funcion);
            evento.agregarFuncion(funcion);
        }
    }
    
    private void crearFuncionPorDefecto(Evento evento, CrearEventoRequest request) {
        Funcion funcion = new Funcion();
        funcion.setEvento(evento);
        funcion.setNumeroFuncion(1);
        funcion.setFecha(request.getFecha());
        funcion.setHorario(request.getHorario());
        funcion.setStatus(Funcion.EstadoFuncion.PUBLISHED);
        
        funcionRepository.save(funcion);
        evento.agregarFuncion(funcion);
    }
    
    private void actualizarFunciones(Evento evento, List<FuncionDTO> funcionesDTO) {
        // Esto es una implementación simplificada
        // En una implementación completa, habría que manejar actualizaciones, eliminaciones, etc.
        List<Funcion> funcionesExistentes = funcionRepository.findByEventoIdOrderByNumeroFuncionAsc(evento.getId());
        
        // Por simplicidad, eliminamos las funciones existentes y creamos las nuevas
        funcionRepository.deleteAll(funcionesExistentes);
        evento.getFunciones().clear();
        
        crearFunciones(evento, funcionesDTO);
    }
    
    private EventoResponse convertirAEventoResponse(Evento evento) {
        EventoResponse response = new EventoResponse();
        
        response.setId(evento.getId());
        response.setTitulo(evento.getTitulo());
        response.setDescripcion(evento.getDescripcion());
        response.setFecha(evento.getFecha());
        response.setHorario(evento.getHorario());
        response.setCategoria(evento.getCategoria());
        response.setModalidad(evento.getModalidad());
        response.setAforo(evento.getAforo());
        response.setValorIngreso(evento.getValorIngreso());
        response.setDestacado(evento.getDestacado());
        response.setImagenCaratula(evento.getImagenCaratula());
        response.setServiciosAdicionales(evento.getServiciosAdicionales());
        response.setStatus(evento.getStatus());
        
        // Mapear ubicación
        if (evento.getUbicacion() != null) {
            UbicacionDTO ubicacionDTO = new UbicacionDTO();
            ubicacionDTO.setDireccionCompleta(evento.getUbicacion().getDireccionCompleta());
            ubicacionDTO.setComunaBarrio(evento.getUbicacion().getComunaBarrio());
            ubicacionDTO.setDireccionDetallada(evento.getUbicacion().getDireccionDetallada());
            ubicacionDTO.setEnlaceMapa(evento.getUbicacion().getEnlaceMapa());
            response.setUbicacion(ubicacionDTO);
        }
        
        // Mapear organizador
        if (evento.getOrganizador() != null) {
            OrganizadorDTO organizadorDTO = new OrganizadorDTO();
            organizadorDTO.setNombre(evento.getOrganizador().getNombre());
            organizadorDTO.setCelular(evento.getOrganizador().getCelular());
            organizadorDTO.setIdentificacion(evento.getOrganizador().getIdentificacion());
            organizadorDTO.setEmail(evento.getOrganizador().getEmail());
            response.setOrganizador(organizadorDTO);
        }
        
        // Mapear funciones
        List<FuncionDTO> funcionesDTO = evento.getFunciones().stream()
            .map(this::convertirAFuncionDTO)
            .collect(Collectors.toList());
        response.setFunciones(funcionesDTO);
        
        // Metadatos
        response.setCreatedAt(evento.getCreatedAt());
        response.setUpdatedAt(evento.getUpdatedAt());
        response.setCreatedBy(evento.getCreatedBy());
        response.setLastEditedBy(evento.getLastEditedBy());
        response.setLastEditedAt(evento.getLastEditedAt());
        response.setCancelledAt(evento.getCancelledAt());
        response.setCancelledBy(evento.getCancelledBy());
        
        // Estadísticas
        response.setCalificacionPromedio(evento.getCalificacionPromedio());
        response.setTotalValoraciones(evento.getTotalValoraciones());
        response.setTotalComentarios(evento.getTotalComentarios());
        response.setTotalFuncionesActivas(
            (int) evento.getFunciones().stream()
                .filter(f -> f.getStatus() == Funcion.EstadoFuncion.PUBLISHED)
                .count()
        );
        
        return response;
    }
    
    private FuncionDTO convertirAFuncionDTO(Funcion funcion) {
        FuncionDTO dto = new FuncionDTO();
        dto.setId(funcion.getId());
        dto.setNumeroFuncion(funcion.getNumeroFuncion());
        dto.setFecha(funcion.getFecha());
        dto.setHorario(funcion.getHorario());
        dto.setStatus(funcion.getStatus());
        dto.setCancelledAt(funcion.getCancelledAt());
        dto.setCancelledBy(funcion.getCancelledBy());
        dto.setCreatedAt(funcion.getCreatedAt());
        dto.setUpdatedAt(funcion.getUpdatedAt());
        return dto;
    }
}