package lecture.javalearnbot.Log;

public class ChatLogEntry extends LogEntry {
    public final String rewrites;
    public final String retrievedChunks;


    public ChatLogEntry(String question, String answer, String rewrites, String retrievedChunks) {
        super(question,answer);
        this.rewrites = rewrites;
        this.retrievedChunks = retrievedChunks;
    }

    public String getRewrites() {
        return rewrites;
    }

    public String getRetrievedChunks() {
        return retrievedChunks;
    }


    @Override
    public String getType() {
        return "Chat Log";
    }
}