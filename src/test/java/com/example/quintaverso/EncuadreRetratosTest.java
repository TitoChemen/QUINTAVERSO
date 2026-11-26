package com.example.quintaverso;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/** La composición automática respeta el cuerpo completo en poses neutras. */
class EncuadreRetratosTest {
    @Test void neutralPermaneceCompleto() {
        assertFalse(EncuadreRetratos.necesitaPrimerPlano("/imagenes/personajes/seba/01_neutral.png"));
        assertFalse(EncuadreRetratos.necesitaPrimerPlano("/imagenes/personajes/caceres/caceres_cuerpo_completo_neutral.png"));
    }

    @Test void lasEmocionesMerecenMasZoom() {
        assertTrue(EncuadreRetratos.necesitaPrimerPlano("/imagenes/personajes/seba/07_sorprendido.png"));
        assertTrue(EncuadreRetratos.necesitaPrimerPlano("/imagenes/personajes/kelly/02_brazos_cruzados.png"));
        assertTrue(EncuadreRetratos.necesitaPrimerPlano("/imagenes/personajes/caceres/caceres_risa.png"));
    }

    @Test void cambiarPlanoEsCiclico() {
        assertEquals(EncuadreRetratos.Plano.PRIMER_PLANO,
                EncuadreRetratos.Plano.AUTOMATICO.siguiente());
        assertEquals(EncuadreRetratos.Plano.CUERPO_COMPLETO,
                EncuadreRetratos.Plano.PRIMER_PLANO.siguiente());
        assertEquals(EncuadreRetratos.Plano.AUTOMATICO,
                EncuadreRetratos.Plano.CUERPO_COMPLETO.siguiente());
    }
}
