package lecture.javalearnbot;

import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;

public class HomeController extends BaseController
{
    @FXML private MenuItem homeMenuItem;
    @FXML private MenuItem chatMenuItem;
    @FXML private MenuItem documentsMenuItem;
    @FXML private MenuItem evaluationMenuItem;
    @FXML private MenuItem adminMenuItem;
    @FXML private TextField globalSearchField;

    @FXML
    public void initialize()
    {
        homeMenuItem.setOnAction(e -> nav.toHome());
        chatMenuItem.setOnAction(e -> nav.toChatBot());
        documentsMenuItem.setOnAction(e -> nav.toDocuments());
        evaluationMenuItem.setOnAction(e -> nav.toEvaluationDashboard());
        adminMenuItem.setOnAction(e-> nav.toAdmin());

        globalSearchField.setOnAction( e->
                globalSearch(globalSearchField.getText())
        );
    }

    private void globalSearch(String keyword)
    {
        if (keyword == null || keyword.isBlank()){
            showError("Search", "Please enter a search term.");
            return;
        }
        showAlert("Search", "You searched: " + keyword);
    }
}
