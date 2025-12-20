package lecture.javalearnbot.Controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import lecture.javalearnbot.RAG.RagHelperClasses.Document;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import lecture.javalearnbot.RAG.Pipeline;
import lecture.javalearnbot.Utility.EventBus;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AdminController extends BaseController {
    @FXML private TextField titleField;
    @FXML private TextField docSearchField;
    @FXML private ComboBox<String> categoryComboBox;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private TextArea descriptionField;
    @FXML private TableView<Document> adminTable;
    @FXML private TableColumn<Document, String> titleColumn;
    @FXML private TableColumn<Document, String> categoryColumn;
    @FXML private TableColumn<Document, String> descriptionColumn;
    @FXML private TableColumn<Document, String> lastModifiedColumn;
    @FXML private Label fileStatusLabel;


    private final ObservableList<Document> adminData = FXCollections.observableArrayList();
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private File selectedFile;
    private final Pipeline pipeline = new Pipeline();
    private final File docsFolder = new File("src/main/resources/docs");
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @FXML
    public void initialize() {
        // Setup table
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        lastModifiedColumn.setCellValueFactory(cellData -> {
            long timestamp = cellData.getValue().getTimestamp();
            LocalDateTime dateTime = java.time.Instant.ofEpochMilli(timestamp)
                    .atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
            return new javafx.beans.property.SimpleStringProperty(
                    dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            );
        });

        // Use EventBus for shared data
        adminTable.setItems(EventBus.ALL_DOCUMENTS);

        // Setup category dropdown
        categoryComboBox.getItems().addAll("OOP", "Inheritance", "Polymorphism", "Core", "Advanced", "Java 8+", "Database");

        // Load initial data
        loadAdminDataFromDocs();

        // Sync with EventBus
        if (EventBus.ALL_DOCUMENTS.isEmpty()) {
            EventBus.ALL_DOCUMENTS.setAll(adminData);
        }

        // Auto-fill form when row selected
        adminTable.getSelectionModel().selectedItemProperty().addListener((obs, oldDoc, newDoc) -> {
            if (newDoc != null) {
                titleField.setText(newDoc.getTitle());
                categoryComboBox.setValue(newDoc.getCategory());
                descriptionField.setText(newDoc.getDescription());
            }
        });
    }

    // ==================== DELETE DOCUMENT ====================
    @FXML
    private void onDeleteDocument() {
        Document selectedDoc = adminTable.getSelectionModel().getSelectedItem();

        if (selectedDoc == null) {
            showAlert("No Selection", "Please select a document to delete.");
            return;
        }

        // Confirm deletion
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Deletion");
        confirmAlert.setHeaderText("Delete: " + selectedDoc.getTitle());
        confirmAlert.setContentText("This will PERMANENTLY delete:\n" +
                "✓ " + selectedDoc.getTitle() + ".txt\n" +
                "✓ " + selectedDoc.getTitle() + ".txt.meta.json\n" +
                "✓ All AI memory chunks for this document\n\n" +
                "This cannot be undone!");

        if (confirmAlert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                // 1. Delete files from filesystem
                boolean filesDeleted = deleteBothFiles(selectedDoc);

                if (filesDeleted) {
                    // 2. Remove from ChunkStorage (AI memory)
                    int chunksRemoved = pipeline.getChunkStore().removeDocument(selectedDoc.getDocumentID());
                    System.out.println("Removed " + chunksRemoved + " AI chunks for document: " + selectedDoc.getTitle());

                    // 3. Remove from EventBus (syncs with DocumentsController)
                    EventBus.removeDocument(selectedDoc);

                    // 4. Remove from local admin data
                    adminData.remove(selectedDoc);

                    // 5. Clear selection
                    adminTable.getSelectionModel().clearSelection();
                    titleField.clear();
                    descriptionField.clear();

                    showAlert("Success",
                            "Document deleted successfully!\n" +
                                    "Removed " + chunksRemoved + " AI chunks from memory.");

                } else {
                    showError("Error", "Could not delete files. They may be in use.");
                }

            } catch (Exception e) {
                showError("Error", "Failed to delete: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private boolean deleteBothFiles(Document doc) {
        try {
            // First, try to find the actual files
            File txtFile = findFileByTitle(doc.getTitle());

            if (txtFile == null) {
                System.err.println("❌ Cannot find file for: " + doc.getTitle());
                return false;
            }

            File metaFile = new File(txtFile.getAbsolutePath() + ".meta.json");

            System.out.println("🗑️ Attempting to delete:");
            System.out.println("   TXT: " + txtFile.getAbsolutePath() + " - exists: " + txtFile.exists());
            System.out.println("   META: " + metaFile.getAbsolutePath() + " - exists: " + metaFile.exists());

            boolean txtDeleted = txtFile.exists() && txtFile.delete();
            boolean metaDeleted = metaFile.exists() && metaFile.delete();

            return txtDeleted || metaDeleted;

        } catch (Exception e) {
            System.err.println("❌ Delete error: " + e.getMessage());
            return false;
        }
    }
    // ==================== EDIT METADATA ====================
    @FXML
    private void onEditMetadata() {
        Document selectedDoc = adminTable.getSelectionModel().getSelectedItem();

        if (selectedDoc == null) {
            showAlert("No Selection", "Please select a document to edit.");
            return;
        }

        // Create edit dialog
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Metadata");
        dialog.setHeaderText("Editing: " + selectedDoc.getTitle());

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        TextField editTitle = new TextField(selectedDoc.getTitle());
        ComboBox<String> editCategory = new ComboBox<>();
        editCategory.getItems().addAll(categoryComboBox.getItems());
        editCategory.setValue(selectedDoc.getCategory());
        TextArea editDescription = new TextArea(selectedDoc.getDescription());
        editDescription.setPrefRowCount(3);

        grid.add(new Label("Title:"), 0, 0);
        grid.add(editTitle, 1, 0);
        grid.add(new Label("Category:"), 0, 1);
        grid.add(editCategory, 1, 1);
        grid.add(new Label("Description:"), 0, 2);
        grid.add(editDescription, 1, 2);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(button -> {
            if (button == ButtonType.OK) {
                try {
                    // Update the metadata file
                    boolean success = updateMetadataFile(
                            selectedDoc,
                            editTitle.getText(),
                            editCategory.getValue(),
                            editDescription.getText()
                    );

                    if (success) {
                        // Create updated document
                        Document updatedDoc = new Document(
                                editTitle.getText(),
                                editCategory.getValue(),
                                selectedDoc.getSource(),
                                selectedDoc.getPath(),
                                editDescription.getText(),
                                System.currentTimeMillis()
                        );

                        // Update EventBus
                        EventBus.removeDocument(selectedDoc);
                        EventBus.addDocument(updatedDoc);

                        // Remove OLD chunks from AI memory
                        pipeline.getChunkStore().removeDocument(selectedDoc.getDocumentID());

                        // Re-index with NEW document (optional but good practice)
                        // pipeline.indexDocs();

                        showAlert("Success", "Metadata updated! Old AI chunks removed.");
                    }
                } catch (Exception e) {
                    showError("Error", "Failed to update: " + e.getMessage());
                }
            }
        });
    }

    private boolean updateMetadataFile(Document oldDoc, String newTitle, String newCategory, String newDescription) {
        try {
            // Get the ACTUAL filename from the document's path
            String oldFilePath = oldDoc.getPath(); // e.g., "src/main/resources/docs/collections.txt"
            File oldTxtFile = new File(oldFilePath);

            if (!oldTxtFile.exists()) {
                // Try to find by title
                oldTxtFile = findFileByTitle(oldDoc.getTitle());
            }

            if (oldTxtFile == null || !oldTxtFile.exists()) {
                System.err.println("❌ Cannot find original file for: " + oldDoc.getTitle());
                return false;
            }

            String oldBaseName = oldTxtFile.getName().replace(".txt", "");
            File oldMetaFile = new File(docsFolder, oldTxtFile.getName() + ".meta.json");

            System.out.println("📁 Found original files:");
            System.out.println("   TXT: " + oldTxtFile.getAbsolutePath() + " - exists: " + oldTxtFile.exists());
            System.out.println("   META: " + oldMetaFile.getAbsolutePath() + " - exists: " + oldMetaFile.exists());

            if (oldMetaFile.exists()) {
                // Read and update metadata
                String jsonContent = Files.readString(oldMetaFile.toPath());
                JsonObject json = gson.fromJson(jsonContent, JsonObject.class);

                json.addProperty("title", newTitle);
                json.addProperty("category", newCategory);
                json.addProperty("description", newDescription);
                json.addProperty("lastModified", LocalDateTime.now().format(dateFormatter));

                Files.writeString(oldMetaFile.toPath(), gson.toJson(json));
                System.out.println("✓ Updated metadata in: " + oldMetaFile.getName());

                // Check if we need to rename files
                String newBaseName = newTitle.replace(" ", "_").replace(".txt", "");
                if (!newBaseName.equals(oldBaseName)) {
                    // Rename files
                    File newTxtFile = new File(docsFolder, newBaseName + ".txt");
                    File newMetaFile = new File(docsFolder, newBaseName + ".txt.meta.json");

                    Files.move(oldTxtFile.toPath(), newTxtFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    Files.move(oldMetaFile.toPath(), newMetaFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                    System.out.println("✓ Renamed files:");
                    System.out.println("   " + oldTxtFile.getName() + " → " + newTxtFile.getName());
                    System.out.println("   " + oldMetaFile.getName() + " → " + newMetaFile.getName());
                }

                return true;
            } else {
                System.err.println("❌ Metadata file not found: " + oldMetaFile.getAbsolutePath());
                return false;
            }

        } catch (IOException e) {
            System.err.println("❌ Error updating metadata: " + e.getMessage());
            return false;
        }
    }

    private File findFileByTitle(String title) {
        // Remove spaces and special characters for filename matching
        String cleanTitle = title.toLowerCase().replace(" ", "_").replaceAll("[^a-z0-9_]", "");

        File[] txtFiles = docsFolder.listFiles((dir, name) -> name.endsWith(".txt"));
        if (txtFiles != null) {
            for (File file : txtFiles) {
                String fileName = file.getName().toLowerCase().replace(".txt", "");
                if (fileName.contains(cleanTitle) || cleanTitle.contains(fileName)) {
                    return file;
                }
            }
        }
        return null;
    }

    // ==================== EXISTING METHODS ====================
    @FXML
    private void onChooseFile() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Choose File to ingest");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("TXT files", "*.txt"));

        File file = fc.showOpenDialog(titleField.getScene().getWindow());
        if (file != null) {
            selectedFile = file;
            titleField.setText(file.getName().replace(".txt", ""));
            fileStatusLabel.setText("File selected: " + file.getName());
        }
    }

    @FXML
    private void onProcessAndIngest() throws IOException {
        String title = titleField.getText();
        String category = categoryComboBox.getValue();
        String description = descriptionField.getText();

        if (title.isEmpty() || category == null || description.isEmpty()) {
            showError("Missing Information", "Please fill in all fields.");
            return;
        }

        if (selectedFile == null) {
            showError("No File", "Please select a file first.");
            return;
        }

        ingestDocument(selectedFile, title, category, description);
        loadAdminDataFromDocs();

        // Reset file selection UI
        selectedFile = null;
        fileStatusLabel.setText("No file selected");
        titleField.clear();
        descriptionField.clear();
        categoryComboBox.setValue(null);
    }

    private void loadAdminDataFromDocs() {
        adminData.clear();
        if (!docsFolder.exists()) {
            return;
        }

        File[] files = docsFolder.listFiles((dir, name) -> name.endsWith(".meta.json"));
        if (files == null) return;

        for (File metaJsonFile : files) {
            try {
                String json = Files.readString(metaJsonFile.toPath());
                JsonObject jsonObject = gson.fromJson(json, JsonObject.class);

                String title = jsonObject.has("title") ? jsonObject.get("title").getAsString() : "";
                String category = jsonObject.has("category") ? jsonObject.get("category").getAsString() : "";
                String description = jsonObject.has("description") ? jsonObject.get("description").getAsString() : "";

                // Get corresponding .txt file
                String txtPath = metaJsonFile.getPath().replace(".meta.json", "");
                Document doc = new Document(
                        title,
                        category,
                        "File",
                        txtPath,
                        description,
                        System.currentTimeMillis()
                );
                adminData.add(doc);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    private void ingestDocument(File file, String title, String category, String description) throws IOException {
        // Ensure title doesn't have .txt extension yet
        if (title.endsWith(".txt")) {
            title = title.substring(0, title.length() - 4);
        }

        File target = new File(docsFolder, title + ".txt");
        if (!target.exists()) {
            Files.copy(file.toPath(), target.toPath());
        }

        File metaDataFile = new File(docsFolder, title + ".txt.meta.json");
        if (!metaDataFile.exists()) {
            // Create metadata with current timestamp
            JsonObject metadata = new JsonObject();
            metadata.addProperty("title", title);
            metadata.addProperty("category", category);
            metadata.addProperty("description", description);
            metadata.addProperty("lastModified", LocalDateTime.now().format(dateFormatter)); // ✅ SET INITIAL TIMESTAMP

            Files.writeString(metaDataFile.toPath(), gson.toJson(metadata));
        }

        // Add to EventBus
        Document newDoc = new Document(
                title,
                category,
                "File",
                target.getPath(),
                description,
                System.currentTimeMillis()
        );
        EventBus.addDocument(newDoc);
    }
}
