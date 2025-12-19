package lecture.javalearnbot;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import lecture.javalearnbot.AiFeatures.Document;
import lecture.javalearnbot.AiFeatures.DocumentMeta;
import lecture.javalearnbot.AiFeatures.Pipeline;
import java.time.LocalDateTime;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class AdminController extends BaseController // Controller for Admin settings page.
{
    @FXML private TextField titleField; // Text field for entering the document title.
    @FXML private TextField docSearchField; // Search field to filter documents.
    @FXML private ComboBox<String> categoryComboBox; // Dropdown for selecting document category.
    @FXML private ComboBox<String> statusComboBox; // Dropdown for filtering documents by status.
    @FXML private TextArea descriptionField;
    @FXML private TableView<Document> adminTable;
    @FXML private TableColumn<Document, String> titleColumn;
    @FXML private TableColumn<Document, String> categoryColumn;
    @FXML private TableColumn<Document, String> descriptionColumn;
    @FXML private TableColumn<Document, String> lastModifiedColumn;
    private final ObservableList<Document> adminData = FXCollections.observableArrayList();

    private final File adminDataFile = new File(".admin_data.json");
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create(); //set pretty printing makes the json ourput more readable
    private File selectedFile; // Stores currently selected file from system.
    private final Pipeline pipeline = new Pipeline();
    private java.io.File docsFolder = new java.io.File("src/main/resources/docs");;
    private final LocalDateTime time = LocalDateTime.now();

    public void initialize() {
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title")); //tell javafx how to populate each cell in title column
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        lastModifiedColumn.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
        adminTable.setItems(adminData);
        categoryComboBox.getItems().addAll(
                "OOP",
                "Inheritance",
                "Polymorphism"
        );

        loadAdminDataFromDocs();
    }

    @FXML private void onChooseFile() // Triggered when choose file button is clicked and opens system file chooser and stores selected file.
    {
        FileChooser fc = new FileChooser();
        fc.setTitle("Choose File to ingest");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("TXT files", "*.txt"));

        File file = fc.showOpenDialog(titleField.getScene().getWindow());
        if (file != null) {
            selectedFile = file;
            titleField.setText(file.getName());
        }


      // selectedFile = fc.showOpenDialog(null);
//        if (selectedFile != null){ // If file is selected, load its preview.
//            loadFilePreview(selectedFile);
//        }
    }

    @FXML
    private void onProcessAndIngest() throws IOException {
        String title = titleField.getText();
        String category = categoryComboBox.getValue();
        String description = descriptionField.getText();
        ingestDocument(selectedFile,title,category,description);
        //addFileToAdminTable(selectedFile,title,category,description);
        loadAdminDataFromDocs();
    }




    private void loadFilePreview(File file){ // Displays a placeholder alert to simulate file preview.
        showAlert("File loaded", "Preview loaded for: " + file.getName());
    }
    @FXML private void onProcessDocument() // Triggered when process document button is clicked. It calls processDocument() with exception handling.
    {
        try {
            processDocument(selectedFile, categoryComboBox.getValue(), titleField.getText());
        } catch (IOException ex) {
            showError("File Error", "Cloud not read file.");
        } catch (Exception ex) {
            showError("Error", "Ingestion failed.");
        }
    }

    private void processDocument(File file, String category, String title) throws IOException // Placeholder for document processing logic. This would call RAG pipeline and store embedded vector.
    {
        showAlert("Success", "Document processed.");
    }

    @FXML
    private void onDocSearchTyped(){ // Triggered when admin types in the document search field, calls filter method for real-time document filtering.
        applyDocFilter();
    }

    private void applyDocFilter(){ // Placeholder method for applying filters based on search text, category, and status.
        //placeholder
    }

    @FXML
    private void onEditMetadata(){ // Triggered when edit metadata button is clicked. Opens metadata editor for selected document.
        showAlert("Edit", "Opening metadata editor...");
    }

    @FXML
    private void onDeleteDocument(){ // Triggered when delete button is clicked. Simulates deletion of selected document.
        showAlert("Delete", "Document deleted.");
    }

//    private void addFileToAdminTable(File file,String title, String category, String description){
//        LocalDateTime timeNow = LocalDateTime.now();
//        AdminEntry entry = new AdminEntry(title,category,description,timeNow);
//        adminData.add(entry);
//        saveAdminData();
//    }
//
//    private void saveAdminData() {
//        try(FileWriter writer = new FileWriter(adminDataFile)){
//            gson.toJson(new ArrayList<>(adminData), writer);
//        }
//        catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }

    private void loadAdminDataFromDocs(){
        adminData.clear();
        if (!docsFolder.exists()){
            return;
        }
        File[] files = docsFolder.listFiles((docsFolder, name) -> name.endsWith(".meta.json"));// get all files and store it in a list if it ends with .meta.json

        for (File metaJsonfiles : files) {
            try{
                String json = Files.readString(metaJsonfiles.toPath());
                DocumentMeta meta = gson.fromJson(json, DocumentMeta.class); //convert the json data into a DocumentMeta Object, we use Gson to pretty much deserialize it
                Document doc = new Document(
                        meta.title,
                        meta.category,
                        meta.source,
                        meta.path,
                        meta.description,
                        meta.timestamp
                );
                adminData.add(doc);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }}}


    //the goal of ingest document is to add the file to to the docs folder along with creating the json file for associated metadata
    private void ingestDocument(File file, String title, String category, String description) throws IOException {
       File target = new File(docsFolder, file.getName());
       if(!target.exists()){
           Files.copy(file.toPath(), target.toPath()); //if the target folder exists then copy the file into it
       }
       File metaDataFile = new File(docsFolder, file.getName() + ".meta.json"); //create a metadata json file with the file name
       if(!metaDataFile.exists()){
               String metadataJson = String.format("""
                        {
                        "title": "%s",
                        "category": "%s",
                        "description": "%s",
                        "lastModified": "%s"
                        }
                        
                        """, title, category,description,time.toString());
               Files.writeString(metaDataFile.toPath(), metadataJson); //write the string metadata Json into the json file
       }
    }


}
