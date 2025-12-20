package lecture.javalearnbot.Log;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ChatLogEntry extends LogEntry {
    public final String rewrites;
    public final String retrievedChunks;


    public ChatLogEntry(LocalDateTime timestamp,String question, String answer, String rewrites, String retrievedChunks) {
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