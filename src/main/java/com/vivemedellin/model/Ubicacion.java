package com.vivemedellin.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ubicacion {
    
    @NotBlank(message = "La dirección completa es obligatoria")
    @Size(min = 5, max = 300, message = "La dirección completa debe tener entre 5 y 300 caracteres")
    @Column(name = "direccion_completa", nullable = false)
    private String direccionCompleta;
    
    @NotBlank(message = "La comuna/barrio es obligatoria")
    @Size(min = 2, max = 100, message = "La comuna/barrio debe tener entre 2 y 100 caracteres")
    @Column(name = "comuna_barrio", nullable = false)
    private String comunaBarrio;
    
    @NotBlank(message = "La dirección detallada es obligatoria")
    @Size(min = 5, max = 300, message = "La dirección detallada debe tener entre 5 y 300 caracteres")
    @Column(name = "direccion_detallada", nullable = false)
    private String direccionDetallada;
    
    @Size(max = 500, message = "El enlace del mapa no puede exceder 500 caracteres")
    @Column(name = "enlace_mapa")
    private String enlaceMapa;
}