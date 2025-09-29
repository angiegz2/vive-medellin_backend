package com.vivemedellin.model;

public enum CategoriaEvento {
    MUSICA("Música"),
    ARTE("Arte"),
    DEPORTE("Deporte"),
    ACADEMICO("Académico"),
    TECNOLOGIA("Tecnología"),
    GASTRONOMIA("Gastronomía"),
    CULTURA("Cultura"),
    ENTRETENIMIENTO("Entretenimiento"),
    NEGOCIOS("Negocios"),
    SALUD("Salud"),
    TURISMO("Turismo"),
    FAMILIAR("Familiar"),
    EDUCACION("Educación"),
    VOLUNTARIADO("Voluntariado"),
    NETWORKING("Networking"),
    OTRO("Otro");
    
    private final String descripcion;
    
    CategoriaEvento(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
}