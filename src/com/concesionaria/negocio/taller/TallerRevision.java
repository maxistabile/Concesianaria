package com.concesionaria.negocio.taller;

import com.concesionaria.diseno.cola.ColaGenerica;
import com.concesionaria.diseno.excepciones.TallerException;
import com.concesionaria.diseno.interfaces.Mantenible;
import com.concesionaria.diseno.interfaces.ServicioTaller;
import com.concesionaria.diseno.modelo.Vehiculo;

public class TallerRevision implements ServicioTaller<Vehiculo> {
    private ColaGenerica<Vehiculo> colaEspera;
    private Lavadero lavadero;
    
    public TallerRevision() {
        this.colaEspera = new ColaGenerica<>();
        this.lavadero = new Lavadero();
    }
    
    @Override
    public void ingresarVehiculo(Vehiculo vehiculo) throws TallerException {
        if (!vehiculo.esUsado()) {
            throw new TallerException("Solo se aceptan vehículos usados en el taller");
        }
        
        colaEspera.encolar(vehiculo);
    }
    
    @Override
    public Vehiculo siguienteVehiculo() throws TallerException {
        if (!tieneVehiculosEnEspera()) {
            throw new TallerException("No hay vehículos en espera");
        }
        return colaEspera.desencolar();
    }
    
    @Override
    public int cantidadEnEspera() {
        return colaEspera.tamanio();
    }
    
    @Override
    public boolean tieneVehiculosEnEspera() {
        return !colaEspera.estaVacia();
    }
    
    public void procesarVehiculo() throws TallerException {
        if (!tieneVehiculosEnEspera()) {
            throw new TallerException("No hay vehículos para procesar");
        }
        
        Vehiculo vehiculo = colaEspera.desencolar();
        
        // Realizar mantenimiento
        if (vehiculo instanceof Mantenible) {
            System.out.println("\nRealizando mantenimiento en " + vehiculo.getMarca() + " " + vehiculo.getModelo() + "...");
            try {
                Thread.sleep(1500); // Simular tiempo de trabajo
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            ((Mantenible) vehiculo).realizarMantenimiento();
            System.out.println("Mantenimiento completado.");
        }
        
        // Enviar al lavadero
        lavadero.procesarVehiculo(vehiculo);
    }
    
    public Vehiculo verProximoVehiculo() {
        return colaEspera.verPrimero();
    }
}