package com.vivemedellin.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vivemedellin.model.Comentario;

@Repository
public interface ComentarioRepository extends JpaRepository<Comentario, Long> {
    
    // Buscar comentarios por evento
    List<Comentario> findByEventoIdAndEstadoOrderByFechaCreacionDesc(Long eventoId, Comentario.EstadoComentario estado);
    
    // Buscar comentarios por grupo
    List<Comentario> findByGrupoIdAndEstadoOrderByFechaCreacionDesc(Long grupoId, Comentario.EstadoComentario estado);
    
    // Buscar comentarios por autor
    Page<Comentario> findByAutorIdAndEstado(Long autorId, Comentario.EstadoComentario estado, Pageable pageable);
    
    // Buscar respuestas a un comentario
    List<Comentario> findByComentarioPadreIdAndEstadoOrderByFechaCreacionAsc(Long comentarioPadreId, Comentario.EstadoComentario estado);
    
    // Contar comentarios activos por evento
    @Query("SELECT COUNT(c) FROM Comentario c WHERE c.evento.id = :eventoId AND c.estado = 'ACTIVO'")
    Long countComentariosActivosByEvento(@Param("eventoId") Long eventoId);
}