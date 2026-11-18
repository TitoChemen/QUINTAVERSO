package com.example.quintaverso.motor;

/** Capítulos obligatorios: fiesta y consecuencias no se pueden saltar durmiendo. */
public final class HitosSeba {
    private HitosSeba() { }
    public record Hito(String id, String escenaId) { }

    public static Hito pendiente(EstadoPartida e) {
        if (!"seba".equals(e.getTemporada())) return null;
        int d = e.getDia();
        if (d >= 6 && !e.ocurrioEvento("seba_hito:tension"))
            return new Hito("tension", "seba_hito_tension");
        if (d >= 10 && !e.ocurrioEvento("seba_hito:fiesta"))
            return new Hito("fiesta", "seba_hito_fiesta");
        if (d >= 12 && !e.ocurrioEvento("seba_hito:consecuencias")) {
            // Si hubo una agresión a Rodrigo, Kelly debe confrontar ese hecho
            // antes de que siga cualquier otra ruta romántica.
            if (e.ocurrioEvento("seba_amira_delato_grupo"))
                return new Hito("consecuencias", "seba_post_delatado");
            if (e.ocurrioEvento("seba_golpeo_rodrigo"))
                return new Hito("consecuencias", "seba_post_combo");
            if (e.ocurrioEvento("seba_infidelidad"))
                return new Hito("consecuencias", "seba_post_infidelidad");
            if (e.ocurrioEvento("seba_fiesta_cama"))
                return new Hito("consecuencias", e.ocurrioEvento("seba_rodri_aviso")
                        ? "seba_post_foto" : "seba_post_sin_foto");
            return new Hito("consecuencias", "seba_post_fiel");
        }
        if (d >= 16 && e.ocurrioEvento("seba_amira_delato_grupo")
                && !e.ocurrioEvento("seba_hito:amira_distancia"))
            return new Hito("amira_distancia", "seba_amira_distancia");
        if (d >= 20 && e.ocurrioEvento("seba_amira_delato_grupo")
                && !e.ocurrioEvento("seba_hito:amira_cierre"))
            return new Hito("amira_cierre", "seba_amira_cierre");
        if (d >= 20 && e.ocurrioEvento("seba_infidelidad")
                && !e.ocurrioEvento("seba_amira_delato_grupo")
                && !e.ocurrioEvento("seba_hito:amira_honesta"))
            return new Hito("amira_honesta", "seba_amira_reencuentro_honesto");
        if (d >= 18 && !e.ocurrioEvento("seba_hito:javi"))
            return new Hito("javi", "seba_hito_javi");
        if (d >= 23 && e.ocurrioEvento("seba_amira_puerta_abierta")
                && !e.ocurrioEvento("seba_amira_delato_grupo")
                && e.ocurrioEvento("seba_acepta_responsabilidad")
                && !e.ocurrioEvento("seba_hito:amira_confesion"))
            return new Hito("amira_confesion", "seba_amira_confesion_tardia");
        if (d >= 23 && e.ocurrioEvento("seba_amira_puerta_abierta")
                && !e.ocurrioEvento("seba_amira_delato_grupo")
                && e.ocurrioEvento("seba_acepta_responsabilidad")
                && !e.ocurrioEvento("seba_hito:amira_confesion"))
            return new Hito("amira_confesion", "seba_amira_confesion_tardia");
        if (d >= 23 && e.ocurrioEvento("seba_amira_puerta_abierta")
                && !e.ocurrioEvento("seba_amira_delato_grupo")
                && e.ocurrioEvento("seba_acepta_responsabilidad")
                && !e.ocurrioEvento("seba_hito:amira_confesion"))
            return new Hito("amira_confesion", "seba_amira_confesion_tardia");
        if (d >= 26 && !e.ocurrioEvento("seba_hito:rumbo"))
            return new Hito("rumbo", "seba_hito_rumbo");
        if (d >= 29 && !e.ocurrioEvento("seba_hito:cierre"))
            return new Hito("cierre", "seba_hito_cierre");
        return null;
    }
}
