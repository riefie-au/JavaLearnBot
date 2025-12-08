package lecture.javalearnbot;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;

public class EvaluationController extends BaseController
{
    @FXML private ComboBox<String> humanLabelComboBox;
    @FXML private ComboBox<Integer> scoreComboBox;
    @FXML private TableView<?> evaluationTable;

    @FXML private void onHumanLabelChange()
    {
        //placeholder
    }

    @FXML private void onScoreChange()
    {
        //placeholder
    }

    @FXML private void onExportSingleRow()
    {
        //placeholder export
        showAlert("Export", "Single item has been exported.");
    }

    @FXML private void onExportAll()
    {
        try{
            //placeholder
            showAlert("Success", "Export completed!");
        } catch (Exception ex){
            showError("Export Failed", "Could not save file");
        }
    }

    @FXML private void onSaveChanges()
    {
        showAlert("Saved", "All evaluation changes saved.");
    }
}
