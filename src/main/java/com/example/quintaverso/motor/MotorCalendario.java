package com.example.quintaverso.motor;

import com.example.quintaverso.HelloApplication;
import com.example.quintaverso.DialogoPartidas;
import com.example.quintaverso.MotorNarrativo;
import com.example.quintaverso.RecursosVisuales;
import com.example.quintaverso.guardado.GestorGuardado;
import com.example.quintaverso.guardado.GaleriaJairo;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.net.URL;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/** Controla el bucle: elegir actividad -> jugar escena -> avanzar -> guardar. */
public final class MotorCalendario {
    private final Stage ventana;
    private final Runnable volverMenu;
    private final GestorGuardado guardado = new GestorGuardado();
    private final GestorHistoria historia;
    private final GestorEventos eventos = new GestorEventos();

    private final String temporada;
    private EstadoPartida estado;
    private CalendarioJuego calendario;

    public MotorCalendario(Stage ventana, Runnable volverMenu) {
        this(ventana, volverMenu, "caceres");
    }

    public MotorCalendario(Stage ventana, Runnable volverMenu, String temporada) {
        this.ventana = ventana;
        this.volverMenu = volverMenu;
        this.temporada = temporada;
        this.historia = new GestorHistoria(temporada);
    }

    private boolean esJairo() { return "jairo".equals(temporada); }
    private boolean esSeba() { return "seba".equals(temporada); }

    public void iniciar() {
        boolean auto = guardado.existe(temporada);
        boolean anterior = guardado.existeAnterior(temporada);
        boolean manual = guardado.hayRanuras(temporada);
        if (auto || anterior || manual) {
            ButtonType continuar = new ButtonType("Continuar autoguardado");
            ButtonType cargar = new ButtonType("Cargar ranura manual");
            ButtonType importar = new ButtonType("Importar partida v0.5");
            ButtonType nueva = new ButtonType("Nueva partida");
            Alert elegir = new Alert(Alert.AlertType.CONFIRMATION);
            elegir.initOwner(ventana);
            elegir.setTitle("QUINTAVERSO — Partidas");
            elegir.setHeaderText("Temporada " + (esJairo() ? "Jairo" : esSeba() ? "Seba" : "Cáceres"));
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
                    estado = guardado.cargar(temporada);
                } else if (opcion.get() == cargar) {
                    Optional<EstadoPartida> seleccion = DialogoPartidas.cargar(ventana, guardado, temporada);
                    if (seleccion.isEmpty()) { volverMenu.run(); return; }
                    estado = seleccion.get();
                } else if (opcion.get() == importar) {
                    estado = guardado.cargarAnterior(temporada);
                } else {
                    Alert confirmar = new Alert(Alert.AlertType.CONFIRMATION,
                            "Se reemplazará el autoguardado al iniciar. Las ranuras manuales no se tocarán.",
                            ButtonType.YES, ButtonType.NO);
                    confirmar.initOwner(ventana);
                    if (confirmar.showAndWait().orElse(ButtonType.NO) != ButtonType.YES) {
                        volverMenu.run(); return;
                    }
                    estado = new EstadoPartida(temporada);
                }
            } catch (IOException ex) {
                HelloApplication.alerta(Alert.AlertType.ERROR, "Error al cargar", ex.getMessage());
                volverMenu.run();
                return;
            }
        } else {
            estado = new EstadoPartida(temporada);
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
            case "HITO_SEBA" -> this::completarHitoSeba;
            case "HITO_JAIRO" -> this::completarHitoJairo;
            case "ACCION" -> this::completarAccion;
            case "FINAL" -> this::completarFinal;
            case "RELECTURA_FINAL" -> this::completarRelecturaFinal;
            default -> () -> { estado.limpiarEscena(); mostrarCalendario(); };
        };
    }

    private void iniciarPrologo() {
        estado.setContextoEscena("PROLOGO");
        reproducir(esJairo() ? "jairo_prologo_inicio" : esSeba() ? "seba_prologo_inicio" : "prologo_inicio", this::completarPrologo, false);
    }
    private void completarPrologo() {
        estado.registrarEvento("prologo_visto");
        estado.limpiarEscena();
        guardarSilencioso();
        mostrarCalendario();
    }

    private void guardarManual() { DialogoPartidas.guardar(ventana, guardado, estado); }
    private void cargarManual() {
        Optional<EstadoPartida> seleccion = DialogoPartidas.cargar(ventana, guardado, temporada);
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
        if (esJairo()) {
            HitosJairo.Hito hito = HitosJairo.pendiente(estado);
            if (HitosJairo.tocaCapitulo(estado, hito)) {
                estado.setContextoEscena("HITO_JAIRO");
                estado.setAccionPendienteId(hito.id());
                reproducir(hito.escenaId(), this::completarHitoJairo, false);
                return;
            }
        }
        if (esSeba()) {
            HitosSeba.Hito hito = HitosSeba.pendiente(estado);
            if (hito != null) {
                estado.setContextoEscena("HITO_SEBA");
                estado.setAccionPendienteId(hito.id());
                reproducir(hito.escenaId(), this::completarHitoSeba, false);
                return;
            }
        }
        if (calendario.campañaTerminada()) {
            gestionarFinal();
            return;
        }
        Label titulo = etiqueta(esJairo() ? "JAIRO: ENTRE LA CONFIANZA Y LA TENTACIÓN" : esSeba() ? "SEBA: EL CAGAZO LEGENDARIO" : "CÁCERES: DOBLE TENTACIÓN", "titulo-mediano");
        Label fecha = etiqueta("DÍA " + calendario.getDia() + " / " + CalendarioJuego.DIAS_CAMPANA
                + "  ·  " + calendario.getFechaFormateada(), "subtitulo");
        Label turno = etiqueta(calendario.getNombreTurno() + "  ·  " + calendario.getTurno().getHorario(), "acento");
        VBox encabezado = new VBox(8, titulo, fecha, turno);

        VBox panelEstadisticas = new VBox(13);
        panelEstadisticas.getStyleClass().add("panel");
        panelEstadisticas.setPadding(new Insets(20));
        panelEstadisticas.getChildren().add(etiqueta("ESTADO DE LA PARTIDA", "etiqueta-caps"));
        if (esJairo()) {
            indicador(panelEstadisticas, "Confianza Mary", "confianza_mary");
            indicador(panelEstadisticas, "Inseguridad Mary", "inseguridad");
            indicador(panelEstadisticas, "Ale", "ale");
            indicador(panelEstadisticas, "Andrea", "andrea");
            indicador(panelEstadisticas, "Cami", "camila");
            indicador(panelEstadisticas, "Anto", "anto");
            indicador(panelEstadisticas, "Honestidad", "honestidad");
            indicador(panelEstadisticas, "Valentía", "valentia");
            indicador(panelEstadisticas, "Madurez", "madurez");
            indicador(panelEstadisticas, "Amistad", "amistad");
        } else if (esSeba()) {
            indicador(panelEstadisticas, "💚 Kelly", "kelly");
            indicador(panelEstadisticas, "🫶 Confianza Kelly", "confianza_kelly");
            indicador(panelEstadisticas, "💜 Amira", "amira");
            indicador(panelEstadisticas, "🩵 Javi", "javi");
            indicador(panelEstadisticas, "🧡 Feña", "fena");
            indicador(panelEstadisticas, "✨ Honestidad", "honestidad");
            indicador(panelEstadisticas, "💬 Sospecha", "sospecha");
            indicador(panelEstadisticas, "🎮 Amistad", "amistad");
            indicador(panelEstadisticas, "🌸 Estilo", "estilo");
            indicador(panelEstadisticas, "🧠 Autoestima", "autoestima");
        } else {
        indicador(panelEstadisticas, "💙 Lumi", "lumi");
        indicador(panelEstadisticas, "💗 Cony", "cony");
        indicador(panelEstadisticas, "💠 Confianza Lumi", "confianza_lumi");
        indicador(panelEstadisticas, "🌷 Confianza Cony", "confianza_cony");
        indicador(panelEstadisticas, "💬 Sospecha", "sospecha");
        indicador(panelEstadisticas, "✨ Honestidad", "honestidad");
        indicador(panelEstadisticas, "🎮 Amistad", "amistad");
        indicador(panelEstadisticas, "🧠 Claridad", "claridad");
        }
        panelEstadisticas.setMinWidth(265);

        VBox listaAcciones = new VBox(12);
        listaAcciones.setFillWidth(true);
        listaAcciones.getChildren().add(etiqueta(esJairo() ? "¿QUÉ HACE JAIRO AHORA?" : esSeba() ? "¿QUÉ HACE SEBA AHORA?" : "¿QUÉ HACE CÁCERES AHORA?", "etiqueta-caps"));
        List<GestorEventos.Accion> disponibles = eventos.disponibles(estado);
        Map<String, VBox> secciones = new LinkedHashMap<>();
        if (esJairo()) {
            String situacion = estado.ocurrioEvento("j_bloqueado") ? "Las chicas pidieron distancia." : estado.ocurrioEvento("j_pausa") ? "Mary pidió espacio." : estado.ocurrioEvento("j_pareja") ? "Mary es tu pareja." : "Jairo está soltero.";
            int d = estado.getDia();
            String etapa = d<=7 ? "Todo parece perfecto" : d<=14 ? "Las primeras grietas" : d<=21 ? "El punto de quiebre" : "Las consecuencias";
            HitosJairo.Hito pendiente = HitosJairo.pendiente(estado);
            String estadoCapitulo = pendiente == null ? "Capítulo del día completado. Queda tiempo libre." :
                    "Capítulo pendiente · " + HitosJairo.turnoCapitulo(Integer.parseInt(pendiente.id())).getNombre() + ". Puedes realizar actividades antes de la historia.";
            listaAcciones.getChildren().addAll(etiqueta(etapa, "acento"), etiqueta(situacion + " · " + estadoCapitulo, "texto-suave"));
        }
        if (esSeba()) {
            Label resumen = etiqueta(resumenSeba(), "texto-suave");
            resumen.setWrapText(true);
            Label cantidad = etiqueta("Actividades desbloqueadas: " + disponibles.size()
                    + " · Discord: " + estado.getEventos().keySet().stream()
                    .filter(k -> k.startsWith("accion:seba_discord_")).count()
                    + "/24 noches · Día " + estado.getDia(), "acento");
            listaAcciones.getChildren().addAll(resumen, cantidad);
        }
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
            if (esSeba()) {
                String grupo = grupoSeba(a.id());
                secciones.computeIfAbsent(grupo, k -> new VBox(9)).getChildren().add(linea);
            } else listaAcciones.getChildren().add(linea);
        }
        if (esSeba()) for (var entrada : secciones.entrySet()) {
            int n = entrada.getValue().getChildren().size();
            TitledPane pestana = new TitledPane(entrada.getKey() + "  (" + n + ")", entrada.getValue());
            pestana.setAnimated(false);
            pestana.setExpanded(entrada.getKey().contains("Discord") || entrada.getKey().contains("Trama"));
            pestana.setMaxWidth(Double.MAX_VALUE);
            listaAcciones.getChildren().add(pestana);
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
        Button dormir = boton(esJairo() && HitosJairo.pendiente(estado)!=null ? "🎬 Ir al capítulo del día" : "🌙 Dormir hasta mañana", this::dormir);
        navegacion.setAlignment(Pos.CENTER_LEFT);
        navegacion.getChildren().addAll(pasar, dormir, save, load, historia, menu);
        if (esJairo()) navegacion.getChildren().add(boton("Galería de finales", this::mostrarGaleriaJairo));
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

    /** Estado narrativo visible; evita que las barras cuenten una historia falsa. */
    private String resumenSeba() {
        if (estado.ocurrioEvento("seba_amira_delato_grupo"))
            return "⚠ Amira se alejó tras denunciar la mentira. Su ruta romántica está cerrada; las disculpas no compran perdón.";
        if (estado.ocurrioEvento("seba_infidelidad") && !estado.ocurrioEvento("seba_amira_confesion_valida"))
            return "💬 Hubo una infidelidad. Antes de pensar en otra relación, Seba debe asumir lo ocurrido.";
        if (estado.ocurrioEvento("seba_kelly_termino"))
            return "🌙 Kelly y Seba terminaron. Los vínculos que siguen se construyen con decisiones nuevas.";
        return "💚 Kelly es tu pareja. Las rutas románticas con otras chicas requieren resolver primero esa relación.";
    }

    private static String grupoSeba(String id) {
        if (id.startsWith("seba_discord")) return "🎧 Noches de Discord";
        if (id.startsWith("seba_kelly")) return "💚 Kelly · Relación";
        if (id.startsWith("seba_amira")) return "💜 Amira · Amistad y límites";
        if (id.startsWith("seba_javi")) return "🩵 Javi · Nuevos comienzos";
        if (id.startsWith("seba_fena")) return "🧡 Feña y Richie";
        if (id.startsWith("seba_estilo")) return "🌸 Estilo y cosplay";
        if (id.startsWith("seba_momento")) return "🎬 Trama y escenas especiales";
        return "📓 Los cabros y el diario";
    }

    private void completarHitoJairo() {
        estado.registrarEvento("jairo_hito:" + estado.getAccionPendienteId());
        estado.agregarHistorial("Día " + estado.getDia() + " — Capítulo e interludio narrativo completados");
        // Los interludios cierran la jornada narrativa; nunca regresamos a la mañana.
        estado.setTurno(CalendarioJuego.Turno.NOCHE);
        estado.limpiarEscena();
        guardarSilencioso();
        mostrarCalendario();
    }
    private void completarHitoSeba() {
        estado.registrarEvento("seba_hito:" + estado.getAccionPendienteId());
        estado.agregarHistorial("Día " + estado.getDia() + " — Evento de la historia: " + estado.getAccionPendienteId());
        estado.limpiarEscena();
        guardarSilencioso();
        mostrarCalendario();
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
        if(esJairo()) {
            HitosJairo.Hito pendiente=HitosJairo.pendiente(estado);
            if(pendiente!=null) {
                var turno=HitosJairo.turnoCapitulo(Integer.parseInt(pendiente.id()));
                if(estado.getTurno().ordinal()<turno.ordinal()) estado.setTurno(turno);
                if(guardarSilencioso()) mostrarCalendario();
                return;
            }
        }
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
        if (esJairo()) try { new GaleriaJairo().desbloquear(estado.getFinalObtenido()); }
        catch (IOException ex) { HelloApplication.alerta(Alert.AlertType.WARNING, "Galería", "No se pudo guardar el desbloqueo: " + ex.getMessage()); }
        Escena epilogo = historia.obtener(estado.getFinalObtenido());
        String rutaCG = RecursosVisuales.cg(epilogo);
        URL url = rutaCG == null ? null : getClass().getResource(rutaCG);
        Label titulo = etiqueta("FIN DE TEMPORADA", "logo");
        Label subtitulo = etiqueta("Has llegado al final de los treinta días.", "subtitulo");
        Label finalId = etiqueta("Final desbloqueado: " + epilogo.getTitulo(), "titulo-mediano");
        Label puntuacion = etiqueta(esJairo() ? String.format("Confianza %d · Honestidad %d · Madurez %d · Valentía %d", estado.obtenerEstadistica("confianza_mary"), estado.obtenerEstadistica("honestidad"), estado.obtenerEstadistica("madurez"), estado.obtenerEstadistica("valentia")) : esSeba() ? String.format(
                "Kelly %d · Amira %d · Javi %d · Feña %d · Estilo %d",
                estado.obtenerEstadistica("kelly"), estado.obtenerEstadistica("amira"),
                estado.obtenerEstadistica("javi"), estado.obtenerEstadistica("fena"),
                estado.obtenerEstadistica("estilo")) : String.format(
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
            estado = new EstadoPartida(temporada);
            calendario = new CalendarioJuego(estado);
            guardarSilencioso();
            iniciarPrologo();
        });
        Button saveFinal = boton("💾 Guardar", this::guardarManual);
        Button loadFinal = boton("📂 Cargar", this::cargarManual);
        Button menu = boton("Volver al menú", this::regresarAlMenu);

        FlowPane acciones = new FlowPane(10, 10, repetirFinal, reiniciar, saveFinal, loadFinal, menu);
        if (url != null) {
            Button verIlustracion = boton("✦ Ver splash completo", () -> mostrarSplashCompleto(url));
            verIlustracion.getStyleClass().add("boton-primario");
            acciones.getChildren().add(0, verIlustracion);
        }
        if (esJairo()) acciones.getChildren().add(boton("Galería de finales", this::mostrarGaleriaJairo));
        acciones.setAlignment(Pos.CENTER);
        VBox panel = new VBox(13, titulo, subtitulo, finalId, puntuacion, acciones);
        panel.setAlignment(Pos.CENTER);
        panel.setPadding(new Insets(24));
        panel.setMaxWidth(1040);
        panel.getStyleClass().add("panel-final-visual");

        StackPane lienzo = new StackPane();
        lienzo.getStyleClass().add("visor-final");
        if (url != null) {
            Image cg = new Image(url.toExternalForm(), false);
            ImageView arteCompleto = new ImageView(cg);
            arteCompleto.setSmooth(true);
            arteCompleto.setPreserveRatio(true);
            arteCompleto.setMouseTransparent(true);
            arteCompleto.fitWidthProperty().bind(lienzo.widthProperty());
            arteCompleto.fitHeightProperty().bind(lienzo.heightProperty());
            lienzo.getChildren().add(arteCompleto);
        }
        lienzo.getChildren().add(panel);
        StackPane.setAlignment(panel, Pos.BOTTOM_CENTER);
        StackPane.setMargin(panel, new Insets(20, 18, 20, 18));
        HelloApplication.cambiarContenido(ventana, lienzo);
    }

    private void mostrarGaleriaJairo() {
        try {
            var ids = new GaleriaJairo().leer();
            Dialog<ButtonType> dialogo = new Dialog<>();
            dialogo.initOwner(ventana);
            dialogo.setTitle("Jairo · Galería de finales");
            dialogo.setHeaderText(ids.size() + " / 12 finales desbloqueados");
            HelloApplication.tematizarDialogo(dialogo);
            dialogo.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
            VBox lista = new VBox(8);
            for (String color : FinalesJairo.COLORES) {
                String id = "final_jairo_" + color;
                boolean visto = ids.contains(id);
                String ruta = RecursosVisuales.cg(historia.obtener(id));
                URL url = getClass().getResource(ruta);
                Button b = boton(visto ? historia.obtener(id).getTitulo() : "Bloqueado · " + color.toUpperCase(java.util.Locale.ROOT), () -> { if (url != null) mostrarSplashCompleto(url); });
                b.setDisable(!visto || url == null);
                b.setMaxWidth(Double.MAX_VALUE);
                lista.getChildren().add(b);
            }
            ScrollPane scroll = new ScrollPane(lista);
            scroll.setFitToWidth(true);
            scroll.setPrefViewportHeight(450);
            scroll.setPrefViewportWidth(450);
            dialogo.getDialogPane().setContent(scroll);
            dialogo.showAndWait();
        } catch (IOException ex) { HelloApplication.alerta(Alert.AlertType.WARNING, "Galería", ex.getMessage()); }
    }

    /** Abre la CG original sin recortes, texto encima ni botones de decisión. */
    private void mostrarSplashCompleto(URL url) {
        Dialog<ButtonType> ventanaSplash = new Dialog<>();
        ventanaSplash.initOwner(ventana);
        ventanaSplash.setTitle("QUINTAVERSO · Ilustración del final");
        ventanaSplash.setResizable(true);
        ventanaSplash.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        HelloApplication.tematizarDialogo(ventanaSplash);
        Image imagen = new Image(url.toExternalForm(), false);
        ImageView ilustracion = new ImageView(imagen);
        ilustracion.setSmooth(true);
        ilustracion.setPreserveRatio(true);
        ilustracion.setFitWidth(960);
        ilustracion.setFitHeight(540);
        StackPane contenido = new StackPane(ilustracion);
        contenido.setPadding(new Insets(8));
        contenido.setStyle("-fx-background-color: #101321;");
        ventanaSplash.getDialogPane().setContent(contenido);
        ventanaSplash.showAndWait();
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
