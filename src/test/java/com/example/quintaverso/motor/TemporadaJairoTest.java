package com.example.quintaverso.motor;

import com.example.quintaverso.RecursosVisuales;
import org.junit.jupiter.api.Test;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class TemporadaJairoTest {
    private final GestorHistoria historia = new GestorHistoria("jairo");
    private void ruta(String color, Map<Integer,Integer> elecciones) {
        EstadoPartida e=new EstadoPartida("jairo");
        CalendarioJuego calendario=new CalendarioJuego(e);
        for(int d=1;d<=30;d++) {
            var h=HitosJairo.pendiente(e);
            assertNotNull(h,"Día "+d);
            String nodo=h.escenaId(); boolean primero=true; int pasos=0;
            while(nodo!=null) {
                assertTrue(++pasos<=8,"Ciclo: "+nodo);
                Escena escena=historia.obtener(nodo);
                var disponibles=escena.getDecisiones().stream().filter(x->x.disponible(e)).toList();
                if(escena.getDecisiones().isEmpty()) { nodo=escena.getSiguiente(); continue; }
                assertFalse(disponibles.isEmpty(),nodo);
                Decision decision=primero&&elecciones.containsKey(d)?escena.getDecisiones().get(elecciones.get(d)):disponibles.get(0);
                assertTrue(decision.disponible(e),color+": día "+d);
                decision.aplicar(e); nodo=decision.getSiguiente(); primero=false;
            }
            assertTrue(pasos>=2,"Falta interludio día "+d);
            e.registrarEvento("jairo_hito:"+h.id());
            assertNull(HitosJairo.pendiente(e));
            calendario.dormirHastaManana();
        }
        assertEquals("final_jairo_"+color,historia.calcularFinal(e));
    }
    @Test void todosLosFinalesSonAlcanzables() {
        ruta("dorado",Map.of()); ruta("verde",Map.of(1,1)); ruta("azul",Map.of(15,2));
        ruta("gris",Map.of(14,1,23,6)); ruta("rojo",Map.of(17,1,19,1));
        ruta("violeta",Map.of(14,1,23,0)); ruta("naranja",Map.of(14,1,15,3,23,1));
        ruta("naranja",Map.of(15,2,24,1)); ruta("celeste",Map.of(14,1,23,4));
        ruta("rosa",Map.of(14,1,23,3)); ruta("blanco",Map.of(14,1,23,5));
        ruta("negro",Map.of(2,1,5,1,15,2,18,2,19,1));
        ruta("turquesa",Map.of(14,1,18,1,23,4,25,1,27,1));
    }
    @Test void horarioYAccionesRespetanContinuidad() {
        EstadoPartida e=new EstadoPartida("jairo");
        var h=HitosJairo.pendiente(e);
        assertNotNull(h); assertFalse(HitosJairo.tocaCapitulo(e,h));
        assertFalse(GestorEventosJairo.disponibles(e).isEmpty());
        e.setTurno(CalendarioJuego.Turno.TARDE);
        assertTrue(HitosJairo.tocaCapitulo(e,h));
        assertTrue(GestorEventosJairo.disponibles(e).isEmpty());
        e.registrarEvento("jairo_hito:01"); e.setTurno(CalendarioJuego.Turno.NOCHE);
        assertTrue(GestorEventosJairo.disponibles(e).stream().anyMatch(a->a.id().equals("jairo_discord_01")));
        e.setDia(25); for(int i=2;i<=25;i++)e.registrarEvento(String.format(java.util.Locale.ROOT,"jairo_hito:%02d",i));
        e.desactivarEvento("j_pareja"); e.registrarEvento("j_bloqueado"); e.registrarEvento("j_interes_anto"); e.setTurno(CalendarioJuego.Turno.MANANA);
        assertFalse(GestorEventosJairo.disponibles(e).stream().anyMatch(a->a.id().contains("anto_ruta")||a.id().contains("mary_")));
        for(var a:GestorEventosJairo.disponibles(e)) assertNotNull(historia.obtener(a.escenaId()));
    }
    @Test void cgYNochesExisten() {
        for(String c:FinalesJairo.COLORES) assertNotNull(getClass().getResource(RecursosVisuales.cg(historia.obtener("final_jairo_"+c))));
        for(int d=1;d<=30;d++) assertNotNull(historia.obtener(String.format(java.util.Locale.ROOT,"jairo_discord_%02d",d)));
    }
}
