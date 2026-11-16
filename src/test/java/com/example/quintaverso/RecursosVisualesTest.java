package com.example.quintaverso;

import com.example.quintaverso.motor.Dialogo;
import com.example.quintaverso.motor.Escena;
import com.example.quintaverso.motor.GestorHistoria;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RecursosVisualesTest {
    @Test
    void finalesTienenSusCGYFondos() {
        GestorHistoria historia = new GestorHistoria();
        for (String id : List.of("final_lumi", "final_cony_pareja", "final_cony_casual",
                "final_acuerdos", "final_descubierto", "final_crecimiento",
                "final_lumi_max", "final_calvo")) {
            Escena e = historia.obtener(id);
            assertNotNull(RecursosVisuales.cg(e), id);
            assertNotNull(getClass().getResource(RecursosVisuales.cg(e)), id);
            assertNotNull(getClass().getResource(RecursosVisuales.fondo(e)), id);
        }
    }

    @Test
    void retratosDisponiblesRespetanNombres() {
        for (String nombre : List.of("Lumi", "Cony", "Cáceres", "Jairo", "Seba")) {
            String path = RecursosVisuales.retrato(new Dialogo(nombre, "Hola"));
            assertNotNull(path, nombre);
            assertNotNull(getClass().getResource(path), nombre);
        }
        assertEquals(
                "/imagenes/personajes/kevin/neutral.png",
                RecursosVisuales.retrato(new Dialogo("Kevin", ""))
        );
    }
}
