package com.example.quintaverso.motor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Estado persistente: decisiones, estadísticas, historial y actividades. */
public class EstadoPartida {
    public static final int VERSION_GUARDADO = 4;
    private int versionGuardado = VERSION_GUARDADO;
    private String temporada = "caceres";
    private int dia = 1;
    private CalendarioJuego.Turno turno = CalendarioJuego.Turno.MANANA;
    private Map<String, Integer> estadisticas = new LinkedHashMap<>();
    private Map<String, Boolean> eventos = new LinkedHashMap<>();
    private Map<String, Integer> repeticiones = new LinkedHashMap<>();
    private List<String> historial = new ArrayList<>();
    private String finalObtenido;

    // v0.6: punto exacto de reanudación, incluso en mitad de una escena.
    private String escenaActual;
    private int lineaActual;
    private String contextoEscena;
    private String accionPendienteId;
    private String accionPendienteTitulo;
    private boolean accionPendienteUnica;
    private List<Dialogo> reaccionPendiente = new ArrayList<>();
    private int lineaReaccion;
    private String destinoPendiente;
    private boolean terminarTrasReaccion;

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
        if ("jairo".equals(temporada)) {
            estadisticas.clear();
            estadisticas.putAll(Map.ofEntries(Map.entry("confianza_mary",65), Map.entry("inseguridad",25),
                Map.entry("honestidad",55), Map.entry("madurez",30), Map.entry("valentia",25), Map.entry("amistad",45),
                Map.entry("ale",5), Map.entry("andrea",5), Map.entry("camila",0), Map.entry("anto",0)));
            registrarEvento("j_pareja");
        }
        if ("seba".equals(temporada)) {
            estadisticas.clear();
            estadisticas.put("kelly", 55);
            estadisticas.put("confianza_kelly", 55);
            estadisticas.put("amira", 40);
            estadisticas.put("javi", 0);
            estadisticas.put("fena", 0);
            estadisticas.put("honestidad", 50);
            estadisticas.put("sospecha", 0);
            estadisticas.put("amistad", 45);
            estadisticas.put("autoestima", 35);
            estadisticas.put("estilo", 0);
            registrarEvento("seba_pareja_kelly");
        }
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
    public String getEscenaActual() { return escenaActual; }
    public void setEscenaActual(String escenaActual) { this.escenaActual = escenaActual; }
    public int getLineaActual() { return lineaActual; }
    public void setLineaActual(int lineaActual) { this.lineaActual = Math.max(0, lineaActual); }
    public String getContextoEscena() { return contextoEscena; }
    public void setContextoEscena(String contextoEscena) { this.contextoEscena = contextoEscena; }
    public String getAccionPendienteId() { return accionPendienteId; }
    public void setAccionPendienteId(String accionPendienteId) { this.accionPendienteId = accionPendienteId; }
    public String getAccionPendienteTitulo() { return accionPendienteTitulo; }
    public void setAccionPendienteTitulo(String accionPendienteTitulo) { this.accionPendienteTitulo = accionPendienteTitulo; }
    public boolean isAccionPendienteUnica() { return accionPendienteUnica; }
    public void setAccionPendienteUnica(boolean accionPendienteUnica) { this.accionPendienteUnica = accionPendienteUnica; }
    public List<Dialogo> getReaccionPendiente() { return reaccionPendiente; }
    public void setReaccionPendiente(List<Dialogo> reaccionPendiente) {
        this.reaccionPendiente = reaccionPendiente == null ? new ArrayList<>() : reaccionPendiente;
    }
    public int getLineaReaccion() { return lineaReaccion; }
    public void setLineaReaccion(int lineaReaccion) { this.lineaReaccion = Math.max(0, lineaReaccion); }
    public String getDestinoPendiente() { return destinoPendiente; }
    public void setDestinoPendiente(String destinoPendiente) { this.destinoPendiente = destinoPendiente; }
    public boolean isTerminarTrasReaccion() { return terminarTrasReaccion; }
    public void setTerminarTrasReaccion(boolean terminarTrasReaccion) { this.terminarTrasReaccion = terminarTrasReaccion; }

    public void limpiarReaccion() {
        reaccionPendiente = new ArrayList<>();
        lineaReaccion = 0;
        destinoPendiente = null;
        terminarTrasReaccion = false;
    }
    public void limpiarEscena() {
        escenaActual = null;
        lineaActual = 0;
        contextoEscena = null;
        accionPendienteId = null;
        accionPendienteTitulo = null;
        accionPendienteUnica = false;
        limpiarReaccion();
    }

    public String getFinalObtenido() { return finalObtenido; }
    public void setFinalObtenido(String finalObtenido) { this.finalObtenido = finalObtenido; }
}
