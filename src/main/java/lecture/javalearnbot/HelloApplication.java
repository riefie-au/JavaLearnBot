package lecture.javalearnbot;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        //ChatPageUI
        FXMLLoader chatPage = new FXMLLoader(HelloApplication.class.getResource("chatPage.fxml"));
        Scene scene = new Scene(chatPage.load(), 320, 240);
        stage.setTitle("Chat Page");
        stage.setScene(scene);

        //Document Page
        FXMLLoader documentPage = new FXMLLoader(HelloApplication.class.getResource("documentPage.fxml"));

        stage.show();
        //testtest

    }

    public static void main(String[] args) {
        launch();
    }
}