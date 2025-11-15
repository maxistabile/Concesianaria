package com.concesionaria.diseno.interfaces;

import com.concesionaria.diseno.excepciones.TallerException;

public interface ServicioTaller {
    void ingresarVehiculo(Object vehiculo) throws TallerException;
    Object siguienteVehiculo() throws TallerException;
    int cantidadEnEspera();
    boolean tieneVehiculosEnEspera();
}
