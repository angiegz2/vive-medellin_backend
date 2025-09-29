package com.vivemedellin.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "funciones")
@Data
@EqualsAndHashCode(exclude = {"evento"})
@ToString(exclude = {"evento"})
public class Funcion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull(message = "El número de función es obligatorio")
    @Min(value = 1, message = "El número de función debe ser mayor a 0")
    @Column(nullable = false)
    private Integer numeroFuncion;
    
    @NotNull(message = "La fecha es obligatoria")
    @Column(nullable = false)
    private LocalDate fecha;
    
    @NotNull(message = "El horario es obligatorio")
    @Column(nullable = false)
    private LocalTime horario;
    
    @NotNull(message = "El estado es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoFuncion status = EstadoFuncion.PUBLISHED;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evento_id", nullable = false)
    private Evento evento;
    
    private LocalDateTime cancelledAt;
    private String cancelledBy;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    public enum EstadoFuncion {
        PUBLISHED, CANCELLED, SUSPENDED
    }
    
    // Método para cancelar función
    public void cancelar(String canceladoPor) {
        this.status = EstadoFuncion.CANCELLED;
        this.cancelledAt = LocalDateTime.now();
        this.cancelledBy = canceladoPor;
    }
}