package lecture.javalearnbot;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

public class JavaLearnBot extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        //ChatPageUI
        FXMLLoader loader = new FXMLLoader(JavaLearnBot.class.getResource("navigationBar.fxml"));
        BorderPane root = loader.load();
        NavigationController nav = loader.getController();
        nav.toHome();
        Scene scene = new Scene(root);
        stage.setTitle("Chat Page");
        stage.setScene(scene);
        stage.show();
        

    }

    public static void main(String[] args) {
        launch();
    }
}