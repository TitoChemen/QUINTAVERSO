package com.example.quintaverso.motor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Estado persistente: decisiones, estadísticas, historial y actividades. */
public class EstadoPartida {
    public static final int VERSION_GUARDADO = 2;
    private int versionGuardado = VERSION_GUARDADO;
    private String temporada = "caceres";
    private int dia = 1;
    private CalendarioJuego.Turno turno = CalendarioJuego.Turno.MANANA;
    private Map<String, Integer> estadisticas = new LinkedHashMap<>();
    private Map<String, Boolean> eventos = new LinkedHashMap<>();
    private Map<String, Integer> repeticiones = new LinkedHashMap<>();
    private List<String> historial = new ArrayList<>();
    private String finalObtenido;

    public EstadoPartida() {
        estadisticas.put("lumi", 15);
        estadisticas.put("cony", 15);
        estadisticas.put("confianza_lumi", 15);
        estadisticas.put("confianza_cony", 15);
        estadisticas.put("honestidad", 50);
        estadisticas.put("sospecha", 0);
        estadisticas.put("amistad", 35);
        estadisticas.put("claridad", 35);
    }

    public EstadoPartida(String temporada) {
        this();
        this.temporada = temporada;
    }

    public int obtenerEstadistica(String clave) { return estadisticas.getOrDefault(clave, 0); }
    public void modificarEstadistica(String clave, int delta) {
        int actual = obtenerEstadistica(clave);
        estadisticas.put(clave, Math.max(0, Math.min(100, actual + delta)));
    }
    public void registrarEvento(String id) { eventos.put(id, true); }
    public void desactivarEvento(String id) { eventos.remove(id); }
    public boolean ocurrioEvento(String id) { return eventos.getOrDefault(id, false); }
    public int contarActividad(String id) { return repeticiones.getOrDefault(id, 0); }
    public void registrarActividad(String id) { repeticiones.merge(id, 1, Integer::sum); }
    public void agregarHistorial(String texto) {
        historial.add(texto);
        while (historial.size() > 250) historial.remove(0);
    }

    public int getVersionGuardado() { return versionGuardado; }
    public void setVersionGuardado(int versionGuardado) { this.versionGuardado = versionGuardado; }
    public String getTemporada() { return temporada; }
    public void setTemporada(String temporada) { this.temporada = temporada; }
    public int getDia() { return dia; }
    public void setDia(int dia) { this.dia = Math.max(1, dia); }
    public CalendarioJuego.Turno getTurno() { return turno; }
    public void setTurno(CalendarioJuego.Turno turno) { this.turno = turno == null ? CalendarioJuego.Turno.MANANA : turno; }
    public Map<String, Integer> getEstadisticas() { return estadisticas; }
    public void setEstadisticas(Map<String, Integer> estadisticas) { this.estadisticas = estadisticas == null ? new LinkedHashMap<>() : estadisticas; }
    public Map<String, Boolean> getEventos() { return eventos; }
    public void setEventos(Map<String, Boolean> eventos) { this.eventos = eventos == null ? new LinkedHashMap<>() : eventos; }
    public Map<String, Integer> getRepeticiones() { return repeticiones; }
    public void setRepeticiones(Map<String, Integer> repeticiones) { this.repeticiones = repeticiones == null ? new LinkedHashMap<>() : repeticiones; }
    public List<String> getHistorial() { return historial; }
    public void setHistorial(List<String> historial) { this.historial = historial == null ? new ArrayList<>() : historial; }
    public String getFinalObtenido() { return finalObtenido; }
    public void setFinalObtenido(String finalObtenido) { this.finalObtenido = finalObtenido; }
}
