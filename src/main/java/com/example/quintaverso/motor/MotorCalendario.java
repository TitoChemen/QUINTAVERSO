package com.example.quintaverso.motor;

import com.example.quintaverso.HelloApplication;
import com.example.quintaverso.MotorNarrativo;
import com.example.quintaverso.RecursosVisuales;
import com.example.quintaverso.guardado.GestorGuardado;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import java.net.URL;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/** Controla el bucle: elegir actividad -> jugar escena -> avanzar -> guardar. */
public final class MotorCalendario {
    private final Stage ventana;
    private final Runnable volverMenu;
    private final GestorGuardado guardado = new GestorGuardado();
    private final GestorHistoria historia = new GestorHistoria();
    private final GestorEventos eventos = new GestorEventos();

    private EstadoPartida estado;
    private CalendarioJuego calendario;

    public MotorCalendario(Stage ventana, Runnable volverMenu) {
        this.ventana = ventana;
        this.volverMenu = volverMenu;
    }

    public void iniciar() {
        if (guardado.existe("caceres")) {
            ButtonType continuar = new ButtonType("Continuar partida", ButtonBar.ButtonData.YES);
            ButtonType nueva = new ButtonType("Nueva partida", ButtonBar.ButtonData.NO);
            Alert elegir = new Alert(Alert.AlertType.CONFIRMATION,
                    "Existe una partida guardada. ¿Qué quieres hacer?",
                    continuar, nueva, ButtonType.CANCEL);
            elegir.setTitle("QUINTAVERSO — Partida guardada");
            elegir.setHeaderText("Temporada Cáceres");
            Optional<ButtonType> opcion = elegir.showAndWait();
            if (opcion.isEmpty() || opcion.get() == ButtonType.CANCEL) {
                volverMenu.run();
                return;
            }
            if (opcion.get() == continuar) {
                try {
                    estado = guardado.cargar("caceres");
                } catch (IOException e) {
                    HelloApplication.alerta(Alert.AlertType.ERROR,
                            "No se pudo cargar", e.getMessage());
                    volverMenu.run();
                    return;
                }
            } else {
                Alert confirmar = new Alert(Alert.AlertType.CONFIRMATION,
                        "La partida anterior será reemplazada al terminar el prólogo.",
                        ButtonType.YES, ButtonType.NO);
                confirmar.setHeaderText("¿Empezar desde cero?");
                if (confirmar.showAndWait().orElse(ButtonType.NO) != ButtonType.YES) {
                    volverMenu.run();
                    return;
                }
                estado = new EstadoPartida("caceres");
            }
        } else {
            estado = new EstadoPartida("caceres");
        }
        calendario = new CalendarioJuego(estado);
        if (!estado.ocurrioEvento("prologo_visto")) {
            reproducir("prologo_inicio", () -> {
                estado.registrarEvento("prologo_visto");
                guardarSilencioso();
                mostrarCalendario();
            });
        } else {
            mostrarCalendario();
        }
    }

    private void mostrarCalendario() {
        if (calendario.campañaTerminada()) {
            gestionarFinal();
            return;
        }
        Label titulo = etiqueta("CÁCERES: DOBLE TENTACIÓN", "titulo-mediano");
        Label fecha = etiqueta("DÍA " + calendario.getDia() + " / " + CalendarioJuego.DIAS_CAMPANA
                + "  ·  " + calendario.getFechaFormateada(), "subtitulo");
        Label turno = etiqueta(calendario.getNombreTurno() + "  ·  " + calendario.getTurno().getHorario(), "acento");
        VBox encabezado = new VBox(8, titulo, fecha, turno);

        VBox panelEstadisticas = new VBox(13);
        panelEstadisticas.getStyleClass().add("panel");
        panelEstadisticas.setPadding(new Insets(20));
        panelEstadisticas.getChildren().add(etiqueta("ESTADO DE LA PARTIDA", "etiqueta-caps"));
        indicador(panelEstadisticas, "💙 Lumi", "lumi");
        indicador(panelEstadisticas, "💗 Cony", "cony");
        indicador(panelEstadisticas, "💠 Confianza Lumi", "confianza_lumi");
        indicador(panelEstadisticas, "🌷 Confianza Cony", "confianza_cony");
        indicador(panelEstadisticas, "💬 Sospecha", "sospecha");
        indicador(panelEstadisticas, "✨ Honestidad", "honestidad");
        indicador(panelEstadisticas, "🎮 Amistad", "amistad");
        indicador(panelEstadisticas, "🧠 Claridad", "claridad");
        panelEstadisticas.setMinWidth(265);

        VBox listaAcciones = new VBox(12);
        listaAcciones.setFillWidth(true);
        listaAcciones.getChildren().add(etiqueta("¿QUÉ HACE CÁCERES AHORA?", "etiqueta-caps"));
        List<GestorEventos.Accion> disponibles = eventos.disponibles(estado);
        for (GestorEventos.Accion a : disponibles) {
            Button boton = new Button(a.titulo());
            boton.getStyleClass().add("boton-evento");
            boton.setMaxWidth(Double.MAX_VALUE);
            boton.setPrefHeight(48);
            boton.setOnAction(e -> elegirAccion(a));
            Label descripcion = etiqueta(a.descripcion() + "  ·  " + a.lugar(), "texto-suave");
            descripcion.setWrapText(true);
            VBox linea = new VBox(4, boton, descripcion);
            linea.setFillWidth(true);
            listaAcciones.getChildren().add(linea);
        }
        HBox paneles = new HBox(23, panelEstadisticas, listaAcciones);
        paneles.setAlignment(Pos.TOP_LEFT);
        HBox.setHgrow(listaAcciones, Priority.ALWAYS);

        Label consejo = etiqueta("Una actividad consume un turno. Los eventos especiales se juegan una sola vez; " +
                "las conversaciones nuevas se consumen en orden. Puedes pasar turno o dormir sin bloquear el calendario.", "texto-suave");
        consejo.setWrapText(true);

        FlowPane navegacion = new FlowPane(9, 9);
        Button save = boton("💾 Guardar", () -> {
            if (guardarSilencioso())
                HelloApplication.alerta(Alert.AlertType.INFORMATION, "Guardado", "Partida guardada correctamente.");
        });
        Button historia = boton("📜 Historial", this::mostrarHistorial);
        Button menu = boton("← Menú", () -> {
            if (guardarSilencioso()) volverMenu.run();
        });
        Button pasar = boton("⏭ Pasar turno", this::pasarTurno);
        Button dormir = boton("🌙 Dormir hasta mañana", this::dormir);
        navegacion.setAlignment(Pos.CENTER_LEFT);
        navegacion.getChildren().addAll(pasar, dormir, save, historia, menu);
        navegacion.setStyle("-fx-padding: 12 0 12 0;");

        VBox contenido = new VBox(23, encabezado, paneles, consejo, navegacion);
        // El ancho mínimo no fuerza a recortar el calendario en pantallas pequeñas.
        navegacion.setMaxWidth(920);
        contenido.setMaxWidth(980);
        contenido.setPadding(new Insets(36, 35, 50, 35));
        VBox root = new VBox(contenido);
        root.setAlignment(Pos.TOP_CENTER);
        ScrollPane scroll = new ScrollPane(root);
        scroll.setFitToWidth(true);
        scroll.getStyleClass().add("scroll-fondo");
        Scene scene = new Scene(scroll, 1100, 720);
        HelloApplication.agregarEstilo(scene);
        ventana.setScene(scene);
    }

    private void elegirAccion(GestorEventos.Accion accion) {
        String fecha = "Día " + estado.getDia() + " / " + estado.getTurno().getNombre();
        reproducir(accion.escenaId(), () -> {
            if (accion.unica()) estado.registrarEvento("accion:" + accion.id());
            else estado.registrarActividad(accion.id());
            estado.registrarEvento("escena_leida:" + accion.escenaId());
            estado.agregarHistorial(fecha + " — " + accion.titulo());
            calendario.avanzarTurno();
            guardarSilencioso();
            mostrarCalendario();
        });
    }

    /** Nunca deja al jugador bloqueado: también se puede pasar turno o ir a mañana. */
    private void pasarTurno() {
        estado.agregarHistorial("Día " + estado.getDia() + " / " + estado.getTurno().getNombre() +
                " — Descansó durante este turno");
        calendario.avanzarTurno();
        if (guardarSilencioso()) mostrarCalendario();
    }

    private void dormir() {
        int desde = estado.getDia();
        estado.agregarHistorial("Día " + desde + " — Decidió dormir hasta la mañana siguiente");
        calendario.dormirHastaManana();
        if (guardarSilencioso()) mostrarCalendario();
    }

    private void reproducir(String id, Runnable alTerminar) {
        new MotorNarrativo(ventana, estado, historia, alTerminar).iniciar(id);
    }

    private void gestionarFinal() {
        if (estado.getFinalObtenido() == null) {
            String id = historia.calcularFinal(estado);
            estado.setFinalObtenido(id);
            guardarSilencioso();
        }
        if (!estado.ocurrioEvento("final_visto")) {
            reproducir(estado.getFinalObtenido(), () -> {
                estado.registrarEvento("final_visto");
                estado.agregarHistorial("Final obtenido: " + estado.getFinalObtenido());
                guardarSilencioso();
                mostrarPantallaFinal();
            });
        } else mostrarPantallaFinal();
    }

    private void mostrarPantallaFinal() {
        Escena epilogo = historia.obtener(estado.getFinalObtenido());
        Label titulo = etiqueta("FIN DE TEMPORADA", "logo");
        Label subtitulo = etiqueta("Has llegado al final de los treinta días.", "subtitulo");
        Label finalId = etiqueta("Final desbloqueado: " + epilogo.getTitulo(), "titulo-mediano");
        Label puntuacion = etiqueta(String.format(
                "Lumi %d  ·  Cony %d  ·  Sospecha %d  ·  Honestidad %d  ·  Amistad %d",
                estado.obtenerEstadistica("lumi"), estado.obtenerEstadistica("cony"),
                estado.obtenerEstadistica("sospecha"), estado.obtenerEstadistica("honestidad"),
                estado.obtenerEstadistica("amistad")), "texto-suave");
        Button repetirFinal = boton("Volver a leer el final", () ->
                reproducir(estado.getFinalObtenido(), this::mostrarPantallaFinal));
        Button reiniciar = boton("Nueva partida", () -> {
            Alert confirmar = new Alert(Alert.AlertType.CONFIRMATION,
                    "Esto reemplazará el guardado actual. ¿Seguro?", ButtonType.YES, ButtonType.NO);
            if (confirmar.showAndWait().orElse(ButtonType.NO) != ButtonType.YES) return;
            estado = new EstadoPartida("caceres");
            calendario = new CalendarioJuego(estado);
            guardarSilencioso();
            reproducir("prologo_inicio", () -> {
                estado.registrarEvento("prologo_visto");
                guardarSilencioso();
                mostrarCalendario();
            });
        });
        Button menu = boton("Volver al menú", volverMenu);

        HBox acciones = new HBox(12, repetirFinal, reiniciar, menu);
        acciones.setAlignment(Pos.CENTER);
        acciones.setFillHeight(true);
        VBox panel = new VBox(13, titulo, subtitulo, finalId, puntuacion, acciones);
        panel.setAlignment(Pos.CENTER);
        panel.setPadding(new Insets(24));
        panel.setMaxWidth(1040);
        panel.getStyleClass().add("panel-final-visual");

        StackPane lienzo = new StackPane();
        String rutaCG = RecursosVisuales.cg(epilogo);
        URL url = rutaCG == null ? null : getClass().getResource(rutaCG);
        if (url != null) {
            Image cg = new Image(url.toExternalForm(), false);
            BackgroundImage imagen = new BackgroundImage(cg, BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
                    new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO,
                            false, false, false, true));
            lienzo.setBackground(new Background(imagen));
        }
        lienzo.getChildren().add(panel);
        StackPane.setAlignment(panel, Pos.BOTTOM_CENTER);
        StackPane.setMargin(panel, new Insets(20, 18, 20, 18));
        Scene scene = new Scene(lienzo, 1100, 720);
        HelloApplication.agregarEstilo(scene);
        ventana.setScene(scene);
    }

    private void mostrarHistorial() {
        String lista = estado.getHistorial().isEmpty() ? "Todavía no hay actividades." :
                String.join("\n", estado.getHistorial());
        TextArea area = new TextArea(lista);
        area.setEditable(false);
        area.setWrapText(true);
        area.setPrefRowCount(17);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Historial");
        alert.setHeaderText("Tus decisiones en QUINTAVERSO");
        alert.getDialogPane().setContent(area);
        alert.showAndWait();
    }

    private void indicador(VBox destino, String nombre, String clave) {
        int valor = estado.obtenerEstadistica(clave);
        Label lbl = etiqueta(nombre + "  " + valor + "/100", "texto-suave");
        ProgressBar barra = new ProgressBar(valor / 100.0);
        barra.setMaxWidth(Double.MAX_VALUE);
        barra.getStyleClass().add("progreso");
        destino.getChildren().addAll(lbl, barra);
    }

    private static Label etiqueta(String texto, String clase) {
        Label l = new Label(texto);
        l.getStyleClass().add(clase);
        l.setWrapText(true);
        return l;
    }

    private static Button boton(String texto, Runnable alPulsar) {
        Button b = new Button(texto);
        b.getStyleClass().add("boton-secundario");
        b.setOnAction(e -> alPulsar.run());
        return b;
    }

    private boolean guardarSilencioso() {
        try {
            guardado.guardar(estado);
            return true;
        } catch (IOException ex) {
            HelloApplication.alerta(Alert.AlertType.ERROR,
                    "No se pudo guardar", ex.getMessage());
            return false;
        }
    }
}
