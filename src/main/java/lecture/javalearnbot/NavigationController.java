package lecture.javalearnbot;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.event.ActionEvent;



public class NavigationController {
    @FXML
    private BorderPane mainLayout;
    // Handles navigation between different FXML pages in JavaLearnBot. All controllers use this class to switch scenes.
    public void goTo(String fxmlName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlName)); // load FXML layout
            Parent view = loader.load();

            Object controller = loader.getController(); //To retrieve controller associated with FXML
            if (controller instanceof BaseController) { // If page controller inherits from BaseController, will link this NavigationController to enable page switching.
                ((BaseController) controller).setNavigationController(this);
            }
            mainLayout.setCenter(view);

        }
        catch (Exception e) { // Prints error if an FXML file fails to load
            System.err.println("Error loading FXML: " + fxmlName);
            e.printStackTrace();
        }
    }
    @FXML public void toHome(ActionEvent event) { toHome(); }
    public void toHome() { goTo("homePage.fxml"); }

    @FXML public void toChatBot(ActionEvent event) { toChatBot(); }
    public void toChatBot() { goTo("chatPage.fxml"); }

    @FXML public void toDocuments(ActionEvent event) { toDocuments(); }
    public void toDocuments() { goTo("documentPage.fxml"); }

    @FXML public void toEvaluationDashboard(ActionEvent event) { toEvaluationDashboard(); }
    public void toEvaluationDashboard() { goTo("evaluationDashboardPage.fxml"); }

    @FXML public void toAdmin(ActionEvent event) { toAdmin(); }
    public void toAdmin() { goTo("adminPage.fxml"); }

    public void toRegister() {
    }

    public void toLogin() {
    }
    //Predefined navigation shortcuts for each pages
}
