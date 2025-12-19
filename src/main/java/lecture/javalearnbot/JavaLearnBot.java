package lecture.javalearnbot;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class JavaLearnBot extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        //ChatPageUI
        FXMLLoader loader = new FXMLLoader(JavaLearnBot.class.getResource("navigationBar.fxml"));
        BorderPane root = loader.load();
        NavigationController nav = loader.getController();
        nav.toHome();
        Scene scene = new Scene(root);
        String css = Objects.requireNonNull(getClass().getResource("css/stylesheet.css")).toExternalForm();
        scene.getStylesheets().add(css);
        stage.setTitle("Chat Page");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}