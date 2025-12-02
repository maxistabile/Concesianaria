package com.concesionaria.presentacion;

import com.concesionaria.diseno.enums.*;
import com.concesionaria.diseno.excepciones.*;
import com.concesionaria.diseno.modelo.*;
import com.concesionaria.diseno.interfaces.Mantenible; // Added
import com.concesionaria.diseno.interfaces.Lavable;   // Added
import com.concesionaria.negocio.GestorConcesionaria;
import java.util.*;

public class MenuPrincipal {
    

    private GestorConcesionaria gestor;
    private Scanner scanner;

    public MenuPrincipal(GestorConcesionaria gestor) {
        this.gestor = gestor;
        this.scanner = new Scanner(System.in);
    }

    public void iniciar() {
        int opcion;
        do {
            mostrarMenuPrincipal();
            opcion = leerEntero("Seleccione una opción: ");
            
            try {
                procesarOpcion(opcion);
            } catch (Exception e) {
                System.err.println("\nError: " + e.getMessage());
            }
            
            if (opcion != 0) {
                esperarEnter();
            }
        } while (opcion != 0);
        
        System.out.println("\n¡Gracias por utilizar el sistema! ¡Hasta luego!");
        scanner.close();
    }

    private void mostrarMenuPrincipal() {
        limpiarPantalla();
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║       SISTEMA DE GESTIÓN DE CONCESIONARIA DE VEHÍCULOS      ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        System.out.println("Gestión de Vehículos");
        System.out.println("   1. Agregar Vehículo");
        System.out.println("   2. Buscar Vehículo");
        System.out.println("   3. Listar Todos los Vehículos");
        System.out.println("   4. Listar por Tipo");
        System.out.println("   5. Actualizar Vehículo");
        System.out.println("   6. Eliminar Vehículo");
        System.out.println("\nTaller y Procesos");
        System.out.println("   7. Procesar Vehículo en Taller");
        System.out.println("   8. Ver Cola del Taller");
        System.out.println("\nReportes");
        System.out.println("   9. Ver Estadísticas");
        System.out.println("\nSalir");
        System.out.println("   0. Salir de la Aplicación");
        System.out.println();
    }

    private void procesarOpcion(int opcion) throws Exception {
        switch (opcion) {
            case 1:
                agregarVehiculo();
                break;
            case 2:
                buscarVehiculo();
                break;
            case 3:
                listarTodos();
                break;
            case 4:
                listarPorTipo();
                break;
            case 5:
                actualizarVehiculo();
                break;
            case 6:
                eliminarVehiculo();
                break;
            case 7:
                procesarTaller();
                break;
            case 8:
                verColaTaller();
                break;
            case 9:
                verEstadisticas();
                break;
            case 0:
                break;
            default:
                System.out.println("Opción inválida. Por favor, intente de nuevo.");
        }
    }

    private void agregarVehiculo() throws VehiculoException {
        System.out.println("\n═══ AGREGAR NUEVO VEHÍCULO ═══\n");

        int tipo;
        Vehiculo vehiculo = null; // Initialize vehiculo here

        do {
            System.out.println("Seleccione el tipo de vehículo:");
            System.out.println("  1. Automóvil");
            System.out.println("  2. Camioneta");
            System.out.println("  3. Motocicleta");
            System.out.println("  0. Volver al Menú Principal");
            tipo = leerEntero("Seleccione: ");

            switch (tipo) {
                case 1:
                case 2:
                case 3:
                    // Valid type, break the loop
                    break;
                case 0:
                    System.out.println("Volviendo al menú principal...");
                    return;
                default:
                    System.out.println("Tipo de vehículo no válido. Por favor, seleccione 1, 2 o 3.");
                    // Loop will continue
            }
        } while (tipo < 1 || tipo > 3); // Loop until a valid type is entered

        // Only proceed to ask for details if type is valid
        String marca = leerCadena("Marca: ");
        String modelo = leerCadena("Modelo: ");
        
        Integer anio;
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        do {
            anio = leerEntero("Año de fabricación (entre 1900 y " + (currentYear + 1) + "): ");
            if (anio < 1900 || anio > (currentYear + 1)) {
                System.out.println("Año inválido. Por favor, ingrese un año entre 1900 y " + (currentYear + 1) + ".");
            }
        } while (anio < 1900 || anio > (currentYear + 1));

        ColorVehiculo color = null;
        try {
            color = seleccionarColor();
        } catch (DatosInvalidosException e) {
            System.out.println("\nOperación de creación de vehículo cancelada: " + e.getMessage());
            return;
        }

        Integer km = leerEntero("Kilometraje: ");

        // Crear una instancia temporal para mostrarla antes de confirmar
        try { // Added try-catch here
            switch (tipo) {
                case 1:
                    TipoCarroceriaAutomovil carroceriaAutomovil = seleccionarCarroceriaAutomovil();
                    vehiculo = new Automovil(marca, modelo, anio, color, km, carroceriaAutomovil);
                    break;
                case 2:
                    TipoCarroceriaCamioneta carroceriaCamioneta = seleccionarCarroceriaCamioneta();
                    vehiculo = new Camioneta(marca, modelo, anio, color, km, carroceriaCamioneta);
                    break;
                case 3:
                    TipoMotocicleta tipoMoto = seleccionarTipoMoto();
                    vehiculo = new Motocicleta(marca, modelo, anio, color, km, tipoMoto);
                    break;
            }
        } catch (DatosInvalidosException e) { // Catch the exception
            System.out.println("\nOperación de creación de vehículo cancelada: " + e.getMessage());
            return;
        }

        System.out.println("\nResumen del vehículo a crear:");
        // Reemplazamos "ID: null" por un texto más amigable como "(Pendiente)"
        System.out.println(vehiculo.toString().replace("ID: null |", "ID: (Pendiente) |"));

        String confirmar = leerCadena("\n¿Confirma la creación de este vehículo? (S/N): ");
        if (confirmar.equalsIgnoreCase("S")) {
            // Capture the returned vehicle with its assigned ID
            vehiculo = gestor.agregarVehiculo(vehiculo); // MODIFIED LINE
            System.out.println("\n¡Vehículo agregado exitosamente!");
            System.out.println("ID asignado: " + vehiculo.getId()); // NEW LINE
            if (vehiculo.esUsado()) {
                System.out.println("   Vehículo usado detectado. Ha sido añadido a la cola del taller automáticamente.");
            }
        } else {
            System.out.println("\nOperación cancelada.");
        }
    }

    private void buscarVehiculo() {
        System.out.println("\n═══ BUSCAR VEHÍCULO ═══\n");
        String id = leerCadena("Ingrese el ID del vehículo(0 para volver): ");

        try {
            Vehiculo v = gestor.buscarVehiculo(id);
            System.out.println("\nVehículo encontrado:");
            List<Vehiculo> vehiculosEncontrados = new ArrayList<>();
            vehiculosEncontrados.add(v);
            imprimirTablaVehiculos(vehiculosEncontrados);
        } catch (VehiculoNoEncontradoException e) {
            System.out.println("\n" + e.getMessage());
        }
    }

    private void listarTodos() {
        System.out.println("\n═══ LISTADO COMPLETO DE VEHÍCULOS ═══\n");
        List<Vehiculo> vehiculos = gestor.listarTodos();

        imprimirTablaVehiculos(vehiculos);
    }

    private void listarPorTipo() {
        System.out.println("\n═══ LISTAR POR TIPO ═══\n");
        System.out.println("  1. Automóviles");
        System.out.println("  2. Camionetas");
        System.out.println("  3. Motocicletas");
        System.out.println("  4. Vehículos Usados");
        int opcion = leerEntero("Seleccione una opción: ");

        List<Vehiculo> vehiculos = null;
        String titulo = "";

        switch (opcion) {
            case 1:
                vehiculos = gestor.listarPorTipo(Automovil.class);
                titulo = "AUTOMÓVILES";
                break;
            case 2:
                vehiculos = gestor.listarPorTipo(Camioneta.class);
                titulo = "CAMIONETAS";
                break;
            case 3:
                vehiculos = gestor.listarPorTipo(Motocicleta.class);
                titulo = "MOTOCICLETAS";
                break;
            case 4:
                vehiculos = gestor.listarUsados();
                titulo = "VEHÍCULOS USADOS";
                break;
            default:
                System.out.println("Opción no válida.");
                return;
        }

        System.out.println("\n═══ " + titulo + " ═══\n");
        imprimirTablaVehiculos(vehiculos);
    }

    private void actualizarVehiculo() throws VehiculoException {
        System.out.println("\n═══ ACTUALIZAR VEHÍCULO ═══\n");
        if (gestor.listarTodos().isEmpty()) {
            System.out.println("No hay vehículos registrados para actualizar.");
            return;
        }
        listarTodos();

        String id = leerCadena("ID del vehículo a actualizar(0 para volver): ");

        Vehiculo actual = gestor.buscarVehiculo(id);
        System.out.println("\nVehículo actual:");
        List<Vehiculo> vehiculoActualList = new ArrayList<>();
        vehiculoActualList.add(actual);
        imprimirTablaVehiculos(vehiculoActualList);

        System.out.println("\nIngrese los nuevos datos (deje en blanco para no cambiar):");

        String marca = leerCadenaOpcional("Marca [" + actual.getMarca() + "]: ");
        if (!marca.isEmpty())
            actual.setMarca(marca);

        String modelo = leerCadenaOpcional("Modelo [" + actual.getModelo() + "]: ");
        if (!modelo.isEmpty())
            actual.setModelo(modelo);

        Integer anio = leerEnteroOpcional("Año [" + actual.getAnio() + "]: ");
        if (anio != null)
            actual.setAnio(anio);

        System.out.println("\nColor actual: " + actual.getColor().getDescripcion());
        for (int i = 0; i < ColorVehiculo.values().length; i++) {
            System.out.println("  " + (i + 1) + ". " + ColorVehiculo.values()[i].getDescripcion());
        }
        Integer colorOpcion = leerEnteroOpcional("Nuevo color (número de opción): ");
        if (colorOpcion != null) {
            actual.setColor(ColorVehiculo.values()[colorOpcion - 1]);
        }

        Integer km = leerEnteroOpcional("Kilometraje [" + actual.getKilometraje() + "]: ");
        if (km != null) {
            actual.setKilometraje(km); // Esto también actualiza el estado
        }

        if (actual instanceof Automovil) {
            Automovil auto = (Automovil) actual;
            System.out.println("\nCarrocería actual: " + auto.getCarroceria().getDescripcion());
            for (int i = 0; i < TipoCarroceriaAutomovil.values().length; i++) {
                System.out.println("  " + (i + 1) + ". " + TipoCarroceriaAutomovil.values()[i].getDescripcion());
            }
            Integer carroceriaOpcion = leerEnteroOpcional("Nueva carrocería (número de opción): ");
            if (carroceriaOpcion != null) {
                auto.setCarroceria(TipoCarroceriaAutomovil.values()[carroceriaOpcion - 1]);
            }
        } else if (actual instanceof Camioneta) {
            Camioneta camioneta = (Camioneta) actual;
            System.out.println("\nCarrocería actual: " + camioneta.getCarroceria().getDescripcion());
            for (int i = 0; i < TipoCarroceriaCamioneta.values().length; i++) {
                System.out.println("  " + (i + 1) + ". " + TipoCarroceriaCamioneta.values()[i].getDescripcion());
            }
            Integer carroceriaOpcion = leerEnteroOpcional("Nueva carrocería (número de opción): ");
            if (carroceriaOpcion != null) {
                camioneta.setCarroceria(TipoCarroceriaCamioneta.values()[carroceriaOpcion - 1]);
            }
        } else if (actual instanceof Motocicleta) {
            Motocicleta moto = (Motocicleta) actual;
            System.out.println("\nTipo actual: " + moto.getTipoMoto().getDescripcion());
            for (int i = 0; i < TipoMotocicleta.values().length; i++) {
                System.out.println("  " + (i + 1) + ". " + TipoMotocicleta.values()[i].getDescripcion());
            }
            Integer tipoMotoOpcion = leerEnteroOpcional("Nuevo tipo (número de opción): ");
            if (tipoMotoOpcion != null) {
                moto.setTipoMoto(TipoMotocicleta.values()[tipoMotoOpcion - 1]);
            }
        }

        String confirmar = leerCadena("\n¿Está seguro de que desea aplicar los cambios? (S/N): ");
        if (confirmar.equalsIgnoreCase("S")) {
            gestor.actualizarVehiculo(id, actual);
            System.out.println("\n¡Vehículo actualizado exitosamente!");
        } else {
            System.out.println("\nOperación cancelada.");
        }
    }

    private void eliminarVehiculo() throws VehiculoException {
        System.out.println("\n═══ ELIMINAR VEHÍCULO ═══\n");
        if (gestor.listarTodos().isEmpty()) {
            System.out.println("No hay vehículos registrados para actualizar.");
            return;
        }
        listarTodos();

        String id = leerCadena("ID del vehículo a eliminar(0 para volver): ");

        Vehiculo v = gestor.buscarVehiculo(id);
        System.out.println("\nSe eliminará el siguiente vehículo:");
        List<Vehiculo> vehiculosAEliminar = new ArrayList<>();
        vehiculosAEliminar.add(v);
        imprimirTablaVehiculos(vehiculosAEliminar);

        String confirmar = leerCadena("\n¿Está seguro de que desea eliminarlo? (S/N): ");
        if (confirmar.equalsIgnoreCase("S")) {
            gestor.eliminarVehiculo(id);
            System.out.println("\nVehículo eliminado permanentemente.");
        } else {
            System.out.println("\nOperación cancelada.");
        }
    }

    private void procesarTaller() {
        System.out.println("\n═══ PROCESAR VEHÍCULO EN TALLER ═══\n");

        if (!gestor.tallerTieneVehiculos()) {
            System.out.println("No hay vehículos en la cola del taller.");
            return;
        }

        Vehiculo proximo = gestor.verProximoVehiculoTaller();
        System.out.println("Próximo vehículo a procesar:");
        System.out.println(proximo.toString());

        String confirmar = leerCadena("\n¿Desea procesar este vehículo ahora? (S/N): ");
        if (confirmar.equalsIgnoreCase("S")) {
            try {
                gestor.procesarVehiculoTaller();
                // Los mensajes de proceso se muestran en TallerRevision y Lavadero
            } catch (TallerException e) {
                System.out.println("\n" + e.getMessage());
            }
        } else {
            System.out.println("\nOperación cancelada.");
        }
    }

    private void verColaTaller() {
        System.out.println("\n═══ COLA DEL TALLER ═══\n");
        int cantidad = gestor.cantidadVehiculosEnTaller();
        System.out.println("Vehículos en espera de ser procesados: " + cantidad);

        if (cantidad > 0) {
            Vehiculo proximo = gestor.verProximoVehiculoTaller();
            System.out.println("\nPróximo en la fila:");
            List<Vehiculo> proximoVehiculoList = new ArrayList<>();
            proximoVehiculoList.add(proximo);
            imprimirTablaVehiculos(proximoVehiculoList);
        }
    }

    private void verEstadisticas() {
        System.out.println("\n═══ ESTADÍSTICAS GENERALES ═══\n");
        Map<String, Integer> stats = gestor.obtenerEstadisticas();

        System.out.println("Total de vehículos en inventario: " + stats.get("Total"));
        System.out.println("------------------------------------");
        System.out.println("Automóviles: ............ " + stats.get("Automoviles"));
        System.out.println("Camionetas: ............. " + stats.get("Camionetas"));
        System.out.println("Motocicletas: ........... " + stats.get("Motocicletas"));
        System.out.println("------------------------------------");
        System.out.println("Vehículos nuevos: ......... " + stats.get("Nuevos"));
        System.out.println("Vehículos usados: ......... " + stats.get("Usados"));
        System.out.println("En cola del taller: ....... " + stats.get("EnTaller"));
    }

    // Métodos auxiliares
    private ColorVehiculo seleccionarColor() throws DatosInvalidosException {
        System.out.println("\nSeleccione el color:");
        ColorVehiculo[] colores = ColorVehiculo.values();
        for (int i = 0; i < colores.length; i++) {
            System.out.println("  " + (i + 1) + ". " + colores[i].getDescripcion());
        }
        
        int intentos = 0;
        while (intentos < 3) {
            int opcion = leerEntero("Seleccione una opción: ");
            if (opcion >= 1 && opcion <= colores.length) {
                return colores[opcion - 1];
            } else {
                System.out.println("Opción de color inválida. Intento " + (intentos + 1) + " de 3.");
                intentos++;
            }
        }
        throw new DatosInvalidosException("Demasiados intentos inválidos para seleccionar color. Operación cancelada.");
    }

    private TipoCarroceriaAutomovil seleccionarCarroceriaAutomovil() throws DatosInvalidosException {
        System.out.println("\nSeleccione la carrocería del automóvil:");
        TipoCarroceriaAutomovil[] tipos = TipoCarroceriaAutomovil.values();
        for (int i = 0; i < tipos.length; i++) {
            System.out.println("  " + (i + 1) + ". " + tipos[i].getDescripcion());
        }

        int intentos = 0;
        while (intentos < 3) {
            int opcion = leerEntero("Seleccione una opción: ");
            if (opcion >= 1 && opcion <= tipos.length) {
                return tipos[opcion - 1];
            } else {
                System.out.println("Opción de carrocería inválida. Intento " + (intentos + 1) + " de 3.");
                intentos++;
            }
        }
        throw new DatosInvalidosException("Demasiados intentos inválidos para seleccionar carrocería de automóvil. Operación cancelada.");
    }

    private TipoCarroceriaCamioneta seleccionarCarroceriaCamioneta() throws DatosInvalidosException {
        System.out.println("\nSeleccione la carrocería de la camioneta:");
        TipoCarroceriaCamioneta[] tipos = TipoCarroceriaCamioneta.values();
        for (int i = 0; i < tipos.length; i++) {
            System.out.println("  " + (i + 1) + ". " + tipos[i].getDescripcion());
        }
        
        int intentos = 0;
        while (intentos < 3) {
            int opcion = leerEntero("Seleccione una opción: ");
            if (opcion >= 1 && opcion <= tipos.length) {
                return tipos[opcion - 1];
            } else {
                System.out.println("Opción de carrocería inválida. Intento " + (intentos + 1) + " de 3.");
                intentos++;
            }
        }
        throw new DatosInvalidosException("Demasiados intentos inválidos para seleccionar carrocería de camioneta. Operación cancelada.");
    }
    
    private TipoMotocicleta seleccionarTipoMoto() throws DatosInvalidosException {
        System.out.println("\nSeleccione el tipo de motocicleta:");
        TipoMotocicleta[] tipos = TipoMotocicleta.values();
        for (int i = 0; i < tipos.length; i++) {
            System.out.println("  " + (i+1) + ". " + tipos[i].getDescripcion());
        }
        
        int intentos = 0;
        while (intentos < 3) {
            int opcion = leerEntero("Seleccione una opción: ");
            if (opcion >= 1 && opcion <= tipos.length) {
                return tipos[opcion - 1];
            } else {
                System.out.println("Opción de tipo de motocicleta inválida. Intento " + (intentos + 1) + " de 3.");
                intentos++;
            }
        }
        throw new DatosInvalidosException("Demasiados intentos inválidos para seleccionar tipo de motocicleta. Operación cancelada.");
    }
    
    private String leerCadena(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("La entrada no puede estar vacía.");
                continue;
            }
            return input;
        }
    }
    
    private Integer leerEntero(String mensaje) {
        while (true) {
            try {
                System.out.print(mensaje);
                String input = scanner.nextLine().trim();
                if (input.isEmpty()) {
                    System.out.println("La entrada no puede estar vacía.");
                    continue;
                }
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Debe ingresar un número válido.");
            }
        }
    }
    
    private Integer leerEnteroOpcional(String mensaje) {
        System.out.print(mensaje);
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) {
            return null;
        }
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Debe ingresar un número válido.");
            return leerEnteroOpcional(mensaje);
        }
    }
    // Método que permite texto vacío (para actualizaciones)
    private String leerCadenaOpcional(String mensaje) {
        System.out.print(mensaje);
        return scanner.nextLine().trim();
    }

    private void esperarEnter() {
        System.out.println("\nPulse ENTER para continuar...");
        scanner.nextLine();
    }
    
    private void limpiarPantalla() {
        try {
            String operatingSystem = System.getProperty("os.name");

            if (operatingSystem.contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            // Si falla, simplemente imprime algunas líneas en blanco como antes, pero menos.
            for (int i = 0; i < 10; i++) {
                System.out.println();
            }
        }
    }
    // Agrega este método privado en MenuPrincipal
private void imprimirTablaVehiculos(List<Vehiculo> vehiculos) {
    if (vehiculos.isEmpty()) {
        System.out.println("No hay vehículos para mostrar.");
        return;
    }

    // New format string with two extra columns for Mant and Lav
    String formato = "| %-4s | %-12s | %-15s | %-10s | %-6s | %-10s | %-12s | %-5s | %-6s |%n"; // Added %-5s and %-6s

    // New header line
    System.out.format("+------+--------------+-----------------+------------+--------+------------+--------------+-------+--------+%n");
    System.out.format("| ID   | Tipo         | Marca           | Modelo     | Año    | KM         | Estado       | Mant. | Lav.   |%n"); // Added Mant. and Lav.
    System.out.format("+------+--------------+-----------------+------------+--------+------------+--------------+-------+--------+%n");

    for (Vehiculo v : vehiculos) {
        String mantenimientoStatus = "N/A";
        String lavadoStatus = "N/A";

        if (v.esUsado()) {
            if (v instanceof Mantenible) {
                mantenimientoStatus = ((Mantenible) v).tieneMantenimientoRealizado() ? "Sí" : "No";
            }
            if (v instanceof Lavable) {
                lavadoStatus = ((Lavable) v).estaLavado() ? "Sí" : "No";
            }
        }
        
        System.out.format(formato, 
            v.getId(), 
            v.getTipoVehiculo(), 
            cortarString(v.getMarca(), 15), 
            cortarString(v.getModelo(), 10), 
            v.getAnio(), 
            v.getKilometraje(),
            v.getEstado().getDescripcion(),
            mantenimientoStatus, // New column
            lavadoStatus         // New column
        );
    }
    System.out.format("+------+--------------+-----------------+------------+--------+------------+--------------+-------+--------+%n");
}

// Método auxiliar para que no se rompa la tabla si el texto es muy largo
private String cortarString(String texto, int largo) {
    if (texto == null) return "";
    if (texto.length() > largo) return texto.substring(0, largo - 3) + "...";
    return texto;
}
}