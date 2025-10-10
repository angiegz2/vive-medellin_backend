package com.vivemedellin.dto;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para filtros de búsqueda avanzada de eventos
 * Permite combinar múltiples criterios de búsqueda
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Filtros para búsqueda avanzada de eventos")
public class EventoFiltrosDTO {

    @Schema(description = "Texto para buscar en título, descripción o categoría", example = "concierto")
    private String texto;

    @Schema(description = "Ubicación (comuna, barrio o dirección)", example = "Poblado")
    private String ubicacion;

    @Schema(description = "Categoría específica del evento", example = "Culturales y Artísticos")
    private String categoria;

    @Schema(description = "Fecha inicial del rango de búsqueda", example = "2025-10-01")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaDesde;

    @Schema(description = "Fecha final del rango de búsqueda", example = "2025-12-31")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaHasta;

    @Schema(description = "Solo eventos destacados", example = "true")
    private Boolean destacado;

    @Schema(description = "Solo eventos gratuitos", example = "true")
    private Boolean gratuito;

    @Schema(description = "Modalidad del evento", example = "PRESENCIAL", allowableValues = {"PRESENCIAL", "VIRTUAL", "HIBRIDA"})
    private String modalidad;

    @Schema(description = "Nombre del organizador", example = "Alcaldía de Medellín")
    private String organizador;

    @Schema(description = "Solo eventos activos/publicados", example = "true")
    private Boolean soloActivos;

    @Schema(description = "Número de página (inicia en 0)", example = "0")
    private Integer page;

    @Schema(description = "Tamaño de página", example = "10")
    private Integer size;

    @Schema(description = "Campo para ordenar", example = "fecha", allowableValues = {"fecha", "titulo", "destacado", "createdAt"})
    private String ordenarPor;

    @Schema(description = "Dirección de ordenamiento", example = "ASC", allowableValues = {"ASC", "DESC"})
    private String direccion;

    /**
     * Verifica si hay al menos un filtro aplicado
     * 
     * @return true si hay filtros, false si está vacío
     */
    public boolean tieneFiltros() {
        return (texto != null && !texto.trim().isEmpty()) ||
               (ubicacion != null && !ubicacion.trim().isEmpty()) ||
               (categoria != null && !categoria.trim().isEmpty()) ||
               fechaDesde != null ||
               fechaHasta != null ||
               destacado != null ||
               gratuito != null ||
               (modalidad != null && !modalidad.trim().isEmpty()) ||
               (organizador != null && !organizador.trim().isEmpty());
    }

    /**
     * Obtiene el tamaño de página con valor por defecto
     * 
     * @return tamaño de página (por defecto 10)
     */
    public int getSizeOrDefault() {
        if (size == null || size <= 0) {
            return 10;
        }
        return size;
    }

    /**
     * Obtiene el número de página con valor por defecto
     * 
     * @return número de página (por defecto 0)
     */
    public int getPageOrDefault() {
        if (page == null || page < 0) {
            return 0;
        }
        return page;
    }

    /**
     * Obtiene el campo de ordenamiento con valor por defecto
     * 
     * @return campo de ordenamiento (por defecto "fecha")
     */
    public String getOrdenarPorOrDefault() {
        return ordenarPor != null && !ordenarPor.trim().isEmpty() ? ordenarPor : "fecha";
    }

    /**
     * Obtiene la dirección de ordenamiento con valor por defecto
     * 
     * @return dirección (por defecto "ASC")
     */
    public String getDireccionOrDefault() {
        return direccion != null && !direccion.trim().isEmpty() ? direccion.toUpperCase() : "ASC";
    }

    /**
     * Valida que las fechas sean coherentes
     * 
     * @return true si las fechas son válidas
     */
    public boolean fechasValidas() {
        if (fechaDesde != null && fechaHasta != null) {
            return !fechaDesde.isAfter(fechaHasta);
        }
        return true;
    }
}
