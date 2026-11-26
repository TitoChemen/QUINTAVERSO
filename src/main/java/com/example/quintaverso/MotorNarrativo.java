package com.example.quintaverso;

import com.example.quintaverso.motor.Decision;
import com.example.quintaverso.motor.Dialogo;
import com.example.quintaverso.motor.Escena;
import com.example.quintaverso.motor.EstadoPartida;
import com.example.quintaverso.motor.GestorHistoria;
import com.example.quintaverso.motor.ReaccionesNarrativas;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Lector de escenas v0.6: UI contenida, reacción tras elegir y guardado desde cualquier diálogo. */
public final class MotorNarrativo {
    private final Stage ventana;
    private final EstadoPartida estado;
    private final GestorHistoria historia;
    private final Runnable alTerminar;
    private final Runnable autoguardar;
    private final Runnable guardarManual;
    private final Runnable cargarManual;
    private final Runnable volverMenu;

    private Escena escena;
    private int lineaActual;
    private final Label titulo = new Label();
    private final Label lugar = new Label();
    private final Label personaje = new Label();
    private final Label texto = new Label();
    private final Label contador = new Label();
    private final VBox opciones = new VBox(9);
    private final StackPane lienzo = new StackPane();
    // Capa visual independiente de los estilos CSS del contenedor.
    private final ImageView fondoVisual = new ImageView();
    private final ImageView retrato = new ImageView();
    // En la temporada Seba, el protagonista también reacciona a quien habla.
    private final ImageView retratoProtagonista = new ImageView();
    private final StackPane marcoProtagonista = new StackPane(retratoProtagonista);
    private final StackPane marcoRetrato = new StackPane(retrato);
    private final ScrollPane scrollDialogo = new ScrollPane();
    private final Rectangle veloAmbiente = new Rectangle();
    private final Button botonEncuadre = new Button();
    private EncuadreRetratos.Plano planoRetratos = EncuadreRetratos.Plano.AUTOMATICO;
    private String rutaRetratoActual;
    private String rutaProtagonistaActual;

    private static final Map<String, Image> CACHE = new LinkedHashMap<>(24, .75f, true) {
        @Override protected boolean removeEldestEntry(Map.Entry<String, Image> eldest) {
            return size() > 18;
        }
    };

    public MotorNarrativo(Stage ventana, EstadoPartida estado, GestorHistoria historia,
                          Runnable alTerminar, Runnable autoguardar, Runnable guardarManual,
                          Runnable cargarManual, Runnable volverMenu) {
        this.ventana = ventana;
        this.estado = estado;
        this.historia = historia;
        this.alTerminar = alTerminar;
        this.autoguardar = autoguardar;
        this.guardarManual = guardarManual;
        this.cargarManual = cargarManual;
        this.volverMenu = volverMenu;
    }

    public void iniciar(String escenaId, boolean reanudar) {
        titulo.getStyleClass().add("titulo-mediano");
        titulo.setWrapText(true);
        lugar.getStyleClass().add("texto-suave");
        lugar.setWrapText(true);
        personaje.getStyleClass().add("personaje");
        texto.getStyleClass().add("dialogo");
        texto.setWrapText(true);
        texto.setMaxWidth(Double.MAX_VALUE);
        contador.getStyleClass().add("texto-suave");

        VBox cuadro = new VBox(10, personaje, texto, contador);
        cuadro.getStyleClass().add("panel-dialogo");
        cuadro.setPadding(new Insets(19, 25, 17, 25));
        cuadro.setFillWidth(true);
        cuadro.setMinWidth(0);

        opciones.setFillWidth(true);
        opciones.setMinWidth(0);
        VBox cajaInferior = new VBox(10, cuadro, opciones);
        cajaInferior.setMinWidth(0);
        cajaInferior.setMaxWidth(1030);
        cajaInferior.setFillWidth(true);
        cajaInferior.setPadding(new Insets(10, 18, 12, 18));

        StackPane contenedorInferior = new StackPane(cajaInferior);
        contenedorInferior.setAlignment(Pos.CENTER);
        scrollDialogo.setContent(contenedorInferior);
        scrollDialogo.setFitToWidth(true);
        scrollDialogo.setPannable(true);
        scrollDialogo.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollDialogo.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollDialogo.setPrefHeight(270);
        scrollDialogo.setMinHeight(100);
        scrollDialogo.maxHeightProperty().bind(lienzo.heightProperty().multiply(.48));
        scrollDialogo.getStyleClass().add("scroll-dialogos");

        FlowPane barra = new FlowPane(8, 6);
        barra.setAlignment(Pos.CENTER_LEFT);
        Button guardar = botonBarra("💾 Guardar partida", guardarManual);
        Button cargar = botonBarra("📂 Cargar partida", cargarManual);
        Button menu = botonBarra("← Menú", volverMenu);
        botonEncuadre.getStyleClass().addAll("boton-secundario", "boton-compacto", "boton-encuadre");
        botonEncuadre.setTooltip(new Tooltip("Alterna automático, acercado y cuerpo completo."));
        botonEncuadre.setOnAction(e -> {
            planoRetratos = planoRetratos.siguiente();
            actualizarBotonEncuadre();
            aplicarEncuadreRetratos();
        });
        actualizarBotonEncuadre();
        barra.getChildren().addAll(guardar, cargar, botonEncuadre, menu);

        VBox cabecera = new VBox(5, titulo, lugar, barra);
        cabecera.getStyleClass().add("panel-encabezado-visual");
        cabecera.setPadding(new Insets(12, 18, 10, 18));
        cabecera.setMaxWidth(1050);
        cabecera.setMinWidth(0);

        retratoProtagonista.setPreserveRatio(true);
        retratoProtagonista.setSmooth(true);
        retratoProtagonista.fitHeightProperty().bind(lienzo.heightProperty().multiply(.68));
        retratoProtagonista.fitWidthProperty().bind(lienzo.widthProperty().multiply(.39));
        marcoProtagonista.setMouseTransparent(true);
        marcoProtagonista.setMinSize(0, 0);
        marcoProtagonista.maxWidthProperty().bind(lienzo.widthProperty().multiply(.40));
        marcoProtagonista.maxHeightProperty().bind(lienzo.heightProperty().multiply(.74));
        StackPane.setAlignment(marcoProtagonista, Pos.CENTER_LEFT);
        StackPane.setMargin(marcoProtagonista, new Insets(78, 0, 95, 10));
        retrato.setPreserveRatio(true);
        retrato.setSmooth(true);
        retrato.fitHeightProperty().bind(lienzo.heightProperty().multiply(.73));
        retrato.fitWidthProperty().bind(lienzo.widthProperty().multiply(.43));
        marcoRetrato.setMouseTransparent(true);
        marcoRetrato.setMinSize(0, 0);
        marcoRetrato.maxWidthProperty().bind(lienzo.widthProperty().multiply(.46));
        marcoRetrato.maxHeightProperty().bind(lienzo.heightProperty().multiply(.80));
        StackPane.setAlignment(marcoRetrato, Pos.CENTER_RIGHT);
        StackPane.setMargin(marcoRetrato, new Insets(70, 14, 95, 0));

        BorderPane interfaz = new BorderPane();
        interfaz.setTop(cabecera);
        interfaz.setBottom(scrollDialogo);
        interfaz.setPickOnBounds(false);
        BorderPane.setAlignment(cabecera, Pos.TOP_CENTER);
        // El fondo va detrás de los retratos y del cuadro de diálogo.
        // No usa Region.setBackground(): el CSS de .root/.visor-visual no puede ocultarlo.
        fondoVisual.setSmooth(true);
        fondoVisual.setPreserveRatio(false);
        fondoVisual.setMouseTransparent(true);
        // Managed para que StackPane centre correctamente el CG cuando conserva la proporción.
        fondoVisual.setManaged(true);
        StackPane.setAlignment(fondoVisual, Pos.CENTER);
        fondoVisual.fitWidthProperty().bind(lienzo.widthProperty());
        fondoVisual.fitHeightProperty().bind(lienzo.heightProperty());
        lienzo.widthProperty().addListener((obs, antes, ahora) -> ajustarEncuadreFondo());
        lienzo.heightProperty().addListener((obs, antes, ahora) -> ajustarEncuadreFondo());
        // Velo ambiental, solo para diálogos: el arte completo de los finales permanece intacto.
        veloAmbiente.setMouseTransparent(true);
        veloAmbiente.setManaged(false);
        veloAmbiente.widthProperty().bind(lienzo.widthProperty());
        veloAmbiente.heightProperty().bind(lienzo.heightProperty());
        veloAmbiente.setFill(new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(11, 8, 25, .12)),
                new Stop(.62, Color.rgb(11, 8, 25, .02)),
                new Stop(1, Color.rgb(11, 8, 25, .53))));
        lienzo.getChildren().addAll(fondoVisual, veloAmbiente, marcoProtagonista, marcoRetrato, interfaz);
        lienzo.getStyleClass().add("visor-visual");

        // Recorta cualquier retrato, fondo o borde que sobresalga del visor al redimensionar.
        Rectangle recorte = new Rectangle();
        recorte.widthProperty().bind(lienzo.widthProperty());
        recorte.heightProperty().bind(lienzo.heightProperty());
        lienzo.setClip(recorte);
        HelloApplication.cambiarContenido(ventana, lienzo);

        if (reanudar) {
            escena = historia.obtener(escenaId);
            lineaActual = Math.max(0, estado.getLineaActual());
            actualizarFondo();
            renderizar();
        } else {
            cambiarEscena(escenaId);
        }
    }

    private void cambiarEscena(String id) {
        escena = historia.obtener(id);
        lineaActual = 0;
        estado.setEscenaActual(id);
        estado.setLineaActual(0);
        estado.limpiarReaccion();
        actualizarFondo();
        renderizar();
    }

    private void actualizarFondo() {
        String cg = RecursosVisuales.cg(escena);
        String ruta = cg != null ? cg : RecursosVisuales.fondo(escena);
        Image imagen = cargar(ruta);
        // Si una ilustracion de final falla, conservar al menos el fondo de la escena.
        if (imagen == null && cg != null) {
            System.err.println("[QUINTAVERSO] CG no disponible; usando fondo de escena: " + cg);
            imagen = cargar(RecursosVisuales.fondo(escena));
        }
        if (imagen == null) {
            System.err.println("[QUINTAVERSO] Fondo no disponible para escena " + escena.getId()
                    + " (" + ruta + ")");
        }
        fondoVisual.setImage(imagen);
        // Los CG ya son horizontales: mostrar la ilustración completa sin cortar personajes.
        fondoVisual.setPreserveRatio(cg != null);
        veloAmbiente.setVisible(cg == null);
        ajustarEncuadreFondo();
        marcoRetrato.setVisible(cg == null);
        marcoProtagonista.setVisible(false);
    }

    /** Recorta la imagen estilo COVER para llenar el visor sin estirarla. */
    private void ajustarEncuadreFondo() {
        Image imagen = fondoVisual.getImage();
        double ancho = lienzo.getWidth();
        double alto = lienzo.getHeight();
        if (imagen == null || ancho <= 0 || alto <= 0
                || imagen.getWidth() <= 0 || imagen.getHeight() <= 0) {
            fondoVisual.setViewport(null);
            return;
        }
        if (RecursosVisuales.cg(escena) != null) {
            fondoVisual.setViewport(null);
            return;
        }
        double anchoImg = imagen.getWidth();
        double altoImg = imagen.getHeight();
        double proporciónVentana = ancho / alto;
        double proporciónImagen = anchoImg / altoImg;
        if (proporciónImagen > proporciónVentana) {
            double anchoVisible = altoImg * proporciónVentana;
            fondoVisual.setViewport(new Rectangle2D(
                    (anchoImg - anchoVisible) / 2.0, 0, anchoVisible, altoImg));
        } else {
            double altoVisible = anchoImg / proporciónVentana;
            fondoVisual.setViewport(new Rectangle2D(
                    0, (altoImg - altoVisible) / 2.0, anchoImg, altoVisible));
        }
    }

    private void renderizar() {
        titulo.setText(escena.getTitulo() == null ? "QUINTAVERSO" : escena.getTitulo());
        lugar.setText(escena.getLugar() == null ? "" : escena.getLugar());
        opciones.getChildren().clear();
        if (!estado.getReaccionPendiente().isEmpty()) {
            renderizarReaccion();
            autoguardar.run();
            scrollDialogo.setVvalue(0);
            return;
        }
        List<Dialogo> dialogos = escena.getDialogos() == null ? List.of() :
                escena.getDialogos().stream().filter(d -> d.disponible(estado)).toList();
        if (dialogos.isEmpty()) {
            mostrarLinea(new Dialogo("Narrador", "..."), "Escena sin diálogos");
        } else {
            lineaActual = Math.min(lineaActual, dialogos.size() - 1);
            estado.setLineaActual(lineaActual);
            mostrarLinea(dialogos.get(lineaActual),
                    "Línea " + (lineaActual + 1) + " de " + dialogos.size());
        }

        FlowPane navegacion = new FlowPane(9, 8);
        navegacion.setAlignment(Pos.CENTER_LEFT);
        if (lineaActual > 0) {
            navegacion.getChildren().add(crearBoton("‹ Anterior", () -> {
                lineaActual--;
                renderizar();
            }));
        }
        if (lineaActual < dialogos.size() - 1) {
            navegacion.getChildren().add(crearBoton("Continuar ›", () -> {
                lineaActual++;
                renderizar();
            }));
            opciones.getChildren().add(navegacion);
            autoguardar.run();
            scrollDialogo.setVvalue(0);
            return;
        }
        if (!navegacion.getChildren().isEmpty()) opciones.getChildren().add(navegacion);

        List<Decision> disponibles = escena.getDecisiones() == null ? List.of() :
                escena.getDecisiones().stream().filter(d -> d.disponible(estado)).toList();
        if (!disponibles.isEmpty()) {
            for (Decision d : disponibles) {
                Button eleccion = crearBoton(d.getTexto(), () -> elegirDecision(d));
                eleccion.getStyleClass().add("boton-decision");
                opciones.getChildren().add(eleccion);
            }
        } else if (escena.getSiguiente() != null) {
            opciones.getChildren().add(crearBoton("Seguir con la historia ›",
                    () -> cambiarEscena(escena.getSiguiente())));
        } else {
            opciones.getChildren().add(crearBoton("Terminar escena y volver al calendario ✓", this::terminar));
        }
        autoguardar.run();
        scrollDialogo.setVvalue(0);
    }

    private void elegirDecision(Decision d) {
        // Aplicar efectos UNA sola vez; si el jugador guarda después, restaurará la reacción.
        d.aplicar(estado);
        estado.agregarHistorial("   ↳ " + d.getTexto());
        estado.setReaccionPendiente(ReaccionesNarrativas.construir(escena, d));
        estado.setLineaReaccion(0);
        estado.setDestinoPendiente(d.getSiguiente());
        estado.setTerminarTrasReaccion(d.getSiguiente() == null);
        renderizar();
    }

    private void renderizarReaccion() {
        List<Dialogo> respuestas = estado.getReaccionPendiente();
        int indice = Math.min(estado.getLineaReaccion(), respuestas.size() - 1);
        estado.setLineaReaccion(indice);
        mostrarLinea(respuestas.get(indice), "Reacción a tu decisión · " + (indice + 1) + "/" + respuestas.size());
        if (indice < respuestas.size() - 1) {
            opciones.getChildren().add(crearBoton("Escuchar respuesta ›", () -> {
                estado.setLineaReaccion(indice + 1);
                renderizar();
            }));
        } else {
            opciones.getChildren().add(crearBoton("Continuar la historia ›", () -> {
                String destino = estado.getDestinoPendiente();
                boolean terminar = estado.isTerminarTrasReaccion();
                estado.limpiarReaccion();
                if (terminar || destino == null) terminar();
                else cambiarEscena(destino);
            }));
        }
    }

    private void mostrarLinea(Dialogo linea, String posicion) {
        personaje.setText(linea.getPersonaje() == null ? "Narrador" : linea.getPersonaje());
        texto.setText(linea.getTexto() == null ? "" : linea.getTexto());
        contador.setText(posicion);
        actualizarRetrato(linea);
    }

    private void actualizarRetrato(Dialogo linea) {
        if (RecursosVisuales.cg(escena) != null) {
            retrato.setImage(null);
            rutaRetratoActual = null;
            rutaProtagonistaActual = null;
            retrato.setViewport(null);
            retratoProtagonista.setViewport(null);
            marcoRetrato.setVisible(false);
            marcoProtagonista.setVisible(false);
            return;
        }
        String nombre = linea == null ? "narrador" : RecursosVisuales.personaje(linea.getPersonaje());
        boolean esJairo = "jairo".equals(estado.getTemporada());
        String ruta = linea == null ? null : esJairo && "jairo".equals(nombre) ? RecursosVisuales.spriteJairo(linea) : RecursosVisuales.retrato(linea);
        if (ruta == null && "narrador".equals(nombre)
                && !"seba".equals(estado.getTemporada()) && !esJairo) {
            String protagonista = RecursosVisuales.protagonistaEscena(escena);
            ruta = protagonista == null ? null : RecursosVisuales.neutro(protagonista);
        }
        Image imagen = cargar(ruta);
        retrato.setImage(imagen);
        rutaRetratoActual = ruta;
        marcoRetrato.setVisible(imagen != null);
        // Dos retratos, evitando duplicar a Seba cuando él tiene la palabra.
        boolean sebaPresente = "seba".equals(estado.getTemporada()) && !"seba".equals(nombre);
        // La emoción de Kelly, Amira u otro interlocutor no debe copiarse a Seba.
        String reaccion = "neutral";
        if (estado.ocurrioEvento("seba_amira_delato_grupo")) reaccion = "pensativo";
        else if (estado.ocurrioEvento("seba_infidelidad")
                && !estado.ocurrioEvento("seba_acepta_responsabilidad")) reaccion = "nervioso";
        rutaProtagonistaActual = sebaPresente ? RecursosVisuales.spriteSeba(reaccion) :
                esJairo && !"jairo".equals(nombre) ? RecursosVisuales.spriteJairo(estado.ocurrioEvento("j_bloqueado") || estado.ocurrioEvento("j_pausa") ? "pensativo" : "neutral") : null;
        Image prota = cargar(rutaProtagonistaActual);
        retratoProtagonista.setImage(prota);
        marcoProtagonista.setVisible(prota != null);
        aplicarEncuadreRetratos();
    }

    private void actualizarBotonEncuadre() {
        String etiqueta = switch (planoRetratos) {
            case AUTOMATICO -> "◉ Plano automático";
            case PRIMER_PLANO -> "🔍 Primer plano";
            case CUERPO_COMPLETO -> "♙ Cuerpo completo";
        };
        botonEncuadre.setText(etiqueta);
    }

    private void aplicarEncuadreRetratos() {
        EncuadreRetratos.aplicar(retrato, rutaRetratoActual, planoRetratos);
        EncuadreRetratos.aplicar(retratoProtagonista, rutaProtagonistaActual, planoRetratos);
    }

    private static Image cargar(String ruta) {
        if (ruta == null || ruta.isBlank()) return null;
        synchronized (CACHE) {
            if (CACHE.containsKey(ruta)) return CACHE.get(ruta);
            URL recurso = MotorNarrativo.class.getResource(ruta);
            if (recurso == null) {
                System.err.println("[QUINTAVERSO] Imagen no encontrada: " + ruta);
                return null;
            }
            try {
                Image img = new Image(recurso.toExternalForm(), false);
                if (img.isError()) throw new IllegalArgumentException(img.getException());
                CACHE.put(ruta, img);
                return img;
            } catch (RuntimeException ex) {
                System.err.println("[QUINTAVERSO] No se pudo abrir " + ruta + ": " + ex.getMessage());
                return null;
            }
        }
    }

    private Button crearBoton(String nombre, Runnable accion) {
        Button b = new Button(nombre);
        b.getStyleClass().addAll("boton-primario", "boton-dialogo");
        b.setWrapText(true);
        b.setMinHeight(40);
        b.setMinWidth(0);
        b.setMaxWidth(Double.MAX_VALUE);
        b.setOnAction(e -> accion.run());
        return b;
    }
    private Button botonBarra(String nombre, Runnable accion) {
        Button b = new Button(nombre);
        b.getStyleClass().add("boton-secundario");
        b.getStyleClass().add("boton-compacto");
        b.setOnAction(e -> accion.run());
        return b;
    }
    private void terminar() {
        alTerminar.run();
    }
}
