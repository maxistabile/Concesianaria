package com.concesionaria.diseno.enums;

public enum TipoCarroceriaCamioneta {
    SUV("SUV"),
    PICKUP("Pickup");
    
    private final String descripcion;
    
    TipoCarroceriaCamioneta(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
}
