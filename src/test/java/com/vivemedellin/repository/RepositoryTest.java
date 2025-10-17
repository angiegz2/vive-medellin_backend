package com.vivemedellin.repository;

import com.vivemedellin.model.Evento;
import com.vivemedellin.model.Modalidad;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class RepositoryTest {

    @Autowired
    private EventoRepository eventoRepository;

    @Test
    @DisplayName("Debe guardar y recuperar un Evento con los campos mínimos requeridos")
    void debeGuardarYRecuperarEvento() {
        Evento evento = new Evento();
        evento.setTitulo("Concierto Medellín 2025");
        evento.setDescripcion("Descripción válida de al menos 10 caracteres.");
        evento.setFecha(LocalDate.now());
        evento.setHorario(LocalTime.of(20, 0)); 
        evento.setCategoria("Música");
        evento.setModalidad(Modalidad.PRESENCIAL); 
        
        Evento guardado = eventoRepository.save(evento);
        Evento encontrado = eventoRepository.findById(guardado.getId()).orElse(null);

        assertThat(encontrado).isNotNull();
        assertThat(encontrado.getId()).isNotNull();
        assertThat(encontrado.getTitulo()).isEqualTo("Concierto Medellín 2025");
        assertThat(encontrado.getHorario()).isEqualTo(LocalTime.of(20, 0));
        assertThat(encontrado.getModalidad()).isEqualTo(Modalidad.PRESENCIAL);
    }
}
