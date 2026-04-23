package com.emt.util;

import com.emt.modelo.TipoAbono;
import com.emt.modelo.Viaje;
import com.emt.modelo.Viajero;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

public class CsvLoader {

    private static final Logger log = LoggerFactory.getLogger(CsvLoader.class);

    public static List<Viajero> cargarViajeros(String ruta) throws IOException {
        log.info("Cargando viajeros desde {}", ruta);

        return Files.lines(Path.of(ruta))
                .skip(1) // ignorar cabecera
                .map(linea -> {
                    List<String> c = List.of(linea.split(","));
                    // c.get(0) = dni, c.get(1) = nombre, c.get(2) = edad,
                    // c.get(3) = municipio, c.get(4) = tipoAbono, c.get(5) = saldoPuntos

                    return new Viajero(
                            c.get(0),
                            c.get(1),
                            Integer.parseInt(c.get(2)),
                            c.get(3),
                            TipoAbono.valueOf(c.get(4)),
                            Integer.parseInt(c.get(5))
                    );
                })
                .peek(v -> log.debug("Viajero cargado: {}", v.getDni()))
                .collect(Collectors.toList());
    }


    public static List<Viaje> cargarViajes(String ruta, List<Viajero> viajeros) throws IOException {
        log.info("Cargando viajes desde {}", ruta);

        return Files.lines(Path.of(ruta))
                .skip(1) // ignorar cabecera
                .map(linea -> {
                    List<String> c = List.of(linea.split(","));

                    //Buscamos el Viajero con el dni que viene en el fichero
                    Viajero viajero = viajeros.stream()
                            .filter(v -> v.getDni().equals(c.get(1)))
                                    .findFirst()
                                    .orElseThrow(() -> new RuntimeException("Viajero no encontrado: " + c.get(1)));


                    return new Viaje(
                            Long.parseLong(c.get(0)),
                            viajero,
                            c.get(2),
                            c.get(3),
                            c.get(4),
                            LocalDate.parse(c.get(5)),
                            LocalTime.parse(c.get(6)),
                            Integer.parseInt(c.get(7)),
                            Double.parseDouble(c.get(8)),
                            Boolean.parseBoolean(c.get(9))
                    );

                })
                .peek(v -> log.debug("Viaje cargado: {}", v.getId()))
                .collect(Collectors.toList());
    }
}
