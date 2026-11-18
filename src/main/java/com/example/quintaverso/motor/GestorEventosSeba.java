package com.example.quintaverso.motor;

import java.util.ArrayList;
import java.util.List;

/** Actividades de Seba: ruta Kelly / Amira / Javi / Feña / amistades / autodescubrimiento. */
public final class GestorEventosSeba {
    private GestorEventosSeba() { }

    public static List<GestorEventos.Accion> disponibles(EstadoPartida e) {
        List<GestorEventos.Accion> a = new ArrayList<>();
        int d = e.getDia();
        if (d <= 25 && !e.ocurrioEvento("seba_kelly_termino")) {
            for (int i=1; i<=8; i++) {
                int desde = switch(i) {case 1->1; case 2->3; case 3->5; case 4->7;
                    case 5->11; case 6->14; case 7->20; default->23;};
                agregar(a,e,d,desde,"kelly_"+i,"💚 Kelly · Episodio "+i,
                    "Una conversación que importa dentro de la relación", "Con Kelly");
            }
        }
        for (int i=1; i<=9; i++) {
            int desde = switch(i) {case 1->1;case 2->2;case 3->4;case 4->6;case 5->9;
                case 6->13;case 7->16;case 8->21;default->24;};
            // Si Amira lo expuso y cortó el contacto, no continúa su ruta amorosa.
            if (e.ocurrioEvento("seba_amira_delato_grupo")) continue;
            if (e.ocurrioEvento("seba_infidelidad") && i >= 6
                    && !e.ocurrioEvento("seba_hito:amira_honesta")) continue;
            if (i >= 7 && !e.ocurrioEvento("seba_kelly_termino")) continue;
            agregar(a,e,d,desde,"amira_"+i,"💜 Amira · Episodio "+i,
                "Amistad, límites y cosas que no se han dicho", "Con Amira");
        }
        if (e.ocurrioEvento("seba_conocio_javi") && e.ocurrioEvento("seba_kelly_termino")) {
            for (int i=1; i<=7; i++) {
                agregar(a,e,d,18+i/2,"javi_"+i,"🩵 Javi · Episodio "+i,
                    "Una persona nueva, sin repetir errores viejos", "Con Javi");
            }
        }
        if (e.ocurrioEvento("seba_kelly_termino")) {
            for (int i=1; i<=6; i++) {
                agregar(a,e,d,13+i,"fena_"+i,"🧡 Feña · Episodio "+i,
                    "Y Richie, ¿qué opina de todo esto?", "Con Feña");
            }
        }
        for (int i=1; i<=24; i++) {
            String id = String.format("discord_%02d", i);
            // Cada noche tiene guion propio, desbloqueo gradual y elecciones persistentes.
            if (d == i && e.getTurno() == CalendarioJuego.Turno.NOCHE
                    && !e.ocurrioEvento("accion:seba_" + id))
                a.add(new GestorEventos.Accion("seba_" + id,
                    "🎧 Discord · Noche " + i, "Una conversación irrepetible de los cabros", id.startsWith("seba_") ? id : "seba_" + id,
                    "Discord", true));
        }
        for (int i=1; i<=26; i++) {
            String id = String.format("momento_%02d", i);
            if (i <= 9 && e.ocurrioEvento("seba_kelly_termino") && (i == 1 || i == 5 || i == 8)) continue;
            if ((i == 18 || i == 19) && !e.ocurrioEvento("seba_conocio_javi")) continue;
            if (d >= i && d <= i + 2 && !e.ocurrioEvento("accion:seba_" + id))
                a.add(new GestorEventos.Accion("seba_" + id,
                    "🎬 Momento " + i, "Una escena distinta que abre nuevos matices", "seba_" + id,
                    "Quinta Región", true));
        }
        for (int i=1; i<=7; i++) {
            int desde = switch(i) {case 1->13;case 2->4;case 3->13;
                case 4->10;case 5->13;case 6->16;default->22;};
            agregar(a,e,d,desde,"amigos_"+i,"🎮 Los cabros · Episodio "+i,
                "Kevin, Jairo, Cáceres y Rodrigo empeoran el chat", "Discord");
        }
        for (int i=1; i<=6; i++) {
            agregar(a,e,d,i*3+1,"estilo_"+i,"🌸 Estilo · Episodio "+i,
                "Un secreto de apariencia y confianza", "Tiendas / Casa");
        }
        if (e.ocurrioEvento("seba_femboy") && e.ocurrioEvento("seba_kelly_termino")
                && e.ocurrioEvento("seba_conocio_javi") && !e.ocurrioEvento("seba_javi_pareja")) {
            agregar(a,e,d,27,"javi_postrosa","🌸 Javi · El nuevo Seba",
                "Una cita después del cambio de estilo", "Café");
        }
        for (int i=1; i<=8; i++) {
            agregar(a,e,d,i*3-1,"reflexion_"+i,"📓 Diario · Episodio "+i,
                "Pensar antes de convertir un error en costumbre", "Habitación");
        }
        return a;
    }

    private static void agregar(List<GestorEventos.Accion> a, EstadoPartida e, int actual,
                                int desde, String id, String titulo, String descripcion, String lugar) {
        // Las rutas personales avanzan por capítulos para evitar saltos y spoilers.
        if (!id.startsWith("amigos_") && !id.equals("javi_postrosa") && id.matches("[a-z]+_[0-9]+")) {
            int separador = id.lastIndexOf('_');
            int numero = Integer.parseInt(id.substring(separador + 1));
            if (numero > 1 && !e.ocurrioEvento("accion:seba_" + id.substring(0, separador)
                    + "_" + (numero - 1))) return;
        }
        if (actual >= desde && !e.ocurrioEvento("accion:seba_"+id)) {
            a.add(new GestorEventos.Accion("seba_"+id, titulo, descripcion,
                    "seba_"+id, lugar, true));
        }
    }
}
