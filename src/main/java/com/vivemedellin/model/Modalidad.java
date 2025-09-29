package com.vivemedellin.model;

public enum Modalidad {
    PRESENCIAL("Presencial"),
    VIRTUAL("Virtual"),
    HIBRIDA("Híbrida");
    
    private final String descripcion;
    
    Modalidad(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
}