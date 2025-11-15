package com.concesionaria.negocio;

import com.concesionaria.diseno.excepciones.*;
import com.concesionaria.diseno.modelo.*;
import com.concesionaria.diseno.persistencia.PersistenciaArchivo;
import com.concesionaria.diseno.repositorio.RepositorioGenerico;
import com.concesionaria.negocio.taller.TallerRevision;
import java.util.*;
import java.util.stream.Collectors;

public class GestorConcesionaria {
    private RepositorioGenerico<Vehiculo> repositorio;
    private TallerRevision taller;
    private PersistenciaArchivo<Vehiculo> persistencia;
    private static final String RUTA_ARCHIVO = "data/vehiculos.dat";
    
    public GestorConcesionaria() {
        this.repositorio = new RepositorioGenerico<>();
        this.taller = new TallerRevision();
        this.persistencia = new PersistenciaArchivo<>(RUTA_ARCHIVO);
        cargarDatos();
    }
    
    // CREATE
    public void agregarVehiculo(Vehiculo vehiculo) throws VehiculoException {
        validarVehiculo(vehiculo);
        repositorio.agregar(vehiculo.getId(), vehiculo);
        guardarDatos();
    }
    
    // READ
    public Vehiculo buscarVehiculo(String id) throws VehiculoNoEncontradoException {
        return repositorio.buscar(id);
    }
    
    public List<Vehiculo> listarTodos() {
        return repositorio.listarTodos();
    }
    
    public List<Vehiculo> listarPorTipo(Class<? extends Vehiculo> tipo) {
        return repositorio.listarTodos().stream()
                .filter(v -> tipo.isInstance(v))
                .collect(Collectors.toList());
    }
    
    public List<Vehiculo> listarUsados() {
        return repositorio.listarTodos().stream()
                .filter(Vehiculo::esUsado)
                .collect(Collectors.toList());
    }
    
    // UPDATE
    public void actualizarVehiculo(String id, Vehiculo vehiculo) throws VehiculoException {
        validarVehiculo(vehiculo);
        repositorio.actualizar(id, vehiculo);
        guardarDatos();
    }
    
    // DELETE
    public void eliminarVehiculo(String id) throws VehiculoException {
        repositorio.eliminar(id);
        guardarDatos();
    }
    
    // TALLER
    public void ingresarVehiculoAlTaller(String id) throws VehiculoException, TallerException {
        Vehiculo vehiculo = repositorio.buscar(id);
        taller.ingresarVehiculo(vehiculo);
    }
    
    public void procesarVehiculoTaller() throws TallerException {
        taller.procesarVehiculo();
        guardarDatos();
    }
    
    public int cantidadVehiculosEnTaller() {
        return taller.cantidadEnEspera();
    }
    
    public boolean tallerTieneVehiculos() {
        return taller.tieneVehiculosEnEspera();
    }
    
    public Vehiculo verProximoVehiculoTaller() {
        return taller.verProximoVehiculo();
    }
    
    // VALIDACIÓN
    private void validarVehiculo(Vehiculo vehiculo) throws DatosInvalidosException {
        if (vehiculo == null) {
            throw new DatosInvalidosException("El vehículo no puede ser nulo");
        }
        if (vehiculo.getId() == null || vehiculo.getId().trim().isEmpty()) {
            throw new DatosInvalidosException("El ID no puede estar vacío");
        }
        if (vehiculo.getMarca() == null || vehiculo.getMarca().trim().isEmpty()) {
            throw new DatosInvalidosException("La marca no puede estar vacía");
        }
        if (vehiculo.getModelo() == null || vehiculo.getModelo().trim().isEmpty()) {
            throw new DatosInvalidosException("El modelo no puede estar vacío");
        }
        if (vehiculo.getAnio() == null || vehiculo.getAnio() < 1900 || 
            vehiculo.getAnio() > Calendar.getInstance().get(Calendar.YEAR) + 1) {
            throw new DatosInvalidosException("Año inválido");
        }
        if (vehiculo.getKilometraje() == null || vehiculo.getKilometraje() < 0) {
            throw new DatosInvalidosException("Kilometraje inválido");
        }
    }
    
    // PERSISTENCIA
    private void guardarDatos() {
        try {
            persistencia.guardar(repositorio.listarTodos());
        } catch (PersistenciaException e) {
            System.err.println("Error al guardar: " + e.getMessage());
        }
    }
    
    private void cargarDatos() {
        try {
            List<Vehiculo> vehiculos = persistencia.cargar();
            for (Vehiculo v : vehiculos) {
                try {
                    repositorio.agregar(v.getId(), v);
                } catch (VehiculoDuplicadoException e) {
                    // Ignorar duplicados al cargar
                }
            }
        } catch (PersistenciaException e) {
            System.err.println("Error al cargar: " + e.getMessage());
        }
    }
    
    // ESTADÍSTICAS (Uso de Wrappers)
    public Map<String, Integer> obtenerEstadisticas() {
        Map<String, Integer> stats = new HashMap<>();
        
        List<Vehiculo> vehiculos = repositorio.listarTodos();
        
        stats.put("Total", Integer.valueOf(vehiculos.size()));
        stats.put("Automoviles", Integer.valueOf((int) vehiculos.stream()
                .filter(v -> v instanceof Automovil).count()));
        stats.put("Camionetas", Integer.valueOf((int) vehiculos.stream()
                .filter(v -> v instanceof Camioneta).count()));
        stats.put("Motocicletas", Integer.valueOf((int) vehiculos.stream()
                .filter(v -> v instanceof Motocicleta).count()));
        stats.put("Usados", Integer.valueOf((int) vehiculos.stream()
                .filter(Vehiculo::esUsado).count()));
        stats.put("Nuevos", Integer.valueOf((int) vehiculos.stream()
                .filter(v -> !v.esUsado()).count()));
        stats.put("EnTaller", Integer.valueOf(taller.cantidadEnEspera()));
        
        return stats;
    }
}