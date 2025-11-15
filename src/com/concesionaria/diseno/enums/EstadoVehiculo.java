package com.concesionaria.diseno.enums;

public enum EstadoVehiculo {
    NUEVO("Nuevo"),
    USADO("Usado");
    
    private final String descripcion;
    
    EstadoVehiculo(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
}