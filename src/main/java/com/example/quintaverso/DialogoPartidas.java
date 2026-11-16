package com.example.quintaverso;

import com.example.quintaverso.guardado.GestorGuardado;
import com.example.quintaverso.motor.EstadoPartida;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Selector de tres ranuras para usar desde el calendario o en mitad de cualquier diálogo. */
public final class DialogoPartidas {
    private DialogoPartidas() { }

    public static void guardar(Stage owner, GestorGuardado guardado, EstadoPartida estado) {
        Optional<Integer> elegida = elegir(owner, guardado, estado.getTemporada(), true);
        if (elegida.isEmpty()) return;
        int ranura = elegida.get();
        if (guardado.existeRanura(estado.getTemporada(), ranura)) {
            Alert confirmar = new Alert(Alert.AlertType.CONFIRMATION,
                    "¿Reemplazar el contenido de la ranura " + ranura + "?",
                    ButtonType.YES, ButtonType.NO);
            confirmar.initOwner(owner);
            confirmar.setHeaderText("Confirmar sobrescritura");
            if (confirmar.showAndWait().orElse(ButtonType.NO) != ButtonType.YES) return;
        }
        try {
            guardado.guardarRanura(estado, ranura);
            HelloApplication.alerta(Alert.AlertType.INFORMATION, "Partida guardada",
                    "Guardado manual en la ranura " + ranura + ". También existe autoguardado independiente.");
        } catch (IOException ex) {
            HelloApplication.alerta(Alert.AlertType.ERROR, "Error de guardado", ex.getMessage());
        }
    }

    public static Optional<EstadoPartida> cargar(Stage owner, GestorGuardado guardado, String temporada) {
        Optional<Integer> elegida = elegir(owner, guardado, temporada, false);
        if (elegida.isEmpty()) return Optional.empty();
        Alert confirmar = new Alert(Alert.AlertType.CONFIRMATION,
                "Se volverá al progreso guardado en la ranura " + elegida.get() + ".",
                ButtonType.YES, ButtonType.NO);
        confirmar.initOwner(owner);
        confirmar.setHeaderText("¿Cargar esta partida?");
        if (confirmar.showAndWait().orElse(ButtonType.NO) != ButtonType.YES)
            return Optional.empty();
        try {
            return Optional.of(guardado.cargarRanura(temporada, elegida.get()));
        } catch (IOException ex) {
            HelloApplication.alerta(Alert.AlertType.ERROR, "Error al cargar", ex.getMessage());
            return Optional.empty();
        }
    }

    private static Optional<Integer> elegir(Stage owner, GestorGuardado guardado,
                                             String temporada, boolean paraGuardar) {
        List<Integer> ranuras = new ArrayList<>();
        for (int n = 1; n <= GestorGuardado.RANURAS; n++) {
            if (paraGuardar || guardado.existeRanura(temporada, n)) ranuras.add(n);
        }
        if (ranuras.isEmpty()) {
            HelloApplication.alerta(Alert.AlertType.INFORMATION, "Sin partidas",
                    "Todavía no hay guardados manuales. Utiliza 'Guardar partida' durante el juego.");
            return Optional.empty();
        }
        ChoiceDialog<Integer> dialogo = new ChoiceDialog<>(ranuras.get(0), ranuras);
        dialogo.initOwner(owner);
        dialogo.setTitle(paraGuardar ? "Guardar partida" : "Cargar partida");
        dialogo.setHeaderText(paraGuardar ? "Elige dónde asegurar tu progreso" : "Elige una partida para continuar");
        StringBuilder descripciones = new StringBuilder();
        for (int n : ranuras) descripciones.append(guardado.descripcion(temporada, n)).append("\n");
        dialogo.setContentText(descripciones + "\nNúmero de ranura:");
        return dialogo.showAndWait();
    }
}
