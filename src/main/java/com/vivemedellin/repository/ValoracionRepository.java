package com.vivemedellin.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vivemedellin.model.Valoracion;

@Repository
public interface ValoracionRepository extends JpaRepository<Valoracion, Long> {
    
    // Buscar valoraciones por evento
    List<Valoracion> findByEventoIdAndEstadoOrderByFechaCreacionDesc(Long eventoId, Valoracion.EstadoValoracion estado);
    
    // Buscar valoración específica de un usuario para un evento
    Optional<Valoracion> findByUsuarioIdAndEventoIdAndEstado(Long usuarioId, Long eventoId, Valoracion.EstadoValoracion estado);
    
    // Verificar si un usuario ya valoró un evento
    boolean existsByUsuarioIdAndEventoIdAndEstado(Long usuarioId, Long eventoId, Valoracion.EstadoValoracion estado);
    
    // Calcular promedio de valoraciones por evento
    @Query("SELECT AVG(v.calificacion) FROM Valoracion v WHERE v.evento.id = :eventoId AND v.estado = 'ACTIVA'")
    Double calcularPromedioByEvento(@Param("eventoId") Long eventoId);
    
    // Contar valoraciones por evento
    @Query("SELECT COUNT(v) FROM Valoracion v WHERE v.evento.id = :eventoId AND v.estado = 'ACTIVA'")
    Long countValoracionesActivasByEvento(@Param("eventoId") Long eventoId);
    
    // Distribución de calificaciones por evento
    @Query("SELECT v.calificacion, COUNT(v) FROM Valoracion v WHERE v.evento.id = :eventoId AND v.estado = 'ACTIVA' GROUP BY v.calificacion ORDER BY v.calificacion DESC")
    List<Object[]> getDistribucionCalificacionesByEvento(@Param("eventoId") Long eventoId);
}