package com.concesionaria.diseno.repositorio;

import com.concesionaria.diseno.excepciones.*;
import java.util.*;

public class RepositorioGenerico<T> {
    private Map<String, T> elementos;
    
    public RepositorioGenerico() {
        this.elementos = new LinkedHashMap<>();
    }
    
    public void agregar(String id, T elemento) throws VehiculoDuplicadoException {
        if (elementos.containsKey(id)) {
            throw new VehiculoDuplicadoException(id);
        }
        elementos.put(id, elemento);
    }
    
    public T buscar(String id) throws VehiculoNoEncontradoException {
        T elemento = elementos.get(id);
        if (elemento == null) {
            throw new VehiculoNoEncontradoException(id);
        }
        return elemento;
    }
    
    public void actualizar(String id, T elemento) throws VehiculoNoEncontradoException {
        if (!elementos.containsKey(id)) {
            throw new VehiculoNoEncontradoException(id);
        }
        elementos.put(id, elemento);
    }
    
    public void eliminar(String id) throws VehiculoNoEncontradoException {
        if (!elementos.containsKey(id)) {
            throw new VehiculoNoEncontradoException(id);
        }
        elementos.remove(id);
    }
    
    public List<T> listarTodos() {
        return new ArrayList<>(elementos.values());
    }
    
    public boolean existe(String id) {
        return elementos.containsKey(id);
    }
    
    public int cantidad() {
        return elementos.size();
    }
    
    public void limpiar() {
        elementos.clear();
    }
}
