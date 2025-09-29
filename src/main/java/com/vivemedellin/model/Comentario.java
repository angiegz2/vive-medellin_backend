package com.vivemedellin.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "comentarios")
@Data
@EqualsAndHashCode(exclude = {"evento", "autor", "comentarioPadre", "respuestas", "grupo"})
@ToString(exclude = {"evento", "autor", "comentarioPadre", "respuestas", "grupo"})
public class Comentario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "El contenido del comentario es obligatorio")
    @Size(min = 1, max = 1000, message = "El comentario debe tener entre 1 y 1000 caracteres")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String contenido;
    
    // Evento al que pertenece el comentario (puede ser null si es comentario de grupo)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evento_id")
    private Evento evento;
    
    // Grupo al que pertenece el comentario (puede ser null si es comentario de evento)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grupo_id")
    private Grupo grupo;
    
    // Autor del comentario
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "autor_id", nullable = false)
    private Usuario autor;
    
    // Para respuestas a comentarios (estructura jerárquica)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comentario_padre_id")
    private Comentario comentarioPadre;
    
    // Respuestas a este comentario
    @OneToMany(mappedBy = "comentarioPadre", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @OrderBy("fechaCreacion ASC")
    private List<Comentario> respuestas = new ArrayList<>();
    
    @NotNull(message = "El estado es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoComentario estado = EstadoComentario.ACTIVO;
    
    @Column(nullable = false)
    private Boolean editado = false;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;
    
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime fechaActualizacion;
    
    // Enum para el estado del comentario
    public enum EstadoComentario {
        ACTIVO, ELIMINADO, MODERADO, REPORTADO
    }
    
    // Métodos de conveniencia
    public void agregarRespuesta(Comentario respuesta) {
        respuestas.add(respuesta);
        respuesta.setComentarioPadre(this);
    }
    
    @Transient
    public Boolean isRespuesta() {
        return comentarioPadre != null;
    }
    
    @Transient
    public Integer getTotalRespuestas() {
        return respuestas.size();
    }
}