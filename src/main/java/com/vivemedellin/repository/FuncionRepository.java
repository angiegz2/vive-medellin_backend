package com.vivemedellin.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vivemedellin.model.Funcion;

@Repository
public interface FuncionRepository extends JpaRepository<Funcion, Long> {
    
    // Buscar funciones por evento
    List<Funcion> findByEventoIdOrderByNumeroFuncionAsc(Long eventoId);
    
    // Buscar funciones por estado
    List<Funcion> findByStatus(Funcion.EstadoFuncion status);
    
    // Buscar funciones por fecha
    List<Funcion> findByFecha(LocalDate fecha);
    
    // Buscar funciones activas de un evento
    @Query("SELECT f FROM Funcion f WHERE f.evento.id = :eventoId AND f.status = 'PUBLISHED'")
    List<Funcion> findFuncionesActivasByEvento(@Param("eventoId") Long eventoId);
    
    // Contar funciones activas por evento
    @Query("SELECT COUNT(f) FROM Funcion f WHERE f.evento.id = :eventoId AND f.status = 'PUBLISHED'")
    Long countFuncionesActivasByEvento(@Param("eventoId") Long eventoId);
}