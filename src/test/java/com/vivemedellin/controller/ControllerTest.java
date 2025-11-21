package com.vivemedellin.controller;

import com.vivemedellin.dto.EventoResponse;
import com.vivemedellin.service.EventoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EventoController.class)
class ControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @SuppressWarnings("removal")
    @MockBean
    private EventoService eventoService;

    @Test
    @DisplayName("✅ listarEventosActivos debe devolver 200 OK y lista de eventos en formato JSON")
    void debeRetornarOkCuandoSeListanEventosActivos() throws Exception {
        EventoResponse evento = new EventoResponse();
        evento.setId(1L);
        evento.setTitulo("Festival de la cerveza");
        evento.setDescripcion("Un evento de música y cerveza artesanal");
        evento.setFecha(LocalDate.now().plusDays(5));
        evento.setCategoria("Cultural");

        when(eventoService.listarEventosActivos()).thenReturn(List.of(evento));

        mockMvc.perform(get("/api/v1/eventos")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].titulo").value("Festival de la cerveza"))
                .andExpect(jsonPath("$[0].categoria").value("Cultural"));
    }

    @Test
    @DisplayName("✅ listarEventosActivos debe devolver lista vacía y 200 OK cuando no hay eventos")
    void debeRetornarListaVaciaSiNoHayEventos() throws Exception {
        when(eventoService.listarEventosActivos()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/eventos")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }
}

