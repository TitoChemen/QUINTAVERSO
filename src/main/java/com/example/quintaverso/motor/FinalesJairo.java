package com.example.quintaverso.motor;

import java.util.List;

/** Los hitos y las elecciones concretas prevalecen sobre la afinidad. */
public final class FinalesJairo {
    private FinalesJairo() {}
    public static final List<String> COLORES = List.of("verde","azul","gris","rojo","violeta","naranja","celeste","rosa","blanco","negro","turquesa","dorado");
    private static boolean f(EstadoPartida e,String k) { return e.ocurrioEvento(k); }
    private static int s(EstadoPartida e,String k) { return e.obtenerEstadistica(k); }
    public static String calcular(EstadoPartida e) { return "final_jairo_"+color(e); }
    private static String color(EstadoPartida e) {
        if(f(e,"j_negro")) return "negro";
        if(f(e,"j_rojo")) return "rojo";
        if(f(e,"j_pareja")) {
            if(f(e,"j_reconciliados") && f(e,"j_confesion")) return "azul";
            if(!f(e,"j_infidelidad") && !f(e,"j_secreto") && !f(e,"j_confesion")) {
                if((!f(e,"j_v11") || f(e,"j_audio_sincero") && f(e,"j_acuerdo_retorno") && f(e,"j_cumple_acuerdo")) && f(e,"j_presencia") && f(e,"j_escucha") && f(e,"j_independencia") && f(e,"j_privacidad") && f(e,"j_limites_mutuos") && f(e,"j_mary_crece") && f(e,"j_equilibrio") && f(e,"j_autonomia") && f(e,"j_futuro") && f(e,"j_compromiso") && f(e,"j_amigos") && s(e,"confianza_mary")>=85 && s(e,"honestidad")>=80 && s(e,"madurez")>=85 && s(e,"amistad")>=70 && s(e,"inseguridad")<=20) return "dorado";
                return "verde";
            }
        }
        if(!f(e,"j_pareja") && !f(e,"j_bloqueado")) {
            if(f(e,"j_cita_anto") && f(e,"j_interes_anto") && f(e,"j_cierre_limpio") && (f(e,"j_anto_confirmada") || !f(e,"j_v11"))) return "celeste";
            if(f(e,"j_anto_perdida")) return "turquesa";
            if(f(e,"j_cita_ale") && f(e,"j_confirmada_ale") && f(e,"j_cierre_limpio")) return "violeta";
            if(f(e,"j_cita_andrea") && f(e,"j_confirmada_andrea") && f(e,"j_cierre_limpio")) return "rosa";
            if(f(e,"j_cita_cami") && f(e,"j_confirmada_cami") && (f(e,"j_cami_beso_libre") || f(e,"j_beso_cami") && f(e,"j_confesion"))) return "naranja";
        }
        if(f(e,"j_gris") && !f(e,"j_infidelidad") && !f(e,"j_confesion") && !f(e,"j_solo")) return "gris";
        return "blanco";
    }
}
