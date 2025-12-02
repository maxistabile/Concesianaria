package com.concesionaria.diseno.interfaces;

import com.concesionaria.diseno.excepciones.TallerException;
/**
 * Define el contrato para cualquier servicio que gestione una cola de espera de vehículos.
 * Permite desacoplar la lógica de "hacer cola" de la implementación concreta (Taller, Lavadero, etc.).
 * * @param <T> El tipo de elemento que este servicio procesa (ej: Vehiculo).
 */
public interface ServicioTaller<T> {
    /**
     * Ingresa un nuevo elemento a la cola de espera del servicio.
     * @param vehiculo El elemento a ingresar.
     * @throws TallerException Si el elemento no cumple los requisitos para ingresar (ej: no es usado).
     */
    void ingresarVehiculo(T vehiculo) throws TallerException;
    /**
     * Retira y devuelve el siguiente elemento de la cola para ser procesado (FIFO).
     * @return El siguiente elemento en espera.
     * @throws TallerException Si la cola está vacía.
     */
    T siguienteVehiculo() throws TallerException;
    int cantidadEnEspera();
    boolean tieneVehiculosEnEspera();
}
