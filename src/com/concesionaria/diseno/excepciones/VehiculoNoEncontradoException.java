package com.concesionaria.diseno.excepciones;

public class VehiculoNoEncontradoException extends VehiculoException {
    public VehiculoNoEncontradoException(String identificador) {
        super("No se encontró el vehículo con identificador: " + identificador);
    }
}