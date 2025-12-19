package lecture.javalearnbot.Controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import lecture.javalearnbot.Evaluation.EvaluationLogEntry;
import lecture.javalearnbot.Evaluation.EvaluationStore;
import lecture.javalearnbot.Log.ChatLogEntry;
import lecture.javalearnbot.Log.LogStore;
import lecture.javalearnbot.RAG.DocumentIngestor;
import lecture.javalearnbot.RAG.Pipeline;
import lecture.javalearnbot.RAG.RagHelperClasses.Hit;
import lecture.javalearnbot.RAG.RagHelperClasses.Result;

import java.time.format.DateTimeFormatter;
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
    private TableView<ChatLogEntry> logTable; //table that displays rows of logEntry Objects
    @FXML
    private TableColumn<ChatLogEntry, String> timestampCol;  //LogEntry is the type of row object as its one object per row, String is the type of value displayed in the column
    @FXML
    private TableColumn<ChatLogEntry, String> questionCol;
    @FXML
    private TableColumn<ChatLogEntry, String> answerCol;
    @FXML
    private ComboBox<Integer> scoreBox;
    @FXML
    private ComboBox<String> labelBox;
    private final String[] labelOptions = {"Correct", "Incorrect", "Needs Review"};
    private final Integer[] scoreOptions = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};


    private final ObservableList<ChatLogEntry> logData = FXCollections.observableArrayList();
    //special list from javafx, that changes the list automatically, so when u do something like logData.add it automatically updates


    private final Pipeline pipeline = new Pipeline();
    private final DocumentIngestor indexer = new DocumentIngestor(pipeline.getEmbeddingModel(), pipeline.getChunkStore());
    private final EvaluationStore evaluationStore = new EvaluationStore();
    private final LogStore logStore = new LogStore();
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @FXML
    private void initialize() {
        setupTableColumns();
        loadTableData();
        setupRowSelectionListener();
        startIndexingThread();
        populateBoxes();
    }

    private void setupTableColumns() {
        timestampCol.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getTimestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
        );
        questionCol.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getQuestion())
        );
        answerCol.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getAnswer())
        );

        logTable.setFixedCellSize(50); //set fixed size to 50 due to answers being potentially too long
    }

    private void loadTableData() {
        logData.addAll(logStore.loadFromCSV());
        logTable.setItems(logData);
    }

    private void setupRowSelectionListener() {
        logTable.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    displayLogEntry(newValue);
                });
    }

    private void startIndexingThread() {
        new Thread(() -> {
            try {
                indexer.indexDocs();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }


    private void displayLogEntry(ChatLogEntry entry) {
        queryField.setText(entry.getQuestion());
        resultArea.setText(entry.getAnswer());
        rewriteArea.setText(entry.getRewrites());
        retrievedDocs.setText(entry.getRetrievedChunks());
    }


    @FXML
    private void onSearchCLick() // Triggered when user clicks the search button and wraps AI search handling in exception blocks.
    {
        try {
            handleSearch(queryField.getText()); // Sends the query text to be processed by AI handler
        } catch (TimeoutException ex) {
            ex.printStackTrace();
            resultArea.setText("Error: AI took too long to respond."); // Specific exception: AI took too long.
        } catch (Exception ex) {
            ex.printStackTrace();
            resultArea.setText("System Error. Unable to retrieve responses from AI."); // General exception handler: system/internal errors
        }
    }

    @FXML
    private void onClearClick() // Triggered when user clicks clear button, it resets both input field and output area.
    {
        queryField.clear();
        resultArea.clear();
        rewriteArea.clear();
        retrievedDocs.clear();
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
                ChatLogEntry entry = new ChatLogEntry(query, answer, rewrittenText, retrievedDocsText); //create a new logEntry object
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
    private void onExportButtonClick() {
        String question = queryField.getText().trim();
        String answer = resultArea.getText().trim();
        if (question.isEmpty() || answer.isEmpty()) {
            Alert questionEmptyAlert = new Alert(Alert.AlertType.WARNING);
            questionEmptyAlert.setTitle("Export error");
            questionEmptyAlert.setHeaderText("Missing data");
            questionEmptyAlert.setContentText("Please enter a query and get a result before exporting.");
            questionEmptyAlert.showAndWait();
            return;
        }

        //obtain value from ComboBoxes
        Integer score = scoreBox.getValue();
        String label = labelBox.getValue();
        if (score == null || label == null) {
            Alert scoreLabelAlert = new Alert(Alert.AlertType.WARNING);
            scoreLabelAlert.setTitle("Export Error");
            scoreLabelAlert.setHeaderText("Invalid Selection");
            scoreLabelAlert.setContentText("Please select both a score and a label before exporting.");
            scoreLabelAlert.showAndWait();
            return;
        }

        try {
            EvaluationLogEntry chatEval = new EvaluationLogEntry(question, answer);
            chatEval.evaluate(label, score);
            EvaluationStore.saveEvaluation(chatEval);
            Alert evalStatus = new Alert(Alert.AlertType.INFORMATION);
            evalStatus.setTitle("Export Success");
            evalStatus.setHeaderText("Evaluation Saved");
            evalStatus.setContentText("The current query and answer have been successfully exported to Evaluation.");
            evalStatus.showAndWait();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }

    public void populateBoxes() {
        scoreBox.getItems().addAll(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        scoreBox.setPromptText("Score:");

        labelBox.getItems().addAll("Correct", "Incorrect", "Needs Review");
        labelBox.setPromptText("Label:");
    }
}

