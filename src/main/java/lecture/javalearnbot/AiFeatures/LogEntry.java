package lecture.javalearnbot.AiFeatures;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class LogEntry {
    public final LocalDateTime timestamp;
    public final String question;
    public final String answer;

    public LogEntry(String question, String answer) {
        this.timestamp = LocalDateTime.now();
        this.question = question;
        this.answer = answer;
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
}
