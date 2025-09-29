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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "grupos")
@Data
@EqualsAndHashCode(exclude = {"creador", "miembros", "comentarios"})
@ToString(exclude = {"creador", "miembros", "comentarios"})
public class Grupo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "El nombre del grupo es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    @Column(nullable = false)
    private String nombre;
    
    @NotBlank(message = "La descripción es obligatoria")
    @Size(min = 10, max = 1000, message = "La descripción debe tener entre 10 y 1000 caracteres")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String descripcion;
    
    @NotNull(message = "El tema es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoriaEvento tema;
    
    @Size(max = 2000, message = "Las reglas no pueden exceder 2000 caracteres")
    @Column(columnDefinition = "TEXT")
    private String reglas;
    
    @NotNull(message = "El tipo de grupo es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoGrupo tipoGrupo = TipoGrupo.PUBLICO;
    
    @Size(max = 500, message = "La URL de la imagen no puede exceder 500 caracteres")
    private String imagenGrupo;
    
    @Min(value = 2, message = "El mínimo de miembros es 2")
    @Max(value = 10000, message = "El máximo de miembros es 10000")
    private Integer limiteMinimos;
    
    @NotNull(message = "El estado es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoGrupo estado = EstadoGrupo.ACTIVO;
    
    // Creador del grupo
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creador_id", nullable = false)
    private Usuario creador;
    
    // Miembros del grupo
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "grupos_miembros",
        joinColumns = @JoinColumn(name = "grupo_id"),
        inverseJoinColumns = @JoinColumn(name = "usuario_id")
    )
    private Set<Usuario> miembros = new HashSet<>();
    
    // Comentarios/publicaciones del grupo
    @OneToMany(mappedBy = "grupo", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @OrderBy("fechaCreacion DESC")
    private List<Comentario> comentarios = new ArrayList<>();
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;
    
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime fechaActualizacion;
    
    // Enums
    public enum TipoGrupo {
        PUBLICO, PRIVADO
    }
    
    public enum EstadoGrupo {
        ACTIVO, INACTIVO, SUSPENDIDO
    }
    
    // Métodos calculados
    @Transient
    public Integer getTotalMiembros() {
        return miembros.size();
    }
    
    @Transient
    public Integer getTotalPublicaciones() {
        return comentarios.size();
    }
    
    @Transient
    public Boolean isPuedeUnirse() {
        return limiteMinimos == null || miembros.size() < limiteMinimos;
    }
    
    // Métodos de conveniencia
    public void agregarMiembro(Usuario usuario) {
        miembros.add(usuario);
        usuario.getGruposMiembro().add(this);
    }
    
    public void removerMiembro(Usuario usuario) {
        miembros.remove(usuario);
        usuario.getGruposMiembro().remove(this);
    }
    
    public void agregarComentario(Comentario comentario) {
        comentarios.add(comentario);
        comentario.setGrupo(this);
    }
}