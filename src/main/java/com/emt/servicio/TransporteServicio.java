package com.emt.servicio;

import com.emt.modelo.TipoAbono;
import com.emt.modelo.Viaje;
import com.emt.modelo.Viajero;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

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

    /**
     * Mostrar todos los viajes de una línea dada (por ejemplo "L1"),
     * ordenados por hora de forma ascendente.
     * @param linea
     * @return List<Viaje>
     */
    public List<Viaje> getViajesPorLinea(String linea) {
        return this.viajes.stream()
                .filter(viaje -> viaje.getLineaTransporte().equals(linea))
                .sorted(Comparator.comparing(Viaje::getFechaViaje))
                .toList();
    }


    /**
     * Obtener los nombres sin repetición de los viajeros que hayan tenido algún
     * viaje con incidencia, ordenados alfabéticamente.
     * @param
     * @return List<String>
     */
    public List<String> getViajerosConIncidencias() {
        return this.viajes.stream()
                .filter(Viaje::isIncidencia)
                .map(Viaje::getViajero)
                .map(Viajero::getNombre)
                .distinct()
                .sorted()
                .toList();
    }


    /**
     * Encontrar el primer viaje registrado
     * (el más temprano) de una fecha concreta.
     * @param
     * @return  Optional<Viaje>
     */
    public Optional<Viaje> getPrimerViaje(LocalDate fecha) {
        return this.viajes.stream()
                .filter(viaje -> viaje.getFechaViaje().equals(fecha))
                .min(Comparator.comparing(Viaje::getFechaViaje));
    }



    /**
     * Mostrar los viajes cuya duración supere el número de minutos dado,
     * ordenados de mayor a menor duración.
     * @param
     * @return List<Viaje>
     */
    public List<Viaje> getViajesLargos(int minutos) {
        return this.viajes.stream()
                .filter(viaje -> viaje.getDuracionMinutos() > minutos)
                .sorted(Comparator.comparing(Viaje::getDuracionMinutos).reversed())
                .toList();
    }




    /**
     * Obtener los 5 viajes que más han durado, mostrando
     * linea, origen, destino, fecha y hora, de mayor a menor.
     * @param
     * @return  List<String>
     */
    public List<String> getTop5ViajesPorDuracion() {
        return this.viajes.stream()
                .sorted(Comparator.comparing(Viaje::getDuracionMinutos).reversed())
                .limit(5)
                .map(viaje -> viaje.getLineaTransporte() + " | " + viaje.getOrigen() + " --> " + viaje.getDestino() + " | "
                        + viaje.getFechaViaje() + " "+  viaje.getHoraViaje().format(DateTimeFormatter.ofPattern("hh:mm")))
                .toList();
    }


    /**
     *Crear un mapa donde la clave sea el nombre del viajero y el valor
     * sea la suma total gastada en todos sus viajes.
     * @param
     * @return Map<String, Double>
     */
    public Map<String, Double> getGastoTotalPorViajero() {
        return this.viajes.stream()
                .collect(Collectors.groupingBy(viaje -> viaje.getViajero().getNombre(),Collectors.summingDouble(Viaje::getPrecio)));
    }

    /**
     *Obtener la duración media de los viajes agrupada por línea de transporte.
     * @param
     * @return Map<String, Double>
     */
    public  Map<String, Double> getDuracionMediaPorLinea() {
        return this.viajes.stream()
                .collect(Collectors.groupingBy(Viaje::getLineaTransporte,Collectors.averagingInt(Viaje::getDuracionMinutos)));
    }


    /**
     *Crear un mapa donde la clave sea el mes (valor numérico 1–12) y el valor sea el número de
     * viajes realizados ese mes. Mostrar ordenado por mes.
     * @param
     * @return Map<Integer, Long>
     */
    public Map<Integer, Long> getViajesPorMes() {
        return this.viajes.stream()
                .collect(Collectors.groupingBy(viaje -> viaje.getFechaViaje().getMonthValue(),Collectors.counting()));
    }


    /**
     *Municipios que tengan incidencias ordenados alfabéticamente..
     * @param
     * @return Set<String>
     */
    public Set<String> getMunicipiosConIncidencias() {
        return this.viajes.stream()
                .filter(Viaje::isIncidencia)
                .map(Viaje::getViajero)
                .collect(Collectors.mapping(viajero -> viajero.getMunicipio(),Collectors.toSet()));
    }



    /**
     *Calcular y mostrar las siguientes estadísticas sobre la duración de todos los viajes:
     * Media
     * Duración mínima
     * Duración máxima
     * Suma total de minutos
     * @param
     * @return void
     */
    public void getEstadisticasDuracion() {
        IntSummaryStatistics statistics = this.viajes.stream()
                .mapToInt(Viaje::getDuracionMinutos)
                .summaryStatistics();

        IO.println(" Media " + statistics.getAverage());
        IO.println(" Duración mínima " + statistics.getMin());
        IO.println(" Duración máxima " + statistics.getMax());
        IO.println(" Suma total de minutos " + statistics.getSum());
    }




    /**
     * Obtener todos los viajes cuyo precio sea menor o igual al umbral dado, ordenados
     * por precio ascendente y mostrando línea, origen, destino y precio.
     * @param
     * @return List<String>
     */
    public List<String> getViajesBaratos(double precioMax){
        return this.viajes.stream()
                .filter(viaje -> viaje.getPrecio() <= precioMax)
                .sorted(Comparator.comparing(Viaje::getPrecio))
                .map(viaje ->  viaje.getOrigen() + " --> " + viaje.getDestino() + " | " + viaje.getPrecio() +"€")
                .toList();
    }


    /**
     * Obtener todos los viajes cuyo precio sea menor o igual al umbral dado, ordenados
     * por precio ascendente y mostrando línea, origen, destino y precio.
     * @param
     * @return boolean
     */
    public boolean todosAnualesSuperanPuntos(int puntos) {
        return this.viajes.stream()
                .filter(viaje -> viaje.getViajero().getSaldoPuntos() > puntos)
                .allMatch(viaje -> viaje.getViajero().getTipoAbono().equals(TipoAbono.ANUAL));
    }



    /**
     * Dado un mes (valor numérico 1–12), mostrar el gasto total de
     * cada viajero que haya realizado algún viaje ese mes.
     * @param
     * @return Map<String, Double>
     */
    public Map<String, Double> getGastoPorViajeroEnMes(int mes) {
        return this.viajes.stream()
                .filter(viaje -> viaje.getFechaViaje().getMonthValue() == mes)
                .sorted(Comparator.comparing(Viaje::getPrecio))
                .collect(Collectors.groupingBy(viaje -> viaje.getViajero().getNombre(),Collectors.summingDouble(Viaje::getPrecio)));
    }



    /**
     *Obtener la línea de transporte que ha acumulado más viajes con incidencia.
     * @param
     * @return Optional<Map.Entry<String, Long>>
     */
    public  Optional<String> getLineaConMasIncidencias() {
        return this.viajes.stream()
                .filter(Viaje::isIncidencia)
                .collect(Collectors.groupingBy(Viaje::getLineaTransporte,Collectors.counting()))
                .entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(v -> v.getKey());
    }


    /**
     *Obtener los nombres de los viajeros con tipo de abono OCASIONAL
     * cuyo gasto total acumulado supere el umbral dado.
     * @param
     * @return List<String>
     */
    public List<String> getOcasionalesGastoAlto(double umbral){
        return this.viajes.stream()
                .filter(viaje -> viaje.getViajero().getTipoAbono().equals(TipoAbono.OCASIONAL))
                .collect(Collectors.groupingBy(viaje -> viaje.getViajero().getNombre(),Collectors.summingDouble(Viaje::getPrecio)))
                .entrySet()
                .stream()
                .filter( gasto -> gasto.getValue() > umbral)
                .map(v -> v.getKey())
                .toList();
    }

}