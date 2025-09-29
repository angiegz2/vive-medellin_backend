package com.vivemedellin.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("ViveMedellin API")
                        .version("1.0.0")
                        .description("API para la aplicación ViveMedellin - Descubre y vive lo mejor de Medellín")
                        .contact(new Contact()
                                .name("Equipo ViveMedellin")
                                .email("contacto@vivemedellin.com")
                                .url("https://vivemedellin.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")));
    }
}