
package com.example.quintaverso;

import com.example.quintaverso.motor.Decision;
import com.example.quintaverso.motor.Dialogo;
import com.example.quintaverso.motor.Escena;
import com.example.quintaverso.motor.EstadoPartida;
import com.example.quintaverso.motor.GestorHistoria;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Motor narrativo de QUINTAVERSO.
 *
 * - Fondos por escena.
 * - Sprites por personaje y emoción.
 * - CG para los finales.
 * - Sin avatar provisional de Kevin.
 * - Conserva decisiones y estadísticas.
 */
public final class MotorNarrativo {

    private final Stage ventana;
    private final EstadoPartida estado;
    private final GestorHistoria historia;
    private final Runnable alTerminar;

    private Escena escena;
    private int lineaActual;

    private final Label titulo = new Label();
    private final Label lugar = new Label();
    private final Label personaje = new Label();
    private final Label texto = new Label();
    private final Label contador = new Label();

    private final VBox opciones = new VBox(10);
    private final VBox panelVisual = new VBox(8);
    private final TitledPane direccion = new TitledPane();

    private final StackPane lienzo = new StackPane();

    private final ImageView retrato = new ImageView();
    private final StackPane marcoRetrato = new StackPane(retrato);

    private final ScrollPane scrollDialogo = new ScrollPane();

    // Caché de imágenes con límite de elementos.
    private static final Map<String, Image> CACHE =
            new LinkedHashMap<>(24, .75f, true) {
                @Override
                protected boolean removeEldestEntry(
                        Map.Entry<String, Image> eldest) {
                    return size() > 18;
                }
            };

    public MotorNarrativo(
            Stage ventana,
            EstadoPartida estado,
            GestorHistoria historia,
            Runnable alTerminar) {

        this.ventana = ventana;
        this.estado = estado;
        this.historia = historia;
        this.alTerminar = alTerminar;
    }

    public void iniciar(String escenaId) {

        titulo.getStyleClass().add("titulo-mediano");
        titulo.setWrapText(true);

        lugar.getStyleClass().add("texto-suave");

        personaje.getStyleClass().add("personaje");

        texto.getStyleClass().add("dialogo");
        texto.setWrapText(true);
        texto.setMaxWidth(990);

        contador.getStyleClass().add("texto-suave");

        VBox cuadro = new VBox(
                12,
                personaje,
                texto,
                contador
        );

        cuadro.getStyleClass().add("panel-dialogo");
        cuadro.setPadding(new Insets(19, 24, 18, 24));
        cuadro.setFillWidth(true);

        panelVisual.getStyleClass().add("panel");
        panelVisual.setPadding(new Insets(14));

        direccion.setText(
                "🎨 Dirección visual y prompt (opcional)"
        );
        direccion.setContent(panelVisual);
        direccion.setExpanded(false);
        direccion.setCollapsible(true);
        direccion.setMaxWidth(Double.MAX_VALUE);

        opciones.setFillWidth(true);

        VBox cajaInferior = new VBox(
                10,
                cuadro,
                opciones,
                direccion
        );

        cajaInferior.setFillWidth(true);
        cajaInferior.setMaxWidth(1060);
        cajaInferior.setPadding(
                new Insets(10, 22, 14, 22)
        );

        scrollDialogo.setContent(cajaInferior);
        scrollDialogo.setFitToWidth(true);

        scrollDialogo.setVbarPolicy(
                ScrollPane.ScrollBarPolicy.AS_NEEDED
        );

        scrollDialogo.setHbarPolicy(
                ScrollPane.ScrollBarPolicy.NEVER
        );

        scrollDialogo.setPannable(true);
        scrollDialogo.setPrefHeight(340);
        scrollDialogo.setMaxHeight(390);

        scrollDialogo.getStyleClass().add(
                "scroll-dialogos"
        );

        VBox cabecera = new VBox(
                5,
                titulo,
                lugar
        );

        cabecera.getStyleClass().add(
                "panel-encabezado-visual"
        );

        cabecera.setPadding(
                new Insets(16, 22, 14, 22)
        );

        cabecera.setMaxWidth(1010);

        // Configuración de sprites.
        retrato.setPreserveRatio(true);
        retrato.setSmooth(true);

        retrato.fitHeightProperty().bind(
                lienzo.heightProperty().multiply(.81)
        );

        retrato.fitWidthProperty().bind(
                lienzo.widthProperty().multiply(.54)
        );

        marcoRetrato.setMouseTransparent(true);

        marcoRetrato.setMaxSize(
                Region.USE_PREF_SIZE,
                Region.USE_PREF_SIZE
        );

        StackPane.setAlignment(
                marcoRetrato,
                Pos.CENTER_RIGHT
        );

        StackPane.setMargin(
                marcoRetrato,
                new Insets(40, 30, 85, 0)
        );

        // Interfaz sobre el fondo.
        BorderPane interfaz = new BorderPane();

        interfaz.setTop(cabecera);

        BorderPane.setAlignment(
                cabecera,
                Pos.TOP_CENTER
        );

        interfaz.setBottom(scrollDialogo);
        interfaz.setPickOnBounds(false);

        // Ya no existe el avatar provisional con la K.
        lienzo.getChildren().addAll(
                marcoRetrato,
                interfaz
        );

        lienzo.getStyleClass().add("visor-visual");

        Scene scene = new Scene(
                lienzo,
                1100,
                720
        );

        HelloApplication.agregarEstilo(scene);

        ventana.setScene(scene);

        cambiarEscena(escenaId);
    }

    private void cambiarEscena(String id) {

        escena = historia.obtener(id);
        lineaActual = 0;

        actualizarFondo();
        actualizarDireccion();
        renderizar();

        scrollDialogo.setVvalue(0);
    }

    private void actualizarFondo() {

        String cg = RecursosVisuales.cg(escena);

        String ruta = cg != null
                ? cg
                : RecursosVisuales.fondo(escena);

        Image imagen = cargar(ruta);

        if (imagen != null) {

            BackgroundSize size = new BackgroundSize(
                    BackgroundSize.AUTO,
                    BackgroundSize.AUTO,
                    false,
                    false,
                    false,
                    true
            );

            BackgroundImage capa = new BackgroundImage(
                    imagen,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundPosition.CENTER,
                    size
            );

            lienzo.setBackground(
                    new Background(capa)
            );

        } else {

            lienzo.setBackground(
                    new Background(
                            new BackgroundFill(
                                    Color.web("#130f1f"),
                                    CornerRadii.EMPTY,
                                    Insets.EMPTY
                            )
                    )
            );
        }

        // Las CG de finales se muestran sin sprites encima.
        marcoRetrato.setVisible(cg == null);
    }

    private void actualizarDireccion() {

        panelVisual.getChildren().clear();

        Label descripcion = new Label(
                "PLANOS / BLOQUEO: " +
                        (escena.getDireccionVisual() == null
                                ? "Sin notas."
                                : escena.getDireccionVisual())
        );

        descripcion.setWrapText(true);
        descripcion.getStyleClass().add("texto-suave");

        TextArea prompt = new TextArea(
                escena.getPromptImagen() == null
                        ? ""
                        : escena.getPromptImagen()
        );

        prompt.setWrapText(true);
        prompt.setEditable(false);
        prompt.setPrefRowCount(3);

        Button copiar = new Button(
                "Copiar prompt visual"
        );

        copiar.getStyleClass().add(
                "boton-secundario"
        );

        copiar.setOnAction(e -> {

            ClipboardContent content =
                    new ClipboardContent();

            content.putString(prompt.getText());

            Clipboard.getSystemClipboard()
                    .setContent(content);

            copiar.setText("¡Copiado!");
        });

        panelVisual.getChildren().addAll(
                descripcion,
                prompt,
                copiar
        );
    }

    private void renderizar() {

        titulo.setText(
                escena.getTitulo() == null
                        ? "QUINTAVERSO"
                        : escena.getTitulo()
        );

        lugar.setText(
                escena.getLugar() == null
                        ? ""
                        : escena.getLugar()
        );

        List<Dialogo> dialogos =
                escena.getDialogos() == null
                        ? List.of()
                        : escena.getDialogos();

        opciones.getChildren().clear();

        if (dialogos.isEmpty()) {

            personaje.setText("Narrador");
            texto.setText("...");
            contador.setText("Escena sin diálogos");

            actualizarRetrato(null);

        } else {

            Dialogo linea = dialogos.get(
                    Math.min(
                            lineaActual,
                            dialogos.size() - 1
                    )
            );

            personaje.setText(
                    linea.getPersonaje() == null
                            ? "Narrador"
                            : linea.getPersonaje()
            );

            texto.setText(
                    linea.getTexto() == null
                            ? ""
                            : linea.getTexto()
            );

            contador.setText(
                    "Línea " +
                            (lineaActual + 1) +
                            " de " +
                            dialogos.size()
            );

            actualizarRetrato(linea);
        }

        HBox navegacion = new HBox(10);
        navegacion.setAlignment(Pos.CENTER_LEFT);

        if (lineaActual > 0) {

            Button atras = crearBoton(
                    "‹ Anterior",
                    () -> {
                        lineaActual--;
                        renderizar();
                    }
            );

            atras.getStyleClass().add(
                    "boton-secundario"
            );

            navegacion.getChildren().add(atras);
        }

        if (lineaActual < dialogos.size() - 1) {

            navegacion.getChildren().add(
                    crearBoton(
                            "Continuar ›",
                            () -> {
                                lineaActual++;
                                renderizar();
                            }
                    )
            );

            opciones.getChildren().add(navegacion);
            scrollDialogo.setVvalue(0);
            return;
        }

        if (!navegacion.getChildren().isEmpty()) {
            opciones.getChildren().add(navegacion);
        }

        List<Decision> disponibles =
                escena.getDecisiones() == null
                        ? List.of()
                        : escena.getDecisiones()
                        .stream()
                        .filter(d -> d.disponible(estado))
                        .toList();

        if (!disponibles.isEmpty()) {

            for (Decision d : disponibles) {

                opciones.getChildren().add(
                        crearBoton(
                                d.getTexto(),
                                () -> {

                                    d.aplicar(estado);

                                    estado.agregarHistorial(
                                            "   ↳ " + d.getTexto()
                                    );

                                    if (d.getSiguiente() == null) {
                                        terminar();
                                    } else {
                                        cambiarEscena(
                                                d.getSiguiente()
                                        );
                                    }
                                }
                        )
                );
            }

        } else if (escena.getSiguiente() != null) {

            opciones.getChildren().add(
                    crearBoton(
                            "Seguir con la historia ›",
                            () -> cambiarEscena(
                                    escena.getSiguiente()
                            )
                    )
            );

        } else {

            opciones.getChildren().add(
                    crearBoton(
                            "Terminar escena y volver al calendario ✓",
                            this::terminar
                    )
            );
        }

        scrollDialogo.setVvalue(0);
    }

    /**
     * Muestra el sprite del personaje que está hablando.
     * Kevin utiliza sus tres expresiones desde RecursosVisuales.
     */
    private void actualizarRetrato(Dialogo linea) {

        // No superponer personajes sobre CG.
        if (RecursosVisuales.cg(escena) != null) {
            retrato.setImage(null);
            marcoRetrato.setVisible(false);
            return;
        }

        String nombre = linea == null
                ? "narrador"
                : RecursosVisuales.personaje(
                linea.getPersonaje()
        );

        String ruta = linea == null
                ? null
                : RecursosVisuales.retrato(linea);

        // Durante la narración puede mantenerse
        // el protagonista de la escena.
        if (ruta == null && nombre.equals("narrador")) {

            String protagonista =
                    RecursosVisuales.protagonistaEscena(
                            escena
                    );

            ruta = protagonista == null
                    ? null
                    : RecursosVisuales.neutro(protagonista);
        }

        Image imagen = cargar(ruta);

        retrato.setImage(imagen);

        // Si falta una imagen, no se muestra ninguna K.
        marcoRetrato.setVisible(imagen != null);
    }

    /**
     * Carga imágenes desde src/main/resources.
     */
    private static Image cargar(String ruta) {

        if (ruta == null || ruta.isBlank()) {
            return null;
        }

        synchronized (CACHE) {

            if (CACHE.containsKey(ruta)) {
                return CACHE.get(ruta);
            }

            URL recurso =
                    MotorNarrativo.class.getResource(ruta);

            if (recurso == null) {

                System.err.println(
                        "[QUINTAVERSO] Imagen no encontrada: " +
                                ruta
                );

                return null;
            }

            try {

                Image img = new Image(
                        recurso.toExternalForm(),
                        false
                );

                if (img.isError()) {
                    throw new IllegalArgumentException(
                            img.getException()
                    );
                }

                CACHE.put(ruta, img);

                return img;

            } catch (RuntimeException e) {

                System.err.println(
                        "[QUINTAVERSO] No se pudo abrir " +
                                ruta +
                                ": " +
                                e.getMessage()
                );

                return null;
            }
        }
    }

    private Button crearBoton(
            String nombre,
            Runnable accion) {

        Button b = new Button(nombre);

        b.getStyleClass().add(
                "boton-primario"
        );

        b.setWrapText(true);
        b.setMinHeight(43);
        b.setMaxWidth(Double.MAX_VALUE);

        b.setOnAction(e -> accion.run());

        return b;
    }

    private void terminar() {
        alTerminar.run();
    }
}
