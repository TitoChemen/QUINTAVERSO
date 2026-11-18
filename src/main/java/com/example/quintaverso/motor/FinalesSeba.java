package com.example.quintaverso.motor;

/** Finales basados en decisiones confirmadas, nunca solo en una barra de afecto. */
public final class FinalesSeba {
    private FinalesSeba() { }

    public static String calcular(EstadoPartida e) {
        if (e.ocurrioEvento("seba_femboy") && e.obtenerEstadistica("estilo") >= 45)
            return "final_seba_femboy";
        // El conflicto físico es una ruta completa, no un reskin del final naranja.
        if (e.ocurrioEvento("seba_richie_golpea_seba")) return "final_seba_turquesa";
        if (e.ocurrioEvento("seba_doble_vida")) return "final_seba_harem";
        if (e.ocurrioEvento("seba_kelly_sigue_sin_saber") && e.ocurrioEvento("seba_no_descubierto")
                && !e.ocurrioEvento("seba_kelly_termino")) return "final_seba_amarillo";
        if (e.ocurrioEvento("seba_kelly_compromiso") && !e.ocurrioEvento("seba_kelly_termino")
                && e.ocurrioEvento("seba_fiesta_fiel")) return "final_seba_verde";
        if (e.ocurrioEvento("seba_kelly_reconciliada") && !e.ocurrioEvento("seba_kelly_termino"))
            return "final_seba_azul";
        if (e.ocurrioEvento("seba_fena_richie_pareja") && e.ocurrioEvento("seba_kelly_termino"))
            return "final_seba_naranja_picnic";
        if (e.ocurrioEvento("seba_fena_pareja") && e.ocurrioEvento("seba_kelly_termino"))
            return "final_seba_naranja";
        if (e.ocurrioEvento("seba_javi_pareja") && e.ocurrioEvento("seba_kelly_termino"))
            return "final_seba_celeste";
        if (e.ocurrioEvento("seba_amira_pareja") && e.ocurrioEvento("seba_kelly_termino")
                && !e.ocurrioEvento("seba_amira_delato_grupo")
                && e.ocurrioEvento("seba_amira_confesion_valida"))
            return e.ocurrioEvento("seba_infidelidad") ? "final_seba_rojo" : "final_seba_violeta";
        if (e.ocurrioEvento("seba_solo")) return "final_seba_blanco";
        if (e.ocurrioEvento("seba_kelly_termino") || e.ocurrioEvento("seba_infidelidad")
                || e.ocurrioEvento("seba_mentira")) return "final_seba_gris";
        if (e.ocurrioEvento("seba_fiesta_fiel") && e.obtenerEstadistica("confianza_kelly") >= 40)
            return "final_seba_verde";
        return "final_seba_blanco";
    }
}
