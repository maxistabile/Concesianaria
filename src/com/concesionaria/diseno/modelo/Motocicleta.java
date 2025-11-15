package com.concesionaria.diseno.modelo;

import com.concesionaria.diseno.enums.ColorVehiculo;
import com.concesionaria.diseno.enums.EstadoVehiculo;
import com.concesionaria.diseno.enums.TipoMotocicleta;
import com.concesionaria.diseno.interfaces.Lavable;
import com.concesionaria.diseno.interfaces.Mantenible;

public class Motocicleta extends Vehiculo implements Mantenible, Lavable {
    private static final long serialVersionUID = 1L;
    
    private TipoMotocicleta tipoMoto;
    private boolean mantenimientoRealizado;
    private boolean lavado;
    
    public Motocicleta() {
        super();
    }
    
    public Motocicleta(String id, String marca, String modelo, Integer anio,
                      ColorVehiculo color, EstadoVehiculo estado, Integer kilometraje,
                      TipoMotocicleta tipoMoto) {
        super(id, marca, modelo, anio, color, estado, kilometraje);
        this.tipoMoto = tipoMoto;
        this.mantenimientoRealizado = false;
        this.lavado = false;
    }
    
    @Override
    public String getTipoVehiculo() {
        return "Motocicleta";
    }
    
    @Override
    public String obtenerDetallesEspecificos() {
        return "Tipo: " + tipoMoto.getDescripcion();
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
    public TipoMotocicleta getTipoMoto() { return tipoMoto; }
    public void setTipoMoto(TipoMotocicleta tipoMoto) { this.tipoMoto = tipoMoto; }
    
    public boolean isMantenimientoRealizado() { return mantenimientoRealizado; }
    public void setMantenimientoRealizado(boolean mantenimientoRealizado) { 
        this.mantenimientoRealizado = mantenimientoRealizado; 
    }
    
    public boolean isLavado() { return lavado; }
    public void setLavado(boolean lavado) { this.lavado = lavado; }
    
    @Override
    public String toString() {
        return super.toString() + " | " + obtenerDetallesEspecificos() +
               (esUsado() ? " | Mant: " + (mantenimientoRealizado ? "Sí" : "No") : "");
    }
}