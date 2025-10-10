package com.vivemedellin.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vivemedellin.model.Evento;

@Repository
public interface EventoRepository extends JpaRepository<Evento, Long>, JpaSpecificationExecutor<Evento> {
    
    // Buscar eventos por estado
    List<Evento> findByStatus(Evento.EstadoEvento status);
    
    // Buscar eventos destacados
    List<Evento> findByDestacadoTrue();
    
    // Buscar eventos por categoría
    List<Evento> findByCategoria(String categoria);
    
    // Buscar eventos por fecha
    List<Evento> findByFecha(LocalDate fecha);
    
    // Buscar eventos por rango de fechas
    List<Evento> findByFechaBetween(LocalDate fechaInicio, LocalDate fechaFin);
    
    // Buscar eventos por título (búsqueda parcial)
    List<Evento> findByTituloContainingIgnoreCase(String titulo);
    
    // Buscar eventos por ubicación
    @Query("SELECT e FROM Evento e WHERE e.ubicacion.comunaBarrio ILIKE %:ubicacion% OR e.ubicacion.direccionCompleta ILIKE %:ubicacion%")
    List<Evento> findByUbicacion(@Param("ubicacion") String ubicacion);
    
    // Buscar eventos gratuitos
    @Query("SELECT e FROM Evento e WHERE LOWER(e.valorIngreso) = 'gratuito'")
    List<Evento> findEventosGratuitos();
    
    // Buscar eventos de pago
    @Query("SELECT e FROM Evento e WHERE LOWER(e.valorIngreso) != 'gratuito'")
    List<Evento> findEventosDePago();
    
    // Búsqueda avanzada con filtros múltiples
    @Query("SELECT e FROM Evento e WHERE " +
           "(:categoria IS NULL OR e.categoria = :categoria) AND " +
           "(:fechaInicio IS NULL OR e.fecha >= :fechaInicio) AND " +
           "(:fechaFin IS NULL OR e.fecha <= :fechaFin) AND " +
           "(:ubicacion IS NULL OR e.ubicacion.comunaBarrio ILIKE %:ubicacion%) AND " +
           "(:destacado IS NULL OR e.destacado = :destacado) AND " +
           "e.status = :status")
    Page<Evento> findEventosConFiltros(
        @Param("categoria") String categoria,
        @Param("fechaInicio") LocalDate fechaInicio,
        @Param("fechaFin") LocalDate fechaFin,
        @Param("ubicacion") String ubicacion,
        @Param("destacado") Boolean destacado,
        @Param("status") Evento.EstadoEvento status,
        Pageable pageable
    );
    
    // Eventos próximos (fecha >= hoy)
    @Query("SELECT e FROM Evento e WHERE e.fecha >= CURRENT_DATE AND e.status = 'PUBLISHED' ORDER BY e.fecha ASC")
    List<Evento> findEventosProximos();
    
    // Eventos más populares (por número de guardados)
    @Query("SELECT e FROM Evento e LEFT JOIN e.usuariosQueGuardaron u " +
           "WHERE e.status = 'PUBLISHED' " +
           "GROUP BY e " +
           "ORDER BY COUNT(u) DESC")
    List<Evento> findEventosMasPopulares(Pageable pageable);
    
    // Buscar por ID incluyendo cancelados
    @Query("SELECT e FROM Evento e WHERE e.id = :id")
    Optional<Evento> findByIdIncludingCancelled(@Param("id") Long id);
    
    // Contar eventos por categoría
    @Query("SELECT e.categoria, COUNT(e) FROM Evento e WHERE e.status = 'PUBLISHED' GROUP BY e.categoria")
    List<Object[]> countEventosPorCategoria();
    
    // Eventos creados por un usuario específico
    List<Evento> findByCreatedBy(String createdBy);
    
    // Búsqueda de texto completo
    @Query("SELECT e FROM Evento e WHERE " +
           "e.titulo ILIKE %:texto% OR " +
           "e.descripcion ILIKE %:texto% OR " +
           "e.organizador.nombre ILIKE %:texto%")
    List<Evento> busquedaTextoCompleto(@Param("texto") String texto);
}