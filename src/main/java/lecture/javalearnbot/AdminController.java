package lecture.javalearnbot;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;

public class AdminController extends BaseController
{
    @FXML private TextField titleField;
    @FXML private TextField docSearchField;
    @FXML private ComboBox<String> categoryComboBox;
    @FXML private ComboBox<String> statusComboBox;

    private File selectedFile;

    @FXML private void onChooseFile()
    {
        FileChooser fc = new FileChooser();
        selectedFile = fc.showOpenDialog(null);
        if (selectedFile != null){
            loadFilePreview(selectedFile);
        }
    }

    private void loadFilePreview(File file){
        showAlert("File loaded", "Preview loaded for: " + file.getName());
    }

    @FXML private void onProcessDocument()
    {
        try {
            processDocument(selectedFile, categoryComboBox.getValue(), titleField.getText());
        } catch (IOException ex) {
            showError("File Error", "Cloud not read file.");
        } catch (Exception ex) {
            showError("Error", "Ingestion failed.");
        }
    }

    private void processDocument(File file, String category, String title) throws IOException
    {
        //placeholder
        showAlert("Success", "Document processed.");
    }

    @FXML
    private void onDocSearchTyped(){
        applyDocFilter();
    }

    private void applyDocFilter(){
        //placeholder
    }

    @FXML
    private void onEditMetadata(){
        showAlert("Edit", "Opening metadata editor...");
    }

    @FXML
    private void onDeleteDocument(){
        showAlert("Delete", "Document deleted.");
    }
}
