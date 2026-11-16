package com.example.quintaverso.motor;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GestorHistoriaTest {
    @Test
    void escenasYEnlacesSonValidos() {
        GestorHistoria h = new GestorHistoria();
        assertTrue(h.cantidadEscenas() >= 200);
        assertEquals("prologo_inicio", h.obtener("prologo_inicio").getId());
        assertEquals("final_lumi", h.obtener("final_lumi").getId());
        assertNotNull(h.obtener("cony_24").getPromptImagen());
    }

    @Test
    void finalDeAcuerdosNecesitaAceptacionDeAmbasYRupturaConMax() {
        GestorHistoria h = new GestorHistoria();
        EstadoPartida e = new EstadoPartida();
        for (String k : new String[]{"honestidad","confianza_lumi","confianza_cony"})
            e.modificarEstadistica(k,100);
        assertNotEquals("final_acuerdos", h.calcularFinal(e));
        e.registrarEvento("acuerdo_conversado");
        e.registrarEvento("acuerdo_lumi");
        assertNotEquals("final_acuerdos", h.calcularFinal(e));
        e.registrarEvento("acuerdo_cony");
        e.registrarEvento("max_ruptura");
        assertEquals("final_acuerdos", h.calcularFinal(e));
    }

    @Test
    void diferenciaLoCasualDeUnaPareja() {
        GestorHistoria h = new GestorHistoria();
        EstadoPartida e = new EstadoPartida();
        e.modificarEstadistica("cony",75);
        e.modificarEstadistica("confianza_cony",75);
        e.registrarEvento("cony_casual_acordado");
        e.registrarEvento("cony_limites_revisados");
        assertEquals("final_cony_casual",h.calcularFinal(e));
        e.registrarEvento("cony_pareja");
        e.desactivarEvento("cony_casual_acordado");
        assertEquals("final_cony_pareja",h.calcularFinal(e));
    }

    @Test
    void unaMentiraGravePuedeCerrarLasRutas() {
        GestorHistoria h = new GestorHistoria();
        EstadoPartida e = new EstadoPartida();
        e.registrarEvento("mentira_grave");
        assertEquals("final_descubierto", h.calcularFinal(e));
    }
}
