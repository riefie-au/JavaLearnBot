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
        Scene scene = new Scene(chatPage.load());
        stage.setTitle("Chat Page");
        stage.setScene(scene);
        stage.show();

    }

    public static void main(String[] args) {
        launch();
    }
}