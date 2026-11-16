package com.example.quintaverso.motor;

import com.example.quintaverso.HelloApplication;
import com.example.quintaverso.DialogoPartidas;
import com.example.quintaverso.MotorNarrativo;
import com.example.quintaverso.RecursosVisuales;
import com.example.quintaverso.guardado.GestorGuardado;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
        boolean auto = guardado.existe("caceres");
        boolean anterior = guardado.existeAnterior("caceres");
        boolean manual = guardado.hayRanuras("caceres");
        if (auto || anterior || manual) {
            ButtonType continuar = new ButtonType("Continuar autoguardado");
            ButtonType cargar = new ButtonType("Cargar ranura manual");
            ButtonType importar = new ButtonType("Importar partida v0.5");
            ButtonType nueva = new ButtonType("Nueva partida");
            Alert elegir = new Alert(Alert.AlertType.CONFIRMATION);
            elegir.initOwner(ventana);
            elegir.setTitle("QUINTAVERSO — Partidas");
            elegir.setHeaderText("Temporada Cáceres");
            elegir.setContentText("Puedes continuar, cargar una ranura o comenzar de cero.");
            // Las alertas CONFIRMATION ya traen OK y CANCEL. Limpiarlos antes
            // de incorporar acciones propias evita botones duplicados en JavaFX.
            elegir.getButtonTypes().clear();
            if (auto) elegir.getButtonTypes().add(continuar);
            if (manual) elegir.getButtonTypes().add(cargar);
            if (anterior) elegir.getButtonTypes().add(importar);
            elegir.getButtonTypes().addAll(nueva, ButtonType.CANCEL);
            Optional<ButtonType> opcion = elegir.showAndWait();
            if (opcion.isEmpty() || opcion.get() == ButtonType.CANCEL) {
                volverMenu.run();
                return;
            }
            try {
                if (opcion.get() == continuar) {
                    estado = guardado.cargar("caceres");
                } else if (opcion.get() == cargar) {
                    Optional<EstadoPartida> seleccion = DialogoPartidas.cargar(ventana, guardado, "caceres");
                    if (seleccion.isEmpty()) { volverMenu.run(); return; }
                    estado = seleccion.get();
                } else if (opcion.get() == importar) {
                    estado = guardado.cargarAnterior("caceres");
                } else {
                    Alert confirmar = new Alert(Alert.AlertType.CONFIRMATION,
                            "Se reemplazará el autoguardado al iniciar. Las ranuras manuales no se tocarán.",
                            ButtonType.YES, ButtonType.NO);
                    confirmar.initOwner(ventana);
                    if (confirmar.showAndWait().orElse(ButtonType.NO) != ButtonType.YES) {
                        volverMenu.run(); return;
                    }
                    estado = new EstadoPartida("caceres");
                }
            } catch (IOException ex) {
                HelloApplication.alerta(Alert.AlertType.ERROR, "Error al cargar", ex.getMessage());
                volverMenu.run();
                return;
            }
        } else {
            estado = new EstadoPartida("caceres");
        }
        mostrarEstadoCargado();
    }

    private void mostrarEstadoCargado() {
        calendario = new CalendarioJuego(estado);
        if (estado.getEscenaActual() != null && estado.getContextoEscena() != null) {
            reproducir(estado.getEscenaActual(), callbackPendiente(), true);
        } else if (!estado.ocurrioEvento("prologo_visto")) {
            iniciarPrologo();
        } else {
            mostrarCalendario();
        }
    }

    private Runnable callbackPendiente() {
        return switch (estado.getContextoEscena()) {
            case "PROLOGO" -> this::completarPrologo;
            case "ACCION" -> this::completarAccion;
            case "FINAL" -> this::completarFinal;
            case "RELECTURA_FINAL" -> this::completarRelecturaFinal;
            default -> () -> { estado.limpiarEscena(); mostrarCalendario(); };
        };
    }

    private void iniciarPrologo() {
        estado.setContextoEscena("PROLOGO");
        reproducir("prologo_inicio", this::completarPrologo, false);
    }
    private void completarPrologo() {
        estado.registrarEvento("prologo_visto");
        estado.limpiarEscena();
        guardarSilencioso();
        mostrarCalendario();
    }

    private void guardarManual() { DialogoPartidas.guardar(ventana, guardado, estado); }
    private void cargarManual() {
        Optional<EstadoPartida> seleccion = DialogoPartidas.cargar(ventana, guardado, "caceres");
        if (seleccion.isEmpty()) return;
        estado = seleccion.get();
        // Ahora el autoguardado reflejará lo que se cargó, sin modificar ninguna ranura.
        guardarSilencioso();
        mostrarEstadoCargado();
    }
    private void regresarAlMenu() {
        if (guardarSilencioso()) volverMenu.run();
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
        // En ventanas angostas la lista pasa debajo del panel; no se sale del borde.
        FlowPane paneles = new FlowPane(18, 18, panelEstadisticas, listaAcciones);
        paneles.setAlignment(Pos.TOP_LEFT);
        listaAcciones.setPrefWidth(570);
        listaAcciones.setMinWidth(300);
        paneles.prefWrapLengthProperty().bind(ventana.widthProperty().subtract(95));

        Label consejo = etiqueta("Una actividad consume un turno. Los eventos especiales se juegan una sola vez; " +
                "las conversaciones nuevas se consumen en orden. Puedes pasar turno o dormir sin bloquear el calendario.", "texto-suave");
        consejo.setWrapText(true);

        FlowPane navegacion = new FlowPane(9, 9);
        Button save = boton("💾 Guardar partida", this::guardarManual);
        Button load = boton("📂 Cargar partida", this::cargarManual);
        Button historia = boton("📜 Historial", this::mostrarHistorial);
        Button menu = boton("← Menú", this::regresarAlMenu);
        Button pasar = boton("⏭ Pasar turno", this::pasarTurno);
        Button dormir = boton("🌙 Dormir hasta mañana", this::dormir);
        navegacion.setAlignment(Pos.CENTER_LEFT);
        navegacion.getChildren().addAll(pasar, dormir, save, load, historia, menu);
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
        HelloApplication.cambiarContenido(ventana, scroll);
    }

    private void elegirAccion(GestorEventos.Accion accion) {
        estado.setContextoEscena("ACCION");
        estado.setAccionPendienteId(accion.id());
        estado.setAccionPendienteTitulo(accion.titulo());
        estado.setAccionPendienteUnica(accion.unica());
        reproducir(accion.escenaId(), this::completarAccion, false);
    }

    private void completarAccion() {
        String fecha = "Día " + estado.getDia() + " / " + estado.getTurno().getNombre();
        if (estado.isAccionPendienteUnica())
            estado.registrarEvento("accion:" + estado.getAccionPendienteId());
        else
            estado.registrarActividad(estado.getAccionPendienteId());
        // El motor ya llegó al final de todas las escenas encadenadas.
        estado.registrarEvento("escena_leida:" + estado.getEscenaActual());
        estado.agregarHistorial(fecha + " — " + estado.getAccionPendienteTitulo());
        calendario.avanzarTurno();
        estado.limpiarEscena();
        guardarSilencioso();
        mostrarCalendario();
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

    private void reproducir(String id, Runnable alTerminar, boolean reanudar) {
        new MotorNarrativo(ventana, estado, historia, alTerminar,
                this::guardarSilencioso, this::guardarManual,
                this::cargarManual, this::regresarAlMenu).iniciar(id, reanudar);
    }

    private void gestionarFinal() {
        if (estado.getFinalObtenido() == null) {
            String id = historia.calcularFinal(estado);
            estado.setFinalObtenido(id);
            guardarSilencioso();
        }
        if (!estado.ocurrioEvento("final_visto")) {
            estado.setContextoEscena("FINAL");
            reproducir(estado.getFinalObtenido(), this::completarFinal, false);
        } else mostrarPantallaFinal();
    }

    private void completarFinal() {
        estado.registrarEvento("final_visto");
        estado.agregarHistorial("Final obtenido: " + estado.getFinalObtenido());
        estado.limpiarEscena();
        guardarSilencioso();
        mostrarPantallaFinal();
    }
    private void completarRelecturaFinal() {
        estado.limpiarEscena();
        guardarSilencioso();
        mostrarPantallaFinal();
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
        Button repetirFinal = boton("Volver a leer el final", () -> {
            estado.setContextoEscena("RELECTURA_FINAL");
            reproducir(estado.getFinalObtenido(), this::completarRelecturaFinal, false);
        });
        Button reiniciar = boton("Nueva partida", () -> {
            Alert confirmar = new Alert(Alert.AlertType.CONFIRMATION,
                    "Esto reemplazará el guardado actual. ¿Seguro?", ButtonType.YES, ButtonType.NO);
            if (confirmar.showAndWait().orElse(ButtonType.NO) != ButtonType.YES) return;
            estado = new EstadoPartida("caceres");
            calendario = new CalendarioJuego(estado);
            guardarSilencioso();
            iniciarPrologo();
        });
        Button saveFinal = boton("💾 Guardar", this::guardarManual);
        Button loadFinal = boton("📂 Cargar", this::cargarManual);
        Button menu = boton("Volver al menú", this::regresarAlMenu);

        FlowPane acciones = new FlowPane(10, 10, repetirFinal, reiniciar, saveFinal, loadFinal, menu);
        acciones.setAlignment(Pos.CENTER);
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
        HelloApplication.cambiarContenido(ventana, lienzo);
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
