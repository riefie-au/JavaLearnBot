package lecture.javalearnbot.Utility;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lecture.javalearnbot.RAG.RagHelperClasses.Document;

public class EventBus {
    public static final ObservableList<Document> ALL_DOCUMENTS = FXCollections.observableArrayList();

    // NEW: Create immutable copy that won't change
    private static Document createImmutableCopy(Document doc) {
        return new Document(
                doc.getTitle(),
                doc.getCategory(),
                doc.getSource(),
                doc.getPath(),
                doc.getDescription(), // This stays fixed
                doc.getTimestamp()
        );
    }

    // UPDATED: Add immutable copy to EventBus
    public static void addDocument(Document doc) {
        Document immutableCopy = createImmutableCopy(doc);
        if (!containsDocument(immutableCopy)) {
            ALL_DOCUMENTS.add(immutableCopy);
        }
    }

    // UPDATED: Remove by comparing document properties
    public static void removeDocument(Document doc) {
        Document toRemove = findDocument(doc.getTitle(), doc.getCategory());
        if (toRemove != null) {
            ALL_DOCUMENTS.remove(toRemove);
        }
    }

    // Helper to find document
    private static Document findDocument(String title, String category) {
        for (Document d : ALL_DOCUMENTS) {
            if (d.getTitle().equals(title) && d.getCategory().equals(category)) {
                return d;
            }
        }
        return null;
    }

    // Helper to check if document exists
    private static boolean containsDocument(Document doc) {
        return findDocument(doc.getTitle(), doc.getCategory()) != null;
    }
}
