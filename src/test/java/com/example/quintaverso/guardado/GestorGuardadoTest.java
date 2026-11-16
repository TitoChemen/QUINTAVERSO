package com.example.quintaverso.guardado;

import com.example.quintaverso.motor.Dialogo;
import com.example.quintaverso.motor.EstadoPartida;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Path;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class GestorGuardadoTest {
    @TempDir Path temporal;

    @Test
    void guardaYReanudaUnaReaccionSinPerderElProgreso() throws Exception {
        GestorGuardado gestor = new GestorGuardado(temporal);
        EstadoPartida estado = new EstadoPartida("caceres");
        estado.setContextoEscena("ACCION");
        estado.setEscenaActual("evento_cony_libre");
        estado.setLineaActual(3);
        estado.setAccionPendienteId("cony_libre");
        estado.setReaccionPendiente(List.of(new Dialogo("Cony", "Te escuché, Cáceres.")));
        estado.setTerminarTrasReaccion(true);
        gestor.guardarRanura(estado, 1);
        gestor.guardar(estado);
        EstadoPartida leido = gestor.cargarRanura("caceres", 1);
        assertEquals("evento_cony_libre", leido.getEscenaActual());
        assertEquals(3, leido.getLineaActual());
        assertEquals("Cony", leido.getReaccionPendiente().get(0).getPersonaje());
        assertTrue(leido.isTerminarTrasReaccion());
        assertTrue(gestor.existe("caceres"));
        assertTrue(gestor.existeRanura("caceres", 1));
        assertFalse(gestor.existeRanura("caceres", 2));
    }
}
