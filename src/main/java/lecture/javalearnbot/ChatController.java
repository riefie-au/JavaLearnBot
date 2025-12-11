package lecture.javalearnbot;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import lecture.javalearnbot.AiFeatures.Pipeline;
import lecture.javalearnbot.AiFeatures.Result;

import java.util.concurrent.TimeoutException;

public class ChatController extends BaseController // Controller for Chat page, handles AI query submission, clearing results, and exception handling.
{
    @FXML
    private TextField queryField; // Text field where user types their question.
    @FXML
    private TextArea resultArea; // Field where AI response is displayed.
    @FXML
    private TextArea rewriteArea; // Field where AI response is displayed.

    private final Pipeline pipeline = new Pipeline();

    //@FXML
    //private void initialize(){
    //    pipeline.indexDocs();
    //}

    @FXML //rie's temporary fix
    private void initialize() {
        try {
            // Attempt to connect to OpenAI
            pipeline.indexDocs();
        } catch (Exception e) {
            // If it fails (bad API key, no internet), print error but DO NOT crash the app
            System.err.println("CRITICAL WARNING: Failed to initialize AI Pipeline. Check API Key.");
            e.printStackTrace();

            // Optional: Tell the user in the UI if possible
            if (resultArea != null) {
                resultArea.setText("Warning: AI System failed to initialize. Please check API Key configuration.");
            }
        }
    }

    @FXML
    private void onSearchCLick() // Triggered when user clicks the search button and wraps AI search handling in exception blocks.
    {
        try{
            handleSearch(queryField.getText()); // Sends the query text to be processed by AI handler
        } catch (TimeoutException ex){
            ex.printStackTrace();
            resultArea.setText("Error: AI took too long to respond."); // Specific exception: AI took too long.
        } catch (Exception ex) {
            ex.printStackTrace();
            resultArea.setText("System Error. Unable to retrieve responses from AI."); // General exception handler: system/internal errors
        }
    }

    @FXML private void onClearClick() // Triggered when user clicks clear button, it resets both input field and output area.
    {
        queryField.clear();
        resultArea.clear();
    }

    @FXML
    private void handleSearch(String query) throws Exception // Processes search logic. This would send query to AI model.
    {
        if (query.isBlank()){ // Prevents sending empty queries
            resultArea.setText("Please enter a query.");
            return;
        }

        Result result = pipeline.run(query);
        String answer = result.getAnswer();
        resultArea.setText(answer);

        StringBuilder rewritesText = new StringBuilder();
        for (String r : result.getRewrites()){
            rewritesText.append("- ").append(r).append("\n");
        }
        rewriteArea.setText(rewritesText.toString());
    }
}
