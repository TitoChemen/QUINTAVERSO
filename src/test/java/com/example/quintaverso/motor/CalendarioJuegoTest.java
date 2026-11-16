package com.example.quintaverso.motor;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CalendarioJuegoTest {
    @Test
    void pasaDelDiaUnoAlDiaDosDespuesDeTresTurnos() {
        EstadoPartida estado = new EstadoPartida("caceres");
        CalendarioJuego calendario = new CalendarioJuego(estado);
        assertEquals(1, calendario.getDia());
        assertEquals(CalendarioJuego.Turno.MANANA, calendario.getTurno());
        calendario.avanzarTurno();
        assertEquals(CalendarioJuego.Turno.TARDE, calendario.getTurno());
        calendario.avanzarTurno();
        assertEquals(CalendarioJuego.Turno.NOCHE, calendario.getTurno());
        calendario.avanzarTurno();
        assertEquals(2, calendario.getDia());
        assertEquals(CalendarioJuego.Turno.MANANA, calendario.getTurno());
    }

    @Test
    void dormirPasaAlSiguienteDiaInclusoDesdeLaManana() {
        CalendarioJuego calendario = new CalendarioJuego(new EstadoPartida("caceres"));
        calendario.dormirHastaManana();
        assertEquals(2, calendario.getDia());
        assertEquals(CalendarioJuego.Turno.MANANA, calendario.getTurno());
    }

    @Test
    void laCampanaTerminaDespuesDe90Actividades() {
        CalendarioJuego calendario = new CalendarioJuego(new EstadoPartida("caceres"));
        for (int i = 0; i < 89; i++) calendario.avanzarTurno();
        assertFalse(calendario.campañaTerminada());
        calendario.avanzarTurno();
        assertTrue(calendario.campañaTerminada());
        assertEquals(31, calendario.getDia());
    }
}
