package lecture.javalearnbot;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class JavaLearnBot extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        //ChatPageUI
        FXMLLoader chatPage = new FXMLLoader(JavaLearnBot.class.getResource("chatPage.fxml"));
        Scene scene = new Scene(chatPage.load(), 800, 900);
        stage.setTitle("Chat Page");
        stage.setScene(scene);

        //Document Page
        FXMLLoader documentPage = new FXMLLoader(JavaLearnBot.class.getResource("documentPage.fxml"));

        stage.show();


    }

    public static void main(String[] args) {
        launch();
    }
}