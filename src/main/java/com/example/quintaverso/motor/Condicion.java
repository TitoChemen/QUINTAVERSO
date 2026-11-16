package com.example.quintaverso.motor;

/** Restricciones combinables (AND) para mostrar una decisión. */
public class Condicion {
    private String estadistica;
    private Integer minimo;
    private Integer maximo;
    private String evento;
    private Boolean eventoActivo;
    private Integer diaDesde;
    private Integer diaHasta;
    private CalendarioJuego.Turno turno;

    public Condicion() { }

    public boolean cumple(EstadoPartida estado) {
        if (estadistica != null) {
            int valor = estado.obtenerEstadistica(estadistica);
            if (minimo != null && valor < minimo) return false;
            if (maximo != null && valor > maximo) return false;
        }
        if (evento != null && estado.ocurrioEvento(evento) !=
                (eventoActivo == null || eventoActivo)) return false;
        if (diaDesde != null && estado.getDia() < diaDesde) return false;
        if (diaHasta != null && estado.getDia() > diaHasta) return false;
        if (turno != null && estado.getTurno() != turno) return false;
        return true;
    }

    public String getEstadistica() { return estadistica; }
    public void setEstadistica(String estadistica) { this.estadistica = estadistica; }
    public Integer getMinimo() { return minimo; }
    public void setMinimo(Integer minimo) { this.minimo = minimo; }
    public Integer getMaximo() { return maximo; }
    public void setMaximo(Integer maximo) { this.maximo = maximo; }
    public String getEvento() { return evento; }
    public void setEvento(String evento) { this.evento = evento; }
    public Boolean getEventoActivo() { return eventoActivo; }
    public void setEventoActivo(Boolean eventoActivo) { this.eventoActivo = eventoActivo; }
    public Integer getDiaDesde() { return diaDesde; }
    public void setDiaDesde(Integer diaDesde) { this.diaDesde = diaDesde; }
    public Integer getDiaHasta() { return diaHasta; }
    public void setDiaHasta(Integer diaHasta) { this.diaHasta = diaHasta; }
    public CalendarioJuego.Turno getTurno() { return turno; }
    public void setTurno(CalendarioJuego.Turno turno) { this.turno = turno; }
}
