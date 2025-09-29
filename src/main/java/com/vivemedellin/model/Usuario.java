package com.vivemedellin.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "usuarios")
@Data
@EqualsAndHashCode(exclude = {"eventosGuardados", "seguidores", "siguiendo", "gruposCreados", "gruposMiembro", "comentarios", "valoraciones"})
@ToString(exclude = {"eventosGuardados", "seguidores", "siguiendo", "gruposCreados", "gruposMiembro", "comentarios", "valoraciones"})
public class Usuario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    @Column(nullable = false)
    private String nombre;
    
    @NotBlank(message = "El apellido es obligatorio")
    @Size(min = 2, max = 100, message = "El apellido debe tener entre 2 y 100 caracteres")
    @Column(nullable = false)
    private String apellido;
    
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Debe ser un email válido")
    @Column(unique = true, nullable = false)
    private String email;
    
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    @Column(nullable = false)
    private String password;
    
    @Size(max = 500, message = "La biografía no puede exceder 500 caracteres")
    private String biografia;
    
    @Min(value = 13, message = "La edad mínima es 13 años")
    @Max(value = 120, message = "La edad máxima es 120 años")
    private Integer edad;
    
    @Size(max = 200, message = "La ubicación no puede exceder 200 caracteres")
    private String ubicacion;
    
    @Size(max = 500, message = "La URL de la foto no puede exceder 500 caracteres")
    private String fotoPerfil;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoUsuario tipoUsuario = TipoUsuario.USUARIO;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoUsuario estado = EstadoUsuario.ACTIVO;
    
    @Column(nullable = false)
    private Boolean emailVerificado = false;
    
    @Column(nullable = false)
    private Boolean notificacionesActivas = true;
    
    // Intereses del usuario (categorías de eventos que le gustan)
    @ElementCollection
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "usuario_intereses", joinColumns = @JoinColumn(name = "usuario_id"))
    @Column(name = "categoria")
    private Set<CategoriaEvento> intereses = new HashSet<>();
    
    // Eventos guardados por el usuario
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "usuarios_eventos_guardados",
        joinColumns = @JoinColumn(name = "usuario_id"),
        inverseJoinColumns = @JoinColumn(name = "evento_id")
    )
    private Set<Evento> eventosGuardados = new HashSet<>();
    
    // Usuarios que siguen a este usuario
    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "siguiendo")
    private Set<Usuario> seguidores = new HashSet<>();
    
    // Usuarios que este usuario sigue
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "usuarios_siguiendo",
        joinColumns = @JoinColumn(name = "seguidor_id"),
        inverseJoinColumns = @JoinColumn(name = "seguido_id")
    )
    private Set<Usuario> siguiendo = new HashSet<>();
    
    // Grupos creados por el usuario
    @OneToMany(mappedBy = "creador", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Grupo> gruposCreados = new HashSet<>();
    
    // Grupos donde el usuario es miembro
    @ManyToMany(mappedBy = "miembros", fetch = FetchType.LAZY)
    private Set<Grupo> gruposMiembro = new HashSet<>();
    
    // Comentarios realizados por el usuario
    @OneToMany(mappedBy = "autor", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Comentario> comentarios = new HashSet<>();
    
    // Valoraciones realizadas por el usuario
    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Valoracion> valoraciones = new HashSet<>();
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;
    
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime fechaActualizacion;
    
    // Enums
    public enum TipoUsuario {
        USUARIO, ADMINISTRADOR, ORGANIZADOR
    }
    
    public enum EstadoUsuario {
        ACTIVO, INACTIVO, SUSPENDIDO
    }
    
    // Métodos de conveniencia
    public void agregarEventoGuardado(Evento evento) {
        eventosGuardados.add(evento);
        evento.getUsuariosQueGuardaron().add(this);
    }
    
    public void removerEventoGuardado(Evento evento) {
        eventosGuardados.remove(evento);
        evento.getUsuariosQueGuardaron().remove(this);
    }
    
    public void seguir(Usuario usuario) {
        siguiendo.add(usuario);
        usuario.getSeguidores().add(this);
    }
    
    public void dejarDeSeguir(Usuario usuario) {
        siguiendo.remove(usuario);
        usuario.getSeguidores().remove(this);
    }
}