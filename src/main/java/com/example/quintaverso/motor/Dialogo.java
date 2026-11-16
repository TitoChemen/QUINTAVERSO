package com.example.quintaverso.motor;

/** Una intervención del narrador o de un personaje, cargada desde JSON. */
public class Dialogo {
    private String personaje;
    private String texto;

    public Dialogo() { }
    public Dialogo(String personaje, String texto) {
        this.personaje = personaje;
        this.texto = texto;
    }
    public String getPersonaje() { return personaje; }
    public void setPersonaje(String personaje) { this.personaje = personaje; }
    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }
}
