package com.example.quintaverso.motor;

import java.util.ArrayList;
import java.util.List;

/** Ofrece eventos únicos con requisitos y episodios cotidianos sin repetir hasta agotar el catálogo. */
public final class GestorEventos {
    public record Accion(String id, String titulo, String descripcion,
                         String escenaId, String lugar, boolean unica) { }

    public List<Accion> disponibles(EstadoPartida e) {
        if ("seba".equals(e.getTemporada())) return GestorEventosSeba.disponibles(e);
        if ("jairo".equals(e.getTemporada())) return GestorEventosJairo.disponibles(e);
        List<Accion> acciones = new ArrayList<>();
        int d = e.getDia();
        int l = e.obtenerEstadistica("lumi");
        int c = e.obtenerEstadistica("cony");
        int cl = e.obtenerEstadistica("confianza_lumi");
        int cc = e.obtenerEstadistica("confianza_cony");
        int h = e.obtenerEstadistica("honestidad");

        if (d >= 1) agregar(acciones,e,"fiesta","🎭 Aquella fiesta","Cómo conociste a Cony", "evento_fiesta", "Fiesta de disfraces");
        if (d >= 2 && l >= 15) agregar(acciones,e,"lumi_identidad","💙 Lumi sin filtro","Mucho más que su historia con Max","evento_lumi_identidad","Cafetería en Viña");
        if (d >= 3 && c >= 18) agregar(acciones,e,"cony_libre","💗 Cony habla claro","¿Cita, carrete o algo sin etiquetas?","evento_cony_libre","Plaza de Villa Alemana");
        if (d >= 4 && l >= 20) agregar(acciones,e,"max_planton","📱 El desplante de Max","Una promesa rota por tercera vez","evento_max_planton","Llamada nocturna");
        if (d >= 5) agregar(acciones,e,"consejo_1","🎧 Consejo del grupo","Kevin y Seba aconsejan; Jairo improvisa","evento_consejo_1","Discord");
        if (d >= 6 && c >= 25) agregar(acciones,e,"cony_cita","🌙 Salir con Cony","Química y expectativas distintas","evento_cony_cita","Paseo en Villa Alemana");
        if (d >= 8 && l >= 25) agregar(acciones,e,"lumi_taller","🧩 El mundo propio de Lumi","Una tarde para conocer sus intereses","evento_lumi_taller","Viña del Mar");
        if (d >= 10 && c >= 35 && cc >= 25 && e.ocurrioEvento("cony_casual_acordado"))
            agregar(acciones,e,"cony_noche","💗 Una invitación de Cony","Deseo, límites y una noche que puede cambiar algo","evento_cony_noche","Departamento de Cony");
        if (d >= 11 && e.ocurrioEvento("max_planton_escuchado"))
            agregar(acciones,e,"max_mentira","⚡ Otra mentira de Max","Lumi decide dejar de justificarlo","evento_max_mentira","Viña del Mar");
        if (d >= 12 && e.ocurrioEvento("cony_intimidad"))
            agregar(acciones,e,"cony_despues","☕ La mañana después","¿Qué significó lo de anoche?","evento_cony_despues","Cocina");
        if (d >= 13 && e.ocurrioEvento("max_mentira_escuchada"))
            agregar(acciones,e,"lumi_decision","💙 Lumi toma una decisión","Su relación con Max llega al límite","evento_lumi_decision","Parque en Viña");
        if (d >= 14 && l >= 25 && c >= 25)
            agregar(acciones,e,"cruce","⚡ La Quinta es un pañuelo","Dos conversaciones chocan en Valparaíso","evento_cruce","Terminal de Valparaíso");
        if (d >= 16 && e.ocurrioEvento("max_ruptura"))
            agregar(acciones,e,"lumi_espacio","🌊 Lumi recupera su espacio","No confundir compañía con salvación","evento_lumi_espacio","Costanera");
        if (d >= 18 && c >= 45 && cc >= 35)
            agregar(acciones,e,"cony_cambio","💗 ¿Y si me pasa algo contigo?","Cony se pregunta si todavía quiere solo algo casual","evento_cony_cambio","Café");
        if (d >= 19) agregar(acciones,e,"consejo_2","🎮 Asamblea sentimental","Kevin y Seba discuten una verdad incómoda","evento_consejo_2","Discord");
        if (d >= 21 && e.ocurrioEvento("max_ruptura") && cl >= 45 && l >= 48)
            agregar(acciones,e,"lumi_sentimientos","💙 La confesión de Lumi","Después de Max, el afecto asusta","evento_lumi_sentimientos","Muelle");
        if (d >= 22 && e.ocurrioEvento("cony_pareja_posible") && c >= 50 && cc >= 40)
            agregar(acciones,e,"cony_pareja","💗 Cony propone algo distinto","De lo casual a una posibilidad real","evento_cony_pareja","Plaza");
        if (d >= 23 && e.ocurrioEvento("cony_casual_acordado") && c >= 40)
            agregar(acciones,e,"cony_reglas","💬 Lo casual también tiene reglas","Cómo seguir sin prometer exclusividad","evento_cony_reglas","Mensajes");
        if (d >= 24 && e.ocurrioEvento("lumi_romance") &&
                e.ocurrioEvento("cony_casual_acordado") && cl >= 50 && cc >= 50 && h >= 70 &&
                !e.ocurrioEvento("cony_pareja") && !e.ocurrioEvento("lumi_exclusiva"))
            agregar(acciones,e,"acuerdos","🤝 Todas las cartas sobre la mesa","Nada de secretos ni consentimientos supuestos","evento_acuerdos","Café frente al mar");
        if (d >= 26 && (e.ocurrioEvento("lumi_romance") || e.ocurrioEvento("cony_pareja_posible")
                || e.ocurrioEvento("cony_casual_acordado")))
            agregar(acciones,e,"definir_rumbo","🗣️ La decisión incómoda","El amor no se define con un marcador","evento_definir_rumbo","Mirador");
        if (d >= 28) agregar(acciones,e,"vispera","🌅 La última conversa","Qué clase de persona fue Cáceres","evento_vispera","Discord");

        recurrente(acciones,e,"lumi",24,"💙 Hablar con Lumi", "Mensajes que cambian con los días", "Mensajes");
        recurrente(acciones,e,"cony",24,"💗 Hablar con Cony", "Bromas, deseos, límites y proyectos", "Mensajes");
        recurrente(acciones,e,"discord",18,"🎮 Discord con los cabros", "Tallas y consejos improbables", "Discord");
        recurrente(acciones,e,"reflexion",12,"📓 Pensar un rato", "Lo que quieres y lo que estás evitando", "Casa");
        recurrente(acciones,e,"paseo",12,"🚶 Recorrer la Quinta", "Salir a despejarte sin perseguir a nadie", "Quinta Región");
        return acciones;
    }

    private void agregar(List<Accion> destino, EstadoPartida e, String id, String titulo,
                         String descripcion, String escenaId, String lugar) {
        if (!e.ocurrioEvento("accion:" + id))
            destino.add(new Accion(id,titulo,descripcion,escenaId,lugar,true));
    }

    private void recurrente(List<Accion> destino, EstadoPartida e, String id, int total,
                            String titulo, String descripcion, String lugar) {
        int numero = e.contarActividad(id);
        // Tras agotar historias únicas se habilitan cuatro momentos de rutina, sin puntos gratis.
        String escena = numero < total ? id + "_" + (numero + 1) : id + "_rutina_" + (numero % 4 + 1);
        destino.add(new Accion(id,titulo,descripcion,escena,lugar,false));
    }
}
