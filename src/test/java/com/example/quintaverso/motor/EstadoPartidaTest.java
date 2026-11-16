package com.example.quintaverso.motor;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EstadoPartidaTest {
    @Test
    void estadisticasSeLimitanA0y100() {
        EstadoPartida e = new EstadoPartida();
        e.modificarEstadistica("lumi", 999);
        assertEquals(100, e.obtenerEstadistica("lumi"));
        e.modificarEstadistica("lumi", -999);
        assertEquals(0, e.obtenerEstadistica("lumi"));
    }

    @Test
    void decisionesPuedenActivarRecuerdos() {
        Decision d = new Decision();
        d.setEventos(java.util.List.of("promesa_serio"));
        d.setEfectos(java.util.Map.of("honestidad", 20));
        EstadoPartida e = new EstadoPartida();
        d.aplicar(e);
        assertTrue(e.ocurrioEvento("promesa_serio"));
        assertEquals(70, e.obtenerEstadistica("honestidad"));
    }

    @Test
    void desactivarEventosEvitaFinalesContradictorios() {
        EstadoPartida e = new EstadoPartida();
        e.registrarEvento("cony_casual_acordado");
        Decision d = new Decision();
        d.setDesactivarEventos(java.util.List.of("cony_casual_acordado"));
        d.setEventos(java.util.List.of("cony_pareja"));
        d.aplicar(e);
        assertFalse(e.ocurrioEvento("cony_casual_acordado"));
        assertTrue(e.ocurrioEvento("cony_pareja"));
    }

    @Test
    void contadoresPersistenProgresionDeConversaciones() {
        EstadoPartida e = new EstadoPartida();
        assertEquals(0,e.contarActividad("lumi"));
        e.registrarActividad("lumi");
        assertEquals(1,e.contarActividad("lumi"));
    }

    @Test
    void condicionesRevisanVariablesYEventos() {
        EstadoPartida e = new EstadoPartida();
        Condicion c = new Condicion();
        c.setEstadistica("honestidad");
        c.setMinimo(60);
        assertFalse(c.cumple(e));
        e.modificarEstadistica("honestidad", 15);
        assertTrue(c.cumple(e));
        c.setEvento("ayudo");
        assertFalse(c.cumple(e));
        e.registrarEvento("ayudo");
        assertTrue(c.cumple(e));
    }
}
