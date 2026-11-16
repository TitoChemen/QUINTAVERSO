package com.example.quintaverso.motor;

import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class ReaccionesNarrativasTest {
    @Test
    void usaDialogosEspecificosCuandoExisten() {
        Escena escena = new Escena();
        escena.setId("evento_lumi_identidad");
        Decision d = new Decision();
        d.setReaccion(List.of(new Dialogo("Lumi", "Respuesta escrita a mano")));
        assertEquals("Respuesta escrita a mano", ReaccionesNarrativas.construir(escena, d).get(0).getTexto());
    }

    @Test
    void generaReaccionDePersonajeCuandoNoFueEscrita() {
        Escena escena = new Escena();
        escena.setId("cony_12");
        Decision d = new Decision();
        d.setEfectos(Map.of("confianza_cony", 5));
        List<Dialogo> lineas = ReaccionesNarrativas.construir(escena, d);
        assertFalse(lineas.isEmpty());
        assertEquals("Cony", lineas.get(0).getPersonaje());
        assertFalse(lineas.get(0).getTexto().isBlank());
    }
}
