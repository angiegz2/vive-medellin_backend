package com.vivemedellin.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Organizador {
    
    @NotBlank(message = "El nombre del organizador es obligatorio")
    @Size(min = 2, max = 200, message = "El nombre debe tener entre 2 y 200 caracteres")
    @Column(name = "organizador_nombre")
    private String nombre;
    
    @NotBlank(message = "El celular es obligatorio")
    @Pattern(regexp = "^[0-9]{10}$", message = "El celular debe tener 10 dígitos")
    @Column(name = "organizador_celular")
    private String celular;
    
    @NotBlank(message = "La identificación es obligatoria")
    @Size(min = 5, max = 20, message = "La identificación debe tener entre 5 y 20 caracteres")
    @Column(name = "organizador_identificacion")
    private String identificacion;
    
    @NotBlank(message = "El email del organizador es obligatorio")
    @Email(message = "Debe ser un email válido")
    @Column(name = "organizador_email")
    private String email;
}