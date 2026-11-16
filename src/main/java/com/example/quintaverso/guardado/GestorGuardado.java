package com.example.quintaverso.guardado;

import com.example.quintaverso.motor.EstadoPartida;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/** Guarda cada partida fuera del proyecto, en el HOME de Ubuntu/Windows. */
public final class GestorGuardado {
    private final ObjectMapper mapper = new ObjectMapper();
    private final Path carpeta = Path.of(System.getProperty("user.home"),
            ".quintaverso", "partidas");

    public Path ruta(String temporada) {
        if (!temporada.matches("[a-zA-Z0-9_-]+")) {
            throw new IllegalArgumentException("Temporada inválida");
        }
        return carpeta.resolve(temporada + "-v05.json");
    }

    public boolean existe(String temporada) {
        return Files.isRegularFile(ruta(temporada));
    }

    public void guardar(EstadoPartida estado) throws IOException {
        Files.createDirectories(carpeta);
        Path destino = ruta(estado.getTemporada());
        Path temporal = Files.createTempFile(carpeta, "partida-", ".tmp");
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(temporal.toFile(), estado);
            try {
                Files.move(temporal, destino,
                        StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE);
            } catch (java.nio.file.AtomicMoveNotSupportedException e) {
                Files.move(temporal, destino, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(temporal);
        }
    }

    public EstadoPartida cargar(String temporada) throws IOException {
        EstadoPartida estado = mapper.readValue(ruta(temporada).toFile(), EstadoPartida.class);
        if (estado.getVersionGuardado() != EstadoPartida.VERSION_GUARDADO) {
            throw new IOException("Guardado de otra versión. Se esperaba v" +
                    EstadoPartida.VERSION_GUARDADO);
        }
        if (!temporada.equals(estado.getTemporada())) {
            throw new IOException("El guardado corresponde a otra temporada");
        }
        return estado;
    }
}
