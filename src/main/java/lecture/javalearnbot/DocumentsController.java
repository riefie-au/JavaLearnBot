package lecture.javalearnbot;

import javafx.beans.property.ReadOnlyLongWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import lecture.javalearnbot.AiFeatures.Document;

public class DocumentsController extends BaseController {

    // --- FXML fields ---
    @FXML private TextField docSearchField;
    @FXML private ComboBox<String> categoryComboBox;
    @FXML private ComboBox<String> statusComboBox;

    @FXML private TableView<Document> documentsTable;
    @FXML private TableColumn<Document, String> colName;
    @FXML private TableColumn<Document, String> colCategory;
    @FXML private TableColumn<Document, String> colSource;
    @FXML private TableColumn<Document, String> colPath;
    @FXML private TableColumn<Document, Number> colTimestamp;

    @FXML private ListView<String> previewListView;

    // --- Data ---
    private final ObservableList<Document> masterData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupColumns();
        loadDummyData();
        setupFilters();
        setupSelectionListener();
    }

    // ----------------------------
    // Table column bindings
    // ----------------------------
    private void setupColumns() {
        colName.setCellValueFactory(data ->
                new ReadOnlyStringWrapper(data.getValue().getTitle())
        );

        colCategory.setCellValueFactory(data ->
                new ReadOnlyStringWrapper(data.getValue().getCategory())
        );

        colSource.setCellValueFactory(data ->
                new ReadOnlyStringWrapper(data.getValue().getSource())
        );

        colPath.setCellValueFactory(data ->
                new ReadOnlyStringWrapper(data.getValue().getPath())
        );

        colTimestamp.setCellValueFactory(data ->
                new ReadOnlyLongWrapper(data.getValue().getTimestamp())
        );
    }

    // ----------------------------
    // Dummy data (replace with DB later)
    // ----------------------------
    private void loadDummyData() {
        masterData.addAll(
                new Document("Java Basics", "Core", "Upload", "/docs/java_basics.pdf", System.currentTimeMillis()),
                new Document("OOP Guide", "OOP", "Indexed", "/docs/oop_guide.pdf", System.currentTimeMillis()),
                new Document("JavaFX Layouts", "UI", "Upload", "/docs/javafx_layouts.pdf", System.currentTimeMillis()),
                new Document("Spring Boot Intro", "Frameworks", "Processing", "/docs/spring_boot.pdf", System.currentTimeMillis())
        );

        categoryComboBox.setItems(
                FXCollections.observableArrayList("All", "Core", "OOP", "UI", "Frameworks")
        );

        statusComboBox.setItems(
                FXCollections.observableArrayList("All", "Upload", "Indexed", "Processing")
        );

        categoryComboBox.getSelectionModel().select("All");
        statusComboBox.getSelectionModel().select("All");
    }

    // ----------------------------
    // Filtering + sorting
    // ----------------------------
    private void setupFilters() {
        FilteredList<Document> filteredData =
                new FilteredList<>(masterData, d -> true);

        docSearchField.textProperty().addListener(
                (obs, oldVal, newVal) -> updatePredicate(filteredData)
        );
        categoryComboBox.valueProperty().addListener(
                (obs, oldVal, newVal) -> updatePredicate(filteredData)
        );
        statusComboBox.valueProperty().addListener(
                (obs, oldVal, newVal) -> updatePredicate(filteredData)
        );

        SortedList<Document> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(documentsTable.comparatorProperty());

        documentsTable.setItems(sortedData);
    }

    private void updatePredicate(FilteredList<Document> filteredData) {
        filteredData.setPredicate(doc -> {
            String searchText = docSearchField.getText().toLowerCase();
            String selectedCategory = categoryComboBox.getValue();
            String selectedStatus = statusComboBox.getValue();

            boolean matchesSearch =
                    searchText.isEmpty() ||
                            doc.getTitle().toLowerCase().contains(searchText);

            boolean matchesCategory =
                    selectedCategory.equals("All") ||
                            doc.getCategory().equals(selectedCategory);

            boolean matchesStatus =
                    selectedStatus.equals("All") ||
                            doc.getSource().equals(selectedStatus);

            return matchesSearch && matchesCategory && matchesStatus;
        });
    }

    // ----------------------------
    // Preview panel
    // ----------------------------
    private void setupSelectionListener() {
        documentsTable.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, oldDoc, newDoc) -> showPreview(newDoc));
    }

    private void showPreview(Document doc) {
        if (doc == null) {
            previewListView.getItems().clear();
            return;
        }

        previewListView.setItems(FXCollections.observableArrayList(
                "Title: " + doc.getTitle(),
                "Category: " + doc.getCategory(),
                "Source: " + doc.getSource(),
                "Path: " + doc.getPath(),
                "Timestamp: " + doc.getTimestamp(),
                "",
                "Preview content goes here..."
        ));
    }
}

