package com.concesionaria.negocio;

import com.concesionaria.diseno.excepciones.*;
import com.concesionaria.diseno.modelo.*;
import com.concesionaria.diseno.persistencia.PersistenciaArchivo;
import com.concesionaria.diseno.repositorio.RepositorioGenerico;
import com.concesionaria.negocio.taller.TallerRevision;
import java.util.*;
import java.util.stream.Collectors;
/**
 * Clase controladora principal del sistema (Business Logic).
 * Se encarga de coordinar la persistencia, el repositorio de memoria y la lógica del taller.
 * Implementa el patrón Singleton implícito al ser instanciada una única vez por el Main.
 */
public class GestorConcesionaria {
    private RepositorioGenerico<Vehiculo> repositorio;
        private TallerRevision taller;
            // Persistencia separada para inventario y cola de espera
            private PersistenciaArchivo<Vehiculo> persistencia;
            private static final String RUTA_ARCHIVO = "data/vehiculos.dat";
            private static final String RUTA_ARCHIVO_TALLER = "data/cola_taller.dat";  
            private PersistenciaArchivo<String> persistenciaColaTaller; // Guarda solo IDs
            private long proximoIdDisponible;        
                        public GestorConcesionaria() {
                            this.repositorio = new RepositorioGenerico<>();
                            this.taller = new TallerRevision(); 
                            this.persistencia = new PersistenciaArchivo<>(RUTA_ARCHIVO);
                            this.persistenciaColaTaller = new PersistenciaArchivo<>(RUTA_ARCHIVO_TALLER); 
                            cargarDatos(); // Carga inicial y configuración de IDs
                        } 
            /**
             * Genera un ID único basado en el último ID registrado en el sistema.
             * @return El nuevo ID como String.
            */                   
            private String generarProximoId() {
        
                return String.valueOf(proximoIdDisponible++);
        
            }
        
    // ==================== ABM (CRUD) ====================

    /**
     * Registra un nuevo vehículo en el sistema.
     * Si el vehículo es usado, se deriva automáticamente a la cola del taller.
     * * @param vehiculo El objeto vehículo con los datos cargados (sin ID).
     * @return El objeto vehículo con el ID asignado.
     * @throws VehiculoException Si los datos no son válidos.
     * @throws RuntimeException Si ocurre un error interno en la generación de IDs.
     */        
        

    public Vehiculo agregarVehiculo(Vehiculo vehiculo) throws VehiculoException {
        validarVehiculo(vehiculo);

        String nuevoId = generarProximoId();
        vehiculo.setId(nuevoId);

        
        try {
            // La validación de duplicados es interna ya que el ID es autogenerado.
            repositorio.agregar(vehiculo.getId(), vehiculo);
        } catch (VehiculoDuplicadoException e) {
            
            throw new RuntimeException("Error crítico interno: El sistema generó un ID duplicado (" + nuevoId + ").", e);
        }
        
        // Lógica de Negocio: Autos usados van al taller obligatoriamente
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
    
    /**
     * Busca un vehículo por su identificador único.
     * @param id El ID del vehículo a buscar.
     * @return El objeto Vehiculo encontrado.
     * @throws VehiculoNoEncontradoException Si no existe un vehículo con ese ID.
     */
    public Vehiculo buscarVehiculo(String id) throws VehiculoNoEncontradoException {
        return repositorio.buscar(id);
    }
    
    public List<Vehiculo> listarTodos() {
        return repositorio.listarTodos();
    }
    /**
     * Filtra el inventario por una clase específica (Polimorfismo).
     * @param tipo La clase del vehículo (ej: Automovil.class).
     * @return Lista de vehículos que coinciden con el tipo.
     */
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
    
    /**
     * Actualiza los datos de un vehículo existente.
     * Si un vehículo cambia de estado NUEVO a USADO, se envía al taller.
     */
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
             // Ignorar si ya estaba en cola
        }
    }
    
    guardarDatos();
}
    
    
    public void eliminarVehiculo(String id) throws VehiculoException {
        repositorio.eliminar(id);
        guardarDatos();
    }
    
    // ==================== TALLER ====================

    /**
     * Procesa el siguiente vehículo en la cola de espera.
     * Realiza la sincronización de estado entre el objeto en cola y el objeto en repositorio.
     */
    public void procesarVehiculoTaller() throws TallerException {
    // 1. Extraer de la cola (FIFO)
    Vehiculo vehiculoDeLaCola = taller.procesarVehiculo(); 
    
    try {
        // 2. Sincronización: Buscar la instancia "viva" en el repositorio
        // Esto evita que datos obsoletos de la cola sobrescriban cambios recientes del inventario.
        Vehiculo vehiculoDelRepo = repositorio.buscar(vehiculoDeLaCola.getId());
        
       
        // 3. Actualizar flags de servicio (Mantenimiento/Lavado)
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
        
        // 4. Persistir el cambio de estado en el repositorio
        repositorio.actualizar(vehiculoDelRepo.getId(), vehiculoDelRepo); 
        
    } catch (VehiculoNoEncontradoException e) {
        // Caso raro: Alguien borró el auto del sistema mientras estaba en el taller
        System.err.println("Advertencia: El vehículo procesado ya no existe en el inventario principal.");
    }
    
    //  Guardamos ambos archivos para que todo quede sincronizado
    guardarDatos(); 
}
    // Métodos delegados al Taller
    public List<Vehiculo> obtenerVehiculosEnCola() {
    return taller.getVehiculosEnCola();
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
    
    // ==================== PERSISTENCIA Y CONFIGURACIÓN ====================
    private void validarVehiculo(Vehiculo vehiculo) throws DatosInvalidosException {
        // Validaciones de integridad de datos obligatorias
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
    
    /**
     * Guarda el estado completo del sistema en archivos binarios.
     * Estrategia:
     * - vehiculos.dat: Guarda los objetos completos.
     * - cola_taller.dat: Guarda solo los IDs para evitar redundancia y desincronización.
     */
    private void guardarDatos() {
    try {
        // 1. Guardamos el inventario completo (Objetos reales)
        persistencia.guardar(repositorio.listarTodos());
        
        // 2. Extraemos SOLO los IDs de la cola
        List<String> idsEnCola = taller.getVehiculosEnCola().stream()
                                       .map(Vehiculo::getId)
                                       .collect(Collectors.toList());
        
        // 3. Guardamos la lista de IDs
        persistenciaColaTaller.guardar(idsEnCola);
        
    } catch (PersistenciaException e) {
        System.err.println("Error al guardar: " + e.getMessage());
    }
}
    /**
     * Carga los datos iniciales y reconstruye el estado del sistema.
     * Calcula el próximo ID disponible basado en los datos cargados.
     */
    private void cargarDatos() {
    try {
        // 1. Cargar Inventario Principal
        List<Vehiculo> vehiculos = persistencia.cargar();
        long maxId = 0;
        
        for (Vehiculo v : vehiculos) {
            try {
                repositorio.agregar(v.getId(), v);
                
                // Detección automática del último ID
                long idActual = Long.parseLong(v.getId());
                if (idActual > maxId) {
                    maxId = idActual;
                }
            } catch (VehiculoDuplicadoException | NumberFormatException e) {
                // Ignorar inconsistencias
            }
        }
        this.proximoIdDisponible = maxId + 1;

        // 2. Reconstruir la cola usando los IDs
        List<String> idsEnCola = persistenciaColaTaller.cargar();
        List<Vehiculo> vehiculosReconstruidos = new ArrayList<>();
        
        for (String id : idsEnCola) {
            try {
                // Recuperamos el objeto "vivo" del repositorio
                Vehiculo v = repositorio.buscar(id);
                vehiculosReconstruidos.add(v);
            } catch (VehiculoNoEncontradoException e) {
                // Si el ID ya no existe en el inventario, se descarta de la cola
                System.err.println("Advertencia: El vehículo ID " + id + " estaba en cola pero ya no existe en inventario.");
            }
        }
        
        
        this.taller = new TallerRevision(vehiculosReconstruidos);

    } catch (PersistenciaException e) {
        System.err.println("Error al cargar: " + e.getMessage());
        this.proximoIdDisponible = 1;
        this.taller = new TallerRevision(); // Iniciar vacío si falla carga
    }
}
    
    // ESTADÍSTICAS
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