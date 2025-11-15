package com.concesionaria.diseno.cola;

import java.util.LinkedList;
import java.util.Queue;

public class ColaGenerica<T> {
    private Queue<T> cola;
    
    public ColaGenerica() {
        this.cola = new LinkedList<>();
    }
    
    public void encolar(T elemento) {
        cola.offer(elemento);
    }
    
    public T desencolar() {
        return cola.poll();
    }
    
    public T verPrimero() {
        return cola.peek();
    }
    
    public boolean estaVacia() {
        return cola.isEmpty();
    }
    
    public int tamanio() {
        return cola.size();
    }
    
    public void limpiar() {
        cola.clear();
    }
}