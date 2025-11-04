package com.vivemedellin.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO optimizado para vista de mosaico (tarjetas)
 * Incluye: Imagen, título, categoría, fecha, ubicación, horario, organizador
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos de evento para vista de mosaico (20 por página)")
public class EventoMosaicoDTO {

    @Schema(description = "ID del evento", example = "1")
    private Long id;

    @Schema(description = "Imagen de carátula del evento", example = "https://example.com/imagen.jpg")
    private String imagenCaratula;

    @Schema(description = "Título del evento", example = "Festival de Rock")
    private String titulo;

    @Schema(description = "Categoría del evento", example = "Música")
    private String categoria;

    @Schema(description = "Fecha del evento", example = "2025-10-20")
    private LocalDate fechaEvento;

    @Schema(description = "Hora del evento", example = "18:00")
    private LocalTime horaEvento;

    @Schema(description = "Comuna o barrio", example = "El Poblado")
    private String ubicacion;

    @Schema(description = "Dirección completa", example = "Parque Lleras, Calle 10")
    private String direccionCompleta;

    @Schema(description = "Nombre del organizador", example = "Secretaría de Cultura")
    private String nombreOrganizador;

    @Schema(description = "Precio de entrada", example = "Gratuito")
    private String valorIngreso;

    @Schema(description = "Evento destacado", example = "true")
    private Boolean destacado;

    @Schema(description = "Modalidad del evento", example = "PRESENCIAL")
    private String modalidad;

    @Schema(description = "Disponible para asistir", example = "true")
    private Boolean disponible;
}
