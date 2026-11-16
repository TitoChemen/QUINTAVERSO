package com.example.quintaverso.motor;

import java.util.ArrayList;
import java.util.List;

/** Escena definida en JSON; puede continuar, ramificarse o terminar. */
public class Escena {
    private String id;
    private String titulo;
    private String lugar;
    private List<Dialogo> dialogos = new ArrayList<>();
    private List<Decision> decisiones = new ArrayList<>();
    private String siguiente;
    private String direccionVisual;
    private String promptImagen;
    /** Archivo de imagen relativo a /imagenes/fondos/ (opcional). */
    private String fondo;
    /** Archivo de imagen relativo a /imagenes/cg/ (solo desenlaces). */
    private String cg;

    public Escena() { }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getLugar() { return lugar; }
    public void setLugar(String lugar) { this.lugar = lugar; }
    public List<Dialogo> getDialogos() { return dialogos; }
    public void setDialogos(List<Dialogo> dialogos) { this.dialogos = dialogos; }
    public List<Decision> getDecisiones() { return decisiones; }
    public void setDecisiones(List<Decision> decisiones) { this.decisiones = decisiones; }
    public String getDireccionVisual() { return direccionVisual; }
    public void setDireccionVisual(String direccionVisual) { this.direccionVisual = direccionVisual; }
    public String getPromptImagen() { return promptImagen; }
    public void setPromptImagen(String promptImagen) { this.promptImagen = promptImagen; }
    public String getFondo() { return fondo; }
    public void setFondo(String fondo) { this.fondo = fondo; }
    public String getCg() { return cg; }
    public void setCg(String cg) { this.cg = cg; }
    public String getSiguiente() { return siguiente; }
    public void setSiguiente(String siguiente) { this.siguiente = siguiente; }
}
