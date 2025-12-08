package lecture.javalearnbot;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.util.concurrent.TimeoutException;

public class ChatController extends BaseController
{
    @FXML private TextField queryField;
    @FXML private TextField resultArea;

    @FXML private void onSearchCLick()
    {
        try{
            handleSearch(queryField.getText());
        } catch (TimeoutException ex){
            resultArea.setText("Error: AI took too long to respond.");
        } catch (Exception ex) {
            resultArea.setText("System Error. Unable to retrieve responses from AI.");
        }
    }

    @FXML private void onClearClick()
    {
        queryField.clear();
        resultArea.clear();
    }

    private void handleSearch(String query) throws Exception
    {
        if (query.isBlank()){
            resultArea.setText("Please enter a query.");
            return;
        }
        resultArea.setText("AI Response: (placeholder)");
    }
}
