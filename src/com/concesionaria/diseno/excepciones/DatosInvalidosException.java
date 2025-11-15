package com.concesionaria.diseno.excepciones;

public class DatosInvalidosException extends VehiculoException {
    public DatosInvalidosException(String mensaje) {
        super("Datos inválidos: " + mensaje);
    }
}