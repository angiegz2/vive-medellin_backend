package com.vivemedellin.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.vivemedellin.model.Funcion;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FuncionDTO {
    
    private Long id;
    
    @NotNull(message = "El número de función es obligatorio")
    @Min(value = 1, message = "El número de función debe ser mayor a 0")
    private Integer numeroFuncion;
    
    @NotNull(message = "La fecha es obligatoria")
    private LocalDate fecha;
    
    @NotNull(message = "El horario es obligatorio")
    private LocalTime horario;
    
    private Funcion.EstadoFuncion status;
    
    // Metadatos de cancelación
    private LocalDateTime cancelledAt;
    private String cancelledBy;
    
    // Metadatos de auditoría
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}