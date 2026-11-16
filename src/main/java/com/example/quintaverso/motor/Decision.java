package com.example.quintaverso.motor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Elección del jugador: efecto, recuerdo y próximo nodo. */
public class Decision {
    private String texto;
    private String siguiente;
    private Map<String, Integer> efectos = new LinkedHashMap<>();
    private List<String> eventos = new ArrayList<>();
    private List<String> desactivarEventos = new ArrayList<>();
    private List<Condicion> condiciones = new ArrayList<>();
    // Respuesta escrita para la decisión (opcional); si falta, se usa una reacción contextual.
    private List<Dialogo> reaccion = new ArrayList<>();

    public Decision() { }

    public boolean disponible(EstadoPartida estado) {
        return condiciones == null || condiciones.stream().allMatch(c -> c.cumple(estado));
    }

    public void aplicar(EstadoPartida estado) {
        if (efectos != null) efectos.forEach(estado::modificarEstadistica);
        if (desactivarEventos != null) desactivarEventos.forEach(estado::desactivarEvento);
        if (eventos != null) eventos.forEach(estado::registrarEvento);
    }

    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }
    public String getSiguiente() { return siguiente; }
    public void setSiguiente(String siguiente) { this.siguiente = siguiente; }
    public Map<String, Integer> getEfectos() { return efectos; }
    public void setEfectos(Map<String, Integer> efectos) { this.efectos = efectos; }
    public List<String> getEventos() { return eventos; }
    public void setEventos(List<String> eventos) { this.eventos = eventos; }
    public List<String> getDesactivarEventos() { return desactivarEventos; }
    public void setDesactivarEventos(List<String> desactivarEventos) { this.desactivarEventos = desactivarEventos; }
    public List<Dialogo> getReaccion() { return reaccion; }
    public void setReaccion(List<Dialogo> reaccion) { this.reaccion = reaccion; }
    public List<Condicion> getCondiciones() { return condiciones; }
    public void setCondiciones(List<Condicion> condiciones) { this.condiciones = condiciones; }
}
