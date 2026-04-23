package com.emt.modelo;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Viaje {

    //Atributos
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

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("");
        sb.append("ID: ").append(id);
        sb.append(" | ").append(viajero);
        sb.append(" | ").append(lineaTransporte);
        sb.append(" | ").append(origen);
        sb.append(" --> ").append(destino);
        sb.append(" | ").append(fechaViaje);
        sb.append(" ").append(horaViaje.format(DateTimeFormatter.ofPattern("hh:mm")));
        sb.append(" | ").append(duracionMinutos).append(" min");;
        sb.append(" | ").append(precio).append("€");
        sb.append(" | Incidencia: ").append(incidencia);
        return sb.toString();
    }
}
