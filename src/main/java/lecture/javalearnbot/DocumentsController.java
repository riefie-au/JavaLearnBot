package lecture.javalearnbot;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class DocumentsController extends BaseController
{
    @FXML private TextField docSearchField;
    @FXML private ComboBox<String> categoryComboBox;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private TableView<?> documentsTable;

    @FXML public void initialize()
    {
        docSearchField.setOnKeyPressed(e -> applyDocFilter());
        categoryComboBox.setOnAction(e -> applyDocFilter());
        statusComboBox.setOnAction(e -> applyDocFilter());
    }

    private void applyDocFilter()
    {
        System.out.println("Filter applied.");
    }

}
