package lecture.javalearnbot;

import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;

public class HomeController extends BaseController // Controller for the Home Page.
{
    // Top navigation menu options
    @FXML private MenuItem homeMenuItem;
    @FXML private MenuItem chatMenuItem;
    @FXML private MenuItem documentsMenuItem;
    @FXML private MenuItem evaluationMenuItem;
    @FXML private MenuItem adminMenuItem;
    @FXML private TextField globalSearchField; // Search bar input field.

    @FXML
    public void initialize()
    {
        homeMenuItem.setOnAction(e -> nav.toHome()); // Home navigation menu action
        chatMenuItem.setOnAction(e -> nav.toChatBot()); // Navigation to Chatbot page
        documentsMenuItem.setOnAction(e -> nav.toDocuments()); // Navigation to Documents page.
        evaluationMenuItem.setOnAction(e -> nav.toEvaluationDashboard()); // Navigation to Evaluation Dashboard.
        adminMenuItem.setOnAction(e-> nav.toAdmin()); // Navigation to Admin dashboard.

        // Trigger global search when enter is pressed in search bar.
        globalSearchField.setOnAction( e->
                globalSearch(globalSearchField.getText())
        );
    }

    private void globalSearch(String keyword)
    {
        if (keyword == null || keyword.isBlank()){  // Validation: Checks if search field is empty.
            showError("Search", "Please enter a search term.");
            return;
        }
        showAlert("Search", "You searched: " + keyword); // Displays search term back to user
    }
}
