package com.example.quintaverso;

import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Locale;

/**
 * Encuadre no destructivo de sprites: alterna cuerpo entero y primeros planos
 * sin crear imágenes duplicadas ni cambiar recursos de la historia.
 */
public final class EncuadreRetratos {
    private EncuadreRetratos() { }

    public enum Plano {
        AUTOMATICO, PRIMER_PLANO, CUERPO_COMPLETO;

        public Plano siguiente() {
            return switch (this) {
                case AUTOMATICO -> PRIMER_PLANO;
                case PRIMER_PLANO -> CUERPO_COMPLETO;
                case CUERPO_COMPLETO -> AUTOMATICO;
            };
        }
    }

    /**
     * La pose neutra se muestra completa para apreciar la vestimenta.
     * Las poses expresivas se muestran desde el rostro hasta el torso.
     * Las poses sentadas usan un recorte más suave para conservar su lenguaje corporal.
     */
    public static boolean necesitaPrimerPlano(String ruta) {
        if (ruta == null || ruta.isBlank()) return false;
        String nombre = ruta.toLowerCase(Locale.ROOT);
        if (nombre.contains("neutral") || nombre.contains("cuerpo_completo")) return false;
        return nombre.endsWith(".png") || nombre.endsWith(".jpg") || nombre.endsWith(".jpeg");
    }

    /** Calcula el recorte sin modificar el archivo fuente. */
    public static Rectangle2D calcular(Image imagen, String ruta, Plano plano) {
        if (imagen == null || imagen.isError() || plano == Plano.CUERPO_COMPLETO
                || imagen.getWidth() <= 0 || imagen.getHeight() <= 0) return null;
        if (plano == Plano.AUTOMATICO && !necesitaPrimerPlano(ruta)) return null;

        String nombre = ruta == null ? "" : ruta.toLowerCase(Locale.ROOT);
        double fraccion;
        if (plano == Plano.PRIMER_PLANO) {
            fraccion = .52;
        } else if (nombre.contains("sentad") || nombre.contains("pensativo")) {
            fraccion = .79;
        } else if (nombre.contains("sorprend") || nombre.contains("traumad")) {
            fraccion = .61;
        } else {
            fraccion = .68;
        }
        return new Rectangle2D(0, 0, imagen.getWidth(), imagen.getHeight() * fraccion);
    }

    public static void aplicar(ImageView vista, String ruta, Plano plano) {
        if (vista == null) return;
        vista.setViewport(calcular(vista.getImage(), ruta, plano));
    }
}
