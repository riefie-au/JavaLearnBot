package lecture.javalearnbot;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.util.concurrent.TimeoutException;

public class ChatController extends BaseController // Controller for Chat page, handles AI query submission, clearing results, and exception handling.
{
    @FXML private TextField queryField; // Text field where user types their question.
    @FXML private TextField resultArea; // Field where AI response is displayed.

    @FXML private void onSearchCLick() // Triggered when user clicks the search button and wraps AI search handling in exception blocks.
    {
        try{
            handleSearch(queryField.getText()); // Sends the query text to be processed by AI handler
        } catch (TimeoutException ex){
            resultArea.setText("Error: AI took too long to respond."); // Specific exception: AI took too long.
        } catch (Exception ex) {
            resultArea.setText("System Error. Unable to retrieve responses from AI."); // General exception handler: system/internal errors
        }
    }

    @FXML private void onClearClick() // Triggered when user clicks clear button, it resets both input field and output area.
    {
        queryField.clear();
        resultArea.clear();
    }

    private void handleSearch(String query) throws Exception // Processes search logic. This would send query to AI model.
    {
        if (query.isBlank()){ // Prevents sending empty queries
            resultArea.setText("Please enter a query.");
            return;
        }
        resultArea.setText("AI Response: (placeholder)"); // Placeholder AI response
    }
}
