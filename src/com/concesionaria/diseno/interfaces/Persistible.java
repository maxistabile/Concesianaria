package com.concesionaria.diseno.interfaces;

import com.concesionaria.diseno.excepciones.PersistenciaException;
import java.util.List;
/**
 * Interfaz genérica para la persistencia de datos.
 * Abstrae el mecanismo de almacenamiento (Archivo, BD, Memoria) del resto del sistema.
 * * @param <T> El tipo de objeto a persistir (debe ser compatible con el medio de almacenamiento).
 */
public interface Persistible<T> {
    /**
     * Guarda una lista completa de objetos en el medio de almacenamiento.
     * Sobrescribe los datos existentes.
     * @param datos La lista de objetos a guardar.
     * @throws PersistenciaException Si ocurre un error de Entrada/Salida.
     */
    void guardar(List<T> datos) throws PersistenciaException;
    /**
     * Recupera todos los objetos almacenados.
     * @return Una lista con los objetos recuperados o una lista vacía si no hay datos.
     * @throws PersistenciaException Si ocurre un error de lectura o formato.
     */
    List<T> cargar() throws PersistenciaException;
}