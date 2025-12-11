module lecture.javalearnbot {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;
    requires java.desktop;

    opens lecture.javalearnbot to javafx.fxml;
    exports lecture.javalearnbot;
}