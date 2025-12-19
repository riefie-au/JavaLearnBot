package lecture.javalearnbot;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
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

    private static final String RESOURCES_PATH = "src/main/resources/docs/";
    private final Gson gson = new Gson();

    // --- Data ---
    private final ObservableList<Document> masterData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupColumns();
        loadDocumentsFromResources();
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
    // Load Documents and Metadata
    // ----------------------------
    private void loadDocumentsFromResources() {
        try {
            // Get the resources/docs directory
            Path resourcesPath = Paths.get(RESOURCES_PATH);

            if (!Files.exists(resourcesPath) || !Files.isDirectory(resourcesPath)) {
                System.err.println("Resources directory not found: " + resourcesPath.toAbsolutePath());
                return;
            }

            // Find all .txt files
            Files.walk(resourcesPath)
                    .filter(path -> path.toString().endsWith(".txt") && !path.toString().endsWith(".meta.json"))
                    .forEach(txtPath -> {
                        try {
                            // Load the document
                            Document doc = loadDocumentFromFile(txtPath);
                            if (doc != null) {
                                masterData.add(doc);
                            }
                        } catch (IOException e) {
                            System.err.println("Error loading document: " + txtPath);
                            e.printStackTrace();
                        }
                    });

            // Setup combo boxes
            Set<String> categories = new HashSet<>();
            Set<String> sources = new HashSet<>();

            for (Document doc : masterData) {
                categories.add(doc.getCategory());
                sources.add(doc.getSource());
            }

            // Create sorted lists for combo boxes
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

            System.out.println("Loaded " + masterData.size() + " documents from resources");

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

        // Read file content for description
        List<String> contentLines = Files.readAllLines(txtPath);
        String description = "";
        if (!contentLines.isEmpty()) {
            // Take first 8 lines as description (more content!)
            int linesToTake = Math.min(8, contentLines.size());
            List<String> firstLines = contentLines.subList(0, linesToTake);
            description = String.join("\n", firstLines);  // Join with newline to keep line breaks

            // Add "..." if there's more content beyond what we took
            if (contentLines.size() > linesToTake) {
                description += "\n... (more content)";
            }
        }

        // Create relative path for display
        String relativePath = txtPath.toString().replace("\\", "/");

        return new Document(title, category, source, relativePath, description, timestamp);
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

    // Show Preview of the documents clicked on
    private void showPreview(Document doc) {
        if (doc == null) {
            previewListView.getItems().clear();
            return;
        }

        List<String> previewItems = new ArrayList<>();

        // Add metadata
        previewItems.add("Title: " + doc.getTitle());
        previewItems.add("Category: " + doc.getCategory());
        previewItems.add("Source: " + doc.getSource());
        previewItems.add("Path: " + doc.getPath());
        previewItems.add("Timestamp: " + new Date(doc.getTimestamp()));
        previewItems.add("");

        // Add description/content
        if (doc.getDescription() != null && !doc.getDescription().isEmpty()) {
            previewItems.add("Description:");
            previewItems.add(doc.getDescription());

            // If description is short, show more content
            if (doc.getDescription().length() < 50) {
                // Try to read actual content
                try {
                    Path filePath = Paths.get(doc.getPath());
                    if (Files.exists(filePath)) {
                        String fullContent = Files.readString(filePath);
                        // Take first 500 chars
                        String preview = fullContent.substring(0, Math.min(500, fullContent.length()));
                        if (fullContent.length() > 500) preview += "...";
                        previewItems.add("");
                        previewItems.add("Preview:");
                        previewItems.add(preview);
                    }
                } catch (Exception e) {
                    // Ignore if can't read file
                }
            }
        } else {
            previewItems.add("No description available");
        }

        previewListView.setItems(FXCollections.observableArrayList(previewItems));
    }
    }
