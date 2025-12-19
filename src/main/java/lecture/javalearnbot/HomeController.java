package lecture.javalearnbot;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

public class HomeController {

    @FXML
    private Button beginChatButton;

    @FXML
    public void initialize() {
        System.out.println("Home Page Initialized");
    }

    @FXML
    void onBeginChatClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("chatpage.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene currentScene = stage.getScene();
            BorderPane mainLayout = (BorderPane) currentScene.getRoot();
            mainLayout.setCenter(root);

            System.out.println("Switched center view to chatpage.fxml");

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error: Could not load chatpage.fxml. Check the file path.");
        }
    }
}
