package com.concesionaria.diseno.enums;

public enum TipoCarroceriaAutomovil {
    SEDAN("Sedán"),
    HATCHBACK("Hatchback"),
    COUPE("Coupé"),
    FAMILIAR("Familiar");
    
    private final String descripcion;
    
    TipoCarroceriaAutomovil(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
}
