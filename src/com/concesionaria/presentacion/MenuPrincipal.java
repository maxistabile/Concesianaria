package com.concesionaria.presentacion;

import com.concesionaria.diseno.enums.*;
import com.concesionaria.diseno.excepciones.*;
import com.concesionaria.diseno.modelo.*;
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
                System.err.println("\n❌ Error: " + e.getMessage());
            }
            
            if (opcion != 0) {
                esperarEnter();
            }
        } while (opcion != 0);
        
        System.out.println("\n¡Hasta luego!");
        scanner.close();
    }
    
    private void mostrarMenuPrincipal() {
        limpiarPantalla();
        System.out.println("╔═══════════════════════════════════════════╗");
        System.out.println("║   SISTEMA DE CONCESIONARIA DE VEHÍCULOS   ║");
        System.out.println("╚═══════════════════════════════════════════╝");
        System.out.println();
        System.out.println("1.  Agregar Vehículo");
        System.out.println("2.  Buscar Vehículo");
        System.out.println("3.  Listar Todos los Vehículos");
        System.out.println("4.  Listar por Tipo");
        System.out.println("5.  Actualizar Vehículo");
        System.out.println("6.  Eliminar Vehículo");
        System.out.println("7.  Ingresar Vehículo al Taller");
        System.out.println("8.  Procesar Vehículo en Taller");
        System.out.println("9.  Ver Cola del Taller");
        System.out.println("10. Ver Estadísticas");
        System.out.println("0.  Salir");
        System.out.println();
    }
    
    private void procesarOpcion(int opcion) throws Exception {
        switch (opcion) {
            case 1: agregarVehiculo(); break;
            case 2: buscarVehiculo(); break;
            case 3: listarTodos(); break;
            case 4: listarPorTipo(); break;
            case 5: actualizarVehiculo(); break;
            case 6: eliminarVehiculo(); break;
            case 7: ingresarAlTaller(); break;
            case 8: procesarTaller(); break;
            case 9: verColaTaller(); break;
            case 10: verEstadisticas(); break;
            case 0: break;
            default: System.out.println("❌ Opción inválida");
        }
    }
    
    private void agregarVehiculo() throws VehiculoException {
        System.out.println("\n═══ AGREGAR VEHÍCULO ═══\n");
        
        System.out.println("Tipo de vehículo:");
        System.out.println("1. Automóvil");
        System.out.println("2. Camioneta");
        System.out.println("3. Motocicleta");
        int tipo = leerEntero("Seleccione: ");
        
        String id = leerCadena("ID: ");
        String marca = leerCadena("Marca: ");
        String modelo = leerCadena("Modelo: ");
        Integer anio = leerEntero("Año: ");
        
        ColorVehiculo color = seleccionarColor();
        EstadoVehiculo estado = seleccionarEstado();
        Integer km = leerEntero("Kilometraje: ");
        
        Vehiculo vehiculo = null;
        
        switch (tipo) {
            case 1:
                TipoCarroceria carroceria = seleccionarCarroceria();
                vehiculo = new Automovil(id, marca, modelo, anio, color, estado, km, carroceria);
                break;
            case 2:
                TipoCarroceria carroCamioneta = seleccionarCarroceria();
                vehiculo = new Camioneta(id, marca, modelo, anio, color, estado, km, carroCamioneta);
                break;
            case 3:
                TipoMotocicleta tipoMoto = seleccionarTipoMoto();
                vehiculo = new Motocicleta(id, marca, modelo, anio, color, estado, km, tipoMoto);
                break;
            default:
                throw new DatosInvalidosException("Tipo de vehículo inválido");
        }
        
        gestor.agregarVehiculo(vehiculo);
        System.out.println("\n✅ Vehículo agregado exitosamente");
    }
    
    private void buscarVehiculo() {
        System.out.println("\n═══ BUSCAR VEHÍCULO ═══\n");
        String id = leerCadena("ID del vehículo: ");
        
        try {
            Vehiculo v = gestor.buscarVehiculo(id);
            System.out.println("\n" + v.toString());
        } catch (VehiculoNoEncontradoException e) {
            System.out.println("\n❌ " + e.getMessage());
        }
    }
    
    private void listarTodos() {
        System.out.println("\n═══ LISTADO DE VEHÍCULOS ═══\n");
        List<Vehiculo> vehiculos = gestor.listarTodos();
        
        if (vehiculos.isEmpty()) {
            System.out.println("No hay vehículos registrados");
        } else {
            for (Vehiculo v : vehiculos) {
                System.out.println(v.toString());
                System.out.println("─".repeat(80));
            }
        }
    }
    
    private void listarPorTipo() {
        System.out.println("\n═══ LISTAR POR TIPO ═══\n");
        System.out.println("1. Automóviles");
        System.out.println("2. Camionetas");
        System.out.println("3. Motocicletas");
        System.out.println("4. Vehículos Usados");
        int opcion = leerEntero("Seleccione: ");
        
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
                System.out.println("❌ Opción inválida");
                return;
        }
        
        System.out.println("\n═══ " + titulo + " ═══\n");
        if (vehiculos.isEmpty()) {
            System.out.println("No hay vehículos de este tipo");
        } else {
            for (Vehiculo v : vehiculos) {
                System.out.println(v.toString());
                System.out.println("─".repeat(80));
            }
        }
    }
    
    private void actualizarVehiculo() throws VehiculoException {
        System.out.println("\n═══ ACTUALIZAR VEHÍCULO ═══\n");
        String id = leerCadena("ID del vehículo a actualizar: ");
        
        Vehiculo actual = gestor.buscarVehiculo(id);
        System.out.println("\nVehículo actual:");
        System.out.println(actual.toString());
        
        System.out.println("\nIngrese los nuevos datos:");
        String marca = leerCadena("Marca [" + actual.getMarca() + "]: ");
        if (marca.isEmpty()) marca = actual.getMarca();
        
        String modelo = leerCadena("Modelo [" + actual.getModelo() + "]: ");
        if (modelo.isEmpty()) modelo = actual.getModelo();
        
        actual.setMarca(marca);
        actual.setModelo(modelo);
        
        gestor.actualizarVehiculo(id, actual);
        System.out.println("\n✅ Vehículo actualizado exitosamente");
    }
    
    private void eliminarVehiculo() throws VehiculoException {
        System.out.println("\n═══ ELIMINAR VEHÍCULO ═══\n");
        String id = leerCadena("ID del vehículo: ");
        
        Vehiculo v = gestor.buscarVehiculo(id);
        System.out.println("\n" + v.toString());
        
        String confirmar = leerCadena("\n¿Confirma la eliminación? (S/N): ");
        if (confirmar.equalsIgnoreCase("S")) {
            gestor.eliminarVehiculo(id);
            System.out.println("\n✅ Vehículo eliminado exitosamente");
        } else {
            System.out.println("\n❌ Operación cancelada");
        }
    }
    
    private void ingresarAlTaller() {
        System.out.println("\n═══ INGRESAR VEHÍCULO AL TALLER ═══\n");
        String id = leerCadena("ID del vehículo: ");
        
        try {
            gestor.ingresarVehiculoAlTaller(id);
            System.out.println("\n✅ Vehículo ingresado al taller");
        } catch (VehiculoException | TallerException e) {
            System.out.println("\n❌ " + e.getMessage());
        }
    }
    
    private void procesarTaller() {
        System.out.println("\n═══ PROCESAR VEHÍCULO EN TALLER ═══\n");
        
        if (!gestor.tallerTieneVehiculos()) {
            System.out.println("No hay vehículos en el taller");
            return;
        }
        
        Vehiculo proximo = gestor.verProximoVehiculoTaller();
        System.out.println("Próximo vehículo a procesar:");
        System.out.println(proximo.toString());
        
        String confirmar = leerCadena("\n¿Procesar este vehículo? (S/N): ");
        if (confirmar.equalsIgnoreCase("S")) {
            try {
                gestor.procesarVehiculoTaller();
                System.out.println("\n✅ Vehículo procesado exitosamente");
            } catch (TallerException e) {
                System.out.println("\n❌ " + e.getMessage());
            }
        }
    }
    
    private void verColaTaller() {
        System.out.println("\n═══ COLA DEL TALLER ═══\n");
        int cantidad = gestor.cantidadVehiculosEnTaller();
        System.out.println("Vehículos en espera: " + cantidad);
        
        if (cantidad > 0) {
            Vehiculo proximo = gestor.verProximoVehiculoTaller();
            System.out.println("\nPróximo en ser atendido:");
            System.out.println(proximo.toString());
        }
    }
    
    private void verEstadisticas() {
        System.out.println("\n═══ ESTADÍSTICAS ═══\n");
        Map<String, Integer> stats = gestor.obtenerEstadisticas();
        
        System.out.println("Total de vehículos:     " + stats.get("Total"));
        System.out.println("Automóviles:            " + stats.get("Automoviles"));
        System.out.println("Camionetas:             " + stats.get("Camionetas"));
        System.out.println("Motocicletas:           " + stats.get("Motocicletas"));
        System.out.println("Vehículos nuevos:       " + stats.get("Nuevos"));
        System.out.println("Vehículos usados:       " + stats.get("Usados"));
        System.out.println("En taller:              " + stats.get("EnTaller"));
    }
    
    // Métodos auxiliares
    private ColorVehiculo seleccionarColor() {
        System.out.println("\nColor:");
        ColorVehiculo[] colores = ColorVehiculo.values();
        for (int i = 0; i < colores.length; i++) {
            System.out.println((i+1) + ". " + colores[i].getDescripcion());
        }
        int opcion = leerEntero("Seleccione: ");
        return colores[opcion - 1];
    }
    
    private EstadoVehiculo seleccionarEstado() {
        System.out.println("\nEstado:");
        System.out.println("1. Nuevo");
        System.out.println("2. Usado");
        int opcion = leerEntero("Seleccione: ");
        return opcion == 1 ? EstadoVehiculo.NUEVO : EstadoVehiculo.USADO;
    }
    
    private TipoCarroceria seleccionarCarroceria() {
        System.out.println("\nTipo de carrocería:");
        TipoCarroceria[] tipos = TipoCarroceria.values();
        for (int i = 0; i < tipos.length; i++) {
            System.out.println((i+1) + ". " + tipos[i].getDescripcion());
        }
        int opcion = leerEntero("Seleccione: ");
        return tipos[opcion - 1];
    }
    
    private TipoMotocicleta seleccionarTipoMoto() {
        System.out.println("\nTipo de motocicleta:");
        TipoMotocicleta[] tipos = TipoMotocicleta.values();
        for (int i = 0; i < tipos.length; i++) {
            System.out.println((i+1) + ". " + tipos[i].getDescripcion());
        }
        int opcion = leerEntero("Seleccione: ");
        return tipos[opcion - 1];
    }
    
    private String leerCadena(String mensaje) {
        System.out.print(mensaje);
        return scanner.nextLine().trim();
    }
    
    private Integer leerEntero(String mensaje) {
        while (true) {
            try {
                System.out.print(mensaje);
                String input = scanner.nextLine().trim();
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("❌ Debe ingresar un número válido");
            }
        }
    }
    
    private void esperarEnter() {
        System.out.println("\nPresione ENTER para continuar...");
        scanner.nextLine();
    }
    
    private void limpiarPantalla() {
        for (int i = 0; i < 50; i++) {
            System.out.println();
        }
    }
}