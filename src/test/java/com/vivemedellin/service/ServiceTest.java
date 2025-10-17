package com.vivemedellin.service;

import com.vivemedellin.dto.EventoResponse;
import com.vivemedellin.model.Evento;
import com.vivemedellin.model.Modalidad;
import com.vivemedellin.repository.EventoRepository;
import com.vivemedellin.repository.FuncionRepository;
import com.vivemedellin.repository.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class ServiceTest {

    @Mock private EventoRepository eventoRepository;
    @Mock private FuncionRepository funcionRepository;
    @Mock private UsuarioRepository usuarioRepository;

    @InjectMocks
    private EventoService eventoService;

    ServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("listarEventosActivos debe mapear a EventoResponse correctamente cuando hay datos")
    void listarEventosActivosMapeaCorrectamente() {
        Evento e = new Evento();
        e.setId(1L);
        e.setTitulo("Feria de las Flores");
        e.setDescripcion("Evento tradicional de Medellín.");
        e.setFecha(LocalDate.now().plusDays(5));
        e.setHorario(LocalTime.of(18, 30));
        e.setCategoria("Cultura");
        e.setModalidad(Modalidad.PRESENCIAL);
        e.setStatus(Evento.EstadoEvento.PUBLISHED);

        when(eventoRepository.findByStatus(Evento.EstadoEvento.PUBLISHED))
                .thenReturn(List.of(e));

        List<EventoResponse> resultado = eventoService.listarEventosActivos();

        assertEquals(1, resultado.size());
        EventoResponse r = resultado.get(0);
        assertEquals("Feria de las Flores", r.getTitulo());
        assertEquals("Cultura", r.getCategoria());
    }

    @Test
    @DisplayName("listarEventosActivos debe devolver lista vacía cuando no hay eventos publicados")
    void listarEventosActivosListaVacia() {
        when(eventoRepository.findByStatus(Evento.EstadoEvento.PUBLISHED))
                .thenReturn(List.of());

        List<EventoResponse> resultado = eventoService.listarEventosActivos();

        assertEquals(0, resultado.size());
    }
}
