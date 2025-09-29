package com.vivemedellin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UbicacionDTO {
    
    @NotBlank(message = "La dirección completa es obligatoria")
    @Size(min = 5, max = 300, message = "La dirección completa debe tener entre 5 y 300 caracteres")
    private String direccionCompleta;
    
    @NotBlank(message = "La comuna/barrio es obligatoria")
    @Size(min = 2, max = 100, message = "La comuna/barrio debe tener entre 2 y 100 caracteres")
    private String comunaBarrio;
    
    @NotBlank(message = "La dirección detallada es obligatoria")
    @Size(min = 5, max = 300, message = "La dirección detallada debe tener entre 5 y 300 caracteres")
    private String direccionDetallada;
    
    @Size(max = 500, message = "El enlace del mapa no puede exceder 500 caracteres")
    private String enlaceMapa;
}