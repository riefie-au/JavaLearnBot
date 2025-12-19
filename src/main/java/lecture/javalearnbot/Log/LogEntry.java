package lecture.javalearnbot.Log;

import java.time.LocalDateTime;

public abstract class LogEntry {
    protected LocalDateTime timestamp;
    protected final String answer;
    protected final String question;

    protected LogEntry(String question, String answer) {
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

    public abstract String getType();

}
