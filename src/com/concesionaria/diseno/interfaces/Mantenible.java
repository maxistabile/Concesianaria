package com.concesionaria.diseno.interfaces;
/**
 * Interfaz que define el comportamiento para vehículos que requieren mantenimiento mecánico.
 * Parte del sistema de gestión del Taller.
 */
public interface Mantenible {
    void realizarMantenimiento();
    boolean tieneMantenimientoRealizado();
}