package com.concesionaria.diseno.enums;

public enum TipoMotocicleta {
    DEPORTIVA("Deportiva"),
    CRUCERO("Crucero"),
    TOURING("Touring"),
    ENDURO("Enduro"),
    SCOOTER("Scooter"),
    NAKED("Naked");
    
    private final String descripcion;
    
    TipoMotocicleta(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
}