# Sistema de Concesionaria de Vehículos

## Estructura del Proyecto

```
com.concesionaria/
├── Main.java
├── diseno/
│   ├── enums/
│   │   ├── ColorVehiculo.java
│   │   ├── TipoCarroceriaAutomovil.java
│   │   ├── TipoCarroceriaCamioneta.java
│   │   ├── TipoMotocicleta.java
│   │   └── EstadoVehiculo.java
│   ├── excepciones/
│   │   ├── VehiculoException.java
│   │   ├── VehiculoNoEncontradoException.java
│   │   ├── VehiculoDuplicadoException.java
│   │   ├── DatosInvalidosException.java
│   │   ├── PersistenciaException.java
│   │   └── TallerException.java
│   ├── interfaces/
│   │   ├── Mantenible.java
│   │   ├── Lavable.java
│   │   ├── ServicioTaller.java
│   │   └── Persistible.java
│   ├── modelo/
│   │   ├── Vehiculo.java (abstracta)
│   │   ├── Automovil.java
│   │   ├── Camioneta.java
│   │   └── Motocicleta.java
│   ├── repositorio/
│   │   └── RepositorioGenerico.java
│   ├── cola/
│   │   └── ColaGenerica.java
│   └── persistencia/
│       └── PersistenciaArchivo.java
├── negocio/
│   ├── GestorConcesionaria.java
│   └── taller/
│       ├── TallerRevision.java
│       └── Lavadero.java
└── presentacion/
    └── MenuPrincipal.java
```

## Requisitos Cumplidos

### ✅ Diseño vs. Negocio (4 puntos)
- **diseno/**: Clases de diseño (enums, excepciones, interfaces, modelos)
- **negocio/**: Lógica de negocio (GestorConcesionaria, TallerRevision, Lavadero)
- **presentacion/**: Interfaz de usuario (MenuPrincipal)

### ✅ Patrón JavaBean (4 puntos)
- Todas las clases modelo tienen:
  - Constructor vacío
  - Getters y Setters
  - Implementan Serializable

### ✅ Relación de Herencia (5 puntos)
- `Vehiculo` (clase abstracta)
  - `Automovil`
  - `Camioneta`
  - `Motocicleta`

### ✅ Clase Abstracta (5 puntos)
- `Vehiculo`: Define estructura común y métodos abstractos
  - `getTipoVehiculo()`
  - `obtenerDetallesEspecificos()`

### ✅ Implementación de Interfaces (7 puntos)
**Interfaces de Java:**
- `Serializable`

**Interfaces de Programador:**
- `Mantenible`: realizarMantenimiento(), tieneMantenimientoRealizado()
- `Lavable`: lavar(), detallar(), estaLavado()
- `ServicioTaller`: ingresarVehiculo(), siguienteVehiculo(), etc.
- `Persistible<T>`: guardar(), cargar()

### ✅ Excepciones (8 puntos)
**Excepciones propias:**
- `VehiculoException` (padre)
- `VehiculoNoEncontradoException`
- `VehiculoDuplicadoException`
- `DatosInvalidosException`
- `PersistenciaException`
- `TallerException`

**Manejo:**
- Try-catch en operaciones críticas
- Propagación controlada de excepciones
- Mensajes descriptivos

### ✅ Tipos Enumerativos (3 puntos)
- `ColorVehiculo`
- `TipoCarroceriaAutomovil`
- `TipoCarroceriaCamioneta`
- `TipoMotocicleta`
- `EstadoVehiculo`

### ✅ UX (3 puntos)
- Menú interactivo con opciones numeradas
- Validación de entrada de datos
- Mensajes de error claros
- Confirmaciones para operaciones críticas
- Formato visual agradable

### ✅ Wrappers (5 puntos)
- Uso de `Integer` en lugar de `int`
- `Map<String, Integer>` para estadísticas
- Conversiones automáticas (autoboxing/unboxing)

### ✅ Colecciones (10 puntos)
- `LinkedHashMap<String, T>`: Repositorio (mantiene orden de inserción)
- `ArrayList<T>`: Listados
- `LinkedList<T>`: Cola FIFO para el taller
- `HashMap<String, Integer>`: Estadísticas

**Justificación:**
- **LinkedHashMap**: Acceso O(1) por ID + orden de inserción
- **LinkedList**: Óptima para cola (offer/poll)
- **ArrayList**: Listados y retorno de resultados
- **HashMap**: Agrupación de estadísticas

### ✅ Cola de Espera (6 puntos)
- `ColaGenerica<T>` con `LinkedList`
- Implementada en `TallerRevision`
- Gestión FIFO de vehículos usados
- Métodos: encolar, desencolar, verPrimero

### ✅ Clase Genérica (8 puntos)
- `RepositorioGenerico<T>`: CRUD genérico
- `ColaGenerica<T>`: Cola genérica
- `PersistenciaArchivo<T>`: Persistencia genérica

### ✅ Persistencia (8 puntos)
- `PersistenciaArchivo<T>` con serialización
- Guardado/carga automática
- Manejo de errores I/O
- Archivo: `data/vehiculos.dat`

### ✅ ABMc/CRUD (9 puntos)
**GestorConcesionaria:**
- **Create**: `agregarVehiculo()`
- **Read**: `buscarVehiculo()`, `listarTodos()`, `listarPorTipo()`
- **Update**: `actualizarVehiculo()`
- **Delete**: `eliminarVehiculo()`

## Diagrama UML (simplificado)

```
┌─────────────────────┐
│   <<abstract>>      │
│      Vehiculo       │
├─────────────────────┤
│ - id: String        │
│ - marca: String     │
│ - modelo: String    │
│ - anio: Integer     │
│ - color: ColorVeh.  │
│ - estado: EstadoVeh.│
│ - kilometraje: Int  │
├─────────────────────┤
│ + getTipoVehiculo() │
│ + obtenerDetalles() │
└──────────┬──────────┘
           │
    ┌──────┴──────┬────────────┐
    │             │            │
┌───▼────┐  ┌────▼────┐  ┌────▼────────┐
│Automovil│  │Camioneta│  │Motocicleta  │
└─────────┘  └─────────┘  └─────────────┘
```

```
┌──────────────────────┐
│RepositorioGenerico<T>│
├──────────────────────┤
│ - elementos: Map     │
├──────────────────────┤
│ + agregar()          │
│ + buscar()           │
│ + actualizar()       │
│ + eliminar()         │
│ + listarTodos()      │
└──────────────────────┘
```

```
┌───────────────────┐      ┌──────────────────┐
│GestorConcesionaria├─────►│TallerRevision    │
├───────────────────┤      ├──────────────────┤
│ - repositorio     │      │ - colaEspera     │
│ - taller          │      │ - lavadero       │
│ - persistencia    │      ├──────────────────┤
├───────────────────┤      │ + ingresarVeh()  │
│ + agregarVeh()    │      │ + procesarVeh()  │
│ + buscarVeh()     │      └──────────────────┘
│ + listarTodos()   │
│ + actualizarVeh() │
│ + eliminarVeh()   │
└───────────────────┘
```

## Compilación y Ejecución

```bash
# Compilar (desde la raíz del proyecto)
javac -d bin -sourcepath src src/com/concesionaria/Main.java

# Ejecutar
java -cp bin com.concesionaria.Main
```

## Ejemplo de Uso

1. **Agregar vehículo**: Ingresar datos completos
2. **Listar vehículos**: Ver inventario completo
3. **Ingresar al taller**: Vehículos usados para mantenimiento
4. **Procesar en taller**: Realizar mantenimiento y lavado
5. **Ver estadísticas**: Resumen del inventario

## Notas Técnicas

- Java 8 compatible
- Persistencia con Serialización
- Manejo robusto de excepciones
- Validación de datos de entrada
- Arquitectura multicapa (Diseño/Negocio/Presentación)