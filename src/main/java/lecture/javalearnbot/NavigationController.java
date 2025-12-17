package lecture.javalearnbot;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.event.ActionEvent;



public class NavigationController {
    @FXML
    private BorderPane mainLayout;

    @FXML
    private TextField globalSearch;

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

    @FXML
    public void globalSearch(ActionEvent event) {
        String query = globalSearch.getText().trim().toLowerCase();

        if (query.isEmpty()) {
            return;
        }

        // Search logic to map keywords to your existing navigation methods
        if (query.contains("home") || query.contains("main")) {
            toHome();
        } else if (query.contains("chat") || query.contains("bot")) {
            toChatBot();
        } else if (query.contains("doc") || query.contains("file")) {
            toDocuments();
        } else if (query.contains("eval") || query.contains("dash")) {
            toEvaluationDashboard();
        } else if (query.contains("admin") || query.contains("setting")) {
            toAdmin();
        } else {
            System.out.println("No page found for: " + query);
        }
    }
}
