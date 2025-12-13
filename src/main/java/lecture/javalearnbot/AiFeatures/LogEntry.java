package lecture.javalearnbot.AiFeatures;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class LogEntry {
    public final LocalDateTime timestamp;
    public final String question;
    public final String answer;
    public final String rewrites;
    public final String retrievedChunks;

    public LogEntry(String question, String answer, String rewrites, String retrievedChunks) {
        this.timestamp = LocalDateTime.now();
        this.question = question;
        this.answer = answer;
        this.rewrites = rewrites;
        this.retrievedChunks = retrievedChunks;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getAnswer() {
        return answer;
    }

    public String getQuestion() {
        return question;
    }

    public String getRewrites() {
        return rewrites;
    }

    public String getRetrievedChunks() {
        return retrievedChunks;
    }
}
