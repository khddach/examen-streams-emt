# Prueba escrita final — Java Streams

## Red de Transporte Público Metropolitano

**Módulo:** Programación · Tema 7 – Streams y Ficheros  
**Tiempo:** 4 horas  
**Entrega:** Repositorio GitHub (enlace a través del aula virtual)

---

## Contexto

La **Empresa Municipal de Transportes (EMT)** necesita analizar los datos de uso de su red de autobuses y metro durante el último año. Para ello dispone de dos ficheros CSV: uno con los datos de los viajeros abonados y otro con los registros de viajes realizados.

Tu tarea consiste en leer esos ficheros, cargar los datos en objetos Java y realizar una serie de consultas usando la **API de Streams de Java**.

---

## Estructura del proyecto Maven

```
examen-streams-emt/
└── src/
    └── main/
        ├── resources/
        │   ├── viajeros.csv
        │   └── viajes.csv
        └── java/
            └── com/
                └── emt/
                    ├── Main.java
                    ├── modelo/
                    │   ├── Viajero.java
                    │   ├── Viaje.java
                    │   └── TipoAbono.java
                    ├── util/
                    │   └── CsvLoader.java
                    └── servicio/
                        └── TransporteServicio.java
```

> **Nota:** Organiza el código en los paquetes indicados. No se valorará un proyecto que no compile o que mezcle las clases en el paquete raíz.

---

## Librerías

Incluye en tu `pom.xml` la dependencia de **Lombok**:

```xml
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <version>1.18.30</version>
    <scope>provided</scope>
</dependency>
```

Vamos a incluir también dos librerías para generar mensajes de **logs**:

```xml
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-api</artifactId>
    <version>2.0.17</version>
</dependency>

<dependency>
    <groupId>ch.qos.logback</groupId>
    <artifactId>logback-classic</artifactId>
    <version>1.5.32</version>
</dependency>
```

---

## Modelo de datos

### Enum `TipoAbono`

Crea un enum con los siguientes valores:

```
MENSUAL
TRIMESTRAL
ANUAL
OCASIONAL
```

---

### Clase `Viajero`

Usa anotaciones Lombok. **No escribas manualmente** getters, setters, constructor
ni `toString()`.

**Atributos:**

```java
private String dni;
private String nombre;
private int edad;
private String municipio;
private TipoAbono tipoAbono;
private int saldoPuntos;       // puntos acumulados por viajes
```

**`toString()` esperado** (sobrescribe el de Lombok):

```
DNI: 12345678A | María López | 34 años | Sevilla | MENSUAL | 320 pts
```

---

### Clase `Viaje`

Usa también anotaciones Lombok.

**Atributos:**

```java
private long id;
private Viajero viajero;          // se resuelve buscando por DNI al cargar
private String lineaTransporte;   // ej: "L1", "L5", "Metro-A"
private String origen;
private String destino;
private LocalDate fechaViaje;
private LocalTime horaViaje;
private int duracionMinutos;
private double precio;
private boolean incidencia;       // true si hubo incidencia en el trayecto
```

**`toString()` esperado:**

```
ID: 1 | 12345678A | L1 | Triana → Centro | 2025-09-15 08:30 | 12 min | 1.5€ | Incidencia: No
```

---

## Ficheros CSV

### `viajeros.csv`

Contiene los datos de los abonados. Cabecera y formato:

```
dni,nombre,edad,municipio,tipoAbono,saldoPuntos
12345678A,María López,34,Sevilla,MENSUAL,320
87654321B,Carlos Ruiz,22,Dos Hermanas,OCASIONAL,45
...
```

---

### `viajes.csv`

Contiene los registros de viajes. En lugar de repetir todos los datos del viajero,
**solo aparece el DNI**, que se usará para enlazar con el objeto `Viajero` correspondiente.
Cabecera y formato:

```
id,dni,lineaTransporte,origen,destino,fechaViaje,horaViaje,duracionMinutos,precio,incidencia
1,12345678A,L1,Triana,Centro,2025-09-15,08:30,12,1.50,false
2,87654321B,L5,Aeropuerto,Santa Justa,2025-10-02,17:15,28,2.00,true
...
```

---

## Carga de los ficheros CSV

Añade objeto para generar logs:

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

private static final Logger log = LoggerFactory.getLogger(CsvLoader.class);
```

Implementa los siguientes métodos estáticos en la clase `CsvLoader`:

### `cargarViajeros(String ruta)`

```java
public static List<Viajero> cargarViajeros(String ruta) throws IOException {
    log.info("Cargando viajeros desde {}", ruta);

    return Files.lines(Path.of(ruta))
         .skip(1) // ignorar cabecera
        .map(linea -> {
            List<String> c = List.of(linea.split(","));
            // c.get(0) = dni, c.get(1) = nombre, c.get(2) = edad,
            // c.get(3) = municipio, c.get(4) = tipoAbono, c.get(5) = saldoPuntos
            return new Viajero(...);
        })
        .peek(v -> log.debug("Viajero cargado: {}", v.getId()))
        .collect(Collectors.toList());
}
```

---

### `cargarViajes(String ruta, List<Viajero> viajeros)`

Recibe además la lista de viajeros ya cargada para poder resolver el DNI.

```java
public static List<Viaje> cargarViajes(String ruta, List<Viajero> viajeros) throws IOException {
    log.info("Cargando viajes desde {}", ruta);

    return Files.lines(Path.of(ruta))
        .skip(1) // ignorar cabecera
        .map(linea -> {
            List<String> c = List.of(linea.split(","));

            //Buscamos el Viajero con el dni que viene en el fichero
            Viajero viajero = viajeros.stream()
                .filter(v -> v.getDni().equals(c.get(1))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Viajero no encontrado: " + c[1]));

            return new Viaje(...,viajero ,...);
            );
        })
        .peek(v -> log.debug("Viaje cargado: {}", v.getDni()))
        .collect(Collectors.toList());
}
```

> **Nota:** El método `cargarViajes` delega la resolución del DNI en `TransporteServicio.buscarViajero()` si lo prefieres, pero hacerlo directamente en el `map` es igualmente válido.

---

## Clase `TransporteServicio`

La clase tendrá **dos listas** como atributos y un método auxiliar para buscar viajeros:

```java
public class TransporteServicio {

    private final List<Viajero> viajeros;
    private final List<Viaje> viajes;

    public TransporteServicio(List<Viajero> viajeros, List<Viaje> viajes) {
        this.viajeros = viajeros;
        this.viajes = viajes;
    }

    /**
     * Busca un viajero en la lista por su DNI.
     * @throws RuntimeException si no existe ningún viajero con ese DNI.
     */
    public Viajero buscarViajero(String dni) {
        return viajeros.stream()
            .filter(v -> v.getDni().equals(dni))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Viajero no encontrado: " + dni));
    }

    // ... métodos de consulta de Streams
}
```

### Inicialización en `Main.java`

```java
List<Viajero> viajeros = CsvLoader.cargarViajeros("src/main/resources/viajeros.csv");
List<Viaje>   viajes   = CsvLoader.cargarViajes("src/main/resources/viajes.csv", viajeros);

TransporteServicio servicio = new TransporteServicio(viajeros, viajes);
```

---

## Consultas con Streams

Implementa los siguientes métodos en la clase `TransporteServicio`. Cada método debe:

- Tener un nombre descriptivo tal como se indica.
- Mostrar el resultado por consola con un encabezado claro.
- **No usar bucles `for` / `while`** si la operación puede resolverse con Streams.

---

### Consulta 1 — Viajes de una línea concreta ordenados por hora

**Método:**  `List<Viaje> getViajesPorLinea(String linea)`  
Mostrar todos los viajes de una línea dada (por ejemplo `"L1"`), ordenados por hora
de forma ascendente.  
*Streams:* `filter`, `sorted`

---

### Consulta 2 — Viajeros con incidencias

**Método:** `List<String> getViajerosConIncidencias()`  
Obtener los **nombres sin repetición** de los viajeros que hayan tenido algún viaje
con incidencia, ordenados alfabéticamente.  
*Streams:* `filter`, `map`, `distinct`, `sorted`

---

### Consulta 3 — Primer viaje del día

**Método:** `Optional<Viaje> getPrimerViaje(LocalDate fecha)`  
Encontrar el primer viaje registrado (el más temprano) de una fecha concreta.  
*Streams:* `filter`, `min`

---

### Consulta 4 — Viajes largos

**Método:** `List<Viaje> getViajesLargos(int minutos)`  
Mostrar los viajes cuya duración supere el número de minutos dado, ordenados
de mayor a menor duración.  
*Streams:* `filter`, `sorted`

---

### Consulta 5 — Top 5 viajes más largos

**Método:** `List<String> getTop5ViajesPorDuracion()`  
Obtener los 5 viajes que más han durado, mostrando linea, origen, destino, fecha y hora, de mayor a menor.  
*Streams:* `sorted`, `limit`, `map`

---

### Consulta 6 — Gasto total por viajero

**Método:** `Map<String, Double> getGastoTotalPorViajero()`  
Crear un mapa donde la clave sea el **nombre del viajero** y el valor sea la **suma
total gastada** en todos sus viajes.  
*Streams:* `collect`, `groupingBy`, `summingDouble`

---

### Consulta 7 — Duración media por línea

**Método:** `Map<String, Double> getDuracionMediaPorLinea()`  
Obtener la duración media de los viajes agrupada por línea de transporte.

En el main muestra el mapa ordenado por línea de transporte.  
*Streams:* `collect`, `groupingBy`, `averagingInt`

---

### Consulta 8 — Número de viajes por mes

**Método:** `Map<Integer, Long> getViajesPorMes()`  
Crear un mapa donde la clave sea el **mes** (valor numérico 1–12) y el valor sea
el número de viajes realizados ese mes. Mostrar ordenado por mes.

Para mostrar ordenado por mes, puedes ordenar las claves del mapa una vez obtenido en el main cuando lo vas a pintar.  
*Streams:* `collect`, `groupingBy`, `counting`

---

### Consulta 9 — Municipios con incidencias

**Método:** `Set<String> getMunicipiosConIncidencias()`  
Municipios que tengan incidencias ordenados alfabéticamente. 

*Streams:* `filter, map, collect`

---

### Consulta 10 — Estadísticas de duración de viajes

**Método:** `void getEstadisticasDuracion()`  
Calcular y *mostrar* las siguientes estadísticas sobre la duración de todos los viajes:

- Media
- Duración mínima
- Duración máxima
- Suma total de minutos

*Streams:* `mapToInt`, `summaryStatistics`

---

### Consulta 11 — Viajes gratuitos o de bajo coste

**Método:** `List<String> getViajesBaratos(double precioMax)` Obtener todos los viajes cuyo precio sea menor o igual al umbral dado, ordenados por precio ascendente y mostrando línea, origen, destino y precio.
*Streams:* `filter`, `sorted`, `map` (para formatear la salida)

---

### Consulta 12 — Comprobar si todos los viajeros ANUALES superan 100 puntos

**Método:** `boolean todosAnualesSuperanPuntos(int puntos)`  
Verificar si **todos** los viajeros con tipo de abono `ANUAL` tienen un saldo de
puntos superior al valor dado.  
*Streams:* `filter` sobre `viajeros`, `allMatch`

> **Nota:** Esta consulta opera sobre la lista `viajeros`, no sobre `viajes`.

---

### Consulta 13 — Mostrar el gasto de los viajeros en un mes concreto

**Método:** `Map<String, Double> getGastoPorViajeroEnMes(int mes)` Dado un mes (valor numérico 1–12), mostrar el gasto total de cada viajero que haya realizado algún viaje ese mes.

*Streams:* `filter`, `collect`, `groupingBy`, `summingDouble`

---

### Consulta 14 — Línea con más incidencias

**Método:** `Optional<String> getLineaConMasIncidencias()`  
Obtener la línea de transporte que ha acumulado más viajes con incidencia.

Hay que sacar el máximo del EntrySet (el valor) ordenado por valor.  
*Streams:* `filter`, `collect`, `groupingBy`, `counting`, `max`

---

### Consulta 15 — Viajeros con abono OCASIONAL y gasto alto

**Método:** `List<String> getOcasionalesGastoAlto(double umbral)`  
Obtener los nombres de los viajeros con tipo de abono `OCASIONAL` cuyo gasto
total acumulado supere el umbral dado.

*Streams:* `filter`, `collect`, `groupingBy`, `summingDouble`, `filter` sobre `entryset.stream()`

---

## Main

Carga los ficheros como está indicado más arriba, y crea un menú con las 15 opciones, te pedirá el número y mostrará los resultados.

```
------------------------- RED TRANSPORTE PÚBLICO ------------------------
Elige opción:
1. Viajes de una línea
2. Viajeros con incidencias
...
15. Viajeros abono Ocasional y gasto alto
16. Salir

== Consulta 1: Viajes de la línea L1 ===
ID: 4  | 12345678A | L1 | Triana → Centro       | 2025-09-15 07:45 | 12 min | 1.5€ | Incidencia: No
ID: 12 | 87654321B | L1 | Bellavista → Nervión  | 2025-09-15 09:10 | 18 min | 1.5€ | Incidencia: Sí
...
```

Debes capturar excepciones de entrada/salida y pintar error de log en caso de que se produzca:

```java
log.error("Error de entrada/salida {}", e.getMessage(), e);
```

---

## Requisitos técnicos

- Usar **Java 11 o superior**.
- Proyecto **Maven** con la estructura de paquetes indicada.
- Usar **Lombok** en las clases del modelo.
- Usar **Stream API** en todas las consultas. No se permiten bucles `for`/`while` para resolver las consultas.
- El código debe estar **comentado** y ser legible.
- En `Main.java` deben invocarse **todos los métodos** de `TransporteServicio` con datos representativos.
- Debes capturar excepciones cuando sea necesario y mostrar **mensajes de log** (error).
- El proyecto debe compilar y ejecutarse con `mvn compile exec:java` sin errores.

---

## Entregables

- Repositorio **GitHub público** con el proyecto Maven completo.
- El repositorio debe contener:
  - `Viajero.java`, `Viaje.java`, `TipoAbono.java` con anotaciones Lombok.
  - `CsvLoader.java` con los métodos `cargarViajeros` y `cargarViajes`.
  - `TransporteServicio.java` con los 15 métodos de consulta y el método `buscarViajero`.
  - `Main.java` que llame a todas las consultas.
  - `viajeros.csv` y `viajes.csv` en `src/main/resources/`.
  - `README.md` con: nombre del alumno, descripción de la aplicación, instrucciones para compilar y ejecutar.

---

## Criterios de evaluación

| Criterio                                             | Peso |
| ---------------------------------------------------- | ---- |
| Correcta lectura y parseo de ambos CSV               | 15%  |
| Uso correcto de Lombok en el modelo y de Logs        | 10%  |
| Implementación correcta de cada consulta con Streams | 55%  |
| Claridad, legibilidad y comentarios del código       | 10%  |
| README y presentación de resultados por consola      | 10%  |

> Las consultas 14 y 15 tienen mayor dificultad y se valorarán con más peso dentro del bloque de Streams.

---

*Módulo de Programación — Prueba final Tema 7*
