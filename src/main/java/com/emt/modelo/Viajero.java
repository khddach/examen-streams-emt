package com.emt.modelo;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Viajero {
    //Atributos
    private String dni;
    private String nombre;
    private int edad;
    private String municipio;
    private TipoAbono tipoAbono;
    private int saldoPuntos;       // puntos acumulados por viajes


    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("");
        sb.append("DNI: ").append(dni);
        sb.append(" | ").append(nombre);
        sb.append(" | ").append(edad).append(" años");
        sb.append(" | ").append(municipio);
        sb.append(" | ").append(tipoAbono);
        sb.append(" | ").append(saldoPuntos).append(" pts");
        return sb.toString();
    }

}
