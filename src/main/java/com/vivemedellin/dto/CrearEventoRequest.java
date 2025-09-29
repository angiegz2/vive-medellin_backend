package com.vivemedellin.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.vivemedellin.model.Modalidad;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CrearEventoRequest {
    
    @NotBlank(message = "El título es obligatorio")
    @Size(min = 5, max = 200, message = "El título debe tener entre 5 y 200 caracteres")
    private String titulo;
    
    @NotBlank(message = "La descripción es obligatoria")
    @Size(min = 10, max = 2000, message = "La descripción debe tener entre 10 y 2000 caracteres")
    private String descripcion;
    
    @NotNull(message = "La fecha es obligatoria")
    private LocalDate fecha;
    
    @NotNull(message = "El horario es obligatorio")
    private LocalTime horario;
    
    @NotBlank(message = "La categoría es obligatoria")
    private String categoria;
    
    @NotNull(message = "La modalidad es obligatoria")
    private Modalidad modalidad;
    
    @Min(value = 1, message = "El aforo mínimo es 1")
    private Integer aforo;
    
    @NotBlank(message = "El valor de ingreso es obligatorio")
    private String valorIngreso;
    
    private Boolean destacado = false;
    
    @Valid
    @NotNull(message = "La ubicación es obligatoria")
    private UbicacionDTO ubicacion;
    
    @Valid
    @NotNull(message = "El organizador es obligatorio")
    private OrganizadorDTO organizador;
    
    private String imagenCaratula;
    
    private List<String> serviciosAdicionales;
    
    private List<FuncionDTO> funciones;
}