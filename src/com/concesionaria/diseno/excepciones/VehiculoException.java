package com.concesionaria.diseno.excepciones;

public class VehiculoException extends Exception {
    public VehiculoException(String mensaje) {
        super(mensaje);
    }
    
    public VehiculoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
