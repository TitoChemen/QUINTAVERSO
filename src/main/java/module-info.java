module com.example.quintaverso {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires com.fasterxml.jackson.databind;

    exports com.example.quintaverso;
    opens com.example.quintaverso to javafx.fxml;
    opens com.example.quintaverso.motor to com.fasterxml.jackson.databind;
}
