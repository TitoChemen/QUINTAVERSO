package com.example.quintaverso.motor;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Carga historias JSON, comprueba IDs y enlaces, resuelve finales con condiciones explícitas. */
public final class GestorHistoria {
    private final Map<String, Escena> escenas = new LinkedHashMap<>();

    public GestorHistoria() { this("caceres"); }

    public GestorHistoria(String temporada) {
        if (!List.of("caceres", "seba").contains(temporada))
            throw new IllegalArgumentException("Temporada sin historia: " + temporada);
        ObjectMapper mapper = new ObjectMapper();
        List<String> archivos = "seba".equals(temporada)
                ? List.of("prologo", "hitos", "kelly", "amira", "javi", "fena", "amigos", "discord_noches", "momentos", "continuidad", "estilo", "finales")
                : List.of("prologo", "lumi", "cony", "amigos", "otros", "eventos", "finales");
        for (String archivo : archivos) {
            String ruta = "/historias/" + temporada + "/" + archivo + ".json";
            try (InputStream entrada = getClass().getResourceAsStream(ruta)) {
                if (entrada == null) throw new IOException("Archivo ausente: " + ruta);
                List<Escena> lista = mapper.readValue(entrada, new TypeReference<>(){});
                for (Escena esc : lista) {
                    if (esc.getId() == null || esc.getId().isBlank()) throw new IOException("Escena sin ID: " + ruta);
                    if (escenas.putIfAbsent(esc.getId(), esc) != null) throw new IOException("ID duplicado: " + esc.getId());
                }
            } catch (IOException ex) {
                throw new IllegalStateException("Error al cargar " + ruta + ": " + ex.getMessage(), ex);
            }
        }
        for (Escena esc : escenas.values()) {
            validarDestino(esc.getId(), esc.getSiguiente());
            if (esc.getDecisiones() != null) for (Decision d : esc.getDecisiones())
                validarDestino(esc.getId(), d.getSiguiente());
        }
    }

    private void validarDestino(String origen, String destino) {
        if (destino != null && !escenas.containsKey(destino))
            throw new IllegalStateException("La escena '" + origen + "' apunta a '" + destino + "', que no existe");
    }

    public Escena obtener(String id) {
        Escena e = escenas.get(id);
        if (e == null) throw new IllegalArgumentException("Escena no encontrada: " + id);
        return e;
    }
    public int cantidadEscenas() { return escenas.size(); }

    /** Las decisiones narrativas son imprescindibles; las estadísticas no crean parejas de la nada. */
    public String calcularFinal(EstadoPartida e) {
        if ("seba".equals(e.getTemporada())) return FinalesSeba.calcular(e);
        int l = e.obtenerEstadistica("lumi");
        int c = e.obtenerEstadistica("cony");
        int h = e.obtenerEstadistica("honestidad");
        int s = e.obtenerEstadistica("sospecha");
        int cl = e.obtenerEstadistica("confianza_lumi");
        int cc = e.obtenerEstadistica("confianza_cony");
        if (e.ocurrioEvento("mentira_grave") || (s >= 80 && h <= 30)) return "final_descubierto";
        if (e.ocurrioEvento("acuerdo_lumi") && e.ocurrioEvento("acuerdo_cony") &&
                e.ocurrioEvento("acuerdo_conversado") && e.ocurrioEvento("max_ruptura") &&
                h >= 70 && cl >= 50 && cc >= 50) return "final_acuerdos";
        if (e.ocurrioEvento("lumi_exclusiva") && e.ocurrioEvento("max_ruptura") &&
                e.ocurrioEvento("lumi_romance") && l >= 55 && cl >= 40) return "final_lumi";
        if (e.ocurrioEvento("cony_pareja") && !e.ocurrioEvento("lumi_romance") && c >= 55 && cc >= 45)
            return "final_cony_pareja";
        if (e.ocurrioEvento("cony_casual_acordado") &&
                e.ocurrioEvento("cony_limites_revisados") && !e.ocurrioEvento("lumi_romance") &&
                !e.ocurrioEvento("cony_pareja") && c >= 45 && cc >= 30) return "final_cony_casual";
        if (e.obtenerEstadistica("amistad") >= 70 && e.obtenerEstadistica("claridad") >= 75 &&
                e.ocurrioEvento("priorizo_crecer")) return "final_crecimiento";
        if (e.ocurrioEvento("lumi_sigue_max") && c < 40) return "final_lumi_max";
        return "final_calvo";
    }
}
