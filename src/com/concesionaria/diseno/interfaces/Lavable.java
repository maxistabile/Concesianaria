package com.concesionaria.diseno.interfaces;
/**
 * Interfaz que define las operaciones de limpieza y estética para un vehículo.
 * Implementada por los vehículos que pueden pasar por el proceso de Lavadero.
 */
public interface Lavable {
    /**
     * Realiza el proceso de lavado básico del vehículo.
     * Cambia el estado de limpieza a 'verdadero'.
     */
    void lavar();
    /**
     * Realiza el proceso de detallado (pulido, encerado, etc.).
     * Requiere que el vehículo esté lavado previamente.
     */
    void detallar();
    /**
     * Consulta el estado de limpieza del vehículo.
     * @return true si el vehículo ya fue lavado, false en caso contrario.
     */
    boolean estaLavado();
}
