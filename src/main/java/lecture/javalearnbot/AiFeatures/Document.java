package lecture.javalearnbot.AiFeatures;

public class Document {
    private final String documentID;
    private final String title;
    private final String category;
    private final String source;
    private final String path;
    private final long timestamp;

    public Document(String DocumentID,String title, String category, String source, String path, long timestamp) {
        this.documentID = generateDocumentId(title,timestamp);
        this.title = title;
        this.category = category;
        this.source = source;
        this.path = path;
        this.timestamp = timestamp;
    }

    public String getDocumentID() {
        return documentID;
    }

    public String getTitle() {
        return title;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getPath() {
        return path;
    }

    public String getSource() {
        return source;
    }

    public String getCategory() {
        return category;
    }

    //generateDocumentId is meant to generate a unique id based on the document name and timestamp it was created
    private String generateDocumentId (String title, long timestamp) {
        String trimmedTitle = title.trim().toLowerCase().replaceAll("\\s","_");
        return trimmedTitle+ "_" + timestamp;
    }
}