package com.vivemedellin.repository;

import com.vivemedellin.model.Evento;
import com.vivemedellin.model.Modalidad;
import com.vivemedellin.model.Ubicacion;
import com.vivemedellin.model.Organizador;
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
        // 🔹 Crear ubicación embebida
        Ubicacion ubicacion = new Ubicacion();
        ubicacion.setComunaBarrio("Laureles");
        ubicacion.setDireccionCompleta("Cra 70 #45-10 Medellín");
        ubicacion.setDireccionDetallada("Cerca al estadio Atanasio Girardot");
        ubicacion.setEnlaceMapa("https://maps.google.com/?q=Laureles+Medellin");

        // 🔹 Crear organizador embebido
        Organizador organizador = new Organizador();
        organizador.setNombre("ViveMedellín");
        organizador.setEmail("contacto@vivemedellin.com");
        organizador.setCelular("3001234567");
        organizador.setIdentificacion("900123456");

        // 🔹 Crear evento principal
        Evento evento = new Evento();
        evento.setTitulo("Concierto Medellín 2025");
        evento.setDescripcion("Descripción válida de al menos 10 caracteres.");
        evento.setFecha(LocalDate.now());
        evento.setHorario(LocalTime.of(20, 0));
        evento.setCategoria("Música");
        evento.setModalidad(Modalidad.PRESENCIAL);
        evento.setUbicacion(ubicacion);
        evento.setOrganizador(organizador);

        // 🔹 Guardar y recuperar el evento
        Evento guardado = eventoRepository.save(evento);
        Evento encontrado = eventoRepository.findById(guardado.getId()).orElse(null);

        // 🔹 Validaciones
        assertThat(encontrado).isNotNull();
        assertThat(encontrado.getId()).isNotNull();
        assertThat(encontrado.getTitulo()).isEqualTo("Concierto Medellín 2025");
        assertThat(encontrado.getHorario()).isEqualTo(LocalTime.of(20, 0));
        assertThat(encontrado.getModalidad()).isEqualTo(Modalidad.PRESENCIAL);
        assertThat(encontrado.getUbicacion()).isNotNull();
        assertThat(encontrado.getUbicacion().getComunaBarrio()).isEqualTo("Laureles");
        assertThat(encontrado.getOrganizador()).isNotNull();
        assertThat(encontrado.getOrganizador().getNombre()).isEqualTo("ViveMedellín");
    }
}
