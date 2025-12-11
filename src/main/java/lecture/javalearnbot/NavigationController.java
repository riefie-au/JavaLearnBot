package lecture.javalearnbot;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.awt.event.ActionEvent;

public class NavigationController // Handles navigation between different FXML pages in JavaLearnBot. All controllers use this class to switch scenes.
{
    private Stage stage;

    //Constructor
    public NavigationController() {
    }
    //Helper to find Stage if it's missing
    private void setStageFromEvent(ActionEvent event) {
        if (this.stage == null && event != null) {
            Node source = (Node) event.getSource();
            this.stage = (Stage) source.getScene().getWindow();
        }
    }

    public NavigationController(Stage stage) // Constructor that receives the primary stage used throughout the whole application.
    {
        this.stage = stage;
    }
    public void goTo(String fxmlName, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlName)); // load FXML layout
            Scene scene = new Scene(loader.load());
            stage.setTitle(title); // Set stage title and show new scene
            stage.setScene(scene);

            Object controller = loader.getController(); //To retrieve controller associated with FXML
            if (controller instanceof BaseController) { // If page controller inherits from BaseController, will link this NavigationController to enable page switching.
                ((BaseController) controller).setNavigationController(this);

            }
        } catch (Exception e) { // Prints error if an FXML file fails to load
            e.printStackTrace();
        }
    }

    //Predefined navigation shortcuts for each pages
    public void toLogin() { goTo("loginPage.fxml", "Login"); }
    public void toRegister() { goTo("registerPage.fxml", "Register"); }
    public void toHome() { goTo("homePage.fxml", "Home"); }
    public void toChatBot() { goTo("chatPage.fxml", "Chat Bot"); }
    public void toDocuments() { goTo("documentPage.fxml", "Documents"); }
    public void toEvaluationDashboard() { goTo("evaluationDashboardPage.fxml", "Evaluation dashboard"); }
    public void toAdmin () { goTo("adminPage.fxml", "Admin"); }

}


