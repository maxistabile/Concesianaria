package com.concesionaria.diseno.excepciones;

public class VehiculoDuplicadoException extends VehiculoException {
    public VehiculoDuplicadoException(String identificador) {
        super("Ya existe un vehículo con el identificador: " + identificador);
    }
}