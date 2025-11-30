package com.concesionaria.negocio.taller;

import com.concesionaria.diseno.interfaces.Lavable;
import com.concesionaria.diseno.modelo.Vehiculo;

public class Lavadero {
    
    public void procesarVehiculo(Vehiculo vehiculo) {
        if (vehiculo instanceof Lavable) {
            Lavable lavable = (Lavable) vehiculo;
            if (!lavable.estaLavado()) {
                System.out.println("\nLavando el vehículo...");
                try {
                    Thread.sleep(1000);
                    System.out.println("...");
                    Thread.sleep(1000);
                    System.out.println("...");
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                lavable.lavar();
                System.out.println("Lavado completado.");
            }
            
            System.out.println("\nDetallando y puliendo...");
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            lavable.detallar();
            System.out.println("Vehículo listo para la venta!");
        }
    }
}