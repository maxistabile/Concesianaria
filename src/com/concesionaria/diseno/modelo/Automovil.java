package com.concesionaria.diseno.modelo;

import com.concesionaria.diseno.enums.ColorVehiculo;
import com.concesionaria.diseno.enums.TipoCarroceriaAutomovil;
import com.concesionaria.diseno.interfaces.Lavable;
import com.concesionaria.diseno.interfaces.Mantenible;

public class Automovil extends Vehiculo implements Mantenible, Lavable {
    private static final long serialVersionUID = 1L;
    
    private TipoCarroceriaAutomovil carroceria;
    private boolean mantenimientoRealizado;
    private boolean lavado;
    
    public Automovil() {
        super();
    }
    
    public Automovil(String marca, String modelo, Integer anio,
                    ColorVehiculo color, Integer kilometraje,
                    TipoCarroceriaAutomovil carroceria) {
        super(marca, modelo, anio, color, kilometraje);
        this.carroceria = carroceria;
        this.mantenimientoRealizado = false;
        this.lavado = false;
    }
    
    @Override
    public String getTipoVehiculo() {
        return "Automóvil";
    }
    
    @Override
    public String obtenerDetallesEspecificos() {
        return "Carrocería: " + carroceria.getDescripcion();
    }
    
    @Override
    public void realizarMantenimiento() {
        this.mantenimientoRealizado = true;
    }
    
    @Override
    public boolean tieneMantenimientoRealizado() {
        return mantenimientoRealizado;
    }
    
    @Override
    public void lavar() {
        this.lavado = true;
    }
    
    @Override
    public void detallar() {
        if (!lavado) {
            lavar();
        }
    }
    
    @Override
    public boolean estaLavado() {
        return lavado;
    }
    
    // Getters y Setters
    public TipoCarroceriaAutomovil getCarroceria() { return carroceria; }
    public void setCarroceria(TipoCarroceriaAutomovil carroceria) { this.carroceria = carroceria; }
    
    public boolean isMantenimientoRealizado() { return mantenimientoRealizado; }
    public void setMantenimientoRealizado(boolean mantenimientoRealizado) { 
        this.mantenimientoRealizado = mantenimientoRealizado; 
    }
    
    public boolean isLavado() { return lavado; }
    public void setLavado(boolean lavado) { this.lavado = lavado; }
    
    @Override
    public String toString() {
        String base = super.toString() + " | " + obtenerDetallesEspecificos();
        String extra = "";
        if (esUsado()) {
            extra += " | Mant: " + (mantenimientoRealizado ? "Sí" : "No");
            extra += " | Lavado: " + (lavado ? "Sí" : "No");
        }
        return base + extra;
    }
}