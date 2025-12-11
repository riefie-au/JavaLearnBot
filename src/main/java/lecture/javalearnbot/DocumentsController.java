package lecture.javalearnbot;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class DocumentsController extends BaseController // Controller for Documents page, handles search, filter, and interacting with documents table.
{
    @FXML private TextField docSearchField; // Text field where user types a search keyword.
    @FXML private ComboBox<String> categoryComboBox; // Dropdown filter for selecting document categories (Notes, Assignments)
    @FXML private ComboBox<String> statusComboBox; // Dropdown filter for selecting document status (Pending, Indexed, etc)
    @FXML private TableView<?> documentsTable; // Table displaying all the documents; placeholder for actual document model)

    @FXML public void initialize() // called after FXML loads
    {
        docSearchField.setOnKeyPressed(e -> applyDocFilter()); // trigger filter whenever key is pressed in document search bar
        categoryComboBox.setOnAction(e -> applyDocFilter()); // applies filter whenever category dropdown changes
        statusComboBox.setOnAction(e -> applyDocFilter()); // applies filter whenever the status dropdown changes
    }

    private void applyDocFilter() // applies document filtering logic based on search keyword, selected category, and selected status.
    {
        System.out.println("Filter applied.");
    }

}
