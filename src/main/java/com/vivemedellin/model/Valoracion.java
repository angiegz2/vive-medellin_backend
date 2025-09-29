package com.vivemedellin.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "valoraciones",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"usuario_id", "evento_id"})
    }
)
@Data
@EqualsAndHashCode(exclude = {"usuario", "evento"})
@ToString(exclude = {"usuario", "evento"})
public class Valoracion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull(message = "La calificación es obligatoria")
    @Min(value = 1, message = "La calificación mínima es 1")
    @Max(value = 5, message = "La calificación máxima es 5")
    @Column(nullable = false)
    private Integer calificacion;
    
    @Size(max = 1000, message = "El comentario no puede exceder 1000 caracteres")
    @Column(columnDefinition = "TEXT")
    private String comentario;
    
    // Usuario que realiza la valoración
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    
    // Evento que se está valorando
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evento_id", nullable = false)
    private Evento evento;
    
    @NotNull(message = "El estado es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoValoracion estado = EstadoValoracion.ACTIVA;
    
    @Column(nullable = false)
    private Boolean editada = false;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;
    
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime fechaActualizacion;
    
    // Enum para el estado de la valoración
    public enum EstadoValoracion {
        ACTIVA, ELIMINADA, REPORTADA
    }
}