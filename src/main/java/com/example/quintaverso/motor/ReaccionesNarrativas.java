package com.example.quintaverso.motor;

import java.util.List;
import java.util.Map;

/** Interludio posterior a una elección: nunca salta de inmediato al calendario. */
public final class ReaccionesNarrativas {
    private ReaccionesNarrativas() { }

    public static List<Dialogo> construir(Escena escena, Decision decision) {
        if (decision.getReaccion() != null && !decision.getReaccion().isEmpty()) {
            return List.copyOf(decision.getReaccion());
        }
        String id = escena.getId() == null ? "" : escena.getId().toLowerCase();
        String nombre = elegirInterlocutor(escena, id);
        Map<String, Integer> efectos = decision.getEfectos();
        int tension = efecto(efectos, "sospecha") - efecto(efectos, "honestidad");
        int confianza = efecto(efectos, "confianza_lumi") + efecto(efectos, "confianza_cony");
        boolean incomodidad = tension > 5 || confianza < -2;
        boolean positivo = !incomodidad &&
                (confianza > 0 || efecto(efectos, "honestidad") > 0 || efecto(efectos, "amistad") > 0);
        String respuesta = switch (nombre) {
            case "Lumi" -> incomodidad
                    ? "Cáceres, prefiero que conversemos sin apurar las cosas. Eso que dijiste me hizo ruido."
                    : positivo
                    ? "Gracias por escucharme. Me importa que podamos hablar de esto con calma."
                    : "Te escuché. Déjame pensarlo un poquito, ¿ya?";
            case "Cony" -> incomodidad
                    ? "Ojo, Cáceres. No quiero que confundamos lo que uno siente con lo que el otro quiere."
                    : positivo
                    ? "Me gusta que hablís claro, así podemos entendernos sin pasarnos películas."
                    : "Ya po, lo conversamos. Prefiero que nos digamos las cosas de frente.";
            case "Kevin" -> incomodidad
                    ? "Uuuh, hermano... piénsala bien, que después la talla te puede salir cara."
                    : positivo
                    ? "¡Eso po, wn! Por fin una decisión que podemos conversar sin gritarte, JAJA."
                    : "Ya, Cáceres, te escuchamos. Después no digai que nadie te avisó.";
            case "Seba" -> incomodidad
                    ? "Hermano, una cosa es decirlo y otra hacerse responsable de lo que pase."
                    : positivo
                    ? "Bien, pero acuérdate de mantenerte consecuente con lo que dijiste."
                    : "Ya. Dale una vuelta igual; nadie te está apurando.";
            case "Jairo" -> incomodidad
                    ? "No sé si ese consejo lo habría dado yo... y eso ya es preocupante."
                    : positivo
                    ? "Ya, esa jugada me gustó. Esta vez ni necesito abrir el Excel."
                    : "Mira, yo tenía otra idea, pero mejor vemos cómo resulta esa.";
            default -> "Cáceres respira un segundo. Ha elegido un camino, y ahora tendrá que vivir con lo que venga.";
        };
        return List.of(new Dialogo(nombre, respuesta));
    }

    private static int efecto(Map<String, Integer> efectos, String clave) {
        return efectos == null ? 0 : efectos.getOrDefault(clave, 0);
    }

    private static String elegirInterlocutor(Escena escena, String id) {
        if (id.contains("lumi") || id.contains("max")) return "Lumi";
        if (id.contains("cony") || id.contains("fiesta")) return "Cony";
        if (id.contains("discord") || id.contains("consejo") || id.contains("vispera")) return "Kevin";
        List<Dialogo> dialogos = escena.getDialogos();
        if (dialogos != null) {
            for (int i = dialogos.size() - 1; i >= 0; i--) {
                String candidato = dialogos.get(i).getPersonaje();
                if (candidato == null) continue;
                if (List.of("Lumi", "Cony", "Kevin", "Seba", "Jairo").contains(candidato))
                    return candidato;
            }
        }
        return "Narrador";
    }
}
