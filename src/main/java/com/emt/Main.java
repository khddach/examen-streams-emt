package com.emt;

import com.emt.modelo.Viaje;
import com.emt.modelo.Viajero;
import com.emt.servicio.TransporteServicio;
import com.emt.util.CsvLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;


public class Main {

    private static final Logger log = LoggerFactory.getLogger(TransporteServicio.class);


    /**
     * listas de Consoltas
     * @param servicio
     * @param opcion
     */
    private static void listConsoltas(TransporteServicio servicio, int opcion) {

        Scanner sc = new Scanner(System.in);
        String telcado;

        switch (opcion) {

            case 1:

                //Consulta 1 — Viajes de una línea concreta ordenados por hora
                IO.println("== Consulta 1: Viajes de la línea L1 ===");
                servicio.getViajesPorLinea("L1").forEach(IO::println);
                break;

            case 2:
                //Consulta 2 — Viajeros con incidencias
                IO.println("== Consulta 2: Viajeros con incidencias ===");
                servicio.getViajerosConIncidencias().forEach(IO::println);
                break;

            case 3:

                //Consulta 3 — Primer viaje del día
                IO.println("== Consulta 3: Primer viaje del día ===");
                IO.println("Dami una fetcha --> 2025-01-14");
                telcado = sc.next();

                servicio.getPrimerViaje(LocalDate.parse(telcado)).ifPresent(IO::println);

                break;


            case 4:

                //Consulta 4 — Viajes largos
                IO.println("== Consulta 4:  Viajes largos ===");
                IO.println("Dami un minutos --> por ejemplo 20");
                telcado = sc.next();
                servicio.getViajesLargos(Integer.parseInt(telcado)).forEach(IO::println);

                break;
            case 5:

                //Consulta 5 — Top 5 viajes más largos
                IO.println("== Consulta 5: Top 5 viajes más largos ===");
                servicio.getTop5ViajesPorDuracion().forEach(IO::println);

                break;

            case 6:

                //Consulta 6 — Gasto total por viajero
                IO.println("== Consulta 6:  Gasto total por viajero ===");
                servicio.getGastoTotalPorViajero().forEach((nombre,precioSumTotal) -> IO.println(nombre + " : " + precioSumTotal));;

                break;

            case 7:

                //Consulta 7 — Duración media por línea
                IO.println("== Consulta 7:  Duración media por línea ===");
                servicio.getDuracionMediaPorLinea().forEach((nombre,media) -> IO.println(nombre + " : " + media));;


                break;

            case 8:

                //Consulta 8 — Número de viajes por mes
                IO.println("== Consulta 8:  Número de viajes por mes ===");
                servicio.getViajesPorMes().forEach((mes,number) -> IO.println(mes + " : " + number));

                break;

            case 9:


                //Consulta 9 — Municipios con incidencias
                IO.println("== Consulta 9:  Municipios con incidencias ===");
                servicio.getMunicipiosConIncidencias().forEach(IO::println);

                break;

            case 10:


                //Consulta 10 — Estadísticas de duración de viajes
                IO.println("== Consulta 10: Estadísticas de duración de viajes ===");
                servicio.getEstadisticasDuracion();
                break;
            case 11:


                //Consulta 11 — Viajes gratuitos o de bajo coste
                IO.println("== Consulta 11: Viajes gratuitos o de bajo coste ===");
                IO.println("Dami una fetcha --> 3.0");
                telcado = sc.next();
                servicio.getViajesBaratos(Double.parseDouble(telcado)).forEach(IO::println);


                break;

            case 12:

                //Consulta 12 — Comprobar si todos los viajeros ANUALES superan 100 puntos
                IO.println("== Consulta 12: Comprobar si todos los viajeros ANUALES superan 100 puntos ===");
                IO.println("Dami un puntos --> 100");
                telcado = sc.next();
                boolean allAnnualExceedPoints = servicio.todosAnualesSuperanPuntos(Integer.parseInt(telcado));
                IO.println("Comprobar si todos los viajeros ANUALES superan 100 puntos --> " + allAnnualExceedPoints);


                break;

            case 13:

                //Consulta 13 — Mostrar el gasto de los viajeros en un mes concreto
                IO.println("== Consulta 13: Mostrar el gasto de los viajeros en un mes concreto ===");
                IO.println("Dami un mes --> 5");
                telcado = sc.next();
                servicio.getGastoPorViajeroEnMes(Integer.parseInt(telcado)).forEach((nombre,sum) -> IO.println(nombre + " : " + sum));;

                break;

            case 14:

                //Consulta 14 — Línea con más incidencias
                IO.println("== Consulta 14: Línea con más incidencias ===");
                servicio.getLineaConMasIncidencias().ifPresent(IO::println);


                break;

            case 15:

                //Consulta 15 — Viajeros con abono OCASIONAL y gasto alto
                IO.println("== Consulta 14: Viajeros con abono OCASIONAL y gasto alto ===");
                IO.println("Dami un umbral --> 2.0");
                telcado = sc.next();
                servicio.getOcasionalesGastoAlto(Double.parseDouble(telcado)).forEach(IO::println);

                break;
            case 16:

                IO.println("== Salir........gracias ===");

                break;
            default:
                IO.println("dami un numero de menu correcto -> 1 --> 16 ");

        }
    }




    /**
     * menu de Consoltas
     * @param servicio
     */
    private static void menuConsoltas(TransporteServicio servicio) {


        Scanner sc = new Scanner(System.in);

        int opcion = -1;

        do {
            System.out.println("------------------------- RED TRANSPORTE PÚBLICO ------------------------");
            System.out.println("Elige opción:");
            System.out.println("1. Viajes de una línea");
            System.out.println("2. Viajeros con incidencias");
            System.out.println("3. Primer viaje del día");
            System.out.println("4. Viajes largos");
            System.out.println("5. Top 5 viajes más largos");
            System.out.println("6. Gasto total por viajero");
            System.out.println("7. Duración media por línea");
            System.out.println("8. Número de viajes por mes");
            System.out.println("9. Municipios con incidencias");
            System.out.println("10. Estadísticas de duración de viajes");
            System.out.println("11. Viajes gratuitos o de bajo coste");
            System.out.println("12. Comprobar si todos los viajeros ANUALES superan 100 puntos");
            System.out.println("13. Mostrar el gasto de los viajeros en un mes concreto");
            System.out.println("14. Línea con más incidencias");
            System.out.println("15. Viajeros con abono OCASIONAL y gasto alto");
            System.out.println("16. Salir");

            try {

                opcion = sc.nextInt();

                listConsoltas(servicio,opcion);

            } catch (Exception e) {
                log.error("Error de entrada/salida {}", e.getMessage(), e);
                sc = new Scanner(System.in);
                opcion = -1;
            }

        } while (opcion != 16);


    }



    public static void main(String[] args) throws IOException {


        List<Viajero> viajeros = CsvLoader.cargarViajeros("src/main/resources/viajeros.csv");
        List<Viaje>   viajes   = CsvLoader.cargarViajes("src/main/resources/viajes.csv", viajeros);


        TransporteServicio servicio = new TransporteServicio(viajeros, viajes);

        menuConsoltas(servicio);


    }
}
