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

    //Starts the application
    public void start(Stage stage) throws IOException {


        FXMLLoader loader = new FXMLLoader(JavaLearnBot.class.getResource("navigationBar.fxml")); //loads navigation bar FXML file
        BorderPane root = loader.load();
        NavigationController nav = loader.getController(); //gets the navigation controller
        nav.toHome();
        Scene scene = new Scene(root);

        //connects the css styling to the java application
        String css = Objects.requireNonNull(getClass().getResource("css/stylesheet.css")).toExternalForm();
        scene.getStylesheets().add(css);

        stage.setTitle("Java Learn Bot"); //sets the title of the window
        stage.setScene(scene); //puts the content (scene) into the window
        stage.show(); //make window visable to user
    }

    public static void main(String[] args) {
        launch();
    }
}