package com.concesionaria.diseno.modelo;

import com.concesionaria.diseno.enums.ColorVehiculo;
import com.concesionaria.diseno.enums.EstadoVehiculo;
import java.io.Serializable;

public abstract class Vehiculo implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String id;
    private String marca;
    private String modelo;
    private Integer anio;
    private ColorVehiculo color;
    private EstadoVehiculo estado;
    private Integer kilometraje;
    
    public Vehiculo() {}
    
    public Vehiculo(String id, String marca, String modelo, Integer anio, 
                   ColorVehiculo color, EstadoVehiculo estado, Integer kilometraje) {
        this.id = id;
        this.marca = marca;
        this.modelo = modelo;
        this.anio = anio;
        this.color = color;
        this.estado = estado;
        this.kilometraje = kilometraje;
    }
    
    // Método abstracto que deben implementar las subclases
    public abstract String getTipoVehiculo();
    public abstract String obtenerDetallesEspecificos();
    
    // Getters y Setters (JavaBean)
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }
    
    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }
    
    public Integer getAnio() { return anio; }
    public void setAnio(Integer anio) { this.anio = anio; }
    
    public ColorVehiculo getColor() { return color; }
    public void setColor(ColorVehiculo color) { this.color = color; }
    
    public EstadoVehiculo getEstado() { return estado; }
    public void setEstado(EstadoVehiculo estado) { this.estado = estado; }
    
    public Integer getKilometraje() { return kilometraje; }
    public void setKilometraje(Integer kilometraje) { this.kilometraje = kilometraje; }
    
    public boolean esUsado() {
        return estado == EstadoVehiculo.USADO;
    }
    
    @Override
    public String toString() {
        return String.format("ID: %s | %s %s %d | Color: %s | Estado: %s | KM: %d",
                id, marca, modelo, anio, color.getDescripcion(), 
                estado.getDescripcion(), kilometraje);
    }
}