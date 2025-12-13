package lecture.javalearnbot;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import lecture.javalearnbot.AiFeatures.*;

import java.util.List;
import java.util.concurrent.TimeoutException;

public class ChatController extends BaseController // Controller for Chat page, handles AI query submission, clearing results, and exception handling.
{
    @FXML
    private TextField queryField; // Text field where user types their question.
    @FXML
    private TextArea resultArea; // Field where AI response is displayed.
    @FXML
    private TextArea rewriteArea; // Field where AI response is displayed.
    @FXML
    private TextArea retrievedDocs;

    @FXML
    private TableView<LogEntry> logTable; //table that displays rows of logEntry Objects
    @FXML
    private TableColumn<LogEntry, String> timestampCol;  //LogEntry is the type of row object as its one object per row, String is the type of value displayed in the column
    @FXML
    private TableColumn<LogEntry, String> questionCol;
    @FXML
    private TableColumn<LogEntry, String> answerCol;

    private final ObservableList<LogEntry> logData = FXCollections.observableArrayList();
    //special list from javafx, that changes the list automatically, so when u do something like logData.add it automatically updates


    private final Pipeline pipeline = new Pipeline();
    private final EvaluationStore evaluationStore = new EvaluationStore();
    private final LogStore logStore = new LogStore();
    //private final LogManager logManager = new LogManager();

    @FXML
    private void initialize(){
        //pipeline.indexDocs();
        timestampCol.setCellValueFactory(new PropertyValueFactory<>("timestamp")); //tels the column which property of log entry to display
        questionCol.setCellValueFactory(new PropertyValueFactory<>("question"));
        answerCol.setCellValueFactory(new PropertyValueFactory<>("answer"));

        logData.addAll(logStore.loadFromCSV()); //adds all data from the CSV file that stores previous answers and questions
        //logData.addAll(logManager.getLogs());
        // Bind ObservableList to TableView to make it so whenever that list is updated the table updates
        logTable.setItems(logData);

        logTable.getSelectionModel()// returns selection manager for the tableView
                .selectedItemProperty() //represents currently selected item, and returns an observable property
                .addListener((observable, oldValue, newValue) -> { //attaches a listener that automatically runs when a user clicks a row
                    displayLogEntry(newValue);
                });
        // Run indexing in a background thread
        new Thread(() -> {
            try {
                pipeline.indexDocs();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void displayLogEntry(LogEntry entry) {
        queryField.setText(entry.getQuestion());
        resultArea.setText(entry.getAnswer());
        rewriteArea.setText(entry.getRewrites());
        retrievedDocs.setText(entry.getRetrievedChunks());
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
        if (query.isBlank()) { // Prevents sending empty queries
            resultArea.setText("Please enter a query.");
            return;
        }
        resultArea.setText("Searching for an answer..."); //while the answer is being generated set text "searching for an answer in the resultArea"

        new Thread(() -> {
            try {
                Result result = pipeline.run(query); // pass the query to the pipeline
                String answer = result.getAnswer(); //obtain an answer from the result object
                resultArea.setText(answer); //sets the answer in the result area

                StringBuilder rewritesBuilder = new StringBuilder(); //create a string builder for rewrites
                for (String r : result.getRewrites()) {
                    rewritesBuilder.append("- ").append(r).append("\n");
                }
                rewriteArea.setText(rewritesBuilder.toString());
                String rewrittenText = rewritesBuilder.toString(); //keeps rewritten text as a String to keep in the logs csv

                StringBuilder retrievedDocsBuilder = new StringBuilder();
                for (Hit hit : result.getHits()) {
                    retrievedDocsBuilder.append("Index: [")
                            .append(hit.getIndex())
                            .append("] Source: ")
                            .append(hit.getHitSource())
                            .append(" | Score: ")
                            .append(String.format("%.2f", hit.getScore()))
                            .append("\nChunk: ")
                            .append(hit.getSnippet())
                            .append("\n\n");
                }
                retrievedDocs.setText(retrievedDocsBuilder.toString());
                String retrievedDocsText = retrievedDocsBuilder.toString();
                //
                //logManager.addLog(query,answer);
                LogEntry entry = new LogEntry(query, answer, rewrittenText, retrievedDocsText); //create a new logEntry object
                logData.add(entry);  //add it to the observable list, this updates the table view
                logStore.add(entry); //add it to the buffer in LogStore
                logStore.saveToCSV(); //save whatever is in the buffer to the CSV logs.
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } //end of thread
        ).start(); //starts the thread
    }

    @FXML
    private void onExport() {
        String query = queryField.getText();
        String answer = resultArea.getText();
        List<String> rewrites = List.of(rewriteArea.getText().split("\n"));
        double score = 8.1;
        String label = "correct";
        String notes = "mostly correct but with flaws";


        evaluationStore.add(new EvaluationRecord(
                query,
                answer,
                rewrites,
                score,
                label,
                notes));
        evaluationStore.exportAsCSV();
}
}
