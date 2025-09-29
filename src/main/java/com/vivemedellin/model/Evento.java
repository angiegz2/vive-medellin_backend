package com.vivemedellin.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Table(name = "eventos")
@Data
@EqualsAndHashCode(exclude = {"usuariosQueGuardaron", "comentarios", "valoraciones", "createdByUser", "lastEditedByUser", "cancelledByUser", "funciones"})
@ToString(exclude = {"usuariosQueGuardaron", "comentarios", "valoraciones", "createdByUser", "lastEditedByUser", "cancelledByUser", "funciones"})
public class Evento {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "El título es obligatorio")
    @Size(min = 5, max = 200, message = "El título debe tener entre 5 y 200 caracteres")
    @Column(nullable = false)
    private String titulo;
    
    @NotBlank(message = "La descripción es obligatoria")
    @Size(min = 10, max = 2000, message = "La descripción debe tener entre 10 y 2000 caracteres")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String descripcion;
    
    @NotNull(message = "La fecha es obligatoria")
    @Column(nullable = false)
    private LocalDate fecha;
    
    @NotNull(message = "El horario es obligatorio")
    @Column(nullable = false)
    private LocalTime horario;
    
    @NotNull(message = "La categoría es obligatoria")
    @Size(max = 100, message = "La categoría no puede exceder 100 caracteres")
    @Column(nullable = false)
    private String categoria;
    
    @NotNull(message = "La modalidad es obligatoria")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Modalidad modalidad = Modalidad.PRESENCIAL;
    
    @Min(value = 1, message = "El aforo mínimo es 1 persona")
    private Integer aforo;
    
    @Column(nullable = false)
    private Boolean destacado = false;
    
    // Valor de ingreso: puede ser numérico o "gratuito"
    @Size(max = 50, message = "El valor de ingreso no puede exceder 50 caracteres")
    private String valorIngreso = "gratuito";
    
    // Ubicación embebida
    @Valid
    @Embedded
    private Ubicacion ubicacion;
    
    // Organizador embebido
    @Valid
    @Embedded
    private Organizador organizador;
    
    @Size(max = 500, message = "La URL de la imagen no puede exceder 500 caracteres")
    private String imagenCaratula;
    
    @ElementCollection
    @CollectionTable(name = "evento_servicios_adicionales", joinColumns = @JoinColumn(name = "evento_id"))
    @Column(name = "servicio")
    private List<String> serviciosAdicionales = new ArrayList<>();
    
    @NotNull(message = "El estado es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoEvento status = EstadoEvento.PUBLISHED;
    
    // Funciones del evento
    @OneToMany(mappedBy = "evento", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @OrderBy("numeroFuncion ASC")
    private List<Funcion> funciones = new ArrayList<>();
    
    // Usuarios que guardaron este evento
    @ManyToMany(mappedBy = "eventosGuardados", fetch = FetchType.LAZY)
    private Set<Usuario> usuariosQueGuardaron = new HashSet<>();
    
    // Comentarios del evento
    @OneToMany(mappedBy = "evento", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @OrderBy("fechaCreacion DESC")
    private List<Comentario> comentarios = new ArrayList<>();
    
    // Valoraciones del evento
    @OneToMany(mappedBy = "evento", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Valoracion> valoraciones = new HashSet<>();
    
    // Auditoría
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id")
    private Usuario createdByUser;
    
    @Column(name = "created_by")
    private String createdBy;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "last_edited_by_user_id")
    private Usuario lastEditedByUser;
    
    @Column(name = "last_edited_by")
    private String lastEditedBy;
    
    @Column(name = "last_edited_at")
    private LocalDateTime lastEditedAt;
    
    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cancelled_by_user_id")
    private Usuario cancelledByUser;
    
    @Column(name = "cancelled_by")
    private String cancelledBy;
    
    // Enums
    public enum EstadoEvento {
        PUBLISHED, CANCELLED, SUSPENDED, DRAFT
    }
    
    // Métodos calculados
    @Transient
    public Double getCalificacionPromedio() {
        if (valoraciones.isEmpty()) {
            return null;
        }
        return valoraciones.stream()
                .mapToInt(Valoracion::getCalificacion)
                .average()
                .orElse(0.0);
    }
    
    @Transient
    public Integer getTotalValoraciones() {
        return valoraciones.size();
    }
    
    @Transient
    public Integer getTotalComentarios() {
        return comentarios.size();
    }
    
    @Transient
    public Boolean isDisponible() {
        return aforo == null || aforo > 0;
    }
    
    @Transient
    public Boolean isGratuito() {
        return "gratuito".equalsIgnoreCase(valorIngreso);
    }
    
    @Transient
    public BigDecimal getPrecioNumerico() {
        if (isGratuito()) {
            return BigDecimal.ZERO;
        }
        try {
            return new BigDecimal(valorIngreso);
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }
    
    // Métodos de conveniencia
    public void agregarComentario(Comentario comentario) {
        comentarios.add(comentario);
        comentario.setEvento(this);
    }
    
    public void agregarValoracion(Valoracion valoracion) {
        valoraciones.add(valoracion);
        valoracion.setEvento(this);
    }
    
    public void agregarFuncion(Funcion funcion) {
        funciones.add(funcion);
        funcion.setEvento(this);
    }
    
    // Métodos de gestión de estado
    public void destacar() {
        this.destacado = true;
        this.lastEditedAt = LocalDateTime.now();
    }
    
    public void quitarDestaque() {
        this.destacado = false;
        this.lastEditedAt = LocalDateTime.now();
    }
    
    public void cancelar(String canceladoPor) {
        this.status = EstadoEvento.CANCELLED;
        this.cancelledAt = LocalDateTime.now();
        this.cancelledBy = canceladoPor;
        this.lastEditedAt = LocalDateTime.now();
    }
    
    public void reactivar() {
        this.status = EstadoEvento.PUBLISHED;
        this.cancelledAt = null;
        this.cancelledBy = null;
        this.lastEditedAt = LocalDateTime.now();
    }
}