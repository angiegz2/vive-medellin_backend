package com.vivemedellin.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import com.vivemedellin.model.Evento;
import com.vivemedellin.model.Modalidad;

import lombok.Data;

@Data
public class EventoResponse {
    
    private Long id;
    private String titulo;
    private String descripcion;
    private LocalDate fecha;
    private LocalTime horario;
    private String categoria;
    private Modalidad modalidad;
    private Integer aforo;
    private String valorIngreso;
    private Boolean destacado;
    private UbicacionDTO ubicacion;
    private OrganizadorDTO organizador;
    private String imagenCaratula;
    private List<String> serviciosAdicionales;
    private Evento.EstadoEvento status;
    private List<FuncionDTO> funciones;
    
    // Metadatos
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String lastEditedBy;
    private LocalDateTime lastEditedAt;
    private LocalDateTime cancelledAt;
    private String cancelledBy;
    
    // Estadísticas
    private Double calificacionPromedio;
    private Integer totalValoraciones;
    private Integer totalComentarios;
    private Integer totalFuncionesActivas;
}