package com.example.quintaverso.guardado;

import com.example.quintaverso.motor.EstadoPartida;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/** Autoguardado separado de las 3 ranuras manuales. Nunca sobrescribe el guardado v0.5. */
public final class GestorGuardado {
    private final ObjectMapper mapper = new ObjectMapper();
    private final Path carpeta;
    public static final int RANURAS = 3;
    private static final DateTimeFormatter FECHA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm").withZone(ZoneId.systemDefault());

    public GestorGuardado() {
        this(Path.of(System.getProperty("user.home"), ".quintaverso", "partidas"));
    }

    /** Permite ejecutar pruebas sin tocar las partidas reales. */
    public GestorGuardado(Path carpeta) { this.carpeta = carpeta; }

    private static void validar(String temporada) {
        if (temporada == null || !temporada.matches("[a-zA-Z0-9_-]+")) {
            throw new IllegalArgumentException("Temporada inválida");
        }
    }
    private static void validarRanura(int ranura) {
        if (ranura < 1 || ranura > RANURAS) {
            throw new IllegalArgumentException("La ranura debe estar entre 1 y " + RANURAS);
        }
    }

    /** Ruta del autoguardado v0.6. */
    public Path ruta(String temporada) {
        validar(temporada);
        return carpeta.resolve(temporada + "-v06-auto.json");
    }
    public Path rutaRanura(String temporada, int ranura) {
        validar(temporada);
        validarRanura(ranura);
        return carpeta.resolve(temporada + "-v06-slot-" + ranura + ".json");
    }
    public Path rutaAnterior(String temporada) {
        validar(temporada);
        return carpeta.resolve(temporada + "-v05.json");
    }
    public boolean existe(String temporada) { return Files.isRegularFile(ruta(temporada)); }
    public boolean existeAnterior(String temporada) { return Files.isRegularFile(rutaAnterior(temporada)); }
    public boolean existeRanura(String temporada, int ranura) {
        return Files.isRegularFile(rutaRanura(temporada, ranura));
    }
    public boolean hayRanuras(String temporada) {
        for (int n = 1; n <= RANURAS; n++) if (existeRanura(temporada, n)) return true;
        return false;
    }

    public void guardar(EstadoPartida estado) throws IOException { escribir(ruta(estado.getTemporada()), estado); }
    public void guardarRanura(EstadoPartida estado, int ranura) throws IOException {
        escribir(rutaRanura(estado.getTemporada(), ranura), estado);
    }

    private void escribir(Path destino, EstadoPartida estado) throws IOException {
        Files.createDirectories(carpeta);
        Path temporal = Files.createTempFile(carpeta, "partida-", ".tmp");
        try {
            estado.setVersionGuardado(EstadoPartida.VERSION_GUARDADO);
            mapper.writerWithDefaultPrettyPrinter().writeValue(temporal.toFile(), estado);
            try {
                Files.move(temporal, destino, StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE);
            } catch (java.nio.file.AtomicMoveNotSupportedException e) {
                Files.move(temporal, destino, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(temporal);
        }
    }

    public EstadoPartida cargar(String temporada) throws IOException { return leer(ruta(temporada), temporada); }
    public EstadoPartida cargarRanura(String temporada, int ranura) throws IOException {
        return leer(rutaRanura(temporada, ranura), temporada);
    }
    /** Permite importar la partida antigua sin modificar el archivo v0.5. */
    public EstadoPartida cargarAnterior(String temporada) throws IOException {
        return leer(rutaAnterior(temporada), temporada);
    }
    private EstadoPartida leer(Path origen, String temporada) throws IOException {
        EstadoPartida estado = mapper.readValue(origen.toFile(), EstadoPartida.class);
        if (estado.getVersionGuardado() != 2 && estado.getVersionGuardado() != 3 &&
                estado.getVersionGuardado() != EstadoPartida.VERSION_GUARDADO) {
            throw new IOException("Formato de guardado no compatible: v" + estado.getVersionGuardado());
        }
        if (!temporada.equals(estado.getTemporada())) {
            throw new IOException("El guardado corresponde a otra temporada");
        }
        // Migración de la continuidad de Amira desde v0.8: sólo cuando está
        // inequívocamente registrado que hubo infidelidad, mentira y NO confesión.
        // Una partida terminada se mantiene intacta para no alterar su final visto.
        if (estado.getVersionGuardado() < 4 && "seba".equals(temporada)
                && estado.getFinalObtenido() == null && estado.ocurrioEvento("seba_infidelidad")
                && estado.ocurrioEvento("seba_mentira") && !estado.ocurrioEvento("seba_confesion")) {
            estado.registrarEvento("seba_amira_delato_grupo");
            estado.registrarEvento("seba_amira_distanciada");
            estado.registrarEvento("seba_kelly_termino");
            estado.desactivarEvento("seba_pareja_kelly");
            estado.desactivarEvento("seba_amira_pareja");
            estado.desactivarEvento("seba_amira_vinculo");
            estado.desactivarEvento("seba_amira_confesion_valida");
            estado.agregarHistorial("v0.9 · Ruta de infidelidad oculta corregida: Amira dejó de participar en la mentira.");
        }
        estado.setVersionGuardado(EstadoPartida.VERSION_GUARDADO);
        return estado;
    }

    public String descripcion(String temporada, int ranura) {
        Path archivo = rutaRanura(temporada, ranura);
        if (!Files.isRegularFile(archivo)) return "Ranura " + ranura + " — vacía";
        try {
            EstadoPartida estado = leer(archivo, temporada);
            Instant modificado = Files.getLastModifiedTime(archivo).toInstant();
            String lugar = estado.getEscenaActual() == null ? "Calendario" : "En una escena";
            return "Ranura " + ranura + " — Día " + estado.getDia() + " · " + lugar +
                    " · " + FECHA.format(modificado);
        } catch (IOException ex) {
            return "Ranura " + ranura + " — archivo dañado (no sobrescribir sin revisar)";
        }
    }
}
