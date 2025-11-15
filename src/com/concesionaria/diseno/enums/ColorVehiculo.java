package com.concesionaria.diseno.enums;

public enum ColorVehiculo {
    BLANCO("Blanco"),
    NEGRO("Negro"),
    GRIS("Gris"),
    ROJO("Rojo"),
    AZUL("Azul"),
    PLATA("Plata"),
    VERDE("Verde"),
    AMARILLO("Amarillo");
    
    private final String descripcion;
    
    ColorVehiculo(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
}