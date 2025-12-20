package lecture.javalearnbot.Controllers;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import lecture.javalearnbot.Log.ChatLogEntry;
import lecture.javalearnbot.Evaluation.EvaluationLogEntry;
import lecture.javalearnbot.Evaluation.EvaluationStore;
import lecture.javalearnbot.Log.LogStore;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class EvaluationController extends BaseController {

    @FXML private TableView<ChatLogEntry> chatLogsTable;
    @FXML private TableColumn<ChatLogEntry, String> chatTimeCol;
    @FXML private TableColumn<ChatLogEntry, String> chatQuestionCol;
    @FXML private TableColumn<ChatLogEntry, String> chatAnswerCol;

    @FXML private TableView<EvaluationLogEntry> evaluationTable;
    @FXML private TableColumn<EvaluationLogEntry, String> evalTimeCol;
    @FXML private TableColumn<EvaluationLogEntry, String> evalQuestionCol;
    @FXML private TableColumn<EvaluationLogEntry, String> evalAnswerCol;
    @FXML private TableColumn<EvaluationLogEntry, String> evalLabelCol;
    @FXML private TableColumn<EvaluationLogEntry, Integer> evalScoreCol;
    @FXML private TableColumn<ChatLogEntry, Void> evalButtonCol;
    @FXML private BarChart<String,Number> evaluationBarChart;

    private final ObservableList<ChatLogEntry> chatLogData = FXCollections.observableArrayList();
    private final ObservableList<EvaluationLogEntry> evaluationData = FXCollections.observableArrayList();
    private final String[] labelOptions = {"Correct", "Incorrect", "Needs Review"};
    private final Integer[] scoreOptions = {1,2,3,4,5,6,7,8,9,10};
    private final LogStore logStore = new LogStore();


    public void initialize() {
        setupChatLogsTable();
        setupEvaluationTable();
        loadData();
        updateScoreChart();
    }

    private void setupChatLogsTable() {
        chatTimeCol.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getTimestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
        chatQuestionCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getQuestion()));
        chatAnswerCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getAnswer()));

        chatLogsTable.setItems(chatLogData);
        chatLogsTable.setFixedCellSize(50);

        // Show full answer on double click
        chatLogsTable.setRowFactory(tv -> {
            TableRow<ChatLogEntry> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getClickCount() == 2) {
                    ChatLogEntry clickedLog = row.getItem();
                    showFullAnswer(clickedLog);
                }
            });
            return row;
        });

        evalButtonCol.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("Evaluate");

            {
                btn.setOnAction(event -> {
                    ChatLogEntry log = getTableView().getItems().get(getIndex());
                    openEvaluationForm(log, null);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btn);
                }
            }
        });
    }

    //setup the evaluation table
    private void setupEvaluationTable() {
        evalTimeCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getTimestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
        evalQuestionCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getQuestion()));
        evalAnswerCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getAnswer()));
        evalLabelCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getLabel()));
        evalScoreCol.setCellValueFactory(d -> new SimpleObjectProperty<>(d.getValue().getScore()));
        evaluationTable.setItems(evaluationData);

        evaluationTable.setFixedCellSize(50);
    }

    //loading the data from the logStore and evalationData from evaluationStore
    private void loadData() {
        chatLogData.addAll(logStore.loadFromCSV());
        List<EvaluationLogEntry> savedEvals = EvaluationStore.loadEvaluations();
        if (savedEvals != null) {
            evaluationData.addAll(savedEvals);
        }
    }

    //Opening the evaluation form, setup in a way that it handles both
    //editing and creating a evaluationLogEntry
    private void openEvaluationForm(ChatLogEntry chatLog, EvaluationLogEntry existingEval) {
        Dialog<EvaluationLogEntry> dialog = new Dialog<>();
        dialog.setTitle("Evaluate AI Answer");


        //Combo box for score options (1-10)
        ComboBox<Integer> scoreBox = new ComboBox<>();
        scoreBox.getItems().addAll(scoreOptions);
        scoreBox.setPromptText("Score: ");

        //combo box for label options (correct, incorrect, etc)
        ComboBox<String> labelBox = new ComboBox<>();
        labelBox.getItems().addAll(labelOptions);
        labelBox.setPromptText("Label: ");

        VBox vbox = new VBox(10,
                new Label("Score:"), scoreBox,
                new Label("Label:"), labelBox);
        dialog.getDialogPane().setContent(vbox);

        //save button creation
        ButtonType saveBtn = new ButtonType(
                "Save",
                ButtonBar.ButtonData.OK_DONE);

        //ading the cancel button and save button
        dialog.getDialogPane().getButtonTypes().addAll(
                saveBtn,
                ButtonType.CANCEL);

        //set result convertor which means do this when the dialog closes in simple terms
        dialog.setResultConverter(dialogButton -> {
            //only if the user clicked saveBtn then create the new existingVal or newEval
            if (dialogButton == saveBtn) {
                    EvaluationLogEntry newEval = new EvaluationLogEntry(chatLog.getQuestion(), chatLog.getAnswer());
                    newEval.evaluate(labelBox.getValue(), scoreBox.getValue());
                    return newEval;
                }
            return null;
        });

        //open the dialog and wait for user input
        EvaluationLogEntry result = dialog.showAndWait().orElse(null);
        if (result != null) {
            if (!evaluationData.contains(result)) {
                evaluationData.add(result); // add new evaluation
            }
            EvaluationStore.saveEvaluation(result);// save to file
            updateScoreChart();
        }
    }

    private void showFullAnswer(ChatLogEntry chatLog) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("AI Answer");
        alert.setHeaderText(chatLog.getQuestion());
        alert.setContentText(chatLog.getAnswer());
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE); // ensure full text shows
        alert.showAndWait();
    }


    private void updateScoreChart() {
        evaluationBarChart.getData().clear();
        int[] scoreCount = new int[11]; //reason we use 11 is for 0-10
        for (EvaluationLogEntry eval : evaluationData) {
            int score = eval.getScore();
            scoreCount[score]++;
        }
        // Create a series for the chart
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Scores");

        for (int i = 1; i <= 10; i++) {
            series.getData().add(new XYChart.Data<>(String.valueOf(i), scoreCount[i]));
        }
        evaluationBarChart.getData().add(series);
    }

}