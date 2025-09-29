package com.vivemedellin.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.vivemedellin.model.Modalidad;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ActualizarEventoRequest {
    
    @Size(min = 5, max = 200, message = "El título debe tener entre 5 y 200 caracteres")
    private String titulo;
    
    @Size(min = 10, max = 2000, message = "La descripción debe tener entre 10 y 2000 caracteres")
    private String descripcion;
    
    private LocalDate fecha;
    
    private LocalTime horario;
    
    private String categoria;
    
    private Modalidad modalidad;
    
    @Min(value = 1, message = "El aforo mínimo es 1")
    private Integer aforo;
    
    private String valorIngreso;
    
    private Boolean destacado;
    
    @Valid
    private UbicacionDTO ubicacion;
    
    @Valid
    private OrganizadorDTO organizador;
    
    private String imagenCaratula;
    
    private List<String> serviciosAdicionales;
    
    private List<FuncionDTO> funciones;
}