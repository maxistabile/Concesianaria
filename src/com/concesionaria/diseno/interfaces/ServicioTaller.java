package com.concesionaria.diseno.interfaces;

import com.concesionaria.diseno.excepciones.TallerException;

public interface ServicioTaller<T> {
    void ingresarVehiculo(T vehiculo) throws TallerException;
    T siguienteVehiculo() throws TallerException;
    int cantidadEnEspera();
    boolean tieneVehiculosEnEspera();
}
