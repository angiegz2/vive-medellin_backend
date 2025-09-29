package com.vivemedellin.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.vivemedellin.model.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    // Buscar por email
    Optional<Usuario> findByEmail(String email);
    
    // Verificar si existe email
    boolean existsByEmail(String email);
    
    // Buscar por tipo de usuario
    List<Usuario> findByTipoUsuario(Usuario.TipoUsuario tipoUsuario);
    
    // Buscar por estado
    List<Usuario> findByEstado(Usuario.EstadoUsuario estado);
    
    // Buscar administradores activos
    @Query("SELECT u FROM Usuario u WHERE u.tipoUsuario = 'ADMINISTRADOR' AND u.estado = 'ACTIVO'")
    List<Usuario> findAdministradoresActivos();
    
    // Buscar por nombre (parcial)
    List<Usuario> findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCase(String nombre, String apellido);
    
    // Contar usuarios activos
    @Query("SELECT COUNT(u) FROM Usuario u WHERE u.estado = 'ACTIVO'")
    Long countUsuariosActivos();
}