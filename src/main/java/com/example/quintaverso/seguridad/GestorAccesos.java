
package com.example.quintaverso.seguridad;

import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Códigos de acceso locales para las temporadas.
 * No representan seguridad criptográfica real.
 */
public final class GestorAccesos {

    private final Map<String, Set<String>> claves = Map.of(
            "caceres", Set.of("Wordbearers21"),
            "jairo", Set.of("JAIRO2026"),
            "seba", Set.of("Kkdvakck1106%")
    );

    public boolean validar(String temporada, String clave) {
        if (temporada == null || clave == null) {
            return false;
        }

        return claves.getOrDefault(
                temporada.toLowerCase(Locale.ROOT),
                Set.of()
        ).contains(clave.trim());
    }
}
