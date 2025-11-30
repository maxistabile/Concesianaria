package com.concesionaria.diseno.persistencia;

import com.concesionaria.diseno.excepciones.PersistenciaException;
import com.concesionaria.diseno.interfaces.Persistible;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class PersistenciaArchivo<T> implements Persistible<T> {
    private String rutaArchivo;
    
    public PersistenciaArchivo(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
    }
    
    @Override
    public void guardar(List<T> datos) throws PersistenciaException {
        ObjectOutputStream oos = null;
        try {
            File archivo = new File(rutaArchivo);
            File directorio = archivo.getParentFile();
            
            if (directorio != null && !directorio.exists()) {
                directorio.mkdirs();
            }
            
            oos = new ObjectOutputStream(new FileOutputStream(archivo));
            oos.writeObject(datos);
            
        } catch (IOException e) {
            throw new PersistenciaException("Error al guardar datos: " + e.getMessage(), e);
        } finally {
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                    // Ignorar error al cerrar
                }
            }
        }
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public List<T> cargar() throws PersistenciaException {
        File archivo = new File(rutaArchivo);
        
        if (!archivo.exists()) {
            return new ArrayList<>();
        }
        
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(new FileInputStream(archivo));
            return (List<T>) ois.readObject();
            
        } catch (IOException | ClassNotFoundException | ClassCastException e) {
            // Si el archivo es incompatible o corrupto, empezamos de cero.
            System.out.println("ADVERTENCIA: No se pudieron cargar los datos guardados. Empezando con una lista vacía.");
            return new ArrayList<>();
        } finally {
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException e) {
                    // Ignorar error al cerrar
                }
            }
        }
    }
    
    public boolean existeArchivo() {
        return new File(rutaArchivo).exists();
    }
    
    public void eliminarArchivo() throws PersistenciaException {
        File archivo = new File(rutaArchivo);
        if (archivo.exists() && !archivo.delete()) {
            throw new PersistenciaException("No se pudo eliminar el archivo");
        }
    }
}