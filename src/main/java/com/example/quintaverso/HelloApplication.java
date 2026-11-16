package com.example.quintaverso;

import com.example.quintaverso.motor.MotorCalendario;
import com.example.quintaverso.seguridad.GestorAccesos;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

/** Inicio del juego, menú principal y acceso por temporada. */
public class HelloApplication extends Application {
    private Stage ventana;
    private final GestorAccesos accesos = new GestorAccesos();

    @Override
    public void start(Stage stage) {
        ventana = stage;
        ventana.setTitle("QUINTAVERSO — Historias de la Quinta Región");
        ventana.setMinWidth(860);
        ventana.setMinHeight(600);
        mostrarMenu();
        ventana.show();
    }

    public void mostrarMenu() {
        Label marca = new Label("QUINTAVERSO");
        marca.getStyleClass().add("logo");
        Label bajada = new Label("Tus decisiones. Sus consecuencias. Una sola Quinta Región.");
        bajada.getStyleClass().add("subtitulo");
        Label intro = new Label("ELIGE UNA TEMPORADA");
        intro.getStyleClass().add("etiqueta-caps");

        VBox tarjetaCaceres = tarjetaTemporada("01", "CÁCERES",
                "DOBLE TENTACIÓN", "30 días · 3 turnos por día · 8 finales", "Empezar temporada",
                () -> solicitarClave("caceres"));
        VBox tarjetaJairo = tarjetaTemporada("02", "JAIRO",
                "PRÓXIMAMENTE", "Historia aún en desarrollo", "Ver temporada",
                () -> solicitarClave("jairo"));
        VBox tarjetaSeba = tarjetaTemporada("03", "SEBA",
                "PRÓXIMAMENTE", "Historia aún en desarrollo", "Ver temporada",
                () -> solicitarClave("seba"));

        HBox tarjetas = new HBox(15, tarjetaCaceres, tarjetaJairo, tarjetaSeba);
        tarjetas.setAlignment(Pos.CENTER);
        for (VBox tarjeta : new VBox[]{tarjetaCaceres, tarjetaJairo, tarjetaSeba}) {
            HBox.setHgrow(tarjeta, Priority.ALWAYS);
            tarjeta.setMaxWidth(Double.MAX_VALUE);
        }
        Label pie = new Label("v0.5 · Proyecto de novela visual · Ficción inspirada en la Quinta Región");
        pie.getStyleClass().add("texto-suave");

        VBox contenido = new VBox(22, marca, bajada, intro, tarjetas, pie);
        contenido.setAlignment(Pos.CENTER);
        contenido.setPadding(new Insets(55, 35, 40, 35));
        VBox.setVgrow(tarjetas, Priority.NEVER);
        ScrollPane scroll = new ScrollPane(contenido);
        scroll.setFitToWidth(true);
        scroll.getStyleClass().add("scroll-fondo");
        aplicarEscena(scroll);
    }

    private VBox tarjetaTemporada(String numero, String protagonista, String subtitulo,
                                 String detalle, String botonTexto, Runnable accion) {
        Label n = new Label("TEMPORADA " + numero);
        n.getStyleClass().add("etiqueta-caps");
        Label nombre = new Label(protagonista);
        nombre.getStyleClass().add("titulo-tarjeta");
        Label s = new Label(subtitulo);
        s.getStyleClass().add("acento");
        Label d = new Label(detalle);
        d.setWrapText(true);
        d.getStyleClass().add("texto-suave");
        Button entrar = new Button(botonTexto);
        entrar.getStyleClass().add("boton-primario");
        entrar.setMaxWidth(Double.MAX_VALUE);
        entrar.setOnAction(e -> accion.run());
        VBox tarjeta = new VBox(12, n, nombre, s, d, entrar);
        tarjeta.getStyleClass().add("tarjeta");
        tarjeta.setPadding(new Insets(22));
        tarjeta.setMinWidth(215);
        return tarjeta;
    }

    private void solicitarClave(String temporada) {
        Dialog<String> dialogo = new Dialog<>();
        dialogo.setTitle("Acceso a temporada");
        dialogo.setHeaderText("Código de acceso para " + temporada.toUpperCase());
        PasswordField password = new PasswordField();
        password.setPromptText("Introduce tu clave");
        password.setPrefWidth(300);
        dialogo.getDialogPane().setContent(password);
        ButtonType aceptar = new ButtonType("Entrar", ButtonBar.ButtonData.OK_DONE);
        dialogo.getDialogPane().getButtonTypes().addAll(aceptar, ButtonType.CANCEL);
        dialogo.setResultConverter(tipo -> tipo == aceptar ? password.getText() : null);
        dialogo.showAndWait().ifPresent(clave -> {
            if (!accesos.validar(temporada, clave)) {
                alerta(Alert.AlertType.ERROR, "Acceso denegado", "Clave incorrecta, compadre.");
                return;
            }
            if (!temporada.equals("caceres")) {
                alerta(Alert.AlertType.INFORMATION, "Coming soon",
                        "Acceso correcto. Esta temporada todavía está en desarrollo.");
                return;
            }
            try {
                new MotorCalendario(ventana, this::mostrarMenu).iniciar();
            } catch (RuntimeException ex) {
                alerta(Alert.AlertType.ERROR, "Error al iniciar QUINTAVERSO", ex.getMessage());
            }
        });
    }

    public static void alerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert a = new Alert(tipo, mensaje, ButtonType.OK);
        a.setTitle(titulo);
        a.setHeaderText(titulo);
        a.showAndWait();
    }

    public void aplicarEscena(javafx.scene.Parent raiz) {
        Scene escena = new Scene(raiz, 1100, 720);
        agregarEstilo(escena);
        ventana.setScene(escena);
    }

    public static void agregarEstilo(Scene escena) {
        var hoja = HelloApplication.class.getResource("/quintaverso.css");
        if (hoja != null) escena.getStylesheets().add(hoja.toExternalForm());
    }

    public static void main(String[] args) { launch(args); }
}
