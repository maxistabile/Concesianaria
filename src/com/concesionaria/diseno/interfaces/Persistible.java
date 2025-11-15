package com.concesionaria.diseno.interfaces;

import com.concesionaria.diseno.excepciones.PersistenciaException;
import java.util.List;

public interface Persistible<T> {
    void guardar(List<T> datos) throws PersistenciaException;
    List<T> cargar() throws PersistenciaException;
}