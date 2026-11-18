package com.example.quintaverso.motor;

/** Una intervención del narrador o de un personaje, cargada desde JSON. */
public class Dialogo {
    private String personaje;
    private String texto;
    // Expresión explícita opcional: se prefiere al análisis automático del diálogo.
    private String expresion;
    private java.util.List<Condicion> condiciones = new java.util.ArrayList<>();

    public Dialogo() { }
    public Dialogo(String personaje, String texto) {
        this.personaje = personaje;
        this.texto = texto;
    }
    public String getPersonaje() { return personaje; }
    public void setPersonaje(String personaje) { this.personaje = personaje; }
    public boolean disponible(EstadoPartida estado) {
        return condiciones == null || condiciones.stream().allMatch(c -> c.cumple(estado));
    }
    public java.util.List<Condicion> getCondiciones() { return condiciones; }
    public void setCondiciones(java.util.List<Condicion> condiciones) { this.condiciones = condiciones; }
    public String getExpresion() { return expresion; }
    public void setExpresion(String expresion) { this.expresion = expresion; }
    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }
}
