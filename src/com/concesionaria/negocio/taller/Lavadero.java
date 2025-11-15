package com.concesionaria.negocio.taller;

import com.concesionaria.diseno.interfaces.Lavable;
import com.concesionaria.diseno.modelo.Vehiculo;

public class Lavadero {
    
    public void procesarVehiculo(Vehiculo vehiculo) {
        if (vehiculo instanceof Lavable) {
            Lavable lavable = (Lavable) vehiculo;
            lavable.lavar();
            lavable.detallar();
        }
    }
    
    public void lavarVehiculo(Vehiculo vehiculo) {
        if (vehiculo instanceof Lavable) {
            ((Lavable) vehiculo).lavar();
        }
    }
    
    public void detallarVehiculo(Vehiculo vehiculo) {
        if (vehiculo instanceof Lavable) {
            ((Lavable) vehiculo).detallar();
        }
    }
}