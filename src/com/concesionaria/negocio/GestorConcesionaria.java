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
            private PersistenciaArchivo<String> persistenciaColaTaller; // Ahora guarda Strings (IDs)
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
    // 1. Sacamos el vehículo de la cola (este objeto puede tener datos viejos, ej: color viejo)
    // Pero TIENE el estado nuevo de mantenimiento/lavado porque acaba de salir del taller.
    Vehiculo vehiculoDeLaCola = taller.procesarVehiculo(); 
    
    try {
        // 2. Buscamos la versión "fresca" en el repositorio principal (tiene el color nuevo)
        Vehiculo vehiculoDelRepo = repositorio.buscar(vehiculoDeLaCola.getId());
        
        // 3. PASO CLAVE: Copiamos los flags de "ya atendido" del objeto de la cola al objeto del repo.
        // Hacemos casting seguro para acceder a los métodos de las interfaces/clases.
        
        if (vehiculoDeLaCola instanceof Automovil && vehiculoDelRepo instanceof Automovil) {
            ((Automovil) vehiculoDelRepo).setMantenimientoRealizado(true); // Asumimos que el taller lo hizo
            ((Automovil) vehiculoDelRepo).setLavado(true);
        } else if (vehiculoDeLaCola instanceof Camioneta && vehiculoDelRepo instanceof Camioneta) {
            ((Camioneta) vehiculoDelRepo).setMantenimientoRealizado(true);
            ((Camioneta) vehiculoDelRepo).setLavado(true);
        } else if (vehiculoDeLaCola instanceof Motocicleta && vehiculoDelRepo instanceof Motocicleta) {
            ((Motocicleta) vehiculoDelRepo).setMantenimientoRealizado(true);
            ((Motocicleta) vehiculoDelRepo).setLavado(true);
        }
        
        // 4. Guardamos la versión del repo (que ahora tiene el color bien Y el mantenimiento hecho)
        repositorio.actualizar(vehiculoDelRepo.getId(), vehiculoDelRepo); 
        
    } catch (VehiculoNoEncontradoException e) {
        // Caso raro: Alguien borró el auto del sistema mientras estaba en el taller
        System.err.println("Advertencia: El vehículo procesado ya no existe en el inventario principal.");
    }
    
    // 5. Guardamos ambos archivos para que todo quede sincronizado
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
        // 1. Guardamos el inventario completo (Objetos reales)
        persistencia.guardar(repositorio.listarTodos());
        
        // 2. "Opción Perfecta": Extraemos SOLO los IDs de la cola
        List<String> idsEnCola = taller.getVehiculosEnCola().stream()
                                       .map(Vehiculo::getId)
                                       .collect(Collectors.toList());
        
        // 3. Guardamos la lista de IDs
        persistenciaColaTaller.guardar(idsEnCola);
        
    } catch (PersistenciaException e) {
        System.err.println("Error al guardar: " + e.getMessage());
    }
}
    
    private void cargarDatos() {
    try {
        // 1. Cargar Inventario Principal
        List<Vehiculo> vehiculos = persistencia.cargar();
        long maxId = 0;
        
        for (Vehiculo v : vehiculos) {
            try {
                repositorio.agregar(v.getId(), v);
                
                // Cálculo de ID máximo integrado
                long idActual = Long.parseLong(v.getId());
                if (idActual > maxId) {
                    maxId = idActual;
                }
            } catch (VehiculoDuplicadoException | NumberFormatException e) {
                // Ignorar inconsistencias
            }
        }
        this.proximoIdDisponible = maxId + 1;

        // 2. "Opción Perfecta": Reconstruir la cola usando los IDs
        List<String> idsEnCola = persistenciaColaTaller.cargar();
        List<Vehiculo> vehiculosReconstruidos = new ArrayList<>();
        
        for (String id : idsEnCola) {
            try {
                // AQUÍ ES LA CLAVE: Buscamos el objeto VIVO en el repositorio.
                // Si editaste el color ayer, aquí traerá el auto con el color nuevo.
                Vehiculo v = repositorio.buscar(id);
                vehiculosReconstruidos.add(v);
            } catch (VehiculoNoEncontradoException e) {
                System.err.println("Advertencia: El vehículo ID " + id + " estaba en cola pero ya no existe en inventario.");
            }
        }
        
        // Inicializamos el taller con los objetos frescos del repositorio
        this.taller = new TallerRevision(vehiculosReconstruidos);

    } catch (PersistenciaException e) {
        System.err.println("Error al cargar: " + e.getMessage());
        this.proximoIdDisponible = 1;
        this.taller = new TallerRevision(); // Iniciar vacío si falla carga
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