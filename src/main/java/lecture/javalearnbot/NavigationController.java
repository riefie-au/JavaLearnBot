package lecture.javalearnbot;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.event.ActionEvent;
import lecture.javalearnbot.Controllers.BaseController;


public class NavigationController {
    @FXML
    private BorderPane mainLayout; //Because borderpane is used, centre of each fxml scene is labeled as mainLayout to be called by navigation controller and switch scenes

    @FXML
    private TextField globalSearch; //text field search bar that will navigate to different tabs based on user input

    // Handles navigation between each fxml page.
    public void goTo(String fxmlName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlName)); // loads fxml layout as fxmlName is being called
            Parent view = loader.load();

            Object controller = loader.getController(); //to recieve controller associated with fxml
            if (controller instanceof BaseController) { // controllers inherited by BaseController will be enabled by NavigationController to allow page switching
                ((BaseController) controller).setNavigationController(this);
            }
            mainLayout.setCenter(view);

        }
        catch (Exception e) { // prints error if fxml fails to load
            System.err.println("Error loading FXML: " + fxmlName);
            e.printStackTrace();
        }
    }


    //Action events to allow page switching
    @FXML public void toHome(ActionEvent event) { //for the navigation buttons
        toHome();
    }
    public void toHome() { //for the search bar
        goTo("homePage.fxml");
    }

    @FXML public void toChatBot(ActionEvent event) {
        toChatBot();
    }
    public void toChatBot() {
        goTo("chatPage.fxml");
    }

    @FXML public void toDocuments(ActionEvent event) {
        toDocuments();
    }
    public void toDocuments() {
        goTo("documentPage.fxml");
    }

    @FXML public void toEvaluationDashboard(ActionEvent event) {
        toEvaluationDashboard();
    }
    public void toEvaluationDashboard() {
        goTo("evaluationDashboardPage.fxml");
    }

    @FXML public void toAdmin(ActionEvent event) {
        toAdmin();
    }
    public void toAdmin() {
        goTo("adminPage.fxml");
    }

    public void toRegister() {
    }

    public void toLogin() {
    }


    @FXML
    public void globalSearch(ActionEvent event) {
        String search = globalSearch.getText().trim().toLowerCase(); //receives user input in lowercase

        if (search.isEmpty()) {
            return;
        }

        // searches for navigation
        if (search.contains("home") || search.contains("main")) {
            toHome();
        } else if (search.contains("chat") || search.contains("bot")) {
            toChatBot();
        } else if (search.contains("doc") || search.contains("file")) {
            toDocuments();
        } else if (search.contains("eval") || search.contains("dash")) {
            toEvaluationDashboard();
        } else if (search.contains("admin") || search.contains("setting")) {
            toAdmin();
        } else {
            System.out.println("No page found for: " + search);
        }
    }
}
