package com.example.quintaverso;

import com.example.quintaverso.motor.Dialogo;
import com.example.quintaverso.motor.Escena;

import java.text.Normalizer;
import java.util.Locale;
import java.util.Map;

/**
 * Convenciones visuales para la temporada de Cáceres.
 * Todos los recursos se buscan en el classpath: funcionan también dentro de un JAR.
 * Los JSON permiten personalizar fondo y CG sin tocar este archivo.
 * No afecta a los cálculos de estadísticas, eventos, enlaces ni partidas.
 */
public final class RecursosVisuales {
    private RecursosVisuales() { }

    private static final Map<String, String> FINALES = Map.of(
        "final_lumi", "final_azul.png",
        "final_cony_pareja", "final_rosa.png",
        "final_cony_casual", "final_magenta.png",
        "final_acuerdos", "final_violeta.png",
        "final_descubierto", "final_gris.png",
        "final_crecimiento", "final_secreto.png",
        "final_lumi_max", "final_indigo.png",
        "final_calvo", "final_calvo.png"
    );

    public static String cg(Escena escena) {
        if (escena.getCg() != null && !escena.getCg().isBlank())
            return "/imagenes/cg/" + escena.getCg();
        String archivo = FINALES.get(escena.getId());
        return archivo == null ? null : "/imagenes/cg/" + archivo;
    }

    public static String fondo(Escena escena) {
        if (escena.getFondo() != null && !escena.getFondo().isBlank())
            return "/imagenes/fondos/" + escena.getFondo();
        String lugar = normalizar(escena.getLugar());
        if (lugar.contains("discord")) return "/imagenes/fondos/discord_noche.png";
        if (lugar.contains("terminal")) return "/imagenes/fondos/terminal_valparaiso.png";
        if (lugar.contains("mirador")) return "/imagenes/fondos/mirador_vina.png";
        if (lugar.contains("muelle")) return "/imagenes/fondos/muelle_noche.png";
        if (lugar.contains("costanera") || lugar.contains("mar"))
            return "/imagenes/fondos/costanera_atardecer.png";
        if (lugar.contains("cafe") || lugar.contains("cafeteria"))
            return "/imagenes/fondos/cafe_vina.png";
        if (lugar.contains("parque")) return "/imagenes/fondos/parque_vina.png";
        if (lugar.contains("plaza")) return "/imagenes/fondos/plaza_villa_alemana.png";
        return "/imagenes/fondos/habitacion_caceres_dia.png";
    }

    public static String personaje(String nombre) {
        String n = normalizar(nombre);
        // También reconoce 'Kevin (mensaje)', 'Cony (audio)', etc.
        if (n.startsWith("lumi")) return "lumi";
        if (n.startsWith("cony")) return "cony";
        if (n.startsWith("caceres")) return "caceres";
        if (n.startsWith("jairo")) return "jairo";
        if (n.startsWith("seba")) return "seba";
        if (n.startsWith("kevin")) return "kevin";
        return "narrador";
    }

    /** Recurso del hablante. Devuelve null para narrador; Kevin ya tiene tres expresiones. */
    public static String retrato(Dialogo d) {
        if (d == null) return null;
        String nombre = personaje(d.getPersonaje());
        String emoción = emocion(d.getTexto());
        return switch (nombre) {
            case "lumi", "cony" -> spriteChica(nombre, emoción, d.getTexto());
            case "caceres" -> "/imagenes/personajes/caceres/" + switch (emoción) {
                case "feliz" -> "caceres_feliz.png";
                case "risa" -> "caceres_risa.png";
                case "serio" -> "caceres_serio.png";
                case "nervioso" -> "caceres_nervioso.png";
                case "sorprendido" -> "caceres_sorprendido.png";
                case "coqueta" -> "caceres_picaro.png";
                case "pensativo" -> "caceres_pensativo.png";
                default -> "caceres_cuerpo_completo_neutral.png";
            };
            case "seba" -> "/imagenes/personajes/seba/" +
                    (emoción.equals("risa") || emoción.equals("feliz") ? "seba_risa.png" :
                    emoción.equals("serio") ? "seba_serio.png" : "seba_neutral.png");
            case "jairo" -> "/imagenes/personajes/jairo/" +
                    (emoción.equals("serio") ? "jairo_serio.png" :
                            emoción.equals("risa") || emoción.equals("feliz") ? "jairo_tranquilo.png" :
                                    "jairo_neutral.png");

            case "kevin" -> "/imagenes/personajes/kevin/" +
                    (emoción.equals("serio") ||
                            emoción.equals("nervioso") ||
                            emoción.equals("pensativo") ? "enojado.png" :
                            emoción.equals("risa") ||
                                    emoción.equals("feliz") ||
                                    emoción.equals("coqueta") ? "feliz.png" :
                                    "neutral.png");
            default -> null;
        };
    }

    /** En los chats se mantiene visible al personaje incluso durante la narración. */
    public static String protagonistaEscena(Escena escena) {
        String lugar = normalizar(escena.getLugar());
        if (lugar.contains("lumi")) return "lumi";
        if (lugar.contains("cony")) return "cony";
        if (lugar.contains("discord")) return null;
        String id = normalizar(escena.getId());
        if (id.startsWith("lumi_")) return "lumi";
        if (id.startsWith("cony_")) return "cony";
        return null;
    }

    public static String neutro(String nombre) {
        return retrato(new Dialogo(nombre, ""));
    }

    private static String spriteChica(String nombre, String emoción, String diálogo) {
        String tono = normalizar(diálogo);
        String archivo = switch (emoción) {
            case "risa", "feliz" -> "04_saludando.png";
            case "serio" -> "02_brazos_cruzados.png";
            case "sorprendido" -> "07_sorprendida.png";
            case "coqueta" -> "08_coqueta.png";
            case "pensativo", "nervioso" -> "05_sentada.png";
            default -> tono.contains("tranquil") || tono.contains("calma") || tono.contains("espacio") ?
                    "06_manos_espalda.png" :
                    tono.contains("hoy") || tono.contains("vamos") || tono.contains("hacer") ?
                    "03_mano_cintura.png" : "01_neutral.png";
        };
        return "/imagenes/personajes/" + nombre + "/" + archivo;
    }

    private static String emocion(String texto) {
        String t = normalizar(texto);
        if (t.contains("jaja") || t.contains("jeje") || t.contains("rio") || t.contains("reir") || t.contains("risa") || t.contains("carcajada"))
            return "risa";
        if (t.contains("sorprend") || t.contains("¿que?") || t.contains("¡que!") || t.contains("no puede ser") || t.contains("en serio?"))
            return "sorprendido";
        if (t.contains("enoj") || t.contains("mentira") || t.contains("engañ") || t.contains("molest") || t.contains("basta") || t.contains("decepcion"))
            return "serio";
        if (t.contains("nervios") || t.contains("miedo") || t.contains("disculpa") || t.contains("perdon") || t.contains("ansiedad") || t.contains("inquiet"))
            return "nervioso";
        if (t.contains("bes") || t.contains("coquet") || t.contains("bonit") || t.contains("lindo") || t.contains("contigo") || t.contains("te quiero") || t.contains("abraz"))
            return "coqueta";
        if (t.contains("pienso") || t.contains("pensar") || t.contains("record") || t.contains("triste") || t.contains("solo"))
            return "pensativo";
        if (t.contains("feliz") || t.contains("genial") || t.contains("gracias") || t.contains("content") || t.contains("sonrie"))
            return "feliz";
        return "neutral";
    }

    private static String normalizar(String valor) {
        if (valor == null) return "";
        String sinTildes = Normalizer.normalize(valor, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "");
        return sinTildes.toLowerCase(Locale.ROOT);
    }
}
