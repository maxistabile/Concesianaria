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
            private static final String RUTA_ARCHIVO_TALLER = "data/cola_taller.dat"; // New file for workshop queue
            private PersistenciaArchivo<Vehiculo> persistenciaColaTaller; // New persistence for workshop queue
            private long proximoIdDisponible;        
                        public GestorConcesionaria() {
                            this.repositorio = new RepositorioGenerico<>();
                            this.taller = new TallerRevision(); // Always initialize to prevent NPE
                            this.persistencia = new PersistenciaArchivo<>(RUTA_ARCHIVO);
                            this.persistenciaColaTaller = new PersistenciaArchivo<>(RUTA_ARCHIVO_TALLER); // Initialize new persistence
                            cargarDatos();
                            calcularProximoIdDisponible(); // Initialize after loading existing data
                        }        
            private String generarProximoId() {
        
                return String.valueOf(proximoIdDisponible++);
        
            }
        
            
        
                // CREATE
        
            
        
                    public Vehiculo agregarVehiculo(Vehiculo vehiculo) throws VehiculoException {
        
            
        
                        validarVehiculo(vehiculo);
        
            
        
                        String nuevoId = generarProximoId();
        
            
        
                        vehiculo.setId(nuevoId);
        
            
        
                        repositorio.agregar(vehiculo.getId(), vehiculo);
        
            
        
                        if (vehiculo.esUsado()) {
        
            
        
                            try {
        
            
        
                                taller.ingresarVehiculo(vehiculo);
        
            
        
                            } catch (TallerException e) {
        
            
        
                                // No debería ocurrir si la lógica es correcta
        
            
        
                                System.err.println("Error inesperado al ingresar al taller: " + e.getMessage());
        
            
        
                            }
        
            
        
                        }
        
            
        
                        guardarDatos();
        
            
        
                        return vehiculo;
        
            
        
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
    public void actualizarVehiculo(String id, Vehiculo vehiculoActualizado) throws VehiculoException {
    validarVehiculo(vehiculoActualizado);
    
    // Recuperar el vehículo viejo para comparar estados
    Vehiculo vehiculoAnterior = repositorio.buscar(id);
    boolean eraNuevo = !vehiculoAnterior.esUsado();
    
    // Actualizar en repositorio
    repositorio.actualizar(id, vehiculoActualizado);
    
    // Lógica de negocio: Si pasó de Nuevo a Usado, debe ir al taller
    if (eraNuevo && vehiculoActualizado.esUsado()) {
        try {
            taller.ingresarVehiculo(vehiculoActualizado);
            System.out.println("   [AVISO] El vehículo ahora figura como USADO. Se ha enviado al taller automáticamente.");
        } catch (TallerException e) {
             // Ya estaba en el taller o error menor
        }
    }
    
    guardarDatos();
}
    
    // DELETE
    public void eliminarVehiculo(String id) throws VehiculoException {
        repositorio.eliminar(id);
        guardarDatos();
    }
    
    // TALLER
    
    public void procesarVehiculoTaller() throws TallerException {
        Vehiculo vehiculoProcesado = taller.procesarVehiculo(); // Capture the processed vehicle
        try {
            repositorio.actualizar(vehiculoProcesado.getId(), vehiculoProcesado); // Update main repository
        } catch (VehiculoNoEncontradoException e) {
            System.err.println("Error: El vehículo procesado no se encontró en el repositorio principal. " + e.getMessage());
        }
        guardarDatos(); // Save changes to both
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
            persistenciaColaTaller.guardar(taller.getVehiculosEnCola()); // Save workshop queue
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

            List<Vehiculo> colaTallerCargada = persistenciaColaTaller.cargar();
            this.taller = new TallerRevision(colaTallerCargada); // Initialize taller with loaded data

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

    private void calcularProximoIdDisponible() {
        long maxId = 0;
        for (Vehiculo v : repositorio.listarTodos()) {
            try {
                long currentId = Long.parseLong(v.getId());
                if (currentId > maxId) {
                    maxId = currentId;
                }
            } catch (NumberFormatException e) {
                System.err.println("Error al parsear ID de vehículo: " + v.getId());
            }
        }
        proximoIdDisponible = maxId + 1;
    }
}