package lecture.javalearnbot.Controllers;

import javafx.beans.property.ReadOnlyLongWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import lecture.javalearnbot.RAG.RagHelperClasses.Document;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lecture.javalearnbot.Utility.EventBus;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

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
    @FXML private TableColumn<Document, String> colTimestamp;
    @FXML private ListView<String> previewListView;

    // --- Constants ---
    private static final String RESOURCES_PATH = "src/main/resources/docs/";
    private final Gson gson = new Gson();

    // --- Data ---
    private final ObservableList<Document> localDocuments = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupColumns();
        loadDocumentsFromResources(); // Load documents from files
        setupFilters();
        setupSelectionListener();
        setupEventBusSync(); //  Setup EventBus listener
        documentsTable.setRowFactory(tv -> {
            TableRow<Document> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    Document doc = row.getItem();
                    openFullDocument(doc);
                }
            });
            return row;
        });
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

        colTimestamp.setCellValueFactory(data -> {
            long ts = data.getValue().getTimestamp();
            String formatted = TIMESTAMP_FORMATTER.format(Instant.ofEpochMilli(ts));
            return new ReadOnlyStringWrapper(formatted);
        });
    }

    // ----------------------------
    // Sync with EventBus
    // ----------------------------
    private void setupEventBusSync() {
        // Documents page loads its own documents FIRST
        // EventBus is only used to sync DELETE operations from Admin

        // Listen for REMOVALS only (when Admin deletes documents)
        EventBus.ALL_DOCUMENTS.addListener((javafx.collections.ListChangeListener.Change<? extends Document> change) -> {
            while (change.next()) {
                if (change.wasRemoved()) {
                    // Remove documents that Admin deleted
                    for (Document removedDoc : change.getRemoved()) {
                        localDocuments.removeIf(doc ->
                                doc.getTitle().equals(removedDoc.getTitle()) &&
                                        doc.getCategory().equals(removedDoc.getCategory())
                        );
                    }
                    // Refresh dropdowns after removal
                    refreshComboBoxes();
                }
                if (change.wasAdded()) {
                    // Add new documents that Admin added
                    for (Document addedDoc : change.getAddedSubList()) {
                        boolean exists = false;
                        for (Document localDoc : localDocuments) {
                            if (localDoc.getTitle().equals(addedDoc.getTitle()) &&
                                    localDoc.getCategory().equals(addedDoc.getCategory())) {
                                exists = true;
                                break;
                            }
                        }
                        if (!exists) {
                            localDocuments.add(addedDoc);
                        }
                    }
                    // Refresh dropdowns after addition
                    refreshComboBoxes();
                }
            }
        });
    }

    // ----------------------------
    // Load documents from resources/docs/ directory
    // ----------------------------
    private void loadDocumentsFromResources() {
        try {
            // Get the resources/docs directory
            Path resourcesPath = Paths.get(RESOURCES_PATH);

            if (!Files.exists(resourcesPath) || !Files.isDirectory(resourcesPath)) {
                System.err.println("Resources directory not found: " + resourcesPath.toAbsolutePath());
                return;
            }

            // Clear existing data
            localDocuments.clear();

            // Find all .txt files
            Files.walk(resourcesPath)
                    .filter(path -> path.toString().endsWith(".txt") && !path.toString().endsWith(".meta.json"))
                    .forEach(txtPath -> {
                        try {
                            // Load the document
                            Document doc = loadDocumentFromFile(txtPath);
                            if (doc != null) {
                                localDocuments.add(doc);
                            }
                        } catch (IOException e) {
                            System.err.println("Error loading document: " + txtPath);
                            e.printStackTrace();
                        }
                    });

            // Setup dropdowns
            refreshComboBoxes();

            // Set table items
            documentsTable.setItems(localDocuments);

            System.out.println("✅ Documents page loaded " + localDocuments.size() + " documents from resources");

        } catch (IOException e) {
            System.err.println("Error walking through resources directory");
            e.printStackTrace();
        }
    }

    private Document loadDocumentFromFile(Path txtPath) throws IOException {
        // Get metadata file path
        Path metaPath = Paths.get(txtPath.toString() + ".meta.json");

        String title = "";
        String category = "Uncategorized";
        String source = "File";
        long timestamp = System.currentTimeMillis();

        // Read metadata if exists
        if (Files.exists(metaPath)) {
            try {
                String metaJson = Files.readString(metaPath);
                JsonObject metaObject = gson.fromJson(metaJson, JsonObject.class);

                if (metaObject.has("title")) {
                    title = metaObject.get("title").getAsString();
                }
                if (metaObject.has("category")) {
                    category = metaObject.get("category").getAsString();
                }
                if (metaObject.has("source")) {
                    source = metaObject.get("source").getAsString();
                }
                if (metaObject.has("timestamp")) {
                    timestamp = metaObject.get("timestamp").getAsLong();
                }
            } catch (Exception e) {
                System.err.println("Error parsing metadata for: " + txtPath);
                e.printStackTrace();
            }
        }

        // If title wasn't in metadata, use filename without extension
        if (title.isEmpty()) {
            String fileName = txtPath.getFileName().toString();
            title = fileName.substring(0, fileName.lastIndexOf('.'));
        }

        // Read file content
        List<String> contentLines = Files.readAllLines(txtPath);
        String description = "";

        // IMPORTANT: Get description from METADATA, NOT from file content
        if (Files.exists(metaPath)) {
            try {
                String metaJson = Files.readString(metaPath);
                JsonObject metaObject = gson.fromJson(metaJson, JsonObject.class);
                if (metaObject.has("description")) {
                    description = metaObject.get("description").getAsString();
                }
            } catch (Exception e) {
                // Ignore if can't read metadata
            }
        }

        // If no metadata description exists, use a SHORT summary (max 2 lines)
        if (description.isEmpty() && !contentLines.isEmpty()) {
            int linesToTake = Math.min(2, contentLines.size());
            List<String> firstLines = contentLines.subList(0, linesToTake);
            description = String.join(" ", firstLines);  // Join with SPACE, not newline

            // Trim if too long
            if (description.length() > 100) {
                description = description.substring(0, 100) + "...";
            }
        }

        // Create relative path for display
        String relativePath = txtPath.toString().replace("\\", "/");

        return new Document(title, category, source, relativePath, description, timestamp);
    }

    private void refreshComboBoxes() {
        // Extract unique categories and sources for combo boxes
        Set<String> categories = new HashSet<>();
        Set<String> sources = new HashSet<>();

        for (Document doc : localDocuments) {
            categories.add(doc.getCategory());
            sources.add(doc.getSource());
        }

        // Setup combo boxes
        List<String> categoryList = new ArrayList<>(categories);
        categoryList.sort(String::compareTo);
        categoryList.add(0, "All");

        List<String> sourceList = new ArrayList<>(sources);
        sourceList.sort(String::compareTo);
        sourceList.add(0, "All");

        categoryComboBox.setItems(FXCollections.observableArrayList(categoryList));
        statusComboBox.setItems(FXCollections.observableArrayList(sourceList));

        categoryComboBox.getSelectionModel().select("All");
        statusComboBox.getSelectionModel().select("All");
    }

    // ----------------------------
    // Filtering + sorting
    // ----------------------------
    private void setupFilters() {
        FilteredList<Document> filteredData =
                new FilteredList<>(localDocuments, d -> true);

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

    private String getPreviewContent(String filePath) {
        try {
            Path path = Paths.get(filePath);
            if (!Files.exists(path)) {
                return "File not found: " + filePath;
            }

            List<String> contentLines = Files.readAllLines(path);
            if (contentLines.isEmpty()) {
                return "Empty file";
            }

            // Take first 8 lines for preview
            int linesToTake = Math.min(8, contentLines.size());
            List<String> firstLines = contentLines.subList(0, linesToTake);
            String preview = String.join("\n", firstLines);

            if (contentLines.size() > linesToTake) {
                preview += "\n...";
            }

            return preview;
        } catch (IOException e) {
            return "Error reading file: " + e.getMessage();
        }
    }

    private static final DateTimeFormatter TIMESTAMP_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                    .withZone(ZoneId.systemDefault());

    // Show Preview of the documents clicked on
    private void showPreview(Document doc) {
        if (doc == null) {
            previewListView.getItems().clear();
            return;
        }

        List<String> previewItems = new ArrayList<>();

        // Add metadata
        previewItems.add("📄 Title:      " + doc.getTitle());
        previewItems.add("🏷️  Category:   " + doc.getCategory());
        previewItems.add("📤 Source:     " + doc.getSource());
        previewItems.add("📁 Path:       " + doc.getPath());
        previewItems.add("📅 Timestamp:  " + new Date(doc.getTimestamp()));
        previewItems.add("");

        // Get PREVIEW CONTENT from file (not from document description)
        String previewContent = getPreviewContent(doc.getPath());

        if (!previewContent.isEmpty()) {
            previewItems.add("══════════════════════════════════════════════");
            previewItems.add("                  PREVIEW                    ");
            previewItems.add("══════════════════════════════════════════════");
            previewItems.add("");

            String[] previewLines = previewContent.split("\n");
            for (String line : previewLines) {
                previewItems.add("  " + line);
            }
        } else {
            previewItems.add("📭 No content available");
        }

        previewListView.setItems(FXCollections.observableArrayList(previewItems));
    }

    private void openFullDocument(Document doc) {
        try {
            Path path = Paths.get(doc.getPath());

            if (!Files.exists(path)) {
                showError("File Not Found", "Cannot find file:\n" + doc.getPath());
                return;
            }

            String content = Files.readString(path);

            TextArea textArea = new TextArea(content);
            textArea.setEditable(false);
            textArea.setWrapText(true);
            textArea.setPrefWidth(700);
            textArea.setPrefHeight(500);

            Dialog<Void> dialog = new Dialog<>();
            dialog.setTitle("Viewing: " + doc.getTitle());
            dialog.getDialogPane().setContent(textArea);
            dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

            dialog.showAndWait();

        } catch (IOException e) {
            showError("Error", "Failed to open file:\n" + e.getMessage());
        }
    }

}
