package com.vivemedellin.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "notificaciones")
@Data
@EqualsAndHashCode(exclude = {"usuario"})
@ToString(exclude = {"usuario"})
public class Notificacion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull(message = "El tipo de notificación es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoNotificacion tipo;
    
    @NotBlank(message = "El título es obligatorio")
    @Size(min = 1, max = 200, message = "El título debe tener entre 1 y 200 caracteres")
    @Column(nullable = false)
    private String titulo;
    
    @NotBlank(message = "El mensaje es obligatorio")
    @Size(min = 1, max = 500, message = "El mensaje debe tener entre 1 y 500 caracteres")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String mensaje;
    
    // ID del recurso relacionado (evento, grupo, comentario, etc.)
    private Long recursoId;
    
    // Tipo de recurso (para saber qué endpoint usar)
    @Size(max = 50, message = "El tipo de recurso no puede exceder 50 caracteres")
    private String tipoRecurso;
    
    // Usuario que recibe la notificación
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    
    @Column(nullable = false)
    private Boolean leida = false;
    
    @Column(nullable = false)
    private Boolean enviada = false;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;
    
    private LocalDateTime fechaLeida;
    
    private LocalDateTime fechaEnviada;
    
    // Enum para tipos de notificación
    public enum TipoNotificacion {
        NUEVO_EVENTO("Nuevo evento"),
        EVENTO_GUARDADO_COMENTADO("Comentario en evento guardado"),
        RESPUESTA_COMENTARIO("Respuesta a comentario"),
        NUEVO_SEGUIDOR("Nuevo seguidor"),
        NUEVA_VALORACION("Nueva valoración"),
        GRUPO_NUEVA_PUBLICACION("Nueva publicación en grupo"),
        GRUPO_NUEVO_MIEMBRO("Nuevo miembro en grupo"),
        EVENTO_PROXIMAMENTE("Evento próximamente"),
        EVENTO_CANCELADO("Evento cancelado"),
        BIENVENIDA("Bienvenida"),
        SISTEMA("Sistema");
        
        private final String descripcion;
        
        TipoNotificacion(String descripcion) {
            this.descripcion = descripcion;
        }
        
        public String getDescripcion() {
            return descripcion;
        }
    }
    
    // Métodos de conveniencia
    public void marcarComoLeida() {
        this.leida = true;
        this.fechaLeida = LocalDateTime.now();
    }
    
    public void marcarComoEnviada() {
        this.enviada = true;
        this.fechaEnviada = LocalDateTime.now();
    }
}