package com.concesionaria.diseno.enums;

public enum TipoCarroceria {
    SEDAN("Sedán"),
    HATCHBACK("Hatchback"),
    SUV("SUV"),
    PICKUP("Pickup"),
    COUPE("Coupé"),
    FAMILIAR("Familiar");
    
    private final String descripcion;
    
    TipoCarroceria(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
}
