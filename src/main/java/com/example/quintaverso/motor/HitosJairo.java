package com.example.quintaverso.motor;

/** Cada jornada tiene un capítulo obligatorio; las variantes respetan el estado de la relación. */
public final class HitosJairo {
    private HitosJairo() {}
    public record Hito(String id, String escenaId) {}
    private static boolean f(EstadoPartida e,String k) { return e.ocurrioEvento(k); }
    public static int coqueteos(EstadoPartida e) {
        int n=0;
        for (String k : new String[]{"ale","andrea","cami","anto"}) if(f(e,"j_flirt_"+k)) n++;
        return n;
    }
    /** Los capítulos de noche (carrete, Discord, confesión nocturna) nunca aparecen por la mañana. */
    public static CalendarioJuego.Turno turnoCapitulo(int dia) {
        return switch(dia) {
            case 4, 8, 11, 15, 20, 26, 29 -> CalendarioJuego.Turno.NOCHE;
            default -> CalendarioJuego.Turno.TARDE;
        };
    }
    public static boolean tocaCapitulo(EstadoPartida e, Hito h) {
        if(h==null) return false;
        int d=Integer.parseInt(h.id());
        return d<e.getDia() || e.getTurno().ordinal()>=turnoCapitulo(d).ordinal();
    }
    public static Hito pendiente(EstadoPartida e) {
        if (!"jairo".equals(e.getTemporada())) return null;
        for(int d=1; d<=Math.min(e.getDia(),30); d++) {
            String id=String.format(java.util.Locale.ROOT,"%02d",d);
            if(!f(e,"jairo_hito:"+id)) return new Hito(id,escena(e,d));
        }
        return null;
    }
    public static String escena(EstadoPartida e,int d) {
        String base=String.format(java.util.Locale.ROOT,"jairo_d%02d",d);
        boolean pareja=f(e,"j_pareja"), pausa=f(e,"j_pausa"), bloqueo=f(e,"j_bloqueado");
        if(d==19) return pareja && f(e,"j_secreto") ? base+"_confesion" : pareja ? base : base+"_libre";
        if(d==20 && pausa) return base+"_pausa";
        if(d==21 && f(e,"j_secreto")) {
            if(coqueteos(e)>=4) return base+"_negro";
            if(f(e,"j_beso_andrea")) return base+"_rojo";
            return base+"_oculto";
        }
        if(d==22) return pausa?base+"_pausa":pareja?base:base+"_libre";
        if(d==23) return pausa?base+"_pausa":pareja?base:bloqueo?base+"_bloqueado":base+"_libre";
        if(d==24) return pausa?base+"_reparar":pareja?base:base+"_libre";
        if(d==25) {
            if(pareja) return f(e,"j_reconciliados")?base+"_reparado":base;
            if(!bloqueo) {
                for(String k:new String[]{"ale","andrea","cami","anto"}) if(f(e,"j_interes_"+k)) return base+"_"+k;
                if(f(e,"j_beso_cami") && f(e,"j_confesion") && !f(e,"j_solo")) return base+"_cami_retorno";
            }
            return base+"_libre";
        }
        if(d==26 && !pareja) return base+"_libre";
        if(d==27 && !pareja) {
            if(f(e,"j_interes_anto")) return base+(f(e,"j_cita_anto")?"_anto_cita":"_anto");
            for(String k:new String[]{"ale","andrea","cami"}) if(f(e,"j_cita_"+k)) return base+"_"+k;
            return base+"_libre";
        }
        if(d==28 && !pareja) return f(e,"j_interes_anto") && !f(e,"j_cita_anto")?base+"_anto_tarde":base+"_libre";
        if(d==29) {
            if(!pareja) return base+"_libre";
            if(e.obtenerEstadistica("confianza_mary")<40 || f(e,"j_evita_cierre") || f(e,"j_reparacion_dano")) return base+"_ruptura";
        }
        if(d==30) {
            if(!pareja) return base+"_libre";
            if(e.obtenerEstadistica("confianza_mary")<40) return base+"_ruptura";
        }
        return base;
    }
}
