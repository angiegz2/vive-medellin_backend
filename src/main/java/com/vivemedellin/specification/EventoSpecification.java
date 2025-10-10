package com.vivemedellin.specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.vivemedellin.model.Evento;
import com.vivemedellin.model.Evento.EstadoEvento;

import jakarta.persistence.criteria.Predicate;

/**
 * Specifications de JPA para filtrado dinámico de Eventos
 * Permite construir consultas complejas de forma programática y type-safe
 */
public class EventoSpecification {

    /**
     * Busca eventos por texto en título o descripción (case insensitive)
     * 
     * @param texto Texto a buscar
     * @return Specification para filtrado
     */
    public static Specification<Evento> conTexto(String texto) {
        return (root, query, criteriaBuilder) -> {
            if (texto == null || texto.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            
            String pattern = "%" + texto.toLowerCase() + "%";
            return criteriaBuilder.or(
                criteriaBuilder.like(criteriaBuilder.lower(root.get("titulo")), pattern),
                criteriaBuilder.like(criteriaBuilder.lower(root.get("descripcion")), pattern)
            );
        };
    }

    /**
     * Filtra eventos por ubicación (comuna/barrio o dirección)
     * 
     * @param ubicacion Texto de ubicación a buscar
     * @return Specification para filtrado
     */
    public static Specification<Evento> conUbicacion(String ubicacion) {
        return (root, query, criteriaBuilder) -> {
            if (ubicacion == null || ubicacion.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            
            String pattern = "%" + ubicacion.toLowerCase() + "%";
            return criteriaBuilder.or(
                criteriaBuilder.like(criteriaBuilder.lower(root.get("ubicacion").get("comunaBarrio")), pattern),
                criteriaBuilder.like(criteriaBuilder.lower(root.get("ubicacion").get("direccionCompleta")), pattern),
                criteriaBuilder.like(criteriaBuilder.lower(root.get("ubicacion").get("direccionDetallada")), pattern)
            );
        };
    }

    /**
     * Filtra eventos por categoría exacta
     * 
     * @param categoria Categoría del evento
     * @return Specification para filtrado
     */
    public static Specification<Evento> conCategoria(String categoria) {
        return (root, query, criteriaBuilder) -> {
            if (categoria == null || categoria.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("categoria"), categoria);
        };
    }

    /**
     * Filtra eventos a partir de una fecha específica (inclusive)
     * 
     * @param fechaDesde Fecha mínima
     * @return Specification para filtrado
     */
    public static Specification<Evento> desdeFecha(LocalDate fechaDesde) {
        return (root, query, criteriaBuilder) -> {
            if (fechaDesde == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.greaterThanOrEqualTo(root.get("fecha"), fechaDesde);
        };
    }

    /**
     * Filtra eventos hasta una fecha específica (inclusive)
     * 
     * @param fechaHasta Fecha máxima
     * @return Specification para filtrado
     */
    public static Specification<Evento> hastaFecha(LocalDate fechaHasta) {
        return (root, query, criteriaBuilder) -> {
            if (fechaHasta == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.lessThanOrEqualTo(root.get("fecha"), fechaHasta);
        };
    }

    /**
     * Filtra eventos en un rango de fechas
     * 
     * @param fechaDesde Fecha inicial (inclusive)
     * @param fechaHasta Fecha final (inclusive)
     * @return Specification para filtrado
     */
    public static Specification<Evento> entreRangoFechas(LocalDate fechaDesde, LocalDate fechaHasta) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (fechaDesde != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("fecha"), fechaDesde));
            }
            
            if (fechaHasta != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("fecha"), fechaHasta));
            }
            
            if (predicates.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            
            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        };
    }

    /**
     * Filtra solo eventos destacados
     * 
     * @param destacado True para solo destacados, False para no destacados, null para todos
     * @return Specification para filtrado
     */
    public static Specification<Evento> esDestacado(Boolean destacado) {
        return (root, query, criteriaBuilder) -> {
            if (destacado == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("destacado"), destacado);
        };
    }

    /**
     * Filtra eventos por estado
     * 
     * @param estado Estado del evento
     * @return Specification para filtrado
     */
    public static Specification<Evento> conEstado(EstadoEvento estado) {
        return (root, query, criteriaBuilder) -> {
            if (estado == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("status"), estado);
        };
    }

    /**
     * Filtra solo eventos activos (publicados)
     * 
     * @return Specification para filtrado
     */
    public static Specification<Evento> soloActivos() {
        return (root, query, criteriaBuilder) -> 
            criteriaBuilder.equal(root.get("status"), EstadoEvento.PUBLISHED);
    }

    /**
     * Filtra eventos gratuitos
     * 
     * @param gratuito True para eventos gratuitos, False para eventos de pago, null para todos
     * @return Specification para filtrado
     */
    public static Specification<Evento> esGratuito(Boolean gratuito) {
        return (root, query, criteriaBuilder) -> {
            if (gratuito == null) {
                return criteriaBuilder.conjunction();
            }
            
            if (gratuito) {
                return criteriaBuilder.equal(
                    criteriaBuilder.lower(root.get("valorIngreso")), 
                    "gratuito"
                );
            } else {
                return criteriaBuilder.notEqual(
                    criteriaBuilder.lower(root.get("valorIngreso")), 
                    "gratuito"
                );
            }
        };
    }

    /**
     * Filtra eventos por modalidad (PRESENCIAL, VIRTUAL, HIBRIDA)
     * 
     * @param modalidad Modalidad del evento
     * @return Specification para filtrado
     */
    public static Specification<Evento> conModalidad(String modalidad) {
        return (root, query, criteriaBuilder) -> {
            if (modalidad == null || modalidad.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(
                criteriaBuilder.upper(root.get("modalidad").as(String.class)), 
                modalidad.toUpperCase()
            );
        };
    }

    /**
     * Filtra eventos próximos (fecha >= hoy)
     * 
     * @return Specification para filtrado
     */
    public static Specification<Evento> proximosEventos() {
        return (root, query, criteriaBuilder) -> 
            criteriaBuilder.greaterThanOrEqualTo(root.get("fecha"), LocalDate.now());
    }

    /**
     * Filtra eventos por nombre del organizador
     * 
     * @param nombreOrganizador Nombre o parte del nombre del organizador
     * @return Specification para filtrado
     */
    public static Specification<Evento> conOrganizador(String nombreOrganizador) {
        return (root, query, criteriaBuilder) -> {
            if (nombreOrganizador == null || nombreOrganizador.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            
            String pattern = "%" + nombreOrganizador.toLowerCase() + "%";
            return criteriaBuilder.like(
                criteriaBuilder.lower(root.get("organizador").get("nombre")), 
                pattern
            );
        };
    }

    /**
     * Ordena resultados por fecha ascendente
     * 
     * @return Specification con ordenamiento
     */
    public static Specification<Evento> ordenadoPorFecha() {
        return (root, query, criteriaBuilder) -> {
            query.orderBy(criteriaBuilder.asc(root.get("fecha")));
            return criteriaBuilder.conjunction();
        };
    }

    /**
     * Specification combinada para búsqueda avanzada con múltiples filtros
     * 
     * @param texto Texto para buscar en título/descripción
     * @param ubicacion Ubicación del evento
     * @param categoria Categoría del evento
     * @param fechaDesde Fecha inicial
     * @param fechaHasta Fecha final
     * @param destacado Si es destacado
     * @param gratuito Si es gratuito
     * @param modalidad Modalidad del evento
     * @param soloActivos Si solo se incluyen eventos activos
     * @return Specification combinada
     */
    public static Specification<Evento> busquedaAvanzada(
            String texto,
            String ubicacion,
            String categoria,
            LocalDate fechaDesde,
            LocalDate fechaHasta,
            Boolean destacado,
            Boolean gratuito,
            String modalidad,
            Boolean soloActivos) {
        
        Specification<Evento> spec = conTexto(texto);
        spec = spec.and(conUbicacion(ubicacion));
        spec = spec.and(conCategoria(categoria));
        spec = spec.and(desdeFecha(fechaDesde));
        spec = spec.and(hastaFecha(fechaHasta));
        spec = spec.and(esDestacado(destacado));
        spec = spec.and(esGratuito(gratuito));
        spec = spec.and(conModalidad(modalidad));
        
        if (soloActivos != null && soloActivos) {
            spec = spec.and(soloActivos());
        }
        
        return spec;
    }

    /**
     * Búsqueda simple por palabras clave en múltiples campos
     * 
     * @param keywords Palabras clave separadas por espacios
     * @return Specification para búsqueda
     */
    public static Specification<Evento> busquedaPorPalabrasClaves(String keywords) {
        return (root, query, criteriaBuilder) -> {
            if (keywords == null || keywords.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            String[] palabras = keywords.trim().toLowerCase().split("\\s+");
            List<Predicate> predicates = new ArrayList<>();

            for (String palabra : palabras) {
                String pattern = "%" + palabra + "%";
                Predicate predicadoPalabra = criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("titulo")), pattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("descripcion")), pattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("categoria")), pattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("organizador").get("nombre")), pattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("ubicacion").get("comunaBarrio")), pattern)
                );
                predicates.add(predicadoPalabra);
            }

            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        };
    }
}
