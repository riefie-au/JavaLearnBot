package lecture.javalearnbot;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;

public class AdminController extends BaseController // Controller for Admin settings page.
{
    @FXML private TextField titleField; // Text field for entering the document title.
    @FXML private TextField docSearchField; // Search field to filter documents.
    @FXML private ComboBox<String> categoryComboBox; // Dropdown for selecting document category.
    @FXML private ComboBox<String> statusComboBox; // Dropdown for filtering documents by status.

    private File selectedFile; // Stores currently selected file from system.

    @FXML private void onChooseFile() // Triggered when choose file button is clicked and opens system file chooser and stores selected file.
    {
        FileChooser fc = new FileChooser();
        selectedFile = fc.showOpenDialog(null);
        if (selectedFile != null){ // If file is selected, load its preview.
            loadFilePreview(selectedFile);
        }
    }

    private void loadFilePreview(File file){ // Displays a placeholder alert to simulate file preview.
        showAlert("File loaded", "Preview loaded for: " + file.getName());
    }

    @FXML private void onProcessDocument() // Triggered when process document button is clicked. It calls processDocument() with exception handling.
    {
        try {
            processDocument(selectedFile, categoryComboBox.getValue(), titleField.getText());
        } catch (IOException ex) {
            showError("File Error", "Cloud not read file.");
        } catch (Exception ex) {
            showError("Error", "Ingestion failed.");
        }
    }

    private void processDocument(File file, String category, String title) throws IOException // Placeholder for document processing logic. This would call RAG pipeline and store embedded vector.
    {
        showAlert("Success", "Document processed.");
    }

    @FXML
    private void onDocSearchTyped(){ // Triggered when admin types in the document search field, calls filter method for real-time document filtering.
        applyDocFilter();
    }

    private void applyDocFilter(){ // Placeholder method for applying filters based on search text, category, and status.
        //placeholder
    }

    @FXML
    private void onEditMetadata(){ // Triggered when edit metadata button is clicked. Opens metadata editor for selected document.
        showAlert("Edit", "Opening metadata editor...");
    }

    @FXML
    private void onDeleteDocument(){ // Triggered when delete button is clicked. Simulates deletion of selected document.
        showAlert("Delete", "Document deleted.");
    }
}
