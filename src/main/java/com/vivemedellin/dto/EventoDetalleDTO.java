package com.vivemedellin.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO completo para mostrar el detalle de un evento.
 * Incluye toda la información necesaria para la página de detalle:
 * - Información básica del evento
 * - Funciones (fechas y horarios)
 * - Ubicación completa con enlace a mapa
 * - Organizador con datos de contacto
 * - Material complementario (imágenes, videos)
 * - Estado del evento (activo, cancelado, finalizado)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventoDetalleDTO {
    
    // ============================================
    // INFORMACIÓN BÁSICA
    // ============================================
    
    private Long id;
    private String titulo;
    private String descripcion;
    private String categoria;
    private String imagenCaratula;
    
    // ============================================
    // FECHAS Y HORARIOS (FUNCIONES)
    // ============================================
    
    /**
     * Lista de funciones del evento con fechas y horarios
     */
    private List<FuncionDTO> funciones;
    
    // ============================================
    // UBICACIÓN COMPLETA
    // ============================================
    
    private UbicacionDetalleDTO ubicacion;
    
    // ============================================
    // CAPACIDAD Y PRECIO
    // ============================================
    
    private Integer aforo;
    private String valorIngreso;
    private Boolean esGratuito;
    
    // ============================================
    // ORGANIZADOR Y CONTACTO
    // ============================================
    
    private OrganizadorDetalleDTO organizador;
    
    // ============================================
    // MODALIDAD Y ESTADO
    // ============================================
    
    private String modalidad; // PRESENCIAL, VIRTUAL, HIBRIDA
    private String estadoEvento; // ACTIVO, CANCELADO, FINALIZADO
    private String mensajeEstado; // Mensaje descriptivo del estado
    private Boolean destacado;
    
    // ============================================
    // MATERIAL COMPLEMENTARIO
    // ============================================
    
    private List<String> imagenes;
    private List<String> videos;
    private List<String> enlaces;
    
    // ============================================
    // INFORMACIÓN COMPLEMENTARIA
    // ============================================
    
    private String requisitos;
    private String recomendaciones;
    private String informacionAdicional;
    
    // ============================================
    // METADATOS
    // ============================================
    
    private LocalDate fechaCreacion;
    private LocalDate fechaActualizacion;
    
    // ============================================
    // ACCIONES ADMINISTRATIVAS (Solo para administradores)
    // ============================================
    
    /**
     * Información sobre las acciones administrativas disponibles para este evento.
     * Solo se incluye cuando el usuario tiene rol de administrador.
     * Indica qué acciones puede realizar (editar, cancelar, destacar).
     */
    private EventoAdminActionsDTO accionesAdmin;
    
    // ============================================
    // CLASES INTERNAS PARA ESTRUCTURAS COMPLEJAS
    // ============================================
    
    /**
     * Información de una función (fecha y horario del evento)
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FuncionDTO {
        private Long id;
        private LocalDate fecha;
        private LocalTime horario;
        private String dia; // Ej: "Sábado 20 de Octubre"
        private Boolean estaFinalizada; // Si la fecha/hora ya pasó
    }
    
    /**
     * Información completa de ubicación con enlace a mapa
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UbicacionDetalleDTO {
        private String direccionCompleta;
        private String comunaBarrio;
        private String ciudad;
        private String departamento;
        
        // Coordenadas para el mapa
        private Double latitud;
        private Double longitud;
        
        // Enlace generado a Google Maps
        private String enlaceMapa;
        
        // Indicaciones adicionales
        private String indicacionesAcceso;
    }
    
    /**
     * Información del organizador con datos de contacto
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrganizadorDetalleDTO {
        private Long id;
        private String nombre;
        private String email;
        private String telefono;
        private String sitioWeb;
        private String descripcion;
        private String logoUrl;
    }
}
