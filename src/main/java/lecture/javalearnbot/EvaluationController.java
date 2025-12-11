package lecture.javalearnbot;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;

public class EvaluationController extends BaseController // Controller for evaluation dashboard page.
{
    @FXML private ComboBox<String> humanLabelComboBox; // dropdown for selecting human labels like correct/incorrect/needs review
    @FXML private ComboBox<Integer> scoreComboBox; // dropdown for selecting numeric scores like 1-5
    @FXML private TableView<?> evaluationTable; // table displaying evaluation items

    @FXML private void onHumanLabelChange() // Triggered when user changes the selected human label in ComboBox; a placeholder for future logic to update table data.
    {
        //placeholder: actual update to table or model will go here.
    }

    @FXML private void onScoreChange() // Triggered when a new score is selected
    {
        //placeholder logic for score update.
    }

    @FXML private void onExportSingleRow() // Triggered when user clicks "Export Selected Row".
    {
        //Shows a success message
        showAlert("Export", "Single item has been exported.");
    }

    @FXML private void onExportAll() // Triggered when user clicks "Export All"
    {
        try{
            //placeholder for exportall logic
            showAlert("Success", "Export completed!");
        } catch (Exception ex){
            showError("Export Failed", "Could not save file");
        }
    }

    @FXML private void onSaveChanges() // Triggered when user clicks "Save Changes"; Shows a confirmation message to say that evaluations were saved.
    {
        showAlert("Saved", "All evaluation changes saved.");
    }
}
