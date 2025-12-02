package com.concesionaria.diseno.cola;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
/**
 * Estructura de datos genérica que implementa el comportamiento FIFO (First In, First Out).
 * Encapsula una LinkedList de Java para gestionar el orden de llegada.
 */
public class ColaGenerica<T> {
    private Queue<T> cola;
    
    public ColaGenerica() {
        // Usamos LinkedList porque implementa eficientemente la interfaz Queue (FIFO)
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

    public List<T> getElementos() {
        return new LinkedList<>(cola);
    }
}