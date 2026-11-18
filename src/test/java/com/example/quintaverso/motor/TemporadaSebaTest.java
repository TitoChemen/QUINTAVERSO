package com.example.quintaverso.motor;

import org.junit.jupiter.api.Test;
import java.util.HashSet;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

/** Regresiones de la temporada Seba: historia, rutas, hitos y finales. */
class TemporadaSebaTest {
    private EstadoPartida seba(String... eventos) {
        EstadoPartida e = new EstadoPartida("seba");
        for (String nombre : eventos) e.registrarEvento(nombre);
        return e;
    }

    @Test void cargaEscenasYEnlacesDeSebaSinAfectarCaceres() {
        GestorHistoria seba = new GestorHistoria("seba");
        assertTrue(seba.cantidadEscenas() >= 81);
        assertEquals("seba_hito_fiesta", seba.obtener("seba_hito_fiesta").getId());
        assertEquals(4, seba.obtener("seba_hito_fiesta").getDecisiones().size());
        assertNotNull(seba.obtener("final_seba_femboy"));
        assertNotNull(new GestorHistoria("caceres").obtener("final_lumi"));
    }

    @Test void todosLosDesenlacesSeDistinguenPorHechos() {
        GestorHistoria historia = new GestorHistoria("seba");
        Set<String> resultados = new HashSet<>();
        resultados.add(historia.calcularFinal(seba("seba_fiesta_fiel", "seba_kelly_compromiso")));
        resultados.add(historia.calcularFinal(seba("seba_reconciliacion_posible", "seba_kelly_reconciliada")));
        resultados.add(historia.calcularFinal(seba("seba_kelly_termino", "seba_mentira")));
        resultados.add(historia.calcularFinal(seba("seba_kelly_termino", "seba_amira_pareja", "seba_amira_confesion_valida")));
        resultados.add(historia.calcularFinal(seba("seba_kelly_termino", "seba_amira_pareja", "seba_infidelidad", "seba_amira_confesion_valida")));
        resultados.add(historia.calcularFinal(seba("seba_kelly_termino", "seba_javi_pareja")));
        resultados.add(historia.calcularFinal(seba("seba_kelly_termino", "seba_fena_pareja")));
        resultados.add(historia.calcularFinal(seba("seba_kelly_termino", "seba_fena_richie_pareja")));
        resultados.add(historia.calcularFinal(seba("seba_kelly_termino", "seba_richie_golpea_seba")));
        resultados.add(historia.calcularFinal(seba("seba_doble_vida")));
        resultados.add(historia.calcularFinal(seba("seba_solo", "seba_kelly_termino")));
        resultados.add(historia.calcularFinal(seba("seba_kelly_sigue_sin_saber", "seba_no_descubierto")));
        EstadoPartida rosa = seba("seba_femboy");
        rosa.modificarEstadistica("estilo", 60);
        resultados.add(historia.calcularFinal(rosa));
        assertEquals(13, resultados.size(), "No debe haber finales accidentalmente equivalentes");
    }

    @Test void codigosDeImagenesSonLosCorrectosParaCadaFinal() {
        GestorHistoria historia = new GestorHistoria("seba");
        assertEquals("/imagenes/cg/seba/final_naranja_sebaxfena.png",
                com.example.quintaverso.RecursosVisuales.cg(historia.obtener("final_seba_naranja")));
        assertEquals("/imagenes/cg/seba/final_naranja_richiexfena.png",
                com.example.quintaverso.RecursosVisuales.cg(historia.obtener("final_seba_naranja_picnic")));
        assertEquals("/imagenes/cg/seba/final_turquesa.png",
                com.example.quintaverso.RecursosVisuales.cg(historia.obtener("final_seba_turquesa")));
        assertEquals("/imagenes/cg/seba/comborodri.png",
                com.example.quintaverso.RecursosVisuales.cg(historia.obtener("seba_fiesta_combo_rodri")));
        assertNotNull(getClass().getResource(com.example.quintaverso.RecursosVisuales.cg(
                historia.obtener("final_seba_naranja_picnic"))));
    }

    @Test void golpearRodrigoAbreConsecuenciasDiferentes() {
        GestorHistoria historia = new GestorHistoria("seba");
        EstadoPartida e = seba("seba_golpeo_rodrigo", "seba_rodri_aviso", "seba_fiesta_cama");
        e.setDia(12);
        e.registrarEvento("seba_hito:tension");
        e.registrarEvento("seba_hito:fiesta");
        assertEquals("seba_post_combo", HitosSeba.pendiente(e).escenaId());
        assertEquals("seba_fiesta_combo_rodri", historia.obtener("seba_fiesta_cama")
                .getDecisiones().get(3).getSiguiente());
    }

    @Test void amiraNoReapareceComoRomanceTrasDelatarLaMentira() {
        EstadoPartida e = seba("seba_infidelidad", "seba_mentira",
                "seba_amira_delato_grupo", "seba_kelly_termino", "seba_amira_pareja");
        e.setDia(21);
        assertFalse(GestorEventosSeba.disponibles(e).stream()
                .anyMatch(a -> a.id().startsWith("seba_amira_")));
        assertEquals("final_seba_gris", FinalesSeba.calcular(e));
        e.registrarEvento("seba_amira_confesion_valida");
        assertEquals("final_seba_gris", FinalesSeba.calcular(e));
    }

    @Test void ramificacionesDeAmiraTienenHitosIncompatibles() {
        EstadoPartida delatado = seba("seba_infidelidad", "seba_amira_delato_grupo",
                "seba_hito:tension", "seba_hito:fiesta", "seba_hito:consecuencias");
        delatado.setDia(20);
        assertEquals("seba_amira_distancia", HitosSeba.pendiente(delatado).escenaId());
        delatado.registrarEvento("seba_hito:amira_distancia");
        assertEquals("seba_amira_cierre", HitosSeba.pendiente(delatado).escenaId());
        EstadoPartida sincero = seba("seba_infidelidad", "seba_confesion",
                "seba_acepta_responsabilidad", "seba_hito:tension", "seba_hito:fiesta",
                "seba_hito:consecuencias");
        sincero.setDia(20);
        assertEquals("seba_amira_reencuentro_honesto", HitosSeba.pendiente(sincero).escenaId());
        sincero.registrarEvento("seba_hito:amira_honesta");
        sincero.registrarEvento("seba_amira_puerta_abierta");
        sincero.registrarEvento("seba_hito:javi");
        sincero.setDia(23);
        assertEquals("seba_amira_confesion_tardia", HitosSeba.pendiente(sincero).escenaId());
    }

    @Test void discordSoloApareceDuranteLaNocheCorrespondiente() {
        EstadoPartida e = seba();
        e.setDia(3);
        e.setTurno(CalendarioJuego.Turno.MANANA);
        assertFalse(GestorEventosSeba.disponibles(e).stream()
                .anyMatch(a -> a.id().equals("seba_discord_03")));
        e.setTurno(CalendarioJuego.Turno.NOCHE);
        assertTrue(GestorEventosSeba.disponibles(e).stream()
                .anyMatch(a -> a.id().equals("seba_discord_03")));
    }

    @Test void finalRosaNoSeObtieneSoloPorTenerPuntos() {
        EstadoPartida e = seba();
        e.modificarEstadistica("estilo", 100);
        assertNotEquals("final_seba_femboy", FinalesSeba.calcular(e));
        e.registrarEvento("seba_femboy");
        assertEquals("final_seba_femboy", FinalesSeba.calcular(e));
    }

    @Test void javiSeHabilitaSoloDespuesDeConocerlaYSinKelly() {
        EstadoPartida e = seba();
        e.setDia(22);
        assertFalse(GestorEventosSeba.disponibles(e).stream().anyMatch(a -> a.id().equals("seba_javi_1")));
        e.registrarEvento("seba_conocio_javi");
        assertFalse(GestorEventosSeba.disponibles(e).stream().anyMatch(a -> a.id().equals("seba_javi_1")));
        e.registrarEvento("seba_kelly_termino");
        assertTrue(GestorEventosSeba.disponibles(e).stream().anyMatch(a -> a.id().equals("seba_javi_1")));
        assertFalse(GestorEventosSeba.disponibles(e).stream().anyMatch(a -> a.id().equals("seba_javi_2")));
        e.registrarEvento("accion:seba_javi_1");
        assertTrue(GestorEventosSeba.disponibles(e).stream().anyMatch(a -> a.id().equals("seba_javi_2")));
    }

    @Test void fiestaNoSeSaltaAdelantandoDios() {
        EstadoPartida e = seba();
        e.setDia(30);
        assertEquals("tension", HitosSeba.pendiente(e).id());
        e.registrarEvento("seba_hito:tension");
        assertEquals("fiesta", HitosSeba.pendiente(e).id());
        e.registrarEvento("seba_hito:fiesta");
        assertEquals("consecuencias", HitosSeba.pendiente(e).id());
        e.registrarEvento("seba_hito:consecuencias");
        assertEquals("javi", HitosSeba.pendiente(e).id());
        e.registrarEvento("seba_hito:javi");
        assertEquals("rumbo", HitosSeba.pendiente(e).id());
        e.registrarEvento("seba_hito:rumbo");
        assertEquals("cierre", HitosSeba.pendiente(e).id());
        e.registrarEvento("seba_hito:cierre");
        assertNull(HitosSeba.pendiente(e));
    }
}
