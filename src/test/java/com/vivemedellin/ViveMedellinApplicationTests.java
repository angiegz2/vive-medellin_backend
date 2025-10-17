package com.vivemedellin;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test") 
class ViveMedellinApplicationTests {

    @Test
    @DisplayName("✅ El contexto de la aplicación carga correctamente")
    void contextLoads() {
        // Arrange - (No se necesita nada, solo verificar contexto)
        
        // Act - SpringBootTest carga el contexto automáticamente

        // Assert - Si llega aquí, el contexto se cargó correctamente
        assert true;
    }
}
